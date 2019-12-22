package com.lycanitesmobs.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;

public class Animator {
	public MatrixStack matrixStack;

	public void doAngle(float rotation, float angleX, float angleY, float angleZ) {
		this.matrixStack.func_227863_a_(new Vector3f(angleX, angleY, angleZ).func_229187_a_(rotation));
	}
	public void doRotate(float rotX, float rotY, float rotZ) {
		this.matrixStack.func_227863_a_(new Vector3f(1F, 0F, 0F).func_229187_a_(rotX));
		this.matrixStack.func_227863_a_(new Vector3f(0F, 1F, 0F).func_229187_a_(rotY));
		this.matrixStack.func_227863_a_(new Vector3f(0F, 0F, 1F).func_229187_a_(rotZ));
	}
	public void doTranslate(float posX, float posY, float posZ) {
		this.matrixStack.func_227861_a_(posX, posY, posZ); // TODO Translation?
	}
	public void doScale(float scaleX, float scaleY, float scaleZ) {
		this.matrixStack.func_227862_a_(scaleX, scaleY, scaleZ); // TODO Scaling?
	}
}
