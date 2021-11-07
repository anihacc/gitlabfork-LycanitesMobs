package com.lycanitesmobs.core.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFenceCustom extends FenceBlock {
	public String blockName = "BlockBase";

	public BlockFenceCustom(Block.Properties properties, BlockBase block) {
		super(properties);
        this.setRegistryName(new ResourceLocation(block.group.modid, block.blockName + "_fence"));
	}

	@Override
	public MutableComponent getName() {
		return new TranslatableComponent(this.getDescriptionId());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(this.getDescription(stack, world));
	}

	public Component getDescription(ItemStack itemStack, @Nullable BlockGetter world) {
		return new TranslatableComponent(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GREEN);
	}
}
