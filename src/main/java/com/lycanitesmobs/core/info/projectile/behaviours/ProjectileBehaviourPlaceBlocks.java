package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProjectileBehaviourPlaceBlocks extends ProjectileBehaviour {
	/** The name of the block to place. **/
	public String blockName;

	/** The chance of placing a block at each location. **/
	public double chance = 1;

	/** The radius of blocks placed. **/
	public int radius = 1;

	/** The height of blocks placed. **/
	public int height = 1;

	@Override
	public void loadFromJSON(JsonObject json) {
		this.blockName = json.get("block").getAsString();

		if(json.has("chance"))
			this.chance = json.get("chance").getAsDouble();

		if(json.has("radius"))
			this.radius = json.get("radius").getAsInt();

		if(json.has("height"))
			this.height = json.get("height").getAsInt();
	}

	@Override
	public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {
		Block block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(this.blockName));
		if(block == null) {
			return;
		}

		if(block == Blocks.WATER) {
			block = Blocks.FLOWING_WATER;
		}
		else if(block == Blocks.LAVA) {
			block = Blocks.FLOWING_LAVA;
		}

		IBlockState blockState = block.getDefaultState();
		if(block instanceof BlockDynamicLiquid || block instanceof BlockFluidBase) {
			blockState = blockState.withProperty(BlockLiquid.LEVEL, 2);
		}

		for(int x = -this.radius + 1; x < this.radius; x++) {
			for(int y = this.height - 1; y < this.height; y++) {
				for(int z = -this.radius + 1; z < this.radius; z++) {
					BlockPos placePos = pos.add(x, y, z);
					if(projectile.canDestroyBlock(placePos) && (this.chance >= 1 || this.chance >= world.rand.nextDouble())) {
						world.setBlockState(placePos, blockState);
					}
				}
			}
		}
	}
}
