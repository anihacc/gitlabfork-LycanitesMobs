package com.lycanitesmobs.core.info;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.item.ItemCustom;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectLists {
	
	// Item List Names:
	public static String[] itemListNames = new String[] {"RawMeat", "CookedMeat", "Vegetables", "RawFish", "CookedFish", "CactusFood", "Mushrooms", "Sweets", "Fuel"};
	
	// Maps:
	public static Map<String, List<ItemStack>> itemLists = new HashMap<>();
	public static Map<String, List<Class>> entityLists = new HashMap<>();
	public static Map<String, List<Potion>> effectLists = new HashMap<>();
	
	
    // ==================================================
    //                        Add
    // ==================================================
	public static void addItem(String list, Object object) {
		if(!(object instanceof Item || object instanceof Block || object instanceof ItemStack || object instanceof String))
			return;
		list = list.toLowerCase();
		if(!itemLists.containsKey(list))
			itemLists.put(list, new ArrayList<>());
		ItemStack itemStack = null;
		
		if(object instanceof Item)
			itemStack = new ItemStack((Item)object);
		else if(object instanceof Block)
			itemStack = new ItemStack((Block)object);
		else if(object instanceof ItemStack)
			itemStack = (ItemStack)object;
		else if(object instanceof String)
			if(ObjectManager.getItem((String)object) != null)
				itemStack = new ItemStack(ObjectManager.getItem((String)object));
			else if(ObjectManager.getBlock((String)object) != null)
				itemStack = new ItemStack(ObjectManager.getBlock((String)object));
		
		if(itemStack != null)
			itemLists.get(list).add(itemStack);
	}
	
	public static void addEntity(String list, Object object) {
		if(!(object instanceof Entity || object instanceof String))
			return;
		list = list.toLowerCase();
		if(!entityLists.containsKey(list))
			entityLists.put(list, new ArrayList<>());

		Class entity = null;
		if(object instanceof String) {
			CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature((String) object);
			if(creatureInfo != null) {
				entity = creatureInfo.entityClass;
			}
		}
		if(entity != null) {
			entityLists.get(list).add(entity);
		}
	}
	
	public static void addEffect(String list, Potion potion) {
		if(potion == null)
			return;
		list = list.toLowerCase();
		if(!effectLists.containsKey(list))
			effectLists.put(list, new ArrayList<>());
		effectLists.get(list).add(potion);
	}
	

    // ==================================================
    //                        Get
    // ==================================================
	public static ItemStack[] getItems(String list) {
		list = list.toLowerCase();
		if(!itemLists.containsKey(list))
			return new ItemStack[0];
		return itemLists.get(list).toArray(new ItemStack[itemLists.get(list).size()]);
	}

	public static Class[] getEntites(String list) {
		list = list.toLowerCase();
		if(!entityLists.containsKey(list))
			return new Class[0];
		return entityLists.get(list).toArray(new Class[entityLists.get(list).size()]);
	}

	public static Potion[] getEffects(String list) {
		list = list.toLowerCase();
		if(!effectLists.containsKey(list))
			return new Potion[0];
		return effectLists.get(list).toArray(new Potion[effectLists.get(list).size()]);
	}
	

    // ==================================================
    //                      Compare
    // ==================================================
	public static boolean inItemList(String list, ItemStack testStack) {
		list = list.toLowerCase();
        if(testStack.isEmpty()) {
			return false;
		}
		else {
			testStack.getItem();
		}
		if(!itemLists.containsKey(list))
			return false;
		for(ItemStack listStack : itemLists.get(list))
			if(testStack.getItem() == listStack.getItem()
			&& testStack.getItemDamage() == listStack.getItemDamage())
				return true;
		return false;
	}

	public static boolean inEntityList(String list, Class testClass) {
		list = list.toLowerCase();
		if(!entityLists.containsKey(list))
			return false;
		return false;
	}

	public static boolean inEffectList(String list, Potion testPotion) {
		list = list.toLowerCase();
		if(!effectLists.containsKey(list))
			return false;
		return effectLists.get(list).contains(testPotion);
	}

    public static boolean isInOreDictionary(String oreEntry, Item item) {
        return isInOreDictionary(oreEntry, new ItemStack(item));
    }
    public static boolean isInOreDictionary(String oreEntry, Block block) {
        return isInOreDictionary(oreEntry, new ItemStack(block));
    }
    public static boolean isInOreDictionary(String oreEntry, ItemStack itemStack) {
        if(itemStack == null) return false;
        List<ItemStack> ores = OreDictionary.getOres(oreEntry);
        if(ores == null) return false;
        for(ItemStack ore : ores)
            if(ore.getItem() == itemStack.getItem())
                return true;
        return false;
    }


    // ==================================================
    //               Create Custom Items
    // ==================================================
    public static void createCustomItems() {
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "itemlists");
        config.setCategoryComment("Custom Objects", "here you can add your own custom items! These items wont do anything and will have no crafting recipes you can however have them drop from mobs by adding them to the custom mob drops. To add items just create a comma seperated list of names (spaces will be removed), you will need to use the item.youritem.name and item.youritem.description lang file entries in your resource pack to name your item and you will also need to save a texture for your item as: assets/lycanitesmobs/items/youritem.png");
        String customItems = config.getString("Custom Objects", "Custom Items", "");
        if("".equals(customItems))
            return;
        for(String itemEntry : customItems.replace(" ", "").split(",")) {
            ObjectManager.addItem(itemEntry, new ItemCustom(itemEntry, LycanitesMobs.modInfo));
        }
    }
	
	
    // ==================================================
    //                   Create Lists
    // ==================================================
	public static void createLists() {
		// ========== Item Lists ==========
		// Raw Meat:
		ObjectLists.addItem("diet_carnivore", Items.BEEF);
		ObjectLists.addItem("diet_carnivore", Items.PORKCHOP);
		ObjectLists.addItem("diet_carnivore", Items.CHICKEN);
		ObjectLists.addItem("diet_carnivore", Items.RABBIT);
		ObjectLists.addItem("diet_carnivore", Items.MUTTON);

		// Cooked Meat:
		ObjectLists.addItem("diet_carnivore", Items.COOKED_BEEF);
		ObjectLists.addItem("diet_carnivore", Items.COOKED_PORKCHOP);
		ObjectLists.addItem("diet_carnivore", Items.COOKED_CHICKEN);
		ObjectLists.addItem("diet_carnivore", Items.COOKED_RABBIT);
		ObjectLists.addItem("diet_carnivore", Items.COOKED_MUTTON);

		// Raw Fish:
		ObjectLists.addItem("diet_piscivore", Items.FISH);
		ObjectLists.addItem("diet_piscivore", new ItemStack(Items.FISH, 1, 1)); // Salmon
		ObjectLists.addItem("diet_piscivore", new ItemStack(Items.FISH, 1, 2)); // Clown
		ObjectLists.addItem("diet_piscivore", new ItemStack(Items.FISH, 1, 3)); // Puffer
		// Cooked Fish:
		ObjectLists.addItem("diet_piscivore", Items.COOKED_FISH);
		ObjectLists.addItem("diet_piscivore", new ItemStack(Items.COOKED_FISH, 1, 1)); // Cooked Salmon

		// Vegetables:
		ObjectLists.addItem("diet_herbivore", Items.WHEAT);
		ObjectLists.addItem("diet_herbivore", Items.CARROT);
		ObjectLists.addItem("diet_herbivore", Items.POTATO);
		
		// Fruit:
		ObjectLists.addItem("diet_frugivore", Items.APPLE);
		ObjectLists.addItem("diet_frugivore", Items.MELON);
		ObjectLists.addItem("diet_frugivore", Blocks.PUMPKIN);

		// Sweets:
		ObjectLists.addItem("diet_frugivore", Items.SUGAR);
		ObjectLists.addItem("diet_frugivore", new ItemStack(Items.DYE, 1, 15)); // Cocoa Beans
		ObjectLists.addItem("diet_frugivore", Items.COOKIE);
		ObjectLists.addItem("diet_frugivore", Blocks.CAKE);
		ObjectLists.addItem("diet_frugivore", Items.PUMPKIN_PIE);
		
		// Extracts:
		ObjectLists.addItem("diet_exudativore", new ItemStack(Items.DYE, 1, 2)); // Cactus Green
		
		// Mushrooms:
        ObjectLists.addItem("diet_fungivore", Items.MUSHROOM_STEW);
        ObjectLists.addItem("diet_fungivore", Blocks.RED_MUSHROOM);
		ObjectLists.addItem("diet_fungivore", Blocks.BROWN_MUSHROOM);
		ObjectLists.addItem("diet_fungivore", Blocks.NETHER_WART);

		// Rotten:
		ObjectLists.addItem("diet_detritivore", Items.ROTTEN_FLESH);
		ObjectLists.addItem("diet_detritivore", Items.FERMENTED_SPIDER_EYE);

		// Minerals:
		ObjectLists.addItem("diet_geovore", Items.COAL);
		ObjectLists.addItem("diet_geovore", new ItemStack(Items.DYE, 1, 4)); // Lapis Lazuli
		ObjectLists.addItem("diet_geovore", Items.DIAMOND);
		ObjectLists.addItem("diet_geovore", Items.EMERALD);

		// Insectoid:
		ObjectLists.addItem("diet_insectivore", Items.SPIDER_EYE);
		ObjectLists.addItem("diet_insectivore", Items.FERMENTED_SPIDER_EYE);

		// Custom Entries:
		for(String itemListName : itemListNames) {
			addFromConfig(itemListName.toLowerCase());
		}
		
		// ========== Effects ==========
		// Buffs:
		ObjectLists.addEffect("buffs", MobEffects.STRENGTH);
		ObjectLists.addEffect("buffs", MobEffects.HASTE);
		ObjectLists.addEffect("buffs", MobEffects.FIRE_RESISTANCE);
		ObjectLists.addEffect("buffs", MobEffects.INSTANT_HEALTH);
		ObjectLists.addEffect("buffs", MobEffects.INVISIBILITY);
		ObjectLists.addEffect("buffs", MobEffects.JUMP_BOOST);
		ObjectLists.addEffect("buffs", MobEffects.SPEED);
		ObjectLists.addEffect("buffs", MobEffects.NIGHT_VISION);
		ObjectLists.addEffect("buffs", MobEffects.REGENERATION);
		ObjectLists.addEffect("buffs", MobEffects.RESISTANCE);
		ObjectLists.addEffect("buffs", MobEffects.WATER_BREATHING);
		ObjectLists.addEffect("buffs", MobEffects.HEALTH_BOOST);
		ObjectLists.addEffect("buffs", MobEffects.ABSORPTION);
		ObjectLists.addEffect("buffs", MobEffects.SATURATION);
        ObjectLists.addEffect("buffs", MobEffects.GLOWING);
        ObjectLists.addEffect("buffs", MobEffects.LEVITATION);
        ObjectLists.addEffect("buffs", MobEffects.LUCK);
		
		// Debuffs:
        ObjectLists.addEffect("buffs", MobEffects.BLINDNESS);
        ObjectLists.addEffect("buffs", MobEffects.NAUSEA);
        ObjectLists.addEffect("buffs", MobEffects.MINING_FATIGUE);
        ObjectLists.addEffect("buffs", MobEffects.INSTANT_DAMAGE);
        ObjectLists.addEffect("buffs", MobEffects.HUNGER);
        ObjectLists.addEffect("buffs", MobEffects.SLOWNESS);
        ObjectLists.addEffect("buffs", MobEffects.POISON);
        ObjectLists.addEffect("buffs", MobEffects.WEAKNESS);
        ObjectLists.addEffect("buffs", MobEffects.WITHER);
        ObjectLists.addEffect("buffs", MobEffects.UNLUCK);
	}
	
	// ========== Add From Config Value ==========
	public static void addFromConfig(String listName) {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "itemlists");
		config.setCategoryComment("item lists", "Here you can add items from vanilla Minecraft or other mods to various lists used by this mod. These are mostly food items that can be fed to farmable/tameable mobs. Format is: mod:itemname,metadata Multiple entries should be semicolon separated, be sure to use a colon and semicolon in the correct place.");
		String customDropsString = config.getString("Item Lists", listName).replace(" ", "");
		LycanitesMobs.logDebug("ItemSetup", "~O========== Custom " + listName + " ==========O~");
		if(customDropsString != null && customDropsString.length() > 0) {
			for (String customDropEntryString : customDropsString.replace(" ", "").split(";")) {
				LycanitesMobs.logDebug("ItemSetup", "Adding: " + customDropEntryString);
				String[] customDropValues = customDropEntryString.split(",");
				String dropName = customDropValues[0];
				int dropMeta = 0;
				if (customDropValues.length > 1)
					dropMeta = Integer.parseInt(customDropValues[1]);
				if (Item.getByNameOrId(dropName) != null) {
					Item customItem = Item.getByNameOrId(dropName);
					ObjectLists.addItem(listName, new ItemStack(customItem, 1, dropMeta));
					LycanitesMobs.logDebug("ItemSetup", "As Item: " + customItem);
				}
				else if (Block.getBlockFromName(dropName) != null) {
					Block customBlock = Block.getBlockFromName(dropName);
					ObjectLists.addItem(listName, new ItemStack(customBlock, 1, dropMeta));
					LycanitesMobs.logDebug("ItemSetup", "As Block: " + customBlock);
				}
			}
		}
	}
	
	
    // ==================================================
    //                   Check Tools
    // ==================================================
    // ========== Sword ==========
	public static boolean isSword(Item item) {
		if(item == null)
			return false;
		if(item instanceof ItemSword)
			return true;
		if(item instanceof ItemShears)
			return false;
		return item.getDestroySpeed(new ItemStack(item), Blocks.MELON_BLOCK.getDefaultState()) > 1F;
	}

    // ========== Pickaxe ==========
	public static boolean isPickaxe(Item item) {
		if(item == null)
			return false;
        try {

            // Check Tinkers Tool:
            String[] toolNameParts = item.getUnlocalizedName().split("\\.");
            if(toolNameParts.length >= 3 && "InfiTool".equalsIgnoreCase(toolNameParts[1])) {
                String toolName = toolNameParts[2];
                if("Pickaxe".equalsIgnoreCase(toolName) || "Hammer".equalsIgnoreCase(toolName))
                    return true;
                return false;
            }

            // Vanilla Based Checks:
            if(item instanceof ItemPickaxe)
                return true;
            if(item.getHarvestLevel(new ItemStack(item), "pickaxe", null, null) != -1)
                return true;
            return item.getDestroySpeed(new ItemStack(item), Blocks.STONE.getDefaultState()) > 1F;

        }
        catch(Exception e) {}
        return false;
	}

    // ========== Axe ==========
	public static boolean isAxe(Item item) {
        if(item == null)
            return false;
        try {

            // Check Tinkers Tool:
            String[] toolNameParts = item.getUnlocalizedName().split("\\.");
            for(String toolNamePart : toolNameParts)
            if(toolNameParts.length >= 3 && "InfiTool".equalsIgnoreCase(toolNameParts[1])) {
                String toolName = toolNameParts[2];
                if("Axe".equalsIgnoreCase(toolName) || "LumberAxe".equalsIgnoreCase(toolName) || "Mattock".equalsIgnoreCase(toolName) || "Battleaxe".equalsIgnoreCase(toolName))
                    return true;
                return false;
            }

            // Vanilla Based Checks:
            if(item instanceof ItemAxe)
                return true;
            if(item.getHarvestLevel(new ItemStack(item), "axe", null, null) != -1)
                return true;
            return item.getDestroySpeed(new ItemStack(item), Blocks.LOG.getDefaultState()) > 1F;

        }
        catch(Exception e) {}
        return false;
	}

    // ========== Shovel ==========
	public static boolean isShovel(Item item) {
		if(item == null)
            return false;
        try {

            // Check Tinkers Tool:
            String[] toolNameParts = item.getUnlocalizedName().split("\\.");
            if(toolNameParts.length >= 3 && "InfiTool".equalsIgnoreCase(toolNameParts[1])) {
                String toolName = toolNameParts[2];
                if("Shovel".equalsIgnoreCase(toolName) || "Excavator".equalsIgnoreCase(toolName) || "Mattock".equalsIgnoreCase(toolName))
                    return true;
                return false;
            }

            // Vanilla Based Checks:
            if(item instanceof ItemSpade)
                return true;
            if(item.getHarvestLevel(new ItemStack(item), "shovel", null, null) != -1)
                return true;
            return item.getDestroySpeed(new ItemStack(item), Blocks.DIRT.getDefaultState()) > 1F;

        }
        catch(Exception e) {}
        return false;
	}

	
    // ==================================================
    //                   Check Names
    // ==================================================
	public static boolean isName(Item item, String name) {
		if(item == null)
			return false;
		String itemName = item.getUnlocalizedName().toLowerCase();
		if(itemName.contains(name))
			return true;
		return false;
	}

	public static boolean isName(Block block, String name) {
		if(block == null)
			return false;
		name = name.toLowerCase();
		String blockName = block.getUnlocalizedName().toLowerCase();
		if(blockName.contains(name)) {
			return true;
		}
		return false;
	}
}
