package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePlayerLeftClick {
	public MessagePlayerLeftClick() {}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessagePlayerLeftClick message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);
		if(ctx.get().getDirection() != NetworkDirection.PLAY_TO_SERVER)
			return;

		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();

			// Equipment:
			InteractionHand activeHand = player.getUsedItemHand();
			ItemStack activeStack = player.getItemInHand(activeHand);
			if(activeStack.getItem() instanceof ItemEquipment) {
				((ItemEquipment)activeStack.getItem()).onItemLeftClick(player.getCommandSenderWorld(), player, activeHand);
			}
        });
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessagePlayerLeftClick decode(FriendlyByteBuf packet) {
		MessagePlayerLeftClick message = new MessagePlayerLeftClick();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessagePlayerLeftClick message, FriendlyByteBuf packet) {

	}
	
}
