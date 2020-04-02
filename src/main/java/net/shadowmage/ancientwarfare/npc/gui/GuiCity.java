package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.npc.container.ContainerCity;
import net.shadowmage.ancientwarfare.npc.tile.MarketPrice;

public class GuiCity  extends GuiContainerBase{

ContainerCity container;
public GuiCity(ContainerBase container){
  super(container);
  this.container = (ContainerCity)container;
  this.ySize = 3*18 + 4*18 + 8 + 8 + 4 + 8 + 16;
  this.xSize = 178;
  }

@Override
public void initElements()
  {
  this.container.addSlots();
  System.out.println("CITY GUI ITEMS:");
	for(MarketPrice p : this.container.prices) {
		System.out.println("ITEM: " + p.i.getUnlocalizedName() + " buy: " + p.buy + " sell: " + p.sell);
	}
  Button button = new Button(8, 8, 75, 12, "guistrings.npc.death_list")
    {
    @Override
    protected void onPressed()
      {
      container.removeSlots();
      Minecraft.getMinecraft().displayGuiScreen(new GuiCityMarket(GuiCity.this));
      }
    };
  button.setText("Market");
  addGuiElement(button);
  }

@Override
public void setupElements()
  {

  }


}
