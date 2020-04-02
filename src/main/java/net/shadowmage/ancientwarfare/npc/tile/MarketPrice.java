package net.shadowmage.ancientwarfare.npc.tile;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class MarketPrice {
	public Item i;
	public float buy;
	public float sell;
	
	public MarketPrice(Item im,float b, float s) {
		i = im;
		buy = b;
		sell = s;
	}
	
	public MarketPrice(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	public NBTBase writeToNBT(NBTTagCompound tag) {
		tag.setFloat("buy", buy);
		tag.setFloat("sell", sell);
		tag.setInteger("id", Item.getIdFromItem(i));
		return tag;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		buy = tag.getFloat("buy");
		sell = tag.getFloat("sell");
		i = Item.getItemById(tag.getInteger("id"));
	}
}
