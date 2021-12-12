package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.capabilities.CapabilityProviderEntity;
import com.lycanitesmobs.core.capabilities.CapabilityProviderPlayer;
import com.lycanitesmobs.core.config.ConfigExtra;
import com.lycanitesmobs.core.entity.*;
import com.lycanitesmobs.core.info.ItemConfig;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.network.MessagePlayerLeftClick;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
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
		if(!(event.getWorld() instanceof Level))
			return;

		// ========== Extended World ==========
		ExtendedWorld.getForWorld((Level)event.getWorld());
	}


    // ==================================================
    //                Attach Capabilities
    // ==================================================
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof LivingEntity) {
            event.addCapability(new ResourceLocation(LycanitesMobs.MODID, "entity"), new CapabilityProviderEntity());
        }

        if(event.getObject() instanceof Player) {
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
		if(event.getEntity() == null || event.getEntity().getCommandSenderWorld() == null || event.getEntity().getCommandSenderWorld().isClientSide)
			return;

        // ========== Force Remove Entity ==========
        if(!(event.getEntity() instanceof LivingEntity)) {
            if(ExtendedEntity.FORCE_REMOVE_ENTITY_IDS != null && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS.size() > 0) {
                LycanitesMobs.logDebug("ForceRemoveEntity", "Forced entity removal, checking: " + event.getEntity().getName());
                for(String forceRemoveID : ExtendedEntity.FORCE_REMOVE_ENTITY_IDS) {
                    if(forceRemoveID.equalsIgnoreCase(event.getEntity().getType().getRegistryName().toString())) {
                        event.getEntity().discard();
                        break;
                    }
                }
            }
        }
	}


	// ==================================================
	//                Entity Leave World
	// ==================================================
	@SubscribeEvent
	public void onEntityLeaveWorld(EntityLeaveWorldEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		ExtendedEntity extendedEntity = ExtendedEntity.getForEntity((LivingEntity)event.getEntity());
		if (extendedEntity != null) {
			extendedEntity.onEntityRemoved();
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
		if(entity instanceof Player) {
			Player player = (Player)entity;
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
		if(entity instanceof Player) {
			Player player = (Player)entity;
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if(playerExt != null)
				playerExt.onUpdate();
		}
	}


	// ==================================================
	//                    Player Click
	// ==================================================
	@SubscribeEvent
	public void onPlayerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
		Player player = event.getPlayer();
		if(player == null)
			return;

		ItemStack itemStack = player.getItemInHand(event.getHand());
		Item item = itemStack.getItem();
		if (item instanceof ItemEquipment) {
			MessagePlayerLeftClick message = new MessagePlayerLeftClick();
			LycanitesMobs.packetHandler.sendToServer(message);
		}
	}

	@SubscribeEvent
	public void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		Player player = event.getPlayer();
		if (player == null || event.getSide().isClient())
			return;

		ItemStack itemStack = player.getItemInHand(event.getHand());
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
		Player player = event.getPlayer();
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
		if(!event.getEntityLiving().hasEffect(MobEffects.INVISIBILITY)) {
			if(targetEntity.isInvisible()) {
				if(event.isCancelable())
					event.setCanceled(true);
				//event.getEntityLiving().setRevengeTarget(null);
				return;
			}
		}

		// Can Be Targeted:
		if(event.getEntityLiving() instanceof Mob && targetEntity instanceof BaseCreatureEntity) {
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
			if(entityDamageSource.getEntity() != null && entityDamageSource.getEntity() instanceof LivingEntity) {
				ExtendedEntity attackerExtendedEntity = ExtendedEntity.getForEntity((LivingEntity) entityDamageSource.getEntity());
				if(attackerExtendedEntity != null) {
					attackerExtendedEntity.setLastAttackedEntity(damagedEntity);
				}
			}
		}

		// ========== Mounted Protection ==========
		if(damagedEntity.getVehicle() != null) {
			if(damagedEntity.getVehicle() instanceof RideableCreatureEntity) {
				RideableCreatureEntity creatureRideable = (RideableCreatureEntity)event.getEntityLiving().getVehicle();

				// Shielding:
				if(creatureRideable.isBlocking()) {
					event.setAmount(0);
					event.setCanceled(true);
					return;
				}

				// Prevent Mounted Entities from Suffocating:
				if("inWall".equals(event.getSource().msgId)) {
					event.setAmount(0);
					event.setCanceled(true);
					return;
				}

				// Copy Mount Immunities to Rider:
				if(!creatureRideable.isVulnerableTo(event.getSource().msgId, event.getSource(), event.getAmount())) {
					event.setAmount(0);
					event.setCanceled(true);
					return;
				}
			}
		}

        // ========== Picked Up/Feared Protection ==========
        if(damagedEntityExt != null && damagedEntityExt.isPickedUp()) {
            // Prevent Picked Up and Feared Entities from Suffocating:
            if("inWall".equals(event.getSource().msgId)) {
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
		Level world = event.getEntityLiving().getCommandSenderWorld();

		// Seasonal Items:
        if(ItemConfig.seasonalItemDropChance > 0
            && (Utilities.isHalloween() || Utilities.isYuletide() || Utilities.isNewYear())) {
            boolean noSeaonalDrop = false;
            if(event.getEntityLiving() instanceof BaseCreatureEntity) {
                if (((BaseCreatureEntity) event.getEntityLiving()).isMinion())
                    noSeaonalDrop = true;
            }

            Item seasonalItem = null;
            if(Utilities.isHalloween())
                seasonalItem = ObjectManager.getItem("halloweentreat");
            if(Utilities.isYuletide()) {
                seasonalItem = ObjectManager.getItem("wintergift");
                if(Utilities.isYuletidePeak() && world.random.nextBoolean())
                    seasonalItem = ObjectManager.getItem("wintergiftlarge");
            }

            if(seasonalItem != null && !noSeaonalDrop event.getEntityLiving().getRandom().nextFloat() < ItemConfig.seasonalItemDropChance) {
                ItemStack dropStack = new ItemStack(seasonalItem, 1);
                CustomItemEntity entityItem = new CustomItemEntity(world, event.getEntityLiving().position().x(), event.getEntityLiving().position().y(), event.getEntityLiving().position().z(), dropStack);
                entityItem.setPickUpDelay(10);
                world.addFreshEntity(entityItem);
            }
        }
	}
	
	
    // ==================================================
    //                 Bucket Fill Event
    // ==================================================
	@SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        Level world = event.getWorld();
        HitResult target = event.getTarget();
        if(target == null || !(target instanceof BlockHitResult))
            return;
        BlockPos pos = ((BlockHitResult)target).getBlockPos();
        Block block = world.getBlockState(pos).getBlock();
        Item bucket = ObjectManager.buckets.get(block);
        if(bucket != null && world.getFluidState(pos).getAmount() == 0) {
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
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isClientSide() || event.isCanceled()) {
			return;
		}

		if(event.getPlayer() != null && !event.getPlayer().isCreative()) {
			if (event.getWorld() instanceof Level) {
				ExtendedWorld extendedWorld = ExtendedWorld.getForWorld((Level) event.getWorld());
				if (!(event.getState().getBlock() instanceof BlockFireBase) && extendedWorld.isBossNearby(Vec3.atLowerCornerOf(event.getPos()))) {
					event.setCanceled(true);
					event.setResult(Event.Result.DENY);
					event.getPlayer().displayClientMessage(new TranslatableComponent("boss.block.protection.break"), true);
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
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isClientSide() || event.isCanceled()) {
			return;
		}

		if(event.getEntity() instanceof Player && !((Player)event.getEntity()).isCreative()) {
			if (event.getWorld() instanceof Level) {
				ExtendedWorld extendedWorld = ExtendedWorld.getForWorld((Level) event.getWorld());
				if (extendedWorld.isBossNearby(Vec3.atLowerCornerOf(event.getPos()))) {
					event.setCanceled(true);
					event.setResult(Event.Result.DENY);
					((Player)event.getEntity()).displayClientMessage(new TranslatableComponent("boss.block.protection.place"), true);
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
		if (event.isSpawner() && event.getSpawner() != null && event.getSpawner().getSpawnerBlockEntity() != null) {
			LivingEntity entity = event.getEntityLiving();
			if (entity instanceof BaseCreatureEntity baseCreatureEntity && event.getWorld() instanceof Level) {
				if (!baseCreatureEntity.checkSpawnGroupLimit((Level) event.getWorld(), event.getSpawner().getSpawnerBlockEntity().getBlockPos(), 16)) {
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
		if(!ConfigExtra.INSTANCE.disableSneakDismount.get() || true) { // Disabled for now as cancelling this event doesn't work correctly for players atm.
			return;
		}
		if(!(event.getEntityMounting() instanceof Player)) {
			return;
		}

		// Override Sneak to Dismount for Lycanites Mobs:
		if (event.isDismounting() && event.getEntityBeingMounted() instanceof RideableCreatureEntity) {
			ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer((Player) event.getEntityMounting());
			if (extendedPlayer == null) {
				return;
			}
			event.setCanceled(event.getEntityMounting().isShiftKeyDown() && !extendedPlayer.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_DISMOUNT));
		}
	}


	// ==================================================
	//                 Projectile Impact
	// ==================================================
	@SubscribeEvent
	public void onProjectileImpact(ProjectileImpactEvent event) {
		Entity shooter = null;
		if (!(event.getRayTraceResult() instanceof EntityHitResult)) {
			return;
		}
		EntityHitResult entityRayTraceResult = (EntityHitResult)event.getRayTraceResult();
		Entity target = entityRayTraceResult.getEntity();
		if (!(target instanceof BaseCreatureEntity)) {
			return;
		}
		BaseCreatureEntity targetCreature = (BaseCreatureEntity)target;

		if (event.getEntity() instanceof Projectile) {
			Projectile projectileEntity = (Projectile)event.getEntity();
			shooter = projectileEntity.getOwner();
		}
		if (event.getEntity() instanceof ThrowableItemProjectile) {
			ThrowableItemProjectile projectileItemEntity = (ThrowableItemProjectile)event.getEntity();
			shooter = projectileItemEntity.getOwner();
		}

		if (shooter != null && !targetCreature.isVulnerableTo(shooter)) {
			event.setCanceled(true);
		}
	}
}
