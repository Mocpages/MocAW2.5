package net.shadowmage.ancientwarfare.npc.entity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraft.command.IEntitySelector;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcPlayerOwnedTrade;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemNPCSettings;
import net.shadowmage.ancientwarfare.npc.needs.INeed;
import net.shadowmage.ancientwarfare.npc.needs.NeedBase;
import net.shadowmage.ancientwarfare.npc.needs.NeedHelper;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;
import net.shadowmage.ancientwarfare.npc.tile.BuyOrder;
import net.shadowmage.ancientwarfare.npc.tile.LandGrant;
import net.shadowmage.ancientwarfare.npc.tile.TileCity;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;



public abstract class NpcPlayerOwned extends NpcBase
{

protected List<INeed> needs = new ArrayList<INeed>();
private Command playerIssuedCommand;//TODO load/save
private int foodValueRemaining = 0;
private int cash = 0;
private int timeToPayday = 0;
private int pay = 0;
public InventoryBackpack invBack = new InventoryBackpack(27);
protected NpcAIPlayerOwnedRideHorse horseAI;
private int x1 = 0;
private int y1 = 0;
public int x2 = 0;
public int y2 = 0;
public int cx = 0;
public int cy = 0;
private NpcPlayerOwned spouse;
private List<NpcPlayerOwned> suitors;
List<NpcPlayerOwned> children;

private BlockPosition townHallPosition;
private BlockPosition upkeepAutoBlock;

public NpcPlayerOwned(World par1World){
  super(par1World);
  children = new ArrayList<NpcPlayerOwned>();
  suitors = new ArrayList<NpcPlayerOwned>();
  addNeeds();
	ItemStack stack = new ItemStack(Item.getItemById(5303));
	Random r = new Random();

  stack.stackSize = r.nextInt(50);
	invBack.setInventorySlotContents(1, stack);
	

  }

public void addNeeds() {
	//System.out.println("adding needs");
	needs.add(NeedHelper.starchNeed(this));
	needs.add(NeedHelper.textilesNeed(this));
	needs.add(NeedHelper.fuelNeed(this));
	needs.add(NeedHelper.vegNeed(this));
	needs.add(NeedHelper.alcoholNeed(this));
	needs.add(NeedHelper.protienNeed(this));
	needs.add(NeedHelper.leatherNeed(this));
	//System.out.println("Needs added " + needs.size());
	//if(!isMale) {setSuitors(new ArrayList<NpcPlayerOwned>());}
}

public void setP(int a, int b) {
	x1 = a;
	y1 = b;
}

@Override
public void writeToNBT(NBTTagCompound tag) {
	super.writeToNBT(tag);
//	System.out.println("WRITING NPC TO NBT");
	NBTTagCompound t = invBack.writeToNBT(new NBTTagCompound());
	tag.setTag("inv",t);
	
	NBTTagList buyList = new NBTTagList();
	  for(INeed b : needs) {
		  buyList.appendTag(b.writeToNBT(new NBTTagCompound()));
	  }
	//System.out.println("SUCCESSFULLY WROTE NPC TO NBT");
}

@Override
public void readFromNBT(NBTTagCompound tag) {
	super.readFromNBT(tag);
	invBack.readFromNBT(tag.getCompoundTag("inv"));
}

public int sumMoney() {
	//values: copper = 1, silver = 10, gold = 100
	int sum = 0;
	for(int i = 0; i <= invBack.getSizeInventory()-1; i++) {
		//System.out.println(invBack.getStackInSlot(i).getDisplayName());
		sum += this.itemToCash(invBack.getStackInSlot(i));
	}
	//System.out.println("Summing money: " + sum);
	return sum;
}

public void getChange(ItemStack stack, int amt) {
	int id = Item.getIdFromItem(stack.getItem());
	if( id == 5303) { //gold
		amt -= 100;
		this.addToInv(new ItemStack(Item.getItemById(5302)));
	}else { //silver
		amt -= 10;
		this.addToInv(new ItemStack(Item.getItemById(5301)));
	}
}

public void remMoney(int amt) {
	for(int i = 0; i <= invBack.getSizeInventory()-1; i++) {
		ItemStack item = invBack.getStackInSlot(i);
		int val = itemToCash(item); //total coin value of this entire slot
		if(val > 0) { //if its worth money
			int amtPer = itemToCash(new ItemStack(item.getItem())); //value of one coin in this stack
			//System.out.println("Removing! " + amt);
			if(val <= amt) { //we can delete the whole stack
				amt -= val;//just do that
				item = null;
				invBack.setInventorySlotContents(i, null);
			//	System.out.println("Deleting stack of " + item.stackSize + " " + item.getDisplayName());
			}else if(amtPer > amt){ //get change
				item.stackSize -= 1;
				getChange(item,amt);
			}else { //gotta delete just part of it
				
				int amtToDel = amt / amtPer; //number of coins we should delete
				item.stackSize -= amtToDel; 
				amt -= amtToDel;
				//System.out.println("partial delete " + item.stackSize + " " + item.getDisplayName());
			}
		}
		invBack.setInventorySlotContents(i,item);
	}
}

public int getPX() {
	return x1;
}

public int getPZ() {
	return y1;
}

@Override
public void setCurrentItemOrArmor(int slot, ItemStack stack)
  {
  super.setCurrentItemOrArmor(slot, stack);
  if(slot==0){onWeaponInventoryChanged();}
  }

@Override
public boolean isPayday() {
	if(timeToPayday <= 0) {
		return true;
	}else {
		return false;
	}
}

public void addToInv(ItemStack i) {
	System.out.println("Adding item " + i.getDisplayName() + " to inventory!");
	InventoryTools.mergeItemStack(invBack, i, 0);
}

public InventoryBackpack getInv() {
	return invBack;
}

public boolean idle() {
	for(INeed n : needs) {
		if(n.shouldIdle()){
			return true;
		}
	}
	return false;
}

@Override
public int getCash() {
	return cash;
}

@Override
public boolean takePay(IInventory inventory, int side) {
	//entire function decremented, ignore
	 int amount = getUpkeepAmount() - cash;
	  if(amount<=0){return true;}
	  ItemStack stack;
	  int val;
	  int eaten = 0;
	  if(side>=0 && inventory instanceof ISidedInventory)
	    {
	    int[] ind = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side);
	    for(int i : ind)
	      {
	      stack = inventory.getStackInSlot(i);
	      val = this.itemToCash(stack);
	      if(val<=0){continue;}
	      while(eaten < amount && stack.stackSize>0)
	        {
	        eaten+=val;
	        stack.stackSize--;
	        inventory.markDirty();
	        }
	      if(stack.stackSize<=0)
	        {
	        inventory.setInventorySlotContents(i, null);
	        }
	      }    
	    }
	  else
	    {
	    for(int i = 0 ; i<inventory.getSizeInventory();i++)
	      {
	      stack = inventory.getStackInSlot(i);
	      val = this.itemToCash(stack);
	      if(val<=0){continue;}
	      while(eaten < amount && stack.stackSize>0)
	        {
	        eaten+=val;
	        stack.stackSize--;
	        inventory.markDirty();
	        }
	      if(stack.stackSize<=0)
	        {
	        inventory.setInventorySlotContents(i, null);
	        }
	      }    
	    }
	  cash = cash+eaten;
	  if(cash>= getUpkeepAmount()) {
		  timeToPayday = 24000;
		  return true;
	  }
	  return false;
}

