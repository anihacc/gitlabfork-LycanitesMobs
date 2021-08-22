package com.lycanitesmobs.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lycanitesmobs.LycanitesMobs;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class VersionChecker {
	public static VersionChecker INSTANCE = new VersionChecker();
	public boolean enabled = true;

	VersionInfo currentVersion = new VersionInfo(LycanitesMobs.versionNumber, LycanitesMobs.versionMC);
	VersionInfo latestVersion = null;
	long lastChecked = -1;

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
			String[] versions = this.versionNumber.split("\\.");
			String[] compareVersions = compareVersion.versionNumber.split("\\.");
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
	}

	public VersionInfo getLatestVersion() {
		if (this.latestVersion != null) {
			return this.latestVersion;
		}

		long currentTime = System.currentTimeMillis() / 1000;
		if (this.lastChecked < 0 || currentTime - this.lastChecked > 60 * 60) {
			this.lastChecked = currentTime;
			VersionChecker.VersionLoader versionLoader = new VersionChecker.VersionLoader();
			Thread thread = new Thread(versionLoader);
			thread.start();
		}

		return this.latestVersion != null ? this.latestVersion : this.currentVersion;
	}

	public static class VersionLoader implements Runnable {
		@Override
		public void run() {
			VersionChecker.INSTANCE.lastChecked = System.currentTimeMillis() / 1000;

			try {
				URL url = new URL(LycanitesMobs.serviceAPI + "/versions?limit=1&sort_by=created_at:desc&mcversion=" + LycanitesMobs.versionMC);
				URLConnection urlConnection = url.openConnection();
				String osName = System.getProperty("os.name");
				urlConnection.setRequestProperty("Authorization", "Bearer 7ed1f44cbc1aff693e604075f23d56402983a4a0"); // This is a public api so the key is safe to be exposed like this. :)
				urlConnection.setRequestProperty("User-Agent", "Minecraft " + LycanitesMobs.versionMC + " (" + osName + ") LycanitesMobs " + LycanitesMobs.versionNumber);
				InputStream inputStream = urlConnection.getInputStream();
				String jsonString;
				try {
					jsonString = IOUtils.toString(inputStream, (Charset) null);
					jsonString = jsonString.replace("\\r", "");
				} finally {
					inputStream.close();
				}

				JsonParser jsonParser = new JsonParser();
				JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
				JsonArray jsonArray = json.getAsJsonArray("data");
				JsonObject versionJson = jsonArray.get(0).getAsJsonObject();
				if (!versionJson.has("version") && !versionJson.has("mcversion")) {
					return;
				}

				String versionNumber = versionJson.get("version").getAsString();
				String mcVersion = versionJson.get("mcversion").getAsString();
				VersionChecker.INSTANCE.latestVersion = new VersionInfo(versionNumber, mcVersion);
				VersionChecker.INSTANCE.latestVersion.loadFromJSON(versionJson);
				VersionChecker.INSTANCE.latestVersion.checkIfNewer(VersionChecker.INSTANCE.currentVersion);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
