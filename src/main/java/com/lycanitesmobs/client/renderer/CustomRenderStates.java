package com.lycanitesmobs.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;
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

	public static RenderType getObjRenderType(ResourceLocation texture, int blending, boolean glow) {
		if(POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormats.POSITION_3F);
			vertexFormatValues.add(DefaultVertexFormats.COLOR_4UB);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2F);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2S);
			vertexFormatValues.add(DefaultVertexFormats.field_227848_e_);
			vertexFormatValues.add(DefaultVertexFormats.NORMAL_3B);
			vertexFormatValues.add(DefaultVertexFormats.PADDING_1B);
			POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderState.TransparencyState transparencyState = field_228515_g_;
		if(blending == BLEND.ADD.getValue()) {
			transparencyState = ADDITIVE_TRANSPARENCY;
		}
		else if(blending == BLEND.SUB.getValue()) {
			transparencyState = SUBTRACTIVE_TRANSPARENCY;
		}
		RenderState.DiffuseLightingState lightingState = field_228532_x_; // first
		if(glow) {
			lightingState = field_228533_y_; // second
		}
		RenderType.State renderTypeState = RenderType.State.func_228694_a_() // getBuilder
				.func_228724_a_(new RenderState.TextureState(texture, false, false)) // Texture
				.func_228726_a_(transparencyState) // Transparency
				.func_228716_a_(lightingState) // Diffuse Lighting (first)
				.func_228713_a_(field_228517_i_) // Alpha 0.003921569F
				.func_228714_a_(field_228491_A_) // Cull (off)
				.func_228719_a_(field_228528_t_) // Lightmap (first)
				.func_228722_a_(field_228530_v_) // Overlay (first)
				.func_228728_a_(true);
		return RenderType.func_228633_a_("lm_obj_translucent_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
	}

	public static RenderType getObjOutlineRenderType(ResourceLocation texture) {
		if(POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormats.POSITION_3F);
			vertexFormatValues.add(DefaultVertexFormats.COLOR_4UB);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2F);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2S);
			vertexFormatValues.add(DefaultVertexFormats.field_227848_e_);
			vertexFormatValues.add(DefaultVertexFormats.NORMAL_3B);
			vertexFormatValues.add(DefaultVertexFormats.PADDING_1B);
			POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderType.State renderTypeState = RenderType.State.func_228694_a_() // getBuilder
				.func_228724_a_(new RenderState.TextureState(texture, false, false)) // Texture
				.func_228714_a_(field_228491_A_) // Cull (off)
				.func_228715_a_(field_228492_B_) // Depth Test (first)
				.func_228713_a_(field_228517_i_) // Alpha 0.003921569F
				.func_228725_a_(field_228525_q_) // Texturing (second)
				.func_228717_a_(field_228501_K_) // Fog (first)
				.func_228721_a_(field_228505_O_) // Target (second)
				.func_228728_a_(false);
		return RenderType.func_228633_a_("lm_obj_outline_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
	}

	public static RenderType getSpriteRenderType(ResourceLocation texture) {
		if(POS_COL_TEX_NORMAL == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormats.POSITION_3F);
			vertexFormatValues.add(DefaultVertexFormats.COLOR_4UB);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2F);
			vertexFormatValues.add(DefaultVertexFormats.NORMAL_3B);
			vertexFormatValues.add(DefaultVertexFormats.PADDING_1B);
			POS_COL_TEX_NORMAL = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderType.State renderTypeState = RenderType.State.func_228694_a_() // getBuilder
				.func_228724_a_(new RenderState.TextureState(texture, false, false)) // Texture
				.func_228713_a_(field_228517_i_) // Alpha 0.003921569F
				.func_228728_a_(true);
		return RenderType.func_228633_a_("lm_sprite", POS_COL_TEX_NORMAL, 7, 256, true, false, renderTypeState);
	}
}
