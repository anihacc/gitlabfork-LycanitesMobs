package com.lycanitesmobs.core.mobevent;

import com.google.gson.*;
import com.lycanitesmobs.ClientManager;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.FileLoader;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.StreamLoader;
import com.lycanitesmobs.core.config.ConfigMobEvent;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.*;


public class MobEventManager extends JSONLoader {
	// Global:
	protected static MobEventManager INSTANCE;
    
    // Mob Events:
    public Map<String, MobEvent> mobEvents = new HashMap<>();
	public List<MobEventSchedule> mobEventSchedules = new ArrayList<>();

    // Properties:
    public boolean mobEventsEnabled = true;
    public boolean mobEventsRandom = false;
	/** The default temporary time applied to mobs spawned from events, where it will forcefully despawn after the specified time (in ticks). MobSpawns can override this. **/
	public int defaultMobDuration = 12000;
	public int minEventsRandomDay = 0;
	public int minTicksUntilEvent = 60 * 60 * 20;
	public int maxTicksUntilEvent = 120 * 60 * 20;


	/** Returns the main Mob Event Manager instance. **/
	public static MobEventManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new MobEventManager();
		}
		return INSTANCE;
	}


    /** Called during early start up, loads all global event configs into the manager. **/
	public void loadConfig() {
		LycanitesMobs.logDebug("", "Config Test: " + ConfigMobEvent.INSTANCE.mobEventsEnabled.get());
        this.mobEventsEnabled = ConfigMobEvent.INSTANCE.mobEventsEnabled.get();
		this.mobEventsRandom = ConfigMobEvent.INSTANCE.mobEventsRandom.get();
		this.defaultMobDuration = ConfigMobEvent.INSTANCE.defaultMobDuration.get();
		this.minEventsRandomDay = ConfigMobEvent.INSTANCE.minEventsRandomDay.get();
		this.minTicksUntilEvent = ConfigMobEvent.INSTANCE.minTicksUntilEvent.get();
		this.maxTicksUntilEvent = ConfigMobEvent.INSTANCE.maxTicksUntilEvent.get();
	}


	/** Loads all JSON Creature Types. Should be done before creatures are loaded so that they can find their type on load. **/
	public void loadAllFromJSON(ModInfo groupInfo) {
		try {
			this.loadAllJson(groupInfo, "Mob Event", "mobevents", "name", true, "event", FileLoader.COMMON, StreamLoader.COMMON);
			LycanitesMobs.logDebug("MobEvents", "Complete! " + this.mobEvents.size() + " JSON Mob Events Loaded In Total.");
		}
		catch(Exception e) {}

		// Load Scheduled Events:
		Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
		String configPath = new File(".") + "/config/" + LycanitesMobs.MODID + "/";
		this.mobEventSchedules.clear();

		JsonObject defaultScheduleJson;
		if(FileLoader.SERVER.ready) {
			Path defaultSchedulePath = FileLoader.SERVER.getPath("mobeventschedule.json");
			defaultScheduleJson = this.loadJsonObject(gson, defaultSchedulePath);
		}
		else {
			defaultScheduleJson = this.loadJsonObject(gson, StreamLoader.SERVER.getStream("mobeventschedule.json"));
		}

		File customScheduleFile = new File(configPath + "mobeventschedule.json");
		JsonObject customScheduleJson = null;
		if(customScheduleFile.exists()) {
			customScheduleJson = this.loadJsonObject(gson, customScheduleFile.toPath());
		}

		JsonObject scheduleJson = this.writeDefaultJSONObject(gson, "mobeventschedule", defaultScheduleJson, customScheduleJson);
		if(scheduleJson.has("schedules")) {
			JsonArray jsonArray = scheduleJson.get("schedules").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject scheduleEntryJson = jsonIterator.next().getAsJsonObject();
				MobEventSchedule mobEventSchedule = MobEventSchedule.createFromJSON(scheduleEntryJson);
				this.mobEventSchedules.add(mobEventSchedule);
			}
		}
		if(this.mobEventSchedules.size() > 0) {
			LycanitesMobs.logDebug("MobEvents", "Loaded " + this.mobEventSchedules.size() + " Mob Event Schedules.");
		}
	}

	@Override
	public void parseJson(ModInfo modInfo, String loadGroup, JsonObject json) {
		LycanitesMobs.logDebug("MobEvents", "Loading Mob Event JSON: " + json);
		MobEvent mobEvent = new MobEvent();
		mobEvent.loadFromJSON(json);
		this.addMobEvent(mobEvent);
	}


	/** Reloads all JSON Mob Events. **/
	public void reload() {
		LycanitesMobs.logDebug("MobEvents", "Destroying JSON Mob Events!");
		for(MobEvent mobEvent : this.mobEvents.values().toArray(new MobEvent[this.mobEvents.size()])) {
			mobEvent.destroy();
		}

		this.loadAllFromJSON(LycanitesMobs.modInfo);
	}


    /**
     * Adds the provided Mob Event.
	 * @param mobEvent The Mob Event instance to add.
    **/
    public void addMobEvent(MobEvent mobEvent) {
        if(mobEvent == null)
            return;
        this.mobEvents.put(mobEvent.name, mobEvent);
        try {
			ObjectManager.addSound("mobevent_" + mobEvent.title.toLowerCase(), LycanitesMobs.modInfo, "mobevent." + mobEvent.title.toLowerCase());
		}
		catch(Exception e) {}
    }


	/** Removes a Mob Event from this Manager. **/
	public void removeMobEvent(MobEvent mobEvent) {
		if(!this.mobEvents.containsKey(mobEvent.name)) {
			LycanitesMobs.logWarning("", "[MobEvents] Tried to remove a Mob Event that hasn't been added: " + mobEvent.name);
			return;
		}
		this.mobEvents.remove(mobEvent.name);
	}


	/**
	 * Gets a Mob Event by name.
	 * @return Null if the event does not exist.
	 **/
	public MobEvent getMobEvent(String mobEventName) {
		if(!this.mobEvents.containsKey(mobEventName)) {
			return null;
		}
		return this.mobEvents.get(mobEventName);
	}


	/** Called every tick in a world and updates any active Server Side Mob Event players. **/
	@SubscribeEvent
	public void onWorldUpdate(WorldTickEvent event) {
		World world = event.world;
		if(world.isRemote)
			return;
		ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		if(worldExt == null) {
			return;
		}

		// Only Tick On World Time Ticks:
		if(worldExt.lastEventUpdateTime == world.getGameTime()) {
			return;
		}
		worldExt.lastEventUpdateTime = world.getGameTime();

		// Only Run If Players Are Present:
		if(world.getPlayers().size() < 1) {
			return;
		}

		// Update World Mob Event Player:
		if(worldExt.serverWorldEventPlayer != null) {
			worldExt.serverWorldEventPlayer.onUpdate();
		}

        // Update Mob Event Players:
        if(worldExt.serverMobEventPlayers.size() > 0) {
            for (MobEventPlayerServer mobEventPlayerServer : worldExt.serverMobEventPlayers.values().toArray(new MobEventPlayerServer[worldExt.serverMobEventPlayers.size()])) {
				mobEventPlayerServer.onUpdate();
            }
        }
    }


	/** Updates the client side mob event players if active in the player's current world. **/
	@SubscribeEvent
	public void onClientUpdate(ClientTickEvent event) {
		if(ClientManager.getInstance().getClientPlayer() == null)
			return;

        World world = ClientManager.getInstance().getClientPlayer().getEntityWorld();
		if(!world.isRemote)
			return;
        ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
        if(worldExt == null)
        	return;

		// Update Mob Event Players:
        for(MobEventPlayerClient mobEventPlayerClient : worldExt.clientMobEventPlayers.values()) {
            mobEventPlayerClient.onUpdate();
        }

		// Update World Mob Event Player:
		if(worldExt.clientWorldEventPlayer != null) {
			worldExt.clientWorldEventPlayer.onUpdate();
		}
	}
}
