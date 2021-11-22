package com.lycanitesmobs;

import com.lycanitesmobs.core.config.ConfigBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WorldEventListener implements IWorldEventListener {
	protected GameEventListener gameEventListener;
	protected List<Entity> customBlockProtectionEntities = new ArrayList<>();
	protected String[] customProtectionEntityIds;

	public WorldEventListener(GameEventListener gameEventListener) {
		this.gameEventListener = gameEventListener;
	}

	@Override
	public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {

	}

	@Override
	public void notifyLightSet(BlockPos pos) {

	}

	@Override
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {

	}

	@Override
	public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {

	}

	@Override
	public void playRecord(SoundEvent soundIn, BlockPos pos) {

	}

	@Override
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {

	}

	@Override
	public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {

	}

	@Override
	public void onEntityAdded(Entity entity) {
		// Custom Entity Block Protection:
		if (this.customProtectionEntityIds == null) {
			ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
			this.customProtectionEntityIds = config.getStringList("Block Protection", "Custom Entity Block Protection", new String[] {}, "A list of entities to prevent players from placing or destroying block near, takes a list of entity ids.");
		}
		if (this.customProtectionEntityIds.length > 0) {
			ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(entity.getEntityWorld());
			if (extendedWorld != null) {
				ResourceLocation entityResourceLocation = EntityList.getKey(entity);
				if (entityResourceLocation != null) {
					String entityId = entityResourceLocation.toString();
					for (String customProtectedEntityId : this.customProtectionEntityIds) {
						if (customProtectedEntityId.equals(entityId)) {
							extendedWorld.bossUpdate(entity);
							customBlockProtectionEntities.add(entity);
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public void onEntityRemoved(Entity entity) {
		this.gameEventListener.onEntityLeaveWorld(entity);
	}

	@Override
	public void broadcastSound(int soundID, BlockPos pos, int data) {

	}

	@Override
	public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {

	}

	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {

	}
}
