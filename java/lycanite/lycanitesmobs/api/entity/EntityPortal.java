package lycanite.lycanitesmobs.api.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.PlayerControlHandler;
import lycanite.lycanitesmobs.Utilities;
import lycanite.lycanitesmobs.api.item.ItemSummoningStaff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityPortal extends EntityProjectileBase {
	// Summoning Portal:
	private double targetX;
	private double targetY;
	private double targetZ;
	public int summonAmount = 0;
	public int summonTick = 0;
	
	// Properties:
	public EntityPlayer shootingEntity;
	public ItemSummoningStaff portalItem;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityPortal(World world) {
        super(world);
        this.setStats();
    }

    public EntityPortal(World world, EntityPlayer shooter, ItemSummoningStaff portalItem) {
        super(world, shooter);
        this.shootingEntity = shooter;
        this.portalItem = portalItem;
        this.setStats();
    }
    
    public void setStats() {
    	this.entityName = "SummoningPortal";
        this.setProjectileScale(4F);
        this.moveToTarget();
    }
    
    
    // ==================================================
 	//                     Updates
 	// ==================================================
    // ========== Main Update ==========
    @Override
    public void onUpdate() {
    	// Move:
    	if(!this.worldObj.isRemote && !this.isDead && (
    			this.shootingEntity == null || this.portalItem == null
    			|| this.shootingEntity.getItemInUse() == null
    			|| this.shootingEntity.getItemInUse().getItem() != this.portalItem)) {
    		this.setDead();
    		return;
    	}
    	
    	this.moveToTarget();
    	
    	// Client:
    	if(this.worldObj.isRemote) {
    		int summonFocus = PlayerControlHandler.getPlayerSummonFocus(this.shootingEntity);
    		int summonCost = PlayerControlHandler.summonFocusCharge;
    		if(summonFocus >= summonCost || this.shootingEntity.capabilities.isCreativeMode) {
	    		for(int i = 0; i < 32; ++i) {
	        		this.worldObj.spawnParticle("portal",
	        				this.posX + (4.0F * this.rand.nextFloat()) - 2.0F,
	        				this.posY + (4.0F * this.rand.nextFloat()) - 2.0F,
	        				this.posZ + (4.0F * this.rand.nextFloat()) - 2.0F,
	        				0.0D, 0.0D, 0.0D);
	    		}
    		}
    		return;
    	}
    	
    	// Summon:
    	if(this.summonTick % this.portalItem.getRapidTime(null) == 0) {
    		int summonFocus = PlayerControlHandler.getPlayerSummonFocus(this.shootingEntity);
    		int summonCost = PlayerControlHandler.summonFocusCharge * this.portalItem.getSummonCost();
    		if(summonFocus >= summonCost || this.shootingEntity.capabilities.isCreativeMode) {
    			PlayerControlHandler.setPlayerSummonFocus(this.shootingEntity, summonFocus - summonCost);
    			this.summonAmount++;
    		}
    	}
    	this.summonTick++;
    }
    
    
    // ==================================================
  	//                 Summon Creatures
  	// ==================================================
    public boolean summonCreatures() {
    	if(this.worldObj.isRemote)
    		return true;
    	for(int i = 0; i < this.summonAmount; i++) {
	    	EntityCreatureTameable entity = this.portalItem.getSummonEntity(this.worldObj);
	    	if(entity == null)
	    		return false;
	    	entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rand.nextFloat() * 360.0F, 0.0F);
	    	entity.setTemporary(this.portalItem.getSummonDuration());
	    	entity.setPlayerOwner(this.shootingEntity);
	    	this.worldObj.spawnEntityInWorld(entity);
    	}
    	return this.summonAmount > 0;
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
    
    // ========== Move to Target ==========
    public void moveToTarget() {
    	if(this.shootingEntity != null) {
    		double[] facingPos = this.getFacingPosition(this.shootingEntity, 2.0D);
	        this.targetX = facingPos[0];
	        this.targetY = facingPos[1] + 2.0F;
	        this.targetZ = facingPos[2];
	        MovingObjectPosition target = Utilities.raytrace(this.worldObj, this.posX, this.posY, this.posZ, this.targetX, this.targetY, this.targetZ, 1.0F, null);

	    	this.posX = this.targetX;
	    	this.posY = this.targetY;
	    	this.posZ = this.targetZ;
        }
    }
    
    // ========== Get Coord Behind ==========
    /** Returns the XYZ coordinate in front or behind this entity (using rotation angle) this entity with the given distance, use a negative distance for behind. **/
    public double[] getFacingPosition(Entity entity, double distance) {
    	double angle = Math.toRadians(this.rotationYaw);
    	double xAmount = -Math.sin(angle);
    	double zAmount = Math.cos(angle);
    	double[] coords = new double[3];
        coords[0] = entity.posX + (distance * xAmount);
        coords[1] = entity.posY;
        coords[2] = entity.posZ + (distance * zAmount);
        return coords;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    @Override
    protected void onImpact(MovingObjectPosition movingObjectPos) {}
    
    
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	if(AssetManager.getTexture(this.entityName) == null)
     		AssetManager.addTexture(this.entityName, LycanitesMobs.domain, "textures/particles/" + this.entityName.toLowerCase() + ".png");
     	return AssetManager.getTexture(this.entityName);
    }
}