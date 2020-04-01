package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class GenericBucketItem extends ItemBucket {
	public String itemName;
	public ModInfo group;

	// ==================================================
	//                   Constructor
	// ==================================================
    public GenericBucketItem(Fluid fluid, Block fluidBlock, String fluidName) {
        super(fluidBlock);
        this.group = LycanitesMobs.modInfo;
        this.itemName = "bucket" + fluidName;
        this.setRegistryName(this.group.modid, this.itemName);
        this.setUnlocalizedName(this.itemName);
        this.setCreativeTab(LycanitesMobs.itemsTab);
        ObjectManager.addBucket(this, fluidBlock, fluid);
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return LanguageManager.translate(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        String description = this.getDescription(stack, worldIn, tooltip, flagIn);
        if(!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
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
        return LanguageManager.translate("item." + this.itemName + ".description");
    }
    
	
	// ==================================================
	//                     Visuals
	// ==================================================
    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
