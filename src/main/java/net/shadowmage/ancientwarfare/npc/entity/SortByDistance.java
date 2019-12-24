package net.shadowmage.ancientwarfare.npc.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;

public class SortByDistance implements Comparator<NpcTrader>{
	private NpcPlayerOwned p;
	SortByDistance(NpcPlayerOwned npc){
		p=npc;
	}
	
	@Override
	public int compare(NpcTrader trader1, NpcTrader trader2) {
		if(trader1 == null) { return -1;}
		if(trader2 == null) {return 1;}
		int distance1 = Math.round(trader1.getDistanceToEntity(p)/10);
		int distance2 = Math.round(trader2.getDistanceToEntity(p)/10);
		
		List<POTrade> tradeList1 = trader1.getTradeList().getTradeList();
		List<POTrade> tradeList2 = trader1.getTradeList().getTradeList();
		
		Collections.sort(tradeList1, Collections.reverseOrder(new SortByValue()));
		Collections.sort(tradeList2, Collections.reverseOrder(new SortByValue()));
		
		distance1 += getPrice(tradeList1.get(0).getCompactInput());
		distance2 += getPrice(tradeList2.get(2).getCompactInput());
		
		if(distance1<distance2) {
			return 1;
		}else if(distance1 > distance2) {
			return -1;
		}
		return 0;
	}

	public int getPrice(List<ItemStack> p) {
		int output = 0;
		for(ItemStack i : p) {
			output += itemToCash(i);
		}
		return output;
	}
	
	public int itemToCash(ItemStack coins) {
		if(coins.getItem()==Items.gold_ingot) {
			return coins.stackSize;
		}
		return 0;
	}
}
