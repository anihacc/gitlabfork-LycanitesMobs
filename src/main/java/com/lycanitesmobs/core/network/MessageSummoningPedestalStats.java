package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSummoningPedestalStats implements IMessage {
	public int capacity;
	public int progress;
	public int fuel;
	public int fuelMax;
    public int x;
    public int y;
    public int z;


	// ==================================================
	//                    Constructors
	// ==================================================
	public MessageSummoningPedestalStats() {}
	public MessageSummoningPedestalStats(int capacity, int progress, int fuel, int fuelMax, int x, int y, int z) {
		this.capacity = capacity;
        this.progress = progress;
        this.fuel = fuel;
        this.fuelMax = fuelMax;
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
	public static void onMessage(MessageSummoningPedestalStats message, MessageContext ctx, EntityPlayer player) {
		TileEntity tileEntity = player.getEntityWorld().getTileEntity(new BlockPos(message.x, message.y, message.z));
		TileEntitySummoningPedestal summoningPedestal = null;
		if (tileEntity instanceof TileEntitySummoningPedestal) {
			summoningPedestal = (TileEntitySummoningPedestal) tileEntity;
		}
		if (summoningPedestal != null) {
			summoningPedestal.capacity = message.capacity;
			summoningPedestal.summonProgress = message.progress;
			summoningPedestal.summoningFuel = message.fuel;
			summoningPedestal.summoningFuelMax = message.fuelMax;
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
        this.x = packet.readInt();
        this.y = packet.readInt();
        this.z = packet.readInt();
        this.capacity = packet.readInt();
        this.progress = packet.readInt();
        this.fuel = packet.readInt();
        this.fuelMax = packet.readInt();
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
        packet.writeInt(this.capacity);
        packet.writeInt(this.progress);
        packet.writeInt(this.fuel);
        packet.writeInt(this.fuelMax);
	}
	
}
