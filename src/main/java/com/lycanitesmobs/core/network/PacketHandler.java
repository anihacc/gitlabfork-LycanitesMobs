package com.lycanitesmobs.core.network;

import org.apache.logging.log4j.util.TriConsumer;

import com.lycanitesmobs.LycanitesMobs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
	public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(LycanitesMobs.modid);
	
	
	// ==================================================
	//                    Initialize
	// ==================================================
	/**
	 * Initializes the Packet Handler where Messages are registered.
	 */
	public void init() {
		int messageID = 0;

		// Server to Client:
		this.network.registerMessage(clientHandler(MessageBeastiary::onMessage), MessageBeastiary.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageCreature::onMessage), MessageCreature.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageCreatureKnowledge::onMessage), MessageCreatureKnowledge.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessagePlayerStats::onMessage), MessagePlayerStats.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessagePetEntry::onMessage), MessagePetEntry.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessagePetEntryRemove::onMessage), MessagePetEntryRemove.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageSummonSet::onMessage), MessageSummonSet.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageSummonSetSelection::onMessage), MessageSummonSetSelection.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageEntityPickedUp::onMessage), MessageEntityPickedUp.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageEntityPerched::onMessage), MessageEntityPerched.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageWorldEvent::onMessage), MessageWorldEvent.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageMobEvent::onMessage), MessageMobEvent.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageSummoningPedestalStats::onMessage), MessageSummoningPedestalStats.class, messageID++, Side.CLIENT);
		this.network.registerMessage(clientHandler(MessageEntityVelocity::onMessage), MessageEntityVelocity.class, messageID++, Side.CLIENT);

		// Client to Server:
		this.network.registerMessage(serverHandler(MessageEntityGUICommand::onMessage), MessageEntityGUICommand.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessageSyncRequest::onMessage), MessageSyncRequest.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessageGUIRequest::onMessage), MessageGUIRequest.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessagePlayerControl::onMessage), MessagePlayerControl.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessagePlayerLeftClick::onMessage), MessagePlayerLeftClick.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessagePlayerAttack::onMessage), MessagePlayerAttack.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessagePetEntry::onMessage), MessagePetEntry.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessagePetEntryRemove::onMessage), MessagePetEntryRemove.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessageSummonSet::onMessage), MessageSummonSet.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessageSummonSetSelection::onMessage), MessageSummonSetSelection.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessageSummoningPedestalSummonSet::onMessage), MessageSummoningPedestalSummonSet.class, messageID++, Side.SERVER);
		this.network.registerMessage(serverHandler(MessageTileEntityButton::onMessage), MessageTileEntityButton.class, messageID++, Side.SERVER);
	}

	private <REQ extends IMessage, REPLY extends IMessage> IMessageHandler<REQ, REPLY> clientHandler(
			TriConsumer<REQ, MessageContext, EntityPlayer> c) {
		return (message, ctx) -> {
			if (ctx.side == Side.CLIENT) {
				FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
					c.accept(message, ctx, LycanitesMobs.proxy.getPlayer(ctx));
				});
			}
			return null;
		};
	}

	private <REQ extends IMessage, REPLY extends IMessage> IMessageHandler<REQ, REPLY> serverHandler(
			TriConsumer<REQ, MessageContext, EntityPlayer> c) {
		return (message, ctx) -> {
			if (ctx.side == Side.SERVER) {
				FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
					c.accept(message, ctx, LycanitesMobs.proxy.getPlayer(ctx));
				});
			}
			return null;
		};
	}
	
	// ==================================================
	//                    Send To All
	// ==================================================
	/**
	 * Sends a packet from the server to all players.
	 * @param message
	 */
	public void sendToAll(IMessage message) {
		this.network.sendToAll(message);
	}
	
	
	// ==================================================
	//                   Send To Player
	// ==================================================
	/**
	 * Sends a packet from the server to the specified player.
	 * @param message
	 * @param player
	 */
	public void sendToPlayer(IMessage message, EntityPlayerMP player) {
		this.network.sendTo(message, player);
	}
	
	
	// ==================================================
	//                 Send To All Around
	// ==================================================
	/**
	 * Sends a packet from the server to all players near the specified target point.
	 * @param message
	 * @param point
	 */
	public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
		this.network.sendToAllAround(message, point);
	}
	
	
	// ==================================================
	//                 Send To Dimension
	// ==================================================
	/**
	 * Sends a packet to all players within the specified dimension.
	 * @param message
	 * @param dimensionID The ID of the dimension to use.
	 */
	public void sendToDimension(IMessage message, int dimensionID) {
		this.network.sendToDimension(message, dimensionID);
	}
	
	
	// ==================================================
	//                   Send To Server
	// ==================================================
	/**
	 * Sends a packet from the client player to the server.
	 * @param message
	 */
	public void sendToServer(IMessage message) {
		this.network.sendToServer(message);
	}
}
