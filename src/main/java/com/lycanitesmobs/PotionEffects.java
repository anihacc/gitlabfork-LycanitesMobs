package com.lycanitesmobs;

import com.google.common.base.Predicate;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.FearEntity;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.network.MessageEntityVelocity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.UUID;

public class PotionEffects {
    private static final UUID swiftswimmingMoveBoostUUID = UUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5e");
    private static final AttributeModifier swiftswimmingMoveBoost = (new AttributeModifier(swiftswimmingMoveBoostUUID, "Swiftswimming Speed Boost", 1D, 2)).setSaved(false);
	private static final UUID swiftswimmingMoveBoostUUID2 = UUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5d");
	private static final AttributeModifier swiftswimmingMoveBoost2 = (new AttributeModifier(swiftswimmingMoveBoostUUID2, "Swiftswimming Speed Boost 2", 2D, 2)).setSaved(false);
	private static final UUID swiftswimmingMoveBoostUUID3 = UUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5c");
	private static final AttributeModifier swiftswimmingMoveBoost3 = (new AttributeModifier(swiftswimmingMoveBoostUUID3, "Swiftswimming Speed Boost 3", 3D, 2)).setSaved(false);
	private static final UUID swiftswimmingMoveBoostUUID4 = UUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5b");
	private static final AttributeModifier swiftswimmingMoveBoost4 = (new AttributeModifier(swiftswimmingMoveBoostUUID4, "Swiftswimming Speed Boost 4", 4D, 2)).setSaved(false);

	// Global Settings:
	public boolean disableNausea = false;

	// ==================================================
	//                    Initialize
	// ==================================================
	public void init(ConfigBase config) {
		config.setCategoryComment("Potion Effects", "Here you can override each potion effect ID from the automatic ID, use 0 if you want it to stay automatic. Overrides should only be needed if you are running a lot of mods that add custom effects.");
		if(config.getBool("Potion Effects", "Enable Custom Effects", true, "Set to false to disable the custom potion effects.")) {
			ObjectManager.addEffect("paralysis", config, true, 0xFFFF00, false);
			ObjectManager.addEffect("penetration", config, true, 0x222222, false);
			ObjectManager.addEffect("recklessness", config, true, 0xFF0044, false); // TODO Implement
			ObjectManager.addEffect("rage", config, true, 0xFF4400, false); // TODO Implement
			ObjectManager.addEffect("weight", config, true, 0x000022, false);
			ObjectManager.addEffect("fear", config, true, 0x220022, false);
			ObjectManager.addEffect("decay", config, true, 0x110033, false);
			ObjectManager.addEffect("insomnia", config, true, 0x002222, false);
			ObjectManager.addEffect("instability", config, true, 0x004422, false);
			ObjectManager.addEffect("lifeleak", config, true, 0x0055FF, false);
			ObjectManager.addEffect("bleed", config, true, 0xFF2222, false);
			ObjectManager.addEffect("plague", config, true, 0x220066, false);
			ObjectManager.addEffect("aphagia", config, true, 0xFFDDDD, false);
			ObjectManager.addEffect("smited", config, true, 0xDDDDFF, false);
			ObjectManager.addEffect("smouldering", config, true, 0xDD0000, false);

			ObjectManager.addEffect("leech", config, false, 0x00FF99, true);
			ObjectManager.addEffect("swiftswimming", config, false, 0x0000FF, true);
			ObjectManager.addEffect("fallresist", config, false, 0xDDFFFF, true);
			ObjectManager.addEffect("rejuvenation", config, false, 0x99FFBB, true);
			ObjectManager.addEffect("immunization", config, false, 0x66FFBB, true);
			ObjectManager.addEffect("cleansed", config, false, 0x66BBFF, true);
			ObjectManager.addEffect("repulsion", config, false, 0xBC532E, true);
			ObjectManager.addEffect("heataura", config, false, 0x996600, true); // TODO Implement
			ObjectManager.addEffect("staticaura", config, false, 0xFFBB551, true); // TODO Implement
			ObjectManager.addEffect("freezeaura", config, false, 0x55BBFF, true); // TODO Implement
			ObjectManager.addEffect("envenom", config, false, 0x44DD66, true); // TODO Implement

			// Event Listener:
			MinecraftForge.EVENT_BUS.register(this);

			// Effect Sounds:
			AssetManager.addSound("effect_fear", LycanitesMobs.modInfo, "effect.fear");
		}
		this.disableNausea = config.getBool("Potion Effects", "Disable Nausea Debuff", disableNausea, "Set to true to disable the vanilla nausea debuff on players.");
	}


