package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemImmunizer extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemImmunizer(Item.Properties properties) {
		super(properties);
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "immunizer";
        this.setup();
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

			if(!world.isRemote && ObjectManager.getEffect("immunization") != null) {
				player.addPotionEffect(new EffectInstance(ObjectManager.getEffect("immunization"), 10 * 20));
			}

			return new ActionResult(ActionResultType.SUCCESS, itemStack);
		}
}
