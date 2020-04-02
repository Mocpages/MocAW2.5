package net.shadowmage.ancientwarfare.npc.ai.owned;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;
import net.shadowmage.ancientwarfare.npc.tile.LandGrant;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

public class NpcAIPlayerOwnedFarm extends NpcAI{
	NpcWorker worker;
	boolean init;
	ArrayList<LandGrant> lands;
	int ticksAtWork;
	LandGrant curPlot;
	boolean planting;
	float taxCounter;

	public NpcAIPlayerOwnedFarm(NpcBase npc){
	  super(npc);
	  if(!(npc instanceof NpcWorker))
	    {
	    throw new IllegalArgumentException("cannot instantiate work ai task on non-worker npc");
	    }
	  worker = (NpcWorker)npc;
	  init = false;
	  planting = false;
	  taxCounter = 0;
	  this.setMutexBits(MOVE+ATTACK);
	  }
	
	/*
	 * @Override public void resetTask(){ npc.setCustomNameTag("Done"); ticksAtWork
	 * = 0; this.npc.removeAITask(TASK_WORK + TASK_MOVE); }
	 */


	@Override
	public boolean shouldExecute() {
		  if(!npc.getIsAIEnabled()){return false;}
		  if(npc.getTownHall() == null) {return false;}
		  if(npc.getTownHall().getOwnedLands((NpcPlayerOwned)npc) == null) {return false;}
		  if (npc.getTownHall().getOwnedLands((NpcPlayerOwned)npc).isEmpty()) {return false;}
		 // if(npc.getFoodRemaining()<=0 || npc.shouldBeAtHome()){return false;} 
		/*
		 * if(!init) { //initialize init = true; return true; }
		 */
		  return true;
	}
	
	@Override
	public boolean continueExecuting() {
		  if(!npc.getIsAIEnabled()){return false;}
	//	  if(npc.getFoodRemaining()<=0 || npc.shouldBeAtHome()){return false;} 
		  return true;
	}
	
	@Override
	public void updateTask() {
		npc.setCustomNameTag("Working");
		//planting = false;
		SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(worker.worldObj);
        SeasonTime seasonTime = new SeasonTime(seasonData.seasonCycleTicks);
        
		Season s = seasonTime.getSeason();
		if(s == Season.SPRING) {
			if(seasonTime.getSubSeason() == SubSeason.EARLY_SPRING || seasonTime.getSubSeason() == SubSeason.MID_SPRING) {
				till();
			}else if(seasonTime.getSubSeason() == SubSeason.LATE_SPRING){
				plant();
			}
		}else if (s== Season.SUMMER) {
			if(seasonTime.getSubSeason() == SubSeason.EARLY_SUMMER) {
				plant();
			}else {
				weed();
			}
		}else if(s== Season.AUTUMN) {
			harvest();
		}
	}
	
	private void till() {
		BlockPosition pos = getNextPos();
		if(pos==null) {
			npc.setCustomNameTag("Pos is null");
			return;
		}
		double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
		if(dist > 5.d*5.d)
		{
			npc.addAITask(TASK_MOVE);
			moveToPosition(pos, dist);
		}
		else
		{
			npc.getNavigator().clearPathEntity();
			npc.removeAITask(TASK_MOVE);
			work(pos);
		}
	}
	
	private void plant() {
		BlockPosition pos = getNextPos();
		if(pos==null) {
			worker.setCustomNameTag("ah fuck here we go again");
			return;
		}
		double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
		if(dist > 5.d*5.d)
		{
			npc.addAITask(TASK_MOVE);
			moveToPosition(pos, dist);
		}
		else
		{
			npc.getNavigator().clearPathEntity();
			npc.removeAITask(TASK_MOVE);
			work_plant(pos);
		}
	}
	
