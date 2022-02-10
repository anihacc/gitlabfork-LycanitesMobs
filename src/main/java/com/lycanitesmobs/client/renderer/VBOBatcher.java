package com.lycanitesmobs.client.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL21;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

public class VBOBatcher {

	public static class VBODrawCommand {

		private final VertexBuffer vbo;
		private final VertexFormat format;
		private final Matrix4f matrix;
		private final ResourceLocation texLocation;
		private float red;
		private float green;
		private float blue;
		private float alpha;
		private float textureOffsetX;
		private float textureOffsetY;
		private float overlayOffsetX;
		private float overlayOffsetY;
		private float lightOffsetX;
		private float lightOffsetY;

		public VBODrawCommand(VertexBuffer vbo, VertexFormat format, Matrix4f matrix, ResourceLocation texLocation) {
			this.vbo = vbo;
			this.format = format;
			this.matrix = matrix;
			this.texLocation = texLocation;
		}

		public VBODrawCommand setColor(Vector4f color) {
			return setColor(color.x(), color.y(), color.z(), color.w());
		}

		public VBODrawCommand setColor(float red, float green, float blue, float alpha) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
			return this;
		}

		public VBODrawCommand setTextureOffset(Vector2f textureOffset) {
			return setTextureOffset(textureOffset.x, textureOffset.y);
		}

		public VBODrawCommand setTextureOffset(float textureOffsetX, float textureOffsetY) {
			this.textureOffsetX = textureOffsetX;
			this.textureOffsetY = textureOffsetY;
			return this;
		}

		public VBODrawCommand setOverlayOffset(float white, boolean red) {
			return setOverlayOffset(OverlayTexture.u(white), OverlayTexture.v(red));
		}

		public VBODrawCommand setOverlayOffset(float overlayOffsetX, float overlayOffsetY) {
			this.overlayOffsetX = overlayOffsetX;
			this.overlayOffsetY = overlayOffsetY;
			return this;
		}

		public VBODrawCommand setLightOffset(int packed) {
			return setLightOffset(LightTexture.block(packed) << 4, LightTexture.sky(packed) << 4);
		}

		public VBODrawCommand setLightOffset(float lightOffsetX, float lightOffsetY) {
			this.lightOffsetX = lightOffsetX;
			this.lightOffsetY = lightOffsetY;
			return this;
		}

		@SuppressWarnings("deprecation")
		public void draw() {
			Minecraft.getInstance().getTextureManager().bind(texLocation);

			RenderSystem.color4f(red, green, blue, alpha);
			if (textureOffsetX != 0.0F || textureOffsetY != 0.0F) {
				RenderSystem.matrixMode(GL21.GL_TEXTURE);
				RenderSystem.pushMatrix();
				RenderSystem.translatef(textureOffsetX, textureOffsetY, 0.0F);
				RenderSystem.matrixMode(GL21.GL_MODELVIEW);
			}
			RenderSystem.glMultiTexCoord2f(GL21.GL_TEXTURE1, overlayOffsetX, overlayOffsetY);
			RenderSystem.glMultiTexCoord2f(GL21.GL_TEXTURE2, lightOffsetX, lightOffsetY);

			vbo.bind();
			format.setupBufferState(0);
			vbo.draw(matrix, GL21.GL_TRIANGLES);
			format.clearBufferState();
			VertexBuffer.unbind();

			if (textureOffsetX != 0.0F || textureOffsetY != 0.0F) {
				RenderSystem.matrixMode(GL21.GL_TEXTURE);
				RenderSystem.popMatrix();
				RenderSystem.matrixMode(GL21.GL_MODELVIEW);
			}
		}

	}

	private static final VBOBatcher INSTANCE = new VBOBatcher();
	private final Map<RenderType, List<VBODrawCommand>> batchedDrawCommands = new HashMap<>();

	public static VBOBatcher getInstance() {
		return INSTANCE;
	}

	public void queue(RenderType renderType, VBODrawCommand drawCommand) {
		batchedDrawCommands.computeIfAbsent(renderType, k -> new ArrayList<>()).add(drawCommand);
	}

	public void endBatches() {
		batchedDrawCommands.forEach((renderType, drawCommands) -> {
			renderType.setupRenderState();
			RenderSystem.enableTexture();

			for (VBODrawCommand drawCommand : drawCommands) {
				drawCommand.draw();
			}

			renderType.clearRenderState();
			drawCommands.clear();
		});
	}

}
