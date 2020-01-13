package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class ContainerLandGrant extends ContainerBase{

	public ContainerLandGrant(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);

		if(player.worldObj.isRemote){return;}
		ItemStack stack = player.getCurrentEquippedItem();
		if(stack==null || stack.getItem()==null || stack.getItem() != AWNpcItemLoader.scanner){
			throw new IllegalArgumentException("Cannot open Land Grant GUI for null or wrong stack/item.");
		}  
	}
	
	@Override
	public void handlePacketData(NBTTagCompound tag)
	  {
	  if(tag.hasKey("landGrant"))
	    {

	    }  
	  }

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
	  {  
	  super.onContainerClosed(par1EntityPlayer);
	  }
}
