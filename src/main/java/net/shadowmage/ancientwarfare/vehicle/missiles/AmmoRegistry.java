package net.shadowmage.ancientwarfare.vehicle.missiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
/*import shadowmage.ancient_warfare.common.config.Config;
import shadowmage.ancient_warfare.common.item.AWItemBase;
import shadowmage.ancient_warfare.common.item.ItemLoader;
import shadowmage.ancient_warfare.common.registry.entry.Description;
import shadowmage.ancient_warfare.common.vehicles.missiles.Ammo;
import shadowmage.ancient_warfare.common.vehicles.missiles.IAmmoType;
import shadowmage.ancient_warfare.common.vehicles.missiles.MissileBase;*/

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
//import shadowmage.ancient_warfare.common.crafting.RecipeType;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
//import shadowmage.ancient_warfare.common.crafting.ResourceListRecipe;
//import shadowmage.ancient_warfare.common.item.ItemLoader;
import net.shadowmage.ancientwarfare.core.item.AWCoreItemLoader;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
//import shadowmage.ancient_warfare.common.research.IResearchGoal;
//import shadowmage.ancient_warfare.common.research.ResearchGoal;
//import shadowmage.ancient_warfare.common.utils.ItemStackWrapperCrafting;
import net.shadowmage.ancientwarfare.vehicle.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItemLoader;
import net.shadowmage.ancientwarfare.vehicle.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.armors.IVehicleArmorType;
import net.shadowmage.ancientwarfare.vehicle.materials.IVehicleMaterial;
import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmoType;
import net.shadowmage.ancientwarfare.vehicle.missiles.VehicleAmmoEntry;
import net.shadowmage.ancientwarfare.vehicle.upgrades.IVehicleUpgradeType;
import net.shadowmage.ancientwarfare.vehicle.missiles.Ammo;
import net.shadowmage.ancientwarfare.vehicle.missiles.MissileBase;

public class AmmoRegistry
{


private AmmoRegistry(){}
private static AmmoRegistry INSTANCE;

private Map<Integer, IAmmoType> ammoInstances = new HashMap<Integer, IAmmoType>();

public static AmmoRegistry instance()
  {
  if(INSTANCE==null){INSTANCE = new AmmoRegistry();}
  return INSTANCE;
  }

public void registerAmmoTypes()
  {
  AWEntityRegistry.registerEntity(MissileBase.class, "entity.missile", 165, 5, true); 
  
  /**
   * debug..these will need to use the itemRegistry method..
   */
  for(Ammo ammo : Ammo.ammoTypes)
    {
    if(ammo!=null)
      {
     // ammo.setEnabled(Config.getConfig().get("f_ammo_config", ammo.getConfigName()+".enabled", ammo.isEnabled()).getBoolean(ammo.isEnabled()));
      ammo.setEnabled(true);
    if(ammo.isEnabled())
        {
      //  ammo.setEntityDamage(Config.getConfig().get("f_ammo_config", ammo.getConfigName()+".ent_damage", ammo.getEntityDamage()).getInt(ammo.getEntityDamage()));
      //  ammo.setVehicleDamage(Config.getConfig().get("f_ammo_config", ammo.getConfigName()+".veh_damage", ammo.getVehicleDamage()).getInt(ammo.getVehicleDamage()));
        this.registerAmmoTypeWithItem(ammo);      
        }
      }
    }
  }

public List<IAmmoType> getAmmoTypes()
  {
  List<IAmmoType> ammosList = new ArrayList<IAmmoType>();
  for(Integer key : this.ammoInstances.keySet())
    {
    IAmmoType t = this.ammoInstances.get(key);
    if(t!=null)
      {
      ammosList.add(t);
      }
    }
  return ammosList;
  } 

/**
 * used by structure gen to fill get ammo types to fill vehicles with
 * @param type
 * @return
 */
public IAmmoType getAmmoEntry(int type)
  {
  return this.ammoInstances.get(type);
  }

public void registerAmmoTypeWithItem(IAmmoType ammo)
  {
  ItemBase item = AWVehicleItemLoader.ammoItem; 
  List<String> tips = ammo.getDisplayTooltip();  
 // Description d = AWVehicleItemLoader.INSTANCE.addSubtypeInfoToItem(item, ammo.getAmmoType(), ammo.getDisplayName());
  for(String tip : tips)
    {
   // d.addTooltip(tip, ammo.getAmmoType());
    }
//  d.addTooltip("Weight: "+ammo.getAmmoWeight(), ammo.getAmmoType());
//  d.addTooltip("Entity Damage: "+ammo.getEntityDamage(), ammo.getAmmoType());
//  d.addTooltip("Vehicle Damage: "+ammo.getVehicleDamage(), ammo.getAmmoType());
//  if(ammo.isFlaming())
//    {
//    d.addTooltip("Flaming -- ignites targets when hit", ammo.getAmmoType());
//    }
//  if(ammo.isProximityAmmo())
//    {
//    d.addTooltip("Proximity -- detonates near targets", ammo.getAmmoType());
//    }
//  if(ammo.isPenetrating())
//    {
//    d.addTooltip("Penetrating -- does not stop on impact", ammo.getAmmoType());
//    }
//  if(ammo.getSecondaryAmmoType() != null && ammo.getSecondaryAmmoTypeCount()>0)
//    {
//    d.addTooltip("Cluster ammunition, spawns "+ammo.getSecondaryAmmoTypeCount()+" submunitions", ammo.getAmmoType());
//    IAmmoType t = ammo.getSecondaryAmmoType();
//    d.addTooltip("Submunition Entity Damage: "+t.getEntityDamage(), ammo.getAmmoType());
//    d.addTooltip("Submunition Vehicle Damage: "+t.getVehicleDamage(), ammo.getAmmoType());
//    }
//  d.addDisplayStack(ammo.getDisplayStack());
  //d.setIconTexture(ammo.getIconTexture(), ammo.getAmmoType());
  this.registerAmmoType(ammo);
  }

public void registerAmmoType(IAmmoType ammo)
  {
  if(ammo==null)
    {
    return;
    }
  int type = ammo.getAmmoType();
  if(!this.ammoInstances.containsKey(type))
    {
    this.ammoInstances.put(type, ammo);
    }
  else
    {
    //Config.logError("Attempt to register a duplicate ammo type for number: "+type);
    //Config.logError("Ammo attempting to being registered: "+ammo.getDisplayName());
    }  
  }

public IAmmoType getAmmoForStack(ItemStack stack)
  {
  if(stack==null || Item.getIdFromItem(stack.getItem()) != Item.getIdFromItem(AWVehicleItemLoader.ammoItem))
    {
    return null;
    }
  return this.ammoInstances.get(stack.getItemDamage());
  }

}