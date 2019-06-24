package com.lycanitesmobs.core.item;

import com.google.common.collect.Multimap;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBase extends Item {
	public static int descriptionWidth = 200;
	
	public String itemName = "unamed_item";
	public ModInfo modInfo = LycanitesMobs.modInfo;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBase(Properties properties) {
    	super(properties);
    }

    public void setup() {
        this.setRegistryName(this.modInfo.modid, this.itemName);
    }

    @Override
	@Nonnull
	public String getTranslationKey() {
    	return this.itemName;
	}
    
    
	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		return new TranslationTextComponent(LanguageManager.translate(this.getTranslationKey(stack) + ".name").trim());
	}

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
    	String description = this.getDescription(stack, worldIn, tooltip, flag);
    	if(!"".equalsIgnoreCase(description)) {
    		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    		List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, descriptionWidth);
    		for(Object formattedDescription : formattedDescriptionList) {
    			if(formattedDescription instanceof String)
                    tooltip.add(new TranslationTextComponent((String)formattedDescription));
    		}
    	}
    	super.addInformation(stack, worldIn, tooltip, flag);
    }

    public String getDescription(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
    	return LanguageManager.translate(this.getTranslationKey() + ".description");
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }
	
    
	// ==================================================
	//                      Update
	// ==================================================
	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		return super.onEntityItemUpdate(stack, entity);
	}
    
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Use ==========
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
    	return super.onItemUse(context);
    }
    
    // ========== Start ==========
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        return new ActionResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
    }

	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		return super.onLeftClickEntity(stack, player, entity);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
    	return super.itemInteractionForEntity(stack, player, entity, hand);
	}

    // ========== Using ==========
    @Override
    public void onUsingTick(ItemStack itemStack, LivingEntity entity, int useRemaining) {
    	super.onUsingTick(itemStack, entity, useRemaining);
    }
    
    // ========== Stop ==========
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    	super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }

    // ========== Animation ==========
    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return super.getUseAction(itemStack);
    }

	
	// ==================================================
	//                     Enchanting
	// ==================================================
    @Override
    public int getItemEnchantability() {
        return 0;
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        return super.getIsRepairable(itemStack, repairStack);
    }


    // ==================================================
    //                      Sound
    // ==================================================
    public void playSound(World world, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        world.playSound(null, x, y, z, sound, category, volume, pitch);
    }

    public void playSound(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        world.playSound(null, pos, sound, category, volume, pitch);
    }
}
