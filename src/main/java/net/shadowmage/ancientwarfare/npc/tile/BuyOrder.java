package net.shadowmage.ancientwarfare.npc.tile;

import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class BuyOrder {
	private ItemStack toBuy;
	private int pricePer;
	private int amt;
	private NpcPlayerOwned owner;
	private long idmsb;
	private long idlsb;
	
	public BuyOrder(ItemStack s, int p, NpcPlayerOwned o, int a) {
		toBuy = s;
		pricePer = p;
		owner = o;
		amt = a;
	}
	
	public BuyOrder(NBTTagCompound tag) {
		readFromNBT(tag);
	}
	
	public Item getItem() {
		return toBuy.getItem();
	}
	
	public int getPrice() {
		return pricePer;
	}
	
	
	public void updateOwner(World worldObj) {
		owner = (NpcPlayerOwned) WorldTools.getEntityByUUID(worldObj, idmsb, idlsb);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		toBuy = InventoryTools.readItemStack(tag.getCompoundTag("BuyStack"));
		idmsb = tag.getLong("idmsb");
		idlsb = tag.getLong("idlsb");
		pricePer = tag.getInteger("price");
		amt = tag.getInteger("amt");
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
	
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		if(owner != null) {
			tag.setTag("BuyStack", InventoryTools.writeItemStack(toBuy, new NBTTagCompound()));
			UUID id = owner.getUniqueID();
			tag.setLong("idmsb", id.getMostSignificantBits());
			tag.setLong("idlsb", id.getLeastSignificantBits());
			tag.setInteger("price", pricePer);
			tag.setInteger("amt", amt);
		}
		return tag;
	}

	public NpcPlayerOwned getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}

	public ItemStack getItemStack() {
		// TODO Auto-generated method stub
		return toBuy;
	}
}
