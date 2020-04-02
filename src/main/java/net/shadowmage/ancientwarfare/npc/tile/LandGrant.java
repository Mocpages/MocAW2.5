package net.shadowmage.ancientwarfare.npc.tile;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;

public class LandGrant{
	private BlockPosition pos1, pos2;
	private float corvee,rent,tithe;
    private AxisAlignedBB aa;
    private NpcPlayerOwned owner;
    private TileTownHall townHall;
    private World worldObj;
    public ArrayList<BlockPosition> blocksToTill, blocksToHarvest, blocksToPlant, blocksToWork;
    private int work;
    private int owner_id;
    long idmsb;
    long idlsb;
    
	LandGrant(BlockPosition p1, BlockPosition p2, float c, float r, float t2, NpcPlayerOwned o, TileTownHall t){
		  blocksToTill = new ArrayList<BlockPosition>();
		  blocksToHarvest = new ArrayList<BlockPosition>();
		  blocksToPlant = new ArrayList<BlockPosition>();
		  blocksToWork = new ArrayList<BlockPosition>();
		pos1 = p1;
		pos2 = p2;
		corvee = c;
		rent = r;
		tithe = t2;
		aa = AxisAlignedBB.getBoundingBox(pos1.x, 0, pos1.z, pos2.x, 255, pos2.z);
		townHall = t;
		setOwner(o);
		worldObj = t.getWorldObj();
		work = 0;
	}
	
	public LandGrant(NBTTagCompound compoundTagAt, TileTownHall t) {
		  blocksToTill = new ArrayList<BlockPosition>();
		  blocksToHarvest = new ArrayList<BlockPosition>();
		  blocksToPlant = new ArrayList<BlockPosition>();
		  blocksToWork = new ArrayList<BlockPosition>();
		this.readFromNBT(compoundTagAt, t);
	}
	
	public NBTBase writeToNBT(NBTTagCompound tag) {
		tag.setTag("pos1", pos1.writeToNBT(new NBTTagCompound()));
		tag.setTag("pos2", pos2.writeToNBT(new NBTTagCompound()));
		tag.setFloat("corvee", corvee);
		tag.setFloat("tithe", tithe);
		tag.setFloat("rent", rent);
		UUID id = getOwner().getUniqueID();
		tag.setLong("idmsb", id.getMostSignificantBits());
		tag.setLong("idlsb", id.getLeastSignificantBits());
		tag.setInteger("tx", townHall.xCoord);
		tag.setInteger("ty", townHall.yCoord);
		tag.setInteger("tz", townHall.zCoord);
		tag.setInteger("id", owner.getEntityId());
		return tag;
	}

	private void readFromNBT(NBTTagCompound tag, TileTownHall t) {
		corvee = tag.getFloat("corvee");
		tithe = tag.getFloat("tithe");
		rent = tag.getFloat("rent");
		pos1 = new BlockPosition(tag.getCompoundTag("pos1"));
		pos2 = new BlockPosition(tag.getCompoundTag("pos2"));
		aa = AxisAlignedBB.getBoundingBox(pos1.x, 0, pos1.z, pos2.x, 255, pos2.z);
		idmsb = tag.getLong("idmsb");
		idlsb = tag.getLong("idlsb");
		townHall = t;
		worldObj = MinecraftServer.getServer().getEntityWorld();
		//owner_id = tag.getInteger("id");
		//owner = (NpcPlayerOwned) worldObj.getEntityByID(tag.getInteger("id"));
		//if(worldObj == null) {throw new IllegalArgumentException("FUCK OFF");}
		//setOwner((NpcPlayerOwned) WorldTools.getEntityByUUID(worldObj, idmsb, idlsb));
		//owner.setCustomNameTag("Loaded!");
	}
	
	public void updateOwner() {
		//owner = (NpcPlayerOwned) worldObj.getEntityByID(owner_id);
		setOwner((NpcPlayerOwned) WorldTools.getEntityByUUID(worldObj, idmsb, idlsb));
	}

