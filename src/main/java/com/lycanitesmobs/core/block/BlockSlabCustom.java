package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockSlabCustom extends SlabBlock {
    public String blockName = "BlockBase";

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockSlabCustom(Block.Properties properties, BlockBase block) {
		super(properties);
        String slabName = "_slab";
        this.setRegistryName(new ResourceLocation(block.group.modid, block.blockName + slabName));
	}

    @Override
    public boolean func_220074_n(BlockState blockState) {
        return false; // Double slabs are defined as BlockSlabDouble.
    }


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


    // ==================================================
    //                    Harvesting
    // ==================================================
    @Nullable
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }
}
