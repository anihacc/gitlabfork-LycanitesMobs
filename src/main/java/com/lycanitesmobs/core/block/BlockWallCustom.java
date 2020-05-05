package com.lycanitesmobs.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import javax.annotation.Nullable;
import java.util.List;

public class BlockWallCustom extends WallBlock {
	public String blockName = "BlockBase";

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockWallCustom(Block.Properties properties, BlockBase block) {
		super(properties);
        this.setRegistryName(new Identifier(block.group.modid, block.blockName + "_wall"));
	}

	@Override
	public ITextComponent getNameTextComponent() {
		return new TranslationTextComponent(this.getTranslationKey());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		tooltip.add(this.getDescription(stack, world));
	}

	public ITextComponent getDescription(ItemStack itemStack, @Nullable IBlockReader world) {
		return new TranslationTextComponent(this.getTranslationKey() + ".description");
	}
}
