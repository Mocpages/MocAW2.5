package net.shadowmage.ancientwarfare.npc.needs;

import net.minecraft.item.Item;

public class NeedPair {
	private Item i;
	private int val;
	
	public NeedPair(Item it, int v) {
		i = it;
		val = v;
	}
	
	public Item getItem() {
		return i;
	}
	
	public int getVal() {
		return val;
	}
}
