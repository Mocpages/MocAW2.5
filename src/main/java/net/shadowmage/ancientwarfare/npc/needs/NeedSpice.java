package net.shadowmage.ancientwarfare.npc.needs;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NeedSpice extends NeedBase{
	
	public NeedSpice(int a, NpcPlayerOwned n, int t) {
		super(a,n,t);
	}
	
	@Override
	public int getVal(Item i) {
		if(i == Items.sugar) {
			return 5000;
		}
		return 0;
	}
}
