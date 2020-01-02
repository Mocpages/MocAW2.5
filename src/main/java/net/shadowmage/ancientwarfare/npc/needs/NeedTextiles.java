package net.shadowmage.ancientwarfare.npc.needs;

import java.util.List;

import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NeedTextiles extends NeedBase{
	
	public NeedTextiles(int a, NpcPlayerOwned n, int t) {
		super(a,n,t);
	}
	
	@Override
	public int getVal(Item i) {
		if(Item.getIdFromItem(i)==35) {
			return 5000;
		}
		return 0;
	}
}
