package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockSlabCustom;
import com.lycanitesmobs.core.entity.EntityFactory;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.stats.Stat;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;

import java.util.HashMap;
import java.util.Map;

public class ObjectManager {
	
	// Maps:
	public static Map<String, Block> blocks = new HashMap<>();
    public static Map<String, Item> items = new HashMap<>();
    public static Map<Item, ModInfo> itemGroups = new HashMap<>();
	public static Map<String, Fluid> fluids = new HashMap<>();
    public static Map<Block, Item> buckets = new HashMap<>();
    public static Map<String, Class> tileEntities = new HashMap<>();
	public static Map<String, EffectBase> effects = new HashMap<>();
	public static Map<String, SoundEvent> sounds = new HashMap<>();

	// Entity Maps:
	public static Map<String, Class<? extends Entity>> specialEntities = new HashMap<>();

    public static Map<String, DamageSource> damageSources = new HashMap<>();

    public static Map<String, Stat> stats = new HashMap<>();
	
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
		name = name.toLowerCase();
		blocks.put(name, block);
        if(block instanceof BlockSlabCustom) {
            BlockSlabCustom blockSlab = (BlockSlabCustom)block;
            //ItemSlabCustom itemSlabCustom = new ItemSlabCustom(blockSlab, blockSlab, blockSlab.getDoubleBlock());
            //items.put(name, itemSlabCustom);
            //itemGroups.put(itemSlabCustom, currentModInfo);
        }
        else {
            //BlockItem itemBlock = new ItemBlockBase(block);
            //itemBlock.setRegistryName(block.getRegistryName());
            //items.put(name, itemBlock);
            //itemGroups.put(itemBlock, currentModInfo);
        }
        return block;
	}

	// ========== Fluid ==========
	public static Fluid addFluid(String fluidName) {
        ModInfo group = currentModInfo;
        //Fluid fluid = new Fluid(fluidName, new ResourceLocation(group.filename + ":blocks/" + fluidName + "_still"), new ResourceLocation(group.filename + ":blocks/" + fluidName + "_flow"));
		//fluids.put(fluidName, fluid);
		//if(!FluidRegistry.registerFluid(fluid)) {
		//    LycanitesMobs.logWarning("", "Another fluid was registered as " + fluidName);
        //}
        return null;
	}

	// ========== Bucket ==========
	public static Item addBucket(Item bucket, Block block, Fluid fluid) {
		buckets.put(block, bucket);
        return bucket;
	}

	// ========== Item ==========
	public static Item addItem(String name, Item item) {
		name = name.toLowerCase();
		items.put(name, item);
        itemGroups.put(item, currentModInfo);

        /*/ Fluid Dispenser:
        if(item instanceof BucketItem) {
            IBehaviorDispenseItem ibehaviordispenseitem = new BehaviorDefaultDispenseItem() {
                private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

                public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                    ItemBucket itembucket = (ItemBucket)stack.getItem();
                    BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().getValue(BlockDispenser.FACING));
                    return itembucket.tryPlaceContainedLiquid(null, source.getWorld(), blockpos) ? new ItemStack(Items.BUCKET) : this.dispenseBehavior.dispense(source, stack);
                }
            };
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, ibehaviordispenseitem);
        }*/

        return item;
	}

	public static Item addItem(String name, Item item, int weight, int minAmount, int maxAmount) {
		return addItem(name, item);
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
		ObjectLists.addEffect(goodEffect ? "buffs" : "debuffs", effect);

		return effect;
	}

	// ========== Special Entity ==========
	public static void addSpecialEntity(String name, Class<? extends Entity> entityClass) {
		specialEntities.put(name, entityClass);

	}

    // ========== Damage Source ==========
    public static void addDamageSource(String name, DamageSource damageSource) {
        name = name.toLowerCase();
        damageSources.put(name, damageSource);
    }

	// ========== Sound ==========
	public static void addSound(String name, ModInfo modInfo, String path) {
		name = name.toLowerCase();
		ResourceLocation resourceLocation = new ResourceLocation(modInfo.modid, path);
		SoundEvent soundEvent = new SoundEvent(resourceLocation);
		soundEvent.setRegistryName(resourceLocation);
		sounds.put(name, soundEvent);
	}

    // ========== Stat ==========
    public static void addStat(String name, Stat stat) {
        name = name.toLowerCase();
        if(stats.containsKey(name))
            return;
        stats.put(name, stat);
    }
	
	
    // ==================================================
    //                        Get
    // ==================================================
	// ========== Block ==========
	public static Block getBlock(String name) {
		name = name.toLowerCase();
		if(!blocks.containsKey(name)) return null;
		return blocks.get(name);
	}
	
	// ========== Item ==========
	public static Item getItem(String name) {
		name = name.toLowerCase();
		if(!items.containsKey(name)) return null;
		return items.get(name);
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

    // ========== Stat ==========
    public static Stat getStat(String name) {
        name = name.toLowerCase();
        if(!stats.containsKey(name)) return null;
        return stats.get(name);
    }


    // ==================================================
    //                  Registry Events
    // ==================================================
    // ========== Blocks ==========
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(blocks.values().toArray(new Block[blocks.size()]));
        for(Block block : blocks.values()) {
            if(block.getRegistryName() == null) {
                LycanitesMobs.logWarning("", "Block: " + block + " has no Registry Name!");
            }
        }
    }

    // ========== Items ==========
    public static void registerItems(RegistryEvent.Register<Item> event) {
	    for(Item item : items.values()) {
	        if(item.getRegistryName() == null) {
	            LycanitesMobs.logWarning("", "Item: " + item + " has no Registry Name!");
            }
            event.getRegistry().register(item);
        }
    }

    // ========== Potions ==========
    public static void registerEffects(RegistryEvent.Register<Effect> event) {
        for(EffectBase effect : effects.values()) {
        	event.getRegistry().register(effect);
		}
    }

	// ========== Entities ==========
	public static void registerSpecialEntities(RegistryEvent.Register<EntityType<?>> event) {
		// Special Entities:
		for(String entityName : specialEntities.keySet()) {
			String registryName = LycanitesMobs.modInfo.modid + ":" + entityName;

			EntityType.Builder entityTypeBuilder = EntityType.Builder.create(EntityFactory.getInstance(), EntityClassification.MISC);
			entityTypeBuilder.setTrackingRange(10);
			entityTypeBuilder.setUpdateInterval(10);
			entityTypeBuilder.setShouldReceiveVelocityUpdates(false);
			entityTypeBuilder.disableSummoning();

			EntityType entityType = entityTypeBuilder.build(entityName);
			entityType.setRegistryName(LycanitesMobs.modid, entityName);
			try {
				EntityFactory.getInstance().addEntityType(entityType, specialEntities.get(entityName).getConstructor(EntityType.class, World.class));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			event.getRegistry().register(entityType);
		}
	}

	// ========== Sounds ==========
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		for(SoundEvent soundEvent : sounds.values()) {
			if(soundEvent.getRegistryName() == null) {
				LycanitesMobs.logWarning("", "Sound: " + soundEvent + " has no Registry Name!");
			}
			event.getRegistry().register(soundEvent);
		}
	}
}
