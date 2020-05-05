package com.lycanitesmobs.client.obj;


import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;

public abstract class Model
{


    public abstract void render(BufferBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness);
    
    public abstract void renderGroups(BufferBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness, String s);

}
