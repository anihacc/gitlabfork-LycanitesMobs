package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBehemoth extends ModelTemplateBiped {
    public ModelBehemoth() {
        this(1.0F);
    }

    public ModelBehemoth(float shadowSize) {

        this.initModel("behemoth", LycanitesMobs.modInfo, "entity/behemoth");

        this.trophyScale = 1.2F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }

    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
        super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

        // Pickup:
        if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).hasPickupEntity()) {
            if (partName.contains("armleft")) {
                this.rotate(-45, 0, -10);
            }
            else if (partName.contains("armright")) {
                this.rotate(-45, 0, 10);
            }
        }
    }
}
