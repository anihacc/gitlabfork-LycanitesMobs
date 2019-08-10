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

import java.util.function.Supplier;

public class MessagePetEntry {
    public String petEntryName;
    public int petEntryID;
    public String petEntryType;
    public boolean spawningActive;
	public boolean teleportEntity;
	public String summonType;
	public int subspecies;
	public byte behaviour;
	public int petEntryEntityID;
	public String petEntryEntityName;
	public int respawnTime;
	public int respawnTimeMax;
	public boolean isRespawning;

	public MessagePetEntry() {}
	public MessagePetEntry(ExtendedPlayer playerExt, PetEntry petEntry) {
        this.petEntryName = petEntry.name != null ? petEntry.name : "";
        this.petEntryID = petEntry.petEntryID;
        this.petEntryType = petEntry.getType();
        this.spawningActive = petEntry.spawningActive;
        this.teleportEntity = petEntry.teleportEntity;
        SummonSet summonSet = petEntry.summonSet;
        this.summonType = summonSet.summonType;
        this.subspecies = petEntry.subspeciesID;
		this.behaviour = summonSet.getBehaviourByte();
		this.petEntryEntityID = petEntry.entity != null ? petEntry.entity.getEntityId() : 0;
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
				summonSet.readFromPacket(message.summonType, 0, message.behaviour);
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
            petEntry = new PetEntry(message.petEntryName, message.petEntryType, player, message.summonType);
            petManager.addEntry(petEntry, message.petEntryID);
        }
        petEntry.setSpawningActive(message.spawningActive);
        petEntry.teleportEntity = message.teleportEntity;
        petEntry.subspeciesID = message.subspecies;
		SummonSet summonSet = petEntry.summonSet;
		summonSet.readFromPacket(message.summonType, 0, message.behaviour);
        Entity entity = null;
        if(message.petEntryEntityID != 0) {
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
        message.petEntryName = packet.readString(512);
        message.petEntryID = packet.readInt();
        message.petEntryType = packet.readString(512);
        message.spawningActive = packet.readBoolean();
        message.teleportEntity = packet.readBoolean();
        message.summonType = packet.readString(512);
		message.subspecies = packet.readInt();
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
        packet.writeString(message.petEntryName);
        packet.writeInt(message.petEntryID);
        packet.writeString(message.petEntryType);
        packet.writeBoolean(message.spawningActive);
        packet.writeBoolean(message.teleportEntity);
        packet.writeString(message.summonType);
		packet.writeInt(message.subspecies);
        packet.writeByte(message.behaviour);
        packet.writeInt(message.petEntryEntityID);
		packet.writeString(message.petEntryEntityName);
        packet.writeInt(message.respawnTime);
        packet.writeInt(message.respawnTimeMax);
        packet.writeBoolean(message.isRespawning);
	}
	
}
