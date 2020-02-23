package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.block.IconRotationMap;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.tile.TileCity;

public class BlockCity  extends Block implements IRotatableBlock{
	IconRotationMap iconMap = new IconRotationMap();

	
	public BlockCity(String regName){
	  super(Material.rock);
	  this.setBlockName(regName);
	  this.setCreativeTab(AWNpcItemLoader.npcTab);
	  setHardness(2.f);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	  {
	  TileCity tile = (TileCity) world.getTileEntity(x, y, z);
	  if(tile!=null){InventoryTools.dropInventoryInWorld(world, tile, x, y, z);}
	  super.breakBlock(world, x, y, z, block, meta);
	  }

	@Override
	public RotationType getRotationType()
	  {
	  return RotationType.FOUR_WAY;
	  }

	@Override
	public boolean invertFacing()
	  {
	  return true;
	  }

	@Override
	public void registerBlockIcons(IIconRegister register)
	  {
	  iconMap.registerIcons(register);
	  }
	
	@Override
	public IIcon getIcon(int side, int meta)
	  {
	  return iconMap.getIcon(this, meta, side);
	  }

	@Override
	public BlockCity setIcon(RelativeSide side, String texName)
	  {
	  iconMap.setIcon(this, side, texName);
	  return this;
	  }

	@Override
	public boolean hasTileEntity(int metadata)
	  {
	  return true;
	  }

	@Override
	public TileEntity createTileEntity(World world, int metadata)
	  {
	  return new TileCity();
	  }

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideHit, float hitX, float hitY, float hitZ)
	  {  
	  TileEntity te = world.getTileEntity(x, y, z);
	  if(te instanceof IInteractableTile)
	    {
	    ((IInteractableTile) te).onBlockClicked(player);
	    }
	  return true;  
	  }
}
