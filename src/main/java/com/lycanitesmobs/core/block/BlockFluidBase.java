package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;


import java.util.List;

public class BlockFluidBase extends BlockFluidClassic {
    public String blockName;
    public ModInfo group;

    // ==================================================
    //                   Constructor
    // ==================================================
    public BlockFluidBase(Fluid fluid, Material material, ModInfo group, String blockName) {
        super(fluid, material);
        this.blockName = blockName;
        this.group = group;
        this.setRegistryName(this.group.modid, this.blockName);
        this.setUnlocalizedName(this.blockName);

        this.setRenderLayer(BlockRenderLayer.TRANSLUCENT);
    }


    // ==================================================
    //                      Info
    // ==================================================
    @Override
    public String getLocalizedName() {
        return LanguageManager.translate(this.getUnlocalizedName() + ".name");
    }

    @Override
    public void addInformation(ItemStack stack,  World world, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(this.getDescription(stack, world));
    }

    public String getDescription(ItemStack itemStack,  World world) {
        return "\u00a7a" + LanguageManager.translate(this.getUnlocalizedName() + ".description");
    }


    // ==================================================
    //                Collision Effects
    // ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);
    }

    @Override
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState state, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
        if(world instanceof World)
            this.onEntityCollidedWithBlock((World)world, blockpos, state, entity);
        return super.isEntityInsideMaterial(world, blockpos, state, entity, yToTest, materialIn, testingHead);
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public EnumBlockRenderType getRenderType(IBlockState blockState) {
        return EnumBlockRenderType.MODEL;
    }
}
