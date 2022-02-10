package com.lycanitesmobs.client.obj;

import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL46;

import com.lycanitesmobs.client.renderer.CustomRenderStates;
import com.lycanitesmobs.client.renderer.VBOBatcher;
import com.lycanitesmobs.client.renderer.VBOBatcher.VBODrawCommand;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector4f;

public class VBOObjModel extends ObjModel {

	public static RenderType renderType;
	public static ResourceLocation tex;

	public VBOObjModel(ResourceLocation resourceLocation) {
		super(resourceLocation);
	}

	@Override
	public void renderPart(IVertexBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness, int fade, ObjPart objPart, Vector4f color, Vector2f textureOffset) {
		VBOBatcher.getInstance().queue(renderType, 
				new VBODrawCommand(objPart.mesh.getVbo(), CustomRenderStates.POS_TEX_NORMAL, matrix4f, tex)
						.setColor(color)
						.setTextureOffset(textureOffset)
						.setOverlayOffset(0.0F, 10.0F - fade)
						.setLightOffset(brightness));
		/*
		RenderSystem.enableRescaleNormal();

		RenderSystem.pushMatrix();
		RenderSystem.multMatrix(matrix4f);
		RenderSystem.color4f(color.x(), color.y(), color.z(), color.w());
		if (textureOffset.x != 0.0F || textureOffset.y != 0.0F) {
			RenderSystem.matrixMode(GL21.GL_TEXTURE);
			RenderSystem.pushMatrix();
			RenderSystem.translatef(textureOffset.x * 0.01F, -textureOffset.y * 0.01F, 0.0F);
			RenderSystem.matrixMode(GL21.GL_MODELVIEW);
		}
		RenderSystem.glMultiTexCoord2f(GL21.GL_TEXTURE1, 0.0F, 10 - fade);
		RenderSystem.glMultiTexCoord2f(GL21.GL_TEXTURE2, LightTexture.block(brightness) << 4, LightTexture.sky(brightness) << 4);
		* /

		GL21.glPushMatrix();
		GlStateManager._multMatrix(matrix4f);
		GL21.glColor4f(color.x(), color.y(), color.z(), color.w());

		GL21.glBindBuffer(GL21.GL_ARRAY_BUFFER, objPart.mesh.getVbo());
		GL21.glVertexPointer(3, GL21.GL_FLOAT, 28, 0);
		GL21.glTexCoordPointer(2, GL21.GL_FLOAT, 28, 12);
		GL21.glNormalPointer(GL21.GL_BYTE, 28, 24);
		GL21.glEnableClientState(GL21.GL_VERTEX_ARRAY);
		GL21.glEnableClientState(GL21.GL_TEXTURE_COORD_ARRAY);
		GL21.glEnableClientState(GL21.GL_NORMAL_ARRAY);

		GL21.glDrawArrays(GL21.GL_TRIANGLES, 0, objPart.mesh.indices.length);

		GL21.glDisableClientState(GL21.GL_NORMAL_ARRAY);
		GL21.glDisableClientState(GL21.GL_TEXTURE_COORD_ARRAY);
		GL21.glDisableClientState(GL21.GL_VERTEX_ARRAY);
		GL21.glBindBuffer(GL21.GL_ARRAY_BUFFER, 0);

		GL21.glPopMatrix();

		/*
		if (textureOffset.x != 0.0F || textureOffset.y != 0.0F) {
			RenderSystem.matrixMode(GL21.GL_TEXTURE);
			RenderSystem.popMatrix();
			RenderSystem.matrixMode(GL21.GL_MODELVIEW);
		}
		RenderSystem.popMatrix();

		RenderSystem.disableRescaleNormal();
		*/
	}

}
