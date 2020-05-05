package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ParticleEntity extends ThrowableEntity {
	// Particle:
	public int particleAge = 0;
	public int particleAgeMax = 20;
	public double particleGravity = 0D;
	public String texture;
	public ModInfo group;
	
    // ==================================================
    //                      Constructor
    // ==================================================
	public ParticleEntity(World world, double x, double y, double z, String texture, ModInfo group) {
		super(EntityType.SNOWBALL, world);
		this.setPosition(x, y, z);
        this.lastTickPosX = x;
        this.lastTickPosY = y;
        this.lastTickPosZ = z;
		this.texture = texture;
		this.group = group;
	}

	@Override
	public void registerData() {}
	
	
    // ==================================================
    //                       Update
    // ==================================================
	@Override
    public void tick() {
		System.out.println("Doing something!");
        this.prevPosX = this.getPositionVec().getX();
        this.prevPosY = this.getPositionVec().getY();
        this.prevPosZ = this.getPositionVec().getZ();

        if(this.particleAge++ >= this.particleAgeMax)
            this.remove();

        /*this.motionY -= 0.04D * (double)this.particleGravity;
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if(this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }*/
    }

	
    // ==================================================
    //                    Interaction
    // ==================================================
	public boolean canAttackWithItem() {
        return false;
    }
	
	protected boolean canTriggerWalking() {
        return false;
    }
	
	@Override
	protected void onImpact(RayTraceResult rayTraceResult) {
		return;
	}
	
	
    // ==================================================
    //                        NBT
    // ==================================================
	@Override
	public void writeAdditional(CompoundNBT par1NBTTagCompound) {}

	@Override
    public void readAdditional(CompoundNBT par1NBTTagCompound) {}
	
	
    // ==================================================
    //                      Visuals
    // ==================================================
    public Identifier getTexture() {
    	if(TextureManager.getTexture(this.texture) == null)
    		TextureManager.addTexture(this.texture, this.group, "textures/particles/" + this.texture.toLowerCase() + ".png");
    	return TextureManager.getTexture(this.texture);
    }
}
