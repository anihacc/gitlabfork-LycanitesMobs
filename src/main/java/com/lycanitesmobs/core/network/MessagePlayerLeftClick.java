package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePlayerLeftClick implements IMessage, IMessageHandler<MessagePlayerLeftClick, IMessage> {
	// ==================================================
	//                    Constructors
	// ==================================================
	public MessagePlayerLeftClick() {}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(final MessagePlayerLeftClick message, final MessageContext ctx) {
		if(ctx.side != Side.SERVER) return null;
        IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.getEntityWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                EntityPlayer player = ctx.getServerHandler().player;

				// Equipment:
				EnumHand activeHand = player.getActiveHand();
				ItemStack activeStack = player.getHeldItem(activeHand);
				if(activeStack.getItem() instanceof ItemEquipment) {
					((ItemEquipment)activeStack.getItem()).onItemLeftClick(player.getEntityWorld(), player, activeHand);
				}
			}
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
