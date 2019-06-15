package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockSlabCustom;
import com.lycanitesmobs.core.entity.EntityFactory;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.model.ModelEquipmentPart;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.stats.Stat;
import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
		//    LycanitesMobs.printWarning("", "Another fluid was registered as " + fluidName);
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
		Utilities.addDungeonLoot(new ItemStack(item), minAmount, maxAmount, weight);
		Utilities.addStrongholdLoot(new ItemStack(item), minAmount, maxAmount, (weight * 2));
		Utilities.addVillageLoot(new ItemStack(item), minAmount, maxAmount, (weight));
		return addItem(name, item);
	}

    // ========== Tile Entity ==========
    public static Class addTileEntity(String name, Class tileEntityClass) {
        name = name.toLowerCase();
        tileEntities.put(name, tileEntityClass);
        //GameRegistry.registerTileEntity(tileEntityClass, LycanitesMobs.modid + "." + name);
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


    // ========== Stat ==========
    public static void addStat(String name, Stat stat) {
        name = name.toLowerCase();
        if(stats.containsKey(name))
            return;
        //stat.registerStat();
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
                LycanitesMobs.printWarning("", "Block: " + block + " has no Registry Name!");
            }
            //event.getRegistry().register(block); Registered with ItemBlock
			//LycanitesMobs.proxy.addBlockRender(currentModInfo, block);
        }
    }

    // ========== Items ==========
    public static void registerItems(RegistryEvent.Register<Item> event) {
	    for(Item item : items.values()) {
	        if(item.getRegistryName() == null) {
	            LycanitesMobs.printWarning("", "Item: " + item + " has no Registry Name!");
            }
            event.getRegistry().register(item);
            //LycanitesMobs.proxy.addItemRender(itemGroups.get(item), item);
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
			String registryName = LycanitesMobs.modInfo.filename + ":" + entityName;

			EntityType.Builder entityTypeBuilder = EntityType.Builder.create(EntityFactory.getInstance(), EntityClassification.MISC);
			entityTypeBuilder.setTrackingRange(80);
			entityTypeBuilder.setUpdateInterval(3);
			entityTypeBuilder.setShouldReceiveVelocityUpdates(false);

			EntityType entityType = entityTypeBuilder.build(registryName);
			EntityFactory.getInstance().addEntityType(entityType, specialEntities.get(entityName));
			event.getRegistry().register(entityType);
		}
	}


    // ==================================================
    //           Register Block and Item Models
    // ==================================================
    @OnlyIn(Dist.CLIENT)
    public static void RegisterModels() {
		for(Item item : items.values()) {
			if(item instanceof ItemBase) {
				ItemBase itemBase = (ItemBase) item;
				if (itemBase.useItemColors()) {
					Minecraft.getInstance().getItemColors().register(ClientManager.itemColor, item);
				}
			}
            if(item instanceof ItemEquipmentPart) {
				ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)item;
            	AssetManager.addItemModel(itemEquipmentPart.itemName, new ModelEquipmentPart(itemEquipmentPart.itemName, itemEquipmentPart.modInfo));
			}
        }
    }
}
