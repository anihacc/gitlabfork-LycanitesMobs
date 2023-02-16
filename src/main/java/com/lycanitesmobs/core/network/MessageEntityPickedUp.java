package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageEntityPickedUp implements IMessage {
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
	public static void onMessage(MessageEntityPickedUp message, MessageContext ctx, EntityPlayer player) {
		World world = player.getEntityWorld();
		Entity pickedUpEntity = world.getEntityByID(message.pickedUpEntityID);
		Entity pickedUpByEntity = message.pickedUpByEntityID != 0 ? world.getEntityByID(message.pickedUpByEntityID) : null;

		if (!(pickedUpEntity instanceof EntityLivingBase))
			return;
		ExtendedEntity pickedUpEntityExt = ExtendedEntity.getForEntity((EntityLivingBase) pickedUpEntity);
		if (pickedUpEntityExt != null)
			pickedUpEntityExt.setPickedUpByEntity(pickedUpByEntity);
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
