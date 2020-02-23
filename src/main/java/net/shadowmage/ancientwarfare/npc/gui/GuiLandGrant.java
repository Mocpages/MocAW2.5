package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.npc.container.ContainerLandGrant;
import net.shadowmage.ancientwarfare.structure.gui.GuiDimensionSelection;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScanner;

public class GuiLandGrant extends GuiContainerBase{
	NumberInput c_input;
	NumberInput r_input;
	NumberInput t_input;
	ContainerLandGrant container;
	
	public GuiLandGrant(ContainerBase par1Container) {
		super(par1Container);
		container = (ContainerLandGrant) par1Container;
		if(container==null) {throw new IllegalArgumentException("Fuck you moc");}
	}

	@Override
	public void initElements() {
	//	container.print(Float.toString(container.viewSettings.getCorvee())); //
		  Label corvee = new Label(8, 8, "Corvee (Days/Week):");
		  c_input = new NumberInput(8+18+55+55+30+4, 8, 60, container.corvee, this){
		      @Override
		      public void onValueUpdated(float value)
		        {
		        container.corvee = value;
		        }
		      };
		  
		  Label rent = new Label(8, 8+15, "Rent (Gold/Year):");
		  r_input = new NumberInput(8+18+55+55+30+4, 8+15, 60, container.rent, this){
		      @Override
		      public void onValueUpdated(float value)
		        {
		        container.rent = value;
		        }
		      };
		  
		  Label tithe = new Label(8, 8+30, "Tithe (% Produce):");
		  t_input = new NumberInput(8+18+55+55+30+4, 8+30, 60, container.tithe, this){
		      @Override
		      public void onValueUpdated(float value)
		        {
		        container.tithe = value;
		        }
		      };
		  
		  //c_input.setValue(c);
		  //r_input.setValue(r);
		  //t_input.setValue(t);
		  this.addGuiElement(corvee);
		  this.addGuiElement(c_input);
		  this.addGuiElement(rent);
		  this.addGuiElement(r_input);
		  this.addGuiElement(tithe);
		  this.addGuiElement(t_input);
	}

	@Override
	public void setupElements() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean onGuiCloseRequested() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("Corvee", c_input.getFloatValue());
		tag.setFloat("Rent", r_input.getFloatValue());
		tag.setFloat("Tithe", t_input.getFloatValue());
		tag.setBoolean("landGrant", true);
		this.sendDataToContainer(tag);
		return true;
	}
}
