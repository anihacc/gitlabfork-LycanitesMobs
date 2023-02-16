package com.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSummonSetSelection implements IMessage {
	public byte summonSetID;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageSummonSetSelection() {}
	public MessageSummonSetSelection(ExtendedPlayer playerExt) {
		this.summonSetID = (byte)playerExt.selectedSummonSet;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageSummonSetSelection message, MessageContext ctx, EntityPlayer player) {
		if (ctx.side == Side.SERVER) {
			// Server Side:
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			playerExt.setSelectedSummonSet(message.summonSetID);
		} else {
			// Client Side:
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if (playerExt == null)
				return;
			playerExt.setSelectedSummonSet(message.summonSetID);
		}
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
		this.summonSetID = packet.readByte();
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
		packet.writeByte(this.summonSetID);
	}
	
}
