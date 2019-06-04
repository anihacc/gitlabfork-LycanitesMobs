package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;

import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBucketOoze extends ItemBucket {
	public String itemName;
	public ModInfo group;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemBucketOoze(Fluid fluid) {
        super(ObjectManager.getBlock("ooze"));
        this.group = LycanitesMobs.modInfo;
        this.itemName = "bucketooze";
        this.setRegistryName(this.group.filename, this.itemName);
        this.setUnlocalizedName(this.itemName);
        this.setCreativeTab(LycanitesMobs.itemsTab);
        ObjectManager.addBucket(this, ObjectManager.getBlock("ooze"), fluid);
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
	//                     Visuals
	// ==================================================
    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
