package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageEntityGUICommand implements IMessage {
	int entityID;
	public byte guiCommandID;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageEntityGUICommand() {}
	public MessageEntityGUICommand(byte guiCommandID, Entity entity) {
		this.entityID = entity.getEntityId();
		this.guiCommandID = guiCommandID;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageEntityGUICommand message, MessageContext ctx, EntityPlayer player) {
		World world = player.getEntityWorld();
		Entity entity = world.getEntityByID(message.entityID);
		if (entity instanceof TameableCreatureEntity) {
			TameableCreatureEntity pet = (TameableCreatureEntity) entity;
			pet.performGUICommand(player, message.guiCommandID);
		}
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
		this.entityID = packet.readInt();
		this.guiCommandID = packet.readByte();
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
		packet.writeInt(this.entityID);
		packet.writeByte(this.guiCommandID);
	}
	
}
