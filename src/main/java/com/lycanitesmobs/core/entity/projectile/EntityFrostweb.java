package com.lycanitesmobs.core.entity.projectile;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.block.Blocks;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFrostweb extends EntityProjectileBase {
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityFrostweb(World world) {
        super(world);
    }

    public EntityFrostweb(World world, LivingEntity entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityFrostweb(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "frostweb";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(2);
        this.knockbackChance = 0D;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(LivingEntity entityLiving) {
        entityLiving.addPotionEffect(new EffectInstance(Effects.SLOWNESS, this.getEffectDuration(2), 0));
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(BlockPos pos) {
        Block block = this.getEntityWorld().getBlockState(pos).getBlock();
        if(block == Blocks.SNOW)
            return true;
        if(block == Blocks.TALL_GRASS)
            return true;
        if(block == Blocks.COBWEB)
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
	   	 world.setBlockState(pos, ObjectManager.getBlock("Frostweb").getDefaultState());
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
            this.getEntityWorld().addParticle(ParticleTypes.ITEM_SNOWBALL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
}
