package net.shadowmage.ancientwarfare.npc.needs;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.gui.GuiCity;
import net.shadowmage.ancientwarfare.npc.gui.GuiCityMarket;

public class NeedHelper {

	public static NeedBase starchNeed(NpcPlayerOwned n) { //get dis bred
		Dictionary d = new Hashtable();
		d.put(296, 1000);
		NeedBase starchN = new NeedBase(1000, null, 1000, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return starchN;
	}
	
	public static NeedBase armorNeed(NpcPlayerOwned n) { //get dis bred
		Dictionary d = new Hashtable();
		d.put(4946, 1000);
		NeedBase starchN = new NeedBase(1000, null, 1000, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return starchN;
	}
	
	public static NeedBase swordNeed(NpcPlayerOwned n) { //get dis bred
		Dictionary d = new Hashtable();
		d.put(4901, 1000);
		NeedBase starchN = new NeedBase(1000, null, 1000, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return starchN;
	}
	
	public static NeedBase breadNeed(NpcPlayerOwned n) { //get dis bred
		Dictionary d = new Hashtable();
		d.put(297, 1000);
		NeedBase starchN = new NeedBase(1000, null, 1000, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return starchN;
	}

	public static NeedBase textilesNeed(NpcPlayerOwned n) { //clothes
		Dictionary d = new Hashtable();
		d.put(4941, 100);
		NeedBase texN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() { //TODO REDO TO REFLECT REAL DURABILITY
				amount --;
			}
		};
		return texN;
	}

	public static NeedBase leatherNeed(NpcPlayerOwned n) { //shoes
		Dictionary d = new Hashtable();
		d.put(5316,10000); 
		NeedBase leathN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() { //TODO REAL DURA
				amount --;
			}
		};
		return leathN;
	}

	public static NeedBase fuelNeed(NpcPlayerOwned n) { 
		Dictionary d = new Hashtable();
		d.put(263, 1000);
		NeedBase fuelN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() { //TODO WINTER ONLY
				amount --;
			}
		};
		return fuelN;
	}

	public static NeedBase vegNeed(NpcPlayerOwned n) {
		Dictionary d = new Hashtable();
		d.put(391, 1000);
		NeedBase vegN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return vegN;
	}

	public static NeedBase alcoholNeed(NpcPlayerOwned n) {
		Dictionary d = new Hashtable();
		d.put(5004, 1000);
		NeedBase alcN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return alcN;
	}

	public static NeedBase protienNeed(NpcPlayerOwned n) {
		Dictionary d = new Hashtable();
		d.put(366, 1000);
		NeedBase protN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return protN;
	}

	public static NeedBase cookwareNeed(NpcPlayerOwned n) { //pottery etc
		Dictionary d = new Hashtable();
		NeedBase cookN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return cookN;
	}

	public static NeedBase spicesNeed(NpcPlayerOwned n) {
		Dictionary d = new Hashtable();
		NeedBase spiceN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return spiceN;
	}

	public static NeedBase booksNeed(NpcPlayerOwned n) {
		Dictionary d = new Hashtable();
		NeedBase bookN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() {
				amount --;
			}
		};
		return bookN;
	}

	public static NeedBase jewelryNeed(NpcPlayerOwned n) {
		Dictionary d = new Hashtable();
		NeedBase jewelN = new NeedBase(0, null, 0, d) {
			@Override
			public void update() {
				this.amount --;
			}
		};
		return jewelN;
	}
}
