package com.lycanitesmobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.time.temporal.ChronoUnit;
import java.util.*;

public class Utilities {
	// ==================================================
  	//                      Raytrace
  	// ==================================================
	// ========== Raytrace All ==========
    public static RayTraceResult raytrace(World world, double x, double y, double z, double tx, double ty, double tz, float borderSize, Entity entity, HashSet<Entity> excluded) {
		Vec3d startVec = new Vec3d(x, y, z);
        Vec3d lookVec = new Vec3d(tx - x, ty - y, tz - z);
        Vec3d endVec = new Vec3d(tx, ty, tz);
		float minX = (float)(x < tx ? x : tx);
		float minY = (float)(y < ty ? y : ty);
		float minZ = (float)(z < tz ? z : tz);
		float maxX = (float)(x > tx ? x : tx);
		float maxY = (float)(y > ty ? y : ty);
		float maxZ = (float)(z > tz ? z : tz);

		// Get Block Collision:
        RayTraceResult collision = world.rayTraceBlocks(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
		startVec = new Vec3d(x, y, z);
		endVec = new Vec3d(tx, ty, tz);
		float distance = (float)endVec.distanceTo(startVec);

		// Get Entity Collision:
		if(excluded != null) {
			AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).expand(borderSize, borderSize, borderSize);
			List<Entity> allEntities = world.getEntitiesWithinAABBExcludingEntity(null, bb);
			Entity closestHitEntity = null;
			float closestHit = Float.POSITIVE_INFINITY;
			float currentHit = distance;
			AxisAlignedBB entityBb;
			EntityRayTraceResult intercept;
			for(Entity ent : allEntities) {
				if(ent.canBeCollidedWith() && !excluded.contains(ent)) {
					float entBorder = ent.getCollisionBorderSize();
					entityBb = ent.getBoundingBox();
					entityBb = entityBb.expand(entBorder, entBorder, entBorder);
					intercept = ProjectileHelper.func_221271_a(entity.getEntityWorld(), entity, startVec, endVec, entity.getBoundingBox().expand(entity.getMotion()).grow(1.0D), (hitEntity) -> hitEntity != entity);
					if(intercept != null) {
						currentHit = (float) intercept.getHitVec().distanceTo(startVec);
						if(currentHit < closestHit || currentHit == 0) {
							closestHit = currentHit;
							closestHitEntity = ent;
						}
					}
				}
			}
			if(closestHitEntity != null) {
				collision = new EntityRayTraceResult(closestHitEntity);
			}
		}
		
		return collision;
    }
	
	
	// ==================================================
  	//                      Seasonal
  	// ==================================================
	public static boolean isValentines() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) == calendar.FEBRUARY && calendar.get(Calendar.DAY_OF_MONTH) >= 7 && calendar.get(Calendar.DAY_OF_MONTH) <= 14;
	}

	protected static Calendar easterCalendar;
	public static boolean isEaster() {
		Calendar calendar = Calendar.getInstance();

		if(easterCalendar == null) {
			int Y = calendar.get(Calendar.YEAR);
			int a = Y % 19;
			int b = Y / 100;
			int c = Y % 100;
			int d = b / 4;
			int e = b % 4;
			int f = (b + 8) / 25;
			int g = (b - f + 1) / 3;
			int h = (19 * a + b - d - g + 15) % 30;
			int i = c / 4;
			int k = c % 4;
			int L = (32 + 2 * e + 2 * i - h - k) % 7;
			int m = (a + 11 * h + 22 * L) / 451;
			int easterMonth = (h + L - 7 * m + 114) / 31;
			int easterDay = ((h + L - 7 * m + 114) % 31) + 1;
			easterCalendar = new GregorianCalendar(Y, easterMonth, easterDay);
		}

		long daysUntilEaster = ChronoUnit.DAYS.between(calendar.toInstant(), easterCalendar.toInstant());
		return daysUntilEaster <= 7 && daysUntilEaster >= 0;
	}

	public static boolean isMidsummer() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) == calendar.JULY && calendar.get(Calendar.DAY_OF_MONTH) >= 10 && calendar.get(Calendar.DAY_OF_MONTH) <= 20;
	}

    public static boolean isHalloween() {
    	Calendar calendar = Calendar.getInstance();
		if(		(calendar.get(Calendar.DAY_OF_MONTH) >= 25 && calendar.get(Calendar.MONTH) == calendar.OCTOBER)
			||	(calendar.get(Calendar.DAY_OF_MONTH) == 1 && calendar.get(Calendar.MONTH) == calendar.NOVEMBER)
		)
			return true;
		return false;
    }

    public static boolean isYuletide() {
    	Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) == calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) >= 10 && calendar.get(Calendar.DAY_OF_MONTH) <= 25;
    }

    public static boolean isYuletidePeak() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) == 25;
    }

    public static boolean isNewYear() {
    	Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) == calendar.JANUARY && calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

	public static int daysBetween(Date d1, Date d2){
		return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
	}
}
