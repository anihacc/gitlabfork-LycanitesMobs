package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureRelationshipEntry;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageCreature implements IMessage, IMessageHandler<MessageCreature, IMessage> {
	int entityID;
	int playerReputation = 0;

	public MessageCreature() {}
	public MessageCreature(BaseCreatureEntity creatureEntity, int playerReputation) {
		this.entityID = creatureEntity.getEntityId();
		this.playerReputation = playerReputation;
	}

	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(MessageCreature message, MessageContext ctx) {
		if(ctx.side != Side.CLIENT) return null;
		EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
		if(player == null) return null;
		World world = player.getEntityWorld();
		Entity entity = world.getEntityByID(message.entityID);
		if (!(entity instanceof BaseCreatureEntity)) {
			return null;
		}
		BaseCreatureEntity creatureEntity = (BaseCreatureEntity)entity;

		CreatureRelationshipEntry relationshipEntry = creatureEntity.relationships.getOrCreateEntry(player);
		relationshipEntry.setReputation(message.playerReputation);

		return null;
	}

	/**
	 * Reads the message from bytes.
	 */
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer packet = new PacketBuffer(buf);
		try {
			this.entityID = packet.readInt();
			this.playerReputation = packet.readInt();
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "There was a problem decoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}

	/**
	 * Writes the message into bytes.
	 */
	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer packet = new PacketBuffer(buf);
        packet.writeInt(this.entityID);
		packet.writeInt(this.playerReputation);
	}
	
}
