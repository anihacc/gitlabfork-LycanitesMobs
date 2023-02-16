package com.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.pets.PetManager;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

public class MessagePetEntry implements IMessage {
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
	public int entityLevel;
	public int entityExperience;
	public boolean isRespawning;


	// ==================================================
	//                    Constructors
	// ==================================================
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
		this.entityLevel = petEntry.entityLevel;
		this.entityExperience = petEntry.entityExperience;
		this.isRespawning = petEntry.isRespawning;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessagePetEntry message, MessageContext ctx, EntityPlayer player) {
		if (ctx.side == Side.SERVER) {
			// Server Side:
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			PetManager petManager = playerExt.petManager;
			PetEntry petEntry = petManager.getEntry(message.petEntryID);
			if (petEntry == null)
				return;
			petEntry.setSpawningActive(message.spawningActive);
			petEntry.teleportEntity = message.teleportEntity;
			SummonSet summonSet = petEntry.summonSet;
			summonSet.readFromPacket(message.summonType, message.subspecies, message.variant, message.behaviour);
			petEntry.onBehaviourUpdate();
		} else {
			// Client Side:
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if (playerExt == null)
				return;
			PetManager petManager = playerExt.petManager;
			PetEntry petEntry = petManager.getEntry(message.petEntryID);
			if (petEntry == null) {
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
			if (message.petEntryEntityID != -1) {
				entity = player.getEntityWorld().getEntityByID(message.petEntryEntityID);
			}
			petEntry.entity = entity;
			petEntry.entityName = message.petEntryEntityName;
			petEntry.respawnTime = message.respawnTime;
			petEntry.respawnTimeMax = message.respawnTimeMax;
			petEntry.entityLevel = message.entityLevel;
			petEntry.entityExperience = message.entityExperience;
			petEntry.isRespawning = message.isRespawning;
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
        this.petEntryID = packet.readUniqueId();
        this.petEntryType = packet.readString(256);
        this.spawningActive = packet.readBoolean();
        this.teleportEntity = packet.readBoolean();
        this.summonType = packet.readString(512);
		this.subspecies = packet.readInt();
		this.variant = packet.readInt();
        this.behaviour = packet.readByte();
        this.petEntryEntityID = packet.readInt();
		this.petEntryEntityName = packet.readString(1024);
        this.respawnTime = packet.readInt();
        this.respawnTimeMax = packet.readInt();
        this.entityLevel = packet.readInt();
        this.entityExperience = packet.readInt();
        this.isRespawning = packet.readBoolean();
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
        packet.writeUniqueId(this.petEntryID);
        packet.writeString(this.petEntryType);
        packet.writeBoolean(this.spawningActive);
        packet.writeBoolean(this.teleportEntity);
        packet.writeString(this.summonType);
		packet.writeInt(this.subspecies);
		packet.writeInt(this.variant);
        packet.writeByte(this.behaviour);
        packet.writeInt(this.petEntryEntityID);
		packet.writeString(this.petEntryEntityName);
        packet.writeInt(this.respawnTime);
        packet.writeInt(this.respawnTimeMax);
        packet.writeInt(this.entityLevel);
        packet.writeInt(this.entityExperience);
        packet.writeBoolean(this.isRespawning);
	}
	
}
