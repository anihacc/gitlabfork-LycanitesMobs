package com.lycanitesmobs.core.entity.navigate;

import com.google.common.collect.Sets;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityJoustAlpha;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.fluid.FluidState;
import net.minecraft.pathfinding.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Region;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public class CreatureNodeProcessor extends NodeProcessor implements ICreatureNodeProcessor {

    public BaseCreatureEntity entityCreature;
    protected float avoidsWater;

    public static double getGroundY(IBlockReader blockReader, BlockPos pos) {
        BlockPos blockpos = pos.below();
        VoxelShape voxelshape = blockReader.getBlockState(blockpos).getCollisionShape(blockReader, blockpos);
        return (double)blockpos.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.max(Direction.Axis.Y));
    }

    // ==================== Setup ====================
    @Override // init()
    public void prepare(Region region, MobEntity mob) {
        super.prepare(region, mob);
        this.avoidsWater = mob.getPathfindingMalus(PathNodeType.WATER);
        if(mob instanceof BaseCreatureEntity)
            this.entityCreature = (BaseCreatureEntity)mob;
        this.updateEntitySize(mob);
    }

    @Override
    public void updateEntitySize(Entity updateEntity) {
        this.entityWidth = MathHelper.floor(this.getWidth(true, updateEntity) + 1.0F);
        this.entityHeight = MathHelper.floor(updateEntity.getDimensions(Pose.STANDING).height + 1.0F);
        this.entityDepth = MathHelper.floor(this.getWidth(true, updateEntity) + 1.0F);
    }

    @Override
    public void done() {
        if(this.entityCreature != null)
            this.mob.setPathfindingMalus(PathNodeType.WATER, this.avoidsWater);
        super.done();
    }


    // ==================== Checks ====================
    /** Returns true if the entity is capable of pathing/moving in water at all. **/
    @Override
    public boolean canFloat() {
        if(this.entityCreature != null)
            return this.entityCreature.canWade() || this.entityCreature.isStrongSwimmer();
        return super.canFloat();
    }

    /** Returns true if the entity should use swimming focused pathing. **/
    public boolean swimming() {
        if(this.entityCreature == null) {
            return false;
        }
        if(this.entityCreature.isInWater()) {
            return this.entityCreature.isStrongSwimmer() || (this.entityCreature.canWade() && this.entityCreature.shouldDive());
        }
        return false;
    }

    /** Returns true if the entity should use flight focused pathing. **/
    public boolean flying() {
        return this.entityCreature != null && this.entityCreature.isFlying() && !this.entityCreature.isUnderWater();
    }

    /**
     * Returns a width to path with.
     * @param blockChecks If true, this width is used for checking blocks, a reduced width can be returned for better performance here.
     * @return The entity width to path with.
     */
    public double getWidth(boolean blockChecks) {
		return this.getWidth(blockChecks, this.mob);
    }

    /**
     * Returns a width to path with.
     * @param blockChecks If true, this width is used for checking blocks, a reduced width can be returned for better performance here.
     * @param entity The entity to get the width of.
     * @return The entity width to path with.
     */
    public double getWidth(boolean blockChecks, Entity entity) {
		return Math.min(3, (double)entity.getDimensions(Pose.STANDING).width);
	}


    // ==================== Start ====================
    /** Returns a PathPoint to the given coordinates. **/
    @Override
    public FlaggedPathPoint getGoal(double x, double y, double z) { // getPathPointToCoords
        // Flying/Strong Swimming:
        if(this.flying() || this.swimming()) {
            return new FlaggedPathPoint(this.getNode(MathHelper.floor(x - this.getWidth(false)), MathHelper.floor(y + 0.5D), MathHelper.floor(z - this.getWidth(false))));
        }
        return new FlaggedPathPoint(this.getNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z)));
    }

    /** Returns the starting position to create a new path from. **/
    @Override
    public PathPoint getStart() {
        // Flying/Strong Swimming:
        if(this.flying() || (this.entityCreature.isStrongSwimmer() && this.entityCreature.isUnderWater())) {
            return this.getNode(MathHelper.floor(this.mob.getBoundingBox().minX), MathHelper.floor(this.mob.getBoundingBox().minY + 0.5D), MathHelper.floor(this.mob.getBoundingBox().minZ));
        }

        // Wading Through Water:
        int posY;
        if (this.canFloat() && this.mob.isUnderWater()) { // If can swim and is swimming underwater
            posY = (int)this.mob.getBoundingBox().minY;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(MathHelper.floor(this.mob.position().x()), posY, MathHelper.floor(this.mob.position().z()));

            for (FluidState fluidState = this.level.getFluidState(blockpos$mutable); fluidState.is(FluidTags.WATER); fluidState = this.level.getFluidState(blockpos$mutable)) {
                ++posY;
                blockpos$mutable.set(MathHelper.floor(this.mob.position().x()), posY, MathHelper.floor(this.mob.position().z()));
            }
        }

        // Walking On ground:
        else if (this.mob.isOnGround()) {
            posY = MathHelper.floor(this.mob.getY() + 0.5D);
        }

        // In Air:
        else {
            BlockPos blockpos;
            for (blockpos = this.mob.blockPosition(); (this.level.getBlockState(blockpos).getMaterial() == Material.AIR || !this.level.getBlockState(blockpos).canOcclude()) && blockpos.getY() > 0; blockpos = blockpos.below()) {} // Was isPassable instead of isSolid
            posY = blockpos.above().getY();
        }

        // XZ Offset:
        BlockPos offsetXZ = this.mob.blockPosition();
        PathNodeType targetNodeType = this.getBlockPathType(this.level, offsetXZ.getX(), posY, offsetXZ.getZ());

        if (this.mob.getPathfindingMalus(targetNodeType) < 0.0F) {
            Set<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.mob.getBoundingBox().minX, (double)posY, this.mob.getBoundingBox().minZ));
            set.add(new BlockPos(this.mob.getBoundingBox().minX, (double)posY, this.mob.getBoundingBox().maxZ));
            set.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)posY, this.mob.getBoundingBox().minZ));
            set.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)posY, this.mob.getBoundingBox().maxZ));

            for (BlockPos betterPos : set) {
                PathNodeType pathnodetype = this.getPathNodeType(this.mob, betterPos);
                if(this.mob.getPathfindingMalus(pathnodetype) >= 0.0F) {
                    return this.getNode(betterPos.getX(), betterPos.getY(), betterPos.getZ());
                }
            }
        }

        return this.getNode(offsetXZ.getX(), posY, offsetXZ.getZ());
    }


    // ==================== Options ====================
    /** Checks points around the provided fromPoint and adds it to path options if it is a valid point to travel to. **/
    @Override
    public int getNeighbors(PathPoint[] pathOptions, PathPoint fromPoint) {
        // Flying/Strong Swimming/Diving:
        if(this.flying() || this.swimming()) {
            int i = 0;
            for (Direction direction : Direction.values()) {
                PathPoint pathPoint = null;
                if(this.swimming()) {
                    pathPoint = this.getWaterNode(fromPoint.x + direction.getStepX(), fromPoint.y + direction.getStepY(), fromPoint.z + direction.getStepZ());
                }
                if(pathPoint == null) {
                    pathPoint = this.getFlightNode(fromPoint.x + direction.getStepX(), fromPoint.y + direction.getStepY(), fromPoint.z + direction.getStepZ());
                }
                if(pathPoint != null && !pathPoint.closed) {
                    pathOptions[i++] = pathPoint;
                }
            }
            return i;
        }

        // Walking:
        int i = 0;
        int pathPriority = 0;
        PathNodeType pathnodetype = this.getPathNodeType(this.mob, fromPoint.x, fromPoint.y + 1, fromPoint.z);
        if (this.mob.getPathfindingMalus(pathnodetype) >= 0.0F) {
            pathPriority = MathHelper.floor(Math.max(1.0F, this.mob.maxUpStep));
        }

        double groundY = getGroundY(this.level, new BlockPos(fromPoint.x, fromPoint.y, fromPoint.z));

        PathPoint southPoint = this.getSafePoint(fromPoint.x, fromPoint.y, fromPoint.z + 1, pathPriority, groundY, Direction.SOUTH);
        if (southPoint != null && !southPoint.closed && southPoint.costMalus >= 0.0F) {
            pathOptions[i++] = southPoint;
        }

        PathPoint westPoint = this.getSafePoint(fromPoint.x - 1, fromPoint.y, fromPoint.z, pathPriority, groundY, Direction.WEST);
        if (westPoint != null && !westPoint.closed && westPoint.costMalus >= 0.0F) {
            pathOptions[i++] = westPoint;
        }

        PathPoint eastPoint = this.getSafePoint(fromPoint.x + 1, fromPoint.y, fromPoint.z, pathPriority, groundY, Direction.EAST);
        if (eastPoint != null && !eastPoint.closed && eastPoint.costMalus >= 0.0F) {
            pathOptions[i++] = eastPoint;
        }

        PathPoint northPoint = this.getSafePoint(fromPoint.x, fromPoint.y, fromPoint.z - 1, pathPriority, groundY, Direction.NORTH);
        if (northPoint != null && !northPoint.closed && northPoint.costMalus >= 0.0F) {
            pathOptions[i++] = northPoint;
        }

        PathPoint northWestPoint = this.getSafePoint(fromPoint.x - 1, fromPoint.y, fromPoint.z - 1, pathPriority, groundY, Direction.NORTH);
        if (this.testDiagonalPoint(fromPoint, westPoint, northPoint, northWestPoint)) {
            pathOptions[i++] = northWestPoint;
        }

        PathPoint northEastPoint = this.getSafePoint(fromPoint.x + 1, fromPoint.y, fromPoint.z - 1, pathPriority, groundY, Direction.NORTH);
        if (this.testDiagonalPoint(fromPoint, eastPoint, northPoint, northEastPoint)) {
            pathOptions[i++] = northEastPoint;
        }

        PathPoint southWestPoint = this.getSafePoint(fromPoint.x - 1, fromPoint.y, fromPoint.z + 1, pathPriority, groundY, Direction.SOUTH);
        if (this.testDiagonalPoint(fromPoint, westPoint, southPoint, southWestPoint)) {
            pathOptions[i++] = southWestPoint;
        }

        PathPoint southEastPoint = this.getSafePoint(fromPoint.x + 1, fromPoint.y, fromPoint.z + 1, pathPriority, groundY, Direction.SOUTH);
        if (this.testDiagonalPoint(fromPoint, eastPoint, southPoint, southEastPoint)) {
            pathOptions[i++] = southEastPoint;
        }

        return i;
    }

    /** Checks a diagonal point (such as North East) and returns true if it should be added to the path options. **/
    private boolean testDiagonalPoint(PathPoint targetPoint, @Nullable PathPoint lateralPoint, @Nullable PathPoint longitudinalPoint, @Nullable PathPoint diagonalPoint) {
        return diagonalPoint != null && !diagonalPoint.closed && longitudinalPoint != null && longitudinalPoint.costMalus >= 0.0F && longitudinalPoint.y <= targetPoint.y && lateralPoint != null && lateralPoint.costMalus >= 0.0F && lateralPoint.y <= targetPoint.y;
    }

    /** Returns a point that the entity can move to safely from the provided coords, or null if invalid, only used when walking and checks for drops/climbs. **/
    @Nullable
    private PathPoint getSafePoint(int x, int y, int z, int stepHeight, double fromGroundY, Direction direction) {
        PathPoint safePoint = null;
        BlockPos blockPos = new BlockPos(x, y, z);
        double groundY = getGroundY(this.level, blockPos);

        if (groundY - fromGroundY > 1.125D) {
            return null;
        }

        PathNodeType pathnodetype = this.getPathNodeType(this.mob, x, y, z);
        float pathPriority = this.mob.getPathfindingMalus(pathnodetype);
        double entityRadius = this.getWidth(true) / 2;

        if (pathPriority >= 0.0F) {
            safePoint = this.getNode(x, y, z);
            safePoint.type = pathnodetype;
            safePoint.costMalus = Math.max(safePoint.costMalus, pathPriority);
        }

        if (pathnodetype == PathNodeType.WALKABLE) {
            return safePoint;
        }

        if ((safePoint == null || safePoint.costMalus < 0.0F) && stepHeight > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
            safePoint = this.getSafePoint(x, y + 1, z, stepHeight - 1, fromGroundY, direction);

            if (safePoint != null && (safePoint.type == PathNodeType.OPEN || safePoint.type == PathNodeType.WALKABLE) && this.getWidth(false) < 1.0F) {
                double offsetX = (double)(x - direction.getStepX()) + 0.5D;
                double offsetZ = (double)(z - direction.getStepZ()) + 0.5D;
                AxisAlignedBB axisalignedbb = new AxisAlignedBB(offsetX - entityRadius, getGroundY(this.level, new BlockPos(offsetX, (double)(y + 1), offsetZ)) + 0.001D, offsetZ - entityRadius, offsetX + entityRadius, (double)this.mob.getBbHeight() + getGroundY(this.level, new BlockPos(safePoint.x, safePoint.y, safePoint.z)) - 0.002D, offsetZ + entityRadius);
                if (!this.level.noCollision(this.mob, axisalignedbb)) {
                    safePoint = null;
                }
            }
        }

        // Surface Weak Swimming:
        if (pathnodetype == PathNodeType.WATER && !this.canFloat()) {
            if (this.getPathNodeType(this.mob, x, y - 1, z) != PathNodeType.WATER) {
                return safePoint;
            }

            while(y > 0) {
                --y;
                pathnodetype = this.getPathNodeType(this.mob, x, y, z);
                if (pathnodetype != PathNodeType.WATER) {
                    return safePoint;
                }

                safePoint = this.getNode(x, y, z);
                safePoint.type = pathnodetype;
                safePoint.costMalus = Math.max(safePoint.costMalus, this.mob.getPathfindingMalus(pathnodetype));
            }
        }

        if (pathnodetype == PathNodeType.OPEN) {
            AxisAlignedBB pathingCollision = new AxisAlignedBB((double)x - entityRadius + 0.5D, (double)y + 0.001D, (double)z - entityRadius + 0.5D, (double)x + entityRadius + 0.5D, (double)((float)y + this.mob.getDimensions(Pose.STANDING).height), (double)z + entityRadius + 0.5D);

            if (!this.level.noCollision(this.mob, pathingCollision)) {
                return null;
            }

            if (this.getWidth(false) >= 1.0F) {
                PathNodeType pathnodetype1 = this.getPathNodeType(this.mob, x, y - 1, z);

                if (pathnodetype1 == PathNodeType.BLOCKED) {
                    safePoint = this.getNode(x, y, z);
                    safePoint.type = PathNodeType.WALKABLE;
                    safePoint.costMalus = Math.max(safePoint.costMalus, pathPriority);
                    return safePoint;
                }
            }

            // Test Drop:
            int i = 0;
            int initialY = y;
            while(pathnodetype == PathNodeType.OPEN) {
                --y;
                if (y < 0) {
                    PathPoint voidDropPoint = this.getNode(x, initialY, z);
                    voidDropPoint.type = PathNodeType.BLOCKED;
                    voidDropPoint.costMalus = -1.0F;
                    return voidDropPoint;
                }

                PathPoint dropPoint = this.getNode(x, y, z);
                if (i++ >= this.mob.getMaxFallDistance()) {
                    dropPoint.type = PathNodeType.BLOCKED;
                    dropPoint.costMalus = -1.0F;
                    return dropPoint;
                }

                pathnodetype = this.getPathNodeType(this.mob, x, y, z);
                pathPriority = this.mob.getPathfindingMalus(pathnodetype);
                if (pathnodetype != PathNodeType.OPEN && pathPriority >= 0.0F) {
                    safePoint = dropPoint;
                    dropPoint.type = pathnodetype;
                    dropPoint.costMalus = Math.max(dropPoint.costMalus, pathPriority);
                    break;
                }

                if (pathPriority < 0.0F) {
                    dropPoint.type = PathNodeType.BLOCKED;
                    dropPoint.costMalus = -1.0F;
                    return dropPoint;
                }
            }
        }

        return safePoint;
    }


    // ==================== Path Nodes ====================
    @Override
    public PathNodeType getBlockPathType(IBlockReader world, int x, int y, int z, MobEntity mobEntity, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        if(this.swimming())
            return PathNodeType.WATER;

        EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathnodetype = PathNodeType.BLOCKED;
        BlockPos blockpos = mobEntity.blockPosition();

        for (int i = -xSize; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                for (int k = -zSize; k < zSize; ++k) {
                    int l = i + x;
                    int i1 = j + y;
                    int j1 = k + z;
                    PathNodeType pathnodetype1 = this.getBlockPathType(world, l, i1, j1);

                    if (pathnodetype1 == PathNodeType.DOOR_WOOD_CLOSED && canBreakDoorsIn && canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.WALKABLE;
                    }

                    if (pathnodetype1 == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.BLOCKED;
                    }

                    if (pathnodetype1 == PathNodeType.RAIL && !(world.getBlockState(blockpos).getBlock() instanceof RailBlock) && !(world.getBlockState(blockpos.below()).getBlock() instanceof RailBlock)) {
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
                if (mobEntity.getPathfindingMalus(pathnodetype3) < 0.0F) {
                    return pathnodetype3;
                }

                if (mobEntity.getPathfindingMalus(pathnodetype3) >= mobEntity.getPathfindingMalus(pathnodetype2)) {
                    pathnodetype2 = pathnodetype3;
                }
            }

            if (pathnodetype == PathNodeType.OPEN && mobEntity.getPathfindingMalus(pathnodetype2) == 0.0F) {
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
        return this.getBlockPathType(this.level, x, y, z, mobEntity, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
    }

    @Override
    public PathNodeType getBlockPathType(IBlockReader blockaccessIn, int x, int y, int z) {
        PathNodeType pathnodetype = this.getPathNodeTypeRaw(blockaccessIn, x, y, z);

        // Water:
        if (pathnodetype == PathNodeType.WATER) {
            for(Direction direction : Direction.values()) {
                PathNodeType pathnodetype2 = this.getPathNodeTypeRaw(blockaccessIn, x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ());
                if (pathnodetype2 == PathNodeType.BLOCKED) {
                    return PathNodeType.WATER_BORDER;
                }
            }
            return PathNodeType.WATER;
        }

        // Open:
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
            BlockPos.Mutable blockpos$pooledmutable = new BlockPos.Mutable();
            for(int i = -1; i <= 1; ++i) {
                for(int j = -1; j <= 1; ++j) {
                    if (i != 0 || j != 0) {
                        BlockState state = blockaccessIn.getBlockState(blockpos$pooledmutable.set(i + x, y, j + z));
                        Block block = state.getBlock();
                        PathNodeType type = block.getAiPathNodeType(state, blockaccessIn, blockpos$pooledmutable, this.mob);
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

        return nodeType;
    }

    protected PathNodeType getPathNodeTypeRaw(IBlockReader blockaccessIn, int x, int y, int z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        BlockState blockstate = blockaccessIn.getBlockState(blockpos);
        PathNodeType type = blockstate.getAiPathNodeType(blockaccessIn, blockpos, this.mob);
        if (type != null) return type;
        Block block = blockstate.getBlock();
        Material material = blockstate.getMaterial();
        if (blockstate.isAir(blockaccessIn, blockpos)) {
            return PathNodeType.OPEN;
        } else if (!block.is(BlockTags.TRAPDOORS) && block != Blocks.LILY_PAD) {
            if (block == Blocks.FIRE) {
                return PathNodeType.DAMAGE_FIRE;
            } else if (block == Blocks.CACTUS) {
                return PathNodeType.DAMAGE_CACTUS;
            } else if (block == Blocks.SWEET_BERRY_BUSH) {
                return PathNodeType.DAMAGE_OTHER;
            } else if (block instanceof DoorBlock && material == Material.WOOD && !blockstate.getValue(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_WOOD_CLOSED;
            } else if (block instanceof DoorBlock && material == Material.METAL && !blockstate.getValue(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_IRON_CLOSED;
            } else if (block instanceof DoorBlock && blockstate.getValue(DoorBlock.OPEN)) {
                return PathNodeType.DOOR_OPEN;
            } else if (block instanceof AbstractRailBlock) {
                return PathNodeType.RAIL;
            } else if (block instanceof LeavesBlock) {
                return PathNodeType.LEAVES;
            } else if (!block.is(BlockTags.FENCES) && !block.is(BlockTags.WALLS) && (!(block instanceof FenceGateBlock) || blockstate.getValue(FenceGateBlock.OPEN))) {
                FluidState ifluidstate = blockaccessIn.getFluidState(blockpos);
                if (ifluidstate.is(FluidTags.WATER)) {
                    return PathNodeType.WATER;
                } else if (ifluidstate.is(FluidTags.LAVA)) {
                    return PathNodeType.LAVA;
                } else {
                    return blockstate.isPathfindable(blockaccessIn, blockpos, PathType.LAND) ? PathNodeType.OPEN : PathNodeType.BLOCKED;
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
                return this.getNode(x, y, z);
        }
        return pathnodetype == PathNodeType.OPEN ? this.getNode(x, y, z) : null;
    }

    protected PathNodeType isFlyablePathNode(int x, int y, int z) {
        BlockPos centerPos = new BlockPos(x, y, z);
        for (int i = 0; i <= this.entityWidth; ++i) {
            for (int j = 0; j <= Math.min(this.entityHeight, 2); ++j) {
                for (int k = 0; k <= this.entityDepth; ++k) {
                    BlockState iblockstate = this.level.getBlockState(centerPos.offset(i, k, j));

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
        PathNodeType pathnodetype;
        if(this.entityCreature != null && this.entityCreature.isFlying()) {
            pathnodetype = this.isFlyablePathNode(x, y, z);
            if(pathnodetype == PathNodeType.OPEN)
                return this.getNode(x, y, z);
        }
        else {
            pathnodetype = this.isSwimmablePathNode(x, y, z);
        }
        return pathnodetype == PathNodeType.WATER ? this.getNode(x, y, z) : null;
    }

    private PathNodeType isSwimmablePathNode(int x, int y, int z) {
        BlockPos centerPos = new BlockPos(x, y, z);
        for (int i = 0; i <= this.entityWidth; ++i) {
            for (int j = 0; j <= Math.min(this.entityHeight, 2); ++j) {
                for (int k = 0; k <= this.entityDepth; ++k) {
                    BlockPos blockPos = centerPos.offset(i, k, j);
                    BlockState blockState = this.level.getBlockState(blockPos);
                    FluidState fluidState = this.level.getFluidState(blockPos);

                    if(this.entityCreature == null || !blockState.isPathfindable(this.level, blockPos, PathType.WATER)) {
                        if(j == y) { // Y must be water.
                            return PathNodeType.BLOCKED;
                        }
                        if(!blockState.isPathfindable(this.level, blockPos, PathType.AIR) && !blockState.isPathfindable(this.level, blockPos, PathType.WATER)) { // Blocked above water.
                            return PathNodeType.BLOCKED;
                        }
                    }

                    // Water Damages:
                    if(this.entityCreature.waterDamage() && fluidState.is(FluidTags.WATER)) {
                        return PathNodeType.BLOCKED;
                    }

                    // Lava Damages:
                    if(this.entityCreature.canBurn() && fluidState.is(FluidTags.LAVA)) {
                        return PathNodeType.BLOCKED;
                    }

                    // Ooze Swimming (With Water Damage):
                    if(this.entityCreature.canFreeze() && ObjectManager.getBlock("ooze") != null && blockState.getBlock() == ObjectManager.getBlock("ooze")) {
                        return PathNodeType.BLOCKED;
                    }
                }
            }
        }
        return PathNodeType.WATER;
    }
}
