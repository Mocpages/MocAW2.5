package net.shadowmage.ancientwarfare.npc.needs;

import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public interface INeed {
	
	public int getAmount();
	
	public void setAmount(int amt);
	
	public NpcPlayerOwned getNpc();

	public void setNpc(NpcPlayerOwned n);
		
	public int getVal(Item item);
		
	public int getThreshold();
	
	public void setThreshold(int t);
	
	public boolean shouldIdle();
	
	public void update();
}
