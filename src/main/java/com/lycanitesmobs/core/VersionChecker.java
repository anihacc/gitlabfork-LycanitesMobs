package com.lycanitesmobs.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class VersionChecker {
	public static boolean enabled = true;

	static VersionInfo currentVersion = new VersionInfo(LycanitesMobs.versionNumber, LycanitesMobs.versionMC);
	static VersionInfo latestVersion = null;

	public static class VersionInfo {
		public String versionNumber;
		public String mcVersion;
		public String name = "";
		public String newFeatures = "";
		public String configChanges = "";
		public String majorFixes = "";
		public String changes = "";
		public String balancing = "";
		public String minorFixes = "";

		public boolean isNewer = false;

		/**
		 * Constructor
		 * @param versionNumber The version number. Ex: 1.20.5.1
		 * @param mcVersion The Minecraft version. Ex: 1.12.2
		 */
		public VersionInfo(String versionNumber, String mcVersion) {
			this.versionNumber = versionNumber;
			this.mcVersion = mcVersion;
		}

		public void loadFromJSON(JsonObject versionJson) {
			this.versionNumber = versionJson.get("version").getAsString();
			this.mcVersion = versionJson.get("mcversion").getAsString();
			this.name = versionJson.get("name").getAsString();
			this.newFeatures = versionJson.get("new").getAsString();
			this.configChanges = versionJson.get("config_changes").getAsString();
			this.majorFixes = versionJson.get("major_fixes").getAsString();
			this.changes = versionJson.get("changes").getAsString();
			this.balancing = versionJson.get("balancing").getAsString();
			this.minorFixes = versionJson.get("minor_fixes").getAsString();
		}

		/** Sets isNewer to true if this VersionInfo is newer than compareVersion. **/
		public void checkIfNewer(VersionInfo compareVersion) {
			this.isNewer = false;
			String[] versions = this.versionNumber.split("-")[0].split("\\.");
			String[] compareVersions = compareVersion.versionNumber.split("-")[0].split("\\.");
			for (int i = 0; i < 4; i++) {
				int versionNumber = NumberUtils.isCreatable(versions[i].replaceAll("[^\\d.]", "")) ? Integer.parseInt(versions[i].replaceAll("[^\\d.]", "")) : 0;
				int compareVersionNumber = NumberUtils.isCreatable(compareVersions[i].replaceAll("[^\\d.]", "")) ? Integer.parseInt(compareVersions[i].replaceAll("[^\\d.]", "")) : 0;
				if (versionNumber > compareVersionNumber) {
					this.isNewer = true;
					return;
				}
				if(versionNumber != compareVersionNumber) {
					return;
				}
			}
		}

		public String getUpdateNotes() {
			String content = "\u00A7l\u00A7n" + new TranslationTextComponent("gui.beastiary.index.changes").getString() + "\u00A7r";
			content += "\n\u00A7l" + new TranslationTextComponent("gui.beastiary.index.changes.name").getString() + ":\u00A7r " + this.name;
			if(this.newFeatures.length() > 0)
				content += "\n\n\u00A7l" + new TranslationTextComponent("gui.beastiary.index.changes.new").getString() + ":\u00A7r\n" + this.newFeatures;
			if(this.configChanges.length() > 0)
				content += "\n\n\u00A7l" + new TranslationTextComponent("gui.beastiary.index.changes.config").getString() + ":\u00A7r\n" + this.configChanges;
			if(this.majorFixes.length() > 0)
				content += "\n\n\u00A7l" + new TranslationTextComponent("gui.beastiary.index.changes.major").getString() + ":\u00A7r\n" + this.majorFixes;
			if(this.changes.length() > 0)
				content += "\n\n\u00A7l" + new TranslationTextComponent("gui.beastiary.index.changes.gameplay").getString() + ":\u00A7r\n" + this.changes;
			if(this.balancing.length() > 0)
				content += "\n\n\u00A7l" + new TranslationTextComponent("gui.beastiary.index.changes.balancing").getString() + ":\u00A7r\n" + this.balancing;
			if(this.minorFixes.length() > 0)
				content += "\n\n\u00A7l" + new TranslationTextComponent("gui.beastiary.index.changes.minor").getString() + ":\u00A7r\n" + this.minorFixes;

			return content;
		}

		public int getUpdateNotesHeight(int displayWidth) {
			return ClientManager.getInstance().getFontRenderer().getWordWrappedHeight(this.getUpdateNotes(), displayWidth) + 10;
		}
	}

	public static VersionInfo getLatestVersion(boolean refresh) {
		if(!refresh && latestVersion != null) {
			return latestVersion;
		}

		try {
			URL url = new URL(LycanitesMobs.websiteAPI + "/latest/" + LycanitesMobs.versionMC);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
			InputStream inputStream = urlConnection.getInputStream();
			String jsonString = null;
			try {
				jsonString = IOUtils.toString(inputStream, (Charset)null);
				jsonString = jsonString.replace("\\r", "");
			} catch (Exception e) {
				throw e;
			} finally {
				inputStream.close();
			}

			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse(jsonString);
			JsonObject versionJson = jsonElement.getAsJsonObject();
			if(!versionJson.has("version") && !versionJson.has("mcversion")) {
				return currentVersion;
			}
			String versionNumber = versionJson.get("version").getAsString();
			String mcVersion = versionJson.get("mcversion").getAsString();
			latestVersion = new VersionInfo(versionNumber, mcVersion);
			latestVersion.loadFromJSON(versionJson);
			latestVersion.checkIfNewer(currentVersion);
		}
		catch(Throwable e) {
			e.printStackTrace();
		}

		if(latestVersion == null) {
			return currentVersion;
		}
		return latestVersion;
	}
}