	public float getCorvee() { return corvee;}
	
	public AxisAlignedBB getBoundingBox() {return aa;}
	
	public boolean collidesWith(LandGrant g) {
		if(g.getOwner() == getOwner()) {return false;}
		return aa.intersectsWith(g.getBoundingBox());
	}

	public NpcPlayerOwned getOwner() {
		return owner;
	}

	public void setOwner(NpcPlayerOwned owner) {
		this.owner = owner;
	}
	
	public void scanAll() {
		//owner.setCustomNameTag("Scanning!");
		BlockPosition min = BlockTools.getMin(pos1, pos2);
		BlockPosition max = BlockTools.getMax(pos1, pos2);
		BlockPosition scanner;
		for(int x = Math.min(pos1.x, pos2.x); x <= Math.max(pos1.x, pos2.x); x++) {
			for(int z = Math.min(pos1.z, pos2.z); z <= Math.max(pos1.z, pos2.z); z++) {
				scanner = new BlockPosition(x,100,z);
				scanBlockPosition(scanner);
			}
		}
	}
	
	public int getArea() {
		int l = Math.max(pos1.x, pos2.x) - Math.min(pos1.x, pos2.x);
		int w = Math.max(pos1.z, pos2.z) - Math.min(pos1.z, pos2.z);
		return l*w;
	}
	
	public void scanBlockPosition(BlockPosition p) {
		int y = worldObj.getTopSolidOrLiquidBlock(p.x, p.z);
		BlockPosition position = new BlockPosition(p.x, y, p.z);
		//owner.setCustomNameTag("x:"+position.x +" y:"+position.y + " z:"+position.z);
		Block block = worldObj.getBlock(position.x, y-1, position.z);
		Block c = worldObj.getBlock(position.x, y, position.z);
		if (c==Blocks.wheat || c==Blocks.carrots || c==Blocks.potatoes) {
			if(worldObj.getBlockMetadata(position.x, y, position.z)>=7){
				blocksToHarvest.add(position);
				return;
			}
		}
		if(block==Blocks.dirt || block==Blocks.grass){
			blocksToTill.add(new BlockPosition(position.x, position.y-1, position.z));
		}
		if(block==Blocks.farmland && c != Blocks.wheat){ //TODO generalize
			blocksToPlant.add(position);
			
		}else{
			blocksToWork.add(position);
		}
	}
	
	public Block getCrop() {
		return Blocks.wheat;
	}
	
	public void addWork() {
		work++;
	}
	
	public int getWork() {
		return work;
	}
	
	public float getTithe() {
		return tithe;
	}
	
	public float getRent() {
		return rent;
	}
	
	private BlockPosition getFirstDirt(ArrayList<BlockPosition> a) {
		Block out;
		for(BlockPosition b : a) {
			out = worldObj.getBlock(b.x, b.y, b.z);
			if(out == Blocks.dirt || out == Blocks.grass) { return b;}
		}
		return null;
	}
	
	private BlockPosition getFirstSoil(ArrayList<BlockPosition> a) {
		Block out;
		for(BlockPosition b : a) {
			out = worldObj.getBlock(b.x, b.y, b.z);
			if(out == Blocks.farmland) { return b;}
		}
		return null;
	}
	
	public void processWork(NpcWorker w) {
		if(!blocksToTill.isEmpty()) {
			BlockPosition b = getFirstDirt(blocksToTill);
			worldObj.setBlock(b.x, b.y, b.z, Blocks.farmland);
		}else if(!blocksToPlant.isEmpty() && w.canPlant()) {
			BlockPosition b = getFirstSoil(blocksToTill);
			//Block crop = ;
		}else if(!blocksToHarvest.isEmpty()) {
			
		}else if(!blocksToWork.isEmpty()) {
			
		}
	}
}