package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockDoubleSlab extends BlockPillar {
    protected String slabName;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockDoubleSlab(Material material, ModInfo group, String name, String slabName) {
		super(material, group, name);
        this.slabName = slabName;
	}


    // ==================================================
    //                      Info
    // ==================================================
    @Override
    public String getLocalizedName() {
        return LanguageManager.translate(this.getTranslationKey() + ".name");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(this.getDescription(stack, world));
    }

    public String getDescription(ItemStack itemStack, @Nullable World world) {
        return LanguageManager.translate(this.getTranslationKey() + ".description");
    }


    // ==================================================
    //                      Break
    // ==================================================
    //========== Drops ==========
    // TODO Slab Drops
}
