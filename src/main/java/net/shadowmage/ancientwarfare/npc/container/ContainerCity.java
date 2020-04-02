package net.shadowmage.ancientwarfare.npc.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.tile.TileCity;
import net.shadowmage.ancientwarfare.npc.tile.BuyOrder;
import net.shadowmage.ancientwarfare.npc.tile.MarketPrice;

public class ContainerCity extends ContainerBase{
	public TileCity city;
	public List<MarketPrice> prices = new ArrayList<MarketPrice>();
	
	public ContainerCity(EntityPlayer player, int x, int y, int z) {
		super(player,x,y,z);
		TileEntity te = player.worldObj.getTileEntity(x, y, z);
		if(te instanceof TileCity){
			city = (TileCity)te;
			IInventory inv = (IInventory) te;
			int xPos, yPos;
			for(int i = 0; i < inv.getSizeInventory(); i++){
				xPos = (i%9)*18 + 8;
				yPos = (i/9)*18 + 8+16;
				addSlotToContainer(new Slot(inv, i, xPos, yPos));
			}    
			addPlayerSlots(player, 8, 8+3*18+8+16, 4);
			if(!player.worldObj.isRemote){
				city.addViewer(this);
				prices.addAll(city.getPrices());
		//		System.out.println("CONTAINER CREATION ITEMS:");
				//for(MarketPrice p : prices) {
			//		System.out.println("ITEM: " + p.i.getUnlocalizedName() + " buy: " + p.buy + " sell: " + p.sell);
				//}
			}
		}
		else{
			throw new IllegalArgumentException("cannot open city gui for tile: "+te);
		}
	}
	
	public List<MarketPrice> getPrices(){
//		List<MarketPrice> prices = new ArrayList<MarketPrice>();
//		for(Item i : city.getItems()) {
//			float b = city.getHighestBuy(i);
//			float s = 0.0f; //city.getLowestSell(i);
//			prices.add(new MarketPrice(i,b,s));
//		}
	//	ItemStack i = new ItemStack(Items.baked_potato);
	//	prices.add(new MarketPrice(i.getItem(),city.getHighestBuy(i.getItem()),city.getLowestSell(i.getItem())));
//		for(BuyOrder b : city.getBuys()) {
//			prices.add(new MarketPrice(b.getItem(),b.getPrice(),city.getLowestSell(b.getItem())));
//		}
		//System.out.println("GET-PRICES PRICES");
		//for(MarketPrice p : prices) {
			//System.out.println("ITEM: " + p.i.getUnlocalizedName() + " buy: " + p.buy + " sell: " + p.sell);
	//	}
		return prices;
	}
	
	@Override
	public void sendInitData(){
		//System.out.println("Sending init data!");
		NBTTagList list = new NBTTagList();
		for(MarketPrice p : prices) {
		    list.appendTag(p.writeToNBT(new NBTTagCompound()));
		}
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("priceList", list);
		sendDataToClient(tag);
		//System.out.println("Sending data to client!");
	 }
	
	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if(tag.hasKey("priceList")) {
			//System.out.println("Receiving packet data");
			prices.clear();
			NBTTagList list = tag.getTagList("priceList", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < list.tagCount(); i++){
		      prices.add(new MarketPrice(list.getCompoundTagAt(i)));
		      }
		    refreshGui();
		}
	}

	
	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer){  
	  super.onContainerClosed(par1EntityPlayer);
	  city.removeViewer(this);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
	  {
	  ItemStack slotStackCopy = null;
	  Slot theSlot = (Slot)this.inventorySlots.get(slotClickedIndex);
	  if (theSlot != null && theSlot.getHasStack())
	    {
	    ItemStack slotStack = theSlot.getStack();
	    slotStackCopy = slotStack.copy();
	    if(slotClickedIndex < city.getSizeInventory())//book slot
	      {      
	      if(!this.mergeItemStack(slotStack, city.getSizeInventory(), city.getSizeInventory()+36, false))//merge into player inventory
	        {
	        return null;
	        }
	      }
	    else
	      {
	      if(!this.mergeItemStack(slotStack, 0, city.getSizeInventory(), false))//merge into player inventory
	        {
	        return null;
	        }
	      }
	    if (slotStack.stackSize == 0)
	      {
	      theSlot.putStack((ItemStack)null);
	      }
	    else
	      {
	      theSlot.onSlotChanged();
	      }
	    if (slotStack.stackSize == slotStackCopy.stackSize)
	      {
	      return null;
	      }
	    theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
	    }
	  return slotStackCopy;
	  }
}
