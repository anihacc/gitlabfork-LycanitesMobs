package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePlayerAttack {
    public int attackEntityID = 0;

	public MessagePlayerAttack() {}
	public MessagePlayerAttack(Entity attackEntity) {
        if(attackEntity != null)
            this.attackEntityID = attackEntity.getId();
	}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessagePlayerAttack message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);
		if(ctx.get().getDirection() != NetworkDirection.PLAY_TO_SERVER)
			return;

		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if(message.attackEntityID != 0)
				playerExt.meleeAttack(player.getCommandSenderWorld().getEntity(message.attackEntityID));
        });
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessagePlayerAttack decode(FriendlyByteBuf packet) {
		MessagePlayerAttack message = new MessagePlayerAttack();
        message.attackEntityID = packet.readInt();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessagePlayerAttack message, FriendlyByteBuf packet) {
        packet.writeInt(message.attackEntityID);
	}
	
}
