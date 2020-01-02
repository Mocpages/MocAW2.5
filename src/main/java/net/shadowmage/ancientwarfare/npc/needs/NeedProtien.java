package net.shadowmage.ancientwarfare.npc.needs;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NeedProtien extends NeedBase{
	
	public NeedProtien(int a, NpcPlayerOwned n, int t) {
		super(a,n,t);
	}
	@Override
	public int getVal(Item i) {
		if(i == Items.cooked_beef) {
			return 5000;
		}
		return 0;
	}
}
