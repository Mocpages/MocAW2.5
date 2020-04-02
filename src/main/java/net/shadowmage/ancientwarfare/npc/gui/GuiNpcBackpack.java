package net.shadowmage.ancientwarfare.npc.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class GuiNpcBackpack extends GuiContainerBase{
	GuiNpcCreativeControls parent;
	
	public GuiNpcBackpack(GuiNpcCreativeControls parent){
		super((ContainerBase)parent.inventorySlots);
		this.parent = parent;
		System.out.println("Setting slots");
		this.parent.container.setSlots();
	}
	
	@Override
	public void initElements(){
	}

	@Override
	public void setupElements(){
	}

}
