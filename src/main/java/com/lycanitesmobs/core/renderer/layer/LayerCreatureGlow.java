package com.lycanitesmobs.core.renderer.layer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureGlow extends LayerCreatureBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerCreatureGlow(RenderCreature renderer) {
        super(renderer);
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public Vector4f getPartColor(String partName, EntityCreatureBase entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    @Override
    public ResourceLocation getLayerTexture(EntityCreatureBase entity) {
		String textureName = entity.getTextureName();
		if(entity.getSubspecies() != null) {
			textureName += "_" + entity.getSubspecies().color;
		}
		textureName += "_glow";
		if(AssetManager.getTexture(textureName) == null)
			AssetManager.addTexture(textureName, entity.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
		return AssetManager.getTexture(textureName);
    }

    @Override
    public void onRenderStart(Entity entity, boolean trophy) {
		int i = 15728880;
		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
    }

    @Override
    public void onRenderFinish(Entity entity, boolean trophy) {
		int i = entity.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float) j, (float) k);
    }
}
