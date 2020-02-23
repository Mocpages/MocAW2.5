package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.tile.TileCity;

public class ContainerCity  extends ContainerBase{
	public TileCity city;

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
			}
		}
		else{
			throw new IllegalArgumentException("cannot open city gui for tile: "+te);
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
