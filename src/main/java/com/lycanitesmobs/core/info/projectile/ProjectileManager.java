package com.lycanitesmobs.core.info.projectile;

import com.google.gson.JsonObject;
import com.lycanitesmobs.FileLoader;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorProjectile;
import com.lycanitesmobs.core.entity.*;
import com.lycanitesmobs.core.entity.projectile.*;
import com.lycanitesmobs.core.info.ItemManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.ItemCharge;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class ProjectileManager extends JSONLoader {
	public static ProjectileManager INSTANCE;

	/** A map of all projectiles by name. **/
	public Map<String, ProjectileInfo> projectiles = new HashMap<>();

	/** A map of old projectile classes that are hardcoded instead of using json definitions that use the default item sprite renderer. **/
	public Map<String, Class<? extends Entity>> oldSpriteProjectiles = new HashMap<>();

	/** A map of old projectiles that use the obj model renderer. Newer json based projectiles provide their model class in their ProjectileInfo definition instead. **/
	public Map<String, Class<? extends Entity>> oldModelProjectiles = new HashMap<>();

	/** A map of old projectile classes to types for creating enw instances. **/
	public Map<Class<? extends Entity>, EntityType<? extends EntityProjectileBase>> oldProjectileTypes = new HashMap<>();

	/** The next available network id for projectiles to register by. **/
	protected int nextProjectileNetworkId = 1000;

	/** Returns the main Projectile Manager instance or creates it and returns it. **/
	public static ProjectileManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ProjectileManager();
		}
		return INSTANCE;
	}

	/**
	 * Called during startup and initially loads everything in this manager.
	 * @param modInfo The mod loading this manager.
	 */
	public void startup(ModInfo modInfo) {
		this.loadAllFromJSON(modInfo);
		for(ProjectileInfo projectileInfo : this.projectiles.values()) {
			projectileInfo.load();
		}
		try {
			ObjectManager.addSpecialEntity("rapidfire", EntityProjectileRapidFire.class, EntityProjectileRapidFire.class.getConstructor(EntityType.class, World.class));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.loadOldProjectiles();
	}

	/** Loads all JSON Creature Types. Should be done before creatures are loaded so that they can find their type on load. **/
	public void loadAllFromJSON(ModInfo groupInfo) {
		try {
			this.loadAllJson(groupInfo, "Projectile", "projectiles", "name", true, FileLoader.PathType.COMMON);
			LycanitesMobs.logDebug("Projectile", "Complete! " + this.projectiles.size() + " JSON Projectile Info Loaded In Total.");
		}
		catch(Exception e) {
			LycanitesMobs.logWarning("", "No Projectiles loaded for: " + groupInfo.name);
		}
	}


	@Override
	public void parseJson(ModInfo modInfo, String name, JsonObject json) {
		ProjectileInfo projectileInfo = new ProjectileInfo(modInfo);
		projectileInfo.loadFromJSON(json);
		if (projectileInfo.name == null) {
			LycanitesMobs.logWarning("", "[Projectile] Unable to load " + name + " json due to missing name.");
			return;
		}

		// Already Exists:
		if (this.projectiles.containsKey(projectileInfo.name)) {
			projectileInfo = this.projectiles.get(projectileInfo.name);
			projectileInfo.loadFromJSON(json);
		}

		this.projectiles.put(projectileInfo.name, projectileInfo);
		return;
	}


	/**
	 * Generates the next available projectile network id to register with.
	 * @return The next projectile network id.
	 */
	public int getNextProjectileNetworkId() {
		return this.nextProjectileNetworkId++;
	}


	/**
	 * Registers all creatures added to this creature manager, called from the registry event.
	 * @param event The entity register event.
	 */
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		ModInfo modInfo = LycanitesMobs.modInfo;
		LycanitesMobs.logDebug("Projectile", "Forge registering all " + this.projectiles.size() + " projectiles from the mod: " + modInfo.name + "...");

		for(ProjectileInfo projectileInfo : this.projectiles.values()) {
			if(projectileInfo.modInfo != modInfo) {
				continue;
			}
			event.getRegistry().register(projectileInfo.getEntityType());
		}

		for(String entityName : this.oldSpriteProjectiles.keySet()) {
			String registryName = LycanitesMobs.modInfo.modid + ":" + entityName;

			EntityType.Builder entityTypeBuilder = EntityType.Builder.create(EntityFactory.getInstance(), EntityClassification.MISC);
			entityTypeBuilder.setTrackingRange(40);
			entityTypeBuilder.setUpdateInterval(3);
			entityTypeBuilder.setShouldReceiveVelocityUpdates(true);

			EntityType entityType = entityTypeBuilder.build(entityName);
			entityType.setRegistryName(modInfo.modid, entityName);
			try {
				EntityFactory.getInstance().addEntityType(entityType, this.oldSpriteProjectiles.get(entityName).getConstructor(EntityType.class, World.class));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			oldProjectileTypes.put(this.oldSpriteProjectiles.get(entityName), entityType);
			event.getRegistry().register(entityType);
		}

		for(String entityName : this.oldModelProjectiles.keySet()) {
			String registryName = LycanitesMobs.modInfo.modid + ":" + entityName;

			EntityType.Builder entityTypeBuilder = EntityType.Builder.create(EntityFactory.getInstance(), EntityClassification.MISC);
			entityTypeBuilder.setTrackingRange(40);
			entityTypeBuilder.setUpdateInterval(3);
			entityTypeBuilder.setShouldReceiveVelocityUpdates(true);

			EntityType entityType = entityTypeBuilder.build(entityName);
			entityType.setRegistryName(modInfo.modid, entityName);
			try {
				EntityFactory.getInstance().addEntityType(entityType, this.oldModelProjectiles.get(entityName).getConstructor(EntityType.class, World.class));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			oldProjectileTypes.put(this.oldModelProjectiles.get(entityName), entityType);
			event.getRegistry().register(entityType);
		}
	}


	/**
	 * Gets a projectile by name.
	 * @param projectileName The name of the projectile to get.
	 * @return The Projectile Info.
	 */
	public ProjectileInfo getProjectile(String projectileName) {
		if(!this.projectiles.containsKey(projectileName))
			return null;
		return this.projectiles.get(projectileName);
	}


	/** Called during early start up, loads all items. **/
	public void loadOldProjectiles() {
		this.addOldProjectile("summoningportal", EntityPortal.class);
		this.addOldProjectile("frostweb", EntityFrostweb.class, true, true);
		this.addOldProjectile("tundra", EntityTundra.class, true, true);
		this.addOldProjectile("icefireball", EntityIcefireball.class, true, true);
		this.addOldProjectile("blizzard", EntityBlizzard.class, true, true);
		this.addOldProjectile("hellfireball", EntityHellfireball.class, true, true);
		this.addOldProjectile("doomfireball", EntityDoomfireball.class, true, true);
		this.addOldProjectile("devilstar", EntityDevilstar.class, true, true);
		this.addOldProjectile("demonicspark", EntityDemonicSpark.class, false);
		this.addOldProjectile("demonicblast", EntityDemonicBlast.class, true, true);
		this.addOldProjectile("hellfirewall", EntityHellfireWall.class, false);
		this.addOldProjectile("hellfireorb", EntityHellfireOrb.class, false);
		this.addOldProjectile("hellfirewave", EntityHellfireWave.class, false);
		this.addOldProjectile("hellfirewavepart", EntityHellfireWavePart.class, false);
		this.addOldProjectile("hellfirebarrier", EntityHellfireBarrier.class, false);
		this.addOldProjectile("hellfirebarrierpart", EntityHellfireBarrierPart.class, false);
		this.addOldProjectile("devilgatling", EntityDevilGatling.class, false);
		this.addOldProjectile("hellshield", EntityHellShield.class, false);
		this.addOldProjectile("helllaser", EntityHellLaser.class, false);
		this.addOldProjectile("helllaserend", EntityHellLaserEnd.class, false);
		this.addOldProjectile("throwingscythe", EntityThrowingScythe.class, true, true);
		this.addOldProjectile("mudshot", EntityMudshot.class, true, true);
		this.addOldProjectile("aquapulse", EntityAquaPulse.class, true, true);
		this.addOldProjectile("whirlwind", EntityWhirlwind.class, true, true);
		this.addOldProjectile("chaosorb", EntityChaosOrb.class, true, true, true);
		this.addOldProjectile("acidsplash", EntityAcidSplash.class, true, true, true);
		this.addOldProjectile("lightball", EntityLightBall.class, true, true);
		this.addOldProjectile("lifedrain", EntityLifeDrain.class, true, true);
		this.addOldProjectile("lifedrainend", EntityLifeDrainEnd.class, false);
		this.addOldProjectile("crystalshard", EntityCrystalShard.class, true, true);
		this.addOldProjectile("frostbolt", EntityFrostbolt.class, true, true);
		this.addOldProjectile("faebolt", EntityFaeBolt.class, true, true);
		this.addOldProjectile("aetherwave", EntityAetherwave.class, true, true);
		this.addOldProjectile("waterjet", EntityWaterJet.class, true, true);
		this.addOldProjectile("waterjetend", EntityWaterJetEnd.class, false);
		this.addOldProjectile("magma", EntityMagma.class, true, true);
		this.addOldProjectile("scorchfireball", EntityScorchfireball.class, true, true);
		this.addOldProjectile("poop", EntityPoop.class, true, true);
		this.addOldProjectile("boulderblast", EntityBoulderBlast.class, true, true);
		this.addOldProjectile("arcanelaserstorm", EntityArcaneLaserStorm.class, true, true);
		this.addOldProjectile("arcanelaser", EntityArcaneLaser.class, false);
		this.addOldProjectile("arcanelaserend", EntityArcaneLaserEnd.class, false);
		this.addOldProjectile("quill", EntityQuill.class, true, true);
		this.addOldProjectile("spectralbolt", EntitySpectralbolt.class, true, true);
		this.addOldProjectile("bloodleech", EntityBloodleech.class, true, true);
		this.addOldProjectile("poisonray", EntityPoisonRay.class, true, true);
		this.addOldProjectile("poisonrayend", EntityPoisonRayEnd.class, false);
	}

	public void addOldProjectile(String name, Class<? extends EntityProjectileBase> entityClass) {
		if(EntityProjectileModel.class.isAssignableFrom(entityClass)) {
			this.oldModelProjectiles.put(name, entityClass);
			return;
		}
		this.oldSpriteProjectiles.put(name, entityClass);
	}
	
	public void addOldProjectile(String name, Class<? extends EntityProjectileBase> entityClass, boolean impactSound) {
		ModInfo modInfo = LycanitesMobs.modInfo;
		ObjectManager.addSound(name, modInfo, "projectile." + name);
		if(impactSound) {
			ObjectManager.addSound(name + "_impact", modInfo, "projectile." + name + ".impact");
		}
		this.addOldProjectile(name, entityClass);
	}

	public void addOldProjectile(String name, Class<? extends EntityProjectileBase> entityClass, boolean createChargeItem, boolean createDispenserBehaviour) {
		this.addOldProjectile(name, entityClass, createChargeItem, createDispenserBehaviour, false);
	}

	public void addOldProjectile(String name, Class<? extends EntityProjectileBase> entityClass, boolean createChargeItem, boolean createDispenserBehaviour, boolean impactSound) {
		this.addOldProjectile(name, entityClass, impactSound);
		if(createChargeItem) {
			Item.Properties itemProperties = new Item.Properties().group(ItemManager.getInstance().items);
			ItemCharge chargeItem = new ItemCharge(itemProperties, name + "charge", entityClass);
			ObjectManager.addItem(name + "charge", chargeItem);
			if (createDispenserBehaviour) {
				DispenserBehaviorProjectile dispenserBehaviour = new DispenserBehaviorProjectile(entityClass, name);
				DispenserBlock.registerDispenseBehavior(chargeItem, dispenserBehaviour);
			}
		}
	}

	public EntityProjectileBase createOldProjectile(Class<? extends EntityProjectileBase> projectileClass, World world, LivingEntity entity) {
		try {
			return projectileClass.getConstructor(EntityType.class, World.class, LivingEntity.class).newInstance(this.oldProjectileTypes.get(projectileClass), world, entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public EntityProjectileBase createOldProjectile(Class<? extends EntityProjectileBase> projectileClass, World world, double x, double y, double z) {
		try {
			return projectileClass.getConstructor(EntityType.class, World.class, Double.class, Double.class, Double.class).newInstance(this.oldProjectileTypes.get(projectileClass), world, x, y, z);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
