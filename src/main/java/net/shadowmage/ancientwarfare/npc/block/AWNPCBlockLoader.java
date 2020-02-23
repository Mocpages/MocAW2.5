package net.shadowmage.ancientwarfare.npc.block;

import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.npc.tile.TileCity;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import cpw.mods.fml.common.registry.GameRegistry;

public class AWNPCBlockLoader
{

public static final BlockTownHall townHall = new BlockTownHall("town_hall");
public static final BlockCity city = new BlockCity("city");


public static void load()
  {
  GameRegistry.registerBlock(townHall, ItemBlockOwnedRotatable.class, "town_hall");
  GameRegistry.registerTileEntity(TileTownHall.class, "town_hall_tile");
  townHall.iconMap.setIcon(townHall, RelativeSide.TOP, "ancientwarfare:npc/town_hall_top");
  townHall.iconMap.setIcon(townHall, RelativeSide.BOTTOM, "ancientwarfare:npc/town_hall_bottom");
  townHall.iconMap.setIcon(townHall, RelativeSide.LEFT, "ancientwarfare:npc/town_hall_side");
  townHall.iconMap.setIcon(townHall, RelativeSide.RIGHT, "ancientwarfare:npc/town_hall_side");
  townHall.iconMap.setIcon(townHall, RelativeSide.FRONT, "ancientwarfare:npc/town_hall_side");
  townHall.iconMap.setIcon(townHall, RelativeSide.REAR, "ancientwarfare:npc/town_hall_side");
  
  GameRegistry.registerBlock(city, ItemBlockOwnedRotatable.class, "city");
  GameRegistry.registerTileEntity(TileCity.class, "city_tile");
  city.iconMap.setIcon(city, RelativeSide.TOP, "ancientwarfare:npc/town_hall_top");
  city.iconMap.setIcon(city, RelativeSide.BOTTOM, "ancientwarfare:npc/town_hall_bottom");
  city.iconMap.setIcon(city, RelativeSide.LEFT, "ancientwarfare:npc/town_hall_side");
  city.iconMap.setIcon(city, RelativeSide.RIGHT, "ancientwarfare:npc/town_hall_side");
  city.iconMap.setIcon(city, RelativeSide.FRONT, "ancientwarfare:npc/town_hall_side");
  city.iconMap.setIcon(city, RelativeSide.REAR, "ancientwarfare:npc/town_hall_side");
  }

}
