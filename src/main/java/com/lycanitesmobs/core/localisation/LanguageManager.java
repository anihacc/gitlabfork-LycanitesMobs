package com.lycanitesmobs.core.localisation;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.ItemCharge;
import com.lycanitesmobs.core.item.consumable.ItemCustomFood;
import com.lycanitesmobs.core.item.consumable.ItemTreat;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.item.temp.ItemScepter;
import com.lycanitesmobs.core.item.temp.ItemStaffSummoning;
import com.lycanitesmobs.core.item.temp.ItemSwordBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.text.translation.I18n;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
		if(!getInstance().map.containsKey(key)) {
			//LycanitesMobs.printInfo("", "Translating " + key + " to " + I18n.translateToLocal(key));
			return I18n.translateToLocal(key);
		}
		return getInstance().map.get(key);
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
	public void loadLanguage(String mainLanguage) {
		LycanitesMobs.printDebug("Localisation", "Loading additional lang files...");

		// Get Languages To Load:
		List<String> languageList = Lists.newArrayList(mainLanguage);
		if (!"en_us".equals(mainLanguage)) {
			languageList.add(mainLanguage);
		}

		// Load Languages Into Map:
		int laodedLangFiles = 0;
		for (String language : languageList) {
			String languageDir = String.format("lang/%s/", language);
			Path languageDirPath = Utilities.getAssetPath(LycanitesMobs.modInfo.getClass(), LycanitesMobs.modInfo.filename, languageDir);
			try {
				// Iterate Language Directories:
				Iterator<Path> languageDirIter = Files.walk(languageDirPath).iterator();
				while(languageDirIter.hasNext()) {
					Path subdirPath = languageDirIter.next();

					// Read Root Lang File:
					if(!Files.isDirectory(subdirPath)) {
						LycanitesMobs.printDebug("Localisation", "Reading translations from lang: " + subdirPath.toAbsolutePath());
						LanguageManager.getInstance().loadLocaleData(Files.newInputStream(subdirPath));
						laodedLangFiles++;
					}

				}
			}
			catch (Exception var9) {}
		}

		LycanitesMobs.printDebug("Localisation", laodedLangFiles + " Additional lang files loaded! Test translation: " + LanguageManager.translate("lycanitesmobs.test"));
	}


	/**
	 * Creatures a collection of lang files using old lang files if possible. Used for development only.
	 */
	public void generateLangs() {
		// Creatures:
		for(CreatureInfo creature : CreatureManager.getInstance().creatures.values()) {
			String creatureLangPath = LycanitesMobs.proxy.getMinecraftDir() + "/lang/en_us/creatures/" + creature.getName() + ".lang";
			LycanitesMobs.printInfo("", "Creating lang file for creature: " + creature.getName() + " path: " + creatureLangPath);
			try {
				File langFile = new File(creatureLangPath);
				langFile.createNewFile();
				FileOutputStream outputStream = new FileOutputStream(langFile);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
				outputStreamWriter.append("entity.lycanitesmobs." + creature.getName() + ".name=" + creature.getTitle());
				outputStreamWriter.append("\nentity.lycanitesmobs." + creature.getName() + ".description=" + creature.getDescription());
				outputStreamWriter.append("\nentity.lycanitesmobs." + creature.getName() + ".habitat=" + "Habitat information coming soon!");
				outputStreamWriter.append("\nentity.lycanitesmobs." + creature.getName() + ".combat=" + "Combat information coming soon!");
				outputStreamWriter.close();
				outputStream.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}

		// Items:
		List<String> addedItems = new ArrayList<>();
		for(String itemType : new String[] { "food", "equipment", "charges", "treats", "tools", "scepters", "blocks", "misc" }) {
			try {
				String itemsLangPath = LycanitesMobs.proxy.getMinecraftDir() + "/lang/en_us/items/" + itemType + ".lang";
				File langFile = new File(itemsLangPath);
				langFile.createNewFile();
				FileOutputStream outputStream = new FileOutputStream(langFile);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
				boolean first = true;
				for (Item item : ObjectManager.items.values()) {
					if("food".equals(itemType)) {
						if (!(item instanceof ItemCustomFood))
							continue;
					}
					else if("equipment".equals(itemType)) {
						if (!(item instanceof ItemEquipmentPart))
							continue;
					}
					else if("charges".equals(itemType)) {
						if(!(item instanceof ItemCharge))
							continue;
					}
					else if("treats".equals(itemType)) {
						if(!(item instanceof ItemTreat))
							continue;
					}
					else if("tools".equals(itemType)) {
						if(!(item instanceof ItemSwordBase) && !(item instanceof ItemStaffSummoning))
							continue;
					}
					else if("scepters".equals(itemType)) {
						if(!(item instanceof ItemScepter))
							continue;
					}
					else if("blocks".equals(itemType)) {
						if(!(item instanceof ItemBlock))
							continue;
					}

					if(addedItems.contains(item.getUnlocalizedName())) {
						continue;
					}
					addedItems.add(item.getUnlocalizedName());

					LycanitesMobs.printInfo("", "Creating lang file for item: " + item.getUnlocalizedName() + " path: " + itemsLangPath);
					if(!first) {
						outputStreamWriter.append("\n");
					}
					first = false;
					outputStreamWriter.append(item.getUnlocalizedName() + ".name=" + translate(item.getUnlocalizedName() + ".name"));
					if(!(item instanceof ItemEquipmentPart)) {
						outputStreamWriter.append("\n" + item.getUnlocalizedName() + ".description=" + translate(item.getUnlocalizedName() + ".description"));
					}
					outputStreamWriter.append("\n#");
				}
				outputStreamWriter.close();
				outputStream.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}
}
