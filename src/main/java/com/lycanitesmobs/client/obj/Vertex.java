package com.lycanitesmobs.client.obj;

import net.minecraft.world.phys.Vec2;
import com.mojang.math.Vector3f;

public class Vertex {
    private Vector3f pos;
    private Vec2 texCoords;
    private Vector3f normal;
    private Vector3f tangent;

    public Vertex(Vector3f pos, Vec2 texCoords, Vector3f normal, Vector3f tangent) {
        this.pos = pos;
        this.texCoords = texCoords;
        this.normal = normal;
        this.tangent = tangent;
    }
    
    public Vector3f getPos() {
        return this.pos;
    }
    
    public Vec2 getTexCoords() {
        return this.texCoords;
    }

    /** Returns per vertex normal for smoother shading. **/
    public Vector3f getNormal() {
        return this.normal;
    }
    
    public Vector3f getTangent() {
        return tangent;
    }
}
