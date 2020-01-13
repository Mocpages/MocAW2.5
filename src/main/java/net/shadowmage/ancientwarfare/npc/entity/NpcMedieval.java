package net.shadowmage.ancientwarfare.npc.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class NpcMedieval extends NpcPlayerOwned{

	
	public NpcMedieval(World par1World) {
		super(par1World);

	}

	@Override
	public boolean isValidOrdersStack(ItemStack stack) {
		return false;
	}

	@Override
	public void onOrdersInventoryChanged() {
		
	}

	@Override
	public String getNpcSubType() {
		return "peasant";
	}

	@Override
	public String getNpcType() {
		return "medieval";
	}

}
