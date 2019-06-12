package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemCleansingCrystal extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemCleansingCrystal() {
        super();
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "cleansingcrystal";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
			if(!player.capabilities.isCreativeMode) {
				itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
			}

			if(!world.isRemote && ObjectManager.getEffect("cleansed") != null) {
				player.addPotionEffect(new PotionEffect(ObjectManager.getEffect("cleansed"), 10 * 20));
			}

			return new ActionResult(EnumActionResult.SUCCESS, itemStack);
		}
}
