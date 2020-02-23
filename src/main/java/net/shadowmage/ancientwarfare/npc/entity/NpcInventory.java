package net.shadowmage.ancientwarfare.npc.entity;

import java.util.*;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class NpcInventory {
	Hashtable<Integer,Integer> items;
	
	public NpcInventory() {
		items = new Hashtable<Integer,Integer>();
	}
	
	public void addItem(int id, int qty) {
		if(items.containsKey(id)) {
			items.put(id, qty + items.get(id));
		}else {
			items.put(id,qty);
		}
		
	}
	
	public int getQty(int id) {
		return items.get(id);
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		Enumeration e = items.keys();
		NBTTagList i = new NBTTagList();
		while(e.hasMoreElements()) {
			int k = (Integer) e.nextElement();
			NBTTagCompound t = new NBTTagCompound();
			t.setInteger("Key", k);
			t.setInteger("Val",items.get(k).intValue());
			i.appendTag(new NBTTagCompound());
		}
		tag.setTag("Items", i);
		return tag;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList inv = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for(int i = 0;i < inv.tagCount(); i++) {
			NBTTagCompound t = inv.getCompoundTagAt(i);
			items.put(t.getInteger("Key"), t.getInteger("Val"));
		}
	}

	public void onDeath(World worldObj, double x,double y, double z) {
		Enumeration e = items.keys();
		while(e.hasMoreElements()) {
			int key = (Integer)e.nextElement();
			ItemStack s = new ItemStack(Item.getItemById(key));
			int numDropping = items.get(key);
			while(numDropping > 0) {
				s.stackSize = Math.min(numDropping, s.getMaxStackSize());
				numDropping -= s.stackSize;
				InventoryTools.dropItemInWorld(worldObj, s, x, y, z);
			}
		}
	}
}
