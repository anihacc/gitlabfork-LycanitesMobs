package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockEquipmentForge extends BlockBase {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public int level;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockEquipmentForge(Block.Properties properties, ModInfo group, int level) {
		super(properties);
		
		// Properties:
		this.group = group;
		this.level = level;
		if(level <= 1) {
			this.blockName = "equipmentforge_lesser";
		}
		else if(level == 2) {
			this.blockName = "equipmentforge_greater";
		}
		else {
			this.blockName = "equipmentforge_master";
		}

		this.setRegistryName(this.group.modid, this.blockName.toLowerCase());
		this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof TileEntityEquipmentForge) {
				InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityEquipmentForge)tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	@Override
	public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
		return state.with(FACING, direction.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public TileEntity createTileEntity(BlockState blockState, IBlockReader world) {
		TileEntityEquipmentForge tileEntityEquipmentForge = new TileEntityEquipmentForge();
		tileEntityEquipmentForge.setLevel(this.level);
		return tileEntityEquipmentForge;
	}

	/*
    @Override
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        return tileEntity != null && tileEntity.receiveClientEvent(eventID, eventParam);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote) {
            if(playerIn != null && playerIn.getEntityWorld() != null) {
                playerIn.openGui(LycanitesMobs.instance, GuiHandler.GuiType.TILEENTITY.id, playerIn.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }*/
}
