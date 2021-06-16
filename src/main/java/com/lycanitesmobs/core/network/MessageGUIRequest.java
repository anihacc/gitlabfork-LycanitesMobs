package com.lycanitesmobs.core.network;

import com.lycanitesmobs.client.gui.beastiary.IndexBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageGUIRequest {
	public enum GuiRequest {
		BEASTIARY((byte)0), SUMMONING((byte)1);
		public byte id;
		GuiRequest(byte i) { id = i; }
	}

	public byte guiId;
	
	public MessageGUIRequest() {}
	public MessageGUIRequest(GuiRequest guiRequest) {
		this.guiId = guiRequest.id;
	}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessageGUIRequest message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);
		if(ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT)
			return;

		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if (playerExt == null) {
				return;
			}

			if(message.guiId == GuiRequest.BEASTIARY.id) {
				Minecraft.getInstance().setScreen(new IndexBeastiaryScreen(player));
			}
			else if(message.guiId == GuiRequest.SUMMONING.id) {
				Minecraft.getInstance().setScreen(new SummoningBeastiaryScreen(player));
			}
		});
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessageGUIRequest decode(PacketBuffer packet) {
		MessageGUIRequest message = new MessageGUIRequest();
		message.guiId = packet.readByte();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessageGUIRequest message, PacketBuffer packet) {
		packet.writeByte(message.guiId);
	}
	
}
