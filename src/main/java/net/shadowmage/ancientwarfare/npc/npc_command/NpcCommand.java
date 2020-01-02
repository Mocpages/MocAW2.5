package net.shadowmage.ancientwarfare.npc.npc_command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NpcCommand
{

public static enum CommandType
{
MOVE,
ATTACK,//attack click on entity
ATTACK_AREA,//attack click on block
GUARD,//attack click on friendly player or npc
SET_HOME,
SET_UPKEEP,
CLEAR_HOME,
CLEAR_UPKEEP,
CLEAR_COMMAND;
}

/**
 * client-side handle command. called from command baton key handler
 * @param cmd
 */
public static void handleCommandClient(CommandType type, MovingObjectPosition hit)
  {
  if(hit!=null && hit.typeOfHit!=MovingObjectType.MISS)
    {
    if(hit.typeOfHit==MovingObjectType.ENTITY && hit.entityHit!=null)
      {
      PacketNpcCommand pkt = new PacketNpcCommand(type, hit.entityHit);
      NetworkHandler.sendToServer(pkt);
      }
    else if(hit.typeOfHit==MovingObjectType.BLOCK)
      {
      PacketNpcCommand pkt = new PacketNpcCommand(type, hit.blockX, hit.blockY, hit.blockZ);
      NetworkHandler.sendToServer(pkt);
      }    
    }
  }

/**
 * server side handle command. called from packet triggered from client key input while baton is equipped
 */
public static void handleServerCommand(EntityPlayer player, CommandType type, boolean block, int x, int y, int z)
  {
  Command cmd = null;
  if(block)
    {
    cmd = new Command(type, x, y, z);
    }
  else
    {
    cmd = new Command(type, x);
    }
  List<Entity> targets = new ArrayList<Entity>();
  ItemCommandBaton.getCommandedEntities(player.worldObj, player.getCurrentEquippedItem(), targets);
  int i = 0;
  NpcPlayerOwned p = (NpcPlayerOwned) targets.get(0);
  if(type == CommandType.SET_HOME){
	  type = CommandType.MOVE;
	  double dist = getDist(p.x2, p.y2, p.getPX(), p.getPZ());
	  double angle = Math.atan2(z-p.cy, x-p.cx);
	  //angle += Math.PI/2;
	  int dx = Math.toIntExact(Math.round(Math.sin(angle) * (dist/2)));
	  int dz = Math.toIntExact(Math.round(Math.cos(angle) * (dist/2)));
	  x = p.cx + dx;
	  z = p.cy + dz;
	  for(Entity e : targets) {
		  p = (NpcPlayerOwned) e;
		  p.setP(p.cx - dx, p.cy - dz);
	  }
  }
  double rows = targets.size() / getDist(p.x2, p.y2, p.getPX(), p.getPZ());
  int files = (int) Math.ceil(targets.size() / rows);
	  for(Entity e : targets)
	    {
	    if(e instanceof NpcBase)
	      {
	      if(type == CommandType.GUARD) {
	    	  p = ((NpcPlayerOwned)e);
	    	  p.setP(x, z);
	    	  p.setCustomNameTag("Test1");
	    	  p.cx = (p.x2 - x)/2;
		      p.cy = (p.y2 - z)/2;
		      int x2 = interpolate(x, p.x2, files, targets.size());
		      int z2 = interpolate(z, p.y2, files, targets.size());
		      cmd = new Command(type, x2, y, z2);
		      i++;
		      ((NpcBase)e).handlePlayerCommand(cmd);
	      }else {
	    	  p = ((NpcPlayerOwned)e);
		      p.cx = (p.getPX() + x)/2;
		      p.cy = (p.getPZ() + z)/2;
		      p.x2 = x;
		      p.y2 = z;
		   //   p.setCustomNameTag(Float.toString(f));
		      int x2 = interpolate(p.getPX(), x, i, targets.size());
		      int z2 = interpolate(p.getPZ(), z, i, targets.size());
		      cmd = new Command(type, x2, y, z2);
		      i++;
		      p.setCustomNameTag("X:"+Integer.toString(x2)+" Z:"+Integer.toString(z2));
		      ((NpcBase)e).handlePlayerCommand(cmd);
	      	}
	      }
	    }
  }

public static int interpolate(int initial, int last, int i, int n) {
	return  Math.round(initial + ((float)i/(n-1)) * (last - initial));
}

public static double getDist(int x1, int y1, int x2, int y2) {
	int rise = y2-y1;
	int run = x2 - x1;
	return Math.sqrt(rise * rise + run * run);
}

public static float getSlope(int x1, int y1, int x2, int y2) {
	float rise = y2 - y1;
	float run = x2 - x1;
	if(rise != 0) {
		return run / rise;
	}
	return (float) Integer.MAX_VALUE;
}


public static void handleServerCommand(EntityPlayer player, CommandType type, boolean block, int xp, int yp, int zp, int x, int y, int z)
{
Command cmd = null;
if(block)
  {
  cmd = new Command(type, x, y, z);
  }
else
  {
  cmd = new Command(type, x);
  }	
List<Entity> targets = new ArrayList<Entity>();
ItemCommandBaton.getCommandedEntities(player.worldObj, player.getCurrentEquippedItem(), targets);
int i = 0;
float m = getSlope(x, z, xp, zp);
for(Entity e : targets)
  {
  if(e instanceof NpcBase)
    {
    cmd = new Command(type, x + i, y, z + Math.round(i * m));
    ((NpcBase) e).setCustomNameTag(Float.toString(m));
    i++;
    ((NpcBase)e).handlePlayerCommand(cmd);
    }
  }
}


public static final class Command
{
public CommandType type;
public int x, y, z;
public boolean blockTarget;

UUID entityID;
Entity entity;

public Command(){}

public Command(NBTTagCompound tag)
  {
  readFromNBT(tag);
  }

public Command(CommandType type, int x, int y, int z)
  {
  this.type = type;
  this.x = x;
  this.y = y;
  this.z = z;
  this.blockTarget = true;  
  }

public Command(CommandType type, int entityID)
  {
  this.type = type;
  this.x = entityID;
  this.y=this.z=0;
  this.blockTarget = false;
  }

public Command copy()
  {
  Command cmd = new Command();
  cmd.type = this.type;
  cmd.x=this.x;
  cmd.y=this.y;
  cmd.z=this.z;
  cmd.entity=this.entity;
  cmd.entityID=this.entityID;
  cmd.blockTarget=this.blockTarget;
  return cmd;
  }

public final void readFromNBT(NBTTagCompound tag)
  {
  type = CommandType.values()[tag.getInteger("type")];
  blockTarget = tag.getBoolean("block");
  x = tag.getInteger("x");
  y = tag.getInteger("y");
  z = tag.getInteger("z");
  if(tag.hasKey("idmsb") && tag.hasKey("idlsb"))
    {
    entityID = new UUID(tag.getLong("idmsb"), tag.getLong("idlsb"));
    }
  }

public final NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("type", type.ordinal());
  tag.setBoolean("block", blockTarget);
  tag.setInteger("x", x);
  tag.setInteger("y", y);
  tag.setInteger("z", z);
  if(entityID!=null)
    {
    tag.setLong("idmsb", entityID.getMostSignificantBits());
    tag.setLong("idlsb", entityID.getLeastSignificantBits());
    }
  return tag;
  }

/**
 * should be called by packet prior to passing command into npc processing
 */
public void findEntity(World world)
  {
  if(blockTarget){return;}
  if(entity!=null){return;}
  if(entityID==null)
    {
    entity = world.getEntityByID(x);
    if(entity!=null)
      {
      entityID = entity.getPersistentID();
      }
    }
  else
    {
    entity = WorldTools.getEntityByUUID(world, entityID.getMostSignificantBits(), entityID.getLeastSignificantBits());
    }
  }

public Entity getEntityTarget(World world)
  {
  if(blockTarget){return null;}
  if(entity!=null)
    {
    return entity;
    }
  else
    {
    findEntity(world);    
    } 
  return entity;
  }

}


}
