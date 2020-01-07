package com.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.pets.PetManager;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePetEntry implements IMessage, IMessageHandler<MessagePetEntry, IMessage> {
    public String petEntryName;
    public int petEntryID;
    public String petEntryType;
    public boolean spawningActive;
	public boolean teleportEntity;
	public String summonType;
	public int subspecies;
	public byte behaviour;
	public int petEntryEntityID = -1;
	public String petEntryEntityName;
	public int respawnTime;
	public int respawnTimeMax;
	public boolean isRespawning;


	// ==================================================
	//                    Constructors
	// ==================================================
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
		this.petEntryEntityID = petEntry.entity != null ? petEntry.entity.getEntityId() : -1;
		this.petEntryEntityName = petEntry.entityName;
		this.respawnTime = petEntry.respawnTime;
		this.respawnTimeMax = petEntry.respawnTimeMax;
		this.isRespawning = petEntry.isRespawning;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	@Override
	public IMessage onMessage(final MessagePetEntry message, final MessageContext ctx) {
        // Server Side:
        if(ctx.side == Side.SERVER) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.getEntityWorld();
            mainThread.addScheduledTask(() -> {
				EntityPlayer player = ctx.getServerHandler().player;
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
            return null;
        }

        // Client Side:
		EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null) return null;
        PetManager petManager = playerExt.petManager;
        PetEntry petEntry = petManager.getEntry(message.petEntryID);
        if(petEntry == null) {
            petEntry = new PetEntry(this.petEntryName, message.petEntryType, player, this.summonType);
            petManager.addEntry(petEntry, message.petEntryID);
        }
        petEntry.setSpawningActive(message.spawningActive);
        petEntry.teleportEntity = message.teleportEntity;
        petEntry.subspeciesID = message.subspecies;
		SummonSet summonSet = petEntry.summonSet;
		summonSet.readFromPacket(message.summonType, 0, message.behaviour);
        Entity entity = null;
        if(message.petEntryEntityID != -1) {
			entity = player.getEntityWorld().getEntityByID(message.petEntryEntityID);
		}
        petEntry.entity = entity;
        petEntry.entityName = message.petEntryEntityName;
        petEntry.respawnTime = message.respawnTime;
        petEntry.respawnTimeMax = message.respawnTimeMax;
        petEntry.isRespawning = message.isRespawning;
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
		PacketBuffer packet = new PacketBuffer(buf);
        this.petEntryName = packet.readString(256);
        this.petEntryID = packet.readInt();
        this.petEntryType = packet.readString(256);
        this.spawningActive = packet.readBoolean();
        this.teleportEntity = packet.readBoolean();
        this.summonType = packet.readString(512);
		this.subspecies = packet.readInt();
        this.behaviour = packet.readByte();
        this.petEntryEntityID = packet.readInt();
		this.petEntryEntityName = packet.readString(1024);
        this.respawnTime = packet.readInt();
        this.respawnTimeMax = packet.readInt();
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
        packet.writeString(this.petEntryName);
        packet.writeInt(this.petEntryID);
        packet.writeString(this.petEntryType);
        packet.writeBoolean(this.spawningActive);
        packet.writeBoolean(this.teleportEntity);
        packet.writeString(this.summonType);
		packet.writeInt(this.subspecies);
        packet.writeByte(this.behaviour);
        packet.writeInt(this.petEntryEntityID);
		packet.writeString(this.petEntryEntityName);
        packet.writeInt(this.respawnTime);
        packet.writeInt(this.respawnTimeMax);
        packet.writeBoolean(this.isRespawning);
	}
	
}
