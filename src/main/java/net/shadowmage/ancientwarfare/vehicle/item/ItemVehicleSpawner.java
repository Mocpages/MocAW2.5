package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.types.VehicleType;

public class ItemVehicleSpawner extends Item implements IItemClickable{


public ItemVehicleSpawner(int itemID)
  {
  //super(itemID,true);
  //this.setCreativeTab(CreativeTabAW.vehicleTab);
  }

//@Override
public boolean onUsedFinal(World world, EntityPlayer player, ItemStack stack, BlockPosition hit, int side)
  {
  if(hit==null || world.isRemote || stack == null)
    {
    return false;
    }
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AWVehSpawner"))
    {
    NBTTagCompound tag = stack.getTagCompound().getCompoundTag("AWVehSpawner");
    int level = tag.getInteger("lev");    
    //hit = BlockTools.offsetForSide(hit, side);      
    VehicleBase vehicle = VehicleType.getVehicleForType(world, stack.getItemDamage(), level);
    if(tag.hasKey("health"))
      {
      vehicle.setHealth(tag.getFloat("health"));
      }
    //vehicle.teamNum = TeamTracker.instance().getTeamForPlayer(player);
    vehicle.setPosition(hit.x+0.5d, hit.y, hit.z+0.5d);
    vehicle.prevRotationYaw = vehicle.rotationYaw = -player.rotationYaw + 180;
    vehicle.localTurretDestRot = vehicle.localTurretRotation = vehicle.localTurretRotationHome = vehicle.rotationYaw;
    if(true)
      {
      vehicle.setSetupState(true, 100);
      }
    world.spawnEntityInWorld(vehicle);      
    if(!player.capabilities.isCreativeMode)
      {
      stack.stackSize--;
      if(stack.stackSize<=0)
        {
        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);    
        }
      }
    return true;
    }
 // Config.logError("Vehicle spawner item was missing NBT data, something may have corrupted this item");
  return false;
  }

@Override
public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
  {
  super.addInformation(stack, par2EntityPlayer, par3List, par4);  
  if(stack!=null)
    {
    if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AWVehSpawner"))
      {
      NBTTagCompound tag = stack.getTagCompound().getCompoundTag("AWVehSpawner");
      par3List.add("Material Level: "+tag.getInteger("lev"));
      if(tag.hasKey("health"))
        {
        par3List.add("Vehicle Health: "+tag.getFloat("health"));
        }
      }
    }  
  }


public static int getVehicleLevelForStack(ItemStack stack)
  {
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("AWVehSpawner"))
    {
    return stack.getTagCompound().getCompoundTag("AWVehSpawner").getInteger("lev");
    }
  return 0;
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean cancelRightClick(EntityPlayer player, ItemStack stack) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void onRightClick(EntityPlayer player, ItemStack stack) {
	// TODO Auto-generated method stub
	
}

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean cancelLeftClick(EntityPlayer player, ItemStack stack) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack) {
	// TODO Auto-generated method stub
	
}
}
