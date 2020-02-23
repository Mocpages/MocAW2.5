package net.shadowmage.ancientwarfare.npc.item;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class ItemNPCSettings {
	private BlockPosition pos1;
	private BlockPosition pos2;
	private float corvee;
	private float rent;
	private float tithe;
	private NpcPlayerOwned owner;
	boolean[] setKeys = new boolean[4];

	
	public ItemNPCSettings(){
		pos1 = new BlockPosition();
		pos2 = new BlockPosition();
	}
	
	public static ItemNPCSettings getSettingsFor(ItemStack stack, ItemNPCSettings settings, World worldObj) {
		NBTTagCompound tag;
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("NPCData")){
			tag = stack.getTagCompound().getCompoundTag("NPCData");
		}else{
			tag = new NBTTagCompound();
		}
		
		for(int i = 0; i < 4; i++){
			settings.setKeys[i] = false;
	    }
		
		if(tag.hasKey("pos1")){
			settings.getPos1().read(tag.getCompoundTag("pos1"));
			settings.setKeys[0] = true;
		}
		
		if(tag.hasKey("pos2"))      {
			settings.getPos2().read(tag.getCompoundTag("pos2"));
			settings.setKeys[1] = true;
		}
		
		if(tag.hasKey("id")) {
			NBTTagCompound id = tag.getCompoundTag("id");
			settings.owner = (NpcPlayerOwned) WorldTools.getEntityByUUID(worldObj, id.getLong("idmsb"), id.getLong("idlsb"));
			//settings.owner.setCustomNameTag("TEST!");
			settings.setKeys[2] = true;
		}
		
		settings.setCorvee(tag.getFloat("Corvee"));
		settings.setTithe(tag.getFloat("Tithe"));
		settings.setRent(tag.getFloat("Rent"));
		return settings;
	}
	
	public static void setSettingsFor(ItemStack item, ItemNPCSettings settings) {
		NBTTagCompound tag = new NBTTagCompound();
		
		if(settings.setKeys[0]) {
			NBTTagCompound tag1 = new NBTTagCompound();
		    settings.getPos1().writeToNBT(tag1);
		    tag.setTag("pos1", tag1);
		}
		
		if(settings.setKeys[1]) {
			NBTTagCompound tag2 = new NBTTagCompound();
		    settings.getPos2().writeToNBT(tag2);
		    tag.setTag("pos2", tag2);
		}
		
		if(settings.setKeys[2]) {
			NBTTagCompound tag3 = new NBTTagCompound();
			UUID id = settings.owner.getPersistentID();
			tag3.setLong("idmsb", id.getMostSignificantBits());
		    tag3.setLong("idlsb", id.getLeastSignificantBits());
		    tag.setTag("id", tag3);
		}
		
		tag.setFloat("Corvee", settings.corvee);
		tag.setFloat("Rent", settings.rent);
		tag.setFloat("Tithe", settings.tithe);
		item.setTagInfo("NPCData", tag);
	}

	public void setPos1(BlockPosition p) {
		pos1 = p;
		if(setKeys[2]) {owner.setCustomNameTag("p1");}
		setKeys[0] = true;
	}
	
	public void setPos2(BlockPosition p) {
		pos2 = p;
		if(setKeys[2]) {owner.setCustomNameTag("p2");}
		setKeys[1] = true;
	}
	
	public void setOwner(NpcPlayerOwned p) {
		owner = p;
		//owner.setCustomNameTag("Owner Set!");
		setKeys[2] = true;
	}
	
	public NpcPlayerOwned getOwner() {
		return owner;
	}

	public boolean hasPos1() {
		return setKeys[0];
	}
	
	public boolean hasPos2() {
		return setKeys[1];
	}

	public void clearSettings() {
		for(int i = 0; i<3; i++){
			this.setKeys[i] = false;
	    }
	}

	public BlockPosition getPos1() {
		return pos1;
	}

	public BlockPosition getPos2() {
		return pos2;
	}

	public float getRent() {
		return rent;
	}

	public void setRent(float r) {
		this.rent = r;
	}

	public float getCorvee() {
		return corvee;
	}

	public void setCorvee(float c) {
		this.corvee = c;
	}

	public float getTithe() {
		return tithe;
	}

	public void setTithe(float t) {
		this.tithe = t;
	}
}