@Override
public void onDeath(DamageSource source)
  {
	//TODO - drop inventory
  if(!worldObj.isRemote)
    {
    if(horseAI!=null)
      {
      horseAI.onKilled();
      }
    validateTownHallPosition();
    TileTownHall townHall = getTownHall();
    if(townHall!=null)
      {
      townHall.handleNpcDeath(this, source);
      }
    }  
  super.onDeath(source);  
  }

@Override
public int getArmorValueOverride()
  {
  return -1;
  }

@Override
public int getAttackDamageOverride()
  {
  return -1;
  }

@Override
public void setTownHallPosition(BlockPosition pos)
  {
  if(pos!=null){this.townHallPosition = pos.copy();}
  else{this.townHallPosition=null;}
  }

@Override
public BlockPosition getTownHallPosition()
  {
  return townHallPosition;
  }

@Override
public TileTownHall getTownHall()
  {
  if(getTownHallPosition()!=null)    
    {
    BlockPosition pos = getTownHallPosition();
    TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof TileTownHall)
      {
      return (TileTownHall)te;
      }
    }
  return null;
  }

@Override
public void handleTownHallBroadcast(TileTownHall tile, BlockPosition position)
  {
	//setCustomNameTag("Broadcast recieved");
  validateTownHallPosition();
  BlockPosition pos = getTownHallPosition();
  if(pos!=null)
    {    
    double curDist = getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
    double newDist = getDistanceSq(position.x+0.5d, position.y, position.z+0.5d);
    if(newDist<curDist)
      {
      setCustomNameTag("Set TH");
      setTownHallPosition(position);
      if(upkeepAutoBlock==null || upkeepAutoBlock.equals(pos))
        {
        upkeepAutoBlock=position;
        }
      }
    }
  else
    {
	  setCustomNameTag("set TH");
    setTownHallPosition(position);
    if(upkeepAutoBlock==null || upkeepAutoBlock.equals(pos))
      {
      upkeepAutoBlock=position;
      }
    }
  }

