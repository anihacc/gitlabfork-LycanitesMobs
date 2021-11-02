package com.lycanitesmobs.core.spawner;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.info.BlockReference;
import com.lycanitesmobs.core.spawner.trigger.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnerEventListener {
    protected static SpawnerEventListener INSTANCE;
    public static boolean testOnCreative = false;

	public List<WorldSpawnTrigger> worldSpawnTriggers = new ArrayList<>();
	public List<PlayerSpawnTrigger> playerSpawnTriggers = new ArrayList<>();
	public List<KillSpawnTrigger> killSpawnTriggers = new ArrayList<>();
	public List<EntitySpawnedSpawnTrigger> entitySpawnedSpawnTriggers = new ArrayList<>();
	public List<ChunkSpawnTrigger> chunkSpawnTriggers = new ArrayList<>();
	public List<BlockSpawnTrigger> blockSpawnTriggers = new ArrayList<>();
	public List<SleepSpawnTrigger> sleepSpawnTriggers = new ArrayList<>();
	public List<FishingSpawnTrigger> fishingSpawnTriggers = new ArrayList<>();
	public List<ExplosionSpawnTrigger> explosionSpawnTriggers = new ArrayList<>();
	public List<MobEventSpawnTrigger> mobEventSpawnTriggers = new ArrayList<>();
	public List<MixBlockSpawnTrigger> mixBlockSpawnTriggers = new ArrayList<>();


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
		if(worldExt.lastSpawnerTime == world.getTotalWorldTime()) {
			return;
		}
		worldExt.lastSpawnerTime = world.getTotalWorldTime();

		// World Tick:
		List<BlockPos> triggerPositions = new ArrayList<>();
		for(EntityPlayer player : world.playerEntities) {
			if(triggerPositions.isEmpty()) {
				triggerPositions.add(player.getPosition());
				continue;
			}
			boolean nearOtherPlayers = false;
			for(BlockPos triggerPosition : triggerPositions) {
				if(player.getDistanceSq(triggerPosition) <= 100 * 100) {
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
	public Map<EntityPlayer, Long> playerUpdateTicks = new HashMap<>();
	
	/** This uses the player update events to update Tick Spawn Triggers. **/
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null || !(entity instanceof EntityPlayer) || entity.getEntityWorld() == null || entity.getEntityWorld().isRemote || event.isCanceled())
			return;
		
		// ========== Spawn Near Players ==========
		EntityPlayer player = (EntityPlayer)entity;
		
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
		EntityLivingBase killedEntity = event.getEntityLiving();
		if(killedEntity == null || killedEntity.getEntityWorld() == null || killedEntity.getEntityWorld().isRemote || event.isCanceled() || !(killedEntity instanceof EntityLiving)) {
			return;
		}
		
		// Get Killer:
		Entity killerEntity = event.getSource().getTrueSource();
		if(!(killerEntity instanceof EntityPlayer)) {
			return;
		}

		// Call Triggers:
		for(KillSpawnTrigger spawnTrigger : this.killSpawnTriggers) {
			spawnTrigger.onKill((EntityPlayer)killerEntity, (EntityLiving)killedEntity);
		}
	}


	// ==================================================
	//                 Entity Spawn Event
	// ==================================================
	/** This uses the entity spawn events to update Entity Spawned Spawn Triggers. **/
	@SubscribeEvent
	public void onEntitySpawn(LivingSpawnEvent event) {
		// Get Spawned:
		EntityLivingBase spawnedEntity = event.getEntityLiving();
		if(spawnedEntity == null || spawnedEntity.getEntityWorld() == null || spawnedEntity.getEntityWorld().isRemote || event.isCanceled() || !(spawnedEntity instanceof EntityLiving)) {
			return;
		}

		// Call Triggers:
		for(EntitySpawnedSpawnTrigger spawnTrigger : this.entitySpawnedSpawnTriggers) {
			spawnTrigger.onEntitySpawned((EntityLiving)spawnedEntity);
		}
	}


	// ==================================================
	//                Populate Chunk Event
	// ==================================================
	/** Set to true when chunk spawn triggers are active and back to false when they have completed. This stops a cascading trigger loop! **/
	public boolean chunkSpawnTriggersActive = false;

	/** Called every time a new chunk is generated. **/
	@SubscribeEvent
	public void onChunkPopulate(PopulateChunkEvent.Post event) {
		if(this.chunkSpawnTriggersActive) {
			return;
		}

		// Call Triggers:
		for(ChunkSpawnTrigger spawnTrigger : this.chunkSpawnTriggers) {
			if(spawnTrigger.onChunkPopulate(event.getWorld(), new ChunkPos(event.getChunkX(), event.getChunkZ()))) {
				this.chunkSpawnTriggersActive = true;
			}
		}

		this.chunkSpawnTriggersActive = false;
	}

	
	// ==================================================
	//                 Harvest Drops Event
	// ==================================================
	/** This uses the block harvest drops events to update Block Spawn Triggers. **/
	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event) {
		EntityPlayer player = event.getHarvester();
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isRemote || event.isCanceled()) {
			return;
		}
		if(player != null && (!testOnCreative && player.capabilities.isCreativeMode)) { // No Spawning for Creative Players
			return;
		}
		
		// Spawn On Block Harvest:
		World world = event.getWorld();
        BlockPos blockPos = event.getPos();
		IBlockState blockState = event.getState();

        for(BlockSpawnTrigger spawnTrigger : this.blockSpawnTriggers) {
            spawnTrigger.onBlockHarvest(world, player, blockPos, blockState, 0, event.getFortuneLevel(), event.isSilkTouching());
        }
	}


    // ==================================================
    //                 Break Block Event
    // ==================================================
	/** This uses the block break events to update Block Spawn Triggers. **/
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isRemote || event.isCanceled()) {
			return;
		}
		this.onBlockBreak(event.getWorld(), event.getPos(), event.getState(), event.getPlayer(), 0);
    }

	public void onBlockBreak(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer player, int chain) {
		if(player != null && (!testOnCreative && player.capabilities.isCreativeMode)) {
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
	public void onBlockPlace(BlockEvent.PlaceEvent event) {
		if(event.getState() == null || event.getWorld() == null || event.getWorld().isRemote || event.isCanceled()) {
			return;
		}
		this.onBlockPlace(event.getWorld(), event.getPos(), event.getState(), event.getPlayer(), 0);
	}

	public void onBlockPlace(World world, BlockPos blockPos, IBlockState blockState, EntityPlayer player, int chain) {
		if(player != null && (!testOnCreative && player.capabilities.isCreativeMode)) {
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
		EntityPlayer player = event.getEntityPlayer();
		if(player == null || player.getEntityWorld() == null || player.getEntityWorld().isRemote || event.isCanceled())
			return;
		
		// Get Coords:
		World world = player.getEntityWorld();
        BlockPos spawnPos = event.getPos().add(0, 0, 1);
		
		if(world == null || world.isRemote || world.provider.isDaytime())
			return;
		
		// Run Spawners:
		boolean interrupted = false;
		for(SleepSpawnTrigger spawnTrigger : this.sleepSpawnTriggers) {
			if(spawnTrigger.onSleep(world, player, spawnPos, world.getBlockState(event.getPos()))) {
				interrupted = true;
			}
		}
		
		// Interrupt:
		if(interrupted) {
			event.setResult(EntityPlayer.SleepResult.NOT_SAFE);
		}
	}


	// ==================================================
	//                  Fished Event
	// ==================================================
	/** This uses the item fished event to spawn mobs. **/
	@SubscribeEvent
	public void onFished(ItemFishedEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(player == null || player.getEntityWorld() == null || player.getEntityWorld().isRemote || event.isCanceled())
			return;
		if(player != null && (!testOnCreative && player.capabilities.isCreativeMode)) { // No Spawning for Creative Players
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
		EntityPlayer player = null;
		if(explosion.getExplosivePlacedBy() instanceof EntityPlayer) {
			player = (EntityPlayer)explosion.getExplosivePlacedBy();
		}

		if(player != null && (!testOnCreative && player.capabilities.isCreativeMode)) { // No Spawning for Creative Players
			return;
		}

		for(ExplosionSpawnTrigger spawnTrigger : this.explosionSpawnTriggers) {
			spawnTrigger.onExplosion(world, player, explosion);
		}
	}


	// ==================================================
	//                  Lava Mix Event
	// ==================================================
	/** Stores a list of references to check for block updates resulting in a mix for. **/
	private final List<BlockReference> mixingWatchList = new ArrayList<>();

	/** This uses the block neighbor notify event with checks for lava and water mixing to spawn mobs. **/
	@SubscribeEvent
	public void onLavaMix(BlockEvent.NeighborNotifyEvent event) {
		if(event.getWorld() == null) {
			return;
		}
		World world = event.getWorld();
		BlockReference eventBlockReference = new BlockReference(world, event.getPos());

		// Check for a Pending Mix:
		if (this.mixingWatchList.contains(eventBlockReference)) {
			IBlockState pendingMixBlockState = event.getWorld().getBlockState(event.getPos());
			if (pendingMixBlockState.getBlock() == Blocks.STONE || pendingMixBlockState.getBlock() == Blocks.OBSIDIAN) {
				for (MixBlockSpawnTrigger spawnTrigger : this.mixBlockSpawnTriggers) {
					spawnTrigger.onMix(world, event.getState(), event.getPos());
				}
			}
			this.mixingWatchList.remove(eventBlockReference);
			return;
		}

		// Only If Players Are Nearby (Big Performance Saving):
		Vec3d posVec = new Vec3d(event.getPos());
		EntityPlayer closestPlayer = event.getWorld().getClosestPlayer(posVec.x, posVec.y, posVec.z, 20, false);
		if(closestPlayer == null) {
			return;
		}

		BlockReference mixBlockReference = new BlockReference(world, event.getPos().down());
		IBlockState mixBlockState = mixBlockReference.getWorld().getBlockState(mixBlockReference.getPos());

		if( event.getState().getBlock() == Blocks.WATER || event.getState().getBlock() == Blocks.FLOWING_WATER) {
			if(mixBlockState.getBlock() == Blocks.LAVA && !this.mixingWatchList.contains(mixBlockReference)) {
				this.mixingWatchList.add(mixBlockReference);
			}
		}

		else if(event.getState().getBlock() == Blocks.LAVA || event.getState().getBlock() == Blocks.FLOWING_LAVA) {
			if(mixBlockState.getBlock() == Blocks.WATER && !this.mixingWatchList.contains(mixBlockReference)) {
				this.mixingWatchList.add(new BlockReference(world, event.getPos()));
			}
		}
	}
}
