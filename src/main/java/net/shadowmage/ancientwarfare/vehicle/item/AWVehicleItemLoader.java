package net.shadowmage.ancientwarfare.vehicle.item;

import net.shadowmage.ancientwarfare.core.item.ItemBase;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AWVehicleItemLoader {
	
	public static final CreativeTabs npcTab = new CreativeTabs("tabs.npc"){    
		  @Override
		  @SideOnly(Side.CLIENT)
		  public Item getTabIconItem()
		    {
		    return null;
		    }
		  
		  @SuppressWarnings({ "rawtypes", "unchecked" })
		  @Override
		  public void displayAllReleventItems(List par1List)
		    {
		    super.displayAllReleventItems(par1List);   
		    Collections.sort(par1List, sorter);     
		    }
		  };

	public static final ItemBase ammoItem = null;
	
	private static final TabSorter sorter = new TabSorter();
	
	
	public static void load() {
		  GameRegistry.registerItem(ammoItem, "ammo_item");  

	}
	
	private static class TabSorter implements Comparator<ItemStack>
	{

	@Override
	public int compare(ItemStack arg0, ItemStack arg1)
	  {
	  Item i1 = arg0.getItem();
	  Item i2 = arg1.getItem();
	  int i1p = getItemPriority(i1);
	  int i2p = getItemPriority(i2);
	  if(i1p==i2p)
	    {
	    return arg0.getDisplayName().compareTo(arg1.getDisplayName());
	    }
	  else
	    {
	    return i1p < i2p ? -1 : 1;
	    }
	  }

	private int compareSpawnerStacks(ItemStack arg0, ItemStack arg1)
	  {
	  String s1 = arg0.getUnlocalizedName();
	  String s2 = arg1.getUnlocalizedName();
	  boolean f1 = s1.contains("bandit") || s1.contains("viking") || s1.contains("native") || s1.contains("desert") || s1.contains("pirate") || s1.contains("custom_1") || s1.contains("custom_2") || s1.contains("custom_3");
	  boolean f2 = s2.contains("bandit") || s2.contains("viking") || s2.contains("native") || s2.contains("desert") || s2.contains("pirate") || s2.contains("custom_1") || s2.contains("custom_2") || s2.contains("custom_3");
	  if(f1 && f2){return s1.compareTo(s2);}
	  else if(!f1 && !f2){return s1.compareTo(s2);}
	  else{return f1 ? 1 : -1;}
	  }

	private int getItemPriority(Item item)
	  {
	  return 0;
	  }
	}


}
