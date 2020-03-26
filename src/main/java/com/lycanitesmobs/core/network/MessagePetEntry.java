package com.lycanitesmobs.core.network;

import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.pets.PetManager;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessagePetEntry {
    public UUID petEntryID;
    public String petEntryType;
    public boolean spawningActive;
	public boolean teleportEntity;
	public String summonType;
	public int subspecies;
	public int variant;
	public byte behaviour;
	public int petEntryEntityID = -1;
	public String petEntryEntityName;
	public int respawnTime;
	public int respawnTimeMax;
	public boolean isRespawning;

	public MessagePetEntry() {}
	public MessagePetEntry(ExtendedPlayer playerExt, PetEntry petEntry) {
        this.petEntryID = petEntry.petEntryID;
        this.petEntryType = petEntry.getType();
        this.spawningActive = petEntry.spawningActive;
        this.teleportEntity = petEntry.teleportEntity;
        SummonSet summonSet = petEntry.summonSet;
        this.summonType = summonSet.summonType;
        this.subspecies = petEntry.subspeciesIndex;
        this.variant = petEntry.variantIndex;
		this.behaviour = summonSet.getBehaviourByte();
		this.petEntryEntityID = petEntry.entity != null ? petEntry.entity.getEntityId() : -1;
		this.petEntryEntityName = petEntry.entityName;
		this.respawnTime = petEntry.respawnTime;
		this.respawnTimeMax = petEntry.respawnTimeMax;
		this.isRespawning = petEntry.isRespawning;
	}
	
	/**
	 * Called when this message is received.
	 */
	public static void handle(MessagePetEntry message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);
		// Server Side:
        if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
			ctx.get().enqueueWork(() -> {
				PlayerEntity player = ctx.get().getSender();
				ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
				PetManager petManager = playerExt.petManager;
				PetEntry petEntry = petManager.getEntry(message.petEntryID);
				if(petEntry == null)
					return;
				petEntry.setSpawningActive(message.spawningActive);
				petEntry.teleportEntity = message.teleportEntity;
				SummonSet summonSet = petEntry.summonSet;
				summonSet.readFromPacket(message.summonType, message.subspecies, message.variant, message.behaviour);
				petEntry.onBehaviourUpdate();
			});
            return;
        }

        // Client Side:
		PlayerEntity player = ClientManager.getInstance().getClientPlayer();
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return;
        PetManager petManager = playerExt.petManager;
        PetEntry petEntry = petManager.getEntry(message.petEntryID);
        if(petEntry == null) {
            petEntry = new PetEntry(message.petEntryID, message.petEntryType, player, message.summonType);
            petManager.addEntry(petEntry);
        }
        petEntry.setSpawningActive(message.spawningActive);
        petEntry.teleportEntity = message.teleportEntity;
        petEntry.subspeciesIndex = message.subspecies;
		petEntry.variantIndex = message.variant;
		SummonSet summonSet = petEntry.summonSet;
		summonSet.readFromPacket(message.summonType, message.subspecies, message.variant, message.behaviour);
        Entity entity = null;
        if(message.petEntryEntityID != -1) {
            entity = player.getEntityWorld().getEntityByID(message.petEntryEntityID);
        }
        petEntry.entity = entity;
        petEntry.entityName = message.petEntryEntityName;
        petEntry.respawnTime = message.respawnTime;
        petEntry.respawnTimeMax = message.respawnTimeMax;
        petEntry.isRespawning = message.isRespawning;
	}
	
	/**
	 * Reads the message from bytes.
	 */
	public static MessagePetEntry decode(PacketBuffer packet) {
		MessagePetEntry message = new MessagePetEntry();
        message.petEntryID = packet.readUniqueId();
        message.petEntryType = packet.readString(512);
        message.spawningActive = packet.readBoolean();
        message.teleportEntity = packet.readBoolean();
        message.summonType = packet.readString(512);
		message.subspecies = packet.readInt();
		message.variant = packet.readInt();
        message.behaviour = packet.readByte();
        message.petEntryEntityID = packet.readInt();
		message.petEntryEntityName = packet.readString(1024);
        message.respawnTime = packet.readInt();
        message.respawnTimeMax = packet.readInt();
        message.isRespawning = packet.readBoolean();
		return message;
	}
	
	/**
	 * Writes the message into bytes.
	 */
	public static void encode(MessagePetEntry message, PacketBuffer packet) {
        packet.writeUniqueId(message.petEntryID);
        packet.writeString(message.petEntryType);
        packet.writeBoolean(message.spawningActive);
        packet.writeBoolean(message.teleportEntity);
        packet.writeString(message.summonType);
		packet.writeInt(message.subspecies);
		packet.writeInt(message.variant);
        packet.writeByte(message.behaviour);
        packet.writeInt(message.petEntryEntityID);
		packet.writeString(message.petEntryEntityName);
        packet.writeInt(message.respawnTime);
        packet.writeInt(message.respawnTimeMax);
        packet.writeBoolean(message.isRespawning);
	}
	
}
