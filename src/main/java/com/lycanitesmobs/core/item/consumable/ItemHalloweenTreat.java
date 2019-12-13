package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.world.World;

public class ItemHalloweenTreat extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemHalloweenTreat() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "halloweentreat";
        this.setup();
		AssetManager.addSound(this.itemName + "_good", this.modInfo, "item." + this.itemName + ".good");
        AssetManager.addSound(this.itemName + "_bad", this.modInfo, "item." + this.itemName + ".bad");
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
         if(!player.capabilities.isCreativeMode) {
             itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
         }
         
         if(!world.isRemote) {
         	if(player.getRNG().nextBoolean())
         		this.openGood(itemStack, world, player);
         	else
         		this.openBad(itemStack, world, player);
         }

        return new ActionResult(EnumActionResult.SUCCESS, itemStack);
     }
    
    
    // ==================================================
  	//                       Good
  	// ==================================================
    public void openGood(ItemStack itemStack, World world, EntityPlayer player) {
    	String message = LanguageManager.translate("item." + this.itemName + ".good");
		player.sendMessage(new TextComponentString(message));
        this.playSound(world, player.posX, player.posY, player.posZ, AssetManager.getSound(this.itemName + "_good"), SoundCategory.AMBIENT, 5.0F, 1.0F);
		
		// Three Random Treats:
		ItemStack[] dropStacks = ObjectLists.getItems("halloween_treats");
		if(dropStacks.length <= 0)
			return;
		ItemStack dropStack = dropStacks[player.getRNG().nextInt(dropStacks.length)];
		if(dropStack != null) {
			dropStack.setCount(1 + player.getRNG().nextInt(4));
			EntityItemCustom entityItem = new EntityItemCustom(world, player.posX, player.posY, player.posZ, dropStack);
			entityItem.setPickupDelay(10);
			world.spawnEntity(entityItem);
		}
    }
    
    
    // ==================================================
  	//                       Bad
  	// ==================================================
    public void openBad(ItemStack itemStack, World world, EntityPlayer player) {
    	String message = LanguageManager.translate("item." + this.itemName + ".bad");
		player.sendMessage(new TextComponentString(message));
        this.playSound(world, player.posX, player.posY, player.posZ, AssetManager.getSound(this.itemName + "_bad"), SoundCategory.AMBIENT, 5.0F, 1.0F);
		
		// One Random Trick:
		Class[] entityClasses = ObjectLists.getEntites("halloween_tricks");
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
                if (entity instanceof BaseCreatureEntity) {
                    BaseCreatureEntity entityCreature = (BaseCreatureEntity) entity;
					entityCreature.addLevel(world.rand.nextInt(10));
                    if (entityCreature.creatureInfo.getName().equals("ent"))
                        entityCreature.setCustomNameTag("Twisted Ent");
					else if (entityCreature.creatureInfo.getName().equals("treant"))
						entityCreature.setCustomNameTag("Wicked Treant");
					else if (entityCreature.creatureInfo.getName().equals("epion"))
						entityCreature.setCustomNameTag("Vampire Bat");
					else if (entityCreature.creatureInfo.getName().equals("grue"))
						entityCreature.setCustomNameTag("Shadow Clown");
                }

	            world.spawnEntity(entity);
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
        ObjectLists.addEntity("halloween_tricks", "reaper");
        ObjectLists.addEntity("halloween_tricks", "epion");
    }
}
