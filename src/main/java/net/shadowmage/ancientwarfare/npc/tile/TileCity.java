package net.shadowmage.ancientwarfare.npc.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.container.ContainerCity;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class TileCity  extends TileEntity implements IOwnable, IInventory, IInteractableTile{
	private String ownerName = "";
	private InventoryBasic inventory = new InventoryBasic(27);
	private List<ContainerCity> viewers = new ArrayList<ContainerCity>();
	public List<BuyOrder> buyOrders = new ArrayList<BuyOrder>();
	public List <SellOrder> sellOrders = new ArrayList<SellOrder>();
	boolean updated = false;
	private static List<TileCity> cityList = new ArrayList<TileCity>();
	Random r = new Random();
	public int id = r.nextInt(10000);
	public int counter = 0;
	private ArrayList<BuyOrder> lastBuy = new ArrayList<BuyOrder>();
	private ArrayList<SellOrder> lastSell = new ArrayList<SellOrder>();
	
	public static TileCity getCityAt(int x, int y, int z) {
		for(TileCity c : cityList) {
			if(c.xCoord == x && c.yCoord == y && c.zCoord == z) {
				return c;
			}
		}
		return null;
	}
	
	@Override
	public boolean canUpdate(){
	  return true;
	}
	
	public List<MarketPrice> getPrices(){
		List<MarketPrice> prices = new ArrayList<MarketPrice>();
		for(Item i : getItems()) {
			float b = getHighestBuy(i);
			float s = getLowestSell(i);
			prices.add(new MarketPrice(i,b,s));
		}
		
		//prices.add(new MarketPrice(Items.beef, buyOrders.size(), getItems().size()));
		return prices;
	}
	
	public float getLowestSell(Item i) {
		float lowest = (float)Double.POSITIVE_INFINITY;
		for(SellOrder sale : sellOrders) {
			if(sale.getItem() == i) {
				float price = sale.getPrice();
				if(lowest > price) {
					lowest = price;
				}
			}
		}
		
		if(lowest < Double.POSITIVE_INFINITY) {
			return lowest;
		}else {
			if(getLastSale(i) != null) {
				return getLastSale(i).getPrice();
			}else {
				return lowest;
			}
		}
	}
	
	public SellOrder getLastSale(Item i) {
		for(SellOrder s : lastSell) {
			if(s.getItem() == i) {
				return s;
			}
		}
		return null;
	}
	
	public BuyOrder getLastBuy(Item i) {
		for(BuyOrder b : lastBuy) {
			if(b.getItem() == i) {
				return b;
			}
		}
		return null;
	}
	
	public SellOrder getLowestSellOrd(Item i) {
		float lowest = (float)Double.POSITIVE_INFINITY;
		SellOrder out = null;
		for(SellOrder sale : sellOrders) {
			if(sale.getItem() == i) {
				float price = sale.getPrice();
				if(lowest > price) {
					lowest = price;
					out = sale;
				}
			}
		}
		return out;
	}
	
	public int getHighestBuy(Item i) {
		int highest = 0;
		for(BuyOrder buy : buyOrders) {
			if(buy.getItem().equals(i)) {
				int price = buy.getPrice();
				if(highest < price) {
					highest = price;
				}
			}
		}
		if(highest > 0) {
			return highest;
		}else {
			if(getLastBuy(i) != null) {
				return getLastBuy(i).getPrice();
			}else {
				return highest;
			}
		}
	}
	
	public BuyOrder getHighestBuyOrd(Item i) {
		float highest = 0.0F;
		BuyOrder out = null;
		for(BuyOrder buy : buyOrders) {
			if(buy.getItem().equals(i)) {
				float price = buy.getPrice();
				if(highest < price) {
					highest = price;
					out = buy;
				}
			}
		}
		return out;
	}
	
	public List<BuyOrder> getBuys(){
		return buyOrders;
	}
	
	public List<Item> getItems(){
		List<Item> items = new ArrayList<Item>();
		
		for(BuyOrder buy : buyOrders) {
			if(!items.contains(buy.getItem())) {
				items.add(buy.getItem());
			}
		}
		
		for(SellOrder sale : sellOrders) {
			if(!items.contains(sale.getItem())) {
				items.add(sale.getItem());
			}
		}
		return items;
	}

	@Override
	public void updateEntity(){
	  if(worldObj.isRemote){return;}
	  if(!updated) {
		  updated = true;
		  for(BuyOrder b : buyOrders) {
			  b.updateOwner(worldObj);
		  }
		  
		  for(SellOrder s : sellOrders) {
			  s.updateOwner(worldObj);
		  }
	  }
	  updateOrders();
	}
	
	public SellOrder getSell(NpcPlayerOwned p, ItemStack stack) {
		for(SellOrder s : sellOrders) {
			if(s.getOwner() == p && s.getItem() == stack.getItem()) {
				return s;
			}
		}
		return null;
	}
	
	public BuyOrder getBuy(NpcPlayerOwned p, ItemStack stack) {
		for(BuyOrder s : buyOrders) {
			if(s.getOwner() == p && s.getItem() == stack.getItem()) {
				return s;
			}
		}
		return null;
	}
	
	
	public void updateOrders() {
		for(Item i : getItems()){
			BuyOrder b = getHighestBuyOrd(i);
			SellOrder s = getLowestSellOrd(i);
			if(b==null) {
				s.setPrice(Math.max(100,s.getPrice()-100));
				continue;
			}
			if(s==null) {continue;}
			if(s.getOwner() == null) {
				ItemStack stk = new ItemStack(s.getItem());
				addItems(stk, s.getAmt());
			}
			if(b.getOwner() == null) {
				ItemStack stk = new ItemStack(Item.getItemById(5303));
				addItems(stk, s.getPrice() * s.getAmt() / 100);
			}
			
			if(b.getOwner() == null || s.getOwner() == null) { continue;}
			
			if(s.getPrice() <= b.getPrice()) {
				handleBuy(b,s);
			}else {
				s.setPrice(Math.max(100,s.getPrice()-100));//todo ???
			}
		}
	}
	
	
	public void handleBuy(BuyOrder b, SellOrder s) {
		ItemStack stk = b.getItemStack();
		int amt = 0;
		if(s.getAmt() > b.getAmt()) {
			s.setAmt(s.getAmt() - b.getAmt()); //Decrease s qty by b qty
			amt = b.getAmt();
			buyOrders.remove(b); //delete b			
		}else if(s.getAmt() == b.getAmt()) {
			amt = s.getAmt();
			buyOrders.remove(b);
			sellOrders.remove(s);
		}else { //s amt < b amt
			b.setAmt(b.getAmt() - s.getAmt());
			sellOrders.remove(s);
			amt = s.getAmt();
		}
		addItems(stk, amt, b.getOwner());
		int price = (s.getPrice() * amt) / 100;
		stk = new ItemStack(Item.getItemById(5303));
		addItems(stk, price, s.getOwner());
	//	b.getOwner().addToInv(stk);
		//s.getOwner().addToInv(); //give gold to seller
	}
	
	public void addItems(ItemStack stk, int amt, NpcPlayerOwned p) {
		System.out.println("Adding " + amt + " " + stk.getDisplayName());
		if(amt <= 64) {
			stk.stackSize = amt;
			p.addToInv(stk);
		}else {
			stk.stackSize = 64;
			p.addToInv(stk);
			addItems(stk, amt - 64, p);
		}
	}
	
	public void addItems(ItemStack stk, int amt) {
		System.out.println("Adding " + amt + " " + stk.getDisplayName());
		if(amt <= 64) {
			stk.stackSize = amt;
			addItem(stk);
		}else {
			stk.stackSize = 64;
			addItem(stk);
			addItems(stk, amt - 64);
		}
	}
	
	public void addItem(ItemStack i) {
		InventoryTools.mergeItemStack(inventory, i, 0);
	}

	public void addViewer(ContainerCity viewer){
	  if(!viewers.contains(viewer)){viewers.add(viewer);}
	}

	public void removeViewer(ContainerCity viewer){
	  while(viewers.contains(viewer)){viewers.remove(viewer);}
	}
	
	@Override
	public void setOwnerName(String name){
	  if(name==null){name="";}
	  this.ownerName = name;
	}

	@Override
	public String getOwnerName(){
	  return ownerName;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		ownerName = tag.getString("owner");
		InventoryTools.readInventoryFromNBT(inventory, tag.getCompoundTag("inventory"));
		
		NBTTagList bl = tag.getTagList("buys", Constants.NBT.TAG_COMPOUND);
		NBTTagList sl = tag.getTagList("sales", Constants.NBT.TAG_COMPOUND);
		NBTTagList lbl = tag.getTagList("lastB", Constants.NBT.TAG_COMPOUND);
		NBTTagList lsl = tag.getTagList("lastS", Constants.NBT.TAG_COMPOUND);
		BuyOrder o;
		for(int i = 0;i < bl.tagCount(); i++) {
			o = new BuyOrder(bl.getCompoundTagAt(i));
			buyOrders.add(o);
		}
		
		SellOrder o2;
		for(int i = 0;i < sl.tagCount(); i++) {
			o2 = new SellOrder(sl.getCompoundTagAt(i));
			sellOrders.add(o2);
		}
		
		for(int i = 0;i < lbl.tagCount(); i++) {
			o = new BuyOrder(lbl.getCompoundTagAt(i));
			lastBuy.add(o);
		}
		
		for(int i = 0;i < lsl.tagCount(); i++) {
			o2 = new SellOrder(lsl.getCompoundTagAt(i));
			lastSell.add(o2);
		}
		updated = false;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag){
	  super.writeToNBT(tag);
	  tag.setString("owner", ownerName);
	  tag.setTag("inventory", InventoryTools.writeInventoryToNBT(inventory, new NBTTagCompound()));
	  
	  NBTTagList buyList = new NBTTagList();
	  for(BuyOrder b : buyOrders) {
		  buyList.appendTag(b.writeToNBT(new NBTTagCompound()));
	  }
	  
	  NBTTagList sellList = new NBTTagList();
	  for(SellOrder s : sellOrders) {
		  sellList.appendTag(s.writeToNBT(new NBTTagCompound()));
	  }
	  
	  NBTTagList lastBuyList = new NBTTagList();
	  for(BuyOrder b : lastBuy) {
		  lastBuyList.appendTag(b.writeToNBT(new NBTTagCompound()));
	  }
	  
	  NBTTagList lastSellList = new NBTTagList();
	  for(SellOrder s : lastSell) {
		  lastSellList.appendTag(s.writeToNBT(new NBTTagCompound()));
	  }
	  
	  tag.setTag("lastB", buyList);
	  tag.setTag("lastS", sellList);
	}
	
	public void addBuy(BuyOrder b) {
		//TODO try instant buy
		buyOrders.add(b);
	}
	

	public void addSell(SellOrder s) {
		// TODO Auto-generated method stub
		sellOrders.add(s);
	}
	
	@Override
	public boolean onBlockClicked(EntityPlayer player)
	  {
	  if(!player.worldObj.isRemote)
	    {
	    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_CITY, xCoord, yCoord, zCoord);
	    }
	  return false;
	  }
	
	@Override
	public int getSizeInventory(){return inventory.getSizeInventory();}

	@Override
	public ItemStack getStackInSlot(int var1){return inventory.getStackInSlot(var1);}

	@Override
	public ItemStack decrStackSize(int var1, int var2){return inventory.decrStackSize(var1, var2);}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1){return inventory.getStackInSlotOnClosing(var1);}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2){inventory.setInventorySlotContents(var1, var2);}

	@Override
	public String getInventoryName(){return inventory.getInventoryName();}

	@Override
	public boolean hasCustomInventoryName(){return inventory.hasCustomInventoryName();}

	@Override
	public int getInventoryStackLimit(){return inventory.getInventoryStackLimit();}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1){return true;}

	@Override
	public void openInventory(){}

	@Override
	public void closeInventory(){}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2){return inventory.isItemValidForSlot(var1, var2);}
	

}
