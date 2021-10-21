package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.container.SummoningPedestalContainerProvider;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockSummoningPedestal extends BlockBase {
    public enum EnumSummoningPedestal {
        NONE(0),
        CLIENT(1),
        PLAYER(2);

        private int ownerId;
        EnumSummoningPedestal(int ownerId) {
            this.ownerId = ownerId;
        }

        public int getOwnerId() {
            return this.ownerId;
        }
    }

    public static final IntegerProperty PROPERTY_OWNER = IntegerProperty.create("owner", 0, 2);


	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockSummoningPedestal(Block.Properties properties, ModInfo group) {
		super(properties);

        this.group = group;
        this.blockName = "summoningpedestal";
        this.setRegistryName(this.group.modid, this.blockName.toLowerCase());
        this.registerDefaultState(this.getStateDefinition().any().setValue(PROPERTY_OWNER, EnumSummoningPedestal.NONE.ownerId));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROPERTY_OWNER);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if(tileentity instanceof TileEntitySummoningPedestal) {
            TileEntitySummoningPedestal tileEntitySummoningPedestal = (TileEntitySummoningPedestal)tileentity;
            tileEntitySummoningPedestal.setOwner(placer);
            if(placer instanceof Player) {
                Player player = (Player)placer;
                ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
                if(playerExt != null) {
                    tileEntitySummoningPedestal.setSummonSet(playerExt.getSelectedSummonSet());
                }
            }
        }
    }

    @Override
    public boolean hasTileEntity(BlockState blockState) {
	    return true;
    }

    @Override
    public BlockEntity createTileEntity(BlockState blockState, BlockGetter world) {
        return new TileEntitySummoningPedestal();
    }

    public static void setState(EnumSummoningPedestal owner, Level worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        worldIn.setBlock(pos, blockState.getBlock().defaultBlockState().setValue(PROPERTY_OWNER, owner.getOwnerId()), 3);

        if (tileentity != null) {
            tileentity.clearRemoved();
            worldIn.setBlockEntity(pos, tileentity);
        }
    }

    @Override //onBlockActivated()
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(!world.isClientSide() && player instanceof ServerPlayer) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof TileEntitySummoningPedestal) {
                ((ServerPlayer)player).connection.send(tileEntity.getUpdatePacket());
                NetworkHooks.openGui((ServerPlayer)player, new SummoningPedestalContainerProvider((TileEntitySummoningPedestal)tileEntity), buf -> buf.writeBlockPos(pos));
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
