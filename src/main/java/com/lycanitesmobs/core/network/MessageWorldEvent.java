package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedWorld;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageWorldEvent implements IMessage {
	public String mobEventName;
	public BlockPos pos;
	public int level = 1;
	public int subspecies = 1;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageWorldEvent() {}
	public MessageWorldEvent(String mobEventName, BlockPos pos, int level, int subspecies) {
		this.mobEventName = mobEventName;
		this.pos = pos;
		this.level = level;
		this.subspecies = subspecies;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageWorldEvent message, MessageContext ctx, EntityPlayer player) {
		World world = player.getEntityWorld();
		ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);

		if ("".equals(message.mobEventName)) {
			worldExt.stopWorldEvent();
		} else {
			worldExt.startMobEvent(message.mobEventName, null, message.pos, message.level, message.subspecies);
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
        this.mobEventName = packet.readString(256);
		this.pos = packet.readBlockPos();
		this.level = packet.readInt();
		this.subspecies = packet.readInt();
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
        packet.writeString(this.mobEventName);
		packet.writeBlockPos(this.pos);
		packet.writeInt(this.level);
		packet.writeInt(this.subspecies);
	}
	
}
