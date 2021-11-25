package com.lycanitesmobs.core.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public abstract class TileEntityBase extends TileEntity implements ITickable, ISidedInventory {
    protected boolean destroyed = false;

    /** Can be called by a block when broken to alert this TileEntity that it is being removed. **/
    public void onRemove() {
        this.destroyed = true;
    }

    /** The main update called every tick. **/
    @Override
    public void update() {

    }

    @Override
    public boolean receiveClientEvent(int eventID, int eventArg) {
        return false;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {}

    public void onGuiButton(byte buttonId) {}

    /** Reads from saved NBT data. **/
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
    }

    /** Writes to NBT data. **/
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        return super.writeToNBT(nbtTagCompound);
    }

    /** Called by the GUI Handler when opening a GUI. **/
    public Object getGUI(EntityPlayer player) {
        return null;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.destroyed) {
            return false;
        }
        return this.getPos().distanceSq(player.getPosition()) < 16F;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] {};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }
}
