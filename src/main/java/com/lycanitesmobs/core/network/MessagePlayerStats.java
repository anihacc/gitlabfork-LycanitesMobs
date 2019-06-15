package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePlayerStats {
	public int spirit;
	public int summonFocus;
	
	public MessagePlayerStats() {}
	public MessagePlayerStats(ExtendedPlayer playerExt) {
		this.spirit = playerExt.spirit;
		this.summonFocus = playerExt.summonFocus;
	}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessagePlayerStats message, Supplier<NetworkEvent.Context> ctx) {
		if(ctx.get().getDirection() != NetworkDirection.LOGIN_TO_CLIENT)
			return;

		PlayerEntity player = LycanitesMobs.proxy.getClientPlayer();
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;

		playerExt.spirit = message.spirit;
		playerExt.summonFocus = message.summonFocus;
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessagePlayerStats decode(PacketBuffer packet) {
		MessagePlayerStats message = new MessagePlayerStats();
		message.spirit = packet.readInt();
		message.summonFocus = packet.readInt();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessagePlayerStats message, PacketBuffer packet) {
		packet.writeInt(message.spirit);
		packet.writeInt(message.summonFocus);
	}
	
}
