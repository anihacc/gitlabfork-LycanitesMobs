package com.lycanitesmobs;

import com.lycanitesmobs.core.EffectBase;
import com.lycanitesmobs.core.container.CreatureContainer;
import com.lycanitesmobs.core.container.EquipmentForgeContainer;
import com.lycanitesmobs.core.container.EquipmentInfuserContainer;
import com.lycanitesmobs.core.container.SummoningPedestalContainer;
import com.lycanitesmobs.core.entity.EntityFactory;
import com.lycanitesmobs.core.info.ItemManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.tileentity.EquipmentInfuserTileEntity;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ObjectManager {
	public static ObjectManager INSTANCE;
	public static ObjectManager getInstance() {
		if(INSTANCE == null)
			INSTANCE = new ObjectManager();
		return INSTANCE;
	}
	
	// Maps:
	public static Map<String, Block> blocks = new HashMap<>();
    public static Map<String, Item> items = new HashMap<>();
    public static Map<Item, ModInfo> itemGroups = new HashMap<>();
	public static Map<String, Fluid> fluids = new HashMap<>();
    public static Map<Block, Item> buckets = new HashMap<>();
    public static Map<String, Class> tileEntities = new HashMap<>();
	public static Map<String, EffectBase> effects = new HashMap<>();
	public static Map<String, SoundEvent> sounds = new HashMap<>();

	// Type Maps:
	public static Map<String, Class<? extends Entity>> specialEntities = new HashMap<>();
	public static Map<Class<? extends Entity>, Constructor<? extends Entity>> specialEntityConstructors = new HashMap<>();
	public static Map<Class<? extends Entity>, EntityType<? extends Entity>> specialEntityTypes = new HashMap<>();
	public static Map<Class<? extends BlockEntity>, BlockEntityType<? extends BlockEntity>> tileEntityTypes = new HashMap<>();

    public static Map<String, DamageSource> damageSources = new HashMap<>();

	public static ModInfo currentModInfo;

	/** The next available network id for special entities to register by. **/
	protected static int nextSpecialEntityNetworkId = 0;

	
    // ==================================================
    //                        Setup
    // ==================================================
	public static void setCurrentModInfo(ModInfo group) {
		currentModInfo = group;
	}


	/**
	 * Generates the next available special entity network id to register with.
	 * @return The next special entity network id.
	 */
	public static int getNextSpecialEntityNetworkId() {
		return nextSpecialEntityNetworkId++;
	}
	
	
    // ==================================================
    //                        Add
    // ==================================================
	// ========== Block ==========
	public static Block addBlock(String name, Block block) {
		blocks.put(name, block);
        return block;
	}

	// ========== Fluid ==========
	public static Fluid addFluid(String name, Fluid fluid) {
        fluids.put(name, fluid);
        return fluid;
	}

	// ========== Item ==========
	public static Item addItem(String name, Item item) {
		items.put(name, item);
        itemGroups.put(item, currentModInfo);
        return item;
	}

    // ========== Tile Entity ==========
    public static Class addTileEntity(String name, Class tileEntityClass) {
        name = name.toLowerCase();
        tileEntities.put(name, tileEntityClass);
        return tileEntityClass;
    }

    // ========== Potion Effect ==========
	public static EffectBase addPotionEffect(String name, boolean isBad, int color, boolean goodEffect) {
        EffectBase effect = new EffectBase(name, isBad, color);
		effects.put(name, effect);
		ObjectLists.addEffect(goodEffect ? "buffs" : "debuffs", effect, name);

		return effect;
	}

	// ========== Special Entity ==========
	public static void addSpecialEntity(String name, Class<? extends Entity> entityClass, Constructor<? extends Entity> specialEntityConstructor) {
		specialEntities.put(name, entityClass);
		specialEntityConstructors.put(entityClass, specialEntityConstructor);

	}

    // ========== Damage Source ==========
    public static void addDamageSource(String name, DamageSource damageSource) {
        name = name.toLowerCase();
        damageSources.put(name, damageSource);
    }

	// ========== Sound ==========
	public static void addSound(String name, ModInfo modInfo, String path) {
		name = name.toLowerCase();
		Identifier resourceLocation = new Identifier(modInfo.modid, path);
		SoundEvent soundEvent = new SoundEvent(resourceLocation);
		sounds.put(name, soundEvent);
	}
	
	
    // ==================================================
    //                        Get
    // ==================================================
	// ========== Block ==========
	public static Block getBlock(String name) {
		name = name.toLowerCase();
		if(!blocks.containsKey(name))
			return null;
		return blocks.get(name);
	}
	
	// ========== Item ==========
	public static Item getItem(String name) {
		name = name.toLowerCase();
		if(!items.containsKey(name))
			return null;
		return items.get(name);
	}

	// ========== Fluid ==========
	public static Fluid getFluid(String name) {
		name = name.toLowerCase();
		if(!fluids.containsKey(name))
			return null;
		return fluids.get(name);
	}

    // ========== Tile Entity ==========
    public static Class getTileEntity(String name) {
        name = name.toLowerCase();
        if(!tileEntities.containsKey(name)) return null;
        return tileEntities.get(name);
    }
	
	// ========== Potion Effect ==========
	public static EffectBase getEffect(String name) {
		name = name.toLowerCase();
		if(!effects.containsKey(name)) return null;
		return effects.get(name);
	}

    // ========== Damage Source ==========
    public static DamageSource getDamageSource(String name) {
        name = name.toLowerCase();
        if(!damageSources.containsKey(name)) return null;
        return damageSources.get(name);
    }

	// ========== Sound ==========
	public static SoundEvent getSound(String name) {
		name = name.toLowerCase();
		if(!sounds.containsKey(name))
			return null;
		return sounds.get(name);
	}


    // ==================================================
    //                     Registry
    // ==================================================
    // ========== Blocks ==========
    public void registerBlocks() {
		for(Block block : blocks.values()) {
			LycanitesMobs.logDebug("Item", "Registering block: " + block.getTranslationKey());
            if(block.getTranslationKey() == null) {
                LycanitesMobs.logWarning("", "Block: " + block + " has no Registry Name!");
            }
			Registry.register(Registry.BLOCK, new Identifier(LycanitesMobs.MOD_ID, block.getTranslationKey()), block);
        }
    }

    // ========== Items ==========
    public void registerItems() {
		for(Item item : items.values()) {
	    	LycanitesMobs.logDebug("Item", "Registering item: " + item.getTranslationKey());
	        if(item.getTranslationKey() == null) {
	            LycanitesMobs.logWarning("", "Item: " + item + " has no Registry Name!");
            }
			Registry.register(Registry.ITEM, new Identifier(LycanitesMobs.MOD_ID, item.getTranslationKey()), item);
        }

	    Item.Settings blockItemSettings = new Item.Settings().group(ItemManager.getInstance().blocksGroup);
		for(Block block : blocks.values()) {
			BlockItem blockItem = new BlockItem(block, blockItemSettings);
			LycanitesMobs.logDebug("Item", "Registering item block: " + blockItem.getTranslationKey());
			Registry.register(Registry.ITEM, new Identifier(LycanitesMobs.MOD_ID, blockItem.getTranslationKey()), blockItem);
		}
    }

    // ========== Potions ==========
    public void registerEffects() {
		for(EffectBase effect : effects.values()) {
			Registry.register(Registry.STATUS_EFFECT, new Identifier(LycanitesMobs.MOD_ID, effect.getName()), effect);
		}
    }

	// ========== Entities ==========
	public void registerEntities() {
		for(String entityName : specialEntities.keySet()) {
			EntityType.Builder entityTypeBuilder = EntityType.Builder.create(EntityFactory.getInstance(), EntityCategory.MISC);
//			entityTypeBuilder.setTrackingRange(10);
//			entityTypeBuilder.setUpdateInterval(3);
//			entityTypeBuilder.setShouldReceiveVelocityUpdates(true);
			entityTypeBuilder.disableSummon();

			EntityType entityType = entityTypeBuilder.build(entityName);
//			entityType.setRegistryName(LycanitesMobs.MODID, entityName);
			EntityFactory.getInstance().addEntityType(entityType, specialEntityConstructors.get(specialEntities.get(entityName)), entityName);
			specialEntityTypes.put(specialEntities.get(entityName), entityType);
			Registry.register(Registry.ENTITY_TYPE, new Identifier(LycanitesMobs.MOD_ID, entityName), entityType);
		}
	}

	// ========== Sounds ==========
	public void registerSounds() {
		for(SoundEvent soundEvent : sounds.values()) {
			Registry.register(Registry.SOUND_EVENT, soundEvent.getId(), soundEvent);
		}
	}

	// ========== Containers ==========
	public void registerContainers() {
		Registry.register(Registry.CONTAINER, CreatureContainer.NAME, CreatureContainer.TYPE);
		Registry.register(Registry.CONTAINER, SummoningPedestalContainer.NAME, SummoningPedestalContainer.TYPE);
		Registry.register(Registry.CONTAINER, EquipmentForgeContainer.NAME, EquipmentForgeContainer.TYPE);
		Registry.register(Registry.CONTAINER, EquipmentInfuserContainer.NAME, EquipmentInfuserContainer.TYPE);
	}

	// ========== Block Entities ==========
	public void registerBlockEntities() {
		BlockEntityType<BlockEntity> summoningPedestalType = BlockEntityType.Builder.create((Supplier<BlockEntity>) TileEntitySummoningPedestal::new,
				getBlock("summoningpedestal")
		).build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "summoningpedestal", summoningPedestalType);
		tileEntityTypes.put(TileEntitySummoningPedestal.class, summoningPedestalType);

		BlockEntityType<BlockEntity> equipmentForgeType = BlockEntityType.Builder.create((Supplier<BlockEntity>) TileEntityEquipmentForge::new,
				getBlock("equipmentforge_lesser"),
				getBlock("equipmentforge_greater"),
				getBlock("equipmentforge_master")
		).build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "equipmentforge", equipmentForgeType);
		tileEntityTypes.put(TileEntityEquipmentForge.class, equipmentForgeType);

		BlockEntityType<BlockEntity> equipmentInfuserType = BlockEntityType.Builder.create((Supplier<BlockEntity>) EquipmentInfuserTileEntity::new,
				getBlock("equipment_infuser")
		).build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, "equipment_infuser", equipmentInfuserType);
		tileEntityTypes.put(EquipmentInfuserTileEntity.class, equipmentInfuserType);
	}
}
