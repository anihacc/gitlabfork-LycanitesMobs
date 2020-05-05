package com.lycanitesmobs.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class CustomRenderStates extends RenderState {
	public static VertexFormat POS_COL_TEX_LIGHT_FADE_NORMAL;
	public static VertexFormat POS_COL_TEX_NORMAL;

	public enum BLEND {
		NORMAL(0), ADD(1), SUB(2);
		public final int id;
		BLEND(int value) { this.id = value; }
		public int getValue() { return id; }
	}

	protected static final RenderState.TransparencyState ADDITIVE_TRANSPARENCY = new RenderState.TransparencyState("lm_additive_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});

	protected static final RenderState.TransparencyState SUBTRACTIVE_TRANSPARENCY = new RenderState.TransparencyState("lm_subtractive_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}, () -> {
		RenderSystem.disableBlend();
		RenderSystem.defaultBlendFunc();
	});

	public CustomRenderStates(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
		super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
	}

	public static RenderType getObjRenderType(Identifier texture, int blending, boolean glow) {
		if(POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormats.POSITION_3F);
			vertexFormatValues.add(DefaultVertexFormats.COLOR_4UB);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2F);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2S);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2SB);
			vertexFormatValues.add(DefaultVertexFormats.NORMAL_3B);
			vertexFormatValues.add(DefaultVertexFormats.PADDING_1B);
			POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderState.TransparencyState transparencyState = TRANSLUCENT_TRANSPARENCY;
		if(blending == BLEND.ADD.getValue()) {
			transparencyState = ADDITIVE_TRANSPARENCY;
		}
		else if(blending == BLEND.SUB.getValue()) {
			transparencyState = SUBTRACTIVE_TRANSPARENCY;
		}
		RenderState.DiffuseLightingState lightingState = DIFFUSE_LIGHTING_ENABLED;
		if(glow) {
			lightingState = DIFFUSE_LIGHTING_DISABLED;
		}
		RenderType.State renderTypeState = RenderType.State.builder()
				.texture(new RenderState.TextureState(texture, false, false)) // Texture
				.transparency(transparencyState)
				.diffuseLighting(lightingState)
				.alpha(DEFAULT_ALPHA)
				.cull(CULL_DISABLED)
				.lightmap(LIGHTMAP_ENABLED)
				.overlay(OVERLAY_ENABLED)
				.build(true);
		return RenderType.get("lm_obj_translucent_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
	}

	public static RenderType getObjColorOnlyRenderType(Identifier texture, int blending, boolean glow) {
		if(POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormats.POSITION_3F);
			vertexFormatValues.add(DefaultVertexFormats.COLOR_4UB);
			vertexFormatValues.add(DefaultVertexFormats.NORMAL_3B);
			vertexFormatValues.add(DefaultVertexFormats.PADDING_1B);
			POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderState.TransparencyState transparencyState = TRANSLUCENT_TRANSPARENCY;
		if(blending == BLEND.ADD.getValue()) {
			transparencyState = ADDITIVE_TRANSPARENCY;
		}
		else if(blending == BLEND.SUB.getValue()) {
			transparencyState = SUBTRACTIVE_TRANSPARENCY;
		}
		RenderState.DiffuseLightingState lightingState = DIFFUSE_LIGHTING_ENABLED;
		if(glow) {
			lightingState = DIFFUSE_LIGHTING_DISABLED;
		}
		RenderType.State renderTypeState = RenderType.State.builder()
				.texture(new RenderState.TextureState(texture, false, false)) // Texture
				.transparency(transparencyState)
				.diffuseLighting(lightingState)
				.alpha(DEFAULT_ALPHA)
				.cull(CULL_DISABLED)
				.lightmap(LIGHTMAP_ENABLED)
				.overlay(OVERLAY_ENABLED)
				.build(true);
		return RenderType.get("lm_obj_translucent_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
	}

	public static RenderType getObjOutlineRenderType(Identifier texture) {
		if(POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormats.POSITION_3F);
			vertexFormatValues.add(DefaultVertexFormats.COLOR_4UB);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2F);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2S);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2SB);
			vertexFormatValues.add(DefaultVertexFormats.NORMAL_3B);
			vertexFormatValues.add(DefaultVertexFormats.PADDING_1B);
			POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderType.State renderTypeState = RenderType.State.builder()
				.texture(new RenderState.TextureState(texture, false, false))
				.cull(CULL_DISABLED)
				.depthTest(DEPTH_ALWAYS)
				.alpha(DEFAULT_ALPHA)
				.texturing(OUTLINE_TEXTURING)
				.fog(NO_FOG)
				.target(OUTLINE_TARGET)
				.build(false);
		return RenderType.get("lm_obj_outline_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
	}

	public static RenderType getSpriteRenderType(Identifier texture) {
		if(POS_COL_TEX_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormats.POSITION_3F);
			vertexFormatValues.add(DefaultVertexFormats.COLOR_4UB);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2F);
			vertexFormatValues.add(DefaultVertexFormats.NORMAL_3B);
			vertexFormatValues.add(DefaultVertexFormats.PADDING_1B);
			POS_COL_TEX_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderType.State renderTypeState = RenderType.State.builder()
				.texture(new RenderState.TextureState(texture, false, false))
				.alpha(DEFAULT_ALPHA)
				.build(true);
		return RenderType.get("lm_sprite", POS_COL_TEX_NORMAL, 7, 256, true, false, renderTypeState);
	}
}
