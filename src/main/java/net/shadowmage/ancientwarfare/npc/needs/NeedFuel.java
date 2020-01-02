package net.shadowmage.ancientwarfare.npc.needs;

import java.util.List;

import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NeedFuel extends NeedBase{
	
	public NeedFuel(int a, NpcPlayerOwned n, int t) {
		super(a,n,t);
	}
	
	@Override
	public int getVal(Item i) {
		if(Item.getIdFromItem(i)==17) {
			return 5000;
		}
		return 0;
	}
}
