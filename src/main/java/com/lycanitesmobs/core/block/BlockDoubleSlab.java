package com.lycanitesmobs.core.block;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

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
    @Override
    public Item getItemDropped(BlockState state, Random random, int zero) {
        Block slabBlock = ObjectManager.getBlock(this.slabName);
        if(slabBlock != null)
            return Item.getItemFromBlock(slabBlock);
        return super.getItemDropped(state, random, zero);
    }

    @Override
    public int damageDropped(BlockState state) {
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return ObjectManager.getBlock(this.slabName) != null ? 2 : 1;
    }
}
