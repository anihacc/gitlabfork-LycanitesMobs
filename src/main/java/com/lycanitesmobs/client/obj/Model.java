package com.lycanitesmobs.client.obj;


import com.mojang.blaze3d.vertex.IVertexBuilder;

public abstract class Model
{

    private String id;

    public abstract void render(IVertexBuilder vertexBuilder);
    
    public abstract void renderGroups(IVertexBuilder vertexBuilder, String s);
    
    public void setID(String id)
    {
        this.id = id;
    }
    
    public String getID()
    {
        return id;
    }
}
