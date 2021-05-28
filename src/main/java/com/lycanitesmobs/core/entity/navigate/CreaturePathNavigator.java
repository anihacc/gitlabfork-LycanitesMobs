package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityJoustAlpha;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import java.util.Iterator;

public class CreaturePathNavigator extends PathNavigator {

    public BaseCreatureEntity entityCreature;
    protected BlockPos climbTargetPos;

    public CreaturePathNavigator(BaseCreatureEntity entityCreature, World world) {
        super(entityCreature, world);
        this.entityCreature = entityCreature;
    }

    /** Create PathFinder with CreatureNodeProcessor. **/
    @Override
    protected PathFinder getPathFinder(int searchRange) {
        this.nodeProcessor = new CreatureNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor, searchRange);
    }


    // ==================== Status ====================
    /** Returns true if the entity is capable of navigating at all. **/
    @Override
    protected boolean canNavigate() {
        if(this.entityCreature.isFlying())
            return true;
        if(this.entityCreature.isInWater()) {
            return this.entityCreature.canWade() || this.entityCreature.isStrongSwimmer();
        }
        return this.entity.isOnGround() || this.entity.isPassenger();
    }

    /** Sets if the creature should navigate as though it can break doors. **/
    public void setCanOpenDoors(boolean setBreakDoors) {
        this.nodeProcessor.setCanOpenDoors(setBreakDoors);
    }

    /** Sets if the creature should navigate as though it can break doors. **/
    public boolean getCanOpenDoors() {
        return this.nodeProcessor.getCanOpenDoors();
    }

    /** Sets if the creature should navigate as though it can break doors. **/
    public void setEnterDoors(boolean setBreakDoors) {
        this.nodeProcessor.setCanEnterDoors(setBreakDoors);
    }

    /** Returns if the creature should navigate as though it can enter doors. **/
    public boolean getEnterDoors() {
        return this.nodeProcessor.getCanEnterDoors();
    }


    // ==================== Create Path ====================
    /** Returns a new path from starting path position to the provided target position. **/
    @Override
    public Path getPathToPos(BlockPos pos, int i) {
        return super.getPathToPos(this.getSuitableDestination(pos), i);
    }

    /** Returns the path to the given EntityLiving. **/
    @Override
    public Path getPathToEntity(Entity entity, int i) {
        return this.getPathToPos(entity.getPosition(), i);
    }


    // ==================== Pathing Destination ====================
    /** Returns a suitable position close to the provided position if the position itself isn't suitable depending on how the creature can travel. **/
    protected BlockPos getSuitableDestination(BlockPos pos) {
        BlockState targetBlockState = this.world.getBlockState(pos);

        // Air:
        if(targetBlockState.isAir(this.world, pos)) {
            // Flying:
            if (this.entityCreature.isFlying()) {
                return pos;
            }

            // Walking:
            return this.getGround(pos);
        }

        // Non-Solid:
        if(!targetBlockState.getMaterial().isSolid()) {
            return pos;
        }

        // Solid:
        return this.getSurface(pos);
    }


    // ==================== Pathing Start ====================
    /** Return the position to path from. **/
    @Override
    protected Vector3d getEntityPosition() {
        return new Vector3d(this.entity.getPositionVec().getX(), (double)this.getPathablePosY(), this.entity.getPositionVec().getZ());
    }

    /** Return the Y position to path from. **/
    protected int getPathablePosY() {
        // If can swim:
        if(this.entityCreature.canSwim()) {

            // Slow swimmers (water bobbing):
            if(this.entityCreature.shouldFloat() && !this.entityCreature.shouldDive()) {
                int posY = (int)this.entity.getBoundingBox().minY;
                Block block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.getPositionVec().getX()), posY, MathHelper.floor(this.entity.getPositionVec().getZ()))).getBlock();
                int searchCount = 0;

                while (this.isSwimmableBlock(block)) { // Search up for surface.
                    ++posY;
                    block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.getPositionVec().getX()), posY, MathHelper.floor(this.entity.getPositionVec().getZ()))).getBlock();
                    ++searchCount;
                    if (searchCount > 16) {
                        return (int)this.entity.getBoundingBox().minY;
                    }
                }

                return posY;
            }

        }

        // Path From Current Y Pos:
        return (int)(this.entity.getBoundingBox().minY + 0.5D);
    }

    /** Starts pathing to target entity. **/
    @Override
    public boolean tryMoveToEntityLiving(Entity targetEntity, double speedIn) {
        Path path = this.getPathToEntity(targetEntity, 1);

        if (path != null) {
            return this.setPath(path, speedIn);
        }

        // Climbing:
        else if(this.entityCreature.canClimb()) {
            this.climbTargetPos = targetEntity.getPosition();
            this.speed = speedIn;
            return true;
        }

        return false;
    }


    // ==================== Testing ====================
    /** Returns true if the block is a block that the creature can swim in (water and lava for lava creatures). **/
    protected boolean isSwimmableBlock(Block block) {
        return this.isSwimmableBlock(block, 0);
    }

    /** Cached check skips the checking of the block type for performance. **/
    protected boolean isSwimmableBlock(Block block, int cachedCheck) {
        if(block == null || block == Blocks.AIR) {
            return false;
        }
        if(cachedCheck == 1 || this.isWaterBlock(block)) {
            return !this.entityCreature.waterDamage();
        }
        if(cachedCheck == 2 || this.isLavaBlock(block)) {
            return !this.entityCreature.canBurn();
        }
        if(cachedCheck == 3 || this.isOozeBlock(block)) {
            return !this.entityCreature.canFreeze();
        }
        return false;
    }

    /** Returns true if the block is a water block. **/
    protected boolean isWaterBlock(Block block) {
        return block == Blocks.WATER;
    }

    /** Returns true if the block is a lava block. **/
    protected boolean isLavaBlock(Block block) {
        return block == Blocks.LAVA || block == ObjectManager.getBlock("purelava");
    }

    /** Returns true if the block is a ooze block. **/
    protected boolean isOozeBlock(Block block) {
        return block == ObjectManager.getBlock("ooze");
    }

    /** Returns true if the entity can move to the block position. **/
    public boolean canEntityStandOnPos(BlockPos pos) {
        // Flight/Swimming:
        if(this.entityCreature.isFlying() || (this.entityCreature.canSwim() && this.entityCreature.isStrongSwimmer())) {
            BlockState blockState = this.world.getBlockState(pos);
            if(blockState.getMaterial().isLiquid()) {
            	return this.entityCreature.isStrongSwimmer();
			}
            return !blockState.getMaterial().isSolid();
        }
        return super.canEntityStandOnPos(pos);
    }


    // ==================== Block Searching ====================
    /** Searches for the ground from the provided position. If the void is hit then the initial position is returned. Experimental: Searched for non-solids instead of just air. **/
    public BlockPos getGround(BlockPos pos) {
        BlockPos resultPos;
        for(resultPos = pos.down(); resultPos.getY() > 0 && this.world.getBlockState(resultPos).isAir(this.world, resultPos); resultPos = resultPos.down()) {}

        if(resultPos.getY() > 0)
            return resultPos.up();

        while(resultPos.getY() < this.world.getHeight() && this.world.getBlockState(resultPos).isAir(this.world, resultPos))
            resultPos = resultPos.up();

        return resultPos;
    }

    /** Searches up for a non-solid block. If the sky limit is hit then the initial position is returned. **/
    public BlockPos getSurface(BlockPos pos) {
        BlockPos resultPos;
        for(resultPos = pos.up(); resultPos.getY() < this.world.getHeight() && !this.world.getBlockState(resultPos).isAir(this.world, resultPos); resultPos = resultPos.up()) {}
        if(resultPos.getY() == this.world.getHeight() && !this.world.getBlockState(resultPos).isAir(this.world, resultPos))
            return pos;
        return resultPos;
    }


    // ==================== Path Checks ====================
    /** Returns true if the path is a straight unblocked line, walking mobs also check for hazards along the line. **/
    @Override
    protected boolean isDirectPathBetweenPoints(Vector3d startVec, Vector3d endVec, int sizeX, int sizeY, int sizeZ) {
        // Flight/Swimming:
        if(this.entityCreature.isFlying() || this.entityCreature.canSwim()) {
            Vector3d vec3d = new Vector3d(endVec.x, endVec.y + (double)this.entity.getHeight() * 0.5D, endVec.z);
            RayTraceResult.Type directTraceType =  this.world.rayTraceBlocks(new RayTraceContext(startVec, vec3d, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.entity)).getType();
            return directTraceType == RayTraceResult.Type.MISS;
        }

        int startX = MathHelper.floor(startVec.x);
        int startZ = MathHelper.floor(startVec.z);
        double distanceX = endVec.x - startVec.x;
        double distanceY = endVec.z - startVec.z;
        double distance = distanceX * distanceX + distanceY * distanceY;

        if (distance < 1.0E-8D) {
            return false;
        }
        else {
            double d3 = 1.0D / Math.sqrt(distance);
            distanceX = distanceX * d3;
            distanceY = distanceY * d3;
            sizeX += 2;
            sizeZ += 2;

            if(!this.isSafeToStandAt(startX, (int)startVec.y, startZ, sizeX, sizeY, sizeZ, startVec, distanceX, distanceY)) {
                return false;
            }
            else {
                sizeX -= 2;
                sizeZ -= 2;
                double scanSpeedX = 1.0D / Math.abs(distanceX);
                double scanSpeedZ = 1.0D / Math.abs(distanceY);
                double scanX = (double) startX - startVec.x;
                double scanZ = (double) startZ - startVec.z;
                if (distanceX >= 0.0D) {
                    ++scanX;
                }

                if (distanceY >= 0.0D) {
                    ++scanZ;
                }

                scanX /= distanceX;
                scanZ /= distanceY;
                int scanPosX = distanceX < 0.0D ? -1 : 1;
                int scanPosZ = distanceY < 0.0D ? -1 : 1;
                int scanEndX = MathHelper.floor(endVec.x);
                int scanEndZ = MathHelper.floor(endVec.z);
                int scanDistanceX = scanEndX - startX;
                int scanDistanceZ = scanEndZ - startZ;

                do {
                    if (scanDistanceX * scanPosX <= 0 && scanDistanceZ * scanPosZ <= 0) {
                        return true;
                    }

                    if (scanX < scanZ) {
                        scanX += scanSpeedX;
                        startX += scanPosX;
                        scanDistanceX = scanEndX - startX;
                    }
                    else {
                        scanZ += scanSpeedZ;
                        startZ += scanPosZ;
                        scanDistanceZ = scanEndZ - startZ;
                    }
                } while (this.isSafeToStandAt(startX, MathHelper.floor(startVec.y), startZ, sizeX, sizeY, sizeZ, startVec, distanceX, distanceY));

                return false;
            }
        }
    }

    /** Returns true if the position is safe for the entity to stand at. **/
    protected boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3d startVec, double distanceX, double distanceZ) {
        // Performance Size Cap:
        sizeX = Math.min(sizeX, 2);
        sizeY = Math.min(sizeY, 3);
        sizeZ = Math.min(sizeZ, 2);

        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;

        if(!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, startVec, distanceX, distanceZ)) {
            return false;
        }
        else {
            for(int k = i; k < i + sizeX; ++k) {
                for (int l = j; l < j + sizeZ; ++l) {
                    double d0 = (double)k + 0.5D - startVec.x;
                    double d1 = (double)l + 0.5D - startVec.z;

                    if(d0 * distanceX + d1 * distanceZ >= 0.0D) {
                        PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y - 1, l, this.entity, sizeX, sizeY, sizeZ, true, true);

                        if(pathnodetype == PathNodeType.WATER) {
                            return false;
                        }

                        if(pathnodetype == PathNodeType.LAVA) {
                            return false;
                        }

                        if(pathnodetype == PathNodeType.OPEN) {
                            return false;
                        }

                        pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y, l, this.entity, sizeX, sizeY, sizeZ, true, true);
                        float f = this.entity.getPathPriority(pathnodetype);

                        if(f < 0.0F || f >= 8.0F) {
                            return false;
                        }

                        // Fire:
                        if(this.entityCreature.canBurn()) {
                            if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    /** Returns true if the position is clear. **/
    private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vector3d startVec, double distanceScaleX, double distanceScaleZ) {
        Iterator blockPosIterator = BlockPos.getAllInBoxMutable(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1)).iterator();

        BlockPos blockPos;
        double distanceX;
        double distanceZ;
        do {
            if (!blockPosIterator.hasNext()) {
                return true;
            }

            blockPos = (BlockPos)blockPosIterator.next();
            distanceX = (double)blockPos.getX() + 0.5D - startVec.x;
            distanceZ = (double)blockPos.getZ() + 0.5D - startVec.z;
        } while(distanceX * distanceScaleX + distanceZ * distanceScaleZ < 0.0D || this.world.getBlockState(blockPos).allowsMovement(this.world, blockPos, PathType.LAND));

        return false;
    }


    // ==================== Path Edits ====================
    /** Trims path data from the end to the first sun covered block. **/
    @Override
    protected void trimPath() {
        super.trimPath();

        for(int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
            PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);
            PathPoint pathpoint1 = i + 1 < this.currentPath.getCurrentPathLength() ? this.currentPath.getPathPointFromIndex(i + 1) : null;
            BlockState iblockstate = this.world.getBlockState(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z));
            Block block = iblockstate.getBlock();

            if (block == Blocks.CAULDRON) {
                this.currentPath.setPoint(i, pathpoint.cloneMove(pathpoint.x, pathpoint.y + 1, pathpoint.z));

                if(pathpoint1 != null && pathpoint.y >= pathpoint1.y) {
                    this.currentPath.setPoint(i + 1, pathpoint1.cloneMove(pathpoint1.x, pathpoint.y + 1, pathpoint1.z));
                }
            }
        }

        if(this.entityCreature.daylightBurns()) {
            if (this.world.canBlockSeeSky(new BlockPos(MathHelper.floor(this.entity.getPositionVec().getX()), (int)(this.entity.getBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getPositionVec().getZ())))) {
                return;
            }

            for(int j = 0; j < this.currentPath.getCurrentPathLength(); ++j) {
                PathPoint pathpoint2 = this.currentPath.getPathPointFromIndex(j);

                if(this.world.canBlockSeeSky(new BlockPos(pathpoint2.x, pathpoint2.y, pathpoint2.z))) {
                    this.currentPath.setPoint(j, pathpoint2.cloneMove(pathpoint2.x, pathpoint2.y + 1, pathpoint2.z)); // Clone last path point instead of decrementing the index in 1.12.2.
                    return;
                }
            }
        }
    }


    // ==================== Pathing ====================
    /** Follows the path moving to the next index when needed, etc. Called by tick() if canNavigate() and noPath() are both true. **/
    @Override
    protected void pathFollow() {
        float entityWidth = this.entity.getSize(Pose.STANDING).width;
        Vector3d currentPos = this.getEntityPosition();

        // Flight:
        if(this.entityCreature.isFlying() || this.entityCreature.canSwim()) {
            float entitySize = entityWidth * entityWidth;

            if (currentPos.squareDistanceTo(this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex())) < entitySize) {
                this.currentPath.incrementPathIndex();
            }

            int pathIndexRange = 6;
            for (int pathIndex = Math.min(this.currentPath.getCurrentPathIndex() + pathIndexRange, this.currentPath.getCurrentPathLength() - 1); pathIndex > this.currentPath.getCurrentPathIndex(); --pathIndex) {
                Vector3d pathVector = this.currentPath.getVectorFromIndex(this.entity, pathIndex);

                if (pathVector.squareDistanceTo(currentPos) <= 36.0D && this.isDirectPathBetweenPoints(currentPos, pathVector, 0, 0, 0)) {
                    this.currentPath.setCurrentPathIndex(pathIndex);
                    break;
                }
            }

            this.checkForStuck(currentPos);
            return;
        }

        // Walking:
        this.maxDistanceToWaypoint = entityWidth > 0.75F ? entityWidth : 0.75F - entityWidth / 2.0F;
        Vector3i pathTargetPos = this.currentPath.getTarget();
        double distanceX = Math.abs(this.entity.getPositionVec().getX() - (pathTargetPos.getX() + (entityWidth + 1) / 2D));
        double distanceY = Math.abs(this.entity.getPositionVec().getY() - pathTargetPos.getY());
        double distanceZ = Math.abs(this.entity.getPositionVec().getZ() - (pathTargetPos.getZ() + (entityWidth + 1) / 2D));
        boolean reachedTargetPos = distanceX <= this.maxDistanceToWaypoint && distanceY <= 1 && distanceZ <= this.maxDistanceToWaypoint;
        if (reachedTargetPos || this.entity.func_233660_b_(this.currentPath.func_237225_h_().nodeType) && this.func_234112_b_(currentPos)) {
            this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
        }
        this.checkForStuck(currentPos);
    }

    /** Copied from PathNavigator. **/
    private boolean func_234112_b_(Vector3d currentPosition) {
        if (this.currentPath.getCurrentPathIndex() + 1 >= this.currentPath.getCurrentPathLength()) {
            return false;
        } else {
            Vector3d vector3d = Vector3d.copyCenteredHorizontally(this.currentPath.func_242948_g());
            if (!currentPosition.isWithinDistanceOf(vector3d, 2.0D)) {
                return false;
            } else {
                Vector3d vector3d1 = Vector3d.copyCenteredHorizontally(this.currentPath.func_242947_d(this.currentPath.getCurrentPathIndex() + 1));
                Vector3d vector3d2 = vector3d1.subtract(vector3d);
                Vector3d vector3d3 = currentPosition.subtract(vector3d);
                return vector3d2.dotProduct(vector3d3) > 0.0D;
            }
        }
    }

    /** Called on entity update to update the navigation progress. **/
    @Override
    public void tick() {
        // Clear Path If Close To Last Node: (Stop stupid spinning)
        if(this.hasPath()) {
            PathPoint finalPoint = this.getPath().getFinalPathPoint();
            Vector3d finalVec = new Vector3d((double)finalPoint.x, (double)finalPoint.y, (double)finalPoint.z);
            if(this.entityCreature.getDistanceSq(finalVec) <= this.entityCreature.getSize(Pose.STANDING).width) {
                this.clearPath();
            }
        }

        // Climbing Tick:
        if (this.noPath() && this.entityCreature.canClimb() && this.climbTargetPos != null) {
            double d0 = (double)(this.entity.getSize(Pose.STANDING).width * this.entity.getSize(Pose.STANDING).width);

            if (this.entity.getDistanceSq(Vector3d.copy(this.climbTargetPos)) >= d0 && (this.entity.getPositionVec().getY() <= (double)this.climbTargetPos.getY() || this.entity.getDistanceSq(new Vector3d(this.climbTargetPos.getX(), MathHelper.floor(this.entity.getPositionVec().getY()), this.climbTargetPos.getZ())) >= d0)) {
                this.entity.getMoveHelper().setMoveTo((double)this.climbTargetPos.getX(), (double)this.climbTargetPos.getY(), (double)this.climbTargetPos.getZ(), this.speed);
            }
            else {
                this.climbTargetPos = null;
            }
            return;
        }

        // Update Path and Move:
        if (this.hasPath()) {
            ++this.totalTicks;
            if (this.tryUpdatePath) {
                this.updatePath();
            }

            if (this.canNavigate()) {
                if(this.currentPath != null) {
                    this.pathFollow();
                }
            }
            else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
                Vector3d vec3d = this.getEntityPosition();
                Vector3d vec3d1 = this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex());
                if (vec3d.y > vec3d1.y && !this.entity.isOnGround() && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d1.z)) {
                    this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
                }
            }

            DebugPacketSender.sendPath(this.world, this.entity, this.currentPath, this.maxDistanceToWaypoint);
            if (this.hasPath()) {
                Vector3d currentPos = this.currentPath.getPosition(this.entity);
                BlockPos blockpos = new BlockPos(currentPos);
                this.entity.getMoveHelper().setMoveTo(currentPos.x, this.world.getBlockState(blockpos.down()).isAir() ? currentPos.y : WalkNodeProcessor.getGroundY(this.world, blockpos), currentPos.z, this.speed);
            }
        }
    }
}
