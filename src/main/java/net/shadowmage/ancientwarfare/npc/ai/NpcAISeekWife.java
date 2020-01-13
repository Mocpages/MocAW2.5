package net.shadowmage.ancientwarfare.npc.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.npc.ai.owned.SortForMarriage;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NpcAISeekWife extends NpcAI {
	NpcPlayerOwned npc;
	int waitTime;
	
	public NpcAISeekWife(NpcPlayerOwned npc) {
		super(npc);
		this.npc = npc;
		waitTime = 0;
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return (npc.getSpouse()==null && npc.age >= npc.MATURITY_AGE);
	}
	
	@Override
	public void updateTask() {
		if(npc.isMale) {
			seekWaifu();
		}else{
			seekHusbando();
		}
	}
	
	private void seekHusbando() {
		npc.setCustomNameTag("Female");
		waitTime++;
		if(waitTime >= 60) {
			List<NpcPlayerOwned> suitors = npc.getSuitors();
			if(suitors.isEmpty()) {return;}
			Collections.sort(suitors, new SortForMarriage());
			int i = 0;
			for(NpcPlayerOwned suitor : suitors) {
				if(i>=10) {return;}
				suitor.addSuitor(npc);
			}
		}
	}

	public void seekWaifu() {
		npc.setCustomNameTag("Male");
		waitTime++;
		if(waitTime <= 200) {
			int range = 255;
			AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(npc.posX-range, npc.posY-range/2, npc.posZ-range, npc.posX+range+1, npc.posY+range/2+1, npc.posZ+range+1);
			List<NpcPlayerOwned> npcs = npc.worldObj.getEntitiesWithinAABB(NpcPlayerOwned.class, bb);
			ArrayList<NpcPlayerOwned> candidates = new ArrayList<NpcPlayerOwned>();
			for(NpcPlayerOwned n : npcs) {
				if(!n.isMale && n.getSpouse() == null && !n.isChild) {
					n.addSuitor(npc);
				}
			}
		}else {
			npc.setCustomNameTag("Groom");
			List<NpcPlayerOwned> suitors = npc.getSuitors();
			Collections.sort(suitors, new SortForMarriage());
			if(!suitors.isEmpty()) {
				npc.setSpouse(suitors.get(0));
				npc.getSpouse().setCustomNameTag("Bride");
				npc.getSpouse().setSpouse(npc);
			}
		}
	}
}
