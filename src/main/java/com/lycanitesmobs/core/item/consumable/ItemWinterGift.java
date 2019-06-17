package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemWinterGift extends ItemBase {

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
     public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(!player.playerAbilities.isCreativeMode) {
            itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
        }

        if(!world.isRemote) {
        if(player.getRNG().nextBoolean())
            this.openGood(itemStack, world, player);
        else
            this.openBad(itemStack, world, player);
        }

        return new ActionResult(ActionResultType.SUCCESS, itemStack);
     }
    
    
    // ==================================================
  	//                       Good
  	// ==================================================
    public void openGood(ItemStack itemStack, World world, PlayerEntity player) {
    	String message = LanguageManager.translate("item." + this.itemName + ".good");
		player.sendMessage(new TranslationTextComponent(message));
        this.playSound(world, player.getPosition(), ObjectManager.getSound(this.itemName + "_good"), SoundCategory.AMBIENT, 5.0F, 1.0F);
		
		// Three Random Gifts:
		for(int i = 0; i < 3; i++) {
			ItemStack[] dropStacks = ObjectLists.getItems("winter_gifts");
			if(dropStacks == null || dropStacks.length <= 0) return;
			ItemStack dropStack = dropStacks[player.getRNG().nextInt(dropStacks.length)];
			if(dropStack != null && dropStack.getItem() != null) {
				dropStack.setCount(1 + player.getRNG().nextInt(4));
				EntityItemCustom entityItem = new EntityItemCustom(world, player.posX, player.posY, player.posZ, dropStack);
				entityItem.setPickupDelay(10);
				world.func_217376_c(entityItem);
			}
		}
    }
    
    
    // ==================================================
  	//                       Bad
  	// ==================================================
    public void openBad(ItemStack itemStack, World world, PlayerEntity player) {
    	String message = LanguageManager.translate("item." + this.itemName + ".bad");
		player.sendMessage(new TranslationTextComponent(message));
        this.playSound(world, player.getPosition(), ObjectManager.getSound(this.itemName + "_bad"), SoundCategory.AMBIENT, 5.0F, 1.0F);

        // One Random Trick:
		Class[] entityClasses = ObjectLists.getEntites("winter_tricks");
        if(entityClasses == null) return;
        if(entityClasses.length <= 0) return;
		Class entityClass = entityClasses[player.getRNG().nextInt(entityClasses.length)];
		if(entityClass != null) {
			Entity entity = null;
            try {
                entity = (Entity)entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
            } catch (Exception e) { e.printStackTrace(); }
            if(entity != null) {
	            entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

                // Themed Names:
                if (entity instanceof EntityCreatureBase) {
                    EntityCreatureBase entityCreature = (EntityCreatureBase) entity;
					entityCreature.addLevel(world.rand.nextInt(10));
                    if (entityCreature.creatureInfo.getName().equals("wildkin"))
                        entityCreature.setCustomName(new TranslationTextComponent("Gooderness"));
                    else if (entityCreature.creatureInfo.getName().equals("jabberwock"))
                        entityCreature.setCustomName(new TranslationTextComponent("Rudolph"));
                    else if (entityCreature.creatureInfo.getName().equals("ent"))
                        entityCreature.setCustomName(new TranslationTextComponent("Salty Tree"));
                    else if (entityCreature.creatureInfo.getName().equals("treant"))
                        entityCreature.setCustomName(new TranslationTextComponent("Salty Tree"));
                    else if (entityCreature.creatureInfo.getName().equals("phantom"))
                        entityCreature.setCustomName(new TranslationTextComponent("Satan Claws"));
                    else if(entityCreature.creatureInfo.getName().equals("behemoth"))
                        entityCreature.setCustomName(new TranslationTextComponent("Krampus"));
                }

	            world.func_217376_c(entity);
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
        ObjectLists.addEntity("winter_tricks", "phantom");
        ObjectLists.addEntity("winter_tricks", "behemoth");
    }
}
