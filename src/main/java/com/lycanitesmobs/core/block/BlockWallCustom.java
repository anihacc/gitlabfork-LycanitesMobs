package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockWallCustom extends WallBlock {
	public String blockName = "BlockBase";

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockWallCustom(Block.Properties properties, BlockBase block) {
		super(properties);
        this.setRegistryName(new ResourceLocation(block.group.filename, block.blockName + "_wall"));
	}

    /*@SideOnly(Side.CLIENT)
	@Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1));
    }*/


	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public ITextComponent getNameTextComponent() {
		return new TranslationTextComponent(LanguageManager.translate(this.getTranslationKey()));
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent(this.getDescription(stack, world)));
	}

	public String getDescription(ItemStack itemStack, @Nullable IBlockReader world) {
		return LanguageManager.translate("block." + this.blockName + ".description");
	}
}
