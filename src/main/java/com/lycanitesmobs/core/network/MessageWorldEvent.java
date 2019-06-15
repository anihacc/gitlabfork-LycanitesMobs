package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageWorldEvent {
	public String mobEventName;
	public BlockPos pos;
	public int level = 1;

	public MessageWorldEvent() {}
	public MessageWorldEvent(String mobEventName, BlockPos pos, int level) {
		this.mobEventName = mobEventName;
		this.pos = pos;
		this.level = level;
	}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessageWorldEvent message, Supplier<NetworkEvent.Context> ctx) {
		if(ctx.get().getDirection() != NetworkDirection.LOGIN_TO_CLIENT)
			return;

		PlayerEntity player = LycanitesMobs.proxy.getClientPlayer();
		World world = player.getEntityWorld();
        ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		
		if("".equals(message.mobEventName))
            worldExt.stopWorldEvent();
		else {
            worldExt.startMobEvent(message.mobEventName, null, message.pos, message.level);
		}
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessageWorldEvent decode(PacketBuffer packet) {
		MessageWorldEvent message = new MessageWorldEvent();
		message.mobEventName = packet.readString(256);
		message.pos = packet.readBlockPos();
		message.level = packet.readInt();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessageWorldEvent message, PacketBuffer packet) {
        packet.writeString(message.mobEventName);
		packet.writeBlockPos(message.pos);
		packet.writeInt(message.level);
	}
	
}
