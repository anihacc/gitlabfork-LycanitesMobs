package com.lycanitesmobs.core.command;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.info.Beastiary;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import com.lycanitesmobs.core.mobevent.MobEvent;
import com.lycanitesmobs.core.mobevent.MobEventListener;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import com.lycanitesmobs.client.localisation.LanguageManager;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandMain implements ICommand {
	
	private List aliases;
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public CommandMain() {
		this.aliases = new ArrayList();
		this.aliases.add("lm");
		this.aliases.add("lycan");
		this.aliases.add("lycmobs");
		this.aliases.add("lycanmobs");
		this.aliases.add("lycanitesmobs");
	}
	
	
	// ==================================================
	//                   Command Info
	// ==================================================
	@Override
	public String getName() {
		return "lycanitesmobs";
	}

	@Override
	public String getUsage(ICommandSender commandSender) {
		if(commandSender instanceof EntityPlayer)
			return "/lycanitesmobs <sub-commands: mobevent [start <event name>, stop, list, enable, disable]>";
		return "/lycanitesmobs <sub-commands: mobevent [start <event name> dimensionID, stop, list, enable, disable]>";
	}

	@Override
	public List getAliases() {
		return this.aliases;
	}

	@Override
	public List getTabCompletions(MinecraftServer server, ICommandSender commandSender, String[] args, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}

    @Override
    public int compareTo(ICommand p_compareTo_1_) {
        return this.getName().compareTo(p_compareTo_1_.getName());
    }
	
	
	// ==================================================
	//                      Process
	// ==================================================
	@Override
	public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) {
		String reply = LanguageManager.translate("lyc.command.invalid");
		if(args.length < 1) {
			commandSender.sendMessage(new TextComponentString(reply));
			commandSender.sendMessage(new TextComponentString(this.getUsage(commandSender)));
			return;
		}

		// Debug:
		if("debug".equalsIgnoreCase(args[0])) {
			reply = LanguageManager.translate("lyc.command.debug.invalid");
			if (args.length < 3) {
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			String debugValue = args[1];
			reply = LanguageManager.translate("lyc.command.debug.set");
			reply = reply.replace("%debug%", debugValue);
			LycanitesMobs.config.setBool("Debug", debugValue, "true".equalsIgnoreCase(args[2]));
			commandSender.sendMessage(new TextComponentString(reply));
			return;
		}

		// Spawner:
		if("spawners".equalsIgnoreCase(args[0])) {
			reply = LanguageManager.translate("lyc.command.spawners.invalid");
			if (args.length < 2) {
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Reload:
			if("reload".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.spawners.reload");
				SpawnerManager.getInstance().reload();
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Creative Test:
			if("creative".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.spawners.creative");
				SpawnerEventListener.testOnCreative = !SpawnerEventListener.testOnCreative;
				reply = reply.replace("%value%", "" + SpawnerEventListener.testOnCreative);
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}
		}
		if("spawner".equalsIgnoreCase(args[0])) {
			reply = LanguageManager.translate("lyc.command.spawners.invalid");
			if (args.length < 2) {
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Add:
			if("reload".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.spawners.reload");
				SpawnerManager.getInstance().reload();
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}
		}

		// Dungeon:
		if("dungeon".equalsIgnoreCase(args[0]) || "dungeons".equalsIgnoreCase(args[0])) {
			reply = LanguageManager.translate("lyc.command.dungeon.invalid");
			if (args.length < 2) {
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Reload:
			if("reload".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.dungeon.reload");
				DungeonManager.getInstance().reload();
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Enable:
			if("enable".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.dungeon.enable");
				ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
				config.setBool("Dungeons", "Dungeons Enabled", true);
				LycanitesMobs.dungeonGenerator.enabled = true;
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Disable:
			if("disable".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.dungeon.disable");
				ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
				config.setBool("Dungeons", "Dungeons Enabled", false);
				LycanitesMobs.dungeonGenerator.enabled = false;
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}
		}

		// Spawner:
		if("creature".equalsIgnoreCase(args[0]) || "creatures".equalsIgnoreCase(args[0])) {
			reply = LanguageManager.translate("lyc.command.creatures.invalid");
			if (args.length < 2) {
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Reload:
			if("reload".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.creatures.reload");
				CreatureManager.getInstance().reload();
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}
		}

		// Equipment:
		if("equipment".equalsIgnoreCase(args[0])) {
			reply = LanguageManager.translate("lyc.command.equipment.invalid");
			if (args.length < 2) {
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Reload:
			if("reload".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.equipment.reload");
				EquipmentPartManager.getInstance().reload();
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}
		}

		// Beastiary:
		if("beastiary".equalsIgnoreCase(args[0])) {
			reply = LanguageManager.translate("lyc.command.beastiary.invalid");
			if (args.length < 2) {
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Player Only:
			if(!(commandSender instanceof EntityPlayer)) {
				reply = LanguageManager.translate("lyc.command.playeronly");
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}
			EntityPlayer player = (EntityPlayer)commandSender;
			ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
			Beastiary beastiary = extendedPlayer.getBeastiary();
			if(extendedPlayer == null || beastiary == null) {
				return;
			}

			// Add:
			if("add".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.beastiary.add.invalid");
				if (args.length < 3) {
					commandSender.sendMessage(new TextComponentString(reply));
					return;
				}

				int rank = 3;
				if(args.length >= 4) {
					rank = NumberUtils.isCreatable(args[3]) ? Integer.parseInt(args[3]) : 3;
				}

				String creatureName = args[2].toLowerCase();
				CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(creatureName);
				if(creatureInfo == null) {
					reply = LanguageManager.translate("lyc.command.beastiary.add.unknown");
					commandSender.sendMessage(new TextComponentString(reply));
					return;
				}

				CreatureKnowledge creatureKnowledge = new CreatureKnowledge(beastiary, creatureInfo.getName(), rank);
				if(beastiary.addCreatureKnowledge(creatureKnowledge) > 0) {
					beastiary.sendAddedMessage(creatureKnowledge);
					beastiary.sendToClient(creatureKnowledge);
				}
				else {
					beastiary.sendKnownMessage(creatureKnowledge);
				}
				return;
			}

			// Complete:
			if("complete".equalsIgnoreCase(args[1])) {
				int rank = 3;
				if(args.length >= 3) {
					rank = NumberUtils.isCreatable(args[2]) ? Integer.parseInt(args[2]) : 3;
				}

				for(CreatureInfo creatureInfo : CreatureManager.getInstance().creatures.values()) {
					beastiary.addCreatureKnowledge(new CreatureKnowledge(beastiary, creatureInfo.getName(), rank));
				}
				beastiary.sendAllToClient();
				reply = LanguageManager.translate("lyc.command.beastiary.complete");
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Clear:
			if("clear".equalsIgnoreCase(args[1])) {
				beastiary.creatureKnowledgeList.clear();
				beastiary.sendAllToClient();
				reply = LanguageManager.translate("lyc.command.beastiary.clear");
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Packet:
			if("packet".equalsIgnoreCase(args[1])) {
				beastiary.sendAllToClient();
				commandSender.sendMessage(new TextComponentString("Force sent a full Beastiary update packet."));
				return;
			}
		}
		
		// Mob Event:
		if("mobevent".equalsIgnoreCase(args[0])) {
			reply = LanguageManager.translate("lyc.command.mobevent.invalid");
			if(args.length < 2) {
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Reload:
			if("reload".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.mobevent.reload");
				MobEventManager.getInstance().reload();
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

			// Creative Test:
			if("creative".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.mobevent.creative");
				MobEventPlayerServer.testOnCreative = !MobEventPlayerServer.testOnCreative;
				reply = reply.replace("%value%", "" + MobEventPlayerServer.testOnCreative);
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}
			
			// Start:
			if("start".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.mobevent.start.invalid");
				if(args.length < 3) {
					commandSender.sendMessage(new TextComponentString(reply));
					return;
				}
				
				String mobEventName = args[2].toLowerCase();
				if(MobEventManager.getInstance().mobEvents.containsKey(mobEventName)) {
					
					// Get World:
					World world = null;
                    if(args.length >= 4 && NumberUtils.isNumber(args[3])) {
                        world = DimensionManager.getWorld(Integer.parseInt(args[3]));
                    }
					else {
						world = commandSender.getEntityWorld();
					}
					
					// No World:
					if(world == null) {
						reply = LanguageManager.translate("lyc.command.mobevent.start.noworld");
						commandSender.sendMessage(new TextComponentString(reply));
						return;
					}

                    ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
					
					// Force Enabled:
					if(!MobEventManager.getInstance().mobEventsEnabled) {
						reply = LanguageManager.translate("lyc.command.mobevent.enable");
						commandSender.sendMessage(new TextComponentString(reply));
						MobEventManager.getInstance().mobEventsEnabled = true;
						ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "mobevents");
						config.setBool("Global", "Mob Events Enabled", true);
					}
					
					reply = LanguageManager.translate("lyc.command.mobevent.start");
					commandSender.sendMessage(new TextComponentString(reply));
					EntityPlayer player = null;
					BlockPos pos = new BlockPos(0, 0, 0);
					if(commandSender instanceof EntityPlayer) {
						player = (EntityPlayer)commandSender;
						pos = player.getPosition();
					}
					int level = 1;
					if(args.length >= 5 && NumberUtils.isNumber(args[4])) {
						level = Integer.parseInt(args[4]);
					}
                    worldExt.startMobEvent(mobEventName, player, pos, level);
					return;
				}
				
				reply = LanguageManager.translate("lyc.command.mobevent.start.unknown");
				commandSender.sendMessage(new TextComponentString(reply));
				return;
			}

            // Get World:
            World world;
            if(args.length >= 3 && NumberUtils.isNumber(args[2])) {
                world = DimensionManager.getWorld(Integer.parseInt(args[2]));
            }
            else {
                world = commandSender.getEntityWorld();
            }

            // No World:
            if(world == null) {
                reply = LanguageManager.translate("lyc.command.mobevent.start.noworld");
                commandSender.sendMessage(new TextComponentString(reply));
                return;
            }

            LycanitesMobs.logDebug("", "Getting Extended World for Dimension: " + world.provider.getDimension() + " World: " + world);
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
            LycanitesMobs.logDebug("", "Got Extended World for Dimension: " + worldExt.world.provider.getDimension() + " World: " + worldExt.world);
            if(worldExt == null) return;
			
			// Random:
			if("random".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.mobevent.random");
				commandSender.sendMessage(new TextComponentString(reply));
				worldExt.stopWorldEvent();
				MobEventListener.getInstance().triggerRandomMobEvent(world, worldExt);
				return;
			}
			
			// Stop:
			if("stop".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.mobevent.stop");
				commandSender.sendMessage(new TextComponentString(reply));
                worldExt.stopWorldEvent();
				return;
			}
			
			// List:
			if("list".equalsIgnoreCase(args[1])) {
				reply = LanguageManager.translate("lyc.command.mobevent.list");
				commandSender.sendMessage(new TextComponentString(reply));
				for(MobEvent mobEvent : MobEventManager.getInstance().mobEvents.values()) {
					String eventName = mobEvent.name + " (" + mobEvent.getTitle() + ")";
					commandSender.sendMessage(new TextComponentString(eventName));
				}
				return;
			}
			
			// Enable:
			if("enable".equalsIgnoreCase(args[1])) {
				if(args.length >= 3) {
					if("random".equalsIgnoreCase(args[2])) {
						reply = LanguageManager.translate("lyc.command.mobevent.enable.random");
						commandSender.sendMessage(new TextComponentString(reply));
						MobEventManager.getInstance().mobEventsRandom = true;
						ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "mobevents");
						config.setBool("Global", "Random Mob Events", true);
						return;
					}
				}
				reply = LanguageManager.translate("lyc.command.mobevent.enable");
				commandSender.sendMessage(new TextComponentString(reply));
				MobEventManager.getInstance().mobEventsEnabled = true;
				ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "mobevents");
				config.setBool("Global", "Mob Events Enabled", true);
				return;
			}
			
			// Disable:
			if("disable".equalsIgnoreCase(args[1])) {
				if(args.length >= 3) {
					if("random".equalsIgnoreCase(args[2])) {
						reply = LanguageManager.translate("lyc.command.mobevent.disable.random");
						commandSender.sendMessage(new TextComponentString(reply));
						MobEventManager.getInstance().mobEventsRandom = false;
						ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "mobevents");
						config.setBool("Global", "Random Mob Events", false);
						return;
					}
				}
				reply = LanguageManager.translate("lyc.command.mobevent.disable");
				commandSender.sendMessage(new TextComponentString(reply));
				MobEventManager.getInstance().mobEventsEnabled = false;
				ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "mobevents");
				config.setBool("Global", "Mob Events Enabled", false);
				return;
			}
		}
		
		commandSender.sendMessage(new TextComponentString(reply));
		commandSender.sendMessage(new TextComponentString(this.getUsage(commandSender)));
	}
	
	
	// ==================================================
	//                     Permission
	// ==================================================
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender commandSender) {
		if(commandSender instanceof EntityPlayer) {
			if(!commandSender.canUseCommand(this.getRequiredPermissionLevel(), this.getName()))
				return false;
		}
		return true;
	}
}
