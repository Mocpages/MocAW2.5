package net.shadowmage.ancientwarfare.vehicle.missiles;

import net.shadowmage.ancientwarfare.vehicle.missiles.IAmmoType;

public class VehicleAmmoEntry
{

public IAmmoType baseAmmoType;
public int ammoCount;

public VehicleAmmoEntry(IAmmoType ammo)
  {
  this.baseAmmoType = ammo;
  }
}