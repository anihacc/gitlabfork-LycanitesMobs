package com.lycanitesmobs;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.model.ModelObjOld;
import com.lycanitesmobs.core.model.ModelItemBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
	
	// Maps:
	public static Map<String, ResourceLocation> textures = new HashMap<>();
	public static Map<String, ResourceLocation[]> textureGroups = new HashMap<>();
	public static Map<String, SoundEvent> sounds = new HashMap<>();
	public static Map<String, ModelBase> models = new HashMap<>();
	public static Map<String, IModel> objModels = new HashMap<>();
	public static Map<String, ModelItemBase> itemModels = new HashMap<>();
	
    // ==================================================
    //                        Add
    // ==================================================
	// ========== Texture ==========
	public static void addTexture(String name, ModInfo group, String path) {
		name = name.toLowerCase();
		textures.put(name, new ResourceLocation(group.filename, path));
	}
	
	// ========== Texture Group ==========
	public static void addTextureGroup(String name, ModInfo group, String[] paths) {
		name = name.toLowerCase();
        ResourceLocation[] textureGroup = new ResourceLocation[paths.length];
		for(int i = 0; i < paths.length; i++)
            textureGroup[i] = new ResourceLocation(group.filename, paths[i]);
        textureGroups.put(name, textureGroup);
	}
	
	// ========== Sound ==========
	public static void addSound(String name, ModInfo group, String path) {
		name = name.toLowerCase();
        ResourceLocation resourceLocation = new ResourceLocation(group.filename, path);
        SoundEvent soundEvent = new SoundEvent(resourceLocation);
        soundEvent.setRegistryName(resourceLocation);
		sounds.put(name, soundEvent);
        GameRegistry.findRegistry(SoundEvent.class).register(soundEvent);
	}
	
	// ========== Model ==========
	public static void addModel(String name, ModelBase model) {
		name = name.toLowerCase();
		models.put(name, model);
	}
	
	// ========== Obj Model ==========
	public static void addObjModel(String name, ModInfo group, String path) {
		name = name.toLowerCase();
		objModels.put(name, ModelObjOld.loadModel(new ResourceLocation(group.filename, "models/" + path + ".obj")));
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
	
	// ========== Sound ==========
	public static SoundEvent getSound(String name) {
		name = name.toLowerCase();
		if(!sounds.containsKey(name))
			return null;
		return sounds.get(name);
	}
	
	// ========== Model ==========
	public static ModelBase getModel(String name) {
		name = name.toLowerCase();
		CreatureInfo creatureInfo = CreatureManager.getInstance().getCreatureFromId(name);
		if(creatureInfo != null) {

		}
		if(!models.containsKey(name))
			return null;
		return models.get(name);
	}

	public static ModelBase getCreatureModel(EntityCreatureBase entityCreature) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (entityCreature.creatureInfo == null) {
			return null;
		}

		// Subpsecies Model:
		if(entityCreature.getSubspecies() != null && entityCreature.getSubspecies().modelClass != null) {
			if(models.containsKey(entityCreature.getSubspecies().modelClass.toString())) {
				return models.get(entityCreature.getSubspecies().modelClass.toString());
			}
			ModelBase creatureModel = entityCreature.getSubspecies().modelClass.getConstructor().newInstance();
			models.put(entityCreature.getSubspecies().modelClass.toString(), creatureModel);
			return creatureModel;
		}

		// Main Model:
		return getCreatureModel(entityCreature.creatureInfo);
	}

	public static ModelBase getCreatureModel(CreatureInfo creatureInfo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		if (creatureInfo == null || creatureInfo.modelClass == null) {
			return null;
		}

		// Main Model:
		if (models.containsKey(creatureInfo.modelClass.toString())) {
			return models.get(creatureInfo.modelClass.toString());
		}
		ModelBase creatureModel = creatureInfo.modelClass.getConstructor().newInstance();
		models.put(creatureInfo.modelClass.toString(), creatureModel);
		return creatureModel;
	}
	
	// ========== Obj Model ==========
	public static IModel getObjModel(String name) {
		name = name.toLowerCase();
		if(!objModels.containsKey(name))
			return null;
		return objModels.get(name);
	}
	public static IModel getObjModel(String name, ModInfo group, String path) {
		name = name.toLowerCase();
		if(!objModels.containsKey(name))
			addObjModel(name, group, path);
		return objModels.get(name);
	}

	// ========== Item Model ==========
	public static ModelItemBase getItemModel(String name) {
		name = name.toLowerCase();
		if(!itemModels.containsKey(name))
			return null;
		return itemModels.get(name);
	}
}
