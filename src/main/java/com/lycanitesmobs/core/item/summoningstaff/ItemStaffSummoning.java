package com.lycanitesmobs.core.item.summoningstaff;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityPortal;
import com.lycanitesmobs.core.gui.beastiary.GuiBeastiarySummoning;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemStaffSummoning extends ItemBase {
	protected float damageScale = 1.0F;
	protected int weaponFlash = 0;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemStaffSummoning(Item.Properties properties, String itemName, String textureName) {
        super(properties);
        this.itemName = itemName;
        this.setup();

        this.addPropertyOverride(new ResourceLocation("using"), new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            public float call(ItemStack itemStack, World world, LivingEntity entity) {
                return entity != null && entity.isHandActive() && entity.getActiveItemStack() == itemStack ? 1.0F : 0.0F;
            }
        });
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    public void damageItemCharged(ItemStack itemStack, LivingEntity entity, float power) {
		ExtendedPlayer playerExt = null;
		if(entity instanceof PlayerEntity) {
			playerExt = ExtendedPlayer.getForPlayer((PlayerEntity)entity);
		}
    	if(playerExt != null && playerExt.staffPortal != null) {
            this.damage_item(itemStack, playerExt.staffPortal.summonAmount, (ServerPlayerEntity)entity);
    	}
    }

	// ========== Prevent Swing ==========
	@Override
	public boolean onEntitySwing(ItemStack itemStack, LivingEntity entity) {
		if(entity instanceof PlayerEntity) {
			entity.setActiveHand(Hand.MAIN_HAND);
			return true;
		}
		return super.onEntitySwing(itemStack, entity);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack itemStack, PlayerEntity player, Entity entity) {
		return true;
	}

	// ========== Start ==========
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt != null) {
			// Summon Selected Mob:
			SummonSet summonSet = playerExt.getSelectedSummonSet();
			if(summonSet.isUseable()) {
				if(!player.getEntityWorld().isRemote) {
					playerExt.staffPortal = new EntityPortal((EntityType<? extends EntityPortal>)ProjectileManager.getInstance().oldProjectileTypes.get(EntityPortal.class), world, player, summonSet.getCreatureClass(), this);
					playerExt.staffPortal.setLocationAndAngles(player.posX, player.posY, player.posZ, world.rand.nextFloat() * 360.0F, 0.0F);
					world.addEntity(playerExt.staffPortal);
				}
			}
			// Open Minion GUI If None Selected:
			else {
				playerExt.staffPortal = null;
				if(!player.getEntityWorld().isRemote)
					playerExt.sendAllSummonSetsToPlayer();
				if(player.getEntityWorld().isRemote)
					GuiBeastiarySummoning.openToPlayer(player);
			}
		}
		player.setActiveHand(hand);
		return new ActionResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	// ========== Using ==========
	@Override
	public void onUsingTick(ItemStack itemStack, LivingEntity entity, int useRemaining) {
		if(itemStack == null || entity == null || entity.getEntityWorld() == null)
			return;
		int useTime = this.getUseDuration(itemStack) - useRemaining;
		if(useTime >= this.getRapidTime(itemStack)) {
			int rapidRemainder = useTime % this.getRapidTime(itemStack);
			if(rapidRemainder == 0 && entity.getEntityWorld() != null) {
				if(this.rapidAttack(itemStack, entity.getEntityWorld(), entity)) {
					this.weaponFlash = Math.max(20, this.getRapidTime(itemStack));
				}
			}
		}
		if(useTime >= this.getChargeTime(itemStack))
			this.weaponFlash = Math.max(20, this.getChargeTime(itemStack));

		super.onUsingTick(itemStack, entity, useRemaining);
	}

	// ========== Stop ==========
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		int useTime = this.getUseDuration(stack) - timeLeft;
		float power = (float)useTime / (float)this.getChargeTime(stack);

		this.weaponFlash = 0;

		if((double)power < 0.1D)
			return;
		if(power > 1.0F)
			power = 1.0F;

		if(this.chargedAttack(stack, worldIn, entityLiving, power)) {
			this.damageItemCharged(stack, entityLiving, power);
			this.weaponFlash = Math.min(20, this.getChargeTime(stack));
		}

		ExtendedPlayer playerExt = null;
		if(entityLiving instanceof PlayerEntity) {
			playerExt = ExtendedPlayer.getForPlayer((PlayerEntity)entityLiving);
		}
		if(playerExt != null) {
			playerExt.staffPortal = null;
		}
	}

	// ========== Animation ==========
	@Override
	public UseAction getUseAction(ItemStack itemStack) {
		return UseAction.BOW;
	}

	// ========== Max Use Duration ==========
	@Override
	public int getUseDuration(ItemStack itemStack) {
		return 72000;
	}
    
    // ========== Charge Time ==========
    public int getChargeTime(ItemStack itemStack) {
        return 1;
    }
    
    // ========== Rapid Time ==========
    public int getRapidTime(ItemStack itemStack) {
        return 20;
    }
    
    // ========== Summon Cost ==========
    public int getSummonCostBoost() {
    	return 0;
    }
    public float getSummonCostMod() {
    	return 1.0F;
    }
    
    // ========== Summon Duration ==========
    public int getSummonDuration() {
    	return 60 * 20;
    }
    
    // ========== Summon Amount ==========
    public int getSummonAmount() {
    	return 1;
    }
    
    // ========== Additional Costs ==========
    public boolean getAdditionalCosts(PlayerEntity player) {
    	return true;
    }
    
    // ========== Minion Behaviour ==========
    public void applyMinionBehaviour(EntityCreatureTameable minion, PlayerEntity player) {
    	SummonSet summonSet = ExtendedPlayer.getForPlayer(player).getSelectedSummonSet();
        summonSet.applyBehaviour(minion);
        minion.applySubspecies(summonSet.subspecies);
    }
    
    // ========== Minion Effects ==========
    public void applyMinionEffects(EntityCreatureBase minion) {}
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    // ========== Rapid ==========
    public boolean rapidAttack(ItemStack itemStack, World world, LivingEntity entity) {
    	return false;
    }

    protected void damage_item(ItemStack itemStack, int amountToDamage, ServerPlayerEntity entity) {
        itemStack.attemptDamageItem(amountToDamage, entity.getRNG(), entity);
        if (itemStack.getCount() == 0) {
            if (entity.getHeldItem(Hand.MAIN_HAND).equals(itemStack)) {
                entity.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
            } else if (entity.getHeldItem(Hand.OFF_HAND).equals(itemStack)) {
                entity.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
            }
        }
    }
    
    // ========== Charged ==========
    public boolean chargedAttack(ItemStack itemStack, World world, LivingEntity entity, float power) {
    	ExtendedPlayer playerExt = null;
    	if(entity instanceof PlayerEntity) {
			playerExt = ExtendedPlayer.getForPlayer((PlayerEntity)entity);
		}
    	if(playerExt != null && playerExt.staffPortal != null) {
			int successCount = playerExt.staffPortal.summonCreatures();
            this.damage_item(itemStack, successCount, (ServerPlayerEntity)entity);
			return successCount > 0;
		}
		return false;
    }
    
	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
    	if(repairStack.getItem() == Items.GOLD_INGOT) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
