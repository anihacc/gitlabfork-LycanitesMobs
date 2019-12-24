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

public class ObjRenderState extends RenderState {
	public static VertexFormat VERTEX_FORMAT;

	public ObjRenderState(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
		super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
	}

	public static RenderType getObjRenderType(ResourceLocation texture) {
		if(VERTEX_FORMAT == null) {
			List<VertexFormatElement> vertexFormatValues = new ArrayList<>();
			vertexFormatValues.add(DefaultVertexFormats.POSITION_3F);
			vertexFormatValues.add(DefaultVertexFormats.COLOR_4UB);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2F);
			vertexFormatValues.add(DefaultVertexFormats.TEX_2S);
			vertexFormatValues.add(DefaultVertexFormats.field_227848_e_);
			vertexFormatValues.add(DefaultVertexFormats.NORMAL_3B);
			vertexFormatValues.add(DefaultVertexFormats.PADDING_1B);
			VERTEX_FORMAT = new VertexFormat(ImmutableList.copyOf(vertexFormatValues));
		}

		RenderType.State renderTypeState = RenderType.State.func_228694_a_()
				.func_228724_a_(new RenderState.TextureState(texture, false, false)) // Texture
				.func_228726_a_(field_228510_b_) // Transparency
				.func_228716_a_(field_228532_x_) // Diffuse Lighting
				.func_228713_a_(field_228517_i_) // Alpha
				.func_228714_a_(field_228491_A_) // Cull
				.func_228719_a_(field_228528_t_) // Lightmap
				.func_228722_a_(field_228530_v_) // Overlay
				.func_228728_a_(true);
		return RenderType.func_228633_a_("obj_cutout_no_cullb", VERTEX_FORMAT, GL11.GL_TRIANGLES, 256, true, false, renderTypeState);
	}
}
