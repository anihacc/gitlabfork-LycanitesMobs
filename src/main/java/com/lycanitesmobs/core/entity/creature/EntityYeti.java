package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupIce;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityYeti extends EntityCreatureAgeable implements IAnimals, IGroupAnimal, IGroupIce {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityYeti(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(1, new EntityAIAttackMelee(this).setLongMemory(false));
        this.field_70714_bg.addTask(2, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.field_70714_bg.addTask(3, new EntityAIMate(this));
        this.field_70714_bg.addTask(4, new EntityAITempt(this).setItemList("Vegetables"));
        this.field_70714_bg.addTask(5, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.field_70714_bg.addTask(6, new EntityAIWander(this));
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
        
        // Trail:
        if(!this.getEntityWorld().isRemote && (this.ticksExisted % 10 == 0 || this.isMoving() && this.ticksExisted % 5 == 0)) {
            int trailHeight = 2;
            if(this.isChild())
                trailHeight = 1;
            for(int y = 0; y < trailHeight; y++) {
                Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
                if(block == Blocks.AIR || block == Blocks.SNOW || block == ObjectManager.getBlock("frostcloud"))
                    this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), ObjectManager.getBlock("frostcloud").getDefaultState());
            }
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().spawnParticle(EnumParticleTypes.SNOW_SHOVEL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        BlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
        if(block != Blocks.AIR) {
            if(blockState.getMaterial() == Material.GRASS || blockState.getMaterial() == Material.SNOW)
                return 10F;
            if(blockState.getMaterial() == Material.GROUND || blockState.getMaterial() == Material.ICE)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
	    return true;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("ooze")) return false;
        return super.isInvulnerableTo(type, source, damage);
    }

    @Override
    public boolean isPotionApplicable(EffectInstance potionEffect) {
        if(potionEffect.getPotion() == MobEffects.SLOWNESS) return false;
        if(potionEffect.getPotion() == MobEffects.HUNGER) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityYeti(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("Vegetables", testStack);
    }
	
    
	// ==================================================
  	//                     Interact
  	// ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(PlayerEntity player, ItemStack itemStack) {
    	HashMap<Integer, String> commands = new HashMap<Integer, String>();
    	commands.putAll(super.getInteractCommands(player, itemStack));
    	
    	if(itemStack != null) {
    		// Milk:
    		if(itemStack.getItem() == Items.BUCKET)
    			commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Milk");
    	}
    	
    	return commands;
    }
    
    // ========== Perform Command ==========
    @Override
    public void performCommand(String command, PlayerEntity player, ItemStack itemStack) {
    	
    	// Milk:
    	if(command.equals("Milk")) {
    		this.replacePlayersItem(player, itemStack, new ItemStack(Items.MILK_BUCKET));
    	}
    	
    	super.performCommand(command, player, itemStack);
    }
}