	public void work_plant(BlockPosition pos) {
		npc.setCustomNameTag("Planting: " + ticksAtWork);
		ticksAtWork++;
		Block b = worker.worldObj.getBlock(pos.x, pos.y-1, pos.z);
		if(ticksAtWork>=60 && b == Blocks.farmland) {
			ticksAtWork = 0;
			curPlot.blocksToPlant.remove(pos);
			worker.worldObj.setBlock(pos.x, pos.y, pos.z, Blocks.wheat);
		}
	}
	
	
	private void weed() {
		npc.setCustomNameTag("Weeding: " + ticksAtWork);
		ticksAtWork++;
		if(ticksAtWork >= 60) {
			//curPlot.addWork();
			ticksAtWork = 0;
		}
	}
	
	private void harvest() {
		BlockPosition pos = getHPos();
		if(pos==null) {
			return;
		}
		double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
		if(dist > 5.d*5.d)
		{
			npc.addAITask(TASK_MOVE);
			moveToPosition(pos, dist);
		}
		else
		{
			npc.getNavigator().clearPathEntity();
			npc.removeAITask(TASK_MOVE);
			work_harvest(pos);
		}
	}
	
	private void work_harvest(BlockPosition pos) {
		ticksAtWork++;
		npc.setCustomNameTag("Harvesting: " + ticksAtWork);
		Block b = worker.worldObj.getBlock(pos.x, pos.y, pos.z);
		if(ticksAtWork>=60 && b == Blocks.wheat) {
			ticksAtWork = 0;
			curPlot.blocksToHarvest.remove(pos);
			int meta = worker.worldObj.getBlockMetadata(pos.x, pos.y, pos.z);  
			List<ItemStack> stacks = b.getDrops(worker.worldObj, pos.x, pos.y, pos.z, meta, 1);
			float taxRate;
			//if(curPlot.getTithe() != 0){
			//	taxRate = 100F / (float)curPlot.getTithe();
			//}else {
		//		taxRate = 0.0F;
			//}
			taxRate = 10.0F;
			//worker.setCustomNameTag("tax rate: " + taxRate);
			for(ItemStack s : stacks) {
				taxCounter++;
				if(taxCounter < taxRate) {
					//taxCounter += s.stackSize;
					worker.inv.addItem(Item.getIdFromItem(s.getItem()), s.stackSize);
				}else {
					taxCounter -= taxRate;
					worker.getTownHall().addItem(s);
				}
			}
			worker.worldObj.setBlock(pos.x, pos.y, pos.z, Blocks.air);
			worker.worldObj.setBlock(pos.x, pos.y-1, pos.z, Blocks.dirt);
		}else if(ticksAtWork>=60) {
			curPlot.blocksToHarvest.remove(pos);
		}
	}
	
	public BlockPosition getNextPos() {
		for(LandGrant plot : lands) {
			curPlot = plot;
			if(plot == null) {
				continue;
			}
			plot.scanAll();
			if(planting) {
				return plot.blocksToPlant.get(0);
			}
			if(plot.blocksToTill != null && !plot.blocksToTill.isEmpty()) {
				return plot.blocksToTill.get(0);
			}else if(plot.blocksToPlant != null && !plot.blocksToPlant.isEmpty()) {
				planting = true;
				return plot.blocksToPlant.get(0);
			}
		}
		return null;
	}
	
	public BlockPosition getHPos() {
		for(LandGrant plot : lands) {
			curPlot = plot;
			if(plot == null) {
				continue;
			} 
			plot.scanAll();
			if(plot.blocksToHarvest != null && !plot.blocksToHarvest.isEmpty()) {
				return plot.blocksToHarvest.get(0);
			}
		}
		return null;
	}
	
	public void work(BlockPosition pos) {
		npc.setCustomNameTag("Tilling: " + ticksAtWork);
		ticksAtWork++;
		Block b = worker.worldObj.getBlock(pos.x, pos.y, pos.z);
		worker.setCustomNameTag(b.getLocalizedName());
		if(ticksAtWork>=60 && (b == Blocks.grass || b == Blocks.dirt)) {
			ticksAtWork = 0;
			curPlot.blocksToTill.remove(pos);
			worker.worldObj.setBlock(pos.x, pos.y, pos.z, Blocks.farmland);
		}
	}
	

	@Override
	public void startExecuting(){
	  lands = worker.getTownHall().getOwnedLands(worker);
	  ticksAtWork = 0;
	  npc.addAITask(TASK_WORK);
	}

}