private boolean validateTownHallPosition()
  {
  if(getTownHallPosition()==null){return false;}  
  BlockPosition pos = getTownHallPosition();
  if(!worldObj.blockExists(pos.x, pos.y, pos.z)){return true;}//cannot validate, unloaded...assume good 
  TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
  if(te instanceof TileTownHall)
    {
    if(canBeCommandedBy(((TileTownHall) te).getOwnerName()))
      {
      return true;
      }
    }
  setTownHallPosition(null);
  return false;
  }

@Override
public Command getCurrentCommand()
  {
  return playerIssuedCommand;
  }

@Override
public void handlePlayerCommand(Command cmd)
  {  
  if(cmd!=null && cmd.type==CommandType.ATTACK)
    {
    Entity e = cmd.getEntityTarget(worldObj);
    AWLog.logDebug("handling attack command : "+e);
    if(e instanceof EntityLivingBase)
      {
      EntityLivingBase elb = (EntityLivingBase)e;
      if(isHostileTowards(elb))//only allow targets npc is hostile towards
        {
        if(elb instanceof NpcPlayerOwned)//if target is also a player-owned npc
          {
          NpcPlayerOwned n = (NpcPlayerOwned)elb;
          if(n.getTeam()!=getTeam())//only allow target if they are not on the same team (non-teamed cannot attack other non-teamed, cannot attack team-mates npcs either)
            {
            setAttackTarget(n);
            }
          }
        else
          {
          setAttackTarget(elb);        
          }      
        }
      }
    cmd=null;
    }else if(cmd!=null && cmd.type == CommandType.MOVE) {
    	//x2=cmd.x;
    	//y2=cmd.z;
    }
  this.setPlayerCommand(cmd); 
  }

@Override
public void setPlayerCommand(Command cmd)
  {
  this.playerIssuedCommand = cmd;
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  if(e instanceof NpcPlayerOwned)
    {
    NpcPlayerOwned npc = (NpcPlayerOwned)e;
    Team t = npc.getTeam();
    return t!=getTeam();
    }
  else if(e instanceof NpcFaction)
    {
    NpcFaction npc = (NpcFaction)e;
    return npc.isHostileTowards(this);//cheap trick to determine if should be hostile or not using the faction-based npcs standing towards this players npcs...handled in NpcFaction
    }
  else if(e instanceof EntityPlayer)
    {
    Team t = worldObj.getScoreboard().getPlayersTeam(e.getCommandSenderName());
    return t!=getTeam();
    }
  else
    {
    String n = EntityList.getEntityString(e);
    List<String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcType(), getNpcSubType());
    if(targets.contains(n))
      {
      return true;
      }
    }
  return false;
  }

@Override
public boolean canTarget(Entity e)
  {
  if(e instanceof NpcPlayerOwned)
    {
    NpcPlayerOwned npc = (NpcPlayerOwned)e;
    Team t = npc.getTeam();
    return t!=getTeam();//do not allow npcs to target their own teams npcs
    }
  else if (e instanceof EntityPlayer)
    {
    Team t = worldObj.getScoreboard().getPlayersTeam(e.getCommandSenderName());
    return t!=getTeam();//do not allow npcs to target their own teams players
    }
  return e instanceof EntityLivingBase;
  }

@Override
public boolean canBeAttackedBy(Entity e)
  {
  if(e instanceof NpcPlayerOwned)
    {
    NpcPlayerOwned npc = (NpcPlayerOwned)e;
    return npc.getTeam()!=getTeam();//can only be attacked by non-same team -- disable friendly fire and combat amongst neutrals
    }
  return true;
  }

protected boolean isHostileTowards(Team team)
  {
  Team a = getTeam();
  if(a!=null && team!=null && a!=team){return true;}
  return false;
  }

