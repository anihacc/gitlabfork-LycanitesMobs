package com.lycanitesmobs.core.info;

import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;

import java.util.ArrayList;
import java.util.List;

/** Loads global creature configs. **/
public class CreatureConfig {

	// Client:
	/** If true, all mobs that are a subspecies will always show their nametag. **/
	public boolean subspeciesTags = true;

	/** The minimum interval in ticks between random idle sounds. **/
	public int idleSoundTicks = 100;

	/** If true, alpha is disabled on mob textures, this can make them look undesirable but can increase performance on low end systems. **/
	public boolean disableModelAlpha = false;

	/** If true, block particles are not spawned by mobs (useful for visual mods that create 3D block particles which can cause lag in high numbers). **/
	public boolean disableBlockParticles = false;

	// Stats:
	/** The minimum base starting level of every mob. Cannot be less than 1. **/
	public int startingLevelMin = 1;

	/** The maximum base starting level of every mob. Ignored when not greater than the min level. **/
	public int startingLevelMax = 5;

	/** Increases the base start level by this amount of every world day that has gone by, use this to slowly level up mobs as the world gets older. Fractions can be used such as 0.05 levels per day. The levels are rounded down so +0.9 would be +0 levels. **/
	public double levelPerDay = 0;

	/** The maximum level to be able gain from levels per day. **/
	public int levelPerDayMax = 100;

	/** How many levels a mob gains multiplied by the local area difficulty level. Staying in an area for a while slowly increases the difficulty of that area ranging from 0.00 to 6.75. So 1.5 means level 10 at full local area difficulty. **/
	public double levelPerLocalDifficulty = 1.5;


	// Pets:
	/** If true, the name of a pet's owner will be shown in it's name tag. **/
	public boolean ownerTags = true;

	/** Whether mob taming is allowed. **/
	public boolean tamingEnabled = true;

	/** Whether mob mounting is allowed. **/
	public boolean mountingEnabled = true;

	/** Whether mob mounting for flying mobs is allowed. **/
	public boolean mountingFlightEnabled = true;

	/** If true, tamed mobs wont harm their owners. **/
	public boolean friendlyFire = true;

	/** The time in tics that it takes for a pet to respawn. **/
	public int petRespawnTime = 3 * 60 * 20;

	/** How far in blocks pets stray from their owner when set to follow. **/
	public int petFollowDistance = 8;


	// Beastiary:
	/** If true, a chat message will be displayed when gaining Beastiary Knowledge. **/
	public boolean beastiaryKnowledgeMessages = true;

	/** How much knowledge experience standing near a creature gives per second. **/
	public int creatureProximityKnowledge = 1;

	/** How much knowledge experience killing a creature gives. **/
	public int creatureKillKnowledge = 50;

	/** How much knowledge experience feeding a treat to a creature gives. **/
	public int creatureTreatKnowledge = 100;

	/** How much knowledge experience studying (using a soulgazer on) a creature gives. **/
	public int creatureStudyKnowledge = 25;

	/** The time in ticks it takes to be able to use a Soulgazer for knowledge again. Default is 200 (10 seconds).. **/
	public int creatureStudyCooldown = 200;

	/** The knowledge experience scale for variant creatures. **/
	public double creatureVariantKnowledgeScale = 2;

	/** The knowledge experience scale for boss creatures. **/
	public double creatureBossKnowledgeScale = 5;


	// Bosses:
	/** How much higher players must be relative to a boss' y position (feet) to trigger anti flight measures. **/
	public double bossAntiFlight = 10;

	/** Caps how much damage a boss can take per tick, this also affects Rare Variants and Dungeon Bosses. Set to 0 to disable the cap. **/
	public int bossDamageCap = 50;


	// Interaction:
	/** If true, predators such as Ventoraptors will attack farm animals such as Sheep or Makas. **/
	public boolean predatorsAttackAnimals = true;

	/** If true, all mobs that attack players will also attack villagers. **/
	public boolean mobsAttackVillagers = true;

	/** If true, passive mobs will fight back when hit instead of running away. **/
	public boolean animalsFightBack = false;

