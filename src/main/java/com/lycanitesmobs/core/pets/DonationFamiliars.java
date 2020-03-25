package com.lycanitesmobs.core.pets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

public class DonationFamiliars {
    public static DonationFamiliars instance = new DonationFamiliars();
    public Map<UUID, Map<UUID, PetEntry>> playerFamiliars = new HashMap<>();
    public long jsonLoadedTime = -1;
    public List<String> familiarBlacklist = new ArrayList<>();

    // ==================================================
    //                  Read From JSON
    // ==================================================
    public void readFromJSON() {
        this.jsonLoadedTime = System.currentTimeMillis() / 1000;

        // Load JSON File:
        String jsonString = null;
		try {
			URL familiarURL = new URL(LycanitesMobs.websiteAPI + "/familiar");
			URLConnection urlConnection = familiarURL.openConnection();
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
			InputStream inputStream = urlConnection.getInputStream();
			try {
				jsonString = IOUtils.toString(inputStream, (Charset) null);
			} catch (Exception e) {
				throw e;
			} finally {
				inputStream.close();
			}
			LycanitesMobs.logInfo("", "Online donations file read successfully.");
		} catch (Throwable e) {
			LycanitesMobs.logInfo("", "Unable to access the online donations file, using local copy instead, this might be out of date.");
			e.printStackTrace();
//            try {
//                jsonString = FileUtils.readFileToString(FileUtils.getFile(LycanitesMobs.proxy.getMinecraftDir() + "/assets/lycanitesmobs/familiars.json"));
//            } catch (IOException e1) {
//                LycanitesMobs.logWarning("Donations", "There was a problem reading the local copy of the donations file.");
//                e1.printStackTrace();
//                return;
//            }
			jsonString = this.getLocalJSON();
		}

        // Parse JSON File:
        if(!this.parseFamiliarJSON(jsonString)) { // Try and parse web JSON.
			this.parseFamiliarJSON(this.getLocalJSON()); // If it fails, use local copy.
		}
    }


    // ==================================================
    //                 Parse Familiar JSON
    // ==================================================
    // Parses JSON to Familiars, returns false if the JSON is invalid.
    public boolean parseFamiliarJSON(String jsonString) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(jsonString);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            Iterator<JsonElement> jsonIterator = jsonArray.iterator();
            while (jsonIterator.hasNext()) {
                try {
                    JsonObject familiarJson = jsonIterator.next().getAsJsonObject();
                    UUID minecraft_uuid = UUID.fromString(familiarJson.get("minecraft_uuid").getAsString());
                    String minecraft_username = familiarJson.get("minecraft_username").getAsString();
                    if (this.familiarBlacklist.contains(minecraft_username)) {
                        continue;
                    }
                    UUID familiar_uuid = UUID.fromString(familiarJson.get("familiar_uuid").getAsString());

                    String familiar_species = familiarJson.get("familiar_species").getAsString();
                    int familiar_subspecies = familiarJson.get("familiar_subspecies").getAsInt();
                    String familiar_name = familiarJson.get("familiar_name").getAsString();
                    String familiar_color = familiarJson.get("familiar_color").getAsString();
                    double familiar_size = 0;
                    if (familiarJson.has("familiar_size")) {
                        familiar_size = familiarJson.get("familiar_size").getAsDouble();
                    }

                    PetEntryFamiliar familiarEntry = new PetEntryFamiliar(familiar_uuid, null, familiar_species.toLowerCase());
                    familiarEntry.setEntitySubspeciesID(familiar_subspecies);
                    if (familiar_size <= 0) {
                        familiarEntry.setEntitySize(familiar_subspecies < 3 ? 0.6D : 0.3D);
                    } else {
                        familiarEntry.setEntitySize(familiar_size);
                    }

                    if (!"".equals(familiar_name))
                        familiarEntry.setEntityName(familiar_name);
                    familiarEntry.setColor(familiar_color);

                    // Add Pet Entries or Update Existing Entries:
                    if (!this.playerFamiliars.containsKey(minecraft_uuid))
                        this.playerFamiliars.put(minecraft_uuid, new HashMap<>());
                    if (!this.playerFamiliars.get(minecraft_uuid).containsKey(familiar_uuid))
                        this.playerFamiliars.get(minecraft_uuid).put(familiar_uuid, familiarEntry);
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
            return false;
        }

        return true;
    }


    // ==================================================
    //              Get Familiars For Player
    // ==================================================
    public Map<UUID, PetEntry> getFamiliarsForPlayer(EntityPlayer player) {
        long currentTime = System.currentTimeMillis() / 1000;
        if(this.jsonLoadedTime < 0 || currentTime - this.jsonLoadedTime > 60 * 60)
            this.readFromJSON();

		Map<UUID, PetEntry> playerFamiliarEntries = new HashMap<>();
        if(this.playerFamiliars.containsKey(player.getUniqueID())) {
			playerFamiliarEntries = this.playerFamiliars.get(player.getUniqueID());
            for(PetEntry familiarEntry : playerFamiliarEntries.values()) {
				if(familiarEntry.host == null) {
					familiarEntry.host = player;
				}
			}
		}
        return playerFamiliarEntries;
    }


    // ==================================================
    //                Local JSON Data
    // ==================================================
    private String getLocalJSON() {
        return "[{\"id\":1,\"donation_username\":\"Lycanite\",\"minecraft_uuid\":\"b1829e52-769d-4296-9733-39654bb0449d\",\"minecraft_username\":\"\",\"familiar_species\":\"Argus\",\"familiar_subspecies\":0,\"familiar_size\":0,\"familiar_name\":\"Jasper\",\"familiar_color\":\"009955\",\"created_at\":\"2016-05-07 18:15:07\",\"updated_at\":\"2018-02-03 19:26:57\"}]";
    }
}
