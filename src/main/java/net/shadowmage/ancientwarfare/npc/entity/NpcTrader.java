package net.shadowmage.ancientwarfare.npc.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFleeHostiles;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFollowCommand;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedGetFood;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedTrader;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemTradeOrder;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;

public class NpcTrader extends NpcPlayerOwned
{

public EntityPlayer trader;//used by guis/containers to prevent further interaction
private POTradeList tradeList = new POTradeList();
private NpcAIPlayerOwnedTrader tradeAI;

public NpcTrader(World par1World)
  {
  super(par1World);
  
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(0, (horseAI=new NpcAIPlayerOwnedRideHorse(this)));
  this.tasks.addTask(2, new NpcAIFollowPlayer(this));
  this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
  this.tasks.addTask(3, new NpcAIFleeHostiles(this));
  this.tasks.addTask(4, tradeAI = new NpcAIPlayerOwnedTrader(this));
  this.tasks.addTask(5, new NpcAIPlayerOwnedGetFood(this));  
  this.tasks.addTask(6, new NpcAIPlayerOwnedIdleWhenHungry(this)); 
  this.tasks.addTask(7, new NpcAIMoveHome(this, 50.f, 3.f, 30.f, 3.f));
  
  //post-100 -- used by delayed shared tasks (look at random stuff, wander)
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
  
  super.age = 5040000;
  }

@Override
public void onEntityUpdate() {
	super.setFoodRemaining(10000);
	super.setCash(100000);
	super.onEntityUpdate();
}

@Override
public boolean isPayday() {
	return false;
}

@Override
public boolean idle() {
	return false;
}

@Override
public void addNeeds() {
	return;
}

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  return stack!=null && stack.getItem() instanceof ItemTradeOrder;
  }

@Override
public void onOrdersInventoryChanged()
  {
  tradeList=null;
  ItemStack order = ordersStack;
  if(order!=null && order.getItem() instanceof ItemTradeOrder)
    {
    tradeList = TradeOrder.getTradeOrder(order).getTradeList();
    }
  tradeAI.onOrdersUpdated();
  }

@Override
public String getNpcSubType()
  {
  return "";
  }

@Override
public String getNpcType()
  {
  return "trader";
  }

@Override
protected boolean interact(EntityPlayer player)
  {
  if(player.worldObj.isRemote){return true;}
  boolean baton = player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton;
  if(baton){return true;}  
  if(player.getCommandSenderName().equals(getOwnerName()))//owner
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
    }
  else//non-owner
    {
    if(!player.worldObj.isRemote && getFoodRemaining()>0 && trader==null)
      {
      trader=player;    
      openAltGui(player);
      }
    }
  return true;   
  }

@Override
public void openGUI(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getEntityId(), 0, 0);  
  }

@Override
public void openAltGui(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_PLAYER_OWNED_TRADE, getEntityId(), 0, 0);  
  }

@Override
public boolean hasAltGui()
  {
  return true;
  }

@Override
public boolean shouldBeAtHome()
  {
  if((!worldObj.provider.hasNoSky && !worldObj.provider.isDaytime()) || worldObj.isRaining())
    { 
    return true;
    }
  return false;
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  return false;
  }

public POTradeList getTradeList()
  {
  return tradeList;
  }

public void removeItems(ItemStack stack, int quantity) {
	InventoryBackpack backpackInventory = ItemBackpack.getInventoryFor(this.getEquipmentInSlot(0));
    if(backpackInventory  == null) { return; }
    for(int i=0; i<backpackInventory.getSizeInventory(); i++) { //for each slot in my backpack
    	if(quantity <=0) {
    		break;
    	}
    	ItemStack s = backpackInventory.getStackInSlot(i); //get the item in that slot
    	if(s != null) { //if there is an item in that slot
    		if(s.isItemEqual(stack)) { //if it's the item we're removing
    			if(quantity >= s.stackSize) { //if we need the whole stack (or more)
    				quantity -= s.stackSize; //decrease the remaining amount to remove by the number of items 
    				backpackInventory.setInventorySlotContents(i, null); //delete the stack
    			}else{ //if we need only part of the stack
    				//backpackInventory.setInventorySlotContents(i, new ItemStack(s.getItem(), s.stackSize - quantity));
    				backpackInventory.decrStackSize(i, quantity); //remove part of the stack
    				quantity = 0;
    			}
    		}
    	}
    }

    backpackInventory.markDirty();
    ItemBackpack.writeBackpackToItem(backpackInventory, getEquipmentInSlot(0));
}

public void addItems(ItemStack stack) {
	InventoryBackpack backpackInventory = ItemBackpack.getInventoryFor(this.getEquipmentInSlot(0));
	if(backpackInventory  == null) { return; }
	for(int i=0; i<backpackInventory.getSizeInventory(); i++) { //for slot
		ItemStack s = backpackInventory.getStackInSlot(i); //get stack
		if(s==null) { //if this slot is empty
			backpackInventory.setInventorySlotContents(i, stack);
			break;
		}else{ //slot's not empty
			if(s.isItemEqual(stack)){ //if it's the item we want
				if(s.getMaxStackSize() - s.stackSize >= stack.stackSize) {
					s.stackSize += stack.stackSize;
					break;
				}else{
					int storable = s.getMaxStackSize() - s.stackSize;
					stack.stackSize -= storable;
					s.stackSize = s.getMaxStackSize();
				}
			}
		}
	}
	backpackInventory.markDirty();
    ItemBackpack.writeBackpackToItem(backpackInventory, this.getEquipmentInSlot(0));
}


public boolean hasSufficient(ItemStack stack, int qty) {
  InventoryBackpack backpackInventory = ItemBackpack.getInventoryFor(this.getEquipmentInSlot(0));
  int found = 0;
  if(backpackInventory  != null) {
        for(int i=0; i<backpackInventory.getSizeInventory(); i++) {
            ItemStack s = backpackInventory.getStackInSlot(i);
            if(s != null) {
                if(s.isItemEqual(stack)) {
                    found += s.stackSize;
                }
                if(found >= qty) {
                    return true;
                }
            }
        }
  }
  return false;
}

public boolean canStore(List<ItemStack> items) { //note: must all be the same item, or shit gets fucky. 
  InventoryBackpack backpackInventory = ItemBackpack.getInventoryFor(this.getEquipmentInSlot(0));
  int num = 0;
  int max = items.get(0).getMaxStackSize();
  ItemStack s1 = items.get(0);
  for(ItemStack stack : items) {
      num += stack.stackSize;
  }
  for(int i=0; i<backpackInventory.getSizeInventory(); i++) {
      ItemStack s = backpackInventory.getStackInSlot(i);
      if(s==null) { //slots empty
          num -= max; //so we can store a whole stack
      }else { //slot's not empty
          if(s.isItemEqual(s1)) { //if it's the item we want
              num += max - s.stackSize;
          }
      }
  }
  return num <= 0;
}

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setTag("tradeAI", tradeAI.writeToNBT(new NBTTagCompound()));
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {
  super.readEntityFromNBT(tag);
  onOrdersInventoryChanged();
  tradeAI.readFromNBT(tag.getCompoundTag("tradeAI"));
  }

}