package com.lycanitesmobs.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

public class BaseGui extends AbstractGui {
    public int zLevel = 0;

    public BaseGui(ITextComponent screenName) {
        super();
    }

    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
        this.drawTexturedModalRect(x, y, u, v, width, height, 1);
    }

    public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int resolution) {
        float scaleX = 0.00390625F * resolution;
        float scaleY = scaleX;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel) // pos()
                .tex(((float)(u + 0) * scaleX), ((float)(v + height) * scaleY)).endVertex(); // tex()
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel)
                .tex(((float)(u + width) * scaleX), ((float)(v + height) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel)
                .tex(((float)(u + width) * scaleX), ((float)(v + 0) * scaleY)).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel)
                .tex(((float)(u + 0) * scaleX), ((float)(v + 0) * scaleY)).endVertex();
        tessellator.draw();
    }

    public static void renderLivingEntity(int x, int y, float scale, float lookX, float lookY, LivingEntity entity) {
        float lookXRot = (float)Math.atan((double)(lookX / 40.0F));
        float lookYRot = (float)Math.atan((double)(lookY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)x, (float)y, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale(scale, scale, scale);
        Quaternion modelRotationRoll = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion modelRotationPitch = Vector3f.XP.rotationDegrees(lookYRot * 20.0F);
        modelRotationRoll.multiply(modelRotationPitch);
        matrixStack.rotate(modelRotationRoll);
        matrixStack.rotate(Vector3f.YN.rotationDegrees(180.0F));
        float renderYawOffset = entity.renderYawOffset;
        float rotationYaw = entity.rotationYaw;
        float rotationPitch = entity.rotationPitch;
        float prevRotationYawHead = entity.prevRotationYawHead;
        float rotationYawHead = entity.rotationYawHead;
        entity.renderYawOffset = lookXRot * 20.0F;
        entity.rotationYaw = lookXRot * 40.0F;
        entity.rotationPitch = -lookYRot * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
        modelRotationPitch.conjugate();
        renderManager.setCameraOrientation(modelRotationPitch);
        renderManager.setRenderShadow(false);
        IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        renderManager.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, renderTypeBuffer, 15728880);
        renderTypeBuffer.finish();
        renderManager.setRenderShadow(true);
        entity.renderYawOffset = renderYawOffset;
        entity.rotationYaw = rotationYaw;
        entity.rotationPitch = rotationPitch;
        entity.prevRotationYawHead = prevRotationYawHead;
        entity.rotationYawHead = rotationYawHead;
        RenderSystem.popMatrix();
    }
}
