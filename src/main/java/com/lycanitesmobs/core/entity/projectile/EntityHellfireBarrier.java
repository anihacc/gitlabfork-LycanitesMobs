package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityRahovart;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

public class EntityHellfireBarrier extends BaseProjectileEntity {

	// Properties:
    public EntityHellfireWall[][] hellfireWalls;
    protected int hellfireWidth = 5;
    protected int hellfireHeight = 3;
    protected int hellfireSize = 10;
    public boolean wall = false;
    public int time = 0;
    public int timeMax = 2 * 20;
    public float angle = 90;
    public double rotation = 0;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireBarrier(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public EntityHellfireBarrier(EntityType<? extends BaseProjectileEntity> entityType, World world, LivingEntity par2LivingEntity) {
        super(entityType, world, par2LivingEntity);
    }

    public EntityHellfireBarrier(EntityType<? extends BaseProjectileEntity> entityType, World world, double par2, double par4, double par6) {
        super(entityType, world, par2, par4, par6);
    }
    
    // ========== Setup Projectile ==========
    public void setup() { // Size 2F
    	this.entityName = "hellfirebarrier";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(0);
    	this.setProjectileScale(0F);
        this.movement = false;
        this.ripper = true;
        this.pierceBlocks = true;
        this.projectileLife = 5 * 20;
        this.animationFrameMax = 59;
        this.noClip = true;
    }

    @Override
    public boolean isBurning() { return false; }


    // ==================================================
    //                      Update
    // ==================================================
    @Override
    public void tick() {
        if(this.getEntityWorld().isRemote)
            return;

        // Time Update:
        if(this.time++ >= this.timeMax)
            this.remove();

        // Populate:
        if(this.hellfireWalls == null) {
            hellfireWalls = new EntityHellfireWall[this.hellfireHeight][this.hellfireWidth];
            for(int row = 0; row < this.hellfireHeight; row++) {
                for(int col = 0; col < this.hellfireWidth; col++) {
                    if(this.getThrower() != null) {
                        if(this.wall) {
                            hellfireWalls[row][col] = new EntityHellfireWall(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireWall.class), this.getEntityWorld(), this.getThrower());
                        }
                        else {
                            hellfireWalls[row][col] = new EntityHellfireBarrierPart(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireBarrierPart.class), this.getEntityWorld(), this.getThrower());
                        }
                    }
                    else {
                        if(this.wall) {
                            hellfireWalls[row][col] = new EntityHellfireWall(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireWall.class), this.getEntityWorld(), this.getPositionVec().getX(), this.getPositionVec().getY() + (this.hellfireSize * row), this.getPositionVec().getZ());
                        }
                        else {
                            hellfireWalls[row][col] = new EntityHellfireBarrierPart(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellfireBarrierPart.class), this.getEntityWorld(), this.getPositionVec().getX(), this.getPositionVec().getY() + (this.hellfireSize * row), this.getPositionVec().getZ());
                        }
                    }

                    double rotationRadians = Math.toRadians(this.rotation);
                    double x = (((float)col / this.hellfireWidth) * (this.hellfireSize * (this.hellfireWidth - 1))) * Math.cos(rotationRadians) + Math.sin(rotationRadians);
                    double z = (((float)col / this.hellfireWidth) * (this.hellfireSize * (this.hellfireWidth - 1))) * Math.sin(rotationRadians) - Math.cos(rotationRadians);
                    this.hellfireWalls[row][col].setPosition(
                            this.getPositionVec().getX() + x,
                            this.getPositionVec().getY() + (this.hellfireSize * row),
                            this.getPositionVec().getZ() + z
                    );
                    this.hellfireWalls[row][col].projectileLife = 2 * 20;

                    this.getEntityWorld().addEntity(hellfireWalls[row][col]);
                    this.hellfireWalls[row][col].setProjectileScale(this.hellfireSize * 2.5F);
                }
            }
        }

        // Move:
        for(int row = 0; row < this.hellfireHeight; row++) {
            for(int col = 0; col < this.hellfireWidth; col++) {

                double rotationRadians = Math.toRadians(this.rotation);
                double x = (((float)col / this.hellfireWidth) * (this.hellfireSize * (this.hellfireWidth - 1))) * Math.cos(rotationRadians) + Math.sin(rotationRadians);
                double z = (((float)col / this.hellfireWidth) * (this.hellfireSize * (this.hellfireWidth - 1))) * Math.sin(rotationRadians) - Math.cos(rotationRadians);
                this.hellfireWalls[row][col].setPosition(
                        this.getPositionVec().getX() + x,
                        this.getPositionVec().getY() + (this.hellfireSize * row),
                        this.getPositionVec().getZ() + z
                );
                this.hellfireWalls[row][col].projectileLife = 2 * 20;

                if(!this.isAlive())
                    this.hellfireWalls[row][col].remove();
            }
        }
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(LivingEntity entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(10) / 20);
    	return true;
    }

    //========== Do Damage Check ==========
    public boolean canDamage(LivingEntity targetEntity) {
        LivingEntity owner = this.getThrower();
        if(owner == null) {
            if(targetEntity instanceof EntityRahovart)
                return false;
        }
        return super.canDamage(targetEntity);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return ObjectManager.getSound("hellfirewave");
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @Environment(EnvType.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public Identifier getTexture() {
        if(TextureManager.getTexture("hellfirewall") == null)
            TextureManager.addTexture("hellfirewall", this.modInfo, "textures/items/hellfirewall" + ".png");
        return TextureManager.getTexture("hellfirewall");
    }
}
