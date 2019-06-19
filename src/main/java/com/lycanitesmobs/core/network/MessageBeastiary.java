package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.Beastiary;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageBeastiary {
	public int entryAmount = 0;
	public String[] creatureNames;
	public int[] ranks;
	
	public MessageBeastiary() {}
	public MessageBeastiary(Beastiary beastiary) {
		this.entryAmount = Math.min(201, beastiary.creatureKnowledgeList.size());
		if(this.entryAmount > 0) {
			this.creatureNames = new String[this.entryAmount];
			this.ranks = new int[this.entryAmount];
			int i = 0;
			for(CreatureKnowledge creatureKnowledge : beastiary.creatureKnowledgeList.values()) {
				this.creatureNames[i] = creatureKnowledge.creatureName;
				this.ranks[i] = creatureKnowledge.rank;
				i++;
			}
		}
	}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessageBeastiary message, Supplier<NetworkEvent.Context> ctx) {
		if(ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT)
			return;

		PlayerEntity player = ClientManager.getInstance().getClientPlayer();
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;
		if(message.entryAmount < 0)
			return;

		playerExt.getBeastiary().creatureKnowledgeList.clear();
		for(int i = 0; i < message.entryAmount; i++) {
			String creatureName = message.creatureNames[i];
			int rank = message.ranks[i];
			CreatureKnowledge creatureKnowledge = new CreatureKnowledge(playerExt.getBeastiary(), creatureName, rank);
			playerExt.getBeastiary().creatureKnowledgeList.put(creatureKnowledge.creatureName, creatureKnowledge);
		}
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessageBeastiary decode(PacketBuffer packet) {
		MessageBeastiary message = new MessageBeastiary();
		message.entryAmount = Math.min(200, packet.readInt());
        if(message.entryAmount == 200) {
        	LycanitesMobs.logWarning("", "Received 200 or more creature entries, something went wrong with the Beastiary packet! Addition entries will be skipped to prevent OOM!");
		}
        if(message.entryAmount > 0) {
			message.creatureNames = new String[message.entryAmount];
			message.ranks = new int[message.entryAmount];
            for(int i = 0; i < message.entryAmount; i++) {
				message.creatureNames[i] = packet.readString(32767);
				message.ranks[i] = packet.readInt();
            }
        }
        return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessageBeastiary message, PacketBuffer packet) {
        packet.writeInt(message.entryAmount);
        if(message.entryAmount > 0) {
            for(int i = 0; i < message.entryAmount; i++) {
                packet.writeString(message.creatureNames[i]);
                packet.writeInt(message.ranks[i]);
            }
        }
	}
}
