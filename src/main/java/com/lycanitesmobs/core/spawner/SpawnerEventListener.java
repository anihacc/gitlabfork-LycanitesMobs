package com.lycanitesmobs.core.spawner;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.trigger.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnerEventListener {
	private static SpawnerEventListener INSTANCE;
	public static boolean testOnCreative = false;

	private List<WorldSpawnTrigger> worldSpawnTriggers = new ArrayList<>();
	private List<PlayerSpawnTrigger> playerSpawnTriggers = new ArrayList<>();
	private List<KillSpawnTrigger> killSpawnTriggers = new ArrayList<>();
	private List<EntitySpawnedSpawnTrigger> entitySpawnedSpawnTriggers = new ArrayList<>();
	private List<ChunkSpawnTrigger> chunkSpawnTriggers = new ArrayList<>();
	private List<BlockSpawnTrigger> blockSpawnTriggers = new ArrayList<>();
	private List<SleepSpawnTrigger> sleepSpawnTriggers = new ArrayList<>();
	private List<FishingSpawnTrigger> fishingSpawnTriggers = new ArrayList<>();
	private List<ExplosionSpawnTrigger> explosionSpawnTriggers = new ArrayList<>();
	private List<MobEventSpawnTrigger> mobEventSpawnTriggers = new ArrayList<>();
	private List<MixBlockSpawnTrigger> mixBlockSpawnTriggers = new ArrayList<>();

	private Map<String, List<ChunkPos>> freshChunks = new HashMap<>();


	/** Returns the main Mob Event Listener instance. **/
	public static SpawnerEventListener getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new SpawnerEventListener();
		}
		return INSTANCE;
	}


	/**
	 * Adds a new Spawn Trigger.
	 * @return True on success, false if it failed to add (could happen if the Trigger type has no matching list created yet.
	 */
	public boolean addTrigger(SpawnTrigger spawnTrigger) {
		if(spawnTrigger instanceof WorldSpawnTrigger && !this.worldSpawnTriggers.contains(spawnTrigger)) {
			this.worldSpawnTriggers.add((WorldSpawnTrigger)spawnTrigger);
			return true;
		}
		if(spawnTrigger instanceof PlayerSpawnTrigger && !this.playerSpawnTriggers.contains(spawnTrigger)) {
			this.playerSpawnTriggers.add((PlayerSpawnTrigger)spawnTrigger);
			return true;
		}
		if(spawnTrigger instanceof KillSpawnTrigger && !this.killSpawnTriggers.contains(spawnTrigger)) {
			this.killSpawnTriggers.add((KillSpawnTrigger)spawnTrigger);
			return true;
		}
		if(spawnTrigger instanceof EntitySpawnedSpawnTrigger && !this.entitySpawnedSpawnTriggers.contains(spawnTrigger)) {
			this.entitySpawnedSpawnTriggers.add((EntitySpawnedSpawnTrigger)spawnTrigger);
			return true;
		}
		if(spawnTrigger instanceof ChunkSpawnTrigger && !this.chunkSpawnTriggers.contains(spawnTrigger)) {
			this.chunkSpawnTriggers.add((ChunkSpawnTrigger)spawnTrigger);
			return true;
		}

		// Block Triggers:
		if(spawnTrigger instanceof MixBlockSpawnTrigger && !this.mixBlockSpawnTriggers.contains(spawnTrigger)) {
			this.mixBlockSpawnTriggers.add((MixBlockSpawnTrigger)spawnTrigger);
			return true;
		}
		if(spawnTrigger instanceof SleepSpawnTrigger && !this.sleepSpawnTriggers.contains(spawnTrigger)) {
			this.sleepSpawnTriggers.add((SleepSpawnTrigger)spawnTrigger);
			return true;
		}
		if(spawnTrigger instanceof BlockSpawnTrigger && !this.blockSpawnTriggers.contains(spawnTrigger)) {
			this.blockSpawnTriggers.add((BlockSpawnTrigger)spawnTrigger);
			return true;
		}

		if(spawnTrigger instanceof FishingSpawnTrigger && !this.fishingSpawnTriggers.contains(spawnTrigger)) {
			this.fishingSpawnTriggers.add((FishingSpawnTrigger)spawnTrigger);
			return true;
		}
		if(spawnTrigger instanceof ExplosionSpawnTrigger && !this.explosionSpawnTriggers.contains(spawnTrigger)) {
			this.explosionSpawnTriggers.add((ExplosionSpawnTrigger)spawnTrigger);
			return true;
		}
		if(spawnTrigger instanceof MobEventSpawnTrigger && !this.mobEventSpawnTriggers.contains(spawnTrigger)) {
			this.mobEventSpawnTriggers.add((MobEventSpawnTrigger)spawnTrigger);
			return true;
		}
		return false;
	}

	/**
	 * Removes a Spawn Trigger.
	 */
	public void removeTrigger(SpawnTrigger spawnTrigger) {
		if(this.worldSpawnTriggers.contains(spawnTrigger)) {
			this.worldSpawnTriggers.remove(spawnTrigger);
		}
		if(this.playerSpawnTriggers.contains(spawnTrigger)) {
			this.playerSpawnTriggers.remove(spawnTrigger);
		}
		if(this.killSpawnTriggers.contains(spawnTrigger)) {
			this.killSpawnTriggers.remove(spawnTrigger);
		}
		if(this.entitySpawnedSpawnTriggers.contains(spawnTrigger)) {
			this.entitySpawnedSpawnTriggers.remove(spawnTrigger);
		}
		if(this.chunkSpawnTriggers.contains(spawnTrigger)) {
			this.chunkSpawnTriggers.remove(spawnTrigger);
		}
		if(this.blockSpawnTriggers.contains(spawnTrigger)) {
			this.blockSpawnTriggers.remove(spawnTrigger);
		}
		if(this.sleepSpawnTriggers.contains(spawnTrigger)) {
			this.sleepSpawnTriggers.remove(spawnTrigger);
		}
		if(this.fishingSpawnTriggers.contains(spawnTrigger)) {
			this.fishingSpawnTriggers.remove(spawnTrigger);
		}
		if(this.explosionSpawnTriggers.contains(spawnTrigger)) {
			this.explosionSpawnTriggers.remove(spawnTrigger);
		}
		if(this.mobEventSpawnTriggers.contains(spawnTrigger)) {
			this.mobEventSpawnTriggers.remove(spawnTrigger);
		}
		if(this.mixBlockSpawnTriggers.contains(spawnTrigger)) {
			this.mixBlockSpawnTriggers.remove(spawnTrigger);
		}
	}


	// ==================================================
	//               World Update Event
	// ==================================================
	/** This uses world update events to update World and Mob Event Spawn Triggers. **/
	@SubscribeEvent
	public void onWorldUpdate(TickEvent.WorldTickEvent event) {
		World world = event.world;
		if(world.isRemote)
			return;
		ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		if(worldExt == null)
			return;

		// Only Tick On World Time Ticks:
		if(worldExt.lastSpawnerTime == world.getGameTime()) {
			return;
		}
		worldExt.lastSpawnerTime = world.getGameTime();

		// World Tick:
		List<BlockPos> triggerPositions = new ArrayList<>();
		for(PlayerEntity player : world.getPlayers()) {
			if(triggerPositions.isEmpty()) {
				triggerPositions.add(player.getPosition());
				continue;
			}
			boolean nearOtherPlayers = false;
			for(BlockPos triggerPosition : triggerPositions) {
				if(player.getDistanceSq(Vector3d.copy(triggerPosition)) <= 100 * 100) {
					nearOtherPlayers = true;
				}
			}
			if(!nearOtherPlayers) {
				triggerPositions.add(player.getPosition());
			}
		}
		for(BlockPos triggerPosition : triggerPositions) {
			for (WorldSpawnTrigger spawnTrigger : this.worldSpawnTriggers) {
				spawnTrigger.onTick(world, triggerPosition, worldExt.lastSpawnerTime);
			}
		}
		this.checkFreshChunks(world);

		// Mob Events:
		if(worldExt.getWorldEvent() != null) {
			for(MobEventSpawnTrigger spawnTrigger : this.mobEventSpawnTriggers) {
				spawnTrigger.onTick(world, worldExt.serverWorldEventPlayer);
			}
		}
	}
	
	
	// ==================================================
	//               Entity Update Event
	// ==================================================
	public Map<PlayerEntity, Long> playerUpdateTicks = new HashMap<>();
	
	/** This uses the player update events to update Tick Spawn Triggers. **/
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		if(entity == null || !(entity instanceof PlayerEntity) || entity.getEntityWorld() == null || entity.getEntityWorld().isRemote || event.isCanceled())
			return;
		
		// ========== Spawn Near Players ==========
		PlayerEntity player = (PlayerEntity)entity;
		
		if(!playerUpdateTicks.containsKey(player))
			playerUpdateTicks.put(player, (long)0);
		long entityUpdateTick = playerUpdateTicks.get(player);
		
		// Custom Mob Spawning:
		int tickOffset = 0;
		for(PlayerSpawnTrigger spawnTrigger : this.playerSpawnTriggers) {
			spawnTrigger.onTick(player, entityUpdateTick - tickOffset);
			tickOffset += 105;
		}

		playerUpdateTicks.put(player, entityUpdateTick + 1);
	}
	
	
	// ==================================================
	//                 Entity Death Event
	// ==================================================
	/** This uses the entity death events to update Kill Spawn Triggers. **/
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		// Get Killed:
		LivingEntity killedEntity = event.getEntityLiving();
		if(killedEntity == null || killedEntity.getEntityWorld().isRemote || event.isCanceled()) {
			return;
		}
		
		// Get Killer:
		Entity killerEntity = event.getSource().getTrueSource();
		if(!(killerEntity instanceof PlayerEntity)) {
			return;
		}

		// Call Triggers:
		for(KillSpawnTrigger spawnTrigger : this.killSpawnTriggers) {
			spawnTrigger.onKill((PlayerEntity)killerEntity, killedEntity);
		}
	}


	// ==================================================
	//                 Entity Spawn Event
	// ==================================================
	/** This uses the entity spawn events to update Entity Spawned Spawn Triggers. **/
	@SubscribeEvent
	public void onEntitySpawn(LivingSpawnEvent event) {
		// Get Spawned:
		LivingEntity spawnedEntity = event.getEntityLiving();
		if(spawnedEntity == null || spawnedEntity.getEntityWorld().isRemote || event.isCanceled()) {
			return;
		}

		// Call Triggers:
		for(EntitySpawnedSpawnTrigger spawnTrigger : this.entitySpawnedSpawnTriggers) {
			spawnTrigger.onEntitySpawned(spawnedEntity);
		}
	}


	// ==================================================
	//                Populate Chunk Event
	// ==================================================
	// TODO: Temporary, this will constantly spawn mobs as chunks load and unload, the mob density isn't too bad so I'm keeping this for testing purposes until a worldgen feature or whatever can take it's place.
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event) {
		if (event.getWorld() instanceof World) {
			String dimensionId = ((World) event.getWorld()).getDimensionKey().getLocation().toString();
			this.onChunkGenerate(dimensionId, event.getChunk().getPos());
		}
	}

	/** Set to true when chunk spawn triggers are active and back to false when they have completed. This stops a cascading trigger loop! **/
	public boolean chunkSpawnTriggersActive = false;

	/** Called every time a new chunk is generated. Adds the chunk to a list for spawning in later when ready. **/
	public void onChunkGenerate(String dimensionId, ChunkPos chunkPos) {
		// Add To Fresh Chunks Map:
		if(!this.freshChunks.containsKey(dimensionId)) {
			this.freshChunks.put(dimensionId, new ArrayList<>());
		}
		if(this.freshChunks.get(dimensionId).contains(chunkPos)) {
			return;
		}
		this.freshChunks.get(dimensionId).add(chunkPos);
	}

	/** Called on World Tick, checks for any fresh chunks to spawn in. **/
	public void checkFreshChunks(World world) {
		String dimensionId = world.getDimensionKey().getLocation().toString();
		if (!this.freshChunks.containsKey(dimensionId)) {
			this.freshChunks.put(dimensionId, new ArrayList<>());
			return;
		}

		List<ChunkPos> freshChunks = new ArrayList<>(this.freshChunks.get(dimensionId));
		for(ChunkPos chunkPos : freshChunks) {
			// Check If Loaded:
			if(!world.getChunkProvider().isChunkLoaded(chunkPos)) {
				continue;
			}
			this.freshChunks.get(dimensionId).remove(chunkPos);

			// Call Triggers:
			//if (this.chunkSpawnTriggersActive) {}
			for (ChunkSpawnTrigger spawnTrigger : this.chunkSpawnTriggers) {
				if (spawnTrigger.onChunkPopulate(world, chunkPos)) {
					//this.chunkSpawnTriggersActive = true;
				}
			}
			//this.chunkSpawnTriggersActive = false;
		}

		// Clear If Too Many:
		if(this.freshChunks.get(dimensionId).size() > 1000) {
			this.freshChunks.get(dimensionId).clear();
		}
	}

	
	// ==================================================
	//                 Harvest Drops Event
	// ==================================================
	/** This uses the block harvest drops events to update Block Spawn Triggers. **/
	@SubscribeEvent
	public void onHarvestDrops(BlockEvent.BlockToolInteractEvent event) {
		PlayerEntity player = event.getPlayer();
		if(event.getState() == null || !(event.getWorld() instanceof World) || event.getWorld().isRemote() || event.isCanceled()) {
			return;
		}
		World world = (World)event.getWorld();
		if(player != null && (!testOnCreative && player.abilities.isCreativeMode)) { // No Spawning for Creative Players
			return;
		}
		
		// Spawn On Block Harvest:
        BlockPos blockPos = event.getPos();
		BlockState blockState = event.getState();
		int bonusLevel = 0;
		int silklevel = 0;
		if(player != null) {
			bonusLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getHeldItemMainhand());
			silklevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand());
		}

        for(BlockSpawnTrigger spawnTrigger : this.blockSpawnTriggers) {
            spawnTrigger.onBlockHarvest(world, player, blockPos, blockState, 0, bonusLevel, silklevel > 0);
        }
	}


    // ==================================================
    //                 Break Block Event
    // ==================================================
	/** This uses the block break events to update Block Spawn Triggers. **/
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
		if(event.getState() == null || !(event.getWorld() instanceof World) || event.getWorld().isRemote() || event.isCanceled()) {
			return;
		}
		World world = (World)event.getWorld();
		this.onBlockBreak(world, event.getPos(), event.getState(), event.getPlayer(), 0);
    }

	public void onBlockBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity player, int chain) {
		if(player != null && (!testOnCreative && player.abilities.isCreativeMode)) {
			return;
		}

		// Spawn On Block Harvest:
		for(BlockSpawnTrigger spawnTrigger : this.blockSpawnTriggers) {
			spawnTrigger.onBlockBreak(world, player, blockPos, blockState, chain);
		}
	}


	// ==================================================
	//                 Block Place Event
	// ==================================================
	/** This uses the block place events to update Block Spawn Triggers. **/
	@SubscribeEvent
	public void onBlockPlace(BlockEvent.EntityPlaceEvent.EntityPlaceEvent event) {
		if(event.getState() == null || !(event.getWorld() instanceof World) || event.getWorld().isRemote() || event.isCanceled()) {
			return;
		}
		World world = (World)event.getWorld();
		if(event.getEntity() instanceof PlayerEntity) {
			this.onBlockPlace(world, event.getPos(), event.getState(), (PlayerEntity) event.getEntity(), 0);
		}
	}

	public void onBlockPlace(World world, BlockPos blockPos, BlockState blockState, PlayerEntity player, int chain) {
		if(player != null && (!testOnCreative && player.abilities.isCreativeMode)) {
			return;
		}

		// Spawn On Block Harvest:
		for(BlockSpawnTrigger spawnTrigger : this.blockSpawnTriggers) {
			spawnTrigger.onBlockPlace(world, player, blockPos, blockState, chain);
		}
	}

	
	// ==================================================
	//                Player Use Bed Event
	// ==================================================
	/** This uses the player sleep in bed event to spawn mobs. **/
	@SubscribeEvent
	public void onSleep(PlayerSleepInBedEvent event) {
		PlayerEntity player = event.getPlayer();
		if(player == null || player.getEntityWorld().isRemote || event.isCanceled())
			return;
		
		// Get Coords:
		World world = player.getEntityWorld();
        BlockPos spawnPos = player.getPosition().add(0, 0, 1);
		
		if(world.isRemote || world.isDaytime())
			return;
		
		// Run Spawners:
		boolean interrupted = false;
		for(SleepSpawnTrigger spawnTrigger : this.sleepSpawnTriggers) {
			if(spawnTrigger.onSleep(world, player, spawnPos, world.getBlockState(player.getPosition()))) {
				interrupted = true;
			}
		}
		
		// Interrupt:
		if(interrupted) {
			event.setResult(PlayerEntity.SleepResult.NOT_SAFE);
		}
	}


	// ==================================================
	//                  Fished Event
	// ==================================================
	/** This uses the item fished event to spawn mobs. **/
	@SubscribeEvent
	public void onFished(ItemFishedEvent event) {
		PlayerEntity player = event.getPlayer();
		if(player == null || player.getEntityWorld().isRemote || event.isCanceled())
			return;
		if(!testOnCreative && player.abilities.isCreativeMode) { // No Spawning for Creative Players
			return;
		}

		World world = player.getEntityWorld();
		Entity hookEntity = event.getHookEntity();
		for(FishingSpawnTrigger spawnTrigger : this.fishingSpawnTriggers) {
			spawnTrigger.onFished(world, player, hookEntity);
		}
	}


	// ==================================================
	//                  Explosion Event
	// ==================================================
	/** This uses the explosion event to spawn mobs. **/
	@SubscribeEvent
	public void onExplosion(ExplosionEvent.Detonate event) {
		Explosion explosion = event.getExplosion();
		if(explosion == null) {
			return;
		}

		World world = event.getWorld();
		PlayerEntity player = null;
		if(explosion.getExplosivePlacedBy() instanceof PlayerEntity) {
			player = (PlayerEntity)explosion.getExplosivePlacedBy();
		}

		if(player != null && (!testOnCreative && player.abilities.isCreativeMode)) { // No Spawning for Creative Players
			return;
		}

		for(ExplosionSpawnTrigger spawnTrigger : this.explosionSpawnTriggers) {
			spawnTrigger.onExplosion(world, player, explosion);
		}
	}


	// ==================================================
	//                  Lava Mix Event
	// ==================================================
	/** Used to keep track of where the mix event was last fired as it sometimes fires multiple times in which case extra triggers should be ignored adn this will also reduce spawns from cobblestone generators. **/
	private BlockPos lastMixPos;

	/** This uses the block neighbor notify event with checks for lava and water mixing to spawn mobs. **/
	@SubscribeEvent
	public void onLavaMix(BlockEvent.NeighborNotifyEvent event) {
		if(!(event.getWorld() instanceof World))
			return;
		boolean trigger = false;
		World world = (World)event.getWorld();

		// Only If Players Are Nearby (Big Performance Saving):
		boolean playerNearby = false;
		Vector3d posVec = Vector3d.copy(event.getPos());
		for(PlayerEntity playerEntity : world.getPlayers()) {
			if(playerEntity.getDistanceSq(posVec) <= 20 * 20) {
				playerNearby = true;
				break;
			}
		}
		if(!playerNearby) {
			return;
		}

		if(event.getState().getBlock() == Blocks.WATER) {
			BlockState sideBlockState = event.getWorld().getBlockState(event.getPos().down());
			if(sideBlockState.getBlock() == Blocks.LAVA && sideBlockState.get(FlowingFluidBlock.LEVEL) == 0) {
				trigger = true;
			}
		}

		else if(event.getState().getBlock() == Blocks.LAVA) {
			BlockState sideBlockState = event.getWorld().getBlockState(event.getPos().down());
			if(sideBlockState.getBlock() == Blocks.WATER && sideBlockState.get(FlowingFluidBlock.LEVEL) == 0) {
				trigger = true;
			}
		}

		if(trigger) {
			if(this.lastMixPos != null && this.lastMixPos.equals(event.getPos())) {
				return;
			}
			this.lastMixPos = event.getPos();

			for(MixBlockSpawnTrigger spawnTrigger : this.mixBlockSpawnTriggers) {
				spawnTrigger.onMix(world, event.getState(), event.getPos());
			}
		}
	}
}
