package com.lycanitesmobs;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.model.ModelCreatureBase;
import com.lycanitesmobs.core.model.ModelItemBase;
import com.lycanitesmobs.core.model.ModelProjectileBase;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
	
	// Maps:
	public static Map<String, ResourceLocation> textures = new HashMap<>();
	public static Map<String, ResourceLocation[]> textureGroups = new HashMap<>();
	public static Map<String, ModelCreatureBase> creatureModels = new HashMap<>();
	public static Map<String, ModelProjectileBase> projectileModels = new HashMap<>();
	public static Map<String, Class<? extends ModelProjectileBase>> oldProjectileModelClasses = new HashMap<>();
	public static Map<String, ModelItemBase> itemModels = new HashMap<>();


    // ==================================================
    //                        Add
    // ==================================================
	// ========== Texture ==========
	public static void addTexture(String name, ModInfo modInfo, String path) {
		name = name.toLowerCase();
		textures.put(name, new ResourceLocation(modInfo.modid, path));
	}
	
	// ========== Texture Group ==========
	public static void addTextureGroup(String name, ModInfo modInfo, String[] paths) {
		name = name.toLowerCase();
        ResourceLocation[] textureGroup = new ResourceLocation[paths.length];
		for(int i = 0; i < paths.length; i++)
            textureGroup[i] = new ResourceLocation(modInfo.modid, paths[i]);
        textureGroups.put(name, textureGroup);
	}

	// ========== Item Model ==========
	public static void addItemModel(String name, ModelItemBase model) {
		name = name.toLowerCase();
		itemModels.put(name, model);
	}
	
	
    // ==================================================
    //                        Get
    // ==================================================
	// ========== Texture ==========
	public static ResourceLocation getTexture(String name) {
		name = name.toLowerCase();
		if(!textures.containsKey(name))
			return null;
		return textures.get(name);
	}
	
	// ========== Icon Group ==========
	public static ResourceLocation[] getTextureGroup(String name) {
		name = name.toLowerCase();
		if(!textureGroups.containsKey(name))
			return null;
		return textureGroups.get(name);
	}
	
	// ========== Model ==========
	public static ModelCreatureBase getCreatureModel(BaseCreatureEntity entityCreature) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (entityCreature.creatureInfo == null) {
			return null;
		}

		// Subpsecies Model:
		if(entityCreature.getSubspecies() != null && entityCreature.getSubspecies().modelClass != null) {
			if(creatureModels.containsKey(entityCreature.getSubspecies().modelClass.toString())) {
				return creatureModels.get(entityCreature.getSubspecies().modelClass.toString());
			}
			ModelCreatureBase creatureModel = entityCreature.getSubspecies().modelClass.getConstructor().newInstance();
			creatureModels.put(entityCreature.getSubspecies().modelClass.toString(), creatureModel);
			return creatureModel;
		}

		// Main Model:
		return getCreatureModel(entityCreature.creatureInfo);
	}

	public static ModelCreatureBase getCreatureModel(CreatureInfo creatureInfo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (creatureInfo == null || creatureInfo.modelClass == null) {
			return null;
		}

		// Main Model:
		if (creatureModels.containsKey(creatureInfo.modelClass.toString())) {
			return creatureModels.get(creatureInfo.modelClass.toString());
		}
		ModelCreatureBase creatureModel = creatureInfo.modelClass.getConstructor().newInstance();
		creatureModels.put(creatureInfo.modelClass.toString(), creatureModel);
		return creatureModel;
	}

	public static ModelProjectileBase getProjectileModel(ProjectileInfo projectileInfo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (projectileInfo == null || projectileInfo.modelClass == null) {
			return null;
		}

		// Main Model:
		if (projectileModels.containsKey(projectileInfo.modelClass.toString())) {
			return projectileModels.get(projectileInfo.modelClass.toString());
		}
		ModelProjectileBase projectileModel = projectileInfo.modelClass.getConstructor().newInstance();
		projectileModels.put(projectileInfo.modelClass.toString(), projectileModel);
		return projectileModel;
	}

	// ========== Old Projectile Model ==========
	public static void registerOldProjectileModel(String name, Class<? extends ModelProjectileBase> model) {
		name = name.toLowerCase();
		oldProjectileModelClasses.put(name, model);
	}

	public static ModelProjectileBase getOldProjectileModel(String projectileName) {
		if(!projectileModels.containsKey(projectileName)) {
			try {
				projectileModels.put("lightball", oldProjectileModelClasses.get(projectileName).getConstructor().newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return projectileModels.get(projectileName);
	}

	// ========== Item Model ==========
	public static ModelItemBase getItemModel(String name) {
		name = name.toLowerCase();
		if(!itemModels.containsKey(name))
			return null;
		return itemModels.get(name);
	}
}
