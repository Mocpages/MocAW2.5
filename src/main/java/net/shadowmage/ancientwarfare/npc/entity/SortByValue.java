package net.shadowmage.ancientwarfare.npc.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;

public class SortByValue implements Comparator<POTrade>{

	@Override
	public int compare(POTrade trade1, POTrade trade2) {
		if(trade1 == null) {
			return -1;
		}else if(trade2 == null) {
			return 1;
		}
		List<ItemStack> input1 = trade1.getCompactInput();
		List<ItemStack> input2 = trade2.getCompactInput();
		List<ItemStack> output1 = trade1.getCompactOutput();
		List<ItemStack> output2 = trade2.getCompactOutput();
		int sumFood1 = 0;
		int sumFood2 = 0;
		int sumCost1 = 0;
		int sumCost2 = 0;
		for(int i = 0; i<=4; i++) {
			sumCost1 += itemToCash(input1.get(i));
			sumCost2 += itemToCash(input2.get(i));
			sumFood1 += AncientWarfareNPC.statics.getFoodValue(output1.get(i));
			sumFood2 += AncientWarfareNPC.statics.getFoodValue(output2.get(i));
		}
		
		int ratio1 = sumFood1 / sumCost1;
		int ratio2 = sumFood2 / sumCost2;
		
		if(ratio1 > ratio2) {
			return 1;
		}else if(ratio2 > ratio1) {
			return -1;
		}
		return 0;
	}

	public int itemToCash(ItemStack coins) {
		if(coins.getItem()==Items.gold_ingot) {
			return coins.stackSize;
		}
		return 0;
	}
}
