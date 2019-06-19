package com.lycanitesmobs;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FileLoader {
	public enum PathType {
		CLIENT, SERVER, COMMON
	}

	/**
	 * Returns A Path instance for the provided asset path of the jar file that the provided class is in.
	 * @param clazz The class to base the jar file off of.
	 * @param domain The mod domain name.
	 * @param subpath The path inside of the mod's assets directory. Ex: "textures/blocks"
	 * @param pathType Whether to load from assets (resource packs, client side), data (data packs, server side) or common (both but not accessible to packs).
	 * @return
	 */
	public static Path getPath(Class clazz, String domain, String subpath, PathType pathType) {
		Path path = null;
		String root = "common";
		if(pathType == PathType.SERVER)
			root = "data";
		else if(pathType == PathType.CLIENT)
			root = "assets";

		String dir = "/" + root + "/" + domain + (!"".equals(subpath) ? "/" + subpath : "");
		try {
			URL url = clazz.getResource("/" + root + "/" + domain + "/" + ".root");
			URI uri = url.toURI();
			if ("file".equals(uri.getScheme())) {
				path = Paths.get(clazz.getResource(dir).toURI());
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
				path = filesystem.getPath(dir);
			}
		}
		catch (Exception e) {
			LycanitesMobs.logWarning("", "No data found in: " + dir);
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
