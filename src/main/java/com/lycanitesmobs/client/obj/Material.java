package com.lycanitesmobs.client.obj;

import net.minecraft.util.math.vector.Vector3f;

public class Material
{
    
    private String name;
    public Vector3f diffuseColor;
    public Vector3f ambientColor;
    public int ambientTexture;
    public int diffuseTexture;
    public float transparency;

    public Material(String name)
    {
        transparency = 1f;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

}
