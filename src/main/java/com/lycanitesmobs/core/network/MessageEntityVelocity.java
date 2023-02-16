package com.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageEntityVelocity implements IMessage {
    public int entityID;
    public int motionX;
    public int motionY;
    public int motionZ;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageEntityVelocity() {}
	public MessageEntityVelocity(Entity entity, double motionX, double motionY, double motionZ) {
		this.entityID = entity.getEntityId();
		double d0 = 3.9D;

		if (motionX < -3.9D) {
			motionX = -3.9D;
		}

		if (motionY < -3.9D) {
			motionY = -3.9D;
		}

		if (motionZ < -3.9D) {
			motionZ = -3.9D;
		}

		if (motionX > 3.9D) {
			motionX = 3.9D;
		}

		if (motionY > 3.9D) {
			motionY = 3.9D;
		}

		if (motionZ > 3.9D) {
			motionZ = 3.9D;
		}

		this.motionX = (int)(motionX * 8000.0D);
		this.motionY = (int)(motionY * 8000.0D);
		this.motionZ = (int)(motionZ * 8000.0D);
	}


	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageEntityVelocity message, MessageContext ctx, EntityPlayer player) {
		World world = player.getEntityWorld();
		Entity entity = world.getEntityByID(message.entityID);
		entity.motionX += (double) message.motionX / 8000.0D;
		entity.motionY += (double) message.motionY / 8000.0D;
		entity.motionZ += (double) message.motionZ / 8000.0D;
	}


	// ==================================================
	//                    From Bytes
	// ==================================================
	/**
	 * Reads the message from bytes.
	 */
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer packet = new PacketBuffer(buf);
		this.entityID = packet.readVarInt();
		this.motionX = packet.readShort();
		this.motionY = packet.readShort();
		this.motionZ = packet.readShort();
	}


	// ==================================================
	//                     To Bytes
	// ==================================================
	/**
	 * Writes the message into bytes.
	 */
	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer packet = new PacketBuffer(buf);
		packet.writeVarInt(this.entityID);
		packet.writeShort(this.motionX);
		packet.writeShort(this.motionY);
		packet.writeShort(this.motionZ);
	}
}