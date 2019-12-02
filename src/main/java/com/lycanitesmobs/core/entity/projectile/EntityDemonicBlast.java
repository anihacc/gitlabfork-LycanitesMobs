package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityCacodemon;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityDemonicBlast extends BaseProjectileEntity {
	
	// Properties:
	public Entity shootingEntity;
	public int expireTime = 15;
	
	// Rapid Fire:
	private int rapidTicks = 0;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityDemonicBlast(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public EntityDemonicBlast(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity entityLivingBase) {
        super(entityType, world, entityLivingBase);
    }

    public EntityDemonicBlast(EntityType<? extends BaseProjectileEntity> entityType, World world, double x, double y, double z) {
        super(entityType, world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "demonicblast";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(10);
    	this.setProjectileScale(2.5F);
		this.ripper = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void tick() {
    	super.tick();

    	if(!this.getEntityWorld().isRemote) {
	    	if(rapidTicks % 5 == 0 && this.isAlive()) {
                for(int i = 0; i < 6; i++) {
                    fireProjectile();
                }
	    	}
	    	if(rapidTicks == Integer.MAX_VALUE)
	    		rapidTicks = -1;
	    	rapidTicks++;
    	}
    	
    	if(this.posY > this.getEntityWorld().getActualHeight() + 20)
    		this.remove();
    	
    	if(this.ticksExisted >= this.expireTime * 20)
    		this.remove();
    }
	
    
    // ==================================================
 	//                 Fire Projectile
 	// ==================================================
    public void fireProjectile() {
    	World world = this.getEntityWorld();
    	
		BaseProjectileEntity projectile;
		if(this.getThrower() != null) {
			projectile = new EntityDemonicSpark(ProjectileManager.getInstance().oldProjectileTypes.get(EntityDemonicSpark.class), world, this.getThrower());
			projectile.posX = this.posX;
			projectile.posY = this.posY;
			projectile.posZ = this.posZ;
		}
		else {
			projectile = new EntityDemonicSpark(ProjectileManager.getInstance().oldProjectileTypes.get(EntityDemonicSpark.class), world, this.posX, this.posY, this.posZ);
		}

		float velocity = 1.2F;
		double motionT = this.getMotion().getX() + this.getMotion().getY() + this.getMotion().getZ();
		if(this.getMotion().getX() < 0) motionT -= this.getMotion().getX() * 2;
		if(this.getMotion().getY() < 0) motionT -= this.getMotion().getY() * 2;
		if(this.getMotion().getZ() < 0) motionT -= this.getMotion().getZ() * 2;
        projectile.shoot(this.getMotion().getX() / motionT + (rand.nextGaussian() - 0.5D), this.getMotion().getY() / motionT + (rand.nextGaussian() - 0.5D), this.getMotion().getZ() / motionT + (rand.nextGaussian() - 0.5D), velocity, 0);

		this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));

		world.addEntity(projectile);
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.0001F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	entityLiving.addPotionEffect(new EffectInstance(Effects.WITHER, this.getEffectDuration(10), 0));
		if(ObjectManager.getEffect("decay") != null) {
			entityLiving.addPotionEffect(new EffectInstance(ObjectManager.getEffect("decay"), this.getEffectDuration(60), 0));
		}
    	return true;
    }
    
    //========== On Impact Splash/Ricochet ==========
    @Override
	public void onEntityCollision(Entity entity) {
    	if(this.getThrower() != null && this.getThrower().getRidingEntity() == entity) {
    		return;
		}
		if(this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
			int explosionRadius = 2;
			if (this.getThrower() != null && this.getThrower() instanceof BaseCreatureEntity) {
				BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity) this.getThrower();
				if(baseCreatureEntity instanceof EntityCacodemon && !((EntityCacodemon) baseCreatureEntity).griefing) {
					super.onImpactComplete(this.getPosition());
					return;
				}
				if(baseCreatureEntity.getOwner() == entity || baseCreatureEntity.getControllingPassenger() == entity) {
					super.onImpactComplete(this.getPosition());
					return;
				}
				if (baseCreatureEntity.getSubspeciesIndex() > 0) {
					explosionRadius += 2;
				}
				if (baseCreatureEntity.getSubspeciesIndex() > 2) {
					explosionRadius = 2;
				}
			}
			this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, Explosion.Mode.BREAK);
		}
    	for(int i = 0; i < 8; ++i) {
			fireProjectile();
		}
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().addParticle(RedstoneParticleData.REDSTONE_DUST, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound("DemonicBlast");
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
