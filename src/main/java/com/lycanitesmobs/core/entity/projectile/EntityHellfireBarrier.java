package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.creature.EntityRahovart;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHellfireBarrier extends EntityProjectileBase {

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
    public EntityHellfireBarrier(World par1World) {
        super(par1World);
    }

    public EntityHellfireBarrier(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityHellfireBarrier(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "hellfirebarrier";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(0);
    	this.setProjectileScale(0F);
        this.setSize(2F, 2F);
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
    public void onUpdate() {
        if(this.getEntityWorld().isRemote)
            return;

        // Time Update:
        if(this.time++ >= this.timeMax)
            this.setDead();

        // Populate:
        if(this.hellfireWalls == null) {
            hellfireWalls = new EntityHellfireWall[this.hellfireHeight][this.hellfireWidth];
            for(int row = 0; row < this.hellfireHeight; row++) {
                for(int col = 0; col < this.hellfireWidth; col++) {
                    if(this.getThrower() != null) {
                        if(this.wall) {
                            hellfireWalls[row][col] = new EntityHellfireWall(this.getEntityWorld(), this.getThrower());
                        }
                        else {
                            hellfireWalls[row][col] = new EntityHellfireBarrierPart(this.getEntityWorld(), this.getThrower());
                        }
                    }
                    else {
                        if(this.wall) {
                            hellfireWalls[row][col] = new EntityHellfireWall(this.getEntityWorld(), this.posX, this.posY + (this.hellfireSize * row), this.posZ);
                        }
                        else {
                            hellfireWalls[row][col] = new EntityHellfireBarrierPart(this.getEntityWorld(), this.posX, this.posY + (this.hellfireSize * row), this.posZ);
                        }
                    }

                    double rotationRadians = Math.toRadians(this.rotation);
                    double x = (((float)col / this.hellfireWidth) * (this.hellfireSize * (this.hellfireWidth - 1))) * Math.cos(rotationRadians) + Math.sin(rotationRadians);
                    double z = (((float)col / this.hellfireWidth) * (this.hellfireSize * (this.hellfireWidth - 1))) * Math.sin(rotationRadians) - Math.cos(rotationRadians);
                    hellfireWalls[row][col].posX = this.posX + x;
                    hellfireWalls[row][col].posY = this.posY + (this.hellfireSize * row);
                    hellfireWalls[row][col].posZ = this.posZ + z;
                    hellfireWalls[row][col].projectileLife = 2 * 20;

                    this.getEntityWorld().spawnEntity(hellfireWalls[row][col]);
                    hellfireWalls[row][col].setProjectileScale(this.hellfireSize * 2.5F);
                }
            }
        }

        // Move:
        for(int row = 0; row < this.hellfireHeight; row++) {
            for(int col = 0; col < this.hellfireWidth; col++) {

                double rotationRadians = Math.toRadians(this.rotation);
                double x = (((float)col / this.hellfireWidth) * (this.hellfireSize * (this.hellfireWidth - 1))) * Math.cos(rotationRadians) + Math.sin(rotationRadians);
                double z = (((float)col / this.hellfireWidth) * (this.hellfireSize * (this.hellfireWidth - 1))) * Math.sin(rotationRadians) - Math.cos(rotationRadians);
                hellfireWalls[row][col].posX = this.posX + x;
                hellfireWalls[row][col].posY = this.posY + (this.hellfireSize * row);
                hellfireWalls[row][col].posZ = this.posZ + z;
                hellfireWalls[row][col].projectileLife = 2 * 20;

                if(this.isDead)
                    hellfireWalls[row][col].setDead();
            }
        }
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean onEntityLivingDamage(EntityLivingBase entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(10) / 20);
    	return true;
    }

    //========== Do Damage Check ==========
    public boolean canDamage(EntityLivingBase targetEntity) {
        EntityLivingBase owner = this.getThrower();
        if(owner == null) {
            if(targetEntity instanceof EntityRahovart)
                return false;
            if(targetEntity instanceof IGroupDemon)
                return false;
        }
        return super.canDamage(targetEntity);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("hellfirewave");
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public ResourceLocation getTexture() {
        if(AssetManager.getTexture("hellfirewall") == null)
            AssetManager.addTexture("hellfirewall", this.modInfo, "textures/items/hellfirewall" + ".png");
        return AssetManager.getTexture("hellfirewall");
    }
}
