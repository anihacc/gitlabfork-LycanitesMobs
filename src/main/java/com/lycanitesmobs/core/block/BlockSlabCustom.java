package com.lycanitesmobs.core.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockSlabCustom extends SlabBlock {
    public String blockName = "BlockBase";

	public BlockSlabCustom(Block.Properties properties, BlockBase block) {
		super(properties);
        String slabName = "_slab";
        this.setRegistryName(new ResourceLocation(block.group.modid, block.blockName + slabName));
	}

    @Override
    public boolean useShapeForLightOcclusion(BlockState blockState) {
        return true;
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

    @Nullable
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Item.byBlock(this);
    }

    public ItemStack getItem(Level worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }
}
