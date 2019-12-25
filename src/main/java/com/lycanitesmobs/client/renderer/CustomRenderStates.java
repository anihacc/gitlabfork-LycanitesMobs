package com.lycanitesmobs.client.renderer;

import com.google.common.collect.ImmutableList;
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

	public CustomRenderStates(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
		super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
	}

	public static RenderType getObjRenderType(ResourceLocation texture) {
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

		RenderType.State renderTypeState = RenderType.State.func_228694_a_()
				.func_228724_a_(new RenderState.TextureState(texture, false, false)) // Texture
				.func_228726_a_(field_228515_g_) // Transparency (translucent) TODO Additive and Subtractive
				.func_228716_a_(field_228532_x_) // Diffuse Lighting (first)
				.func_228713_a_(field_228517_i_) // Alpha 0.003921569F
				.func_228714_a_(field_228491_A_) // Cull (off)
				.func_228719_a_(field_228528_t_) // Lightmap (first)
				.func_228722_a_(field_228530_v_) // Overlay (first)
				.func_228728_a_(true);
		return RenderType.func_228633_a_("obj_cutout_no_cull", POS_COL_TEX_LIGHT_FADE_NORMAL, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
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

		RenderType.State renderTypeState = RenderType.State.func_228694_a_()
				.func_228724_a_(new RenderState.TextureState(texture, false, false)) // Texture
				.func_228713_a_(field_228517_i_) // Alpha 0.003921569F
				.func_228728_a_(true);
		return RenderType.func_228633_a_("sprite", POS_COL_TEX_NORMAL, 7, 256, true, false, renderTypeState);
	}
}
