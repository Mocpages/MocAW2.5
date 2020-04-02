package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class ContainerNpcCreativeControls extends ContainerNpcBase
{

	public String ownerName;//allow for editing owner name for player-owned, no effect on faction-owned
	public boolean wander;//temp flag in all npcs
	public int maxHealth;
	public int attackDamage;//faction based only
	public int armorValue;//faction based only
	public String customTexRef;//might as well allow for player-owned as well...
	public InventoryBackpack b;

	boolean hasChanged;//if set to true, will set all flags to entity on container close

	public ContainerNpcCreativeControls(EntityPlayer player, int x, int y, int z)
	{
		super(player, x, y, z);
		ownerName = npc.getOwnerName();
		customTexRef = npc.getCustomTex();
		wander = npc.getIsAIEnabled();
		maxHealth = npc.getMaxHealthOverride();
		attackDamage = npc.getAttackDamageOverride();
		armorValue = npc.getArmorValueOverride();
		NpcPlayerOwned p = (NpcPlayerOwned)npc;
		b = p.invBack;
		System.out.println("WTF "  +b.getStackInSlot(1));
	}

	public void sendChangesToServer()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("ownerName", ownerName);
		tag.setString("customTex", customTexRef);
		tag.setBoolean("wander", wander);
		tag.setInteger("maxHealth", maxHealth);
		tag.setInteger("attackDamage", attackDamage);
		tag.setInteger("armorValue", armorValue);  
		sendDataToServer(tag);
	}

	@Override
	public void sendInitData()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("ownerName", ownerName);
		tag.setString("customTex", customTexRef);
		tag.setBoolean("wander", wander);
		tag.setInteger("maxHealth", maxHealth);
		tag.setInteger("attackDamage", attackDamage);
		tag.setInteger("armorValue", armorValue);  
		System.out.println("Sending data to client "+ b.getStackInSlot(1));
		tag.setTag("inv", b.writeToNBT(new NBTTagCompound()));
		sendDataToClient(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag)
	{
		System.out.println("Receiving data from server!");
		if(tag.hasKey("ownerName")){ownerName = tag.getString("ownerName");}
		if(tag.hasKey("wander")){wander = tag.getBoolean("wander");}
		if(tag.hasKey("attackDamage")){attackDamage=tag.getInteger("attackDamage");}
		if(tag.hasKey("armorValue")){armorValue=tag.getInteger("armorValue");}
		if(tag.hasKey("maxHealth")){maxHealth=tag.getInteger("maxHealth");}
		if(tag.hasKey("customTex")){customTexRef=tag.getString("customTex");}
		b = new InventoryBackpack(27);
		if(tag.hasKey("inv")) {
			InventoryTools.readInventoryFromNBT(b, tag.getCompoundTag("inv"));
			System.out.println("Doing a thing, fuckers " + b.getStackInSlot(1));
		}
		hasChanged = true;
		refreshGui();
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
	{
		if(hasChanged && !player.worldObj.isRemote)
		{
			hasChanged=false;
			npc.setOwnerName(ownerName);
			npc.setCustomTexRef(customTexRef);
			npc.setAttackDamageOverride(attackDamage);
			npc.setArmorValueOverride(armorValue);
			npc.setIsAIEnabled(wander);
			npc.setMaxHealthOverride(maxHealth);
		}
		super.onContainerClosed(par1EntityPlayer);
	}

	public void setSlots() {
		System.out.println("Doin the thing mocc r u proud of me");
		NpcPlayerOwned p = (NpcPlayerOwned)npc;
		//InventoryBackpack b = p.getInv();
		if(b==null) {return;}
		System.out.println("gui" + b.getStackInSlot(1));
		int xPos, yPos;
		for(int i = 0; i < b.getSizeInventory(); i++)
		{
			xPos = (i%9)*18+8;
			yPos = (i/9)*18+8;
			addSlotToContainer(new Slot(b, i, xPos, yPos));
		}
	}
}
