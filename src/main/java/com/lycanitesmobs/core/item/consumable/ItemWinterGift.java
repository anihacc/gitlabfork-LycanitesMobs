package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CustomItemEntity;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.util.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import java.util.List;

import net.minecraft.Util;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;

public class ItemWinterGift extends BaseItem {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemWinterGift(Item.Properties properties) {
		super(properties);
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "wintergift";
        this.setup();
		ObjectManager.addSound(this.itemName + "_good", this.modInfo, "item." + this.itemName + ".good");
		ObjectManager.addSound(this.itemName + "_bad", this.modInfo, "item." + this.itemName + ".bad");
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
     public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(!player.abilities.instabuild) {
            itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
        }

        if(!world.isClientSide) {
        if(player.getRandom().nextBoolean())
            this.openGood(itemStack, world, player);
        else
            this.openBad(itemStack, world, player);
        }

        return new InteractionResultHolder(InteractionResult.SUCCESS, itemStack);
     }
    
    
    // ==================================================
  	//                       Good
  	// ==================================================
    public void openGood(ItemStack itemStack, Level world, Player player) {
		Component message = new TranslatableComponent("item.lycanitesmobs." + this.itemName + ".good");
		player.sendMessage(message, Util.NIL_UUID);
        this.playSound(world, player.blockPosition(), ObjectManager.getSound(this.itemName + "_good"), SoundSource.AMBIENT, 5.0F, 1.0F);
		
		// Three Random Gifts:
		List<ItemStack> dropStacks = ObjectLists.getItems("winter_gifts");
		if(dropStacks == null || dropStacks.isEmpty())
			return;
		ItemStack dropStack = dropStacks.get(player.getRandom().nextInt(dropStacks.size()));
		dropStack.setCount(1 + player.getRandom().nextInt(4));
		CustomItemEntity entityItem = new CustomItemEntity(world, player.position().x(), player.position().y(), player.position().z(), dropStack);
		entityItem.setPickUpDelay(10);
		world.addFreshEntity(entityItem);
    }
    
    
    // ==================================================
  	//                       Bad
  	// ==================================================
    public void openBad(ItemStack itemStack, Level world, Player player) {
		Component message = new TranslatableComponent("item.lycanitesmobs." + this.itemName + ".bad");
		player.sendMessage(message, Util.NIL_UUID);
        this.playSound(world, player.blockPosition(), ObjectManager.getSound(this.itemName + "_bad"), SoundSource.AMBIENT, 5.0F, 1.0F);

        // One Random Trick:
		List<EntityType> entityTypes = ObjectLists.getEntites("winter_tricks");
		if(entityTypes.isEmpty())
			return;
		EntityType entityType = entityTypes.get(player.getRandom().nextInt(entityTypes.size()));
		if(entityType != null) {
			Entity entity = entityType.create(world);
            if(entity != null) {
	            entity.moveTo(player.position().x(), player.position().y(), player.position().z(), player.yRot, player.xRot);

                // Themed Names:
                if (entity instanceof BaseCreatureEntity) {
                    BaseCreatureEntity entityCreature = (BaseCreatureEntity) entity;
					entityCreature.addLevel(world.random.nextInt(10));
                    if (entityCreature.creatureInfo.getName().equals("wildkin"))
                        entityCreature.setCustomName(new TextComponent("Gooderness"));
                    else if (entityCreature.creatureInfo.getName().equals("jabberwock"))
                        entityCreature.setCustomName(new TextComponent("Rudolph"));
                    else if (entityCreature.creatureInfo.getName().equals("ent"))
                        entityCreature.setCustomName(new TextComponent("Salty Tree"));
                    else if (entityCreature.creatureInfo.getName().equals("treant"))
                        entityCreature.setCustomName(new TextComponent("Salty Tree"));
                    else if (entityCreature.creatureInfo.getName().equals("reaper"))
                        entityCreature.setCustomName(new TextComponent("Satan Claws"));
                    else if(entityCreature.creatureInfo.getName().equals("behemoth"))
                        entityCreature.setCustomName(new TextComponent("Krampus"));
                }

	            world.addFreshEntity(entity);
            }
		}
    }


    // ==================================================
    //                       Lists
    // ==================================================
    public static void createObjectLists() {
        // Halloween Treats:
        ObjectLists.addItem("winter_gifts", Items.DIAMOND);
        ObjectLists.addItem("winter_gifts", Items.GOLD_INGOT);
        ObjectLists.addItem("winter_gifts", Items.EMERALD);
        ObjectLists.addItem("winter_gifts", Blocks.IRON_BLOCK);
        ObjectLists.addItem("winter_gifts", Items.ENDER_PEARL);
        ObjectLists.addItem("winter_gifts", Items.BLAZE_ROD);
        ObjectLists.addItem("winter_gifts", Items.GLOWSTONE_DUST);
        ObjectLists.addItem("winter_gifts", Items.COAL);
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("mosspie"));
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("ambercake"));
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("peakskebab"));
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("bulwarkburger"));
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("palesoup"));
        ObjectLists.addFromConfig("winter_gifts");

        // Halloween Mobs:
        ObjectLists.addEntity("winter_tricks", "wildkin");
        ObjectLists.addEntity("winter_tricks", "jabberwock");
        ObjectLists.addEntity("winter_tricks", "ent");
        ObjectLists.addEntity("winter_tricks", "treant");
        ObjectLists.addEntity("winter_tricks", "reaper");
        ObjectLists.addEntity("winter_tricks", "behemoth");
    }
}
