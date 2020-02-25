package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.world.World;

public class ItemWinterGiftLarge extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemWinterGiftLarge() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "wintergiftlarge";
        this.setup();
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
         	this.open(itemStack, world, player);
         }

        return new ActionResult(EnumActionResult.SUCCESS, itemStack);
     }
    
    
    // ==================================================
  	//                       Open
  	// ==================================================
    public void open(ItemStack itemStack, World world, EntityPlayer player) {
    	String message = LanguageManager.translate("item." + this.itemName + ".bad");
		player.sendMessage(new TextComponentString(message));
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
                    if (entity instanceof BaseCreatureEntity) {
                        BaseCreatureEntity entityCreature = (BaseCreatureEntity) entity;
                        entityCreature.addLevel(world.rand.nextInt(10));
                        if (entityCreature.creatureInfo.getName().equals("wildkin"))
                            entityCreature.setCustomNameTag("Gooderness");
                        else if (entityCreature.creatureInfo.getName().equals("jabberwock"))
                            entityCreature.setCustomNameTag("Rudolph");
                        else if (entityCreature.creatureInfo.getName().equals("ent"))
                            entityCreature.setCustomNameTag("Salty Tree");
                        else if (entityCreature.creatureInfo.getName().equals("treant"))
                            entityCreature.setCustomNameTag("Salty Tree");
                        else if (entityCreature.creatureInfo.getName().equals("reaper"))
                            entityCreature.setCustomNameTag("Satan Claws");
                        else if(entityCreature.creatureInfo.getName().equals("behemoth"))
                            entityCreature.setCustomNameTag("Krampus");
                    }

                    world.spawnEntity(entity);
                }
            }
        }
    }
}
