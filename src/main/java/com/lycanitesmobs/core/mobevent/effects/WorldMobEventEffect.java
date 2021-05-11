package com.lycanitesmobs.core.mobevent.effects;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class WorldMobEventEffect extends MobEventEffect {

	/** Controls the rain. Can be: none, start or stop. **/
	protected String rain = "none";

	/** Controls the thundering. Can be: none, start or stop. **/
	protected String thunder = "none";

	/** If set, changes the time of day. 20000 is good for night time events. This only moves the day time forwards and not backwards. **/
	protected int dayTime = -1;


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("rain"))
			this.rain = json.get("rain").getAsString();

		if(json.has("thunder"))
			this.thunder = json.get("thunder").getAsString();

		if(json.has("dayTime"))
			this.dayTime = json.get("dayTime").getAsInt();

		super.loadFromJSON(json);
	}


	@Override
	public void onUpdate(World world, PlayerEntity player, BlockPos pos, int level, int ticks, int variant) {
		if(ticks == 0) {
			// Rain:
			if ("start".equalsIgnoreCase(this.rain)) {
				world.getWorldInfo().setRaining(true);
			} else if ("stop".equalsIgnoreCase(this.rain)) {
				world.getWorldInfo().setRaining(false);
			}

			// Lightning:
			if ("start".equalsIgnoreCase(this.thunder)) {
				world.setThunderStrength(1.0F);
			} else if ("stop".equalsIgnoreCase(this.thunder)) {
				world.setThunderStrength(1.0F);
			}

			// Day Time:
			if (this.dayTime >= 0) {
				int dayTime = 23999;
				long currentTime = world.getDayTime();
				int targetTime = this.dayTime;
				long excessTime = currentTime % dayTime;
				if (excessTime > targetTime) {
					targetTime += dayTime;
				}
				for (ServerWorld serverWorld : world.getServer().getWorlds()) {
					serverWorld.setDayTime(currentTime - excessTime + targetTime);
				}
			}
		}
	}
}
