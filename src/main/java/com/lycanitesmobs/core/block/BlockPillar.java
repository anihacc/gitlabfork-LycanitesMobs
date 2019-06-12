package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;

public class BlockPillar extends BlockBase {
    public static EnumProperty<Direction.Axis> AXIS;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockPillar(Block.Properties properties, ModInfo group, String name) {
		super(properties, group, name);
        this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Y));
	}


    // ==================================================
    //                   Block States
    // ==================================================
    /*@Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {AXIS});
    }

    @Override
    protected ItemStack getSilkTouchDrop(BlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Y;
        int i = meta & 12;

        if (i == 4)
            enumfacing$axis = EnumFacing.Axis.X;
        else if (i == 8)
            enumfacing$axis = EnumFacing.Axis.Z;

        return this.getDefaultState().withProperty(AXIS, enumfacing$axis);
    }

    @Override
    public int getMetaFromState(BlockState state) {
        int i = 0;
        EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis)state.getValue(AXIS);

        if (enumfacing$axis == EnumFacing.Axis.X)
            i |= 4;
        else if (enumfacing$axis == EnumFacing.Axis.Z)
            i |= 8;

        return i;
    }*/


    // ==================================================
    //                    Placement
    // ==================================================
    /*@Override
    public BlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS, facing.getAxis());
    }*/


    // ==================================================
    //                     Rotation
    // ==================================================
    /*@Override
    public boolean rotateBlock(net.minecraft.world.World world, BlockPos pos, EnumFacing axis) {
        net.minecraft.block.state.BlockState state = world.getBlockState(pos);
        for (net.minecraft.block.properties.IProperty<?> prop : state.getProperties().keySet()) {
            if (prop.getName().equals("axis")) {
                world.setBlockState(pos, state.cycleProperty(prop));
                return true;
            }
        }
        return false;
    }*/

    /** Returns a blockstate for the provided rotation. **/
    /*@Override
    public BlockState withRotation(BlockState state, Rotation rot) {
        switch (rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:

                switch ((EnumFacing.Axis)state.getValue(AXIS)) {
                    case X:
                        return state.withProperty(AXIS, EnumFacing.Axis.Z);
                    case Z:
                        return state.withProperty(AXIS, EnumFacing.Axis.X);
                    default:
                        return state;
                }

            default:
                return state;
        }
    }*/
}
