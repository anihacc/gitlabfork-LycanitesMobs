package com.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerStats implements IMessage {
	public int spirit;
	public int summonFocus;
	public int creatureStudyCooldown;

	// ==================================================
	//                    Constructors
	// ==================================================
	public MessagePlayerStats() {}
	public MessagePlayerStats(ExtendedPlayer playerExt) {
		this.spirit = playerExt.spirit;
		this.summonFocus = playerExt.summonFocus;
		this.creatureStudyCooldown = playerExt.creatureStudyCooldown;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessagePlayerStats message, MessageContext ctx, EntityPlayer player) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if (playerExt == null)
			return;

		playerExt.spirit = message.spirit;
		playerExt.summonFocus = message.summonFocus;
		playerExt.creatureStudyCooldown = message.creatureStudyCooldown;
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
		this.spirit = packet.readInt();
		this.summonFocus = packet.readInt();
		this.creatureStudyCooldown = packet.readInt();
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
		packet.writeInt(this.spirit);
		packet.writeInt(this.summonFocus);
		packet.writeInt(this.creatureStudyCooldown);
	}
	
}
