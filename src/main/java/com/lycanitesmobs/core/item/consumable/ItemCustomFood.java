package com.lycanitesmobs.core.item.consumable;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.ItemConfig;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCustomFood extends ItemFood {

    /** The various classes of foods, used mainly for generic configurable effect durations. **/
    public static enum FOOD_CLASS {
        NONE(0), RAW(1), COOKED(2), MEAL(3), FEAST(4);
        public final int id;
        private FOOD_CLASS(int value) { this.id = value; }
        public int getValue() { return id; }
    }
	
	public String itemName = "customfood";
	public ModInfo group = LycanitesMobs.modInfo;
	public String texturePath = "customfood";
    public FOOD_CLASS foodClass = FOOD_CLASS.NONE;

    /** The ID of the potion effect that will occur upon eating this food. Set using setPotionEffect(). */
    protected PotionEffect effect;
    /** The ID of the chance effect that will occur upon eating this food. Set using setPotionEffect(). */
    protected float effectChance;

    // ==================================================
  	//                    Constructors
  	// ==================================================
	public ItemCustomFood(String setItemName, ModInfo group, String setTexturePath, int feed, float saturation, FOOD_CLASS foodClass) {
		super(feed, saturation, false);
		this.itemName = setItemName;
		this.group = group;
		this.texturePath = setTexturePath;
        this.foodClass = foodClass;
		this.setMaxStackSize(64);
		this.setRegistryName(this.group.filename, this.itemName);
		this.setCreativeTab(LycanitesMobs.itemsTab);
		this.setUnlocalizedName(itemName);
		this.setPotionEffect(MobEffects.INSTANT_HEALTH, 1, this.getInstantHealing(), 1.0F);
	}
	public ItemCustomFood(String setItemName, ModInfo group, int feed, float saturation, FOOD_CLASS foodClass) {
		this(setItemName, group, setItemName.toLowerCase(), feed, saturation, foodClass);
	}
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String description = this.getDescription(stack, worldIn, tooltip, flagIn);
        if(!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, ItemBase.descriptionWidth);
            for(Object formattedDescription : formattedDescriptionList) {
                if(formattedDescription instanceof String)
                    tooltip.add("\u00a7a" + formattedDescription);
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public String getDescription(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        return LanguageManager.translate("item." + this.itemName + ".description");
    }


    // ==================================================
    //                     Effects
    // ==================================================
    public int getEffectDuration() {
        if(this.foodClass == FOOD_CLASS.RAW)
            return ItemConfig.durationRaw * 20;
        else if(this.foodClass == FOOD_CLASS.COOKED)
            return ItemConfig.durationCooked * 20;
        else if(this.foodClass == FOOD_CLASS.MEAL)
            return ItemConfig.durationMeal * 20;
        else if(this.foodClass == FOOD_CLASS.FEAST)
            return ItemConfig.durationFeast * 20;
        return 1;
    }

	public int getInstantHealing() {
		if(this.foodClass == FOOD_CLASS.COOKED)
			return ItemConfig.healingCooked;
		else if(this.foodClass == FOOD_CLASS.MEAL)
			return ItemConfig.healingMeal;
		else if(this.foodClass == FOOD_CLASS.FEAST)
			return ItemConfig.healingFeast;
		return 0;
	}

    public ItemCustomFood setPotionEffect(Potion potion, int duration, int amplifier, float chance) {
        PotionEffect potionEffect = new PotionEffect(potion, duration * 20, amplifier, false, false);
        if(potion == MobEffects.INSTANT_HEALTH) {
			potionEffect = new PotionEffect(potion, 1, amplifier);
		}
        this.effect = potionEffect;
        this.setPotionEffect(potionEffect, chance);
        return this;
    }

    public ItemCustomFood setAlwaysEdible() {
        super.setAlwaysEdible();
        return this;
    }
}
