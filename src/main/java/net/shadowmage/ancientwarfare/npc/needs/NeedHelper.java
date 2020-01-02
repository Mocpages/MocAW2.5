package net.shadowmage.ancientwarfare.npc.needs;

import java.util.ArrayList;
import java.util.List;

import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NeedHelper {
	
	public static NeedBase starchNeed(NpcPlayerOwned n) {
		return new NeedStarch(10000, n, 5000);
	}
	
	public static NeedBase textilesNeed(NpcPlayerOwned n) {
		return new NeedTextiles(0, n, 0);
	}
	
	public static NeedBase fuelNeed(NpcPlayerOwned n) {
		return new NeedFuel(0, n, 0);
	}
	
	public static NeedBase vegNeed(NpcPlayerOwned n) {
		return new NeedVeg(0, n, 0);
	}
	
	public static NeedBase alcoholNeed(NpcPlayerOwned n) {
		return new NeedBase(0, n, 0);
	}
	
	public static NeedBase protienNeed(NpcPlayerOwned n) {
		return new NeedProtien(0, n, 0);
	}
	
	public static NeedBase cookwareNeed(NpcPlayerOwned n) {
		return new NeedBase(0, n, 0);
	}
	
	public static NeedBase spicesNeed(NpcPlayerOwned n) {
		return new NeedSpice(0, n, 0);
	}
	
	public static NeedBase booksNeed(NpcPlayerOwned n) {
		return new NeedBase(0, n, 0);
	}
	
	public static NeedBase jewelryNeed(NpcPlayerOwned n) {
		return new NeedBase(0, n, 0);
	}
}
