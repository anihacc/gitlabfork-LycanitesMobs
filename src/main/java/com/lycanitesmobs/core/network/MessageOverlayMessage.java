package com.lycanitesmobs.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageOverlayMessage {
	public Component message;

	public MessageOverlayMessage() {}
	public MessageOverlayMessage(Component message) {
		this.message = message;
	}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessageOverlayMessage message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);
		if(ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT)
			return;

		net.minecraft.client.Minecraft.getInstance().gui.setOverlayMessage(message.message, false);
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessageOverlayMessage decode(FriendlyByteBuf packet) {
		MessageOverlayMessage message = new MessageOverlayMessage();
		message.message = packet.readComponent();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessageOverlayMessage message, FriendlyByteBuf packet) {
		packet.writeComponent(message.message);
	}
	
}
