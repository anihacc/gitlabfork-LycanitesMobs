package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockEquipmentForge extends BlockBase {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public int level;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockEquipmentForge(Block.Properties properties, ModInfo group, int level) {
		super(properties);
		// TODO Get material from level.
        //this.setCreativeTab(LycanitesMobs.blocksTab);
        //this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		
		// Properties:
		this.group = group;
		this.level = level;
		if(level <= 1) {
			this.blockName = "equipmentforge_lesser";
			//this.setHarvestLevel("axe", 1);
			//this.setSoundType(SoundType.WOOD);
			//this.setResistance(10F);
		}
		else if(level == 2) {
			this.blockName = "equipmentforge_greater";
			//this.setHarvestLevel("pickaxe", 1);
			//this.setSoundType(SoundType.STONE);
			//this.setResistance(20F);
		}
		else {
			this.blockName = "equipmentforge_master";
			//this.setHarvestLevel("pickaxe", 2);
			//this.setSoundType(SoundType.METAL);
			//this.setResistance(1000F);
		}
		this.setup();
		
		// Stats:
		//this.setHardness(5F);

        // Tile Entity:
        //this.hasTileEntity = true;
	}

    /*@Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }*/


    // ==================================================
    //                     Block Events
    // ==================================================
    /*@Override
    public void onBlockAdded(World world, BlockPos pos, BlockState state) {
		this.setDefaultFacing(world, pos, state);
        super.onBlockAdded(world, pos, state);
    }

	protected void setDefaultFacing(World world, BlockPos pos, BlockState state) {
		if (!world.isRemote) {
			BlockState iblockstate = world.getBlockState(pos.north());
			BlockState iblockstate1 = world.getBlockState(pos.south());
			BlockState iblockstate2 = world.getBlockState(pos.west());
			BlockState iblockstate3 = world.getBlockState(pos.east());
			EnumFacing enumfacing = state.getValue(FACING);

			if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
				enumfacing = EnumFacing.SOUTH;
			}
			else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
				enumfacing = EnumFacing.NORTH;
			}
			else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
				enumfacing = EnumFacing.EAST;
			}
			else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
				enumfacing = EnumFacing.WEST;
			}

			world.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, BlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity != null && tileEntity instanceof TileEntityBase)
            ((TileEntityBase)tileEntity).onRemove();
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

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


    // ==================================================
    //                    Tile Entity
    // ==================================================
    @Override
    public TileEntity createTileEntity(BlockState blockState, IBlockReader world) {
		TileEntityEquipmentForge tileEntityEquipmentForge = new TileEntityEquipmentForge();
		tileEntityEquipmentForge.setLevel(this.level);
        return tileEntityEquipmentForge;
    }


    // ==================================================
    //                    Block State
    // ==================================================
	/*@Override
	public BlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

    @Override
	public int getMetaFromState(BlockState state) {
		return (state.getValue(FACING)).getIndex();
	}

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}*/
}
