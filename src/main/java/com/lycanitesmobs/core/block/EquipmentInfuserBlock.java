package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.container.EquipmentInfuserContainerProvider;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.tileentity.EquipmentInfuserTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.network.NetworkHooks;

public class EquipmentInfuserBlock extends BlockBase {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public EquipmentInfuserBlock(Properties properties, ModInfo group) {
		super(properties);
		
		// Properties:
		this.group = group;
		this.blockName = "equipment_infuser";
		this.setRegistryName(this.group.modid, this.blockName.toLowerCase());
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
		return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemStack) {
		super.setPlacedBy(world, pos, state, entity, itemStack);
	}

	@Override
	public boolean hasTileEntity(BlockState blockState) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState blockState, BlockGetter world) {
		EquipmentInfuserTileEntity equipmentInfuserTileEntity = new EquipmentInfuserTileEntity();
		return equipmentInfuserTileEntity;
	}

	@Override //onBlockActivated()
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if(!world.isClientSide() && player instanceof ServerPlayer) {
			BlockEntity tileEntity = world.getBlockEntity(pos);
			if(tileEntity instanceof EquipmentInfuserTileEntity) {
				NetworkHooks.openGui((ServerPlayer) player, new EquipmentInfuserContainerProvider((EquipmentInfuserTileEntity)tileEntity), buf -> buf.writeBlockPos(pos));
			}
		}
		return InteractionResult.SUCCESS;
	}

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int eventID, int eventParam) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        return tileEntity != null && tileEntity.triggerEvent(eventID, eventParam);
    }
}
