package com.lycanitesmobs.core.capabilities;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;

public class ExtendedEntityStorage implements Capability.IStorage<IExtendedEntity> {

    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
    @Override
    public void readNBT(Capability<IExtendedEntity> capability, IExtendedEntity instance, Direction facing, Tag nbt) {
        if(!(instance instanceof ExtendedEntity) || !(nbt instanceof CompoundTag))
            return;
        ExtendedEntity extendedEntity = (ExtendedEntity)instance;
        CompoundTag extTagCompound = (CompoundTag)nbt;
        extendedEntity.readNBT(extTagCompound);
    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    @Override
    public Tag writeNBT(Capability<IExtendedEntity> capability, IExtendedEntity instance, Direction facing) {
        if(!(instance instanceof ExtendedEntity))
            return null;
        ExtendedEntity extendedEntity = (ExtendedEntity)instance;
        CompoundTag extTagCompound = new CompoundTag();
        extendedEntity.writeNBT(extTagCompound);
        return extTagCompound;
    }
}