@Override
public void onWeaponInventoryChanged()
  {
  updateTexture();
  }

@Override
public int getFoodRemaining()
  {
  return foodValueRemaining;
  }

@Override
public void setFoodRemaining(int food)
  {
  this.foodValueRemaining = food;
  }

@Override
public BlockPosition getUpkeepPoint()
  {  
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepPosition();
    }
  return upkeepAutoBlock;
  }

@Override
public void setUpkeepAutoPosition(BlockPosition pos)
  {
  upkeepAutoBlock = pos;
  }

@Override
public int getUpkeepBlockSide()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepBlockSide();
    }
  return 0;
  }

@Override
public int getUpkeepDimensionId()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepDimension();
    }
  return worldObj.provider.dimensionId;
  }

@Override
public int getUpkeepAmount()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepAmount();
    }
  return AWNPCStatics.npcDefaultUpkeepWithdraw;
  }

@Override
public boolean requiresUpkeep()
  {
  return true;
  }

@Override
protected boolean interact(EntityPlayer player)
  {
  if(player.worldObj.isRemote){return false;}
  Team t = player.getTeam();
  Team t1 = getTeam();
  boolean baton = player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton;
  if(t==t1 && this.canBeCommandedBy(player.getCommandSenderName()) && !baton)
    {
    if(player.isSneaking())
      {
      if(this.followingPlayerName==null)
        {
        this.followingPlayerName = player.getCommandSenderName();    
        }
      else if(this.followingPlayerName.equals(player.getCommandSenderName()))
        {
        this.followingPlayerName = null;
        }
      else
        {
        this.followingPlayerName = player.getCommandSenderName();      
        }
      }
    else if(player.getCurrentEquippedItem()!= null && this.getTownHall() != null){ //player rclicked with land grant!
    	if(player.getCurrentEquippedItem().getItem()==AWNpcItemLoader.scanner) {
    		this.getTownHall().grantLand(this, player.getCurrentEquippedItem());
    		this.setCustomNameTag("Granted land!");
    		ItemNPCSettings scanSettings = new ItemNPCSettings();
    		ItemNPCSettings.getSettingsFor(player.getCurrentEquippedItem(), scanSettings, player.worldObj);
    		scanSettings.setOwner(this);
    	}
    }
    else
      {
      openGUI(player);
      }
    return true;
    }
  return true;
  }

public int itemToCash(ItemStack coins) {
	if(coins == null) {return 0;}
	//values: copper = 1, silver = 10, gold = 100
	Item c = coins.getItem();
	int i = Item.getIdFromItem(c);
	if(c==Items.gold_ingot) {
		return coins.stackSize * 1000;
	}else if(i==5301) { //copper
		return coins.stackSize * 1;
	}else if (i==5302) { //silver
		return coins.stackSize * 10;
	}else if (i==5303) {//gold
		return coins.stackSize * 100;
	}
	return 0;
}

public void setCash(int i) {
	cash = i;
}

public void addItemStack(InventoryBackpack storage, ItemStack stack) {
	for(int i=0; i<=storage.getSizeInventory()-1; i++) {
		if(storage.getStackInSlot(i)== null || storage.getStackInSlot(i).isItemEqual(stack)) {
			storage.setInventorySlotContents(i, stack);
		}
	}
	storage.markDirty();
}

public void removeItems(InventoryBasic inv, ItemStack stack) {
	int a = stack.stackSize;
	for(int i=0; i<inv.getSizeInventory(); i++) {
		if( a <= 0) {return;}
		ItemStack s = inv.getStackInSlot(i);
		if(s.isItemEqual(stack)) {
			int amt = Math.min(a, s.stackSize);
			s.stackSize -= amt;
			a-=amt;
		}
	}
	inv.markDirty();
}


public NpcTrader getTrader(List<NpcTrader> traderList, INeed need) { //DECREMENTED - ignore
	//Collections.sort(traderList, new SortByDistance(this));
	return traderList.get(0);
	/*
	 * for(NpcTrader t : traderList) { if(t != null) { trader = t; break; } }
	 */
	//if(trader == null) {return false;}
}

public POTrade getTrade(NpcTrader trader, INeed need) {//DECREMENTED - ignore
	POTradeList tradeListP = trader.getTradeList();
	if(tradeListP==null) {return null;}
	List<POTrade>tradeList = tradeListP.getTradeList();
	Collections.sort(tradeList, Collections.reverseOrder(new SortByValue()));
	return tradeList.get(0);
}

