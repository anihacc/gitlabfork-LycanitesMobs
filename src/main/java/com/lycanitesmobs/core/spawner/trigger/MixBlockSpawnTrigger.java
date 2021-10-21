package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MixBlockSpawnTrigger extends BlockSpawnTrigger {

	/** Constructor **/
	public MixBlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
	}

	@Override
	public int getBlockLevel(BlockState blockState, Level world, BlockPos blockPos) {
		return 0;
	}


	/** Called every time liquids mix to form a block. **/
	public void onMix(Level world, BlockState blockState, BlockPos mixPos) {
		// Check Block:
		if(!this.isTriggerBlock(blockState, world, mixPos, 0, null)) {
			return;
		}

		// Chance:
		if(this.chance < 1 && world.random.nextDouble() > this.chance) {
			return;
		}

		this.trigger(world, null, mixPos.above(), this.getBlockLevel(blockState, world, mixPos), 0);
	}
}
