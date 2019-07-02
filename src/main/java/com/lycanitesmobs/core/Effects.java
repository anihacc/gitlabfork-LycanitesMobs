package com.lycanitesmobs.core;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.core.config.ConfigExtra;
import com.lycanitesmobs.core.entity.FearEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.network.MessageEntityVelocity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.UUID;

public class Effects {
    private static final UUID swiftswimmingMoveBoostUUID = UUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5e");
    private static final AttributeModifier swiftswimmingMoveBoost = (new AttributeModifier(swiftswimmingMoveBoostUUID, "Swiftswimming Speed Boost", 0.6D, AttributeModifier.Operation.ADDITION)).setSaved(false);

	// Global Settings:
	public boolean disableNausea = false;

	// ==================================================
	//                    Initialize
	// ==================================================
	public Effects() {
		ObjectManager.addPotionEffect("paralysis", true, 0xFFFF00, false);
		ObjectManager.addPotionEffect("penetration", true, 0x222222, false);
		ObjectManager.addPotionEffect("recklessness", true, 0xFF0044, false); // TODO Implement
		ObjectManager.addPotionEffect("rage", true, 0xFF4400, false); // TODO Implement
		ObjectManager.addPotionEffect("weight", true, 0x000022, false);
		ObjectManager.addPotionEffect("fear", false, 0x220022, false);
		ObjectManager.addPotionEffect("decay", true, 0x110033, false);
		ObjectManager.addPotionEffect("insomnia", true, 0x002222, false);
		ObjectManager.addPotionEffect("instability", true, 0x004422, false);
		ObjectManager.addPotionEffect("lifeleak", true, 0x0055FF, false);
		ObjectManager.addPotionEffect("bleed", true, 0xFF2222, false);
		ObjectManager.addPotionEffect("plague", true, 0x220066, false);
		ObjectManager.addPotionEffect("aphagia", true, 0xFFDDDD, false);
		ObjectManager.addPotionEffect("smited", true, 0xDDDDFF, false);
		ObjectManager.addPotionEffect("smouldering", true, 0xDD0000, false);

		ObjectManager.addPotionEffect("leech", false, 0x00FF99, true);
		ObjectManager.addPotionEffect("swiftswimming", false, 0x0000FF, true);
		ObjectManager.addPotionEffect("fallresist", false, 0xDDFFFF, true);
		ObjectManager.addPotionEffect("rejuvenation", false, 0x99FFBB, true);
		ObjectManager.addPotionEffect("immunization", false, 0x66FFBB, true);
		ObjectManager.addPotionEffect("cleansed", false, 0x66BBFF, true);
		ObjectManager.addPotionEffect("heataura", false, 0x996600, true); // TODO Implement
		ObjectManager.addPotionEffect("staticaura", false, 0xFFBB551, true); // TODO Implement
		ObjectManager.addPotionEffect("freezeaura", false, 0x55BBFF, true); // TODO Implement
		ObjectManager.addPotionEffect("envenom", false, 0x44DD66, true); // TODO Implement

		// Event Listener:
		MinecraftForge.EVENT_BUS.register(this);

		// Effect Sounds:
		ObjectManager.addSound("effect_fear", LycanitesMobs.modInfo, "effect.fear");
	}


    // ==================================================
	//                   Entity Update
	// ==================================================
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
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
		if(entity.isPotionActive(net.minecraft.potion.Effects.BLINDNESS) && entity.isPotionActive(net.minecraft.potion.Effects.NIGHT_VISION)) {
			entity.removePotionEffect(net.minecraft.potion.Effects.BLINDNESS);
		}


		// Disable Nausea:
		this.disableNausea = ConfigExtra.INSTANCE.disableNausea.get();
		if(this.disableNausea && event.getEntityLiving() instanceof PlayerEntity) {
			if(entity.isPotionActive(net.minecraft.potion.Effects.NAUSEA)) {
				entity.removePotionEffect(net.minecraft.potion.Effects.NAUSEA);
			}
		}