public void withdraw(List<NpcTrader> traderList, INeed need) {//DECREMENTED - ignore
	NpcTrader trader = getTrader(traderList, need);
	POTrade trade = getTrade(trader, need);
	if(trade == null) {return;}
	
	for(ItemStack stack : trade.getCompactOutput()) {
		if(stack != null) {
			if(!trader.hasSufficient(stack, stack.stackSize)) {
				return;
			}
		}
	}
	int sum = 0;
	for(ItemStack stack : trade.getCompactInput()) {
		sum += itemToCash(stack);
	}
	if(sum > cash) {return;}

	if(!trader.canStore(trade.getCompactInput())) {
		return;
	}

	for(ItemStack stack : trade.getCompactOutput()) {
		if(stack != null) {
			trader.removeItems(stack, stack.stackSize);
			setFoodRemaining(getFoodRemaining() + AncientWarfareNPC.statics.getFoodValue(stack));
		}
	}

	//for(ItemStack stack : trade.getCompactInput()) {
	//trader.addItems(stack);
	//}
	for(int i=0; i<9; i++) {
		ItemStack stack = trade.getInputStack(i);
		cash -= this.itemToCash(stack);
		if(stack != null) {
			trader.addItems(stack);
		}
	}
}

public boolean withdrawFood(List<NpcTrader> traderList){//DECREMENTED - ignore
	int amount = getUpkeepAmount() - getFoodRemaining();
	if(amount<=0){return true;}
	  
	NpcTrader trader = traderList.get(0);

	POTradeList tradeListP = trader.getTradeList();
	if(tradeListP==null) {return false;}
	List<POTrade>tradeList = tradeListP.getTradeList();
	
	Collections.sort(tradeList, Collections.reverseOrder(new SortByValue()));
	POTrade trade = tradeList.get(0);
		/*
		 * for(POTrade t : tradeList) { if(t!= null) { trade = t; break; } }
		 */
	//if(trade == null) {return false;}
	
	ItemStack backpack = trader.getEquipmentInSlot(0);
	if(backpack!=null && backpack.getItem() instanceof ItemBackpack){
		InventoryBackpack storage = ItemBackpack.getInventoryFor(backpack);
		
		for(ItemStack stack : trade.getCompactOutput()) {
			if(stack != null) {
				if(!trader.hasSufficient(stack, stack.stackSize)) {
					return false;
				}
			}
		}
		int sum = 0;
		for(ItemStack stack : trade.getCompactInput()) {
			sum += itemToCash(stack);
		}
		if(sum > cash) {return false;}
		
		if(!trader.canStore(trade.getCompactInput())) {
			return false;
		}
		
		for(ItemStack stack : trade.getCompactOutput()) {
			if(stack != null) {
				trader.removeItems(stack, stack.stackSize);
				setFoodRemaining(getFoodRemaining() + AncientWarfareNPC.statics.getFoodValue(stack));
			}
		}
		//for(ItemStack stack : trade.getCompactInput()) {
			//trader.addItems(stack);
		//}
		for(int i=0; i<9; i++) {
			ItemStack stack = trade.getInputStack(i);
			cash -= this.itemToCash(stack);
			if(stack != null) {
				trader.addItems(stack);
			}
		}
	 }
	if(getFoodRemaining() >= getUpkeepAmount()) {
		return true;
	}
	return false;
 }	

public void initNeeds() {
	
}

public void addNeed() {
	
}

public void removeNeed() {
	
}

@Override
public void openGUI(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getEntityId(), 0, 0);  
  }

public TileCity getCity() {
	if(getTownHall()!=null) {
		BlockPosition p = getTownHall().cityPos;
		if(p!=null) {
			TileEntity te = worldObj.getTileEntity(p.x, p.y, p.z);
			if(te instanceof TileCity){
				TileCity c = (TileCity)te;
				return c;
			}
		}
	}
	return null;
}

@Override
public void onLivingUpdate(){  
	super.onLivingUpdate();
	Command cmd = this.getCurrentCommand();
	if(guide != null) {
		double[] goal = NpcCommand.getRelOffset(guide.posX, guide.posZ, this.angle, this.xOff, this.zOff);
		this.handlePlayerCommand(new Command(CommandType.ATTACK_AREA, goal[0], 90, goal[1]));
	}
	if(cmd != null) {
		double[] d = cmd.getCoords();
		double dist = Trig.getDistance(this.posX, d[1], this.posZ, d[0], d[1], d[2]);
		if(dist <= 1.0) {
			this.setPosition(d[0], this.posY, d[2]);
		}
		this.setCustomNameTag(""+Math.round(dist * 100.0) / 100.0);
	}else {
		//this.setCustomNameTag("Bob");
	}
	
	//if(timeToPayday>0) {timeToPayday--;}
	timeToPayday--;
	
	for(INeed n : needs) {
		n.update();
		if(n.getAmount()<=n.getThreshold()) {
			buyNeeds();
		}
	}
}

