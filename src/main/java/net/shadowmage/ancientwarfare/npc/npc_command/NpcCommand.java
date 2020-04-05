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
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
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

public static void handleCommandClient(CommandType type, int x, int y, int z, double angle, int ranks) {
	PacketNpcCommand pkt = new PacketNpcCommand(type, x, y, z, angle, ranks);
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

public static void handleRow(List<NpcBase> targets, double x1, double x2, double z1, double z2) {
	int i = 0;
	for(NpcBase n : targets){
		double xf = interpolate(x1, x2, i, targets.size());
		double zf = interpolate(z1, z2, i, targets.size());
	    System.out.println("X1: " + x1 +  " Z1: " + z2 + " X2: " + x2 + " Z2: " + z2 + " XF: " + xf + " ZF " + zf);
	    i++;
	    n.handlePlayerCommand(new Command(CommandType.ATTACK_AREA, xf, 90, zf));
	    
	}
}

public static void handleFormation(List<NpcBase> targets, double x1, double x2, double z1, double z2, int files) {
	double angle = getAngle(x1,x2,z1,z2);
	angle = Math.PI;
	targets.get(0).handlePlayerCommand(new Command(CommandType.ATTACK_AREA, x1, 90, z1));
	handleRow(targets,angle);
	/**
	if(targets.size() <= files) { //we only need one rank!
		//handleRow(targets,x1,x2,z1,z2);
		targets.get(0).handlePlayerCommand(new Command(CommandType.ATTACK_AREA, x1, 90, z1));
		handleRow(targets,angle);
	}else {
		//handleRow(targets.subList(0, files),x1,x2,z1,z2);
		targets.get(0); //set the row guide
		handleRow(targets,angle);
		double[] leftCoords = getRelOffset(x1,z1, angle, 0, -2);
		double[] rightCoords = getRelOffset(x2,z2,angle, 0, -2);
		handleFormation(targets.subList(files, targets.size()), leftCoords[0], rightCoords[0], leftCoords[1], rightCoords[1], files);
	} **/
}

public static void handleFormation(List<NpcBase> targets, double angle, int files) {
	if(targets.size() <= files) { //we only need one rank!
		//handleRow(targets,x1,x2,z1,z2);
		handleRow(targets,angle);
	}else {
		NpcBase guide = targets.get(0);
		handleRow(targets.subList(0, files),angle); //handle this rank
		targets = targets.subList(files, targets.size());
		
		//set guide for next rank
		if(targets.size() == 0) { return;}
		NpcBase rowLead = targets.get(0);
		rowLead.guide = guide;
		rowLead.xOff = 0;
		rowLead.zOff = -1;
		rowLead.angle = angle;
		handleFormation(targets, angle, files);
	}
	
	
}

public static void handleRow(List<NpcBase> targets, double angle) {
	NpcBase guide = targets.get(0);
	for(NpcBase n : targets) {
		if(n != guide) {
			n.rotationYaw = (float) angle;
			n.guide = guide;
			n.xOff = -1;
			n.zOff = 0;
			n.angle = angle;
			guide = n;
		}
	}
}

//int op = Math.max(Math.abs(x1 - x2), Math.abs(x2 - x1));
//int adj = Math.max(Math.abs(z1 - z2), Math.abs(z2 - z1));
//double angle = Math.atan2(op / adj);

public static double[] getRelOffset(double x, double z, double angle, double offX, double offZ) {
	//System.out.println("Angle: " + angle / Math.PI + "pi");
	
	double xPrime = x * Math.cos(angle) + z * Math.sin(angle);
	double zPrime = -x  * Math.sin(angle) + z * Math.cos(angle);
	zPrime += offZ;
	xPrime += offX;
	//System.out.println("X': " + xPrime +  " Z': " + zPrime);
	double x2 = xPrime * Math.cos(angle) - zPrime * Math.sin(angle);
	double z2 = xPrime  * Math.sin(angle) + zPrime * Math.cos(angle);
	return new double[] {x2,z2};
}

public static double getAngle(double x1, double x2, double z1, double z2) {
	double cx = (x1 + x2) / 2;
	double cz = (z1 + z2) / 2;
	double angle = Math.atan2(z2 - cz, x2 - cx);
	return angle;
}

public static double interpolate(double initial, double last, int i, int n) {
	return initial + ((double)i/(n-1)) * (last - initial);
}

public static double getDist(double x1, double y1, double x2, double y2) {
	double rise = y2-y1;
	double run = x2 - x1;
	return Math.sqrt(rise * rise + run * run);
}

public static double getSlope(double x1, double y1, double x2, double y2) {
	double rise = y2 - y1;
	double run = x2 - x1;
	if(rise != 0) {
		return run / rise;
	}
	return (double) Integer.MAX_VALUE;
}


public static void handleServerCommand(EntityPlayer player, CommandType type, boolean block, int x, int y, int z, double angle, int ranks){
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
if(type == CommandType.GUARD) {
	NpcBase n = targets.get(0);
	if(n.guide == player) {
		n.guide = null;
	}else {
		angle = getAngle(n.posX, player.posX, n.posY, player.posY);
		n.guide = player;
		n.xOff = 0;
		n.zOff = -3;
		n.angle = angle;

	}	
}
//System.out.println("Handling. X1 " + xp + " Z1 " + zp  + " X2 " +x + " Z2 " + z);
//int rows = (int) Math.ceil(targets.size() / getDist(xp, zp, x,z));
//int files = (int) Math.ceil(targets.size() / rows);

targets.get(0).handlePlayerCommand(new Command(CommandType.ATTACK_AREA, x, 90, z));
handleFormation(targets,angle, ranks);
//handleRow(targets, xp,  x, zp, z);
}


public static final class Command
{
public CommandType type;
public double x, y, z;
public boolean blockTarget;

UUID entityID;
Entity entity;

public Command(){}

public Command(NBTTagCompound tag)
  {
  readFromNBT(tag);
  }

public Command(CommandType type, double xf, float y, double zf)
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
  x = tag.getDouble("x");
  y = tag.getDouble("y");
  z = tag.getDouble("z");
  if(tag.hasKey("idmsb") && tag.hasKey("idlsb"))
    {
    entityID = new UUID(tag.getLong("idmsb"), tag.getLong("idlsb"));
    }
  }

public final NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("type", type.ordinal());
  tag.setBoolean("block", blockTarget);
  tag.setDouble("x", x);
  tag.setDouble("y", y);
  tag.setDouble("z", z);
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
    entity = world.getEntityByID((int) Math.round(x));
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

public double[] getCoords() {
	return new double[] {x,y,z};
}

}





}
