package com.lycanitesmobs.core.capabilities;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class ExtendedPlayerStorage implements Capability.IStorage<IExtendedPlayer> {

    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    @Override
    public void readNBT(Capability<IExtendedPlayer> capability, IExtendedPlayer instance, Direction facing, Tag nbt) {
        if(!(instance instanceof ExtendedPlayer) || !(nbt instanceof CompoundTag))
            return;
        ExtendedPlayer extendedPlayer = (ExtendedPlayer)instance;
        CompoundTag extTagCompound = (CompoundTag)nbt;
        extendedPlayer.readNBT(extTagCompound);
    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    @Override
    public Tag writeNBT(Capability<IExtendedPlayer> capability, IExtendedPlayer instance, Direction facing) {
        if(!(instance instanceof ExtendedPlayer))
            return null;
        ExtendedPlayer extendedPlayer = (ExtendedPlayer)instance;
        CompoundTag extTagCompound = new CompoundTag();
        extendedPlayer.writeNBT(extTagCompound);
        return extTagCompound;
    }
}
