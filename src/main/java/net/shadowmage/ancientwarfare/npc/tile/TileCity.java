package net.shadowmage.ancientwarfare.npc.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.container.ContainerCity;

public class TileCity  extends TileEntity implements IOwnable, IInventory, IInteractableTile{
	private String ownerName = "";
	private InventoryBasic inventory = new InventoryBasic(27);
	private List<ContainerCity> viewers = new ArrayList<ContainerCity>();

	
	@Override
	public boolean canUpdate(){
	  return true;
	}

	@Override
	public void updateEntity(){
	  if(worldObj.isRemote){return;}

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
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag){
	  super.writeToNBT(tag);
	  tag.setString("owner", ownerName);
	  tag.setTag("inventory", InventoryTools.writeInventoryToNBT(inventory, new NBTTagCompound()));
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
