package com.lycanitesmobs.client.localisation;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LanguageManager {
	public static LanguageManager INSTANCE;
	protected static final Splitter SPLITTER = Splitter.on('=').limit(2);
	protected static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

	protected Map<String, String> map = new HashMap<>();

	/** Returns the main Item Manager instance or creates it and returns it. **/
	public static LanguageManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new LanguageManager();
		}
		return INSTANCE;
	}


	/**
	 * Translates the provided text key, if it cannot be translated, attempts to translate it with the vanilla translator instead.
	 * @param key The key to translate into text.
	 * @return
	 */
	public static String translate(String key) {
		return I18n.translateToLocal(key);
	}





	/**
	 * Loads locale data from a specific file stream.
	 * @param inputStreamIn The input stream to read from.
	 * @throws IOException
	 */
	public void loadLocaleData(InputStream inputStreamIn) throws IOException {
		inputStreamIn = net.minecraftforge.fml.common.FMLCommonHandler.instance().loadLanguage(this.map, inputStreamIn);
		if (inputStreamIn == null) return;
		for (String s : IOUtils.readLines(inputStreamIn, StandardCharsets.UTF_8)) {
			if (!s.isEmpty() && s.charAt(0) != '#') {
				String[] splitters = Iterables.toArray(SPLITTER.split(s), String.class);

				if (splitters != null && splitters.length == 2) {
					String s1 = splitters[0];
					String s2 = PATTERN.matcher(splitters[1]).replaceAll("%$1s");
					this.map.put(s1, s2);
				}
			}
		}
	}


	/**
	 * Loads the provided language.
	 * @param mainLanguage The language to load, default is en_us.
	 */
	public void loadLanguage(String mainLanguage, IResourceManager resourceManager) {
		LycanitesMobs.logDebug("Localisation", "Loading additional lang files...");

		// Get Languages To Load:
		List<String> languageList = Lists.newArrayList(mainLanguage);
		if (!"en_us".equals(mainLanguage)) {
			languageList.add(mainLanguage);
		}

		// Load Languages Into Map:
		int laodedLangFiles = 0;
		for (String language : languageList) {
			String languageDir = String.format("lang/%s/", language);
			Path languageWalkPath = Utilities.getAssetPath(LycanitesMobs.modInfo.getClass(), LycanitesMobs.modInfo.modid, "lang/en_us/"); // Always walk en_us files.
			try {
				// Iterate Language Directories:
				Iterator<Path> languageDirIter = Files.walk(languageWalkPath).iterator();
				String languageSubdir = "";
				while(languageDirIter.hasNext()) {
					Path subdirPath = languageDirIter.next();

					// Read Root Lang File:
					if(!Files.isDirectory(subdirPath)) {
						try {
							if(!subdirPath.toString().replace("\\", "/").contains(languageSubdir)) {
								languageSubdir = "";
							}
							String languagePath = languageDir + languageSubdir + subdirPath.getFileName();
							LycanitesMobs.logDebug("Language", "Reading translations from lang: " + languagePath + " Subdir Path:" + subdirPath.toString().replace("en_us", language));
							ResourceLocation langLocation = new ResourceLocation(LycanitesMobs.modInfo.modid, languagePath);
							getInstance().loadLocaleData(resourceManager.getResource(langLocation).getInputStream());
							//getInstance().loadLocaleData(Files.newInputStream(subdirPath));
						}
						catch(Exception e) {
							LycanitesMobs.logWarning("", "Error reading translations from lang: " + languageDir + languageSubdir + subdirPath.getFileName() + " Subdir: " + languageSubdir + " Subdir Path: " + subdirPath.toString().replace("en_us", language));
							//throw new RuntimeException(e);
						}
						laodedLangFiles++;
					}
					else {
						languageSubdir = subdirPath.getName(subdirPath.getNameCount() - 1).toString().replace("\\", "/");
						if(!languageSubdir.substring(languageSubdir.length() - 1).equals("/"))
							languageSubdir = languageSubdir + "/";
						LycanitesMobs.logDebug("Language", "Setting Subdir: " + languageSubdir);
					}

				}
			}
			catch (Exception e) {
				//throw new RuntimeException(e);
			}
		}

		LycanitesMobs.logDebug("Localisation", laodedLangFiles + " Additional lang files loaded! Test translation: " + LanguageManager.translate("lycanitesmobs.test"));
	}
}
