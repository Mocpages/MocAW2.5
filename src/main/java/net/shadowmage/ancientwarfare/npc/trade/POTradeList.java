package net.shadowmage.ancientwarfare.npc.trade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class POTradeList
{

private List<POTrade> tradeList = new ArrayList<POTrade>();

public POTradeList(){}

public void decrementTrade(int index)
  {
  if(index<=0 || index>=getTradeList().size()){return;}
  POTrade t = getTradeList().remove(index);
  getTradeList().add(index-1, t);
  }

public void incrementTrade(int index)
  {
  if(index<0 || index>=getTradeList().size()-1){return;}
  POTrade t = getTradeList().remove(index);
  getTradeList().add(index+1, t);
  }

public void deleteTrade(int index)
  {
  if(index<0 || index>=getTradeList().size()){return;}
  getTradeList().remove(index);
  }

public void addNewTrade(){getTradeList().add(new POTrade());}

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  NBTTagList list = new NBTTagList();
  for(int i = 0; i < this.getTradeList().size(); i++)
    {
    list.appendTag(this.getTradeList().get(i).writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("tradeList", list);
  return tag;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  getTradeList().clear();
  NBTTagList list = tag.getTagList("tradeList", Constants.NBT.TAG_COMPOUND);
  POTrade t;
  for(int i = 0; i < list.tagCount(); i++)
    {
    t = new POTrade();
    t.readFromNBT(list.getCompoundTagAt(i));
    getTradeList().add(t);
    }
  }

public void getTrades(List<POTrade> trades)
  {
  trades.addAll(getTradeList());
  }

public void performTrade(EntityPlayer player, IInventory tradeInput, IInventory storage, int integer)
  {
  getTradeList().get(integer).perfromTrade(player, tradeInput, storage);
  }

public List<POTrade> getTradeList() {
	return tradeList;
}

public void setTradeList(List<POTrade> tradeList) {
	this.tradeList = tradeList;
}

}
