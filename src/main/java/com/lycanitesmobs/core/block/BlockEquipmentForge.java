package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.container.EquipmentForgeContainerProvider;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

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
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
		return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemStack) {
		super.setPlacedBy(world, pos, state, entity, itemStack);
	}

	@Override
	public boolean hasTileEntity(BlockState blockState) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState blockState, IBlockReader world) {
		TileEntityEquipmentForge tileEntityEquipmentForge = new TileEntityEquipmentForge();
		tileEntityEquipmentForge.setLevel(this.level);
		return tileEntityEquipmentForge;
	}

	@Override //onBlockActivated()
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(!world.isClientSide() && player instanceof ServerPlayerEntity) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if(tileEntity instanceof TileEntityEquipmentForge) {
				NetworkHooks.openGui((ServerPlayerEntity) player, new EquipmentForgeContainerProvider((TileEntityEquipmentForge)tileEntity), buf -> buf.writeBlockPos(pos));
			}
		}
		return ActionResultType.SUCCESS;
	}

    @Override
    public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        return tileEntity != null && tileEntity.triggerEvent(eventID, eventParam);
    }
}
