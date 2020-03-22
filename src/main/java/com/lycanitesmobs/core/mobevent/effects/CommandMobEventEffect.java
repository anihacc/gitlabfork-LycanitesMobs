package com.lycanitesmobs.core.mobevent.effects;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandMobEventEffect extends MobEventEffect {
	/** The command to run. **/
	public String command = "";


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);

		if(json.has("command"))
			this.command = json.get("command").getAsString();
	}

	@Override
	public void onUpdate(World world, PlayerEntity player, BlockPos pos, int level, int ticks, int subspecies) {
		super.onUpdate(world, player, pos, level, ticks, subspecies);
		if(!this.canActivate(world, player, pos, level, ticks)) {
			return;
		}

		MinecraftServer server = world.getServer();
		if(server != null) {
			server.getCommandManager().handleCommand(null, this.command);
		}
	}
}
