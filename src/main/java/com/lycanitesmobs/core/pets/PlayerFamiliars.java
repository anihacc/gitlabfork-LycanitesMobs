package com.lycanitesmobs.core.pets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Variant;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;

public class PlayerFamiliars {
    public static PlayerFamiliars INSTANCE = new PlayerFamiliars();
    public Map<UUID, Map<UUID, PetEntry>> playerFamiliars = new HashMap<>();
	public Map<UUID, Long> playerFamiliarLoadedTimes = new HashMap<>();
    public List<String> familiarBlacklist = new ArrayList<>();

	public static class FamiliarLoader implements Runnable {
		EntityPlayer player;

		public FamiliarLoader(EntityPlayer player) {
			this.player = player;
		}

		@Override
		public void run() {
			this.getForPlayerUUID(this.player.getUniqueID());
			ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(this.player);
			if (extendedPlayer != null) {
				extendedPlayer.loadFamiliars();
			}
			PlayerFamiliars.INSTANCE.updatePlayerFamiliarLoadedTime(this.player);
		}

		private void getForPlayerUUID(UUID uuid) {
			String jsonString;
			try {
				URL familiarsURL = new URL(LycanitesMobs.serviceAPI + "/familiars?minecraft_uuid=" + uuid.toString());
				URLConnection urlConnection = familiarsURL.openConnection();
				String osName = System.getProperty("os.name");
				urlConnection.setRequestProperty("Authorization", "Bearer 7ed1f44cbc1aff693e604075f23d56402983a4a0"); // This is a public api so the key is safe to be exposed like this. :)
				urlConnection.setRequestProperty("User-Agent", "Minecraft " + LycanitesMobs.versionMC + " (" + osName + ") LycanitesMobs " + LycanitesMobs.versionNumber);
				InputStream inputStream = urlConnection.getInputStream();
				try {
					jsonString = IOUtils.toString(inputStream, (Charset) null);
				} catch (Exception e) {
					throw e;
				} finally {
					inputStream.close();
				}
				LycanitesMobs.logInfo("", "Online familiars loaded successfully for " + uuid + ".");
			} catch (Throwable e) {
				LycanitesMobs.logInfo("", "Unable to access the online familiars service.");
				e.printStackTrace();
				return;
			}

			// Parse JSON File:
			PlayerFamiliars.INSTANCE.parseFamiliarJSON(jsonString);
		}
	}

