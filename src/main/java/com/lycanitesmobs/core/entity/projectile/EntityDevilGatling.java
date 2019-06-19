package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class EntityDevilGatling extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;
	public int expireTime = 15;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityDevilGatling(EntityType<? extends EntityProjectileBase> entityType, World world) {
        super(entityType, world);
    }

    public EntityDevilGatling(EntityType<? extends EntityProjectileBase> entityType, World world, LivingEntity entityLivingBase) {
        super(entityType, world, entityLivingBase);
    }

    public EntityDevilGatling(EntityType<? extends EntityProjectileBase> entityType, World world, double x, double y, double z) {
        super(entityType, world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "devilgatling";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(4);
		this.ripper = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void tick() {
    	super.tick();

    	if(this.posY > this.getEntityWorld().getActualHeight() + 20)
    		this.remove();
    	
    	if(this.ticksExisted >= this.expireTime * 20)
    		this.remove();
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public void onDamage(LivingEntity target, float damage, boolean attackSuccess) {
    	super.onDamage(target, damage, attackSuccess);

        // Remove Buffs:
        if(this.rand.nextBoolean()) {
            List<Effect> goodEffects = new ArrayList<>();
            for (Object potionEffectObj : target.getActivePotionEffects()) {
                if (potionEffectObj instanceof EffectInstance) {
                    Effect effect = ((EffectInstance) potionEffectObj).getPotion();
                    if (effect != null) {
                        if (ObjectLists.inEffectList("buffs", effect))
                            goodEffects.add(effect);
                    }
                }
            }
            if (!goodEffects.isEmpty()) {
                if (goodEffects.size() > 1)
                    target.removePotionEffect(goodEffects.get(this.rand.nextInt(goodEffects.size())));
                else
                    target.removePotionEffect(goodEffects.get(0));
            }
        }

		if(ObjectManager.getEffect("decay") != null) {
			target.addPotionEffect(new EffectInstance(ObjectManager.getEffect("decay"), this.getEffectDuration(60), 0));
		}
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().addParticle(ParticleTypes.EXPLOSION, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound("devilgatling");
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
