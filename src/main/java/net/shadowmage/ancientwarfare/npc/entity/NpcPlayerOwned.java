package net.shadowmage.ancientwarfare.npc.entity;

import java.util.List;
import java.util.*; 

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFleeHostiles;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFollowCommand;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedGetFood;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedTrader;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;

import net.shadowmage.ancientwarfare.npc.container.ContainerNpcPlayerOwnedTrade;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public abstract class NpcPlayerOwned extends NpcBase
{

private Command playerIssuedCommand;//TODO load/save
private int foodValueRemaining = 0;

protected NpcAIPlayerOwnedRideHorse horseAI;

private BlockPosition townHallPosition;
private BlockPosition upkeepAutoBlock;

public NpcPlayerOwned(World par1World)
  {
  super(par1World);
  

  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(0, (horseAI=new NpcAIPlayerOwnedRideHorse(this)));
  this.tasks.addTask(2, new NpcAIFollowPlayer(this));
  this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
  this.tasks.addTask(3, new NpcAIFleeHostiles(this));
  this.tasks.addTask(5, new NpcAIPlayerOwnedGetFood(this));  
  this.tasks.addTask(6, new NpcAIPlayerOwnedIdleWhenHungry(this)); 
  this.tasks.addTask(7, new NpcAIMoveHome(this, 50.f, 3.f, 30.f, 3.f));
  
  //post-100 -- used by delayed shared tasks (look at random stuff, wander)
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
  
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

/*
 * POTrade cheapest = null;
	NpcTrader cheapest_trader;
	int goldSumCheapest = Integer.MAX_VALUE;
	int amount = getUpkeepAmount() - getFoodRemaining();
	if(amount<=0){return true;}
	
	
	for(NpcTrader trader : traderList) {
		
		POTradeList list_trades = trader.getTradeList();
		List<POTrade> tradeList = new ArrayList<POTrade>();
		list_trades.getTrades(tradeList);
		List<POTrade> foodTrades = new ArrayList<POTrade>();
		
		for(POTrade trade : tradeList) {
			boolean hasFood = false;
			for(int i = 0; i<9; i++) {
				ItemStack stack = trade.getInputStack(i);
				if(AncientWarfareNPC.statics.getFoodValue(stack) > 0) {
					hasFood = true;
				}
			}
			if(hasFood) {
				foodTrades.add(trade);
			}
		}
		
		int goldSum;
		for(POTrade trade : foodTrades){
			goldSum = 0;
			for(int i = 0; i<9; i++) {
				ItemStack stack = trade.getInputStack(i);
				if(stack==null) {continue;}
				if(Item.getIdFromItem(stack.getItem())==266) {
					goldSum += stack.stackSize;
				}
				
			}
			
			if(goldSum < goldSumCheapest) {
				goldSumCheapest = goldSum;
				cheapest = trade;
			}
		}
	}
	
	if(cheapest==null) {return false;}
	ItemStack stack;
	int val=0;
	int eaten = 0;
	
	for(int i = 0 ; i<9;i++){
		stack = cheapest.getOutputStack(i);
		val += AncientWarfareNPC.statics.getFoodValue(stack);
		
	}    
	eaten=val;
	//cheapest.perfromTrade(this, container.tradeInput, container.storage);
	setFoodRemaining(getFoodRemaining()+eaten);
	return getFoodRemaining()>=getUpkeepAmount();
 */
//this is the old version, keep that shit collapsed. If your IDE doesn't let you collapse a block comment, get a better IDE.
//Sorry.

public boolean withdrawFood(List<NpcTrader> traderList){
	
	if(getFoodRemaining()>=getUpkeepAmount()){return true;}
	

	for(NpcTrader trader : traderList) {
		InventoryBackpack backpack = trader.backpackInventory;
		for(int i = 0; i<= backpack.getSizeInventory(); i++) {
			ItemStack stack = backpack.getStackInSlot(i);
			int foodValue = AncientWarfareNPC.statics.getFoodValue(stack);
			if(foodValue >=0){
				//setFoodRemaining(getFoodRemaining()+foodValue);
				backpack.decrStackSize(i, 1);
				if(getFoodRemaining()>=getUpkeepAmount()) { return true;}
			}
		}
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
