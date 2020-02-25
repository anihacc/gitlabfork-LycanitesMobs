package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSyncRequest implements IMessage, IMessageHandler<MessageSyncRequest, IMessage> {

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
	@Override
	public IMessage onMessage(final MessageSyncRequest message, final MessageContext ctx) {
		if(ctx.side != Side.SERVER)
			return null;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.getEntityWorld();
        mainThread.addScheduledTask(() -> {
			EntityPlayer player = ctx.getServerHandler().player;
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			playerExt.needsFullSync = true;
			LycanitesMobs.logDebug("Packets", "Requested full player sync.");
		});
		return null;
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
	}
	
}
