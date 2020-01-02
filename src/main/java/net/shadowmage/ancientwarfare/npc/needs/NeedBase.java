package net.shadowmage.ancientwarfare.npc.needs;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;


public class NeedBase implements INeed{
	private int amount;
	private NpcPlayerOwned npc;
//	private List<NeedPair> items = new ArrayList<NeedPair>();
	private int threshold = 0;

	public NeedBase(int a, NpcPlayerOwned n, int t) {
		amount = a;
		npc = n;
		//items = i;
		threshold = t;
	}
	
	@Override
	public int getAmount() {
		return amount;
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

}
