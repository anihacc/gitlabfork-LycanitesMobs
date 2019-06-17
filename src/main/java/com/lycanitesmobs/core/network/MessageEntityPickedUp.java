package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.ExtendedEntity;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEntityPickedUp {
	public int pickedUpEntityID;
	public int pickedUpByEntityID;
	
	public MessageEntityPickedUp() {}
	public MessageEntityPickedUp(Entity pickedUpEntity, Entity pickedUpByEntity) {
		this.pickedUpEntityID = pickedUpEntity.getEntityId();
		this.pickedUpByEntityID = pickedUpByEntity != null ? pickedUpByEntity.getEntityId() : 0;
	}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessageEntityPickedUp message, Supplier<NetworkEvent.Context> ctx) {
		if(ctx.get().getDirection() != NetworkDirection.LOGIN_TO_CLIENT)
			return;

		PlayerEntity player = ClientManager.getInstance().getClientPlayer();
		World world = player.getEntityWorld();
		Entity pickedUpEntity = world.getEntityByID(message.pickedUpEntityID);
		Entity pickedUpByEntity = message.pickedUpByEntityID != 0 ? world.getEntityByID(message.pickedUpByEntityID) : null;

        if(!(pickedUpEntity instanceof LivingEntity))
            return;
		ExtendedEntity pickedUpEntityExt = ExtendedEntity.getForEntity((LivingEntity)pickedUpEntity);
		if(pickedUpEntityExt != null)
			pickedUpEntityExt.setPickedUpByEntity(pickedUpByEntity);
		return;
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessageEntityPickedUp decode(PacketBuffer packet) {
		MessageEntityPickedUp message = new MessageEntityPickedUp();
		message.pickedUpEntityID = packet.readInt();
		message.pickedUpByEntityID = packet.readInt();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessageEntityPickedUp message, PacketBuffer packet) {
		packet.writeInt(message.pickedUpEntityID);
		packet.writeInt(message.pickedUpByEntityID);
	}
	
}
