package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.container.EquipmentForgeContainerProvider;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
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
import net.minecraft.world.server.ServerWorld;
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
		super.onReplaced(state, worldIn, pos, newState, isMoving);
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
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack itemStack) {
		super.onBlockPlacedBy(world, pos, state, entity, itemStack);
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
	public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if(!world.isRemote() && player instanceof ServerPlayerEntity) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if(tileEntity instanceof TileEntityEquipmentForge) {
				NetworkHooks.openGui((ServerPlayerEntity) player, new EquipmentForgeContainerProvider((TileEntityEquipmentForge)tileEntity), buf -> buf.writeBlockPos(pos));
			}
		}
		return super.func_225533_a_(state, world, pos, player, handIn, hit);
	}

    @Override
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        return tileEntity != null && tileEntity.receiveClientEvent(eventID, eventParam);
    }
}
