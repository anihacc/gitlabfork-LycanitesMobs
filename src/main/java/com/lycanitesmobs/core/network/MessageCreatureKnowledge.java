package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCreatureKnowledge implements IMessage {
	public String creatureName;
	public int rank;
	public int experience;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageCreatureKnowledge() {}
	public MessageCreatureKnowledge(CreatureKnowledge creatureKnowledge) {
		this.creatureName = creatureKnowledge.creatureName;
		this.rank = creatureKnowledge.rank;
		this.experience = creatureKnowledge.experience;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageCreatureKnowledge message, MessageContext ctx, EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if (playerExt == null)
			return;

		playerExt.beastiary.addCreatureKnowledge(new CreatureKnowledge(playerExt.beastiary, message.creatureName, message.rank, message.experience), false);
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
		try {
			this.creatureName = packet.readString(256);
			this.rank = packet.readInt();
			this.experience = packet.readInt();
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "There was a problem decoding the packet: " + packet + ".");
			e.printStackTrace();
		}
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
		packet.writeString(this.creatureName);
        packet.writeInt(this.rank);
		packet.writeInt(this.experience);
	}
	
}
