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

public class MessageEntityPerched implements IMessage {
	public int perchedOnEntityID;
	public int perchedByEntityID;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageEntityPerched() {}
	public MessageEntityPerched(Entity perchedOnEntity, Entity perchedByEntity) {
		this.perchedOnEntityID = perchedOnEntity.getEntityId();
		this.perchedByEntityID = perchedByEntity != null ? perchedByEntity.getEntityId() : 0;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageEntityPerched message, MessageContext ctx, EntityPlayer player) {
		World world = player.getEntityWorld();
		Entity perchedOnEntity = world.getEntityByID(message.perchedOnEntityID);
		Entity perchedByEntity = message.perchedByEntityID != 0 ? world.getEntityByID(message.perchedByEntityID) : null;

		if (!(perchedOnEntity instanceof EntityLivingBase))
			return;
		ExtendedEntity perchedOnEntityExt = ExtendedEntity.getForEntity((EntityLivingBase) perchedOnEntity);
		if (perchedOnEntityExt != null)
			perchedOnEntityExt.setPerchedByEntity(perchedByEntity);
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
		this.perchedOnEntityID = packet.readInt();
		this.perchedByEntityID = packet.readInt();
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
		packet.writeInt(this.perchedOnEntityID);
		packet.writeInt(this.perchedByEntityID);
	}
	
}
