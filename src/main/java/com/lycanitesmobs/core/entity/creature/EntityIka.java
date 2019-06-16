package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityIka extends EntityCreatureAgeable implements IAnimals, IGroupAnimal {

	EntityAIWander wanderAI;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityIka(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = false;

        this.babySpawnChance = 0.01D;
        this.canGrow = true;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this).setSink(true));
        this.field_70714_bg.addTask(1, new EntityAIAttackMelee(this).setLongMemory(false));
        this.field_70714_bg.addTask(2, new EntityAITempt(this).setItemList("Vegetables"));
        this.field_70714_bg.addTask(3, new EntityAIStayByWater(this));
        this.field_70714_bg.addTask(4, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.field_70714_bg.addTask(5, new EntityAIMate(this));
        this.field_70714_bg.addTask(6, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.wanderAI = new EntityAIWander(this);
        this.field_70714_bg.addTask(7, this.wanderAI);
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));
        this.field_70715_bh.addTask(1, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.field_70715_bh.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Wander Pause Rates:
        if(!this.getEntityWorld().isRemote) {
            if (this.isInWater())
                this.wanderAI.setPauseRate(20);
            else
                this.wanderAI.setPauseRate(0);
        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
	// ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        float waterSpeed = 1.0F;
        if(this.isInWater()) // Checks specifically just for water.
            waterSpeed = 2.0F;
        else if(this.waterContact()) // Checks for water, rain, etc.
            waterSpeed = 1.5F;

    	if(this.getHealth() > (this.getMaxHealth() / 2)) // Slower with shell.
    		return waterSpeed * 0.75F;
    	return waterSpeed;
    }
	
    // Pathing Weight:
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = this.getEntityWorld().getBlockState(pos);
        if(blockState.getBlock() == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(blockState.getBlock() == Blocks.FLOWING_WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * waterWeight;
        if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(pos))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }

    // ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(PlayerEntity player) { return true; }

    // ========== Can Be Tempted ==========
    @Override
    public boolean canBeTempted() {
        if(this.getAir() <= -100)
            return false;
        else return super.canBeTempted();
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    /** A multiplier that alters how much damage this mob receives from the given DamageSource, use for resistances and weaknesses. Note: The defense multiplier is handled before this. **/
    public float getDamageModifier(DamageSource damageSrc) {
    	if(this.getHealth() > (this.getMaxHealth() / 2)) // Stronger with shell.
    		return 0.25F;
    	return 1.0F;
    }


    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
    public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
        return new EntityIka(this.getEntityWorld());
    }

    // ========== Breeding Item ==========
    @Override
    public boolean isBreedingItem(ItemStack testStack) {
        if(this.getAir() <= -100)
            return false;
        return ObjectLists.inItemList("Vegetables", testStack);
    }
}