package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGrell extends ModelTemplateBiped {

    public ModelGrell() {
        this(1.0F);
    }

    public ModelGrell(float shadowSize) {
    	this.initModel("grell", LycanitesMobs.modInfo, "entity/grell");
    	this.flightBobScale = 0.1F;
        this.trophyScale = 0.5F;
    }

    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		if(partName.contains("tentacle")) {
			float loopOffset = 0;
			if(partName.contains("front")) {
				loopOffset += 10;
			}
			else if(partName.contains("left")) {
				loopOffset += 20;
			}
			else if(partName.contains("right")) {
				loopOffset += 30;
			}

			if(partName.contains("left")) {
				this.rotate(
						(float) Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.2F),
						(float) Math.toDegrees(MathHelper.sin((loop + (loopOffset / 2)) * 0.2F) * 0.25F) - 10,
						(float) -Math.toDegrees(MathHelper.cos((loop + (loopOffset / 2)) * 0.09F) * 0.1F)
				);
			}
			else {
				this.rotate(
						(float) Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.2F),
						(float) -Math.toDegrees(MathHelper.sin((loop + (loopOffset / 2)) * 0.2F) * 0.25F) - 10,
						(float) -Math.toDegrees(MathHelper.cos((loop + (loopOffset / 2)) * 0.09F) * 0.1F)
				);
			}
		}
    }
}
