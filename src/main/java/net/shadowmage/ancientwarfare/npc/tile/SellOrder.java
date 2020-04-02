package net.shadowmage.ancientwarfare.npc.tile;

import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class SellOrder {
	private ItemStack toSell;
	private int pricePer;
	private NpcPlayerOwned owner;
	private long idmsb;
	private long idlsb;
	private int amt;
	
	public SellOrder(ItemStack s, int p, NpcPlayerOwned o, int a) {
		toSell = s;
		pricePer = p;
		owner = o;
		amt = a;
		System.out.println("CREATING SELL ORDER " + owner.getUniqueID());
	}
	
	public SellOrder(NBTTagCompound tag) {
		readFromNBT(tag);
	}
	
	public void updateOwner(World worldObj) {
		owner = (NpcPlayerOwned) WorldTools.getEntityByUUID(worldObj, idmsb, idlsb);
	}

	
	public Item getItem() {
		return toSell.getItem();
	}
	
	public int getPrice() {
		return pricePer;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		toSell = InventoryTools.readItemStack(tag.getCompoundTag("SellStack"));
		idmsb = tag.getLong("idmsb");
		idlsb = tag.getLong("idlsb");
		pricePer = tag.getInteger("price");
		amt = tag.getInteger("amt");
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("SellStack", InventoryTools.writeItemStack(toSell, new NBTTagCompound()));
		UUID id = owner.getUniqueID();
		tag.setLong("idmsb", id.getMostSignificantBits());
		tag.setLong("idlsb", id.getLeastSignificantBits());
		tag.setInteger("price", pricePer);
		tag.setInteger("amt", amt);
		return tag;
	}

	public NpcPlayerOwned getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}
	
	public void setPrice(int p) {
		pricePer = p;
	}
	
	public void setAmt(int q) {
		amt = q;
	}
	
	public int getAmt() {
		return amt;
	}
}
