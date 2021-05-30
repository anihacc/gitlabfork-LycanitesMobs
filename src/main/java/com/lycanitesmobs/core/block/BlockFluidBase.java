package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockFluidBase extends FlowingFluidBlock {
    public String blockName;
    public ModInfo group;

    public BlockFluidBase(FlowingFluid fluid, Block.Properties properties, ModInfo group, String blockName) {
        super(fluid, properties);
        this.blockName = blockName;
        this.group = group;
        this.setRegistryName(this.group.modid, this.blockName);
    }

    @Override
    public IFormattableTextComponent getName() {
        return new TranslationTextComponent(this.getDescriptionId());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(this.getDescription(stack, world));
    }

    public ITextComponent getDescription(ItemStack itemStack, @Nullable IBlockReader world) {
        return new TranslationTextComponent("block." + this.blockName + ".description").withStyle(style -> style.withColor(Color.fromLegacyFormat(TextFormatting.GREEN)));
    }
}
