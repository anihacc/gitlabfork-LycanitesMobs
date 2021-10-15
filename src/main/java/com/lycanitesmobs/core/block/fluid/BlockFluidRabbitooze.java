package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.block.BlockFluidBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockFluidRabbitooze extends BlockFluidOoze {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFluidRabbitooze(Fluid fluid, String name) {
        super(fluid, name);
        this.destroyItems = false;
	}


    // ==================================================
    //                       Fluid
    // ==================================================
    @Override
    public boolean canDisplace(IBlockAccess world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);

        // Renewable Fluid:
        if (blockState.getBlock() == this) {
            if (blockState.getBlock().getMetaFromState(blockState) != 0) {
                byte otherSourceBlocks = 0;
                ArrayList<BlockPos> adjBlockPositions = new ArrayList<BlockPos>();
                adjBlockPositions.add(pos.add(-1, 0, 0));
                adjBlockPositions.add(pos.add(1, 0, 0));
                adjBlockPositions.add(pos.add(0, 1, 0));
                adjBlockPositions.add(pos.add(0, 0, -1));
                adjBlockPositions.add(pos.add(0, 0, 1));
                for (BlockPos adjBlockPos : adjBlockPositions) {
                    IBlockState adjBlockState = world.getBlockState(adjBlockPos);
                    Block adjBlock = adjBlockState.getBlock();
                    int adjMetadata = adjBlock.getMetaFromState(adjBlockState);
                    if (adjBlock == this && adjMetadata == 0)
                        otherSourceBlocks++;
                    if (otherSourceBlocks > 1)
                        break;
                }

                if (otherSourceBlocks > 1) {
                    if (world instanceof World) {
                        ((World) world).setBlockState(pos, this.getDefaultState());
                    }
                }
            }
            return false;
        }

        return super.canDisplace(world, pos);
    }
}
