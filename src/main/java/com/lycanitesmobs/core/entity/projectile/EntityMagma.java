package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMagma extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityMagma(EntityType<? extends EntityProjectileBase> entityType, World world) {
        super(entityType, world);
    }

    public EntityMagma(EntityType<? extends EntityProjectileBase> entityType, World world, LivingEntity entityLivingBase) {
        super(entityType, world, entityLivingBase);
    }

    public EntityMagma(EntityType<? extends EntityProjectileBase> entityType, World world, double x, double y, double z) {
        super(entityType, world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "magma";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(6);
    	this.setProjectileScale(4F);
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(5) / 20);
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(BlockPos pos) {
    	return true;
    }
    
    public boolean canDestroyBlockSub(BlockPos pos) {
        Block block = this.getEntityWorld().getBlockState(pos).getBlock();
        if(block == Blocks.SNOW)
            return true;
        if(block == Blocks.TALL_GRASS)
            return true;
        if(block == Blocks.FIRE)
            return true;
        if(block == Blocks.COBWEB)
            return true;
        if(ObjectManager.getBlock("PoisonCloud") != null && block == ObjectManager.getBlock("PoisonCloud"))
            return true;
        if(ObjectManager.getBlock("FrostCloud") != null && block == ObjectManager.getBlock("FrostCloud"))
            return true;
        if(ObjectManager.getBlock("PoopCloud") != null && block == ObjectManager.getBlock("PoopCloud"))
            return true;
        if(ObjectManager.getBlock("Frostweb") != null && block == ObjectManager.getBlock("Frostweb"))
            return true;
        if(ObjectManager.getBlock("QuickWeb") != null && block == ObjectManager.getBlock("QuickWeb"))
            return true;
        if(ObjectManager.getBlock("Hellfire") != null && block == ObjectManager.getBlock("Hellfire"))
            return true;
        if(ObjectManager.getBlock("Frostfire") != null && block == ObjectManager.getBlock("Frostfire"))
            return true;
        if(ObjectManager.getBlock("Icefire") != null && block == ObjectManager.getBlock("Icefire"))
            return true;
        if(ObjectManager.getBlock("Scorchfire") != null && block == ObjectManager.getBlock("Scorchfire"))
            return true;
        return super.canDestroyBlock(pos);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, BlockPos pos) {
        BlockState placedBlockBig = Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, 4);
        BlockState placedBlock = Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, 5);
        if(this.canDestroyBlockSub(pos))
            world.setBlockState(pos, placedBlockBig, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())))
            world.setBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()), placedBlock, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())))
            world.setBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()), placedBlock, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)))
            world.setBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1), placedBlock, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)))
            world.setBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1), placedBlock, 3);
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    		this.getEntityWorld().addParticle(ParticleTypes.LARGE_SMOKE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    	}
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