    // ==================================================
	//                   Entity Update
	// ==================================================
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null) {
			return;
		}

		// Null Effect Fix:
		for(Object potionEffectObj : entity.getActivePotionEffects()) {
			if(potionEffectObj == null) {
				entity.clearActivePotions();
				LycanitesMobs.logWarning("EffectsSetup", "Found a null potion effect on entity: " + entity + " all effects have been removed from this entity.");
			}
		}
		
		// Night Vision Stops Blindness:
		if(entity.isPotionActive(MobEffects.BLINDNESS) && entity.isPotionActive(MobEffects.NIGHT_VISION)) {
			entity.removePotionEffect(MobEffects.BLINDNESS);
		}


		// Disable Nausea:
		if(this.disableNausea && event.getEntityLiving() instanceof EntityPlayer) {
			if(entity.isPotionActive(MobEffects.NAUSEA)) {
				entity.removePotionEffect(MobEffects.NAUSEA);
			}
		}

		// Immunity:
		boolean invulnerable = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			invulnerable = player.isCreative() || player.isSpectator();
		}


		// ========== Debuffs ==========
		// Paralysis
		PotionBase paralysis = ObjectManager.getEffect("paralysis");
		if(paralysis != null) {
			if(!invulnerable && entity.isPotionActive(paralysis)) {
				entity.motionX = 0;
				if(entity.motionY > 0)
					entity.motionY = 0;
				entity.motionZ = 0;
				entity.onGround = false;
			}
		}
		
		// Weight
		PotionBase weight = ObjectManager.getEffect("weight");
		if(weight != null) {
			if(!invulnerable && entity.isPotionActive(weight) && !entity.isPotionActive(MobEffects.STRENGTH)) {
				if(entity.motionY > -0.2D)
					entity.motionY = -0.2D;
			}
		}
		
		// Fear
		PotionBase fear = ObjectManager.getEffect("fear");
		if(fear != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(fear)) {
				ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
				if(extendedEntity != null) {
					if(extendedEntity.fearEntity == null) {
						FearEntity fearEntity = new FearEntity(entity.getEntityWorld(), entity);
						entity.getEntityWorld().spawnEntity(fearEntity);
						extendedEntity.fearEntity = fearEntity;
					}
				}
			}
		}

		// Instability
		PotionBase instability = ObjectManager.getEffect("instability");
		if(instability != null && !entity.getEntityWorld().isRemote && !(entity instanceof IGroupBoss)) {
			if(!invulnerable && entity.isPotionActive(instability)) {
				if(entity.getEntityWorld().rand.nextDouble() <= 0.1) {
					double strength = (1 + entity.getActivePotionEffect(instability).getAmplifier()) * 0.5D;
					entity.motionX += strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
					entity.motionY += strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
					entity.motionZ += strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
					try {
						if (entity instanceof EntityPlayerMP) {
							EntityPlayerMP player = (EntityPlayerMP) entity;
							player.connection.sendPacket(new SPacketEntityVelocity(entity));
							MessageEntityVelocity messageEntityVelocity = new MessageEntityVelocity(
									player,
									strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D),
									strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D),
									strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D)
							);
							LycanitesMobs.packetHandler.sendToPlayer(messageEntityVelocity, player);
						}
					}
					catch(Exception e) {
						LycanitesMobs.logWarning("", "Failed to create and send a network packet for instability velocity!");
						e.printStackTrace();
					}
				}
			}
		}

		// Plague
		PotionBase plague = ObjectManager.getEffect("plague");
		if(plague != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(plague)) {

				// Poison:
				int poisonAmplifier = entity.getActivePotionEffect(plague).getAmplifier();
				int poisonDuration = entity.getActivePotionEffect(plague).getDuration();
				if(entity.isPotionActive(MobEffects.POISON)) {
					poisonAmplifier = Math.max(poisonAmplifier, entity.getActivePotionEffect(MobEffects.POISON).getAmplifier());
					poisonDuration = Math.max(poisonDuration, entity.getActivePotionEffect(MobEffects.POISON).getDuration());
				}
				entity.addPotionEffect(new PotionEffect(MobEffects.POISON, poisonDuration, poisonAmplifier));

				// Spread:
				if(entity.getEntityWorld().getTotalWorldTime() % 20 == 0) {
					List aoeTargets = this.getNearbyEntities(entity, EntityLivingBase.class, null, 2);
					for(Object entityObj : aoeTargets) {
						EntityLivingBase target = (EntityLivingBase)entityObj;
						if(target != entity && !entity.isOnSameTeam(target)) {
							int amplifier = entity.getActivePotionEffect(plague).getAmplifier();
							int duration = entity.getActivePotionEffect(plague).getDuration();
							if(amplifier > 0) {
								target.addPotionEffect(new PotionEffect(plague, duration, amplifier - 1));
							}
							else {
								target.addPotionEffect(new PotionEffect(MobEffects.POISON, duration, amplifier));
							}
						}
					}
				}
			}
		}

		// Smited
		PotionBase smited = ObjectManager.getEffect("smited");
		if(smited != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(smited) && entity.getEntityWorld().getTotalWorldTime() % 20 == 0) {
				float brightness = entity.getBrightness();
				if(brightness > 0.5F && entity.getEntityWorld().canBlockSeeSky(entity.getPosition())) {
					entity.setFire(4);
				}
			}
		}

		// Bleed
		PotionBase bleed = ObjectManager.getEffect("bleed");
		if(bleed != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(bleed) && entity.getEntityWorld().getTotalWorldTime() % 20 == 0 && !entity.isRiding()) {
				if(entity.prevDistanceWalkedModified != entity.distanceWalkedModified) {
					entity.attackEntityFrom(DamageSource.MAGIC, entity.getActivePotionEffect(bleed).getAmplifier() + 1);
				}
			}
		}

		// Smouldering
		PotionBase smouldering = ObjectManager.getEffect("smouldering");
		if(smouldering != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(smouldering) && entity.getEntityWorld().getTotalWorldTime() % 20 == 0) {
				entity.setFire(4 + (4 * entity.getActivePotionEffect(smouldering).getAmplifier()));
			}
		}


		// ========== Buffs ==========
		// Swiftswimming
		PotionBase swiftswimming = ObjectManager.getEffect("swiftswimming");
		if(swiftswimming != null && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			IAttributeInstance movement = entity.getEntityAttribute(EntityLivingBase.SWIM_SPEED);
			int amplifier = -1;
			if(entity.isPotionActive(swiftswimming)) {
				amplifier = entity.getActivePotionEffect(swiftswimming).getAmplifier();
			}
			if(amplifier == 0 && movement.getModifier(swiftswimmingMoveBoostUUID) == null) {
				movement.applyModifier(swiftswimmingMoveBoost);
			}
			else if(amplifier != 0 && movement.getModifier(swiftswimmingMoveBoostUUID) != null) {
				movement.removeModifier(swiftswimmingMoveBoost);
			}
			if(amplifier == 1 && movement.getModifier(swiftswimmingMoveBoostUUID2) == null) {
				movement.applyModifier(swiftswimmingMoveBoost2);
			}
			else if(amplifier != 1 && movement.getModifier(swiftswimmingMoveBoostUUID2) != null) {
				movement.removeModifier(swiftswimmingMoveBoost2);
			}
			if(amplifier == 2 && movement.getModifier(swiftswimmingMoveBoostUUID3) == null) {
				movement.applyModifier(swiftswimmingMoveBoost3);
			}
			else if(amplifier != 2 && movement.getModifier(swiftswimmingMoveBoostUUID3) != null) {
				movement.removeModifier(swiftswimmingMoveBoost3);
			}
			if(amplifier >= 3 && movement.getModifier(swiftswimmingMoveBoostUUID4) == null) {
				movement.applyModifier(swiftswimmingMoveBoost4);
			}
			else if(amplifier < 3 && movement.getModifier(swiftswimmingMoveBoostUUID4) != null) {
				movement.removeModifier(swiftswimmingMoveBoost4);
			}
		}

		// Immunisation
		PotionBase immunization = ObjectManager.getEffect("immunization");
		if(immunization != null && !entity.getEntityWorld().isRemote) {
			if(entity.isPotionActive(ObjectManager.getEffect("immunization"))) {
				if(entity.isPotionActive(MobEffects.POISON)) {
					entity.removePotionEffect(MobEffects.POISON);
				}
				if(entity.isPotionActive(MobEffects.HUNGER)) {
					entity.removePotionEffect(MobEffects.HUNGER);
				}
				if(entity.isPotionActive(MobEffects.WEAKNESS)) {
					entity.removePotionEffect(MobEffects.WEAKNESS);
				}
				if(entity.isPotionActive(MobEffects.NAUSEA)) {
					entity.removePotionEffect(MobEffects.NAUSEA);
				}
				if(ObjectManager.getEffect("paralysis") != null) {
					if(entity.isPotionActive(ObjectManager.getEffect("paralysis"))) {
						entity.removePotionEffect(ObjectManager.getEffect("paralysis"));
					}
				}
			}
		}

		// Cleansed
		PotionBase cleansed = ObjectManager.getEffect("cleansed");
		if(ObjectManager.getEffect("cleansed") != null && !entity.getEntityWorld().isRemote) {
			if(entity.isPotionActive(ObjectManager.getEffect("cleansed"))) {
				if(entity.isPotionActive(MobEffects.WITHER)) {
					entity.removePotionEffect(MobEffects.WITHER);
				}
				if(entity.isPotionActive(MobEffects.UNLUCK)) {
					entity.removePotionEffect(MobEffects.UNLUCK);
				}
				if(ObjectManager.getEffect("fear") != null) {
					if(entity.isPotionActive(ObjectManager.getEffect("fear"))) {
						entity.removePotionEffect(ObjectManager.getEffect("fear"));
					}
				}
				if(ObjectManager.getEffect("insomnia") != null) {
					if(entity.isPotionActive(ObjectManager.getEffect("insomnia"))) {
						entity.removePotionEffect(ObjectManager.getEffect("insomnia"));
					}
				}
			}
		}
	}
	
	
	// ==================================================
	//                    Entity Jump
	// ==================================================
	@SubscribeEvent
	public void onEntityJump(LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null)
			return;

		boolean invulnerable = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			invulnerable = player.capabilities.isCreativeMode;
		}
		if(invulnerable) {
			return;
		}
			
		// Anti-Jumping:
		PotionBase paralysis = ObjectManager.getEffect("paralysis");
		if(paralysis != null) {
			if(entity.isPotionActive(paralysis)) {
				if(event.isCancelable()) event.setCanceled(true);
			}
		}

		PotionBase weight = ObjectManager.getEffect("weight");
		if(weight != null) {
			if(entity.isPotionActive(weight)) {
				if(event.isCancelable()) event.setCanceled(true);
			}
		}
	}


	// ==================================================
	//               Living Attack Event
	// ==================================================
	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event) {
		if(event.isCancelable() && event.isCanceled())
			return;

		if(event.getEntityLiving() == null)
			return;

		EntityLivingBase target = event.getEntityLiving();
		EntityLivingBase attacker = null;
		if(event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {
			attacker = (EntityLivingBase) event.getSource().getTrueSource();
		}
		if(attacker == null) {
			return;
		}

		// ========== Debuffs ==========
		// Lifeleak
		PotionBase lifeleak = ObjectManager.getEffect("lifeleak");
		if(lifeleak != null && !event.getEntityLiving().getEntityWorld().isRemote) {
			if(attacker.isPotionActive(lifeleak)) {
				if (event.isCancelable()) {
					event.setCanceled(true);
				}
				target.heal(event.getAmount());
			}
		}
	}


    // ==================================================
    //                 Living Hurt Event
    // ==================================================
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if(event.isCancelable() && event.isCanceled())
            return;

        if(event.getEntityLiving() == null)
            return;
		EntityLivingBase target = event.getEntityLiving();
		Entity attacker = event.getSource().getTrueSource();


		// ========== Debuffs ==========
        // Fall Resistance
		PotionBase fallresist = ObjectManager.getEffect("fallresist");
		if(fallresist != null) {
            if(event.getEntityLiving().isPotionActive(fallresist)) {
                if("fall".equals(event.getSource().damageType)) {
                    event.setAmount(0);
                    event.setCanceled(true);
                }
            }
        }

		// Penetration
		PotionBase penetration = ObjectManager.getEffect("penetration");
		if(penetration != null) {
			if(event.getEntityLiving().isPotionActive(penetration)) {
				float damage = event.getAmount();
				float multiplier = event.getEntityLiving().getActivePotionEffect(penetration).getAmplifier();
				event.setAmount(damage + (damage * multiplier));
			}
		}

		// Fear
		PotionBase fear = ObjectManager.getEffect("fear");
		if(fear != null) {
			if(event.getEntityLiving().isPotionActive(fear)) {
				if("inWall".equals(event.getSource().damageType)) {
					event.setAmount(0);
					event.setCanceled(true);
				}
			}
		}


        // ========== Buffs ==========
        // Leeching
		PotionBase leech = ObjectManager.getEffect("leech");
		if(leech != null && event.getSource().getTrueSource() != null) {
			EntityLivingBase leechingEntity = null;
				if(event.getSource().getImmediateSource() instanceof EntityLivingBase) {
					leechingEntity = (EntityLivingBase)event.getSource().getImmediateSource();
				}
				else if(event.getSource().getTrueSource() instanceof EntityLivingBase) {
					leechingEntity = (EntityLivingBase)event.getSource().getTrueSource();
				}
            if(leechingEntity != null) {
                if(leechingEntity.isPotionActive(leech)) {
                    int leeching = leechingEntity.getActivePotionEffect(leech).getAmplifier() + 1;
					leechingEntity.heal(Math.max(leeching, 1));
                }
            }
        }

		// Repulsion
		PotionBase repulsion = ObjectManager.getEffect("repulsion");
		if(repulsion != null) {
			boolean attackerIsBoss = attacker instanceof IGroupBoss;
			if(!attackerIsBoss && CreatureManager.getInstance().getCreatureGroup("boss") != null) {
				attackerIsBoss = CreatureManager.getInstance().getCreatureGroup("boss").hasEntity(attacker);
			}
			if(attacker != null && !attackerIsBoss && target.isPotionActive(repulsion)) {
				float knockback = target.getActivePotionEffect(repulsion).getAmplifier() + 2;
				double xDist = attacker.getPositionVector().x - target.getPositionVector().x;
				double zDist = attacker.getPositionVector().z - target.getPositionVector().z;
				double xzDist = Math.max(MathHelper.sqrt(xDist * xDist + zDist * zDist), 0.01D);
				double motionCap = 10;
				double xVel = xDist / xzDist * knockback;
				double zVel = zDist / xzDist * knockback;
				if (attacker.motionX < motionCap && attacker.motionX > -motionCap && attacker.motionZ < motionCap && attacker.motionZ > -motionCap) {
					attacker.addVelocity(xVel, 0, zVel);
				}
			}
		}
    }


	// ==================================================
	//                    Entity Heal
	// ==================================================
	@SubscribeEvent
	public void onEntityHeal(LivingHealEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null)
			return;

		// Rejuvenation:
		PotionBase rejuvenation = ObjectManager.getEffect("rejuvenation");
		if(rejuvenation != null) {
			if(entity.isPotionActive(rejuvenation)) {
				event.setAmount((float)Math.ceil(event.getAmount() * (2 * (1 + entity.getActivePotionEffect(rejuvenation).getAmplifier()))));
			}
		}

		// Decay:
		PotionBase decay = ObjectManager.getEffect("decay");
		if(decay != null) {
			if(entity.isPotionActive(decay)) {
				event.setAmount((float)Math.floor(event.getAmount() / (2 * (1 + entity.getActivePotionEffect(decay).getAmplifier()))));
			}
		}
	}


	// ==================================================
	//                Player Use Bed Event
	// ==================================================
	/** This uses the player sleep in bed event to spawn mobs. **/
	@SubscribeEvent
	public void onSleep(PlayerSleepInBedEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(player == null || player.getEntityWorld().isRemote || event.isCanceled())
			return;

		// Insomnia:
		PotionBase insomnia = ObjectManager.getEffect("insomnia");
		if(insomnia != null && player.isPotionActive(insomnia)) {
			event.setResult(EntityPlayer.SleepResult.NOT_SAFE);
		}
	}


	// ==================================================
	//               Item Use Event
	// ==================================================
	@SubscribeEvent
	public void onLivingUseItem(LivingEntityUseItemEvent event) {
		if(event.isCancelable() && event.isCanceled())
			return;

		if(event.getEntityLiving() == null)
			return;

		// ========== Debuffs ==========
		// Aphagia
		PotionBase aphagia = ObjectManager.getEffect("aphagia");
		if(aphagia != null && !event.getEntityLiving().getEntityWorld().isRemote) {
			if(event.getEntityLiving().isPotionActive(aphagia)) {
				if(event.isCancelable()) {
					event.setCanceled(true);
				}
			}
		}
	}


	// ==================================================
	//                     Utility
	// ==================================================
	/** Get entities that are near the provided entity. **/
	public <T extends Entity> List<T> getNearbyEntities(Entity searchEntity, Class <? extends T > clazz, final Class filterClass, double range) {
		return searchEntity.getEntityWorld().getEntitiesWithinAABB(clazz, searchEntity.getEntityBoundingBox().grow(range, range, range), (Predicate<Entity>) entity -> {
			if(filterClass == null)
				return true;
			return filterClass.isAssignableFrom(entity.getClass());
		});
	}
}
