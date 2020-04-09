package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.capabilities.CapabilityProviderEntity;
import com.lycanitesmobs.core.capabilities.CapabilityProviderPlayer;
import com.lycanitesmobs.core.config.ConfigDebug;
import com.lycanitesmobs.core.config.ConfigExtra;
import com.lycanitesmobs.core.entity.*;
import com.lycanitesmobs.core.info.ItemConfig;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.network.MessagePlayerLeftClick;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GameEventListener {

    // ==================================================
    //                     Constructor
    // ==================================================
	public GameEventListener() {}


    // ==================================================
    //                    World Load
    // ==================================================
	@SubscribeEvent
	public void onWorldLoading(WorldEvent.Load event) {
		if(!(event.getWorld() instanceof World))
			return;

		// ========== Extended World ==========
		ExtendedWorld.getForWorld((World)event.getWorld());
	}


    // ==================================================
    //                Attach Capabilities
    // ==================================================
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof LivingEntity) {
            event.addCapability(new ResourceLocation(LycanitesMobs.MODID, "entity"), new CapabilityProviderEntity());
        }

        if(event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(LycanitesMobs.MODID, "player"), new CapabilityProviderPlayer());
        }
    }


    // ==================================================
    //                    Player Clone
    // ==================================================
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(event.getOriginal());
		if(extendedPlayer != null) {
			extendedPlayer.backupPlayer();
		}
    }


	// ==================================================
    //                Entity Constructing
    // ==================================================
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if(event.getEntity() == null || event.getEntity().getEntityWorld() == null || event.getEntity().getEntityWorld().isRemote)
			return;

        // ========== Force Remove Entity ==========
        if(!(event.getEntity() instanceof LivingEntity)) {
            if(ExtendedEntity.FORCE_REMOVE_ENTITY_IDS != null && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS.size() > 0) {
                LycanitesMobs.logDebug("ForceRemoveEntity", "Forced entity removal, checking: " + event.getEntity().getName());
                for(String forceRemoveID : ExtendedEntity.FORCE_REMOVE_ENTITY_IDS) {
                    if(forceRemoveID.equalsIgnoreCase(event.getEntity().getType().getRegistryName().toString())) {
                        event.getEntity().remove();
                        break;
                    }
                }
            }
        }
	}


	// ==================================================
    //                 Living Death Event
    // ==================================================
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if(entity == null) return;

		// ========== Extended Entity ==========
        ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
        if (extendedEntity != null)
            extendedEntity.onDeath();

		// ========== Extended Player ==========
		if(entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)entity;
            ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
            if(extendedPlayer != null)
			    extendedPlayer.onDeath();
		}
	}


	// ==================================================
	//                   Entity Update
	// ==================================================
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if(entity == null) return;

		// ========== Extended Entity ==========
		ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
		if(extendedEntity != null)
			extendedEntity.onUpdate();

		// ========== Extended Player ==========
		if(entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)entity;
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if(playerExt != null)
				playerExt.onUpdate();
		}
	}


	// ==================================================
	//                 Player Left Click
	// ==================================================
	@SubscribeEvent
	public void onPlayerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
		PlayerEntity player = event.getPlayer();
		if(player == null)
			return;

		ItemStack itemStack = player.getHeldItem(event.getHand());
		Item item = itemStack.getItem();
		if (item instanceof ItemEquipment) {
			MessagePlayerLeftClick message = new MessagePlayerLeftClick();
			LycanitesMobs.packetHandler.sendToServer(message);
		}
	}

	@SubscribeEvent
	public void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		PlayerEntity player = event.getPlayer();
		if(player == null || event.getSide().isClient())
			return;

		ItemStack itemStack = player.getHeldItem(event.getHand());
		Item item = itemStack.getItem();
		if (item instanceof ItemEquipment) {
			((ItemEquipment)item).onItemLeftClick(event.getWorld(), player, event.getHand());
		}
	}


    // ==================================================
    //               Entity Interact Event
    // ==================================================
	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		PlayerEntity player = event.getPlayer();
		Entity entity = event.getTarget();
        if(player == null || !(entity instanceof LivingEntity))
			return;

		/*ItemStack itemStack = player.getHeldItem(event.getHand());
		Item item = itemStack.getItem();
		if (item instanceof ItemBase) {
			if (item.itemInteractionForEntity(itemStack, player, (LivingEntity)entity, event.getHand())) {
				if (event.isCancelable())
					event.setCanceled(true);
			}
		}*/
	}


    // ==================================================
    //                 Attack Target Event
    // ==================================================
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onAttackTarget(LivingSetAttackTargetEvent event) {
		Entity targetEntity = event.getTarget();
		if(event.getEntityLiving() == null || targetEntity == null) {
			return;
		}

		// Better Invisibility:
		if(!event.getEntityLiving().isPotionActive(Effects.INVISIBILITY)) {
			if(targetEntity.isInvisible()) {
				if(event.isCancelable())
					event.setCanceled(true);
				//event.getEntityLiving().setRevengeTarget(null);
				return;
			}
		}

		// Can Be Targeted:
		if(event.getEntityLiving() instanceof MobEntity && targetEntity instanceof BaseCreatureEntity) {
			if(!((BaseCreatureEntity)targetEntity).canBeTargetedBy(event.getEntityLiving())) {
				//event.getEntityLiving().setRevengeTarget(null);
				if(event.isCancelable())
					event.setCanceled(true);
				//((MobEntity)event.getEntityLiving()).setAttackTarget(null);
			}
		}
	}


    // ==================================================
    //                 Living Hurt Event
    // ==================================================
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingHurt(LivingHurtEvent event) {
		if(event.isCanceled())
	      return;

		if(event.getSource() == null || event.getEntityLiving() == null)
			return;

        LivingEntity damagedEntity = event.getEntityLiving();
        ExtendedEntity damagedEntityExt = ExtendedEntity.getForEntity(damagedEntity);

		// True Source Extended Entity:
		EntityDamageSource entityDamageSource;
		if(event.getSource() instanceof EntityDamageSource) {
			entityDamageSource = (EntityDamageSource) event.getSource();
			if(entityDamageSource.getTrueSource() != null && entityDamageSource.getTrueSource() instanceof LivingEntity) {
				ExtendedEntity attackerExtendedEntity = ExtendedEntity.getForEntity((LivingEntity) entityDamageSource.getTrueSource());
				if(attackerExtendedEntity != null) {
					attackerExtendedEntity.setLastAttackedEntity(damagedEntity);
				}
			}
		}

		// ========== Mounted Protection ==========
		if(damagedEntity.getRidingEntity() != null) {
			if(damagedEntity.getRidingEntity() instanceof RideableCreatureEntity) {
				RideableCreatureEntity creatureRideable = (RideableCreatureEntity)event.getEntityLiving().getRidingEntity();

				// Shielding:
				if(creatureRideable.isBlocking()) {
					event.setAmount(0);
					event.setCanceled(true);
					return;
				}

				// Prevent Mounted Entities from Suffocating:
				if("inWall".equals(event.getSource().damageType)) {
					event.setAmount(0);
					event.setCanceled(true);
					return;
				}

				// Copy Mount Immunities to Rider:
				if(!creatureRideable.isVulnerableTo(event.getSource().damageType, event.getSource(), event.getAmount())) {
					event.setAmount(0);
					event.setCanceled(true);
					return;
				}
			}
		}

        // ========== Picked Up/Feared Protection ==========
        if(damagedEntityExt != null && damagedEntityExt.isPickedUp()) {
            // Prevent Picked Up and Feared Entities from Suffocating:
            if("inWall".equals(event.getSource().damageType)) {
                event.setAmount(0);
                event.setCanceled(true);
                return;
            }
        }
	}


	// ==================================================
    //                 Living Drops Event
    // ==================================================
	@SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
		World world = event.getEntityLiving().getEntityWorld();

		// Seasonal Items:
        if(ItemConfig.seasonalItemDropChance > 0
            && (Utilities.isHalloween() || Utilities.isYuletide() || Utilities.isNewYear())) {
            boolean noSeaonalDrop = false;
            boolean alwaysDrop = false;
            if(event.getEntityLiving() instanceof BaseCreatureEntity) {
                if (((BaseCreatureEntity) event.getEntityLiving()).isMinion())
                    noSeaonalDrop = true;
                if (((BaseCreatureEntity) event.getEntityLiving()).getSubspecies() != null)
                    alwaysDrop = true;
            }

            Item seasonalItem = null;
            if(Utilities.isHalloween())
                seasonalItem = ObjectManager.getItem("halloweentreat");
            if(Utilities.isYuletide()) {
                seasonalItem = ObjectManager.getItem("wintergift");
                if(Utilities.isYuletidePeak() && world.rand.nextBoolean())
                    seasonalItem = ObjectManager.getItem("wintergiftlarge");
            }

            if(seasonalItem != null && !noSeaonalDrop && (alwaysDrop || event.getEntityLiving().getRNG().nextFloat() < ItemConfig.seasonalItemDropChance)) {
                ItemStack dropStack = new ItemStack(seasonalItem, 1);
                CustomItemEntity entityItem = new CustomItemEntity(world, event.getEntityLiving().getPositionVec().getX(), event.getEntityLiving().getPositionVec().getY(), event.getEntityLiving().getPositionVec().getZ(), dropStack);
                entityItem.setPickupDelay(10);
                world.addEntity(entityItem);
            }
        }
	}
	
	
    // ==================================================
    //                 Bucket Fill Event
    // ==================================================
	@SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        World world = event.getWorld();
        RayTraceResult target = event.getTarget();
        if(target == null || !(target instanceof BlockRayTraceResult))
            return;
        BlockPos pos = ((BlockRayTraceResult)target).getPos();
        Block block = world.getBlockState(pos).getBlock();
        Item bucket = ObjectManager.buckets.get(block);
        if(bucket != null && world.getFluidState(pos).getLevel() == 0) {
            world.removeBlock(pos, true);
        }
        
        if(bucket == null)
        	return;

        event.setFilledBucket(new ItemStack(bucket));
        event.setResult(Event.Result.ALLOW);
    }


	// ==================================================
	//                 Break Block Event
	// ==================================================
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isRemote() || event.isCanceled()) {
			return;
		}

		if(event.getPlayer() != null && !event.getPlayer().isCreative()) {
			if (event.getWorld() instanceof World) {
				ExtendedWorld extendedWorld = ExtendedWorld.getForWorld((World) event.getWorld());
				if (!(event.getState().getBlock() instanceof BlockFireBase) && extendedWorld.isBossNearby(new Vec3d(event.getPos()))) {
					event.setCanceled(true);
					event.setResult(Event.Result.DENY);
					event.getPlayer().sendStatusMessage(new TranslationTextComponent("boss.block.protection.break"), true);
					return;
				}
			}
		}

		if(event.getPlayer() != null) {
			ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(event.getPlayer());
			if (extendedPlayer == null) {
				return;
			}
			extendedPlayer.setJustBrokenBlock(event.getState());
		}
	}


	// ==================================================
	//                 Block Place Event
	// ==================================================
	/** This uses the block place events to update Block Spawn Triggers. **/
	@SubscribeEvent
	public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isRemote() || event.isCanceled()) {
			return;
		}

		if(event.getEntity() instanceof PlayerEntity && !((PlayerEntity)event.getEntity()).isCreative()) {
			if (event.getWorld() instanceof World) {
				ExtendedWorld extendedWorld = ExtendedWorld.getForWorld((World) event.getWorld());
				if (extendedWorld.isBossNearby(new Vec3d(event.getPos()))) {
					event.setCanceled(true);
					event.setResult(Event.Result.DENY);
					((PlayerEntity)event.getEntity()).sendStatusMessage(new TranslationTextComponent("boss.block.protection.place"), true);
					return;
				}
			}
		}
	}


	// ==================================================
	//                   Check Spawn
	// ==================================================
	@SubscribeEvent
	public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
		if(event.isSpawner()) {
			LivingEntity entity = event.getEntityLiving();
			if(entity instanceof BaseCreatureEntity) {
				BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)entity;
				if(!baseCreatureEntity.checkSpawnGroupLimit(event.getWorld().getWorld(), event.getSpawner().getSpawnerPosition(), 16)) {
					event.setResult(Event.Result.DENY);
				}
			}
		}
	}


	// ==================================================
	//               Mounting / Dismounting
	// ==================================================
	@SubscribeEvent
	public void onEntityMount(EntityMountEvent event) {
		if(!ConfigExtra.INSTANCE.disableSneakDismount.get()) {
			return;
		}
		if(!event.isDismounting() || !(event.getEntityMounting() instanceof PlayerEntity) || !(event.getEntityBeingMounted() instanceof RideableCreatureEntity)) {
			return;
		}
		event.setCanceled(event.getEntityMounting().isShiftKeyDown()); //isSneaking()
	}


	// ==================================================
	//                 Debug Overlay
	// ==================================================
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onGameOverlay(RenderGameOverlayEvent.Text event) {
		if(!ConfigDebug.INSTANCE.creatureOverlay.get())
			return;

		// Entity:
		RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
		if(mouseOver instanceof EntityRayTraceResult) {
			Entity mouseOverEntity = ((EntityRayTraceResult)mouseOver).getEntity();
			if(mouseOverEntity instanceof BaseCreatureEntity) {
				BaseCreatureEntity mouseOverCreature = (BaseCreatureEntity)mouseOverEntity;
				event.getLeft().add("");
				event.getLeft().add("Target Creature: " + mouseOverCreature.getName().getFormattedText());
				event.getLeft().add("Distance To player: " + mouseOverCreature.getDistance(Minecraft.getInstance().player));
				event.getLeft().add("Elements: " + mouseOverCreature.creatureInfo.getElementNames(mouseOverCreature.getSubspecies()).getFormattedText());
				event.getLeft().add("Subspecies: " + mouseOverCreature.getSubspeciesIndex());
				event.getLeft().add("Variant: " + mouseOverCreature.getVariantIndex());
				event.getLeft().add("Level: " + mouseOverCreature.getLevel());
				event.getLeft().add("Experience: " + mouseOverCreature.getExperience() + "/" + mouseOverCreature.creatureStats.getExperienceForNextLevel());
				event.getLeft().add("Size: " + mouseOverCreature.sizeScale);
				event.getLeft().add("");
				event.getLeft().add("Health: " + mouseOverCreature.getHealth() + "/" + mouseOverCreature.getMaxHealth() + " Fresh: " + mouseOverCreature.creatureStats.getHealth());
				event.getLeft().add("Speed: " + mouseOverCreature.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue() + "/" + mouseOverCreature.creatureStats.getSpeed());
				event.getLeft().add("");
				event.getLeft().add("Defense: " + mouseOverCreature.creatureStats.getDefense());
				event.getLeft().add("Armor: " + mouseOverCreature.getTotalArmorValue());
				event.getLeft().add("");
				event.getLeft().add("Damage: " + mouseOverCreature.creatureStats.getDamage());
				event.getLeft().add("Melee Speed: " + mouseOverCreature.creatureStats.getAttackSpeed());
				event.getLeft().add("Melee Range: " + mouseOverCreature.getPhysicalRange());
				event.getLeft().add("Ranged Speed: " + mouseOverCreature.creatureStats.getRangedSpeed());
				event.getLeft().add("Pierce: " + mouseOverCreature.creatureStats.getPierce());
				event.getLeft().add("");
				event.getLeft().add("Effect Duration: " + mouseOverCreature.creatureStats.getEffect() + " Base Seconds");
				event.getLeft().add("Effect Amplifier: x" + mouseOverCreature.creatureStats.getAmplifier());
				event.getLeft().add("");
				event.getLeft().add("Has Attack Target: " + mouseOverCreature.hasAttackTarget());
				event.getLeft().add("Has Avoid Target: " + mouseOverCreature.hasAvoidTarget());
				event.getLeft().add("Has Master Target: " + mouseOverCreature.hasMaster());
				event.getLeft().add("Has Parent Target: " + mouseOverCreature.hasParent());
				if(mouseOverEntity instanceof TameableCreatureEntity) {
					TameableCreatureEntity mouseOverTameable = (TameableCreatureEntity)mouseOverCreature;
					event.getLeft().add("");
					event.getLeft().add("Owner ID: " + (mouseOverTameable.getOwnerId() != null ? mouseOverTameable.getOwnerId().toString() : "None"));
					event.getLeft().add("Owner Name: " + mouseOverTameable.getOwnerName().getFormattedText());
				}
			}
		}
	}
}
