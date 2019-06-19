package com.lycanitesmobs;

import com.lycanitesmobs.core.capabilities.CapabilityProviderEntity;
import com.lycanitesmobs.core.capabilities.CapabilityProviderPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.info.ItemConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {

    // ==================================================
    //                     Constructor
    // ==================================================
	public EventListener() {}


    // ==================================================
    //                  Registry Events
    // ==================================================
    // ========== Blocks ==========
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ObjectManager.registerBlocks(event);
    }

    // ========== Items ==========
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        ObjectManager.registerItems(event);
    }

    // ========== Effects ==========
    @SubscribeEvent
    public void registerEffects(RegistryEvent.Register<Effect> event) {
        ObjectManager.registerEffects(event);
    }

	// ========== Entities ==========
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		ObjectManager.registerSpecialEntities(event);
	}

	// ========== Sound Events ==========
	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		ObjectManager.registerSounds(event);
	}


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
            event.addCapability(new ResourceLocation(LycanitesMobs.modid, "entity"), new CapabilityProviderEntity());
        }

        if(event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(LycanitesMobs.modid, "player"), new CapabilityProviderPlayer());
        }
    }


    // ==================================================
    //                    Player Clone
    // ==================================================
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(event.getOriginal());
        if(extendedPlayer != null)
            extendedPlayer.backupPlayer();
    }


	// ==================================================
    //                Entity Constructing
    // ==================================================
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if(event.getEntity() == null || event.getEntity().getEntityWorld().isRemote)
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
    //               Entity Interact Event
    // ==================================================
	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		PlayerEntity player = event.getEntityPlayer();
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
				event.getEntityLiving().setRevengeTarget(null);
				return;
			}
		}

		// Can Be Targeted:
		if(event.getEntityLiving() instanceof MobEntity && targetEntity instanceof EntityCreatureBase) {
			if(!((EntityCreatureBase)targetEntity).canBeTargetedBy(event.getEntityLiving())) {
				event.getEntityLiving().setRevengeTarget(null);
				if(event.isCancelable())
					event.setCanceled(true);
				((MobEntity)event.getEntityLiving()).setAttackTarget(null);
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

        EntityDamageSource entityDamageSource = null;
        if(event.getSource() instanceof EntityDamageSource)
            entityDamageSource = (EntityDamageSource)event.getSource();

//        Entity damagingEntity = null;
//        if(entityDamageSource != null)
//            damagingEntity = entityDamageSource.getSourceOfDamage();

		// ========== Mounted Protection ==========
		if(damagedEntity.getRidingEntity() != null) {
			if(damagedEntity.getRidingEntity() instanceof EntityCreatureRideable) {

				// Prevent Mounted Entities from Suffocating:
				if("inWall".equals(event.getSource().damageType)) {
					event.setAmount(0);
					event.setCanceled(true);
					return;
				}

				// Copy Mount Immunities to Rider:
				EntityCreatureRideable creatureRideable = (EntityCreatureRideable)event.getEntityLiving().getRidingEntity();
				if(!creatureRideable.isInvulnerableTo(event.getSource().damageType, event.getSource(), event.getAmount())) {
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
            if(event.getEntityLiving() instanceof EntityCreatureBase) {
                if (((EntityCreatureBase) event.getEntityLiving()).isMinion())
                    noSeaonalDrop = true;
                if (((EntityCreatureBase) event.getEntityLiving()).getSubspecies() != null)
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
                EntityItemCustom entityItem = new EntityItemCustom(world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, dropStack);
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
		if(event.getState() == null || event.getWorld() == null || event.getPlayer() == null || event.getWorld().isRemote() || event.isCanceled()) {
			return;
		}
		ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(event.getPlayer());
		if(extendedPlayer == null) {
			return;
		}
		extendedPlayer.setJustBrokenBlock(event.getState());
	}


	// ==================================================
	//                 Debug Overlay
	// ==================================================
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onGameOverlay(RenderGameOverlayEvent.Text event) {
		// Entity:
		RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
		if(mouseOver instanceof EntityRayTraceResult) {
			Entity mouseOverEntity = ((EntityRayTraceResult)mouseOver).getEntity();
			if(mouseOverEntity instanceof EntityCreatureBase) {
				EntityCreatureBase mouseOverCreature = (EntityCreatureBase)mouseOverEntity;
				event.getLeft().add("");
				event.getLeft().add("Target Creature: " + mouseOverCreature.getName());
				event.getLeft().add("Distance To player: " + mouseOverCreature.getDistance(Minecraft.getInstance().player));
				event.getLeft().add("Elements: " + mouseOverCreature.creatureInfo.getElementNames());
				event.getLeft().add("Subspecies: " + mouseOverCreature.getSubspeciesIndex());
				event.getLeft().add("Level: " + mouseOverCreature.getLevel());
				event.getLeft().add("Size: " + mouseOverCreature.sizeScale);
				event.getLeft().add("");
				event.getLeft().add("Health: " + mouseOverCreature.getHealth() + "/" + mouseOverCreature.getMaxHealth() + " Fresh: " + mouseOverCreature.creatureStats.getHealth());
				event.getLeft().add("Speed: " + mouseOverCreature.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue() + "/" + mouseOverCreature.creatureStats.getSpeed());
				event.getLeft().add("");
				event.getLeft().add("Defense: " + mouseOverCreature.creatureStats.getDefense());
				event.getLeft().add("Armor: " + mouseOverCreature.creatureStats.getArmor());
				event.getLeft().add("");
				event.getLeft().add("Damage: " + mouseOverCreature.creatureStats.getDamage());
				event.getLeft().add("Melee Speed: " + mouseOverCreature.creatureStats.getAttackSpeed());
				event.getLeft().add("Melee Range: " + mouseOverCreature.getMeleeAttackRange());
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
				if(mouseOverEntity instanceof EntityCreatureTameable) {
					EntityCreatureTameable mouseOverTameable = (EntityCreatureTameable)mouseOverCreature;
					event.getLeft().add("");
					event.getLeft().add("Owner ID: " + (mouseOverTameable.getOwnerId() != null ? mouseOverTameable.getOwnerId().toString() : "None"));
					event.getLeft().add("Owner Name: " + mouseOverTameable.getOwnerName());
				}
			}
		}
	}
}
