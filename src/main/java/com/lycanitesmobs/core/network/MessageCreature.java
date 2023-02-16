package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureRelationshipEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCreature implements IMessage {
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
	public static void onMessage(MessageCreature message, MessageContext ctx, EntityPlayer player) {
		if (player == null)
			return;
		World world = player.getEntityWorld();
		Entity entity = world.getEntityByID(message.entityID);
		if (!(entity instanceof BaseCreatureEntity)) {
			return;
		}
		BaseCreatureEntity creatureEntity = (BaseCreatureEntity) entity;

		CreatureRelationshipEntry relationshipEntry = creatureEntity.relationships.getOrCreateEntry(player);
		relationshipEntry.setReputation(message.playerReputation);
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
