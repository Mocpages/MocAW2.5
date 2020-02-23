package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemNPCSettings;

public class ContainerLandGrant extends ContainerBase{
	public float corvee,tithe,rent;
	public ItemNPCSettings viewSettings;
	
	public ContainerLandGrant(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);

		if(player.worldObj.isRemote){return;}
		ItemStack stack = player.getCurrentEquippedItem();
		if(stack==null || stack.getItem()==null || stack.getItem() != AWNpcItemLoader.scanner){
			throw new IllegalArgumentException("Cannot open Land Grant GUI for null or wrong stack/item.");
		}else {
			viewSettings = new ItemNPCSettings();
			ItemNPCSettings.getSettingsFor(stack, viewSettings, player.worldObj);
			if(viewSettings == null) {throw new IllegalArgumentException("Settings are null, fucker");}
			corvee = viewSettings.getCorvee();
			tithe = viewSettings.getTithe();
			rent = viewSettings.getRent();
		//	player.addChatComponentMessage(new ChatComponentText("Corvee: "+corvee));
		}
	}
	
	@Override
	public void handlePacketData(NBTTagCompound tag)
	  {
	  if(tag.hasKey("landGrant")){
		  ItemStack stack = player.getCurrentEquippedItem();
			if(stack==null || stack.getItem()==null || stack.getItem() != AWNpcItemLoader.scanner){
				throw new IllegalArgumentException("Player not holding land grant!");
			}  
			//ItemNPCSettings viewSettings = new ItemNPCSettings();
			//ItemNPCSettings.getSettingsFor(stack, viewSettings, player.worldObj);
			viewSettings.setCorvee(tag.getFloat("Corvee"));
			viewSettings.setTithe(tag.getFloat("Tithe"));
			viewSettings.setRent(tag.getFloat("Rent"));
			ItemNPCSettings.setSettingsFor(stack, viewSettings);
	//		player.addChatComponentMessage(new ChatComponentText("Corvee: "+viewSettings.getCorvee()));
	  }  
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer){  
	  super.onContainerClosed(par1EntityPlayer);
	}
	
	public void print(String s) {
		player.addChatComponentMessage(new ChatComponentText(s));
	}
}
