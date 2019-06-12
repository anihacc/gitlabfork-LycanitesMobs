package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedEntity;
import com.lycanitesmobs.LycanitesMobs;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageEntityPickedUp implements IMessage, IMessageHandler<MessageEntityPickedUp, IMessage> {
	public int pickedUpEntityID;
	public int pickedUpByEntityID;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageEntityPickedUp() {}
	public MessageEntityPickedUp(Entity pickedUpEntity, Entity pickedUpByEntity) {
		this.pickedUpEntityID = pickedUpEntity.getEntityId();
		this.pickedUpByEntityID = pickedUpByEntity != null ? pickedUpByEntity.getEntityId() : 0;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessageEntityPickedUp message, MessageContext ctx) {
		if(ctx.side != Side.CLIENT) return null;
		PlayerEntity player = LycanitesMobs.proxy.getClientPlayer();
		World world = player.getEntityWorld();
		Entity pickedUpEntity = world.getEntityByID(message.pickedUpEntityID);
		Entity pickedUpByEntity = message.pickedUpByEntityID != 0 ? world.getEntityByID(message.pickedUpByEntityID) : null;

        if(!(pickedUpEntity instanceof LivingEntity))
            return null;
		ExtendedEntity pickedUpEntityExt = ExtendedEntity.getForEntity((LivingEntity)pickedUpEntity);
		if(pickedUpEntityExt != null)
			pickedUpEntityExt.setPickedUpByEntity(pickedUpByEntity);
		return null;
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
		this.pickedUpEntityID = packet.readInt();
		this.pickedUpByEntityID = packet.readInt();
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
		packet.writeInt(this.pickedUpEntityID);
		packet.writeInt(this.pickedUpByEntityID);
	}
	
}
