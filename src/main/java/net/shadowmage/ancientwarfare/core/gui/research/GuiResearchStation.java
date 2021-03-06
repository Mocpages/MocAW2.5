package net.shadowmage.ancientwarfare.core.gui.research;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.block.Direction;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerResearchStation;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.ProgressBar;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import org.lwjgl.input.Mouse;

public class GuiResearchStation extends GuiContainerBase
{

ContainerResearchStation container;
Label researcherLabel;
Label researchGoalLabel;
ProgressBar bar;
Checkbox useAdjacentInventory;
Button invDir, invSide;

ItemSlot[] layoutSlots = new ItemSlot[9];

public GuiResearchStation(ContainerBase par1Container)
  {
  super(par1Container, 178, 240, defaultBackground);
  container = (ContainerResearchStation) par1Container;
  container.setGui(this);
  }

@Override
public void initElements()
  {
  String name = container.researcherName ==null? "guistrings.research.no_researcher" : container.researcherName;
  researcherLabel = new Label(8, 8, name);  
  addGuiElement(researcherLabel);
  
  name = "guistrings.research.no_research"; 
  int goalNumber = container.currentGoal;
  if(goalNumber>=0)
    {
    ResearchGoal g = ResearchGoal.getGoal(goalNumber);
    if(g!=null)
      {
      name = g.getName();
      }
    }
  researchGoalLabel = new Label(80, 8+18*2, name);
  addGuiElement(researchGoalLabel);
  addGuiElement(new Label(8, 8+18*2, StatCollector.translateToLocal("guistrings.research.current_goal")+":"));
  
  bar = new ProgressBar(70, 8+18*2+12, 178-70-8, 12);
  addGuiElement(bar);
  
  Button button = new Button(178-8-140, 8+12+4, 140, 12, "guistrings.research.research_queue")
    {
    @Override
    protected void onPressed()
      {
      container.removeSlots();
      Minecraft.getMinecraft().displayGuiScreen(new GuiResearchStationSelection(GuiResearchStation.this, Mouse.getX(), Mouse.getY()));
      }
    };  
  addGuiElement(button);
  
  int x, y;
  for(int i = 0; i < 9; i++)
    {
    x = (i%3) * 18 + 98;
    y = (i/3) * 18 + 98;
    layoutSlots[i] = new ItemSlot(x, y, null, this);
    addGuiElement(layoutSlots[i]);
    }  
  addGuiElement(new Label(8, 8+18*2+12+1, "guistrings.research.progress"));
  addGuiElement(new Label(8+18, 8+3*18+10+4+10, "guistrings.research.input"));
  addGuiElement(new Label(8+5*18, 8+3*18+10+4+10, "guistrings.research.needed"));
  
  useAdjacentInventory = new Checkbox(8, 8+3*18+6, 16, 16, "guistrings.research.use_adjacent_inventory")
    {
    @Override
    public void onToggled()
      {
      container.toggleUseAdjacentInventory();
      setChecked(container.useAdjacentInventory);
      }
    };
  addGuiElement(useAdjacentInventory);
  
  invDir = button = new Button(80, 8+3*18+6, 40, 16, StatCollector.translateToLocal(Direction.getDirectionFor(container.tile.inventoryDirection.ordinal()).getTranslationKey()))
    {
    @Override
    protected void onPressed()
      {
      int o = container.tile.inventoryDirection.ordinal();
      o++;
      if(o>6){o=0;}
      NBTTagCompound tag = new NBTTagCompound();
      tag.setInteger("inventoryDirection", o);
      sendDataToContainer(tag);
      container.tile.inventoryDirection = ForgeDirection.getOrientation(o);
      refreshGui();
      }
    };
  addGuiElement(button);
  invSide = button = new Button(120, 8+3*18+6, 40, 16, StatCollector.translateToLocal(Direction.getDirectionFor(container.tile.inventorySide.ordinal()).getTranslationKey()))
    {
    @Override
    protected void onPressed()
      {
      int o = container.tile.inventorySide.ordinal();
      o++;
      if(o>6){o=0;}
      NBTTagCompound tag = new NBTTagCompound();
      tag.setInteger("inventoryDirection", o);
      sendDataToContainer(tag);
      container.tile.inventorySide = ForgeDirection.getOrientation(o);
      refreshGui();
      }
    };
  addGuiElement(button);  
  }

@Override
public void setupElements()
  {
  String name = container.researcherName ==null? "guistrings.research.no_researcher" : container.researcherName;
  researcherLabel.setText(name);
  
  if(container.researcherName==null)
    {
    for(int i = 0; i<9;i++)
      {
      layoutSlots[i].setItem(null);
      }
    }
  
  name = "guistrings.research.no_research"; 
  int goalNumber = container.currentGoal;
  float progress = 0.f;
  if(goalNumber>=0)
    {
    ResearchGoal g = ResearchGoal.getGoal(goalNumber);
    if(g!=null)
      {
      name = g.getName();
      
      float total = g.getTotalResearchTime();      
      float time = container.progress;
      if(total==0){total = time;}
      progress = time / total; 
      }
    for(int i = 0; i<9;i++)
      {
      layoutSlots[i].setItem(null);
      }
    }
  else
    {
    List<Integer> queue = container.queuedResearch;
    if(!queue.isEmpty())
      {
      int g1 = queue.get(0);
      ResearchGoal g = ResearchGoal.getGoal(g1);
      if(g!=null)
        {
        name = g.getName();
        ItemStack resource;
        for(int i = 0; i<9;i++)
          {
          if(i>=g.getResources().size())
            {
            layoutSlots[i].setItem(null);
            }
          else
            {
            resource = g.getResources().get(i);
            if(!InventoryTools.doItemStacksMatch(resource, layoutSlots[i].getStack()))
              {
              layoutSlots[i].setItem(resource.copy());
              }          
            }
          }
        }
      }
    else
      {
      for(int i = 0; i<9;i++)
        {
        layoutSlots[i].setItem(null);
        }
      }
    }
  bar.setProgress(progress);
  researchGoalLabel.setText(name);
  
  useAdjacentInventory.setChecked(container.useAdjacentInventory);
  invDir.setText(StatCollector.translateToLocal(Direction.getDirectionFor(container.tile.inventoryDirection.ordinal()).getTranslationKey()));
  invSide.setText(StatCollector.translateToLocal(Direction.getDirectionFor(container.tile.inventorySide.ordinal()).getTranslationKey()));
  }


}
