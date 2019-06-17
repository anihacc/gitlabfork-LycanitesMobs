package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageCreatureKnowledge {
	public String creatureName;
	public int rank;
	
	public MessageCreatureKnowledge() {}
	public MessageCreatureKnowledge(CreatureKnowledge creatureKnowledge) {
		this.creatureName = creatureKnowledge.creatureName;
		this.rank = creatureKnowledge.rank;
	}

	/**
	 * Called when this message is received.
	 */
	public static void handle(MessageCreatureKnowledge message, Supplier<NetworkEvent.Context> ctx) {
		if(ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT)
			return;

		PlayerEntity player = ClientManager.getInstance().getClientPlayer();
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;
		
		playerExt.beastiary.addCreatureKnowledge(new CreatureKnowledge(playerExt.beastiary, message.creatureName, message.rank));
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessageCreatureKnowledge decode(PacketBuffer packet) {
		MessageCreatureKnowledge message = new MessageCreatureKnowledge();
		try {
			message.creatureName = packet.readString(256);
			message.rank = packet.readInt();
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "There was a problem decoding the packet: " + packet + ".");
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessageCreatureKnowledge message, PacketBuffer packet) {
		packet.writeString(message.creatureName);
        packet.writeInt(message.rank);
	}
	
}
