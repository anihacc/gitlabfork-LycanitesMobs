package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.config.ConfigBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockFrostfire extends BlockFireBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public BlockFrostfire(Block.Properties properties) {
        super(properties, LycanitesMobs.modInfo, "frostfire");

        // Stats:
        this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = false;
        this.agingRate = 3;
        this.spreadChance = 1;
        this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Frostfire", true);
        this.removeOnNoFireTick = ConfigBase.getConfig(this.group, "general").getBool("Features", "Remove Frostfire on No Fire Tick", false);

        //this.setLightOpacity(1);
        //this.setLightLevel(0);
    }


    // ==================================================
    //                       Break
    // ==================================================
    /*@Override
    public Item getItemDropped(BlockState state, Random random, int zero) {
        return ObjectManager.getItem("icefirecharge");
    }*/


    // ==================================================
    //                        Fire
    // ==================================================
    @Override
    public boolean canCatchFire(IBlockReader world, BlockPos pos, Direction face) {
        Block block = world.getBlockState(pos).getBlock();
        if(block ==  Blocks.ICE || block == Blocks.PACKED_ICE)
            return true;
        return false;
    }

    @Override
    public boolean isBlockFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        if(state.getBlock() == Blocks.SNOW || state.getBlock() == Blocks.SNOW_BLOCK)
            return true;
        return false;
    }

    @Override
    public int getBlockFlammability(IBlockReader world, BlockPos pos, Direction face) {
        Block block = world.getBlockState(pos).getBlock();
        if(block ==  Blocks.ICE)
            return 20;
        return 0;
    }

    @Override
    protected boolean canDie(World world, BlockPos pos) {
        return false;
    }

    @Override
    public void burnBlockReplace(World world, BlockPos pos, int newFireAge) {
        if(world.getBlockState(pos).getBlock() == Blocks.ICE) {
            world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), 3);
            return;
        }
        super.burnBlockReplace(world, pos, newFireAge);
    }

    @Override
    public void burnBlockDestroy(World world, BlockPos pos) {
        if(world.getBlockState(pos).getBlock() == Blocks.ICE) {
            world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), 3);
            return;
        }
        super.burnBlockDestroy(world, pos);
    }


    // ==================================================
    //                Collision Effects
    // ==================================================
    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(blockState, world, pos, entity);

        if(entity instanceof LivingEntity) {
            EffectInstance effect = new EffectInstance(Effects.field_76421_d, 3 * 20, 0);
            LivingEntity entityLiving = (LivingEntity)entity;
            if(entityLiving.isPotionApplicable(effect))
                entityLiving.addPotionEffect(effect);
            else
                return; // Entities immune to slow are immune to frostfire damage.
        }

        if(entity instanceof ItemEntity)
            return;

        entity.attackEntityFrom(DamageSource.MAGIC, 2);
    }


    // ==================================================
    //                      Particles
    // ==================================================
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if(random.nextInt(24) == 0)
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("frostfire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        if (random.nextInt(100) == 0) {
            x = pos.getX() + random.nextFloat();
            z = pos.getZ() + random.nextFloat();
            world.addParticle(ParticleTypes.ITEM_SNOWBALL, x, y, z, 0.0D, 0.0D, 0.0D);
        }
        super.animateTick(state, world, pos, random);
    }
}