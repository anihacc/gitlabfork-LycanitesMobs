package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.block.Blocks;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHellfireball extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireball(World world) {
        super(world);
    }

    public EntityHellfireball(World world, LivingEntity entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityHellfireball(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "hellfire";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(4);
    	this.setProjectileScale(2.5F);
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	if(!entityLiving.isImmuneToFire()) {
    		entityLiving.setFire(this.getEffectDuration(10) / 20);
        }
        if(ObjectManager.getEffect("decay") != null) {
            entityLiving.addPotionEffect(new EffectInstance(ObjectManager.getEffect("decay"), this.getEffectDuration(60), 0));
        }
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(BlockPos pos) {
    	return true;
    }

    public boolean canDestroyBlockSub(BlockPos pos) {
        Block block = this.getEntityWorld().getBlockState(pos).getBlock();
        if(block == Blocks.SNOW_LAYER)
            return true;
        if(block == Blocks.TALLGRASS)
            return true;
        if(block == Blocks.FIRE)
            return true;
        if(block == Blocks.WEB)
            return true;
        if(ObjectManager.getBlock("PoisonCloud") != null && block == ObjectManager.getBlock("PoisonCloud"))
            return true;
        if(ObjectManager.getBlock("PoopCloud") != null && block == ObjectManager.getBlock("PoopCloud"))
            return true;
        if(ObjectManager.getBlock("FrostCloud") != null && block == ObjectManager.getBlock("FrostCloud"))
            return true;
        if(ObjectManager.getBlock("Frostweb") != null && block == ObjectManager.getBlock("Frostweb"))
            return true;
        if(ObjectManager.getBlock("QuickWeb") != null && block == ObjectManager.getBlock("QuickWeb"))
            return true;
        if(ObjectManager.getBlock("Hellfire") != null && block == ObjectManager.getBlock("Hellfire"))
            return true;
        if(ObjectManager.getBlock("doomfire") != null && block == ObjectManager.getBlock("doomfire"))
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
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        BlockState placedBlockState = ObjectManager.getBlock("hellfire").getDefaultState();
        if(this.canDestroyBlockSub(new BlockPos(x, y, z)))
            world.setBlockState(new BlockPos(x, y, z), placedBlockState);
        if (this.canDestroyBlockSub(new BlockPos(x + 1, y, z)))
            world.setBlockState(new BlockPos(x + 1, y, z), placedBlockState);
        if(this.canDestroyBlockSub(new BlockPos(x - 1, y, z)))
            world.setBlockState(new BlockPos(x - 1, y, z), placedBlockState);
        if(this.canDestroyBlockSub(new BlockPos(x, y, z + 1)))
            world.setBlockState(new BlockPos(x, y, z + 1), placedBlockState);
        if(this.canDestroyBlockSub(new BlockPos(x, y, z - 1)))
            world.setBlockState(new BlockPos(x, y, z - 1), placedBlockState);
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("hellfireball");
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
