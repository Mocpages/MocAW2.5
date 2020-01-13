package net.shadowmage.ancientwarfare.npc.ai.owned;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class SortForMarriage implements Comparator<NpcPlayerOwned>{

	@Override
	public int compare(NpcPlayerOwned p0, NpcPlayerOwned p1) {
		return p0.getLandArea() - p1.getLandArea();
	}

}
