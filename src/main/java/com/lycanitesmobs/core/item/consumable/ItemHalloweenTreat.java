package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CustomItemEntity;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class ItemHalloweenTreat extends BaseItem {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemHalloweenTreat(Item.Properties properties) {
		super(properties);
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "halloweentreat";
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
         if(!player.abilities.isCreativeMode) {
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
		ITextComponent message = new TranslationTextComponent("item." + this.itemName + ".good");
		player.sendMessage(message);
        this.playSound(world, player.posX, player.posY, player.posZ, ObjectManager.getSound(this.itemName + "_good"), SoundCategory.AMBIENT, 5.0F, 1.0F);
		
		// Three Random Treats:
		List<ItemStack> dropStacks = ObjectLists.getItems("halloween_treats");
		if(dropStacks == null || dropStacks.isEmpty())
			return;
		ItemStack dropStack = dropStacks.get(player.getRNG().nextInt(dropStacks.size()));
		dropStack.setCount(1 + player.getRNG().nextInt(4));
		CustomItemEntity entityItem = new CustomItemEntity(world, player.posX, player.posY, player.posZ, dropStack);
		entityItem.setPickupDelay(10);
		world.addEntity(entityItem);
    }
    
    
    // ==================================================
  	//                       Bad
  	// ==================================================
    public void openBad(ItemStack itemStack, World world, PlayerEntity player) {
		ITextComponent message = new TranslationTextComponent("item." + this.itemName + ".bad");
		player.sendMessage(message);
        this.playSound(world, player.posX, player.posY, player.posZ, ObjectManager.getSound(this.itemName + "_bad"), SoundCategory.AMBIENT, 5.0F, 1.0F);
		
		// One Random Trick:
		List<EntityType> entityTypes = ObjectLists.getEntites("halloween_tricks");
		if(entityTypes.isEmpty())
			return;
		EntityType entityType = entityTypes.get(player.getRNG().nextInt(entityTypes.size()));
		if(entityType != null) {
			Entity entity = entityType.create(world);
            if(entity != null) {
	            entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

                // Themed Names:
                if (entity instanceof BaseCreatureEntity) {
                    BaseCreatureEntity entityCreature = (BaseCreatureEntity) entity;
					entityCreature.addLevel(world.rand.nextInt(10));
                    if (entityCreature.creatureInfo.getName().equals("ent"))
                        entityCreature.setCustomName(new TranslationTextComponent("Twisted Ent"));
					else if (entityCreature.creatureInfo.getName().equals("treant"))
						entityCreature.setCustomName(new TranslationTextComponent("Wicked Treant"));
					else if (entityCreature.creatureInfo.getName().equals("epion"))
						entityCreature.setCustomName(new TranslationTextComponent("Vampire Bat"));
					else if (entityCreature.creatureInfo.getName().equals("grue"))
						entityCreature.setCustomName(new TranslationTextComponent("Shadow Clown"));
                }

	            world.addEntity(entity);
            }
		}
    }


    // ==================================================
    //                       Lists
    // ==================================================
    public static void createObjectLists() {
        // Halloween Treats:
        ObjectLists.addItem("halloween_treats", Items.DIAMOND);
        ObjectLists.addItem("halloween_treats", Items.GOLD_INGOT);
        ObjectLists.addItem("halloween_treats", Items.EMERALD);
        ObjectLists.addItem("halloween_treats", Blocks.IRON_BLOCK);
        ObjectLists.addItem("halloween_treats", Items.ENDER_PEARL);
        ObjectLists.addItem("halloween_treats", Items.BLAZE_ROD);
        ObjectLists.addItem("halloween_treats", Items.GLOWSTONE_DUST);
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("mosspie"));
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("bulwarkburger"));
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("paleosalad"));
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("searingtaco"));
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("devillasagna"));
        ObjectLists.addFromConfig("halloween_treats");

        // Halloween Mobs:
        ObjectLists.addEntity("halloween_tricks", "behemoth");
        ObjectLists.addEntity("halloween_tricks", "ent");
        ObjectLists.addEntity("halloween_tricks", "treant");
        ObjectLists.addEntity("halloween_tricks", "wraith");
        ObjectLists.addEntity("halloween_tricks", "grue");
        ObjectLists.addEntity("halloween_tricks", "phantom");
        ObjectLists.addEntity("halloween_tricks", "epion");
    }
}
