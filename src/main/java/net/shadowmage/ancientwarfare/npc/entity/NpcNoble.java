package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedNobleSell;
import net.shadowmage.ancientwarfare.npc.needs.NeedHelper;

public class NpcNoble extends NpcCombat{

	public NpcNoble(World par1World) {
		super(par1World);
		this.tasks.addTask(110, new NpcAIPlayerOwnedNobleSell(this));
	}

	@Override
	public void addNeeds() {
		needs.add(NeedHelper.swordNeed(this));
		needs.add(NeedHelper.armorNeed(this));
		needs.add(NeedHelper.breadNeed(this));
		needs.add(NeedHelper.textilesNeed(this));
		needs.add(NeedHelper.vegNeed(this));
		needs.add(NeedHelper.alcoholNeed(this));
		needs.add(NeedHelper.protienNeed(this));

	}
	
	@Override
	public String getNpcType() {
		return "noble";
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		age = 5040000;
	}
}
