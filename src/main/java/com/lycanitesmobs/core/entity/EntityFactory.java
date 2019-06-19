package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class EntityFactory implements EntityType.IFactory<Entity> {
	public static EntityFactory INSTANCE;
	/** Returns the main Entity Factory instance or creates it and returns it. **/
	public static EntityFactory getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new EntityFactory();
		}
		return INSTANCE;
	}

	public Map<EntityType, Constructor<? extends Entity>> entityTypeClassMap = new HashMap<>();
	public Map<Constructor<? extends Entity>, EntityType> entityClassTypeMap = new HashMap<>();

	/**
	 * Adds a new Entity Type and Entity Class mapping for this Factory to create.
	 * @param entityType The Entity Type to create from.
	 * @param entityClass The Entity Class to instantiate for the type.
	 */
	public void addEntityType(EntityType entityType, Constructor<? extends Entity> entityClass) {
		LycanitesMobs.logDebug("", "Adding entity: " + entityClass + " Type: " + entityType.getName() + " Classification: " + entityType.getClassification()); // Name always shows pig!
		this.entityTypeClassMap.put(entityType, entityClass);
		this.entityClassTypeMap.put(entityClass, entityType);
	}

	/**
	 * Creates an entity from an entity type in the provided world.
	 * @param entityType The entity type to create an entity from.
	 * @param world The world to create the entity in.
	 * @return The created entity or null if no entity could be created.
	 */
	@Override
	public Entity create(EntityType entityType, World world) {
		LycanitesMobs.logDebug("", "Spawning entity: " + this.entityTypeClassMap.get(entityType).toString() + " Type: " + entityType.getName() + " Classification: " + entityType.getClassification()); // Name always shows pig!
		Constructor<? extends Entity> constructor = this.entityTypeClassMap.get(entityType);

		try {
			return constructor.newInstance(entityType, world);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	public BiFunction<net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity, World, Entity> createOnClientFunction = this::createOnClient;
	/**
	 * Spawns an entity on the client side from a server packet.
	 * @param spawnPacket The entity spawn packet.
	 * @param world The world to spawn in.
	 */
	public Entity createOnClient(net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity spawnPacket, World world) {
		LycanitesMobs.logDebug("", "Client factory called!"); // This is never called.
		return null;
	}
}
