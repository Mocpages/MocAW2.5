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
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
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

public static void handleCommandClient(CommandType type, int x, int y, int z, int x1, int y1, int z1) {
	PacketNpcCommand pkt = new PacketNpcCommand(type, x, y, z, x1, y1, z1);
    NetworkHandler.sendToServer(pkt);
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
//  ItemCommandBaton.getCommandedEntities(player.worldObj, player.getCurrentEquippedItem(), targets);
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
  int rows = (int) Math.ceil(targets.size() / getDist(p.x2, p.y2, p.getPX(), p.getPZ()));
  int files = (int) Math.ceil(targets.size() / rows);
  System.out.println("ranks: " + rows + " files: " + files);
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
		    //  int x2 = interpolate(x, p.x2, files, targets.size());
		      //int z2 = interpolate(z, p.y2, files, targets.size());
		    //  cmd = new Command(type, x2, y, z2);
		      i++;
		     // ((NpcBase)e).handlePlayerCommand(cmd);
	      }else {
	    	  p = ((NpcPlayerOwned)e);
		      p.cx = (p.getPX() + x)/2;
		      p.cy = (p.getPZ() + z)/2;
		      p.x2 = x;
		      p.y2 = z;
		   //   p.setCustomNameTag(Float.toString(f));
		      //int x2 = interpolate(p.getPX(), x, i, targets.size());
		      //int z2 = interpolate(p.getPZ(), z, i, targets.size());
		//      cmd = new Command(type, x2, y, z2);
		      i++;
		     // p.setCustomNameTag("X:"+Integer.toString(x2)+" Z:"+Integer.toString(z2));
		  //    ((NpcBase)e).handlePlayerCommand(cmd);
	      	}
	      }
	    }
  }

public static void handleRow(List<NpcBase> targets, int x1, int x2, int z1, int z2) {
	int i = 0;
	for(NpcBase n : targets){
		float xf = interpolate(x1, x2, i, targets.size());
	    float zf = interpolate(z1, z2, i, targets.size());
	    System.out.println("X1: " + x1 +  " Z1: " + z2 + " X2: " + x2 + " Z2: " + z2 + " XF: " + xf + " ZF " + zf);
	    i++;
	    n.handlePlayerCommand(new Command(CommandType.ATTACK_AREA, xf, 90, zf));
	    
	}
}

public static void handleFormation(List<NpcBase> targets, int x1, int x2, int z1, int z2, int files) {
	if(targets.size() <= files) { //we only need one rank!
		handleRow(targets,x1,x2,z1,z2);
	}else {
		handleRow(targets.subList(0, files),x1,x2,z1,z2);
		double angle = getAngle(x1,x2,z1,z2);
		int[] leftCoords = getBackOne(x1,z1, angle);
		int[] rightCoords = getBackOne(x2,z2,angle);
		handleFormation(targets.subList(files, targets.size()), leftCoords[0], rightCoords[0], leftCoords[1], rightCoords[1], files);
	}
}

//int op = Math.max(Math.abs(x1 - x2), Math.abs(x2 - x1));
//int adj = Math.max(Math.abs(z1 - z2), Math.abs(z2 - z1));
//double angle = Math.atan2(op / adj);
public static int[] getNewCoords(int x1, int x2, int z1, int z2) {
	
	
	return null;
}

public static int[] getBackOne(int x, int z, double angle) {
	//System.out.println("Angle: " + angle / Math.PI + "pi");
	angle = 0.785;
	double xPrime = x * Math.cos(angle) - z * Math.sin(angle);
	double zPrime = -x  * Math.sin(angle) + z * Math.cos(angle);
	zPrime -= 1;
	int x2 = (int) (xPrime * Math.cos(angle) - zPrime * Math.sin(angle));
	int z2 = (int) (-xPrime  * Math.sin(angle) + zPrime * Math.cos(angle));
	return new int[] {x2,z2};
}

public static double getAngle(int x1, int x2, int z1, int z2) {
	int cx = (x1 + x2) / 2;
	int cz = (z1 + z2) / 2;
	double angle = Math.atan2(z2 - cz, x2 - cx);
	return angle;
}

public static float interpolate(int initial, int last, int i, int n) {
	return initial + ((float)i/(n-1)) * (last - initial);
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


public static void handleServerCommand(EntityPlayer player, CommandType type, boolean block, int xp, int yp, int zp, int x, int y, int z){
Command cmd = null;
if(block)
  {
  cmd = new Command(type, x, y, z);
  }
else
  {
  cmd = new Command(type, x);
  }	
List<NpcBase> targets = new ArrayList<NpcBase>();
ItemCommandBaton.getCommandedEntities(player.worldObj, player.getCurrentEquippedItem(), targets);
System.out.println("Handling. X1 " + xp + " Z1 " + zp  + " X2 " +x + " Z2 " + z);
int rows = (int) Math.ceil(targets.size() / getDist(xp, zp, x,z));
int files = (int) Math.ceil(targets.size() / rows);

handleFormation(targets,xp,x,zp,z,files);
//handleRow(targets, xp,  x, zp, z);
}


public static final class Command
{
public CommandType type;
public float x, y, z;
public boolean blockTarget;

UUID entityID;
Entity entity;

public Command(){}

public Command(NBTTagCompound tag)
  {
  readFromNBT(tag);
  }

public Command(CommandType type, float xf, float y, float zf)
  {
  this.type = type;
  this.x = xf;
  this.y = y;
  this.z = zf;
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
  x = tag.getFloat("x");
  y = tag.getFloat("y");
  z = tag.getFloat("z");
  if(tag.hasKey("idmsb") && tag.hasKey("idlsb"))
    {
    entityID = new UUID(tag.getLong("idmsb"), tag.getLong("idlsb"));
    }
  }

public final NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("type", type.ordinal());
  tag.setBoolean("block", blockTarget);
  tag.setFloat("x", x);
  tag.setFloat("y", y);
  tag.setFloat("z", z);
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
    entity = world.getEntityByID(Math.round(x));
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