public void buyNeeds() {
	//TODO split this into sub-functions, this is kind of awful 
	if(getTownHall() == null || getTownHall().city == null) {return;}
	for(INeed n : needs) {
		NeedBase n2 = (NeedBase)n;
		if(n.getAmount()<=n.getThreshold()) {
			TileCity c = getTownHall().city;
			if(c!=null) {
				for(Enumeration k = n2.getNeeds().keys(); k.hasMoreElements();) {
					ItemStack i = new ItemStack(Item.getItemById((Integer) k.nextElement()));
					int price = c.getHighestBuy(i.getItem()) + 100;
					this.remMoney(100);
					if(price <= this.sumMoney()) {
						BuyOrder b = c.getBuy(this, i);
						if(b!=null) {
							if(this.ticksExisted%20 == 0) {
								System.out.println("Increasing price: " + price);
								b.setPrice(price);
							}
						}else {	
							System.out.println("Adding order for " + i.stackSize + " " + i.getDisplayName() + " at " + price);
							b = new BuyOrder(i,price,this,i.stackSize);
							c.addBuy(b);
							this.remMoney(price);
						}
					}
				}
			}
		}
	}
}

@Override
public void travelToDimension(int par1)
  {
  this.townHallPosition=null;
  this.upkeepAutoBlock=null;
  super.travelToDimension(par1);
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag){  
  super.readEntityFromNBT(tag);
  foodValueRemaining = tag.getInteger("foodValue");
  if(tag.hasKey("command")){playerIssuedCommand = new Command(tag.getCompoundTag("command"));} 
  if(tag.hasKey("townHall")){townHallPosition = new BlockPosition(tag.getCompoundTag("townHall"));}
  if(tag.hasKey("upkeepPos")){upkeepAutoBlock = new BlockPosition(tag.getCompoundTag("upkeepPos"));}
  
  NBTTagList needList = tag.getTagList("needs", Constants.NBT.TAG_COMPOUND);

	for(int i = 0;i < needList.tagCount(); i++) {
		NBTTagCompound tg = needList.getCompoundTagAt(i);
		needs.add(new NeedBase(tg));
	}
  
  onWeaponInventoryChanged();
}

@Override
public void writeEntityToNBT(NBTTagCompound tag){  
  super.writeEntityToNBT(tag);
  tag.setInteger("foodValue", foodValueRemaining);
  if(playerIssuedCommand!=null){tag.setTag("command", playerIssuedCommand.writeToNBT(new NBTTagCompound()));}
  if(townHallPosition!=null){tag.setTag("townHall", townHallPosition.writeToNBT(new NBTTagCompound()));}
  if(upkeepAutoBlock!=null){tag.setTag("upkeepPos", upkeepAutoBlock.writeToNBT(new NBTTagCompound()));}
  
  NBTTagList needsL = new NBTTagList();
  
  for(INeed need : needs) {
	  needsL.appendTag(need.writeToNBT(new NBTTagCompound()));
  }
  tag.setTag("needs", needsL);
}

public NpcPlayerOwned getSpouse() {
	return spouse;
}

public void setSpouse(NpcPlayerOwned spouse) {
	this.spouse = spouse;
}

public void addSuitor(NpcPlayerOwned npc) {
	suitors.add(npc);
	
}

public List<NpcPlayerOwned> getSuitors() {
	return suitors;
}

public void setSuitors(List<NpcPlayerOwned> suitors) {
	this.suitors = suitors;
}

public int getLandArea() {
	if(getTownHall() == null) {return Integer.MIN_VALUE;}
	if(getTownHall().getOwnedLands(this) == null) {return Integer.MIN_VALUE;}
	if (getTownHall().getOwnedLands(this).isEmpty()) {return Integer.MIN_VALUE;}
	
	int area = 0;
	for(LandGrant plot : getTownHall().getOwnedLands(this)) {
		area += plot.getArea();
	}
	return area;
}

}