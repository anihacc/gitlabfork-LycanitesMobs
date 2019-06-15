package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageGUIRequest {
	public byte guiID;
	
	public MessageGUIRequest() {}
	public MessageGUIRequest(byte guiID) {
		this.guiID = guiID;
	}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessageGUIRequest message, Supplier<NetworkEvent.Context> ctx) {
		if(ctx.get().getDirection() != NetworkDirection.PLAY_TO_SERVER)
			return;

		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			playerExt.requestGUI(message.guiID);
		});
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessageGUIRequest decode(PacketBuffer packet) {
		MessageGUIRequest message = new MessageGUIRequest();
		message.guiID = packet.readByte();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessageGUIRequest message, PacketBuffer packet) {
		packet.writeByte(message.guiID);
	}
	
}
