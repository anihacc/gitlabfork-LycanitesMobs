package com.lycanitesmobs.core.block;


import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class BlockFireBase extends BlockBase {
    public static final BooleanProperty PERMANENT = BooleanProperty.create("permanent");
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final BooleanProperty UP = SixWayBlock.UP;
    private static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199776_0_) -> p_199776_0_.getKey() != Direction.DOWN).collect(Util.toMapCollector());

    public boolean dieInRain = true;
    public boolean triggerTNT = true;
    public boolean tickRandomly = true;
    public int agingRate = 3;
    public float spreadChance = 1;
    public boolean removeOnNoFireTick;


    // ==================================================
    //                   Constructor
    // ==================================================
    public BlockFireBase(Block.Properties properties, ModInfo group, String name) {
        super(properties, group, name);

        this.removeOnTick = false;
        this.removeOnNoFireTick = false;
        this.loopTicks = true;
        this.canBeCrushed = true;

        this.noBreakCollision = false;
        this.isOpaque = false;

        this.tickRate = 30; // Default tick rate, configs can set this to 1 to remove this fire block from worlds.

        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(AGE, 0)
                .with(PERMANENT, false)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE, PERMANENT, NORTH, EAST, SOUTH, WEST, UP);
    }


    // ==================================================
    //                   Block States
    // ==================================================
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getStateForPlacement(context.getWorld(), context.getPos());
    }

    public BlockState getStateForPlacement(IBlockReader world, BlockPos blockPos) {
        BlockPos lowerBlockPos = blockPos.down();
        BlockState lowerBlockState = world.getBlockState(lowerBlockPos);
        if (!this.canCatchFire(world, blockPos, Direction.UP) && !Block.hasSolidSide(lowerBlockState, world, lowerBlockPos, Direction.UP)) {
            BlockState placementState = this.getDefaultState();

            for(Direction direction : Direction.values()) {
                BooleanProperty booleanproperty = FACING_TO_PROPERTY_MAP.get(direction);
                if (booleanproperty != null) {
                    placementState = placementState.with(booleanproperty, this.canCatchFire(world, blockPos.offset(direction), direction.getOpposite()));
                }
            }

            return placementState.with(PERMANENT, false);
        }

        return this.getDefaultState();
    }


    // ==================================================
    //                  Block Placement
    // ==================================================
    /** Returns true if this block can place another block at the specified location. **/
    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.down();
        return worldIn.getBlockState(blockpos).func_224755_d(worldIn, blockpos, Direction.UP) || this.areNeighborsFlammable(worldIn, pos);
    }

    protected boolean areNeighborsFlammable(IBlockReader worldIn, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            if (this.canCatchFire(worldIn, pos.offset(direction), direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return this.isValidPosition(state, worldIn, currentPos) ? this.getStateForPlacement(worldIn, currentPos).with(AGE, state.get(AGE)) : Blocks.AIR.getDefaultState();
    }



    // ==================================================
    //                     Ticking
    // ==================================================
    // ========== Tick Rate ==========
    @Override
    public int tickRate(IWorldReader world) {
        return this.tickRate;
    }

    // ========== Tick Update ==========
    @Override
    public void func_225534_a_(BlockState blockState, ServerWorld world, BlockPos pos, Random rand) { //tick()
        if (!world.isAreaLoaded(pos, 2)) {
            return;
        }

        boolean permanent = blockState.get(PERMANENT);

        if (!world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            if(this.removeOnNoFireTick && !permanent)
                world.removeBlock(pos, false);
            return;
        }

        // Prevent Self Replacement:
        if (!this.isValidPosition(blockState, world, pos) || this.removeOnTick) {
            world.removeBlock(pos, false);
        }

        BlockState blockStateBelow = world.getBlockState(pos.down());
        boolean isOnFireSource = permanent || this.isBlockFireSource(blockStateBelow, world, pos.down(), Direction.UP);
        int age = blockState.get(AGE);

        // Environmental Extinguish:
        if (!isOnFireSource && this.canDie(world, pos) && rand.nextFloat() < 0.2F + (float)age * 0.03F) {
            world.removeBlock(pos, false);
            return;
        }

        // Increase Age:
        if (age < 15) {
            blockState = blockState.with(AGE, Math.max(age + Math.round((float)rand.nextInt(this.agingRate) / 2), 15));
            world.setBlockState(pos, blockState, 4);
        }

        // Schedule Next Update:
        if(this.loopTicks)
            world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world) + rand.nextInt(10));

        // Natural Extinguish:
        if (!isOnFireSource) {
            // On Air:
            if(world.getBlockState(pos.down()).getBlock() == Blocks.AIR) {
				world.removeBlock(pos, false);
				return;
			}

            // Can't spread, old or on none solid surface:
            if (!this.canNeighborCatchFire(world, pos)) {
                if (!Block.hasSolidSide(world.getBlockState(pos), world, pos.down(), Direction.UP) || age > 3) {
                    world.removeBlock(pos, false);
                }
                return;
            }

            // End of life and can't spread below:
            if (!this.canCatchFire(world, pos.down(), Direction.UP) && age == 15 && rand.nextInt(4) == 0) {
                world.removeBlock(pos, false);
                return;
            }
        }

        // Spread Fire:
        if(this.spreadChance <= 0 || permanent)
            return;
        boolean highHumidity = world.isBlockinHighHumidity(pos);
        int humidityChance = 0;
        if (highHumidity)
            humidityChance = -50;
        this.tryCatchFire(world, pos.east(), 300 + humidityChance, rand, age, Direction.WEST);
        this.tryCatchFire(world, pos.west(), 300 + humidityChance, rand, age, Direction.EAST);
        this.tryCatchFire(world, pos.down(), 250 + humidityChance, rand, age, Direction.UP);
        this.tryCatchFire(world, pos.up(), 250 + humidityChance, rand, age, Direction.DOWN);
        this.tryCatchFire(world, pos.north(), 300 + humidityChance, rand, age, Direction.SOUTH);
        this.tryCatchFire(world, pos.south(), 300 + humidityChance, rand, age, Direction.NORTH);

        // Mass Spreading:
        for (int offsetX = -1; offsetX <= 1; ++offsetX) {
            for (int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
                for (int offsetY = -1; offsetY <= 4; ++offsetY) {
                    if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
                        int chance = 100;
                        // Heat Rises:
                        if (offsetY > 1)
                            chance += (offsetY - 1) * 100;

                        BlockPos spreadPos = pos.add(offsetX, offsetY, offsetZ);
                        int spreadEncouragement = this.getNeighborEncouragement(world, spreadPos);
                        if (spreadEncouragement > 0) {
                            int spreadFlammability = (spreadEncouragement + 40 + world.getDifficulty().getId() * 7) / (age + 30);
                            if (highHumidity)
                                spreadFlammability /= 2;

                            if (spreadFlammability > 0 && rand.nextInt(chance) <= spreadFlammability && (!world.isRaining() || !this.canDie(world, spreadPos))) {
                                int spreadAge = age + rand.nextInt(5) / 4;
                                if (spreadAge > 15)
                                    spreadAge = 15;
                                world.setBlockState(spreadPos, blockState.with(AGE, spreadAge).with(PERMANENT, false), 3);
                            }
                        }
                    }
                }
            }
        }
    }


    // ==================================================
    //                       Fire
    // ==================================================

    /** Returns true if any adjacent blocks can catch fire. **/
    protected boolean canNeighborCatchFire(World worldIn, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (this.canCatchFire(worldIn, pos.offset(direction), direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    /** Gets the flammability of nearby blocks, highly flammable blocks that are near each other will help spread fire faster. **/
    protected int getNeighborEncouragement(World worldIn, BlockPos pos) {
        if (!worldIn.isAirBlock(pos))
            return 0;
        else {
            int i = 0;
            for (Direction direction : Direction.values()) {
                i = Math.max(worldIn.getBlockState(pos.offset(direction)).getFlammability(worldIn, pos.offset(direction), direction.getOpposite()), i);
            }
            return i;
        }
    }

    /** Attempts to ignite the position. **/
    private void tryCatchFire(World world, BlockPos pos, int chance, Random random, int age, Direction face) {
        int flammability = this.getBlockFlammability(world, pos, face);
        if (Math.round(random.nextInt(chance) / this.spreadChance) < flammability) {
            BlockState blockState = world.getBlockState(pos);

            if (random.nextInt(age + 10) < 5 && !world.isRainingAt(pos)) {
                int newFireAge = age + random.nextInt(5) / 4;
                if (newFireAge > 15)
                    newFireAge = 15;
                this.burnBlockReplace(world, pos, newFireAge);
            }
            else {
                this.burnBlockDestroy(world, pos);
            }

            if (this.triggerTNT && blockState.getBlock() instanceof TNTBlock) {
                TNTBlock.explode(world, pos);
            }
        }
    }

    /** Burns away a block, typically replacing it with this fire block, but can change it to other things depending on the type of fire block. **/
    public void burnBlockReplace(World world, BlockPos pos, int newFireAge) {
        world.setBlockState(pos, this.getDefaultState().with(AGE, newFireAge), 3);
    }

    /** Burns away a block, typically setting it to air but can change it to other things depending on the type of fire block. **/
    public void burnBlockDestroy(World world, BlockPos pos) {
        world.removeBlock(pos, false);
    }

    /** Returns true if the block at the provided position and face can catch fire. **/
    public boolean canCatchFire(IBlockReader world, BlockPos pos, Direction face) {
        return world.getBlockState(pos).isFlammable(world, pos, face);
    }

    /** Checks if the provided block is a fire source, can be overridden for custom sources. **/
    public boolean isBlockFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return state.isFireSource(world, pos, side);
    }

    /** Returns how flammable the target block is. **/
    public int getBlockFlammability(IBlockReader world, BlockPos pos, Direction face) {
        return world.getBlockState(pos).getFlammability(world, pos, face);
    }

    /** Returns true if this fire block should be extinguished, can check for rain and position, etc. **/
    protected boolean canDie(World world, BlockPos pos) {
        return world.isRainingAt(pos) || world.isRainingAt(pos.west()) || world.isRainingAt(pos.east()) || world.isRainingAt(pos.north()) || world.isRainingAt(pos.south());
    }


    // ==================================================
    //                      Rendering
    // ==================================================
    /*@Override Redundant?
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }*/
}
