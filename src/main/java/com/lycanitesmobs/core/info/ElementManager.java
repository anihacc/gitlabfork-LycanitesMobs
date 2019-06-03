package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigBase;

import java.util.HashMap;
import java.util.Map;

/** Loads and manages all Element definitions. **/
public class ElementManager extends JSONLoader {
	public static ElementManager INSTANCE;

	/** A list of all elements. **/
	public Map<String, ElementInfo> elements = new HashMap<>();


	/** Returns the main Element Manager INSTANCE or creates it and returns it. **/
	public static ElementManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ElementManager();
		}
		return INSTANCE;
	}


	/** Called during early start up, loads all global configs into this manager. **/
	public void loadConfig() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
	}


	/** Loads all JSON Elements. Should only be done on pre-init and before Creature Info is loaded. **/
	public void loadAllFromJSON(ModInfo groupInfo) {
		this.elements.clear();
		this.loadAllJson(groupInfo, "Element", "elements", "name", false);
		for(ElementInfo elementInfo : this.elements.values()) {
			elementInfo.init();
		}
		LycanitesMobs.printDebug("Element", "Complete! " + this.elements.size() + " JSON Elements Loaded In Total.");
	}


	@Override
	public void parseJson(ModInfo groupInfo, String name, JsonObject json) {
		ElementInfo elementInfo = new ElementInfo();
		elementInfo.loadFromJSON(json);
		if(elementInfo.name == null) {
			LycanitesMobs.printWarning("", "Unable to load " + name + " json due to missing name.");
			return;
		}
		this.elements.put(elementInfo.name, elementInfo);
	}


	/**
	 * Gets an element by name.
	 * @param elementName The name of the element to get.
	 * @return The Element Info.
	 */
	public ElementInfo getElement(String elementName) {
		if(!this.elements.containsKey(elementName))
			return null;
		return this.elements.get(elementName);
	}
}
