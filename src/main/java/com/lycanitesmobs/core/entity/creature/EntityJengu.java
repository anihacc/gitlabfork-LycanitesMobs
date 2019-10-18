package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.projectile.EntityAquaPulse;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityJengu extends TameableCreatureEntity implements IMob, IFusable {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityJengu(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.spawnsInWater = true;
        this.hasAttackSound = false;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(5.0F));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_DROP, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
                this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile(EntityAquaPulse.class, target, range, 0, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }


    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }


    // ==================================================
    //                     Interact
    // ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(EntityPlayer player, ItemStack itemStack) {
        HashMap<Integer, String> commands = new HashMap<>();
        commands.putAll(super.getInteractCommands(player, itemStack));

        if(itemStack != null) {
            // Water:
            if(itemStack.getItem() == Items.BUCKET && this.isTamed())
                commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Water");
        }

        return commands;
    }

    // ========== Perform Command ==========
    @Override
    public boolean performCommand(String command, EntityPlayer player, ItemStack itemStack) {

        // Water:
        if(command.equals("Water")) {
            this.replacePlayersItem(player, itemStack, new ItemStack(Items.WATER_BUCKET));
            return true;
        }

        return super.performCommand(command, player, itemStack);
    }


    // ==================================================
    //                      Fusion
    // ==================================================
    protected IFusable fusionTarget;

    @Override
    public IFusable getFusionTarget() {
        return this.fusionTarget;
    }

    @Override
    public void setFusionTarget(IFusable fusionTarget) {
        this.fusionTarget = fusionTarget;
    }

    @Override
    public Class getFusionClass(IFusable fusable) {
        if(fusable instanceof EntityCinder) {
            return CreatureManager.getInstance().getEntityClass("xaphan");
        }
        if(fusable instanceof EntityGeonach) {
            return CreatureManager.getInstance().getEntityClass("spriggan");
        }
        if(fusable instanceof EntityDjinn) {
            return CreatureManager.getInstance().getEntityClass("reiver");
        }
        if(fusable instanceof EntityAegis) {
            return CreatureManager.getInstance().getEntityClass("nymph");
        }
        if(fusable instanceof EntityArgus) {
            return CreatureManager.getInstance().getEntityClass("eechetik");
        }
        return null;
    }
}
