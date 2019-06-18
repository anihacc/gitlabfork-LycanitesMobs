package com.lycanitesmobs.core.entity.navigate;

import com.google.common.collect.Sets;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.fluid.IFluidState;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public class CreatureNodeProcessor extends NodeProcessor implements ICreatureNodeProcessor {

    public EntityCreatureBase entityCreature;
    protected float avoidsWater;

    public static double getGroundY(IBlockReader p_197682_0_, BlockPos pos) {
        BlockPos blockpos = pos.down();
        VoxelShape voxelshape = p_197682_0_.getBlockState(blockpos).getCollisionShape(p_197682_0_, blockpos);
        return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.getEnd(Direction.Axis.Y));
    }

    // ==================== Setup ====================
    @Override
    public void init(IWorldReader sourceIn, MobEntity mob) {
        super.init(sourceIn, mob);
        this.avoidsWater = mob.getPathPriority(PathNodeType.WATER);
        if(mob instanceof EntityCreatureBase)
            this.entityCreature = (EntityCreatureBase)mob;
    }

    @Override
    public void updateEntitySize(Entity updateEntity) {
        this.entitySizeX = MathHelper.floor(this.getWidth(updateEntity) + 1.0F);
        this.entitySizeY = MathHelper.floor(updateEntity.getSize(Pose.STANDING).height + 1.0F);
        this.entitySizeZ = MathHelper.floor(this.getWidth(updateEntity) + 1.0F);
    }

    @Override
    public void postProcess() {
        if(this.entityCreature != null)
            this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
        super.postProcess();
    }


    // ==================== Checks ====================
    /** Returns true if the entity is capable of pathing/moving in water at all. **/
    @Override
    public boolean getCanSwim() {
        if(this.entityCreature != null)
            return this.entityCreature.canWade() || this.entityCreature.isStrongSwimmer();
        return super.getCanSwim();
    }

    /** Returns true if the entity should use swimming focused pathing. **/
    public boolean swimming() {
        if(this.entityCreature == null) {
            return false;
        }
        if(this.entityCreature.isInWater()) {
            return this.entityCreature.isStrongSwimmer() || (this.entityCreature.canWade() && this.entityCreature.canDive());
        }
        return false;
    }

    /** Returns true if the entity should use flight focused pathing. **/
    public boolean flying() {
        return this.entityCreature != null && this.entityCreature.isFlying() && !this.entityCreature.isInWater();
    }

    /** Returns a width to path with. **/
    public double getWidth() {
		return this.getWidth(this.entity);
    }
	public double getWidth(Entity entity) {
		return Math.min(3, (double)entity.getSize(Pose.STANDING).width);
	}


    // ==================== Start ====================
    /** Returns a PathPoint to the given coordinates. **/
    @Override
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        // Flying/Strong Swimming:
        if(this.flying() || this.swimming()) {
            return this.openPoint(MathHelper.floor(x - this.getWidth()), MathHelper.floor(y + 0.5D), MathHelper.floor(z - this.getWidth()));
        }
        return this.openPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
    }

    /** Returns the starting position to create a new path from. **/
    @Override
    public PathPoint getStart() {
        // Flying/Strong Swimming:
        if(this.flying() || this.swimming()) {
            return this.openPoint(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getBoundingBox().minZ));
        }

        // Wading Through Water:
        int posY;
        if (this.getCanSwim() && this.entity.isInWater()) {
            posY = (int)this.entity.getBoundingBox().minY;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor(this.entity.posX), posY, MathHelper.floor(this.entity.posZ));

            for (Block block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock(); block == Blocks.WATER; block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock()) {
                ++posY;
                blockpos$mutableblockpos.setPos(MathHelper.floor(this.entity.posX), posY, MathHelper.floor(this.entity.posZ));
            }
        }

        // Walking On ground:
        else if (this.entity.onGround) {
            posY = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D);
        }

        // In Air:
        else {
            BlockPos blockpos;
            for (blockpos = new BlockPos(this.entity); (this.blockaccess.getBlockState(blockpos).getMaterial() == Material.AIR || !this.blockaccess.getBlockState(blockpos).isSolid()) && blockpos.getY() > 0; blockpos = blockpos.down()) {} // Was isPassable instead of isSolid
            posY = blockpos.up().getY();
        }

        // XZ Offset:
        BlockPos offsetXZ = new BlockPos(this.entity);
        PathNodeType targetNodeType = this.getPathNodeType(this.blockaccess, offsetXZ.getX(), posY, offsetXZ.getZ());

        if (this.entity.getPathPriority(targetNodeType) < 0.0F) {
            Set<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)posY, this.entity.getBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)posY, this.entity.getBoundingBox().maxZ));
            set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)posY, this.entity.getBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)posY, this.entity.getBoundingBox().maxZ));

            for (BlockPos betterPos : set) {
                PathNodeType pathnodetype = this.getPathNodeType(this.entity, betterPos);
                if(this.entity.getPathPriority(pathnodetype) >= 0.0F) {
                    return this.openPoint(betterPos.getX(), betterPos.getY(), betterPos.getZ());
                }
            }
        }

        return this.openPoint(offsetXZ.getX(), posY, offsetXZ.getZ());
    }


    // ==================== Options ====================
    /** Returns options from the current point to the target point. **/
    @Override
    public int func_222859_a(PathPoint[] pathOptions, PathPoint currentPoint) {
        // Flying/Strong Swimming/Diving:
        if(this.flying() || this.swimming()) {
            int i = 0;
            for (Direction direction : Direction.values()) {
                PathPoint pathPoint;
                if(this.flying())
                    pathPoint = this.getFlightNode(currentPoint.x + direction.getXOffset(), currentPoint.y + direction.getYOffset(), currentPoint.z + direction.getZOffset());
                else
                    pathPoint = this.getWaterNode(currentPoint.x + direction.getXOffset(), currentPoint.y + direction.getYOffset(), currentPoint.z + direction.getZOffset());
                if(pathPoint != null && !pathPoint.visited) {
                    pathOptions[i++] = pathPoint;
                }
            }
            return i;
        }

        // Walking:
        int i = 0;
        int j = 0;
        PathNodeType pathnodetype = this.getPathNodeType(this.entity, currentPoint.x, currentPoint.y + 1, currentPoint.z);
        if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
            j = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
        }

        double d0 = getGroundY(this.blockaccess, new BlockPos(currentPoint.x, currentPoint.y, currentPoint.z));
        PathPoint pathpoint = this.getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z + 1, j, d0, Direction.SOUTH);
        if (pathpoint != null && !pathpoint.visited && pathpoint.costMalus >= 0.0F) {
            pathOptions[i++] = pathpoint;
        }

        PathPoint pathpoint1 = this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z, j, d0, Direction.WEST);
        if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.costMalus >= 0.0F) {
            pathOptions[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z, j, d0, Direction.EAST);
        if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.costMalus >= 0.0F) {
            pathOptions[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = this.getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z - 1, j, d0, Direction.NORTH);
        if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.costMalus >= 0.0F) {
            pathOptions[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z - 1, j, d0, Direction.NORTH);
        if (this.func_222860_a(currentPoint, pathpoint1, pathpoint3, pathpoint4)) {
            pathOptions[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1, j, d0, Direction.NORTH);
        if (this.func_222860_a(currentPoint, pathpoint2, pathpoint3, pathpoint5)) {
            pathOptions[i++] = pathpoint5;
        }

        PathPoint pathpoint6 = this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1, j, d0, Direction.SOUTH);
        if (this.func_222860_a(currentPoint, pathpoint1, pathpoint, pathpoint6)) {
            pathOptions[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z + 1, j, d0, Direction.SOUTH);
        if (this.func_222860_a(currentPoint, pathpoint2, pathpoint, pathpoint7)) {
            pathOptions[i++] = pathpoint7;
        }

        return i;
    }

    // I think this determines the best path to go with?
    private boolean func_222860_a(PathPoint p_222860_1_, @Nullable PathPoint p_222860_2_, @Nullable PathPoint p_222860_3_, @Nullable PathPoint p_222860_4_) {
        return p_222860_4_ != null && !p_222860_4_.visited && p_222860_3_ != null && p_222860_3_.costMalus >= 0.0F && p_222860_3_.y <= p_222860_1_.y && p_222860_2_ != null && p_222860_2_.costMalus >= 0.0F && p_222860_2_.y <= p_222860_1_.y;
    }

    /** Returns a point that the entity can move to safely, only used when walking. **/
    @Nullable
    private PathPoint getSafePoint(int x, int y, int z, int stepHeight, double groundY, Direction facing) {
        PathPoint pathpoint = null;
        BlockPos blockpos = new BlockPos(x, y, z);
        double d0 = getGroundY(this.blockaccess, blockpos);

        if (d0 - groundY > 1.125D) {
            return null;
        }
        else {
            PathNodeType pathnodetype = this.getPathNodeType(this.entity, x, y, z);
            float f = this.entity.getPathPriority(pathnodetype);
            double entityRadius = this.getWidth() / 2;

            if (f >= 0.0F) {
                pathpoint = this.openPoint(x, y, z);
                pathpoint.nodeType = pathnodetype;
                pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            }

            if (pathnodetype == PathNodeType.WALKABLE) {
                return pathpoint;
            }
            else {
                if (pathpoint == null && stepHeight > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
                    pathpoint = this.getSafePoint(x, y + 1, z, stepHeight - 1, groundY, facing);

                    if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.getWidth() < 1.0F) {
                        double d2 = (double)(x - facing.getXOffset()) + 0.5D;
                        double d3 = (double)(z - facing.getZOffset()) + 0.5D;
                        AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - entityRadius, getGroundY(this.blockaccess, new BlockPos(d2, (double)(y + 1), d3)) + 0.001D, d3 - entityRadius, d2 + entityRadius, (double)this.entity.getHeight() + getGroundY(this.blockaccess, new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z)) - 0.002D, d3 + entityRadius);
                        if (!this.blockaccess.isCollisionBoxesEmpty(this.entity, axisalignedbb)) {
                            pathpoint = null;
                        }
                    }
                }

                if (pathnodetype == PathNodeType.OPEN) {
                    AxisAlignedBB axisalignedbb3 = new AxisAlignedBB((double)x - entityRadius + 0.5D, (double)y + 0.001D, (double)z - entityRadius + 0.5D, (double)x + entityRadius + 0.5D, (double)((float)y + this.entity.getSize(Pose.STANDING).height), (double)z + entityRadius + 0.5D);

                    if (this.entity.world.areCollisionShapesEmpty(axisalignedbb3)) {
                        return null;
                    }

                    if (this.getWidth() >= 1.0F) {
                        PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, x, y - 1, z);

                        if (pathnodetype1 == PathNodeType.BLOCKED) {
                            pathpoint = this.openPoint(x, y, z);
                            pathpoint.nodeType = PathNodeType.WALKABLE;
                            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                            return pathpoint;
                        }
                    }

                    int i = 0;

                    while (y > 0 && pathnodetype == PathNodeType.OPEN) {
                        --y;

                        if (i++ >= this.entity.getMaxFallHeight()) {
                            return null;
                        }

                        pathnodetype = this.getPathNodeType(this.entity, x, y, z);
                        f = this.entity.getPathPriority(pathnodetype);

                        if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
                            pathpoint = this.openPoint(x, y, z);
                            pathpoint.nodeType = pathnodetype;
                            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                            break;
                        }

                        if (f < 0.0F) {
                            return null;
                        }
                    }
                }

                return pathpoint;
            }
        }
    }


    // ==================== Path Nodes ====================
    @Override
    public PathNodeType getPathNodeType(IBlockReader world, int x, int y, int z, MobEntity mobEntity, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        if(this.swimming())
            return PathNodeType.WATER;

        EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathnodetype = PathNodeType.BLOCKED;
        BlockPos blockpos = new BlockPos(mobEntity);

        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                for (int k = 0; k < zSize; ++k) {
                    int l = i + x;
                    int i1 = j + y;
                    int j1 = k + z;
                    PathNodeType pathnodetype1 = this.getPathNodeType(world, l, i1, j1);

                    if (pathnodetype1 == PathNodeType.DOOR_WOOD_CLOSED && canBreakDoorsIn && canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.WALKABLE;
                    }

                    if (pathnodetype1 == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.BLOCKED;
                    }

                    if (pathnodetype1 == PathNodeType.RAIL && !(world.getBlockState(blockpos).getBlock() instanceof RailBlock) && !(world.getBlockState(blockpos.down()).getBlock() instanceof RailBlock)) {
                        pathnodetype1 = PathNodeType.FENCE;
                    }

                    if (i == 0 && j == 0 && k == 0) {
                        pathnodetype = pathnodetype1;
                    }

                    enumset.add(pathnodetype1);
                }
            }
        }

        if (enumset.contains(PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        else {
            PathNodeType pathnodetype2 = PathNodeType.BLOCKED;

            for (PathNodeType pathnodetype3 : enumset) {
                if (mobEntity.getPathPriority(pathnodetype3) < 0.0F) {
                    return pathnodetype3;
                }

                if (mobEntity.getPathPriority(pathnodetype3) >= mobEntity.getPathPriority(pathnodetype2)) {
                    pathnodetype2 = pathnodetype3;
                }
            }

            if (pathnodetype == PathNodeType.OPEN && mobEntity.getPathPriority(pathnodetype2) == 0.0F) {
                return PathNodeType.OPEN;
            }
            else {
                return pathnodetype2;
            }
        }
    }

    public PathNodeType getPathNodeType(MobEntity mobEntity, BlockPos pos) {
        if(this.swimming())
            return PathNodeType.WATER;

        return this.getPathNodeType(mobEntity, pos.getX(), pos.getY(), pos.getZ());
    }

    public PathNodeType getPathNodeType(MobEntity mobEntity, int x, int y, int z) {
        if(this.swimming())
            return PathNodeType.WATER;

        return this.getPathNodeType(this.blockaccess, x, y, z, mobEntity, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
    }

    @Override
    public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
        PathNodeType pathnodetype = this.getPathNodeTypeRaw(blockaccessIn, x, y, z);
        if (pathnodetype == PathNodeType.OPEN && y >= 1) {
            Block block = blockaccessIn.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
            PathNodeType pathnodetype1 = this.getPathNodeTypeRaw(blockaccessIn, x, y - 1, z);
            pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;

            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || block == Blocks.CAMPFIRE) {
                pathnodetype = PathNodeType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
                pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
                pathnodetype = PathNodeType.DAMAGE_OTHER;
            }
            if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) pathnodetype = PathNodeType.DAMAGE_OTHER; // Forge: consider modded damage types
        }

        pathnodetype = this.checkNeighborBlocks(blockaccessIn, x, y, z, pathnodetype);
        return pathnodetype;
    }

    public PathNodeType checkNeighborBlocks(IBlockReader blockaccessIn, int x, int y, int z, PathNodeType nodeType) {
        if (nodeType == PathNodeType.WALKABLE) {
            try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
                for(int i = -1; i <= 1; ++i) {
                    for(int j = -1; j <= 1; ++j) {
                        if (i != 0 || j != 0) {
                            BlockState state = blockaccessIn.getBlockState(blockpos$pooledmutableblockpos.setPos(i + x, y, j + z));
                            Block block = state.getBlock();
                            PathNodeType type = block.getAiPathNodeType(state, blockaccessIn, blockpos$pooledmutableblockpos, this.entity);
                            if (block == Blocks.CACTUS || type == PathNodeType.DAMAGE_CACTUS) {
                                nodeType = PathNodeType.DANGER_CACTUS;
                            } else if (block == Blocks.FIRE || type == PathNodeType.DAMAGE_FIRE) {
                                nodeType = PathNodeType.DANGER_FIRE;
                            } else if (block == Blocks.SWEET_BERRY_BUSH || type == PathNodeType.DAMAGE_OTHER) {
                                nodeType = PathNodeType.DANGER_OTHER;
                            }
                        }
                    }
                }
            }
        }

        return nodeType;
    }

    protected PathNodeType getPathNodeTypeRaw(IBlockReader blockaccessIn, int x, int y, int z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        BlockState blockstate = blockaccessIn.getBlockState(blockpos);
        PathNodeType type = blockstate.getAiPathNodeType(blockaccessIn, blockpos, this.entity);
        if (type != null) return type;
        Block block = blockstate.getBlock();
        Material material = blockstate.getMaterial();
        if (blockstate.isAir(blockaccessIn, blockpos)) {
            return PathNodeType.OPEN;
        } else if (!block.isIn(BlockTags.TRAPDOORS) && block != Blocks.LILY_PAD) {
            if (block == Blocks.FIRE) {
                return PathNodeType.DAMAGE_FIRE;
            } else if (block == Blocks.CACTUS) {
                return PathNodeType.DAMAGE_CACTUS;
            } else if (block == Blocks.SWEET_BERRY_BUSH) {
                return PathNodeType.DAMAGE_OTHER;
            } else if (block instanceof DoorBlock && material == Material.WOOD && !blockstate.get(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_WOOD_CLOSED;
            } else if (block instanceof DoorBlock && material == Material.IRON && !blockstate.get(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_IRON_CLOSED;
            } else if (block instanceof DoorBlock && blockstate.get(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_OPEN;
            } else if (block instanceof AbstractRailBlock) {
                return PathNodeType.RAIL;
            } else if (block instanceof LeavesBlock) {
                return PathNodeType.LEAVES;
            } else if (!block.isIn(BlockTags.FENCES) && !block.isIn(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockstate.get(FenceGateBlock.OPEN))) {
                IFluidState ifluidstate = blockaccessIn.getFluidState(blockpos);
                if (ifluidstate.isTagged(FluidTags.WATER)) {
                    return PathNodeType.WATER;
                } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
                    return PathNodeType.LAVA;
                } else {
                    return blockstate.allowsMovement(blockaccessIn, blockpos, PathType.LAND) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
                }
            } else {
                return PathNodeType.FENCE;
            }
        } else {
            return PathNodeType.TRAPDOOR;
        }
    }

    protected PathPoint getFlightNode(int x, int y, int z) {
        PathNodeType pathnodetype = this.isFlyablePathNode(x, y, z);
        if(this.entityCreature != null && this.entityCreature.isStrongSwimmer()) {
            if(pathnodetype == PathNodeType.WATER)
                return this.openPoint(x, y, z);
        }
        return pathnodetype == PathNodeType.OPEN ? this.openPoint(x, y, z) : null;
    }

    protected PathNodeType isFlyablePathNode(int x, int y, int z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int i = x; i < x + this.entitySizeX; ++i) {
            for (int j = y; j < y + this.entitySizeY; ++j) {
                for (int k = z; k < z + this.entitySizeZ; ++k) {
                    BlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));

                    // Non-Solid:
					if (!iblockstate.getMaterial().isSolid() && !iblockstate.getMaterial().isLiquid()) {
						return PathNodeType.OPEN;
					}

                    // Check For Open Air:
                    if (iblockstate.getMaterial() != Material.AIR) {
                        // If Can Swim Check For Swimmable Node:
                        if(this.entityCreature != null && this.entityCreature.isStrongSwimmer()) {
                            return this.isSwimmablePathNode(x, y, z);
                        }
                        return PathNodeType.BLOCKED;
                    }
                }
            }
        }

        return PathNodeType.OPEN;
    }

    @Nullable
    private PathPoint getWaterNode(int x, int y, int z) {
        PathNodeType pathnodetype = null;
        if(this.entityCreature != null && this.entityCreature.isFlying()) {
            pathnodetype = this.isFlyablePathNode(x, y, z);
            if(pathnodetype == PathNodeType.OPEN)
                return this.openPoint(x, y, z);
        }
        else {
            pathnodetype = this.isSwimmablePathNode(x, y, z);
        }
        return pathnodetype == PathNodeType.WATER ? this.openPoint(x, y, z) : null;
    }

    private PathNodeType isSwimmablePathNode(int x, int y, int z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int i = x; i < x + this.entitySizeX; ++i) {
            for (int j = y; j < y + Math.min(this.entitySizeY, 3); ++j) {
                for (int k = z; k < z + this.entitySizeZ; ++k) {
                    BlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));

                    if(this.entityCreature == null) {
                        return PathNodeType.BLOCKED;
                    }

                    // Water Swimming:
                    if(!this.entityCreature.waterDamage()) {
                        if (iblockstate.getMaterial() != Material.WATER) {
                            // Ooze Swimming:
                            if(!this.entityCreature.canFreeze()) {
                                if (iblockstate.getBlock() != ObjectManager.getBlock("ooze")) {
                                    return PathNodeType.BLOCKED;
                                }
                            }
                            return PathNodeType.BLOCKED;
                        }
                    }

                    // Lava Swimming:
                    else if(!this.entityCreature.canBurn()) {
                        if (iblockstate.getMaterial() != Material.LAVA) {
                            return PathNodeType.BLOCKED;
                        }
                    }

                    // Ooze Swimming (With Water Damage):
                    else if(!this.entityCreature.canFreeze()) {
                        if (iblockstate.getBlock() != ObjectManager.getBlock("ooze")) {
                            return PathNodeType.BLOCKED;
                        }
                    }
                }
            }
        }
        return PathNodeType.WATER;
    }
}
