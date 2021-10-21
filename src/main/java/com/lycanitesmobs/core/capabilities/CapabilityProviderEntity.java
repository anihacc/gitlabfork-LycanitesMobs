package com.lycanitesmobs.core.capabilities;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityProviderEntity implements ICapabilitySerializable<CompoundTag> {
	public LazyOptional<IExtendedEntity> instance;

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability != LycanitesMobs.EXTENDED_ENTITY)
			return LazyOptional.empty();
		if(this.instance == null) {
			this.instance = LazyOptional.of(LycanitesMobs.EXTENDED_ENTITY::getDefaultInstance);
		}
		return this.instance.cast();
	}

	@Override
	public CompoundTag serializeNBT() {
		return (CompoundTag) LycanitesMobs.EXTENDED_ENTITY.getStorage().writeNBT(LycanitesMobs.EXTENDED_ENTITY, this.getCapability(LycanitesMobs.EXTENDED_ENTITY, null).orElse(null), null);
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		LycanitesMobs.EXTENDED_ENTITY.getStorage().readNBT(LycanitesMobs.EXTENDED_ENTITY, this.getCapability(LycanitesMobs.EXTENDED_ENTITY, null).orElse(null), null, nbt);
	}
}
