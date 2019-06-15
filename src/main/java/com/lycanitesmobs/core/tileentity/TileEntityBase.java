package com.lycanitesmobs.core.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public abstract class TileEntityBase extends TileEntity implements ITickableTileEntity {
    /**
     * Constructor
     */
    public TileEntityBase() {
        super(TileEntityType.CHEST);
    }

    /**
     * Gets the Tile Entity Type used by this Tile Entity. Should be overridden.
     * @return The Tile Entity Type.
     */
    public TileEntityType<?> getType() {
        return super.getType();
    }

    /**
     * The main update called every tick.
     */
    @Override
    public void tick() {}

    /**
     * Removes this Tile Entity.
     */
    @Override
    public void remove() {
        super.remove();
    }

    /**
     * Called when receiving an event from a client, used for opening GUIs, etc.
     * @param eventID The ID of the event.
     * @param eventArg The argument ID of the event.
     * @return
     */
    @Override
    public boolean receiveClientEvent(int eventID, int eventArg) {
        return false;
    }

    /**
     * Gets the update packet for this Tile Entity.
     * @return The update packet to use or null if not needed.
     */
    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return super.getUpdatePacket();
    }

    /**
     * Called when this Tile Entity receives a Data Packet.
     * @param net The Network Manager.
     * @param pkt The packet received.
     */
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
    }

    /**
     * Called by the GUI Handler when opening a GUI.
     * @param player The player opening the GUI.
     * @return A GUI if client side and an optional Container if server side.
     */
    public Object getGUI(PlayerEntity player) {
        return null;
    }

    /**
     * Called when a GUI opened by this Tile Entity has a button pressed.
     * @param buttonId The ID of the button pressed.
     */
    public void onGuiButton(byte buttonId) {}

    /**
     * Reads from saved NBT data.
     * @param nbtTagCompound The NBT to read from.
     */
    @Override
    public void read(CompoundNBT nbtTagCompound) {
        super.read(nbtTagCompound);
    }

    /**
     * Writes to NBT data.
     * @param nbtTagCompound The NBT data to write to.
     * @return The written to NBT data.
     */
    @Override
    public CompoundNBT write(CompoundNBT nbtTagCompound) {
        return super.write(nbtTagCompound);
    }
}
