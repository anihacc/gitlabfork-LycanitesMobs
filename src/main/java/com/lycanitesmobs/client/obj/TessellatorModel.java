package com.lycanitesmobs.client.obj;

import com.google.common.collect.ImmutableList;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author jglrxavpok
 */
public class TessellatorModel extends ObjModel
{
	public static VertexFormat VERTEX_FORMAT;

    //public static final EventBus MODEL_RENDERING_BUS = new EventBus();

    public TessellatorModel(ResourceLocation resourceLocation)
    {
        super(resourceLocation.getPath());
        String path = resourceLocation.toString();
        try
        {
			InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(resourceLocation).getInputStream();
            String content = new String(read(inputStream), "UTF-8");
            String startPath = path.substring(0, path.lastIndexOf('/') + 1);
            HashMap<ObjObject, IndexedModel> map = new OBJLoader().loadModel(startPath, content);
            objObjects.clear();
            Set<ObjObject> keys = map.keySet();
            Iterator<ObjObject> it = keys.iterator();
            while(it.hasNext())
            {
                ObjObject object = it.next();
                Mesh mesh = new Mesh();
                object.mesh = mesh;
                objObjects.add(object);
                map.get(object).toMesh(mesh);
            }
        }
        catch(Exception e)
        {
			LycanitesMobs.logWarning("", "Unable to load model: " + resourceLocation);
            e.toString();
        }
    }


    @Override
    public void renderImpl()
    {
        Collections.sort(objObjects, (a, b) -> {
			Vec3d v = Minecraft.getInstance().getRenderViewEntity().getPositionVector();
			double aDist = v.distanceTo(new Vec3d(a.center.getX(), a.center.getY(), a.center.getZ()));
			double bDist = v.distanceTo(new Vec3d(b.center.getX(), b.center.getY(), b.center.getZ()));
			return Double.compare(aDist, bDist);
		});
        for(ObjObject object : objObjects)
        {
            renderGroup(object);
        }
    }


    @Override
    public void renderGroupsImpl(String group)
    {
        for(ObjObject object : objObjects)
        {
            if(object.getName().equals(group))
            {
                renderGroup(object);
            }
        }
    }


    @Override
    public void renderGroupImpl(ObjObject obj, Vector4f color, Vec2f textureOffset) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tess.getBuffer();
        if(obj.mesh == null) {
            return;
        }
		int[] indices = obj.mesh.indices;
		Vertex[] vertices = obj.mesh.vertices;

        // Colors From OBJ:
        //Vector4f color = new Vector4f(1, 1, 1, 1);
        /*if(obj.material != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.material.diffuseTexture);
            color = new Vector3f(
                    obj.material.diffuseColor.x * obj.material.ambientColor.x,
                    obj.material.diffuseColor.y * obj.material.ambientColor.y,
                    obj.material.diffuseColor.z * obj.material.ambientColor.z);
            alpha = obj.material.transparency;
        }*/

		// Get/Create Normals:
		if(obj.mesh.normals == null) {
			obj.mesh.normals = new Vector3f[indices.length];
		}

		// Build Buffer:
		if(VERTEX_FORMAT == null) {
			//VERTEX_FORMAT = new VertexFormat(ImmutableList.builder().add(DefaultVertexFormats.POSITION_3F).add(DefaultVertexFormats.TEX_2F).add(DefaultVertexFormats.COLOR_4UB).add(DefaultVertexFormats.NORMAL_3B).add(DefaultVertexFormats.PADDING_1B).build());
		}
        bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        for(int i = 0; i < indices.length; i += 3) {

        	// Normal:
			Vector3f normal = obj.mesh.normals[i];
			if(normal == null) {
				normal = this.getNormal(vertices[indices[i]].getPos(), vertices[indices[i + 1]].getPos(), vertices[indices[i + 2]].getPos());
				obj.mesh.normals[i] = normal;
			}

            for(int iv = 0; iv < 3; iv++) {
                Vertex v = obj.mesh.vertices[indices[i + iv]];
				bufferBuilder
                        .func_225582_a_(v.getPos().getX(), v.getPos().getY(), v.getPos().getZ()) //pos
                        .func_225583_a_(v.getTexCoords().x + (textureOffset.x * 0.01f), 1f - (v.getTexCoords().y + (textureOffset.y * 0.01f))) //tex
                        .func_227885_a_(color.getX(), color.getY(), color.getZ(), color.getW()) //color
                        //.normal(v.getNormal().getX(), v.getNormal().getY(), v.getNormal().getZ())
						.func_225584_a_(normal.getX(), normal.getY(), normal.getZ()) //normal
                        .endVertex();
            }
        }



        // Draw Buffer:
		tess.draw();
		/*bufferBuilder.finishDrawing();
		if (bufferBuilder.getVertexCount() > 0) {
			VertexFormat vertexformat = bufferBuilder.getVertexFormat();
			int i = vertexformat.getSize() + 1;
			ByteBuffer bytebuffer = bufferBuilder.getByteBuffer();
			List<VertexFormatElement> list = vertexformat.getElements();

			for (int j = 0; j < list.size(); ++j) {
				VertexFormatElement vertexformatelement = list.get(j);
				bytebuffer.position(vertexformat.getOffset(j));
				vertexformatelement.getUsage().preDraw(vertexformat, j, i, bytebuffer);
			}

			GlStateManager.drawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
			int i1 = 0;

			for (int j1 = list.size(); i1 < j1; ++i1) {
				VertexFormatElement vertexformatelement1 = list.get(i1);
				vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, bytebuffer);
			}
		}
		bufferBuilder.reset();*/
    }


    @Override
    public boolean fireEvent(ObjEvent event) {
        /*Event evt = null;
        if(event.type == ObjEvent.EventType.PRE_RENDER_GROUP)
        {
            evt = new TessellatorModelEvent.RenderGroupEvent.Pre(((ObjObject) event.data[1]).getName(), this);
        }
        else if(event.type == ObjEvent.EventType.POST_RENDER_GROUP)
        {
            evt = new TessellatorModelEvent.RenderGroupEvent.Post(((ObjObject) event.data[1]).getName(), this);
        }
        else if(event.type == ObjEvent.EventType.PRE_RENDER_ALL)
        {
            evt = new TessellatorModelEvent.RenderPre(this);
        }
        else if(event.type == ObjEvent.EventType.POST_RENDER_ALL)
        {
            evt = new TessellatorModelEvent.RenderPost(this);
        }
        if(evt != null)
            return !MODEL_RENDERING_BUS.post(evt);*/
        return true;
    }

	public Vector3f getNormal(Vector3f p1, Vector3f p2, Vector3f p3) {
		Vector3f output = new Vector3f();

		// Calculate Edges:
		Vector3f calU = new Vector3f(p2.getX() - p1.getX(), p2.getY() - p1.getY(), p2.getZ() - p1.getZ());
		Vector3f calV = new Vector3f(p3.getX() - p1.getX(), p3.getY() - p1.getY(), p3.getZ() - p1.getZ());

		// Cross Edges
		output.set(
				calU.getY() * calV.getZ() - calU.getZ() * calV.getY(),
				calU.getZ() * calV.getX() - calU.getX() * calV.getZ(),
				calU.getX() * calV.getY() - calU.getY() * calV.getX()
		);

		output.func_229194_d_(); // normalize()
		return output;
	}
}
