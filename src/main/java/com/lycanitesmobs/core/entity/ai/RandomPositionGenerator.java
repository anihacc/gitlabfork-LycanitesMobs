package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;

import java.util.Random;

public class RandomPositionGenerator {
	private static Vec3d staticVector = new Vec3d(0.0D, 0.0D, 0.0D);

    // ==================================================
 	//                 Find Random Target
 	// ==================================================
    public static Vec3d findRandomTarget(EntityCreatureBase entity, int range, int height) {
        return findRandomTarget(entity, range, height, 0);
    }
    public static Vec3d findRandomTarget(EntityCreatureBase entity, int range, int height, int heightLevel) {
        return getTargetBlock(entity, range, height, (Vec3d)null, heightLevel);
    }

    // ========== Find Random Waypoint to Target ==========
    public static Vec3d findRandomTargetTowards(EntityCreatureBase entity, int range, int height, Vec3d par3Vec3d) {
        staticVector = new Vec3d(par3Vec3d.x - entity.posX, par3Vec3d.y - entity.posY, par3Vec3d.z - entity.posZ);
        return findRandomTargetTowards(entity, range, height, staticVector, 0);
    }
    public static Vec3d findRandomTargetTowards(EntityCreatureBase entity, int range, int height, Vec3d par3Vec3d, int heightLevel) {
        staticVector = new Vec3d(par3Vec3d.x - entity.posX, par3Vec3d.y - entity.posY, par3Vec3d.z - entity.posZ);
        return getTargetBlock(entity, range, height, staticVector, heightLevel);
    }

    // ========== Find Random Waypoint from Target ==========
    public static Vec3d findRandomTargetAwayFrom(EntityCreatureBase entity, int range, int height, Vec3d avoidTarget) {
        return findRandomTargetAwayFrom(entity, range, height, avoidTarget, 0);
    }
    public static Vec3d findRandomTargetAwayFrom(EntityCreatureBase entity, int range, int height, Vec3d avoidTarget, int heightLevel) {
        staticVector = new Vec3d(entity.posX, entity.posY, entity.posZ).subtract(avoidTarget);
        return getTargetBlock(entity, range, height, staticVector, heightLevel);
    }

    // ========== Get Target Block ==========
    private static Vec3d getTargetBlock(EntityCreatureBase entity, int range, int height, Vec3d target, int heightLevel) {
        PathNavigator pathNavigate = entity.getNavigator();
        Random random = entity.getRNG();
        boolean validTarget = false;
        int targetX = 0;
        int targetY = 0;
        int targetZ = 0;
        float pathMin = -99999.0F;
        boolean pastHome;

        if(entity.hasHome()) {
            double homeDist = (entity.getHomePosition().distanceSq(entity.getPosition()) + 4.0F);
            double homeDistMax = (double)(entity.getHomeDistanceMax() + (float)range);
            pastHome = homeDist < homeDistMax * homeDistMax;
        }
        else
        	pastHome = false;

        for(int attempt = 0; attempt < 10; ++attempt) {
            int possibleX = random.nextInt(2 * range) - range;
            int possibleY = random.nextInt(2 * height) - height;
            int possibleZ = random.nextInt(2 * range) - range;

            // Random Height:
            if(entity.isFlying() || (entity.isStrongSwimmer() && entity.isInWater())) {
	            if(entity.posY > entity.getEntityWorld().getHeight(Heightmap.Type.OCEAN_FLOOR_WG, entity.getPosition()).getY() + (heightLevel * 1.25))
	        		possibleY = random.nextInt(2 * height) - height * 3 / 2;
	            else if(entity.posY < entity.getEntityWorld().getHeight(Heightmap.Type.OCEAN_FLOOR_WG, entity.getPosition()).getY() + heightLevel)
	            	possibleY = random.nextInt(2 * height) - height / 2;
            }

            if(target == null || (double)possibleX * target.x + (double)possibleZ * target.z >= 0.0D) {
            	possibleX += MathHelper.floor(entity.posX);
            	possibleY += MathHelper.floor(entity.posY);
            	possibleZ += MathHelper.floor(entity.posZ);
                BlockPos possiblePos = new BlockPos(possibleX, possibleY, possibleZ);

                if((!pastHome || entity.positionNearHome(possibleX, possibleY, possibleZ)) && pathNavigate.canEntityStandOnPos(possiblePos)) {
                    float pathWeight = entity.getBlockPathWeight(possibleX, possibleY, possibleZ);
                    if(pathWeight > pathMin) {
                    	pathMin = pathWeight;
                    	targetX = possibleX;
                    	targetY = possibleY;
                    	targetZ = possibleZ;
                        validTarget = true;
                    }
                }
            }
        }

        if(validTarget)
            return new Vec3d((double)targetX, (double)targetY, (double)targetZ);
        else
        	return null;
    }
}
