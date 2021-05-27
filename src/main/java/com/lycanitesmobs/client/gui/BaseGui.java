package com.lycanitesmobs.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;

public abstract class BaseGui extends AbstractGui {
    public BaseGui(ITextComponent screenName) {
        super();
    }

    public static void renderLivingEntity(MatrixStack matrixStack, int x, int y, float scale, float lookX, float lookY, LivingEntity entity) {
        float lookXRot = (float)Math.atan((double)(lookX / 40.0F));
        float lookYRot = (float)Math.atan((double)(lookY / 40.0F));
        matrixStack.push();
        matrixStack.translate((float)x, (float)y, 1500.0F);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
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
        entity.setOnGround(true);

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
        matrixStack.pop();
    }
}
