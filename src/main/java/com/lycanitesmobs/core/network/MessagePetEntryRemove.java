package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.pets.PetEntry;
import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.pets.PetManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class MessagePetEntryRemove implements IMessage {
    public UUID petEntryID;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessagePetEntryRemove() {}
	public MessagePetEntryRemove(ExtendedPlayer playerExt, PetEntry petEntry) {
        this.petEntryID = petEntry.petEntryID;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessagePetEntryRemove message, MessageContext ctx, EntityPlayer player) {
		if (ctx.side == Side.SERVER) {
			// Server Side:
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);

			PetManager petManager = playerExt.petManager;
			PetEntry petEntry = petManager.getEntry(message.petEntryID);
			if (petEntry == null) {
				LycanitesMobs.logWarning("", "Tried to remove a null PetEntry from server!");
				return; // Nothing to remove!
			}
			petEntry.remove();
		} else {
			// Client Side:
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if (playerExt == null)
				return;

			PetManager petManager = playerExt.petManager;
			PetEntry petEntry = petManager.getEntry(message.petEntryID);
			if (petEntry == null) {
				LycanitesMobs.logWarning("", "Tried to remove a null PetEntry from client!");
				return; // Nothing to remove!
			}
			petEntry.remove();
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
		try {
            this.petEntryID = packet.readUniqueId();
		} catch (Exception e) {
			LycanitesMobs.logWarning("", "There was a problem decoding the packet: " + packet + ".");
			e.printStackTrace();
		}
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
		try {
			packet.writeUniqueId(this.petEntryID);
		} catch (Exception e) {
			LycanitesMobs.logWarning("", "There was a problem encoding the packet: " + packet + ".");
			e.printStackTrace();
		}
	}
	
}
