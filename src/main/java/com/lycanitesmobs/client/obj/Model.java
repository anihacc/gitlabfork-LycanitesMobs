package com.lycanitesmobs.client.obj;


import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

public abstract class Model
{


    public abstract void render(VertexConsumer vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness);
    
    public abstract void renderGroups(VertexConsumer vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness, String s);

}
