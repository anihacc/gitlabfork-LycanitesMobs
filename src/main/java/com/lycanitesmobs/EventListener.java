package com.lycanitesmobs;

import com.lycanitesmobs.core.capabilities.IExtendedEntity;
import com.lycanitesmobs.core.capabilities.IExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.info.ItemConfig;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.ItemSwordBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
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
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    // ========== Potions ==========
    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        ObjectManager.registerPotions(event);
    }

	// ========== Entities ==========
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		ObjectManager.registerEntities(event, LycanitesMobs.group);
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
	}


    // ==================================================
    //                Attach Capabilities
    // ==================================================
    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof EntityLivingBase) {
            event.addCapability(new ResourceLocation(LycanitesMobs.modid, "IExtendedEntity"), new ICapabilitySerializable<NBTTagCompound>() {
                IExtendedEntity instance = LycanitesMobs.EXTENDED_ENTITY.getDefaultInstance();

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
                IExtendedPlayer instance = LycanitesMobs.EXTENDED_PLAYER.getDefaultInstance();

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
        ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(event.getOriginal());
        if(extendedPlayer != null)
            extendedPlayer.backupPlayer();
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
                LycanitesMobs.printDebug("ForceRemoveEntity", "Forced entity removal, checking: " + event.getEntity().getName());
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

        // ========== Item Early Update ==========
        if(event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)event.getEntityLiving();
            if(entityPlayer.getHeldItem(EnumHand.MAIN_HAND) != null) {
                ItemStack equippedItemStack = entityPlayer.getHeldItem(EnumHand.MAIN_HAND);
                if(equippedItemStack.getItem() instanceof ItemSwordBase) {
                    ((ItemSwordBase)equippedItemStack.getItem()).onEarlyUpdate(equippedItemStack, event.getEntityLiving(), EnumHand.MAIN_HAND);
                }
            }
            if(entityPlayer.getHeldItem(EnumHand.OFF_HAND) != null) {
                ItemStack equippedItemStack = entityPlayer.getHeldItem(EnumHand.OFF_HAND);
                if(equippedItemStack.getItem() instanceof ItemSwordBase) {
                    ((ItemSwordBase)equippedItemStack.getItem()).onEarlyUpdate(equippedItemStack, event.getEntityLiving(), EnumHand.OFF_HAND);
                }
            }
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
            if (item instanceof ItemBase)
                if (((ItemBase) item).onItemRightClickOnEntity(player, entity, itemStack)) {
                    if (event.isCancelable())
                        event.setCanceled(true);
                }
            if (item instanceof ItemSwordBase)
                if (((ItemSwordBase) item).onItemRightClickOnEntity(player, entity, itemStack)) {
                    if (event.isCancelable())
                        event.setCanceled(true);
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
				event.getEntityLiving().setRevengeTarget(null);
				return;
			}
		}

		// Can Be Targeted:
		if(event.getEntityLiving() instanceof EntityLiving && targetEntity instanceof EntityCreatureBase) {
			if(!((EntityCreatureBase)targetEntity).canBeTargetedBy(event.getEntityLiving())) {
				event.getEntityLiving().setRevengeTarget(null);
				if(event.isCancelable())
					event.setCanceled(true);
				((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
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
                world.spawnEntity(entityItem);
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
		if(event.getState() == null || event.getWorld() == null || event.getPlayer() == null || event.getWorld().isRemote || event.isCanceled()) {
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
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGameOverlay(RenderGameOverlayEvent.Text event) {
		if(!LycanitesMobs.config.getBool("Debug", "Overlay", false)) {
			return;
		}

		// Entity:
		RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
		if(mouseOver != null) {
			Entity mouseOverEntity = mouseOver.entityHit;
			if(mouseOverEntity != null) {
				if(mouseOverEntity instanceof EntityCreatureBase) {
					EntityCreatureBase mouseOverCreature = (EntityCreatureBase)mouseOverEntity;
					event.getLeft().add("");
					event.getLeft().add("Target Creature: " + mouseOverCreature.getName());
					event.getLeft().add("Distance To player: " + mouseOverCreature.getDistance(Minecraft.getMinecraft().player));
					event.getLeft().add("Elements: " + mouseOverCreature.creatureInfo.getElementNames());
					event.getLeft().add("Subspecies: " + mouseOverCreature.getSubspeciesIndex());
					event.getLeft().add("Level: " + mouseOverCreature.getLevel());
					event.getLeft().add("Size: " + mouseOverCreature.sizeScale);
					event.getLeft().add("");
					event.getLeft().add("Health: " + mouseOverCreature.getHealth() + "/" + mouseOverCreature.getMaxHealth() + " Fresh: " + mouseOverCreature.creatureStats.getHealth());
					event.getLeft().add("Speed: " + mouseOverCreature.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() + "/" + mouseOverCreature.creatureStats.getSpeed());
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
}
