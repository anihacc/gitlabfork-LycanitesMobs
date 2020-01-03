package com.lycanitesmobs.core.info.projectile;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.entity.EntityPortal;
import com.lycanitesmobs.core.entity.EntityProjectileModel;
import com.lycanitesmobs.core.entity.projectile.*;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

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
		this.loadOldProjectiles();
	}

	/** Loads all JSON Creature Types. Should be done before creatures are loaded so that they can find their type on load. **/
	public void loadAllFromJSON(ModInfo groupInfo) {
		try {
			this.loadAllJson(groupInfo, "Projectile", "projectiles", "name", true);
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
	 * @param event The enity register event.
	 */
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		ModInfo modInfo = LycanitesMobs.modInfo;
		LycanitesMobs.logDebug("Projectile", "Forge registering all " + this.projectiles.size() + " projectiles from the mod: " + modInfo.name + "...");

		for(ProjectileInfo projectileInfo : this.projectiles.values()) {
			if(projectileInfo.modInfo != modInfo) {
				continue;
			}
			EntityEntry entityEntry = EntityEntryBuilder.create()
					.entity(projectileInfo.entityClass)
					.id(projectileInfo.getEntityId(), this.getNextProjectileNetworkId())
					.name(projectileInfo.getName())
					.tracker(40, 3, true)
					.build();
			event.getRegistry().register(entityEntry);
		}

		for(String entityName : this.oldSpriteProjectiles.keySet()) {
			String registryName = LycanitesMobs.modInfo.modid + ":" + entityName;
			EntityEntry entityEntry = EntityEntryBuilder.create()
					.entity(this.oldSpriteProjectiles.get(entityName))
					.id(registryName, this.getNextProjectileNetworkId())
					.name(entityName)
					.tracker(40, 3, true)
					.build();
			event.getRegistry().register(entityEntry);
		}

		for(String entityName : this.oldModelProjectiles.keySet()) {
			String registryName = LycanitesMobs.modInfo.modid + ":" + entityName;
			EntityEntry entityEntry = EntityEntryBuilder.create()
					.entity(this.oldModelProjectiles.get(entityName))
					.id(registryName, this.getNextProjectileNetworkId())
					.name(entityName)
					.tracker(40, 3, true)
					.build();
			event.getRegistry().register(entityEntry);
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

		this.addOldProjectile("shadowfirebarrier", EntityShadowfireBarrier.class, false);
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
	}

	public void addOldProjectile(String name, Class<? extends Entity> entityClass) {
		if(EntityProjectileModel.class.isAssignableFrom(entityClass)) {
			this.oldModelProjectiles.put(name, entityClass);
			return;
		}
		this.oldSpriteProjectiles.put(name, entityClass);
	}
	
	public void addOldProjectile(String name, Class<? extends Entity> entityClass, boolean impactSound) {
		name = name.toLowerCase();
		ModInfo modInfo = LycanitesMobs.modInfo;
		AssetManager.addSound(name, modInfo, "projectile." + name);
		if(impactSound) {
			AssetManager.addSound(name + "_impact", modInfo, "projectile." + name + ".impact");
		}
		this.addOldProjectile(name, entityClass);
	}
}
