package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncRequest implements IMessage {

	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageSyncRequest() {}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageSyncRequest message, MessageContext ctx, EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		playerExt.needsFullSync = true;
		LycanitesMobs.logDebug("Packets", "Requested full player sync.");
	}
	
	
	// ==================================================
	//                    From Bytes
	// ==================================================
	/**
	 * Reads the message from bytes.
	 */
	@Override
	public void fromBytes(ByteBuf buf) {

	}
	
	
	// ==================================================
	//                     To Bytes
	// ==================================================
	/**
	 * Writes the message into bytes.
	 */
	@Override
	public void toBytes(ByteBuf buf) {

	}
	
}
