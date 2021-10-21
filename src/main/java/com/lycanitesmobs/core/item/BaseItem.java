package com.lycanitesmobs.core.item;

import com.google.common.collect.Multimap;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.UseAnim;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;

public class BaseItem extends Item {
	public static int DESCRIPTION_WIDTH = 200;
	
	public String itemName = "unamed_item";
	public ModInfo modInfo = LycanitesMobs.modInfo;

    public BaseItem(Properties properties) {
    	super(properties);
    }

    public void setup() {
        this.setRegistryName(this.modInfo.modid, this.itemName);
    }

    @Override
	@Nonnull
	public String getDescriptionId() {
    	return "item." + this.modInfo.modid + "." + this.itemName;
	}

	@Override
	public Component getName(ItemStack stack) {
		return new TranslatableComponent(this.getDescriptionId(stack));
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
		Component description = this.getDescription(stack, worldIn, tooltip, flag);
    	if(!"".equalsIgnoreCase(description.getString())) {
			tooltip.add(description);
    	}
    	super.appendHoverText(stack, worldIn, tooltip, flag);
    }

    public Component getDescription(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
    	return new TranslatableComponent(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GREEN);
    }

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		return super.onEntityItemUpdate(stack, entity);
	}

    @Override
    public InteractionResult useOn(UseOnContext context) {
    	return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    	return super.use(world, player, hand);
    }

	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		return super.onLeftClickEntity(stack, player, entity);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
    	return super.interactLivingEntity(stack, player, entity, hand);
	}

    @Override
    public void onUsingTick(ItemStack itemStack, LivingEntity entity, int useRemaining) {
    	super.onUsingTick(itemStack, entity, useRemaining);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
    	super.releaseUsing(stack, worldIn, entityLiving, timeLeft);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return super.getUseAnimation(itemStack);
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemStack, ItemStack repairStack) {
        return super.isValidRepairItem(itemStack, repairStack);
    }

	/** Gets or creates an NBT Compound for the provided itemstack. **/
	public CompoundTag getTagCompound(ItemStack itemStack) {
		if(itemStack.hasTag()) {
			return itemStack.getTag();
		}
		return new CompoundTag();
	}

    public void playSound(Level world, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch) {
        world.playSound(null, x, y, z, sound, category, volume, pitch);
    }

    public void playSound(Level world, BlockPos pos, SoundEvent sound, SoundSource category, float volume, float pitch) {
        world.playSound(null, pos, sound, category, volume, pitch);
    }
}
