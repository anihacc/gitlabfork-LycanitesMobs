package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.creature.EntityRahovart;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHellfireOrb extends BaseProjectileEntity {

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireOrb(World par1World) {
        super(par1World);
    }

    public EntityHellfireOrb(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityHellfireOrb(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "hellfireorb";
    	this.modInfo = LycanitesMobs.modInfo;
    	this.setDamage(0);
    	this.setProjectileScale(2F);
        this.movement = false;
        this.ripper = true;
        this.pierceBlocks = true;
        this.projectileLife = 5;
        this.animationFrameMax = 4;
        this.noClip = true;
    }

    @Override
    public boolean isBurning() { return false; }


    // ==================================================
    //                      Update
    // ==================================================
    @Override
    public void onUpdate() {
        super.onUpdate();
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
    	return AssetManager.getSound("hellfirewall");
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
}
