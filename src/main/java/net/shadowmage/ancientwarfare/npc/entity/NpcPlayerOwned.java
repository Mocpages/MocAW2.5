package net.shadowmage.ancientwarfare.npc.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.command.IEntitySelector;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcPlayerOwnedTrade;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;



public abstract class NpcPlayerOwned extends NpcBase
{

private Command playerIssuedCommand;//TODO load/save
private int foodValueRemaining = 0;
private int cash = 1000;
public InventoryBackpack invBack = new InventoryBackpack(27);
protected NpcAIPlayerOwnedRideHorse horseAI;

private BlockPosition townHallPosition;
private BlockPosition upkeepAutoBlock;

public NpcPlayerOwned(World par1World)
  {
  super(par1World);
  invBack.setInventorySlotContents(0, new ItemStack(Items.gold_ingot, 64));
  }

@Override
public void setCurrentItemOrArmor(int slot, ItemStack stack)
  {
  super.setCurrentItemOrArmor(slot, stack);
  if(slot==0){onWeaponInventoryChanged();}
  }

@Override
public void onDeath(DamageSource source)
  {
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
  validateTownHallPosition();
  BlockPosition pos = getTownHallPosition();
  if(pos!=null)
    {    
    double curDist = getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
    double newDist = getDistanceSq(position.x+0.5d, position.y, position.z+0.5d);
    if(newDist<curDist)
      {
      setTownHallPosition(position);
      if(upkeepAutoBlock==null || upkeepAutoBlock.equals(pos))
        {
        upkeepAutoBlock=position;
        }
      }
    }
  else
    {
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
    else
      {
      openGUI(player);
      }
    return true;
    }
  return true;
  }

public int itemToCash(ItemStack coins) {
	if(coins.getItem()==Item.getItemById(266)) {
		return coins.stackSize;
	}
	return 0;
}

public void addItemStack(InventoryBackpack storage, ItemStack stack) {
	for(int i=0; i<=storage.getSizeInventory(); i++) {
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

public boolean withdrawFood(List<NpcTrader> traderList){
	int amount = getUpkeepAmount() - getFoodRemaining();
	if(amount<=0){return true;}
	  
	//IEntitySelector selector = new IEntitySelector();
	//double dist=50; 
	//AxisAlignedBB bb = boundingBox.expand(dist, dist/2, dist);
	//List<NpcTrader> traderList = worldObj.getEntitiesWithinAABB(NpcTrader.class, bb);
	//List<NpcTrader> traderList = NpcTrader.getTraderList();
	//Collections.sort(traderList, new SortByDistance(this));
	NpcTrader trader = traderList.get(0);
		/*
		 * for(NpcTrader t : traderList) { if(t != null) { trader = t; break; } }
		 */
	//if(trader == null) {return false;}
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

		if(!trader.canStore(trade.getCompactOutput())) {
			//return false;
		}
		
		for(ItemStack stack : trade.getCompactOutput()) {
			if(stack != null) {
				trader.removeItems(stack, stack.stackSize);
				setFoodRemaining(getFoodRemaining() + AncientWarfareNPC.statics.getFoodValue(stack));
			}
		}
		for(ItemStack stack : trade.getCompactInput()) {
			trader.addItems(stack);
		}
	 }
	if(getFoodRemaining() >= getUpkeepAmount()) {
		return true;
	}
	return false;
 }	

@Override
public void openGUI(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getEntityId(), 0, 0);  
  }

@Override
public void onLivingUpdate()
  {  
  super.onLivingUpdate();
  if(foodValueRemaining>0){foodValueRemaining--;}
  }

@Override
public void travelToDimension(int par1)
  {
  this.townHallPosition=null;
  this.upkeepAutoBlock=null;
  super.travelToDimension(par1);
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  foodValueRemaining = tag.getInteger("foodValue");
  if(tag.hasKey("command")){playerIssuedCommand = new Command(tag.getCompoundTag("command"));} 
  if(tag.hasKey("townHall")){townHallPosition = new BlockPosition(tag.getCompoundTag("townHall"));}
  if(tag.hasKey("upkeepPos")){upkeepAutoBlock = new BlockPosition(tag.getCompoundTag("upkeepPos"));}
  onWeaponInventoryChanged();
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {  
  super.writeEntityToNBT(tag);
  tag.setInteger("foodValue", foodValueRemaining);
  if(playerIssuedCommand!=null){tag.setTag("command", playerIssuedCommand.writeToNBT(new NBTTagCompound()));}
  if(townHallPosition!=null){tag.setTag("townHall", townHallPosition.writeToNBT(new NBTTagCompound()));}
  if(upkeepAutoBlock!=null){tag.setTag("upkeepPos", upkeepAutoBlock.writeToNBT(new NBTTagCompound()));}
  }

}