    // Parses JSON to Familiars, returns false if the JSON is invalid.
    public void parseFamiliarJSON(String jsonString) {
        try {
			JsonParser jsonParser = new JsonParser();
			JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
			JsonArray jsonArray = json.getAsJsonArray("data");
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				try {
					// Familiar UUIDs:
					JsonObject familiarJson = jsonIterator.next().getAsJsonObject();
					UUID minecraft_uuid = UUID.fromString(familiarJson.get("minecraft_uuid").getAsString());
					if (this.familiarBlacklist.contains(minecraft_uuid.toString())) {
						continue;
					}
					UUID familiar_uuid = UUID.fromString(familiarJson.get("familiar_uuid").getAsString());

					// Familiar Properties:
					String familiar_species = familiarJson.get("familiar_species").getAsString();
					CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(familiar_species);
					int familiar_subspecies = familiarJson.get("familiar_subspecies").getAsInt();
					familiar_species = this.handleLegacySpecies(familiar_species);
					int familiar_variant = familiarJson.get("familiar_variant").getAsInt();
					Variant creatureVariant = creatureInfo != null ? creatureInfo.getSubspecies(familiar_subspecies).getVariant(familiar_variant) : null;
					String familiar_name = familiarJson.get("familiar_name").getAsString();
					String familiar_color = familiarJson.get("familiar_color").getAsString();
					double familiar_size = familiarJson.get("familiar_size").getAsDouble();
					if(familiar_size <= 0) { // Default Reduced Size:
						familiar_size = 0.5D;
						if(creatureVariant != null && creatureVariant.scale != 1) {
							familiar_size *= 1 / creatureVariant.scale;
						}
					}

					// Create Familiar Pet Entry:
					PetEntryFamiliar familiarEntry = new PetEntryFamiliar(familiar_uuid, null, familiar_species.toLowerCase());
					familiarEntry.setEntitySubspecies(familiar_subspecies);
					familiarEntry.setEntityVariant(familiar_variant);
					familiarEntry.setEntitySize(familiar_size);
					if (!"".equals(familiar_name)) {
						familiarEntry.setEntityName(familiar_name);
					}
					familiarEntry.setColor(familiar_color);

					// Add Pet Entries or Update Existing Entries:
					if (!this.playerFamiliars.containsKey(minecraft_uuid)) {
						this.playerFamiliars.put(minecraft_uuid, new HashMap<>());
					}
					if (!this.playerFamiliars.get(minecraft_uuid).containsKey(familiar_uuid)) {
						this.playerFamiliars.get(minecraft_uuid).put(familiar_uuid, familiarEntry);
					}
					else {
						PetEntry existingEntry = this.playerFamiliars.get(minecraft_uuid).get(familiar_uuid);
						existingEntry.copy(familiarEntry);
					}
				}
				catch(Exception e) {}
            }
        }
        catch(Exception e) {
            /*LycanitesMobs.logWarning("", "A problem occurred when loading online player familiars:");
            e.printStackTrace();*/
        }
    }

	public Map<UUID, PetEntry> getFamiliarsForPlayer(EntityPlayer player) {
		long currentTime = System.currentTimeMillis() / 1000;
		long loadedTime = this.getPlayerFamiliarLoadedTime(player);
		if (loadedTime < 0 || currentTime - loadedTime > 30 * 60) {
			this.updatePlayerFamiliarLoadedTime(player);
			FamiliarLoader familiarLoader = new FamiliarLoader(player);
			Thread thread = new Thread(familiarLoader);
			thread.start();
		}

		Map<UUID, PetEntry> playerFamiliarEntries = new HashMap<>();
		if (this.playerFamiliars.containsKey(player.getUniqueID())) {
			playerFamiliarEntries = this.playerFamiliars.get(player.getUniqueID());
			for(PetEntry familiarEntry : playerFamiliarEntries.values()) {
				if(familiarEntry.host == null) {
					familiarEntry.host = player;
				}
			}
		}
		return playerFamiliarEntries;
	}

	public long getPlayerFamiliarLoadedTime(EntityPlayer player) {
		UUID uuid = player.getUniqueID();
		if (!this.playerFamiliarLoadedTimes.containsKey(uuid)) {
			return -1;
		}
		return this.playerFamiliarLoadedTimes.get(uuid);
	}

	public void updatePlayerFamiliarLoadedTime(EntityPlayer player) {
		this.playerFamiliarLoadedTimes.put(player.getUniqueID(), System.currentTimeMillis() / 1000);
	}

	/**
	 * Converts newer 1.16.5+ version species names into old ones for 1.12.2
	 * @param species The species name to convert from newer versions down to 1.12.2 equivalents.
	 * @return The 1.12.2 species name to use.
	 */
	public String handleLegacySpecies(String species) {
		if (species.equalsIgnoreCase("apollyon")) {
			return "archvile";
		}
		if (species.equalsIgnoreCase("belphegor")) {
			return "belph";
		}
		if (species.equalsIgnoreCase("behemophet")) {
			return "behemoth";
		}
		if (species.equalsIgnoreCase("malwrath")) {
			return "cacodemon";
		}
		if (species.equalsIgnoreCase("naxiris")) {
			return "beholder";
		}
		if (species.equalsIgnoreCase("zephyr")) {
			return "djinn";
		}
		if (species.equalsIgnoreCase("raidra")) {
			return "zephyr";
		}
		if (species.equalsIgnoreCase("ningen")) {
			return "dweller";
		}
		if (species.equalsIgnoreCase("cherufe")) {
			return "lobber";
		}
		if (species.equalsIgnoreCase("ostimien")) {
			return "lurker";
		}
		if (species.equalsIgnoreCase("kathoga")) {
			return "pinky";
		}
		if (species.equalsIgnoreCase("brucha")) {
			return "quillbeast";
		}
		if (species.equalsIgnoreCase("stryder")) {
			return "strider";
		}
		if (species.equalsIgnoreCase("lycosa")) {
			return "tarantula";
		}
		return species;
	}
}
