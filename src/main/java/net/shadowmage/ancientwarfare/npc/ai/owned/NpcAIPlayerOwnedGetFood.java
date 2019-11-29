package net.shadowmage.ancientwarfare.npc.ai.owned;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

import net.shadowmage.ancientwarfare.npc.container.ContainerNpcPlayerOwnedTrade;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.client.Minecraft;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

import tradebooth.tileentity.TileEntityTradeBoothTop;

public class NpcAIPlayerOwnedGetFood extends NpcAI
{
IEntitySelector selector;

	
	
public NpcAIPlayerOwnedGetFood(NpcPlayerOwned npc)
  {
  super(npc);
  this.setMutexBits(MOVE+ATTACK+HUNGRY);
  selector = new IEntitySelector()
  {
  @Override
  public boolean isEntityApplicable(Entity var1)
    {
    if(var1 instanceof NpcTrader)
      {
      NpcBase e = (NpcBase)var1;
      //if(NpcAIPlayerOwnedGetFood.this.npc.canBeCommandedBy(e.getOwnerName()) && isCommander(e))
        //{
        return true;
        //}
      }
    return false;
    }
  };
  }

@Override
public boolean shouldExecute()
  {
  if(!npc.getIsAIEnabled()){return false;}
  return npc.requiresUpkeep() && npc.getUpkeepPoint()!=null && npc.getFoodRemaining()==0 && npc.getUpkeepDimensionId()==npc.worldObj.provider.dimensionId;
  }

@Override
public boolean continueExecuting()
  {
  if(!npc.getIsAIEnabled()){return false;}
  return npc.requiresUpkeep() && npc.getUpkeepPoint()!=null && npc.getFoodRemaining() < npc.getUpkeepAmount() && npc.getUpkeepDimensionId()==npc.worldObj.provider.dimensionId;
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
@Override
public void startExecuting()
  {
  npc.addAITask(TASK_UPKEEP);
  }

/**
 * Updates the task
 */

/*
 * //Minecraft.getMinecraft().thePlayer.sendChatMessage("test"); double dist =
 * 50; AxisAlignedBB bb = npc.boundingBox.expand(dist, dist/2, dist);
 * List<NpcTrader> traderList =
 * npc.worldObj.selectEntitiesWithinAABB(NpcTrader.class, bb, selector);
 * ((NpcPlayerOwned)npc).withdrawFood(traderList);
 */	//tryUpkeep(traderList);
@Override
public void updateTask(){
	BlockPosition pos = npc.getUpkeepPoint();
	if(pos==null){return;}
	double dist = npc.getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
	if(dist>5.d*5.d){
		npc.addAITask(TASK_MOVE);
		moveToPosition(pos, dist);
	}else{
	    npc.removeAITask(TASK_MOVE);
	    tryUpkeep(pos);
	}
}

protected void tryUpkeep(BlockPosition pos)
{
TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
int side = npc.getUpkeepBlockSide();
if(te instanceof TileEntityTradeBoothTop)
  {
  ((NpcPlayerOwned)npc).withdrawFood((TileEntityTradeBoothTop) te, side);
  }
}

/**
 * Resets the task
 */
@Override
public void resetTask()
  {
  moveRetryDelay=0;
  npc.removeAITask(TASK_UPKEEP + TASK_MOVE);
  }
}
