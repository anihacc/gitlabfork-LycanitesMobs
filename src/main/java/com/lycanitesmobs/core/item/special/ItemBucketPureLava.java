package com.lycanitesmobs.core.item.special;

import com.google.common.collect.Multimap;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBucketPureLava extends BucketItem {
	public String itemName;
	public ModInfo group;

    public ItemBucketPureLava(Item.Properties properties, Fluid fluid) {
        super(fluid, properties);
        this.group = LycanitesMobs.modInfo;
        this.itemName = "bucketpurelava";
        this.setRegistryName(this.group.modid, this.itemName);
        ObjectManager.addBucket(this, ObjectManager.getBlock("purelava"), fluid);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent(this.getTranslationKey(stack) + ".name");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        ITextComponent description = this.getDescription(stack, worldIn, tooltip, flag);
        if(!"".equalsIgnoreCase(description.getFormattedText())) {
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            List<String> formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description.getFormattedText(), BaseItem.DESCRIPTION_WIDTH);
            for(String formattedDescription : formattedDescriptionList) {
                tooltip.add(new StringTextComponent(formattedDescription));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flag);
    }

    public ITextComponent getDescription(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flag) {
        return new TranslationTextComponent(this.getTranslationKey() + ".description");
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return super.getAttributeModifiers(slot, stack);
    }
}
