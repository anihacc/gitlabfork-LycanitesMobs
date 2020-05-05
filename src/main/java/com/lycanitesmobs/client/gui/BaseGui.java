package com.lycanitesmobs.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Quaternion;

public class BaseGui extends DrawableHelper {
    public int zLevel = 0;

    public BaseGui(Text screenName) {
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
        vertexbuffer.begin(7, VertexFormats.POSITION_TEXTURE);
        vertexbuffer.vertex((double)(x + 0), (double)(y + height), (double)this.zLevel) // pos()
                .texture(((float)(u + 0) * scaleX), ((float)(v + height) * scaleY)).next(); // tex()
        vertexbuffer.vertex((double)(x + width), (double)(y + height), (double)this.zLevel)
                .texture(((float)(u + width) * scaleX), ((float)(v + height) * scaleY)).next();
        vertexbuffer.vertex((double)(x + width), (double)(y + 0), (double)this.zLevel)
                .texture(((float)(u + width) * scaleX), ((float)(v + 0) * scaleY)).next();
        vertexbuffer.vertex((double)(x + 0), (double)(y + 0), (double)this.zLevel)
                .texture(((float)(u + 0) * scaleX), ((float)(v + 0) * scaleY)).next();
        tessellator.draw();
    }

    public static void renderLivingEntity(int x, int y, float scale, float lookX, float lookY, LivingEntity entity) {
        float lookXRot = (float)Math.atan((double)(lookX / 40.0F));
        float lookYRot = (float)Math.atan((double)(lookY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)x, (float)y, 1500.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.0D, 0.0D, 1000.0D);
        matrixStack.scale(scale, scale, scale);
        Quaternion modelRotationRoll = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion modelRotationPitch = Vector3f.POSITIVE_X.getDegreesQuaternion(lookYRot * 20.0F);
        modelRotationRoll.hamiltonProduct(modelRotationPitch);
        matrixStack.multiply(modelRotationRoll);
        matrixStack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(180.0F));
        float renderYawOffset = entity.bodyYaw;
        float rotationYaw = entity.yaw;
        float rotationPitch = entity.pitch;
        float prevRotationYawHead = entity.prevHeadYaw;
        float rotationYawHead = entity.headYaw;
        entity.bodyYaw = lookXRot * 20.0F;
        entity.yaw = lookXRot * 40.0F;
        entity.pitch = -lookYRot * 20.0F;
        entity.prevHeadYaw = entity.yaw;
        entity.headYaw = entity.yaw;
        EntityRenderDispatcher renderManager = MinecraftClient.getInstance().getEntityRenderManager();
        modelRotationPitch.conjugate();
        renderManager.setRotation(modelRotationPitch);
        renderManager.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        renderManager.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack, immediate, 15728880);
        immediate.draw();
        renderManager.setRenderShadows(true);
        entity.bodyYaw = renderYawOffset;
        entity.yaw = rotationYaw;
        entity.pitch = rotationPitch;
        entity.prevHeadYaw = prevRotationYawHead;
        entity.headYaw = rotationYawHead;
        RenderSystem.popMatrix();
    }
}
