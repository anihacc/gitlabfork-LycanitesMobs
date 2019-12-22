package com.lycanitesmobs.client.obj;


import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;

public abstract class Model
{

    private String id;

    public abstract void render(IVertexBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness);
    
    public abstract void renderGroups(IVertexBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness, String s);
    
    public void setID(String id)
    {
        this.id = id;
    }
    
    public String getID()
    {
        return id;
    }
}
