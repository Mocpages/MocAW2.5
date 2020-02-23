package net.shadowmage.ancientwarfare.npc.item;

import java.io.File;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;
import net.shadowmage.ancientwarfare.structure.template.save.TemplateExporter;
import net.shadowmage.ancientwarfare.structure.template.scan.TemplateScanner;

public class ItemLandGrant extends Item implements IItemKeyInterface, IItemClickable
{

public ItemLandGrant(String localizationKey)
  {
  this.setUnlocalizedName(localizationKey); 
  this.setCreativeTab(AWNpcItemLoader.npcTab);
  this.setMaxStackSize(1);
  this.setTextureName("ancientwarfare:structure/"+localizationKey);
  }



@Override
public boolean cancelRightClick(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean cancelLeftClick(EntityPlayer player, ItemStack stack)
  {
  return false;
  }

ItemNPCSettings viewSettings = new ItemNPCSettings();
@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4){ 
  if(par1ItemStack!=null){
    ItemNPCSettings.getSettingsFor(par1ItemStack, viewSettings, par2EntityPlayer.worldObj);
    String key = InputHandler.instance().getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
    if(viewSettings.hasPos1() && viewSettings.hasPos2())
      {
      list.add(key+" = "+StatCollector.translateToLocal("guistrings.structure.scanner.click_to_process"));
      list.add("(3/3)");
      }        
    else if(!viewSettings.hasPos1())
      {
      list.add(key+" = "+StatCollector.translateToLocal("guistrings.structure.scanner.select_first_pos"));
      list.add("(1/3)");
      }
    else if(!viewSettings.hasPos2())
      {
      list.add(key+" = "+StatCollector.translateToLocal("guistrings.structure.scanner.select_second_pos"));
      list.add("(2/3)");
      }
    }  
  }

ItemNPCSettings scanSettings = new ItemNPCSettings();
@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  ItemNPCSettings.getSettingsFor(stack, scanSettings, player.worldObj);
  if(player.isSneaking()){
    scanSettings.clearSettings();
    ItemNPCSettings.setSettingsFor(stack, scanSettings);
  }else if(scanSettings.hasPos1() && scanSettings.hasPos2()){
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_GRANT, 0, 0, 0);
    } 
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key)
  {
  return key==ItemKey.KEY_0;
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack, ItemKey key)
  {  
  if(!MinecraftServer.getServer().getConfigurationManager().func_152607_e(player.getGameProfile()))      
    {
    return;
    }
  BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
  if(hit==null){return;}
  ItemNPCSettings.getSettingsFor(stack, scanSettings, player.worldObj);
  if(scanSettings.hasPos1() && scanSettings.hasPos2())
    {
    player.addChatMessage(new ChatComponentTranslation("guistrings.structure.scanner.click_to_process"));
    }
  else if(!scanSettings.hasPos1())
    {
    scanSettings.setPos1(new BlockPosition(hit.x, hit.y, hit.z));
    player.addChatMessage(new ChatComponentTranslation("guistrings.structure.scanner.set_first_pos"));
    }
  else if(!scanSettings.hasPos2())
    {
    scanSettings.setPos2(new BlockPosition(hit.x, hit.y, hit.z));
    player.addChatMessage(new ChatComponentTranslation("guistrings.structure.scanner.set_second_pos"));
    }
  ItemNPCSettings.setSettingsFor(stack, scanSettings);
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack)
  {
  return false;
  }

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack)
  {
  
  }


}