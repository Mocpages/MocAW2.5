/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.vehicle.helpers;

import net.minecraft.entity.player.EntityPlayer;
//import shadowmage.ancient_warfare.common.interfaces.INBTTaggable;
//import shadowmage.ancient_warfare.common.network.GUIHandler;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
//import shadowmage.ancient_warfare.common.tracker.PlayerTracker;
//import net.shadowmage.ancientwarfare.core.
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;


public abstract class VehicleFiringVarsHelper
{

protected VehicleBase vehicle;

public VehicleFiringVarsHelper(VehicleBase vehicle)
  {
  this.vehicle = vehicle;
  }

/**
 * called on every tick that the vehicle is 'firing' to update the firing animation and to call
 * launchMissile when animation has reached launch point
 */
public abstract void onFiringUpdate();

/**
 * called every tick after the vehicle has fired, until reload timer is complete, to update animations
 */
public abstract void onReloadUpdate();

/**
 * called every tick after startLaunching() is called, until setFinishedLaunching() is called...
 */
public abstract void onLaunchingUpdate();

public abstract void onReloadingFinished();

public void onTick(){}

public boolean interact(EntityPlayer player)
  {
  if(player.worldObj.isRemote)
    {
    return true;
    }
  
  if(!player.isSneaking() && vehicle.riddenByEntity==null)
    {
    player.mountEntity(vehicle);
    return true;
    }
  else{
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_VEHICLE_DEBUG, vehicle.getEntityId(), 0, 0);  
    }
  if(vehicle.riddenByEntity instanceof NpcBase)
    {
    NpcBase npc = (NpcBase)vehicle.riddenByEntity;
    npc.dismountVehicle();
    }
  return true;
  }

public abstract float getVar1();
public abstract float getVar2();
public abstract float getVar3();
public abstract float getVar4();
public abstract float getVar5();
public abstract float getVar6();
public abstract float getVar7();
public abstract float getVar8();


}
