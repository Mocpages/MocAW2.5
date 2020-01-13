package net.shadowmage.ancientwarfare.npc.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;

public class GuiLandGrant extends GuiContainerBase{

	public GuiLandGrant(ContainerBase par1Container) {
		super(par1Container);
	}

	@Override
	public void initElements() {
		  Label label = new Label(8, 8, "Corvee Labor (Days per Week):");
		  this.addGuiElement(label);
		  NumberInput input = new NumberInput(8+18+55+55+30, 8+10, 60, 1.0F, this);
		  this.addGuiElement(input);
	}

	@Override
	public void setupElements() {
		// TODO Auto-generated method stub
		
	}
}
