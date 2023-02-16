package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSummonSet implements IMessage {
	public byte summonSetID;
	public int subpsecies;
	public int variant;
	public String summonType;
	public byte behaviour;
	
	
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageSummonSet() {}
	public MessageSummonSet(ExtendedPlayer playerExt, byte summonSetID) {
		this.summonSetID = summonSetID;
		this.summonType = playerExt.getSummonSet(summonSetID).summonType;
		this.subpsecies = playerExt.getSummonSet(summonSetID).subspecies;
		this.variant = playerExt.getSummonSet(summonSetID).variant;
		this.behaviour = playerExt.getSummonSet(summonSetID).getBehaviourByte();
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageSummonSet message, MessageContext ctx, EntityPlayer player) {
		if (ctx.side == Side.SERVER) {
			// Server Side:
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);

			SummonSet summonSet = playerExt.getSummonSet(message.summonSetID);
			summonSet.readFromPacket(message.summonType, message.subpsecies, message.variant, message.behaviour);
		} else {
			// Client Side:
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if (playerExt == null)
				return;

			SummonSet summonSet = playerExt.getSummonSet(message.summonSetID);
			summonSet.readFromPacket(message.summonType, message.subpsecies, message.variant, message.behaviour);
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
        this.summonType = packet.readString(256);
		this.subpsecies = packet.readInt();
		this.variant = packet.readInt();
        this.behaviour = packet.readByte();
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
        packet.writeString(this.summonType);
		packet.writeInt(this.subpsecies);
		packet.writeInt(this.variant);
        packet.writeByte(this.behaviour);
	}
	
}
