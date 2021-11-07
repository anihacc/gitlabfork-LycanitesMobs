package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemImmunizer extends BaseItem {

    public ItemImmunizer(Item.Properties properties) {
		super(properties);
        this.modInfo = LycanitesMobs.modInfo;
        this.itemName = "immunizer";
        this.setup();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
			if(!player.getAbilities().instabuild) {
				itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
			}

			if(!world.isClientSide && ObjectManager.getEffect("immunization") != null) {
				player.addEffect(new MobEffectInstance(ObjectManager.getEffect("immunization"), 10 * 20));
			}

			return new InteractionResultHolder(InteractionResult.SUCCESS, itemStack);
		}
}
