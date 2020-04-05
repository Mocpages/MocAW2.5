package net.shadowmage.ancientwarfare.npc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

/**
 * client->server npc-command packet<br>
 * should simply contain the command type-id (enum.ordinal)<br>
 * as well as the target, either entity-id or block coordinates. 
 * @author Shadowmage
 *
 */
public class PacketNpcCommand extends PacketBase
{

	CommandType type;
	boolean blockTarget, moc;
	int x, y, z, xp, yp, zp, x2, y2, z2;
	double angle;
	int ranks;

	public PacketNpcCommand(CommandType type, Entity ent)
	{
		this.type = type;
		this.blockTarget = false;
		this.x = ent.getEntityId();
	}

	public PacketNpcCommand(CommandType type, int x, int y, int z)
	{
		this.type = type;
		this.blockTarget = true;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public PacketNpcCommand(CommandType type, int x, int y, int z, double angle, int ranks)
	{
		this.type = type;
		this.blockTarget = true;
		this.moc = true;
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;
		this.ranks = ranks;

	}


	public PacketNpcCommand()
	{
	}

	@Override
	protected void writeToStream(ByteBuf data)
	{
		System.out.println("Writing data. X2 " +x2 + " Z2 " + z2);
		data.writeInt(type.ordinal());
		data.writeBoolean(blockTarget);
		data.writeBoolean(moc);
		if(blockTarget)
		{
			data.writeInt(x);
			data.writeInt(y);
			data.writeInt(z);
		}
		if(moc) {
			data.writeDouble(angle);
			data.writeInt(ranks);
		}
		else
		{
			data.writeInt(x);
		}
	}

	@Override
	protected void readFromStream(ByteBuf data)	{
		this.type = CommandType.values()[data.readInt()];
		blockTarget = data.readBoolean();
		moc = data.readBoolean();
		x=data.readInt();
		if(blockTarget)
		{
			y = data.readInt();
			z = data.readInt();
		}
		if(moc) {
			angle = data.readDouble();
			ranks = data.readInt();
		}
	}

	@Override
	protected void execute()
	{

		NpcCommand.handleServerCommand(player, type, blockTarget, x, y, z, angle, ranks);
	}

}
