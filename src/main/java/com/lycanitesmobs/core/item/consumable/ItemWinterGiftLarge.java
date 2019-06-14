package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemWinterGiftLarge extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemWinterGiftLarge(Item.Properties properties) {
        super(properties);
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "wintergiftlarge";
        this.setup();
        AssetManager.addSound(this.itemName + "_bad", this.modInfo, "item." + this.itemName + ".bad");
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
         	this.open(itemStack, world, player);
         }

        return new ActionResult(ActionResultType.SUCCESS, itemStack);
     }
    
    
    // ==================================================
  	//                       Open
  	// ==================================================
    public void open(ItemStack itemStack, World world, PlayerEntity player) {
    	String message = LanguageManager.translate("item." + this.itemName + ".bad");
		player.sendMessage(new TranslationTextComponent(message));
        this.playSound(world, player.getPosition(), AssetManager.getSound(this.itemName + "_bad"), SoundCategory.AMBIENT, 5.0F, 1.0F);
		
		// Lots of Random Tricks:
		Class[] entityClasses = ObjectLists.getEntites("winter_tricks");
        if(entityClasses == null) return;
		if(entityClasses.length <= 0) return;
        for(int i = 0; i < 15; i++) {
            Class entityClass = entityClasses[player.getRNG().nextInt(entityClasses.length)];
            if(entityClass != null) {
                Entity entity = null;
                try {
                    entity = (Entity) entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (entity != null) {
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
    }
}
