package com.lycanitesmobs;

import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.entity.*;
import com.lycanitesmobs.core.info.ItemConfig;
import com.lycanitesmobs.core.info.ItemManager;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.network.MessagePlayerLeftClick;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
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
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class GameEventListener {
		WorldEventListener worldEventListener;

    // ==================================================
    //                     Constructor
    // ==================================================
	public GameEventListener() {
		this.worldEventListener = new WorldEventListener(this);
	}


    // ==================================================
    //                  Registry Events
    // ==================================================
    // ========== Blocks ==========
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        ObjectManager.registerBlocks(event);
		ItemManager.getInstance().registerBlockOres();
    }

    // ========== Items ==========
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        ObjectManager.registerItems(event);
        ItemManager.getInstance().registerItemOres();
    }

    // ========== Potions ==========
    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        ObjectManager.registerPotions(event);
    }

	// ========== Entities ==========
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		ObjectManager.registerSpecialEntities(event);
	}


    // ==================================================
    //                    World Load
    // ==================================================
	@SubscribeEvent
	public void onWorldLoading(WorldEvent.Load event) {
		if(event.getWorld() == null)
			return;

		// ========== Extended World ==========
		ExtendedWorld.getForWorld(event.getWorld());
		event.getWorld().addEventListener(this.worldEventListener);
	}

	@SubscribeEvent
	public void onWorldUnloading(WorldEvent.Unload event) {
		ExtendedWorld.loadedExtWorlds.remove(event.getWorld());
	}


    // ==================================================
    //                Attach Capabilities
    // ==================================================
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof EntityLivingBase) {
            event.addCapability(new ResourceLocation(LycanitesMobs.modid, "IExtendedEntity"), new ICapabilitySerializable<NBTTagCompound>() {
                private final ExtendedEntity instance = LycanitesMobs.EXTENDED_ENTITY.getDefaultInstance();

                @Override
                public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                    return capability == LycanitesMobs.EXTENDED_ENTITY;
                }

                @Override
                public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                    return capability == LycanitesMobs.EXTENDED_ENTITY ? LycanitesMobs.EXTENDED_ENTITY.<T>cast(this.instance) : null;
                }

                @Override
                public NBTTagCompound serializeNBT() {
                    return (NBTTagCompound) LycanitesMobs.EXTENDED_ENTITY.getStorage().writeNBT(LycanitesMobs.EXTENDED_ENTITY, this.instance, null);
                }

                @Override
                public void deserializeNBT(NBTTagCompound nbt) {
                    LycanitesMobs.EXTENDED_ENTITY.getStorage().readNBT(LycanitesMobs.EXTENDED_ENTITY, this.instance, null, nbt);
                }
            });
        }

        if(event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(LycanitesMobs.modid, "IExtendedPlayer"), new ICapabilitySerializable<NBTTagCompound>() {
                private final ExtendedPlayer instance = LycanitesMobs.EXTENDED_PLAYER.getDefaultInstance();

                @Override
                public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                    return capability == LycanitesMobs.EXTENDED_PLAYER;
                }

                @Override
                public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                    return capability == LycanitesMobs.EXTENDED_PLAYER ? LycanitesMobs.EXTENDED_PLAYER.<T>cast(this.instance) : null;
                }

                @Override
                public NBTTagCompound serializeNBT() {
                    return (NBTTagCompound) LycanitesMobs.EXTENDED_PLAYER.getStorage().writeNBT(LycanitesMobs.EXTENDED_PLAYER, this.instance, null);
                }

                @Override
                public void deserializeNBT(NBTTagCompound nbt) {
                    LycanitesMobs.EXTENDED_PLAYER.getStorage().readNBT(LycanitesMobs.EXTENDED_PLAYER, this.instance, null, nbt);
                }
            });
        }
    }


    // ==================================================
    //                    Player Clone
    // ==================================================
	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event) {
		ExtendedPlayer extendedPlayerOld = ExtendedPlayer.getForPlayer(event.getOriginal());
		ExtendedPlayer extendedPlayerNew = ExtendedPlayer.getForPlayer(event.getEntityPlayer());
		NBTTagCompound nbt = new NBTTagCompound();
		extendedPlayerOld.writeNBT(nbt);
		extendedPlayerNew.readNBT(nbt);
	}


	// ==================================================
    //                Entity Constructing
    // ==================================================
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if(event.getEntity() == null || event.getEntity().getEntityWorld() == null || event.getEntity().getEntityWorld().isRemote)
			return;

        // ========== Force Remove Entity ==========
        if(!(event.getEntity() instanceof EntityLivingBase)) {
            if(ExtendedEntity.FORCE_REMOVE_ENTITY_IDS != null && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS.length > 0) {
                LycanitesMobs.logDebug("ForceRemoveEntity", "Forced entity removal, checking: " + event.getEntity().getName());
                for(String forceRemoveID : ExtendedEntity.FORCE_REMOVE_ENTITY_IDS) {
                    if(forceRemoveID.equalsIgnoreCase(event.getEntity().getName())) {
                        event.getEntity().setDead();
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
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null) return;

		// ========== Extended Entity ==========
        ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
        if (extendedEntity != null)
            extendedEntity.onDeath();

		// ========== Extended Player ==========
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
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
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null) return;

		// ========== Extended Entity ==========
		ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
		if(extendedEntity != null)
			extendedEntity.onUpdate();

		// ========== Extended Player ==========
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
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
		EntityPlayer player = event.getEntityPlayer();
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
		EntityPlayer player = event.getEntityPlayer();
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
		EntityPlayer player = event.getEntityPlayer();
		Entity entity = event.getTarget();
        if(player == null || entity == null)
			return;

        if (player.getHeldItem(event.getHand()) != null) {
            ItemStack itemStack = player.getHeldItem(event.getHand());
            Item item = itemStack.getItem();
            if (item instanceof ItemBase) {
				if (((ItemBase) item).onItemRightClickOnEntity(player, entity, itemStack)) {
					if (event.isCancelable())
						event.setCanceled(true);
				}
			}
        }
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
		if(!event.getEntityLiving().isPotionActive(MobEffects.NIGHT_VISION)) {
			if(targetEntity.isInvisible()) {
				if(event.isCancelable())
					event.setCanceled(true);
				//event.getEntityLiving().setRevengeTarget(null);
				return;
			}
		}

		// Can Be Targeted:
		if(event.getEntityLiving() instanceof EntityLiving && targetEntity instanceof BaseCreatureEntity) {
			if(!((BaseCreatureEntity)targetEntity).canBeTargetedBy(event.getEntityLiving())) {
				//event.getEntityLiving().setRevengeTarget(null);
				if(event.isCancelable())
					event.setCanceled(true);
				//((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
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

        EntityLivingBase damagedEntity = event.getEntityLiving();
        ExtendedEntity damagedEntityExt = ExtendedEntity.getForEntity(damagedEntity);

        // True Source Extended Entity:
        EntityDamageSource entityDamageSource;
        if(event.getSource() instanceof EntityDamageSource) {
			entityDamageSource = (EntityDamageSource) event.getSource();
			if(entityDamageSource.getTrueSource() != null && entityDamageSource.getTrueSource() instanceof EntityLivingBase) {
				ExtendedEntity attackerExtendedEntity = ExtendedEntity.getForEntity((EntityLivingBase)entityDamageSource.getTrueSource());
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
				if(!creatureRideable.isDamageTypeApplicable(event.getSource().damageType, event.getSource(), event.getAmount())) {
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
            if(event.getEntityLiving() instanceof BaseCreatureEntity) {
                if (((BaseCreatureEntity) event.getEntityLiving()).isMinion())
                    noSeaonalDrop = true;
            }

            Item seasonalItem = null;
            if(Utilities.isHalloween())
                seasonalItem = ObjectManager.getItem("halloweentreat");
            if(Utilities.isYuletide()) {
                seasonalItem = ObjectManager.getItem("wintergift");
                if(Utilities.isYuletidePeak() && world.rand.nextBoolean())
                    seasonalItem = ObjectManager.getItem("wintergiftlarge");
            }

            if(seasonalItem != null && !noSeaonalDrop && event.getEntityLiving().getRNG().nextFloat() < ItemConfig.seasonalItemDropChance) {
                ItemStack dropStack = new ItemStack(seasonalItem, 1);
                EntityItemCustom entityItem = new EntityItemCustom(world, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, dropStack);
                entityItem.setPickupDelay(10);
                world.spawnEntity(entityItem);
            }
        }
	}



	// ==================================================
	//                 Mount Entity Event
	// ==================================================
	@SubscribeEvent
	public void onMountEntity(EntityMountEvent event) {
		if (event.isMounting() && event.getEntityBeingMounted() instanceof EntityBoat && event.getEntityBeingMounted() instanceof EntityMinecart) {
			if (event.getEntityMounting() instanceof BaseCreatureEntity) {
				BaseCreatureEntity creatureEntity = (BaseCreatureEntity)event.getEntityMounting();
				if (creatureEntity.isBoss() || creatureEntity.width >= 2 || creatureEntity.height >= 2) {
					event.setCanceled(true);
				}
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
        if(target == null)
            return;
        BlockPos pos = target.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();
        Item bucket = ObjectManager.buckets.get(block);
        if(bucket != null && world.getBlockState(pos).getValue(BlockLiquid.LEVEL) == 0) {
            world.setBlockToAir(pos);
        }
        
        if(bucket == null)
        	return;

        event.setFilledBucket(new ItemStack(bucket));
        event.setResult(Result.ALLOW);
    }


	// ==================================================
	//                 Break Block Event
	// ==================================================
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		if(event.getState() == null || event.getWorld() == null || event.isCanceled() || event.getWorld().isRemote) {
			return;
		}

		if(event.getPlayer() != null && !event.getPlayer().isCreative()) {
			ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(event.getWorld());
			if (!(event.getState().getBlock() instanceof BlockFireBase) && extendedWorld.isBossNearby(new Vec3d(event.getPos()))) {
				event.setCanceled(true);
				event.setResult(Result.DENY);
				event.getPlayer().sendStatusMessage(new TextComponentString(LanguageManager.translate("boss.block.protection.break")), true);
				return;
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
	public void onBlockPlace(BlockEvent.PlaceEvent event) {
		if(event.getState() == null || event.getWorld() == null || event.isCanceled()) {
			return;
		}

		if(event.getPlayer() != null && !event.getPlayer().isCreative()) {
			ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(event.getWorld());
			if (extendedWorld.isBossNearby(new Vec3d(event.getPos()))) {
				event.setCanceled(true);
				event.setResult(Result.DENY);
				event.getPlayer().sendStatusMessage(new TextComponentString(LanguageManager.translate("boss.block.protection.place")), true);
			}
		}
	}


	// ==================================================
	//               Mounting / Dismounting
	// ==================================================
	@SubscribeEvent
	public void onEntityMount(EntityMountEvent event) {
		if(!LycanitesMobs.config.getBool("Extras", "Disable Sneak Dismount", true)) {
			return;
		}
		if(!event.isDismounting() || !(event.getEntityMounting() instanceof EntityPlayer) || !(event.getEntityBeingMounted() instanceof RideableCreatureEntity)) {
			return;
		}
		ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer((EntityPlayer)event.getEntityMounting());
		if(extendedPlayer == null) {
			return;
		}
		event.setCanceled(event.getEntityMounting().isSneaking() && !extendedPlayer.isControlActive(ExtendedPlayer.CONTROL_ID.MOUNT_DISMOUNT));
	}


	// ==================================================
	//                 Projectile Impact
	// ==================================================
	@SubscribeEvent
	public void onProjectileImpact(ProjectileImpactEvent event) {
		Entity shooter = null;
		Entity target = event.getRayTraceResult().entityHit;
		if (!(target instanceof BaseCreatureEntity)) {
			return;
		}
		BaseCreatureEntity targetCreature = (BaseCreatureEntity)target;

		if (event.getEntity() instanceof EntityArrow) {
			EntityArrow arrow = (EntityArrow)event.getEntity();
			shooter = arrow.shootingEntity;
		}
		if (event.getEntity() instanceof EntityThrowable) {
			EntityThrowable throwable = (EntityThrowable)event.getEntity();
			shooter = throwable.getThrower();
		}

		if (shooter != null && !targetCreature.isDamageEntityApplicable(shooter)) {
			event.setCanceled(true);
		}
	}
}
