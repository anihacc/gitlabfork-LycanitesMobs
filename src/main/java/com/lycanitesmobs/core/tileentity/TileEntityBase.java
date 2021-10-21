package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.Nullable;

public abstract class TileEntityBase extends BlockEntity implements TickableBlockEntity, Container {
    /**
     * Constructor
     */
    public TileEntityBase() {
        super(BlockEntityType.CHEST);
    }

    /**
     * Gets the Tile Entity Type used by this Tile Entity. Should be overridden.
     * @return The Tile Entity Type.
     */
    public BlockEntityType<?> getType() {
        return super.getType();
    }

    /**
     * The main update called every tick.
     */
    @Override
    public void tick() {}

    @Override
    public boolean stillValid(Player player) {
        return this.getBlockPos().distSqr(player.blockPosition()) < 16F;
    }

    /**
     * Removes this Tile Entity.
     */
    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    /**
     * Called when receiving an event from a client, used for opening GUIs, etc.
     * @param eventID The ID of the event.
     * @param eventArg The argument ID of the event.
     * @return
     */
    @Override
    public boolean triggerEvent(int eventID, int eventArg) {
        return false;
    }

    /**
     * Gets the update packet for this Tile Entity.
     * @return The update packet to use or null if not needed.
     */
    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return super.getUpdatePacket();
    }

    /**
     * Called when this Tile Entity receives a Data Packet.
     * @param net The Network Manager.
     * @param pkt The packet received.
     */
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }

    /**
     * Called when a GUI opened by this Tile Entity has a button pressed.
     * @param buttonId The ID of the button pressed.
     */
    public void onGuiButton(int buttonId) {}

    /**
     * Reads from saved NBT data.
     * @param nbtTagCompound The NBT to read from.
     */
    @Override
    public void load(BlockState blockState, CompoundTag nbtTagCompound) {
        super.load(blockState, nbtTagCompound);
    }

    /**
     * Writes to NBT data.
     * @param nbtTagCompound The NBT data to write to.
     * @return The written to NBT data.
     */
    @Override
    public CompoundTag save(CompoundTag nbtTagCompound) {
        return super.save(nbtTagCompound);
    }
}