	/** If true, some elemental mobs will fuse with each other on sight into a stronger different elemental. **/
	public boolean elementalFusion = true;

	/** Controls how fused mobs combine their levels. Can be 'both' (default) where both levels are added together, 'highest' where the higher level is used and 'lowest' where the lowest level is used. **/
	public String elementalFusionLevelMix = "both";

	/** The level of a mob created via fusion is multiplied by this value, set to 1 for no changes. Tamed fusions aren't multiplied by this value. **/
	public double elementalFusionLevelMultiplier = 10;

	/** If true, when a mob picks up a player, the player will be positioned where the mob is rather than offset to where the mob is holding the player at. **/
	public boolean disablePickupOffsets = false;

	/** If true, all mobs will be immune to suffocation damage. **/
	public boolean suffocationImmunity = false;

	/** If true, all mobs will be immune to damage from running out of air. **/
	public boolean drownImmunity = false;

	/** If true, mobs can be lured with treats even if they are in a pack. **/
	public boolean packTreatLuring = false;


	// Variations:
	/** If true, mobs will have a chance of becoming a subspecies when spawned. **/
	public boolean variantSpawn = true;

	/** If true, mobs will vary in sizes when spawned. **/
	public boolean randomSizes = true;

	/** The minimum size scale mobs can randomly spawn at. **/
	public double randomSizeMin = 0.85D;

	/** The maximum size scale mobs can randomly spawn at. **/
	public double randomSizeMax = 1.15D;


	// Drops:
	/** A string of global drops to add to every mob. **/
	public String globalDropsString = "";

	/** A list of global drops to add to every mob. **/
	protected List<ItemDrop> globalDrops;


