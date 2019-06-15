package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEntityGUICommand {
	int entityID;
	public byte guiCommandID;
	
	public MessageEntityGUICommand() {}
	public MessageEntityGUICommand(byte guiCommandID, Entity entity) {
		this.entityID = entity.getEntityId();
		this.guiCommandID = guiCommandID;
	}

	/**
	 * Called when this message is received.
	 */
	public static void handle(MessageEntityGUICommand message, Supplier<NetworkEvent.Context> ctx) {
		if(ctx.get().getDirection() != NetworkDirection.PLAY_TO_SERVER)
			return;

        ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			World world = player.getEntityWorld();
			Entity entity = world.getEntityByID(message.entityID);
			if (entity instanceof EntityCreatureTameable) {
				EntityCreatureTameable pet = (EntityCreatureTameable) entity;
				pet.performGUICommand(player, message.guiCommandID);
			}
		});
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessageEntityGUICommand decode(PacketBuffer packet) {
		MessageEntityGUICommand message = new MessageEntityGUICommand();
		message.entityID = packet.readInt();
		message.guiCommandID = packet.readByte();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessageEntityGUICommand message, PacketBuffer packet) {
		packet.writeInt(message.entityID);
		packet.writeByte(message.guiCommandID);
	}
	
}
