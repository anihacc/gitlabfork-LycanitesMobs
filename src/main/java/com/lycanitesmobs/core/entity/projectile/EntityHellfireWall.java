package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.creature.EntityRahovart;
import com.lycanitesmobs.core.info.ObjectLists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityHellfireWall extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireWall(World par1World) {
        super(par1World);
    }

    public EntityHellfireWall(World par1World, LivingEntity par2LivingEntity) {
        super(par1World, par2LivingEntity);
    }

    public EntityHellfireWall(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "hellfirewall";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(10);
    	this.setProjectileScale(20F);
        this.setSize(10F, 10F);
        this.movement = false;
        this.ripper = true;
        this.pierceBlocks = true;
        this.projectileLife = 2 * 20;
        this.animationFrameMax = 59;
        this.textureTiling = 2;
        this.noClip = true;
        this.waterProof = true;
        this.lavaProof = true;
    }

    @Override
    public boolean isBurning() { return false; }


    // ==================================================
    //                      Update
    // ==================================================
    @Override
    public void tick() {
    	super.tick();

        Vec3d vec3 = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec31 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult movingobjectposition = this.getEntityWorld().rayTraceBlocks(vec3, vec31);

        if (!this.getEntityWorld().isRemote) {
            List list = this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            LivingEntity entitylivingbase = this.getThrower();

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = (Entity)list.get(j);
                this.onImpact(new RayTraceResult(entity));
            }
        }
    }

    @Override
    public boolean handleWaterMovement() {
        return false;
    }

    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(10) / 20);
    	return true;
    }

    //========== Do Damage Check ==========
    @Override
    public boolean canDamage(LivingEntity targetEntity) {
        LivingEntity owner = this.getThrower();
        if(owner == null) {
            if(targetEntity instanceof EntityRahovart)
                return false;
            if(targetEntity instanceof IGroupDemon)
                return false;
        }
        return super.canDamage(targetEntity);
    }

    //========== On Damage ==========
    @Override
    public void onDamage(LivingEntity target, float damage, boolean attackSuccess) {

        // Remove Good Potion Effects:
        for(EffectInstance potionEffect : target.getActiveEffectInstances().toArray(new EffectInstance[target.getActiveEffectInstances().size()])) {
            if(ObjectLists.inEffectList("buffs", potionEffect.getPotion()))
                target.removeEffectInstance(potionEffect.getPotion());
        }

        boolean obliterate = true;
        if(target instanceof PlayerEntity)
            obliterate = false;
        else if(target instanceof EntityTameable) {
            obliterate = !(((EntityTameable)target).getOwner() instanceof PlayerEntity);
        }
        else if(target instanceof EntityCreatureTameable) {
            obliterate = !(((EntityCreatureTameable)target).getOwner() instanceof PlayerEntity);
        }
        if(target instanceof IGroupBoss)
            obliterate = false;
        if(target instanceof IEntityOwnable) {
            obliterate = ((IEntityOwnable)target).getOwner() == null;
        }
        if(obliterate)
            target.setHealth(0);
        super.onDamage(target, damage, attackSuccess);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("hellfirewall");
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