package com.lycanitesmobs.client.renderer.layer;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCreatureGlow extends LayerCreatureBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerCreatureGlow(CreatureRenderer renderer) {
        super(renderer);
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public Vector4f getPartColor(String partName, BaseCreatureEntity entity, boolean trophy) {
        return new Vector4f(1, 1, 1, 1);
    }

    @Override
    public ResourceLocation getLayerTexture(BaseCreatureEntity entity) {
		String textureName = entity.getTextureName();
		if(entity.getSubspecies() != null) {
			textureName += "_" + entity.getSubspecies().color;
		}
		textureName += "_glow";
		if(TextureManager.getTexture(textureName) == null)
			TextureManager.addTexture(textureName, entity.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
		return TextureManager.getTexture(textureName);
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
