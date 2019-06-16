package com.lycanitesmobs.core.entity.goals;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Comparator;

public class TargetSorterNearest implements Comparator {
    private final Entity host;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================

    public TargetSorterNearest(Entity setHost) {
        this.host = setHost;
    }
    
    
    // ==================================================
   	//                      Compare
   	// ==================================================
    public int compare(Object objectA, Object objectB) {
    	if(objectA instanceof Entity && objectB instanceof Entity)
    		return this.compareDistanceSq((Entity)objectA, (Entity)objectB);
    	if(objectA instanceof BlockPos && objectB instanceof BlockPos)
    		return this.compareDistanceSq((BlockPos)objectA, (BlockPos)objectB);
    	return 0;
    }
    
    
    // ==================================================
  	//                   Compare Distance
  	// ==================================================
    public int compareDistanceSq(Entity targetA, Entity targetB) {
        double distanceA = this.host.getDistance(targetA);
        double distanceB = this.host.getDistance(targetB);
        return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
    }

    public int compareDistanceSq(BlockPos targetA, BlockPos targetB) {
        BlockPos hostCoords = new BlockPos((int)this.host.posX, (int)this.host.posY, (int)this.host.posZ);
        double distanceA = hostCoords.distanceSq(new Vec3i(targetA.getX(), targetA.getY(), targetA.getZ()));
        double distanceB = hostCoords.distanceSq(new Vec3i(targetB.getX(), targetB.getY(), targetB.getZ()));
        return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
    }
}
