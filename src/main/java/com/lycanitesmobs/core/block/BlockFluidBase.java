package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

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

        //this.setRenderLayer(BlockRenderLayer.TRANSLUCENT);
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
        return new TranslationTextComponent("block." + this.blockName + ".description");
    }


    // ==================================================
    //                Collision Effects
    // ==================================================
    /*@Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, BlockState state, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
        if(world instanceof World)
            this.onEntityCollidedWithBlock((World)world, blockpos, state, entity);
        return super.isEntityInsideMaterial(world, blockpos, state, entity, yToTest, materialIn, testingHead);
    }*/


    // ==================================================
    //                      Visuals
    // ==================================================
    /*@Override
    public EnumBlockRenderType getRenderType(BlockState blockState) {
        return EnumBlockRenderType.MODEL;
    }*/
}
