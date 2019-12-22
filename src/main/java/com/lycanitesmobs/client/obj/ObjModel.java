package com.lycanitesmobs.client.obj;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.util.math.Vec2f;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class ObjModel extends Model
{

    public List<ObjObject> objObjects;

    protected String filename;
    
    ObjModel() {
        objObjects = new ArrayList<>();
    }
    
    public ObjModel(String classpathElem) {
        this();
        this.filename = classpathElem;
        if(filename.contains("/"))
            setID(filename.substring(filename.lastIndexOf("/")+1));
        else
            setID(filename);
    }
    
    protected byte[] read(InputStream resource) throws IOException {
        int i;
        byte[] buffer = new byte[65565];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((i = resource.read(buffer, 0, buffer.length)) != -1)
        {
            out.write(buffer,0,i);
        }
        out.flush();
        out.close();
        return out.toByteArray();
    }

    public void renderGroup(IVertexBuilder vertexBuilder, ObjObject group) {
        this.renderGroup(vertexBuilder, group, new Vector4f(1, 1, 1, 1), new Vec2f(0, 0));
    }

    public void renderGroup(IVertexBuilder vertexBuilder, ObjObject group, Vector4f color, Vec2f textureOffset) {
        //if(fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_GROUP).setData(group, group)))
            this.renderGroupImpl(vertexBuilder, group, color, textureOffset);
        //fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_GROUP).setData(group, group));
    }
    
    public void renderGroups(IVertexBuilder vertexBuilder, String groupsName) {
        if(fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_GROUPS).setData(groupsName)))
            this.renderGroupsImpl(vertexBuilder, groupsName);
        fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_GROUPS).setData(groupsName));
    }
    
    public void render(IVertexBuilder vertexBuilder) {
        if(fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_ALL)))
            this.renderImpl(vertexBuilder);
        fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_ALL));
    }
    
    protected abstract void renderGroupsImpl(IVertexBuilder vertexBuilder, String groupsName);
    
    protected abstract void renderGroupImpl(IVertexBuilder vertexBuilder, ObjObject objGroup, Vector4f color, Vec2f textureOffset);

    protected abstract void renderImpl(IVertexBuilder vertexBuilder);
    
    public abstract boolean fireEvent(ObjEvent event);
}
