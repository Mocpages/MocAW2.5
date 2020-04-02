package net.shadowmage.ancientwarfare.npc.needs;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.tile.BuyOrder;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class NeedBase implements INeed{
	protected int amount;
	private NpcPlayerOwned npc;
	private List<NeedPair> items = new ArrayList<NeedPair>();
	private Dictionary needs;
	private int threshold = 0;

	public NeedBase(int a, NpcPlayerOwned n, int t, Dictionary ne) {
		amount = a;
		npc = n;
		//items = i;
		threshold = t;
		needs = ne;
	}
	
	public NeedBase(NBTTagCompound tag) {
		needs = new Hashtable();
		readFromNBT(tag);
	}
	
	@Override
	public int getAmount() {
		return amount;
	}
	
	public Dictionary getNeeds() {
		return needs;
	}

	public void addNeed(int id, int amt) {
		needs.put(id, amt);
	}
	
	public int getNeed(int id) {
		return (Integer) needs.get(id);
	}
	
	public void remNeed(int id) {
		needs.remove(id);
	}
	
	@Override
	public void setAmount(int amt) {
		amount = amt;
	}

	@Override
	public NpcPlayerOwned getNpc() {
		return npc;
	}

	@Override
	public void setNpc(NpcPlayerOwned n) {
		npc = n;
		
	}


	@Override
	public int getVal(Item item) {
		return 0;
	}


	@Override
	public boolean shouldIdle() {
		return amount <= threshold;
	}
	
	@Override
	public int getThreshold() {
		return threshold;
	}

	@Override
	public void setThreshold(int t) {
		threshold = t;
	}
	@Override
	public void update() {
		amount --;
	}

	@Override
	public NBTBase writeToNBT(NBTTagCompound tag) {
		tag.setInteger("thresh", threshold);
		tag.setInteger("amt", amount);
		
		NBTTagList needList = new NBTTagList();
		for(Enumeration k = needs.keys(); k.hasMoreElements();) {
			int key = (Integer) k.nextElement();
			int val = (Integer) needs.get(key);
			NBTTagCompound need = new NBTTagCompound();
			need.setInteger("key", key);
			need.setInteger("val",val);
			needList.appendTag(need);
		}
		tag.setTag("needs", needList);
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		threshold = tag.getInteger("thresh");
		amount = tag.getInteger("amt");

		NBTTagList needList = tag.getTagList("needs", Constants.NBT.TAG_COMPOUND);

		for(int i = 0;i < needList.tagCount(); i++) {
			NBTTagCompound tg = needList.getCompoundTagAt(i);
			int key = tg.getInteger("key");
			int val = tg.getInteger("val");
			needs.put(key, val);
		}
	}
}
