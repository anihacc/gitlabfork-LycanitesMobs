package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ItemProperties;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GenericFoodItem extends ItemFood {
	public String itemName;
	public ModInfo group = LycanitesMobs.modInfo;
	public String modelName;
	public ItemProperties properties;

	public GenericFoodItem(ItemProperties properties, String itemName) {
		super(properties.food.hunger, properties.food.saturation, properties.food.meat);
		this.itemName = itemName;
		this.properties = properties;
		this.setup();
	}

	public void setup() {
		this.setRegistryName(this.group.modid, this.itemName);
		this.setUnlocalizedName(this.itemName);
		this.setCreativeTab(this.properties.creativeTab);
		this.setMaxStackSize(this.properties.maxStackSize);
		if(properties.food.alwaysEdible)
			this.setAlwaysEdible();
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String description = this.getDescription(stack, worldIn, tooltip, flagIn);
		if(!"".equalsIgnoreCase(description)) {
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
			List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, ItemBase.DESCRIPTION_WIDTH);
			for(Object formattedDescription : formattedDescriptionList) {
				if(formattedDescription instanceof String)
					tooltip.add("\u00a7a" + formattedDescription);
			}
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	public String getDescription(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		return LanguageManager.translate(this.getUnlocalizedName() + ".description");
	}

	@Override
	protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player) {
		super.onFoodEaten(itemStack, world, player);
		for(PotionEffect effectInstance : this.properties.food.effects.keySet()) {
			float chance = this.properties.food.effects.get(effectInstance);
			if(chance == 1 || world.rand.nextFloat() <= chance) {
				player.addPotionEffect(new PotionEffect(effectInstance));
			}
		}
	}
}
