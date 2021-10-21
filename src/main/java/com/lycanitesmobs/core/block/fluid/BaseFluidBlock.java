package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.ElementInfo;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class BaseFluidBlock extends LiquidBlock {
	public String blockName;
	protected ElementInfo element;
	protected boolean destroyItems = true;

	public BaseFluidBlock(Supplier<? extends FlowingFluid> fluidSupplier, Properties properties, String name, ElementInfo element, boolean destroyItems) {
        super(fluidSupplier, properties);
        this.setRegistryName(LycanitesMobs.MODID, name);
        this.blockName = name;
        this.element = element;
        this.destroyItems = destroyItems;
	}

	public ElementInfo getElement() {
		return this.element;
	}

	@Override
    public void neighborChanged(BlockState blockState, Level world, BlockPos blockPos, Block neighborBlock, BlockPos neighborBlockPos, boolean someBoolean) {
	    super.neighborChanged(blockState, world, blockPos, neighborBlock, neighborBlockPos, someBoolean);
		if (neighborBlock == this) {
	        return;
        }
        BlockState neighborBlockState = world.getBlockState(neighborBlockPos);
		if (neighborBlockState.getBlock() == this) {
		    return;
        }
        if (this.shouldSpreadLiquid(world, neighborBlockPos, blockState)) {
            world.getLiquidTicks().scheduleTick(blockPos, blockState.getFluidState().getType(), this.getFluid().getTickDelay(world));
        }
    }

    public boolean shouldSpreadLiquid(Level world, BlockPos neighborBlockPos, BlockState blockState) {
        BlockState neighborBlockState = world.getBlockState(neighborBlockPos);
        if (neighborBlockState.getMaterial().isLiquid()) {
            return false;
        }
        return true;
    }

    @Override
    public void entityInside(BlockState blockState, Level world, BlockPos pos, Entity entity) {
		if (this.destroyItems && (entity instanceof ItemEntity || entity instanceof ExperienceOrb)) {
			entity.kill();
		}
        super.entityInside(blockState, world, pos, entity);
    }

	/** Client side animation and sounds. **/
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		if(random.nextInt(52) == 0) {
			world.playLocalSound(x + 0.5D, y + 0.5D, z + 0.5D, ObjectManager.getSound(this.blockName), SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
		}
		super.animateTick(state, world, pos, random);
    }
}