		// Immunity:
		boolean invulnerable = false;
		if(entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)entity;
			invulnerable = player.isCreative() || player.isSpectator();
		}


		// ========== Debuffs ==========
		// Paralysis
		EffectBase paralysis = ObjectManager.getEffect("paralysis");
		if(paralysis != null) {
			if(!invulnerable && entity.isPotionActive(paralysis)) {
				entity.setMotion(0, entity.getMotion().getY() > 0 ? 0 : entity.getMotion().getY(), 0);
				entity.onGround = false;
			}
		}
		
		// Weight
		EffectBase weight = ObjectManager.getEffect("weight");
		if(weight != null) {
			if(!invulnerable && entity.isPotionActive(weight) && !entity.isPotionActive(net.minecraft.potion.Effects.STRENGTH)) {
				if(entity.getMotion().getY() > -0.2D)
					entity.setMotion(entity.getMotion().add(0, -0.2D, 0));
			}
		}
		
		// Fear
		EffectBase fear = ObjectManager.getEffect("fear");
		if(fear != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(fear)) {
				ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
				if(extendedEntity != null) {
					if(extendedEntity.fearEntity == null) {
						FearEntity fearEntity = new FearEntity((EntityType<? extends FearEntity>) CreatureManager.getInstance().getEntityType("fear"), entity.getEntityWorld(), entity);
						entity.getEntityWorld().addEntity(fearEntity);
						extendedEntity.fearEntity = fearEntity;
					}
				}
			}
		}

		// Instability
		EffectBase instability = ObjectManager.getEffect("instability");
		if(instability != null && !entity.getEntityWorld().isRemote && !(entity instanceof IGroupBoss)) {
			if(!invulnerable && entity.isPotionActive(instability)) {
				if(entity.getEntityWorld().rand.nextDouble() <= 0.1) {
					double strength = 1 + entity.getActivePotionEffect(instability).getAmplifier();
					double motionX = strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
					double motionY = strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
					double motionZ = strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
					entity.setMotion(entity.getMotion().add(motionX, motionY, motionZ));
					try {
						if (entity instanceof ServerPlayerEntity) {
							ServerPlayerEntity player = (ServerPlayerEntity) entity;
							player.connection.sendPacket(new SEntityVelocityPacket(entity));
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
		EffectBase plague = ObjectManager.getEffect("plague");
		if(plague != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(plague)) {

				// Poison:
				int poisonAmplifier = entity.getActivePotionEffect(plague).getAmplifier();
				int poisonDuration = entity.getActivePotionEffect(plague).getDuration();
				if(entity.isPotionActive(net.minecraft.potion.Effects.POISON)) {
					poisonAmplifier = Math.max(poisonAmplifier, entity.getActivePotionEffect(net.minecraft.potion.Effects.POISON).getAmplifier());
					poisonDuration = Math.max(poisonDuration, entity.getActivePotionEffect(net.minecraft.potion.Effects.POISON).getDuration());
				}
				entity.addPotionEffect(new EffectInstance(net.minecraft.potion.Effects.POISON, poisonDuration, poisonAmplifier));

				// Spread:
				if(entity.getEntityWorld().getGameTime() % 20 == 0) {
					List aoeTargets = this.getNearbyEntities(entity, LivingEntity.class, null, 10);
					for(Object entityObj : aoeTargets) {
						LivingEntity target = (LivingEntity)entityObj;
						if(target != entity && !entity.isOnSameTeam(target)) {
							int amplifier = entity.getActivePotionEffect(plague).getAmplifier();
							int duration = entity.getActivePotionEffect(plague).getDuration();
							if(amplifier > 0) {
								target.addPotionEffect(new EffectInstance(plague, duration, amplifier - 1));
							}
							else {
								target.addPotionEffect(new EffectInstance(net.minecraft.potion.Effects.POISON, duration, amplifier));
							}
						}
					}
				}
			}
		}

		// Smited
		EffectBase smited = ObjectManager.getEffect("smited");
		if(smited != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(smited) && entity.getEntityWorld().getGameTime() % 20 == 0) {
				float brightness = entity.getBrightness();
				if(brightness > 0.5F && entity.getEntityWorld().canBlockSeeSky(entity.getPosition())) {
					entity.setFire(4);
				}
			}
		}

		// Bleed
		EffectBase bleed = ObjectManager.getEffect("bleed");
		if(bleed != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(bleed) && entity.getEntityWorld().getGameTime() % 20 == 0 && entity.getRidingEntity() != null) {
				if(entity.prevDistanceWalkedModified != entity.distanceWalkedModified) {
					entity.attackEntityFrom(DamageSource.MAGIC, entity.getActivePotionEffect(bleed).getAmplifier() + 1);
				}
			}
		}

		// Smouldering
		EffectBase smouldering = ObjectManager.getEffect("smouldering");
		if(smouldering != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(smouldering) && entity.getEntityWorld().getGameTime() % 20 == 0) {
				entity.setFire(4 + (4 * entity.getActivePotionEffect(smouldering).getAmplifier()));
			}
		}


		// ========== Buffs ==========
		// Swiftswimming
		EffectBase swiftswimming = ObjectManager.getEffect("swiftswimming");
		if(swiftswimming != null && entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)entity;
			if(entity.isPotionActive(swiftswimming) && entity.isInWater()) {
				int amplifier = entity.getActivePotionEffect(swiftswimming).getAmplifier();
				IAttributeInstance movement = entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
				if(movement.getModifier(swiftswimmingMoveBoostUUID) == null) {
					movement.applyModifier(swiftswimmingMoveBoost);
				}
			}
			else {
				IAttributeInstance movement = entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
				if(movement.getModifier(swiftswimmingMoveBoostUUID) != null) {
					movement.removeModifier(swiftswimmingMoveBoost);
				}
			}
		}

		// Immunisation
		EffectBase immunization = ObjectManager.getEffect("immunization");
		if(immunization != null && !entity.getEntityWorld().isRemote) {
			if(entity.isPotionActive(ObjectManager.getEffect("immunization"))) {
				if(entity.isPotionActive(net.minecraft.potion.Effects.POISON)) {
					entity.removePotionEffect(net.minecraft.potion.Effects.POISON);
				}
				if(entity.isPotionActive(net.minecraft.potion.Effects.HUNGER)) {
					entity.removePotionEffect(net.minecraft.potion.Effects.HUNGER);
				}
				if(entity.isPotionActive(net.minecraft.potion.Effects.WEAKNESS)) {
					entity.removePotionEffect(net.minecraft.potion.Effects.WEAKNESS);
				}
				if(entity.isPotionActive(net.minecraft.potion.Effects.NAUSEA)) {
					entity.removePotionEffect(net.minecraft.potion.Effects.NAUSEA);
				}
				if(ObjectManager.getEffect("paralysis") != null) {
					if(entity.isPotionActive(ObjectManager.getEffect("paralysis"))) {
						entity.removePotionEffect(ObjectManager.getEffect("paralysis"));
					}
				}
			}
		}

		// Cleansed
		EffectBase cleansed = ObjectManager.getEffect("cleansed");
		if(ObjectManager.getEffect("cleansed") != null && !entity.getEntityWorld().isRemote) {
			if(entity.isPotionActive(ObjectManager.getEffect("cleansed"))) {
				if(entity.isPotionActive(net.minecraft.potion.Effects.WITHER)) {
					entity.removePotionEffect(net.minecraft.potion.Effects.WITHER);
				}
				if(entity.isPotionActive(net.minecraft.potion.Effects.UNLUCK)) {
					entity.removePotionEffect(net.minecraft.potion.Effects.UNLUCK);
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
		LivingEntity entity = event.getEntityLiving();
		if(entity == null)
			return;

		boolean invulnerable = false;
		if(entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)entity;
			invulnerable = player.abilities.disableDamage;
		}
		if(invulnerable) {
			return;
		}
			
		// Anti-Jumping:
		EffectBase paralysis = ObjectManager.getEffect("paralysis");
		if(paralysis != null) {
			if(entity.isPotionActive(paralysis)) {
				if(event.isCancelable()) event.setCanceled(true);
			}
		}

		EffectBase weight = ObjectManager.getEffect("weight");
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

		LivingEntity target = event.getEntityLiving();
		LivingEntity attacker = null;
		if(event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof LivingEntity) {
			attacker = (LivingEntity) event.getSource().getTrueSource();
		}
		if(attacker == null) {
			return;
		}

		// ========== Debuffs ==========
		// Lifeleak
		EffectBase lifeleak = ObjectManager.getEffect("lifeleak");
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


		// ========== Debuffs ==========
        // Fall Resistance
		EffectBase fallresist = ObjectManager.getEffect("fallresist");
		if(fallresist != null) {
            if(event.getEntityLiving().isPotionActive(fallresist)) {
                if("fall".equals(event.getSource().damageType)) {
                    event.setAmount(0);
                    event.setCanceled(true);
                }
            }
        }

		// Penetration
		EffectBase penetration = ObjectManager.getEffect("penetration");
		if(penetration != null) {
			if(event.getEntityLiving().isPotionActive(penetration)) {
				float damage = event.getAmount();
				float multiplier = event.getEntityLiving().getActivePotionEffect(penetration).getAmplifier();
				event.setAmount(damage + (damage * multiplier));
			}
		}

		// Fear
		EffectBase fear = ObjectManager.getEffect("fear");
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
		EffectBase leech = ObjectManager.getEffect("leech");
		if(leech != null && event.getSource().getTrueSource() != null) {
            if(event.getSource().getTrueSource() instanceof LivingEntity) {
                LivingEntity attackingEntity = (LivingEntity)(event.getSource().getTrueSource());
                if(attackingEntity.isPotionActive(leech)) {
                    float damage = event.getAmount();
                    float multiplier = attackingEntity.getActivePotionEffect(leech).getAmplifier();
                    attackingEntity.heal(damage * multiplier);
                }
            }
        }
    }


	// ==================================================
	//                    Entity Heal
	// ==================================================
	@SubscribeEvent
	public void onEntityHeal(LivingHealEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if(entity == null)
			return;

		// Rejuvenation:
		EffectBase rejuvenation = ObjectManager.getEffect("rejuvenation");
		if(rejuvenation != null) {
			if(entity.isPotionActive(rejuvenation)) {
				event.setAmount((float)Math.ceil(event.getAmount() * (2 * (1 + entity.getActivePotionEffect(rejuvenation).getAmplifier()))));
			}
		}

		// Decay:
		EffectBase decay = ObjectManager.getEffect("decay");
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
		PlayerEntity player = event.getEntityPlayer();
		if(player == null || player.getEntityWorld().isRemote || event.isCanceled())
			return;

		// Insomnia:
		EffectBase insomnia = ObjectManager.getEffect("insomnia");
		if(insomnia != null && player.isPotionActive(insomnia)) {
			event.setResult(PlayerEntity.SleepResult.NOT_SAFE);
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
		EffectBase aphagia = ObjectManager.getEffect("aphagia");
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
		return searchEntity.getEntityWorld().getEntitiesWithinAABB(clazz, searchEntity.getBoundingBox().grow(range, range, range), (Predicate<Entity>) entity -> {
			if(filterClass == null)
				return true;
			return filterClass.isAssignableFrom(entity.getClass());
		});
	}
}
