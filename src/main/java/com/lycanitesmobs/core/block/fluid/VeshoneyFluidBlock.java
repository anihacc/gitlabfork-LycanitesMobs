package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.core.entity.creature.Vespid;
import com.lycanitesmobs.core.entity.creature.VespidQueen;
import com.lycanitesmobs.core.info.ElementInfo;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Supplier;

public class VeshoneyFluidBlock extends BaseFluidBlock {
    public VeshoneyFluidBlock(Supplier<? extends FlowingFluid> fluidSupplier, Properties properties, String name, ElementInfo element, boolean destroyItems) {
        super(fluidSupplier, properties, name, element, destroyItems);
    }

    public boolean shouldSpreadLiquid(Level world, BlockPos neighborBlockPos, BlockState blockState) {
        BlockState neighborBlockState = world.getBlockState(neighborBlockPos);

        // Water Dirt:
        if (neighborBlockState.getMaterial() == Material.WATER) {
            world.setBlock(neighborBlockPos, Blocks.DIRT.defaultBlockState(), 4);
            return false;
        }

        // Lava Gravel:
        if (neighborBlockState.getMaterial() == Material.LAVA) {
            world.setBlock(neighborBlockPos, Blocks.COBBLESTONE.defaultBlockState(), 4);
            return false;
        }

        return super.shouldSpreadLiquid(world, neighborBlockPos, blockState);
    }

    @Override
    public void entityInside(BlockState blockState, Level world, BlockPos pos, Entity entity) {
        // Extinguish:
        if(entity.isOnFire())
            entity.clearFire();

        // Effects:
        if(entity instanceof LivingEntity && !(entity instanceof Vespid) && !(entity instanceof VespidQueen)) {
            if (!(entity instanceof Player) || !((Player)entity).isCreative() || !entity.isSpectator()) {
                entity.makeStuckInBlock(blockState, new Vec3(0.3D, 0.6D, 0.3D));
                entity.setDeltaMovement(0, -0.02, 0);
            }
        }

        super.entityInside(blockState, world, pos, entity);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        float f;
        float f1;
        float f2;

        if (random.nextInt(100) == 0) {
            f = (float)pos.getX() + random.nextFloat();
            f1 = (float)pos.getY() + random.nextFloat() * 0.5F;
            f2 = (float)pos.getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.RAIN, (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
        }
        super.animateTick(state, world, pos, random);
    }
}
