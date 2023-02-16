package com.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGUIRequest implements IMessage {
	public byte guiID;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageGUIRequest() {}
	public MessageGUIRequest(byte guiID) {
		this.guiID = guiID;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageGUIRequest message, MessageContext ctx, EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		playerExt.requestGUI(message.guiID);
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
		this.guiID = packet.readByte();
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
		packet.writeByte(this.guiID);
	}
	
}
