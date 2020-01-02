package net.shadowmage.ancientwarfare.npc.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class NpcMedieval extends NpcPlayerOwned{
	public boolean isMale;
	public boolean isChild;
	int age;
	NpcMedieval spouse;
	List<NpcMedieval> children;
	
	public NpcMedieval(World par1World) {
		super(par1World);
		age = 0;
		isChild = true;
		isMale = Math.random() >= 0.5;
		children = new ArrayList<NpcMedieval>();
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
		return null;
	}

	@Override
	public String getNpcType() {
		return null;
	}

}
