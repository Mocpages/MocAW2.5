package net.shadowmage.ancientwarfare.npc.ai.owned;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;
import net.shadowmage.ancientwarfare.npc.tile.LandGrant;
import sereneseasons.api.season.Season;
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

	public NpcAIPlayerOwnedFarm(NpcBase npc){
	  super(npc);
	  if(!(npc instanceof NpcWorker))
	    {
	    throw new IllegalArgumentException("cannot instantiate work ai task on non-worker npc");
	    }
	  worker = (NpcWorker)npc;
	  init = false;
	  planting = false;
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
		planting = false;
		SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(worker.worldObj);
        SeasonTime seasonTime = new SeasonTime(seasonData.seasonCycleTicks);
		Season s = seasonTime.getSeason();
		if(s == Season.SPRING) {
			if(!planting) {
				till();
			}else {
				plant();
			}
		}else if (s== Season.SUMMER) {
			weed();
		}else if(s== Season.AUTUMN) {
			harvest();
		}
	}
	
	private void till() {
		BlockPosition pos = getNextPos();
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
			work(pos);
		}
	}
	
	private void plant() {
		BlockPosition pos = getNextPos();
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
			work_plant(pos);
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
			List<ItemStack> stacks = b.getDrops(worker.worldObj, pos.x, pos.y, pos.z, 1, 1);
			worker.worldObj.setBlock(pos.x, pos.y, pos.z, Blocks.air);
			worker.worldObj.setBlock(pos.x, pos.y-1, pos.z, Blocks.dirt);
		}
	}
	
	public BlockPosition getNextPos() {
		for(LandGrant plot : lands) {
			curPlot = plot;
			if(plot == null) {
				continue;
			}
			plot.scanAll();
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
		npc.setCustomNameTag("Working: " + ticksAtWork);
		ticksAtWork++;
		Block b = worker.worldObj.getBlock(pos.x, pos.y, pos.z);
		if(ticksAtWork>=60 && b == Blocks.grass || b == Blocks.dirt) {
			ticksAtWork = 0;
			curPlot.blocksToTill.remove(pos);
			worker.worldObj.setBlock(pos.x, pos.y, pos.z, Blocks.farmland);
		}
	}
	
	public void work_plant(BlockPosition pos) {
		npc.setCustomNameTag("Planting: " + ticksAtWork);
		ticksAtWork++;
		Block b = worker.worldObj.getBlock(pos.x, pos.y, pos.z);
		if(ticksAtWork>=60 && b == Blocks.farmland) {
			ticksAtWork = 0;
			curPlot.blocksToPlant.remove(pos);
			worker.worldObj.setBlock(pos.x, pos.y+1, pos.z, Blocks.wheat);
		}
	}
	
	@Override
	public void startExecuting(){
	  lands = worker.getTownHall().getOwnedLands(worker);
	  ticksAtWork = 0;
	  npc.addAITask(TASK_WORK);
	}

}
