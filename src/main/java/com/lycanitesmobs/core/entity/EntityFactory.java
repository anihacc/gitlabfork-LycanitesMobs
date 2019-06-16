package com.lycanitesmobs.core.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import java.util.Map;

public class EntityFactory implements EntityType.IFactory {
	public static EntityFactory INSTANCE;

	/** Returns the main Entity Factory instance or creates it and returns it. **/
	public static EntityFactory getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new EntityFactory();
		}
		return INSTANCE;
	}

	public Map<EntityType, Class<? extends Entity>> entityTypeClassMap;

	/**
	 * Adds a new Entity Type and Entity Class mapping for this Factory to create.
	 * @param entityType The Entity Type to create from.
	 * @param entityClass The Entity Class to instantiate for the type.
	 */
	public void addEntityType(EntityType entityType, Class<? extends Entity> entityClass) {
		this.entityTypeClassMap.put(entityType, entityClass);
	}

	/**
	 * Creates an entity from an entity type in the provided world.
	 * @param entityType The entity type to create an entity from.
	 * @param world The world to create the entity in.
	 * @return The created entity or null if no entity could be created.
	 */
	@Override
	public Entity create(EntityType entityType, World world) {
		Class<? extends Entity> entityClass = this.entityTypeClassMap.get(entityType);

		try {
			Entity entity = entityClass.getConstructor(World.class).newInstance(world);
			world.func_217376_c(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}