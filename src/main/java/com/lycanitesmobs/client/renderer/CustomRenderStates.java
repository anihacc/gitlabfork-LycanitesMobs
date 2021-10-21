package com.lycanitesmobs.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class CustomRenderStates extends RenderStateShard {
	public static VertexFormat POS_COL_TEX_LIGHT_FADE_NORMAL;
	public static VertexFormat POS_COL_TEX_NORMAL;

	public enum BLEND {
		NORMAL(0), ADD(1), SUB(2);
		public final int id;
		BLEND(int value) { this.id = value; }
		public int getValue() { return id; }
	}

	protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("lm_additive_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});

	protected static final RenderStateShard.TransparencyStateShard SUBTRACTIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("lm_subtractive_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});

	public CustomRenderStates(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
		super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
	}

	public static RenderType getObjRenderType(ResourceLocation texture, int blending, boolean glow) {
		if(POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_POSITION);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_COLOR);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_UV0);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_UV1);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_UV2);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_NORMAL);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_PADDING);
			POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderStateShard.TransparencyStateShard transparencyState = TRANSLUCENT_TRANSPARENCY;
		if(blending == BLEND.ADD.getValue()) {
			transparencyState = ADDITIVE_TRANSPARENCY;
		}
		else if(blending == BLEND.SUB.getValue()) {
			transparencyState = SUBTRACTIVE_TRANSPARENCY;
		}
		RenderStateShard.DiffuseLightingStateShard lightingState = DIFFUSE_LIGHTING;
		if(glow) {
			lightingState = NO_DIFFUSE_LIGHTING;
		}
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)) // Texture
				.setTransparencyState(transparencyState)
				.setDiffuseLightingState(lightingState)
				.setAlphaState(DEFAULT_ALPHA)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true);
		return RenderType.create("lm_obj_translucent_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
	}

	public static RenderType getObjColorOnlyRenderType(ResourceLocation texture, int blending, boolean glow) {
		if(POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_POSITION);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_COLOR);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_NORMAL);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_PADDING);
			POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderStateShard.TransparencyStateShard transparencyState = TRANSLUCENT_TRANSPARENCY;
		if(blending == BLEND.ADD.getValue()) {
			transparencyState = ADDITIVE_TRANSPARENCY;
		}
		else if(blending == BLEND.SUB.getValue()) {
			transparencyState = SUBTRACTIVE_TRANSPARENCY;
		}
		RenderStateShard.DiffuseLightingStateShard lightingState = DIFFUSE_LIGHTING;
		if(glow) {
			lightingState = NO_DIFFUSE_LIGHTING;
		}
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)) // Texture
				.setTransparencyState(transparencyState)
				.setDiffuseLightingState(lightingState)
				.setAlphaState(DEFAULT_ALPHA)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true);
		return RenderType.create("lm_obj_translucent_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
	}

	public static RenderType getObjOutlineRenderType(ResourceLocation texture) {
		if(POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_POSITION);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_COLOR);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_UV0);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_UV1);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_UV2);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_NORMAL);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_PADDING);
			POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setCullState(NO_CULL)
				.setDepthTestState(NO_DEPTH_TEST)
				.setAlphaState(DEFAULT_ALPHA)
				.setTexturingState(OUTLINE_TEXTURING)
				.setFogState(NO_FOG)
				.setOutputState(OUTLINE_TARGET)
				.createCompositeState(false);
		return RenderType.create("lm_obj_outline_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
	}

	public static RenderType getSpriteRenderType(ResourceLocation texture) {
		if(POS_COL_TEX_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_POSITION);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_COLOR);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_UV0);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_NORMAL);
			vertexFormatValues.add(DefaultVertexFormat.ELEMENT_PADDING);
			POS_COL_TEX_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setAlphaState(DEFAULT_ALPHA)
				.createCompositeState(true);
		return RenderType.create("lm_sprite", POS_COL_TEX_NORMAL, 7, 256, true, false, renderTypeState);
	}
}