	/**
	 * Loads settings from the config.
	 * @param config The config to load from.
	 */
	public void loadConfig(ConfigBase config) {
		// Client:
		config.setCategoryComment("Client", "Mostly client side settings that affect visuals or sounds such as mob names or inventory tabs, etc.");
		this.subspeciesTags = config.getBool("Client", "Subspecies Tags", this.subspeciesTags, "If true, all mobs that are a subspecies will always show their nametag.");
		this.idleSoundTicks = config.getInt("Client", "Idle Sound Ticks", this.idleSoundTicks, "The minimum interval in ticks between random idle sounds.");
		this.disableModelAlpha = config.getBool("Client", "Disable Model Alpha", this.disableModelAlpha, "If true, alpha is disabled on mob textures, this can make them look undesirable but can increase performance on low end systems.");
		this.disableBlockParticles = config.getBool("Client", "Disable Block Particles", this.disableBlockParticles, "If true, block particles are not spawned by mobs (useful for visual mods that create 3D block particles which can cause lag in high numbers).");

		// Stats:
		config.setCategoryComment("Base Starting Level", "The base starting level is the level every mob will start at. Mob Events, Special Spawners and other things will then add onto this base level.");
		this.startingLevelMin = config.getInt("Base Starting Level", "Starting Level Min", this.startingLevelMin, "The minimum base starting level of every mob. Cannot be less than 1.");
		this.startingLevelMax = config.getInt("Base Starting Level", "Starting Level Max", this.startingLevelMax, "The maximum base starting level of every mob. Ignored when not greater than the min level.");
		this.levelPerDay = config.getDouble("Base Starting Level", "Level Gain Per Day", this.levelPerDay, "Increases the base start level by this amount of every world day that has gone by, use this to slowly level up mobs as the world gets older. Fractions can be used such as 0.05 levels per day. The levels are rounded down so +0.9 would be +0 levels.");
		this.levelPerDayMax = config.getInt("Base Starting Level", "Level Gain Per Day Max", this.levelPerDayMax, "The maximum level to be able gain from levels per day.");
		this.levelPerLocalDifficulty = config.getDouble("Base Starting Level", "Level Gain Per Local Difficulty", this.levelPerLocalDifficulty, "How many levels a mob gains multiplied by the local area difficulty level. Staying in an area for a while slowly increases the difficulty of that area ranging from 0.00 to 6.75. So 1.5 means level 10 at full local area difficulty.");

		// Pets:
		config.setCategoryComment("Pets", "Here you can control all settings related to taming and mounting.");
		this.ownerTags = config.getBool("Pets", "Owner Tags", this.ownerTags, "If true, tamed mobs will display their owner's name in their name tag.");
		this.tamingEnabled = config.getBool("Pets", "Taming", this.tamingEnabled, "Set to false to disable pet/mount taming.");
		this.mountingEnabled = config.getBool("Pets", "Mounting", this.mountingEnabled, "Set to false to disable mounts.");
		this.mountingFlightEnabled = config.getBool("Pets", "Flying Mounting", this.mountingFlightEnabled, "Set to false to disable flying mounts, if all mounts are disable this option doesn't matter.");
		this.friendlyFire = config.getBool("Pets", "Friendly Fire", this.friendlyFire, "If true, pets, minions, etc can't harm their owners (with ranged attacks, etc).");
		this.petRespawnTime = config.getInt("Pets", "Respawn Time", this.petRespawnTime, "The time in tics that it takes for a pet to respawn.");
		this.petFollowDistance = config.getInt("Pets", "Follow Distance", this.petFollowDistance, "How far in blocks pets stray from their owner when set to follow.");

		// Beastiary:
		config.setCategoryComment("Beastiary", "Here you can control all settings related to the player's Beastiary.");
		this.beastiaryKnowledgeMessages = config.getBool("Beastiary", "Beastiary Knowledge Messages", this.beastiaryKnowledgeMessages, "If true, a chat message will be displayed when gaining Beastiary Knowledge.");
		this.creatureProximityKnowledge = config.getInt("Beastiary", "Creature Proximity Knowledge", this.creatureProximityKnowledge, "How much knowledge experience standing near a creature gives per second.");
		this.creatureKillKnowledge = config.getInt("Beastiary", "Creature Kill Knowledge", this.creatureKillKnowledge, "How much knowledge experience killing a creature gives.");
		this.creatureTreatKnowledge = config.getInt("Beastiary", "Creature Treat Knowledge", this.creatureTreatKnowledge, "How much knowledge experience feeding a treat to a creature gives.");
		this.creatureStudyKnowledge = config.getInt("Beastiary", "Creature Study Knowledge", this.creatureStudyKnowledge, "How much knowledge experience studying (using a Soulgazer on) a creature gives.");
		this.creatureStudyCooldown = config.getInt("Beastiary", "Creature Study Cooldown", this.creatureStudyCooldown, "The time in ticks it takes to be able to use a Soulgazer for knowledge again. Default is 200 (10 seconds).");
		this.creatureVariantKnowledgeScale = config.getDouble("Beastiary", "Creature Variant Knowledge Scale", this.creatureVariantKnowledgeScale, "The knowledge experience scale for variant creatures.");
		this.creatureBossKnowledgeScale = config.getDouble("Beastiary", "Creature Boss Knowledge Scale", this.creatureBossKnowledgeScale, "The knowledge experience scale for boss creatures.");

		// Bosses:
		config.setCategoryComment("Bosses", "Here you can control all settings related to boss creatures, this does not include rare subspecies (mini bosses).");
		this.bossAntiFlight = config.getDouble("Bosses", "How much higher players must be relative to a boss' y position (feet) to trigger anti flight measures.", this.bossAntiFlight);
		this.bossDamageCap = config.getInt("Bosses", "Boss Damage Cap", this.bossDamageCap, "Caps how much damage a boss can take per tick, this also affects Rare Variants and Dungeon Bosses. Set to 0 to disable the cap.");
		BaseCreatureEntity.BOSS_DAMAGE_LIMIT = this.bossDamageCap;

		// Interaction:
		config.setCategoryComment("Mob Interaction", "Here you can control how mobs interact with other mobs.");
		this.predatorsAttackAnimals = config.getBool("Mob Interaction", "Predators Attack Animals", this.predatorsAttackAnimals, "Set to false to prevent predator mobs from attacking animals/farmable mobs.");
		this.mobsAttackVillagers = config.getBool("Mob Interaction", "Mobs Attack Villagers", this.mobsAttackVillagers, "Set to false to prevent mobs that attack players from also attacking villagers.");
		this.animalsFightBack = config.getBool("Mob Interaction", "Animals Fight Back", this.animalsFightBack, "If true, passive mobs will fight back when hit instead of running away.");
		this.elementalFusion = config.getBool("Mob Interaction", "Elemental Fusion", this.elementalFusion, "If true, some elemental mobs will fuse with each other on sight into a stronger different elemental.");
		this.elementalFusionLevelMix = config.getString("Mob Interaction", "Elemental Fusion", this.elementalFusionLevelMix, "Controls how fused mobs combine their levels. Can be 'both' (default) where both levels are added together, 'highest' where the higher level is used and 'lowest' where the lowest level is used.");
		this.elementalFusionLevelMultiplier = config.getDouble("Mob Interaction", "Elemental Fusion", this.elementalFusionLevelMultiplier, "The level of a mob created via fusion is multiplied by this value, set to 1 for no changes. Tamed fusions aren't multiplied by this value.");
		this.disablePickupOffsets = config.getBool("Mob Interaction", "Disable Pickup Offset", this.disablePickupOffsets, "If true, when a mob picks up a player, the player will be positioned where the mob is rather than offset to where the mob is holding the player at.");
		this.suffocationImmunity = config.getBool("Mob Interaction", "Global Suffocation Immunity", this.suffocationImmunity, "If true, all mobs will be immune to suffocation (inWall) damage.");
		this.drownImmunity = config.getBool("Mob Interaction", "Global Drown Immunity", this.drownImmunity, "If true, all mobs will be immune to damage from running out of air (drown damage).");
		this.packTreatLuring = config.getBool("Mob Interaction", "Pack Treat Luring", this.packTreatLuring, "If true, mobs can be lured with treats even if they are in a pack.");

		// Variations:
		config.setCategoryComment("Mob Variations", "Settings for how mobs randomly vary such as subspecies. Subspecies are uncommon and rare variants of regular mobs, uncommon subspecies tend to be a bit tougher and rare subspecies are quite powerful and can be considered as mini bosses..");
		this.variantSpawn = config.getBool("Mob Variations", "Variants Can Spawn", this.variantSpawn, "Set to false to prevent variants from spawning, this will not affect mobs that have already spawned as variants.");
		this.randomSizes = config.getBool("Mob Variations", "Random Sizes", this.randomSizes, "Set to false to prevent mobs from having a random size variation when spawning, this will not affect mobs that have already spawned.");
		this.randomSizeMin = config.getDouble("Mob Variations", "Random Size Min", this.randomSizeMin, "The minimum size scale mobs can randomly spawn at.");
		this.randomSizeMax = config.getDouble("Mob Variations", "Random Size Max", this.randomSizeMax, "The maximum size scale mobs can randomly spawn at.");
		Variant.loadGlobalSettings(config);

		// Drops:
		config.setCategoryComment("Custom Item Drops", "Here you can add a global list of item drops to add to every mob from Lycanites Mobs. Format is: mod:item,metadata,chance,min,max Multiple drops should be semicolon separated and chances are in decimal format. You can also add an additional comma and then a subspecies ID to restrict that drop to a certain subspecies like so: mod:item,metadata,chance,min,max,subspecies. minecraft:wool,2,0.25,0,3 is Green Wool with a 25% drop rate and will drop 0 to 3 blocks. Be sure to use a colon for mod:item and commas for everything else in an entry. Semicolons can be used to separate multiple entries.");
		this.globalDropsString = config.getString("Default Item Drops", "Global Drops", this.globalDropsString, "");
	}


	/**
	 * Returns a list of item drops to be drops by all mobs.
	 * @return Global item drops list.
	 */
	public List<ItemDrop> getGlobalDrops() {
		if(this.globalDrops == null) {
			this.globalDrops = new ArrayList<>();
			if(this.globalDropsString != null && this.globalDropsString.length() > 0) {
				for (String customDropEntryString : this.globalDropsString.replace(" ", "").split(";")) {
					ItemDrop itemDrop = ItemDrop.createFromConfigString(customDropEntryString);
					if(itemDrop != null) {
						this.globalDrops.add(itemDrop);
					}
				}
			}
		}
		return this.globalDrops;
	}
}
