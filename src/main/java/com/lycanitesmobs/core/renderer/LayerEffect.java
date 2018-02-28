package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerEffect extends LayerBase {

	public String textureSuffix;
	public boolean glow = false;
	public boolean additive = false;
	public boolean subspecies = true;

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerEffect(RenderCreature renderer, String textureSuffix) {
        super(renderer);
        this.name = textureSuffix;
        this.textureSuffix = textureSuffix;
    }

	public LayerEffect(RenderCreature renderer, String textureSuffix, boolean glow, boolean additive, boolean subspecies) {
		super(renderer);
		this.name = textureSuffix;
		this.textureSuffix = textureSuffix;
		this.glow = glow;
		this.additive = additive;
		this.subspecies = subspecies;
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
		return entity.getTexture(this.textureSuffix);
    }

	@Override
	public void onRenderStart(Entity entity, boolean trophy) {
		// Glow In Dark:
		int i = entity.getBrightnessForRender();
		if(this.glow) {
			i = 15728880;
		}
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);

		// Additive: TODO Get this working!
    	if(this.additive) {
			//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
		}

		// Blend:
		//GlStateManager.depthMask(false);
		/*GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);*/
	}

	@Override
	public void onRenderFinish(Entity entity, boolean trophy) {
		/*GlStateManager.disableBlend();
		GlStateManager.enableAlpha();*/
		//GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
	}
}
