package net.shadowmage.ancientwarfare.npc.needs;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NeedStarch extends NeedBase{
	
	public NeedStarch(int a, NpcPlayerOwned n, int t) {
		super(a,n,t);
	}
	
	@Override
	public int getVal(Item i) {
		if(i == Items.bread) {
			return 3750;
		}
		return 0;
	}
	
	@Override
	public void update() {
		if(getAmount() == 0) {
			getNpc().setDead();
		}
	}
}
