package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.entity.EntityFactory;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.UUID;

public class EntitySpawnPacket implements IPacket<ClientPlayNetHandler> {
	public String entityTypeName = "";
	public int entityId = 0;
	public UUID uuid;
	public float pitch;
	public float yaw;
	public double x;
	public double y;
	public double z;

	public EntitySpawnPacket(Entity serverEntity) {
		if(serverEntity != null) {
			if(serverEntity instanceof BaseProjectileEntity) {
				this.entityTypeName = ((BaseProjectileEntity)serverEntity).entityName;
			}
			this.entityId = serverEntity.getEntityId();
			this.uuid = serverEntity.getUniqueID();
			this.pitch = serverEntity.rotationPitch;
			this.yaw = serverEntity.rotationYaw;
			this.x = serverEntity.posX;
			this.y = serverEntity.posY;
			this.z = serverEntity.posZ;
		}
	}

	@Override
	public void readPacketData(PacketBuffer packet) throws IOException {
		this.entityTypeName = packet.readString();
		this.entityId = packet.readInt();
		this.uuid = packet.readUniqueId();
		this.pitch = packet.readFloat();
		this.yaw = packet.readFloat();
		this.x = packet.readDouble();
		this.y = packet.readDouble();
		this.z = packet.readDouble();
	}

	@Override
	public void writePacketData(PacketBuffer packet) throws IOException {
		packet.writeString(this.entityTypeName);
		packet.writeInt(this.entityId);
		packet.writeUniqueId(this.uuid);
		packet.writeFloat(this.pitch);
		packet.writeFloat(this.yaw);
		packet.writeDouble(this.x);
		packet.writeDouble(this.y);
		packet.writeDouble(this.z);
	}

	@Override
	public void processPacket(ClientPlayNetHandler handler) {
		if(!EntityFactory.getInstance().entityTypeNetworkMap.containsKey(this.entityTypeName)) {
			LycanitesMobs.logWarning("", "Unable to find entity type from packet: " + this.entityTypeName);
			return;
		}
		EntityType entityType = EntityFactory.getInstance().entityTypeNetworkMap.get(this.entityTypeName);
		Entity entity = EntityFactory.getInstance().create(entityType, LycanitesMobs.PROXY.getWorld());
		if(entity == null) {
			LycanitesMobs.logWarning("", "Unable to create client entity from packet: " + this.entityTypeName);
			return;
		}
		entity.setPosition(this.x, this.y, this.z);
		entity.rotationPitch = this.pitch;
		entity.rotationYaw = this.yaw;
		entity.setEntityId(this.entityId);
		entity.setUniqueId(this.uuid);

		// Projectiles:
		if(entity instanceof BaseProjectileEntity) {
			((BaseProjectileEntity)entity).entityName = this.entityTypeName;
			if(entity instanceof CustomProjectileEntity) {
				ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.entityTypeName);
				if(projectileInfo != null) {
					((CustomProjectileEntity) entity).setProjectileInfo(projectileInfo);
				}
			}
		}

		handler.getWorld().addEntity(this.entityId, entity);
	}
}
