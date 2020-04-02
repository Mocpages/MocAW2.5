package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcNoble;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;
import net.shadowmage.ancientwarfare.npc.tile.SellOrder;
import net.shadowmage.ancientwarfare.npc.tile.TileCity;

public class NpcAIPlayerOwnedNobleSell extends NpcAI{
	boolean init;
	UpkeepOrder order;
	ItemStack routeStack;
	Block tgt;
	int ticksToWork;
	int ticksAtSite;
	NpcNoble noble;

	public NpcAIPlayerOwnedNobleSell(NpcBase npc) {
		super(npc);
		noble = (NpcNoble)npc;
		this.setMutexBits(ATTACK+MOVE);
	}

	@Override
	public boolean shouldExecute() {
		if(!init){
			init = true;
			routeStack = npc.ordersStack;
			order = UpkeepOrder.getUpkeepOrder(routeStack);
			if((order!=null && order.getBlock()!=null)){tgt = order.getBlock();}
		}
		if(!npc.getIsAIEnabled() || npc.shouldBeAtHome()){return false;}
		return noble.invBack !=null && order!=null && tgt != null;
	}

	@Override
	public boolean continueExecuting(){
		if(!npc.getIsAIEnabled() || npc.shouldBeAtHome()){return false;}
		return noble.invBack!=null && order!=null && tgt != null;
	}

	@Override
	public void startExecuting(){  
		npc.addAITask(TASK_WORK);
	}

	@Override
	public void updateTask()
	{
		BlockPosition pos = order.getUpkeepPosition();
		double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);  
		if(dist>5.d*5.d)
		{    
			npc.addAITask(TASK_MOVE);
			ticksAtSite=0;
			ticksToWork=0;
			moveToPosition(pos, dist);    
		}
		else
		{
			moveRetryDelay=0;
			npc.getNavigator().clearPathEntity();
			npc.removeAITask(TASK_MOVE);
			workAtSite();
		}
	}

	@Override
	public void resetTask()
	{
		ticksToWork=0;
		ticksAtSite=0;
		moveRetryDelay=0;
		npc.getNavigator().clearPathEntity();
		npc.removeAITask(TASK_WORK+TASK_MOVE);
	}

	public void workAtSite()
	{
		ticksAtSite++;
		if(npc.ticksExisted%10==0){npc.swingItem();}
		if(ticksAtSite>20){
			ticksAtSite = 0;
			IInventory inv = getTargetInventory();
			if(inv == null) {return;}
			for(int i = 0; i<= inv.getSizeInventory()-1; i++) {
				ItemStack stack = inv.getStackInSlot(i);

				if(stack != null && stack.getItem() != AWNpcItemLoader.upkeepOrder) {
					System.out.println(stack.getDisplayName());
					TileCity c = noble.getCity();
					if(c != null) {
						
						int price;
						inv.setInventorySlotContents(i, null);
						
						
						if(c.getHighestBuyOrd(stack.getItem()) != null){
							price = c.getHighestBuyOrd(stack.getItem()).getPrice() + 100;
						}else if(c.getLastBuy(stack.getItem()) != null) {
							price = c.getLastBuy(stack.getItem()).getPrice() + 100;
						}else{
							price = 100;
						}
						
						price = Math.max(100, price);
						
						SellOrder s = c.getSell(noble, stack);
						if(s == null) {
							s = new SellOrder(stack, price, noble,stack.stackSize);
							c.addSell(s);
							System.out.println("Creating sale order for " + s.getItem().getUnlocalizedName());
						}else {
							System.out.println("Updating Saleorder for " + s.getItem().getUnlocalizedName());
							s.setAmt(s.getAmt() + stack.stackSize);
						}
					}
				}
			}
		}
	}

	private void startWork()
	{
		IInventory target = getTargetInventory();
		IInventory npcInv = noble.invBack;
		if(target!=null)
		{

		}
	}

	private IInventory getTargetInventory()
	{
		BlockPosition pos = order.getUpkeepPosition();
		TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
		if(te instanceof IInventory){return (IInventory)te;}
		return null;
	}


	public void onOrdersChanged()
	{
		routeStack = npc.ordersStack;
		order = UpkeepOrder.getUpkeepOrder(routeStack);
		ticksAtSite=0;
		ticksToWork=0;
		moveRetryDelay=0;
	}

	public void readFromNBT(NBTTagCompound tag)
	{
		ticksAtSite = tag.getInteger("ticksAtSite");
		ticksToWork = tag.getInteger("ticksToWork");
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger("ticksAtSite", ticksAtSite);
		tag.setInteger("ticksToWork", ticksToWork);
		return tag;
	}

}
