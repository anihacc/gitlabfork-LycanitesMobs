package com.lycanitesmobs.core.entity;

import net.minecraft.entity.Entity;

import java.util.UUID;

public class BossEntry {
    public UUID uuid;
    public Entity entity;
    public long lastUpdated = -1;
    public int nearbyRange = 60;

    /**
     * Updates this boss entry with the provided entity.
     * @param entity The entity to update this boss entry with.
     */
    public void update(Entity entity) {
        this.uuid = entity.getUniqueID();
        this.entity = entity;
        this.lastUpdated = entity.getEntityWorld().getTotalWorldTime();
        if(entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity creatureEntity = (BaseCreatureEntity)entity;
            if(creatureEntity.creatureInfo != null) {
                this.nearbyRange = creatureEntity.creatureInfo.bossNearbyRange;
            }
        }
    }
}
