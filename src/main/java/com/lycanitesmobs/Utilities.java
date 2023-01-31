package com.lycanitesmobs;

import com.lycanitesmobs.core.config.ConfigBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import org.apache.commons.io.FilenameUtils;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class Utilities {
    
	// ==================================================
  	//                    Dungeon Loot
  	// ==================================================
	public static void addDungeonLoot(ItemStack itemStack, int minAmount, int maxAmount, int weight) {
        ConfigBase config = LycanitesMobs.config;
        config.setCategoryComment("Dungeon Loot Enabled", "Here you can enable/disable dungeon loot for various types of dungeons.");
        if(config.getBool("Dungeon Loot Enabled", "Dungeons", true, "These are most dungeons from underground mob spawner dungeons to pyramids, mineshafts and jungle temples.")) {
            /*ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));*/
        }
	}

	public static void addStrongholdLoot(ItemStack itemStack, int minAmount, int maxAmount, int weight) {
        ConfigBase config = LycanitesMobs.config;
        if(config.getBool("Dungeon Loot Enabled", "Strongholds", true, "Stronghold dungeons including corridors, libraries and other parts.")) {
            /*ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CROSSING, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));*/
        }
	}

	public static void addVillageLoot(ItemStack itemStack, int minAmount, int maxAmount, int weight) {
        ConfigBase config = LycanitesMobs.config;
        if(config.getBool("Dungeon Loot Enabled", "Blacksmiths", true, "These are the chests found in village blacksmiths homes.")) {
            //ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
        }
	}
	
	
	// ==================================================
  	//                      Raytrace
  	// ==================================================
	// ========== Raytrace All ==========
	public static RayTraceResult raytrace(World world, Vec3d start, Vec3d end, boolean stopOnLiquid,
			boolean ignoreBlockWithoutBoundingBox, float rayWidth, @Nullable Predicate<Entity> entityPredicate) {
		RayTraceResult blockResult = raytraceBlocks(world, start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox);
		RayTraceResult entityResult = raytraceEntities(world, start, end, rayWidth, entityPredicate);

		if (blockResult != null && blockResult.typeOfHit != Type.MISS) {
			if (entityResult != null && entityResult.typeOfHit != Type.MISS) {
				if (start.squareDistanceTo(blockResult.hitVec) <= start.squareDistanceTo(entityResult.hitVec)) {
					return blockResult;
				} else {
					return entityResult;
				}
			} else {
				return blockResult;
			}
		} else if (entityResult != null && entityResult.typeOfHit != Type.MISS) {
			return entityResult;
		}

		return new RayTraceResult(Type.MISS, end, null, new BlockPos(end));
	}

	public static RayTraceResult raytraceBlocks(World world, Vec3d start, Vec3d end, boolean stopOnLiquid,
			boolean ignoreBlockWithoutBoundingBox) {
		RayTraceResult result = world.rayTraceBlocks(start, end, stopOnLiquid, ignoreBlockWithoutBoundingBox, false);
		if (result == null || result.typeOfHit == Type.MISS) {
			return new RayTraceResult(Type.MISS, end, null, new BlockPos(end));
		}
		return result;
	}

	public static <T extends Entity> RayTraceResult raytraceEntities(World world, Vec3d start, Vec3d end,
			float rayWidth, @Nullable Predicate<Entity> predicate) {
		AxisAlignedBB aabb = new AxisAlignedBB(start, end);
		if (rayWidth != 0.0F) {
			aabb = aabb.grow(rayWidth);
		}
		List<Entity> possibleEntities = world.getEntitiesWithinAABB(Entity.class, aabb,
				predicate != null ? predicate::test : null);

		Entity closestHitEntity = null;
		Vec3d closestHitLocation = null;
		double closestHitDistSqr = Double.POSITIVE_INFINITY;
		for (Entity entity : possibleEntities) {
			AxisAlignedBB entityAabb = entity.getEntityBoundingBox();
			if (rayWidth + entity.getCollisionBorderSize() != 0.0F) {
				entityAabb = entityAabb.grow(rayWidth + entity.getCollisionBorderSize());
			}

			RayTraceResult currentHit = entityAabb.calculateIntercept(start, end);
			if (currentHit == null) {
				continue;
			}

			double currentHitDistSqr = start.squareDistanceTo(currentHit.hitVec);
			if (currentHitDistSqr < closestHitDistSqr) {
				closestHitEntity = entity;
				closestHitLocation = currentHit.hitVec;
				closestHitDistSqr = currentHitDistSqr;
			}
		}

		if (closestHitEntity == null || closestHitLocation == null) {
			return new RayTraceResult(Type.MISS, end, null, new BlockPos(end));
		}

		return new RayTraceResult(closestHitEntity, closestHitLocation);
	}

	public static Predicate<Entity> collidable() {
		return entity -> EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith();
	}

	public static Predicate<Entity> collidableExlcuding(Entity entityToIgnore) {
		if (entityToIgnore == null)
			return collidable();
		return entity -> EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith()
				&& entity != entityToIgnore;
	}

	public static Predicate<Entity> collidableExlcuding(Entity entityToIgnore1, Entity entityToIgnore2) {
		if (entityToIgnore1 == null)
			return collidableExlcuding(entityToIgnore2);
		if (entityToIgnore2 == null)
			return collidableExlcuding(entityToIgnore1);
		return entity -> EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith()
				&& entity != entityToIgnore1 && entity != entityToIgnore2;
	}

	public static Predicate<Entity> collidableExlcuding(Entity entityToIgnore1, Entity entityToIgnore2,
			Entity entityToIgnore3) {
		if (entityToIgnore1 == null)
			return collidableExlcuding(entityToIgnore2, entityToIgnore3);
		if (entityToIgnore2 == null)
			return collidableExlcuding(entityToIgnore1, entityToIgnore3);
		if (entityToIgnore3 == null)
			return collidableExlcuding(entityToIgnore1, entityToIgnore2);
		return entity -> EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith()
				&& entity != entityToIgnore1 && entity != entityToIgnore2 && entity != entityToIgnore3;
	}

	public static Predicate<Entity> collidableExlcuding(Entity... entities) {
		if (entities.length >= 0 && entities.length < 4) {
			if (entities.length == 0) {
				return collidable();
			} else if (entities.length == 1) {
				return collidableExlcuding(entities[0]);
			} else if (entities.length == 2) {
				return collidableExlcuding(entities[0], entities[1]);
			} else if (entities.length == 3) {
				return collidableExlcuding(entities[0], entities[1], entities[2]);
			}
		}
		Set<Entity> entitySet = Arrays.stream(entities).filter(Objects::nonNull).collect(Collectors.toSet());
		return entity -> EntitySelectors.NOT_SPECTATING.test(entity) && entity.canBeCollidedWith()
				&& !entitySet.contains(entity);
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


	// ==================================================
	//                   File Loading
	// ==================================================

	/**
	 * Returns A Path instance for the provided asset path of the jar file that the provided class is in.
	 * @param clazz The class to base the jar file off of.
	 * @param assetDomain The mod domain name.
	 * @param assetPath The path inside of the mod's assets directory. Ex: "textures/blocks"
	 * @return
	 */
	public static Path getAssetPath(Class clazz, String assetDomain, String assetPath) {
		Path path = null;
		String assetDir = "/assets/" + assetDomain + (!"".equals(assetPath) ? "/" + assetPath : "");
		try {
			URL url = clazz.getResource("/assets/" + assetDomain + "/" + ".root");
			URI uri = url.toURI();
			if ("file".equals(uri.getScheme())) {
				path = Paths.get(clazz.getResource(assetDir).toURI());
			}
			else {
				if (!"jar".equals(uri.getScheme())) {
					LycanitesMobs.logWarning("", "Unsupported file scheme: " + uri.getScheme());
					return null;
				}
				FileSystem filesystem;
				try {
					filesystem = FileSystems.getFileSystem(uri);
				}
				catch (Exception e) {
					filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
				}
				path = filesystem.getPath(assetDir);
			}
		}
		catch (Exception e) {
			LycanitesMobs.logWarning("", "No data found in: " + assetDir);
			//e.printStackTrace();
		}

		return path;
	}

	/**
	 * Returns a list of ResourceLocations for every file in the provided Path instance.
	 * @param path The directory Path instance to read from.
	 * @param assetDomain The mod domain name.
	 * @param fileType The file extension to use. Ex: "png"
	 * @return
	 */
	public static List<ResourceLocation> getPathResourceLocations(Path path, String assetDomain, String fileType) {
		List<ResourceLocation> resourceLocations = new ArrayList<>();
		try {
			Iterator<Path> iterator = Files.walk(path).iterator();
			while(iterator.hasNext()) {
				Path filePath = iterator.next();
				if (fileType == null || fileType.equals(FilenameUtils.getExtension(filePath.toString()))) {
					Path relativePath = path.relativize(filePath);
					String resourceLocationPath = FilenameUtils.removeExtension(relativePath.toString()).replaceAll("\\\\", "/");
					ResourceLocation resourceLocation = new ResourceLocation(assetDomain, resourceLocationPath);
					resourceLocations.add(resourceLocation);
				}
			}
		}
		catch (Exception e) {
			LycanitesMobs.logWarning("", "There was a problem getting ResourceLocations for: " + path + ", " + fileType + ", " + " \n" + e.toString());
		}

		return resourceLocations;
	}
}
