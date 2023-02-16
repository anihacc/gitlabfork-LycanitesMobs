package com.lycanitesmobs.core.network;

import io.netty.buffer.ByteBuf;
import com.lycanitesmobs.core.pets.SummonSet;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSummoningPedestalSummonSet implements IMessage {
	public String summonType;
	public int subpsecies;
	public int variant;
	public byte behaviour;
    public int x;
    public int y;
    public int z;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageSummoningPedestalSummonSet() {}
	public MessageSummoningPedestalSummonSet(SummonSet summonSet, int x, int y, int z) {
		this.summonType = summonSet.summonType;
		this.subpsecies = summonSet.subspecies;
		this.variant = summonSet.variant;
		this.behaviour = summonSet.getBehaviourByte();
        this.x = x;
        this.y = y;
        this.z = z;
	}
	
	
	// ==================================================
	//                    On Message
	// ==================================================
	/**
	 * Called when this message is received.
	 */
	public static void onMessage(MessageSummoningPedestalSummonSet message, MessageContext ctx, EntityPlayer player) {
		// Server Side:
		TileEntity tileEntity = player.getEntityWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));
		TileEntitySummoningPedestal summoningPedestal = null;
		if (tileEntity instanceof TileEntitySummoningPedestal)
			summoningPedestal = (TileEntitySummoningPedestal) tileEntity;
		if (summoningPedestal == null)
			return;
		if (summoningPedestal.summonSet == null)
			summoningPedestal.summonSet = new SummonSet(null);
		summoningPedestal.summonSet.readFromPacket(message.summonType, message.subpsecies, message.variant, message.behaviour);
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
        this.x = packet.readInt();
        this.y = packet.readInt();
        this.z = packet.readInt();
        this.summonType = packet.readString(256);
        this.subpsecies = packet.readInt();
        this.variant = packet.readInt();
        this.behaviour = packet.readByte();
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
        packet.writeInt(this.x);
        packet.writeInt(this.y);
        packet.writeInt(this.z);
        packet.writeString(this.summonType);
        packet.writeInt(this.subpsecies);
        packet.writeInt(this.variant);
        packet.writeByte(this.behaviour);
	}
	
}
