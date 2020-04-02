package net.shadowmage.ancientwarfare.npc.gui;


import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.npc.tile.MarketPrice;

public class GuiCityMarket extends GuiContainerBase {
	GuiCity parent;
	CompositeScrolled area;

	public GuiCityMarket(GuiCity parent){
		super((ContainerBase)parent.inventorySlots);
		this.parent = parent;
	}
	
	@Override
	public void initElements() {
		  area = new CompositeScrolled(this, 0, 40, xSize, ySize-40);
		  addGuiElement(area);
	}
	
	@Override
	public void setupElements() {
		area.clearElements();
		List<MarketPrice> prices = parent.container.getPrices();
		
		//System.out.println("GUI ITEMS:");
		for(MarketPrice p : prices) {
			//System.out.println("ITEM: " + p.i.getUnlocalizedName() + " buy: " + p.buy + " sell: " + p.sell);
		}
		
		Label label;
		int height = 0;
		label = new Label(32, height, "# Items" + prices.size());
		for(MarketPrice price : prices) {
			height += 8;
			ItemSlot slot = new ItemSlot(6,height,new ItemStack(price.i),this);
			area.addGuiElement(slot);
			label = new Label(32, height, "Buy Price: " + price.buy / 100);
			area.addGuiElement(label);
		    height+=12;
		    
		    label = new Label(32, height, "Sale Price: " + price.sell / 100);
			area.addGuiElement(label);
		    height+=12;
		    
		    area.addGuiElement(new Line(0, height-1, xSize, height-1, 1, 0x000000ff));
		}
		area.setAreaSize(height);
	}

	@Override
	protected boolean onGuiCloseRequested()
	  {
	  Minecraft.getMinecraft().displayGuiScreen(parent);
	  parent.container.addSlots();
	  parent.refreshGui();
	  return false;
	  }

}
