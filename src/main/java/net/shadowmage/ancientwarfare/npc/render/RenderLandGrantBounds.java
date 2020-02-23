
package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemNPCSettings;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderLandGrantBounds{


private static RenderLandGrantBounds INSTANCE = new RenderLandGrantBounds();
private RenderLandGrantBounds(){}
public static RenderLandGrantBounds instance(){return INSTANCE;}

@SubscribeEvent
public void handleRenderLastEvent(RenderWorldLastEvent evt)
  {
  Minecraft mc = Minecraft.getMinecraft();
  if(mc==null)
    {
    return;
    }
  EntityPlayer player = mc.thePlayer;
  if(player==null)
    {
    return;
    }
  ItemStack stack = player.inventory.getCurrentItem();
  Item item;
  if(stack==null || (item=stack.getItem())==null)
    {
    return;
    }
  if(item==AWNpcItemLoader.scanner)
    {
    renderScannerBoundingBox(player, stack, evt.partialTicks);
    }  
  }

StructureBB bb = new StructureBB(new BlockPosition(), new BlockPosition()){};
ItemNPCSettings settings = new ItemNPCSettings();

private void renderScannerBoundingBox(EntityPlayer player, ItemStack stack, float delta){
  ItemNPCSettings.getSettingsFor(stack, settings, player.worldObj);
  BlockPosition pos1, pos2, min, max;
  if(settings.hasPos1())
    {
    pos1 = settings.getPos1();
    }
  else
    {
    pos1 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
    }
  if(settings.hasPos2())
    {
    pos2 = settings.getPos2();
    }
  else
    {
    pos2 = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
    }
  if(pos1!=null && pos2!=null)
    {
    min = BlockTools.getMin(pos1, pos2);
    max = BlockTools.getMax(pos1, pos2);
    max.offset(1, 1, 1);
    renderBoundingBox(player, min, max, delta);
    }
  }

private void renderBoundingBox(EntityPlayer player, BlockPosition min, BlockPosition max, float delta)
  {
  AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
  RenderTools.adjustBBForPlayerPos(bb, player, delta);
  RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
  }

}
