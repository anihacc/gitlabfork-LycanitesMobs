package com.lycanitesmobs.client.obj;

import com.lycanitesmobs.LycanitesMobs;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
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
    public void renderImpl(IVertexBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness)
    {
        Collections.sort(objObjects, (a, b) -> {
			Vec3d v = Minecraft.getInstance().getRenderViewEntity().getPositionVector();
			double aDist = v.distanceTo(new Vec3d(a.center.getX(), a.center.getY(), a.center.getZ()));
			double bDist = v.distanceTo(new Vec3d(b.center.getX(), b.center.getY(), b.center.getZ()));
			return Double.compare(aDist, bDist);
		});
        for(ObjObject object : objObjects)
        {
            renderGroup(vertexBuilder, matrix3f, matrix4f, brightness, object);
        }
    }


    @Override
    public void renderGroupsImpl(IVertexBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness, String group)
    {
        for(ObjObject object : objObjects)
        {
            if(object.getName().equals(group))
            {
                renderGroup(vertexBuilder, matrix3f, matrix4f, brightness, object);
            }
        }
    }


    @Override
    public void renderGroupImpl(IVertexBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness, ObjObject obj, Vector4f color, Vec2f textureOffset) {

		// Mesh data:
		if(obj.mesh == null) {
			return;
		}
		int[] indices = obj.mesh.indices;
		Vertex[] vertices = obj.mesh.vertices;
		if(obj.mesh.normals == null) {
			obj.mesh.normals = new Vector3f[indices.length];
		}

    	// New Builder:
    	if(vertexBuilder instanceof BufferBuilder) {
			for(int i = 0; i < indices.length; i += 3) {

				// Normal:
				Vector3f normal = obj.mesh.normals[i];
				if(normal == null) {
					normal = this.getNormal(vertices[indices[i]].getPos(), vertices[indices[i + 1]].getPos(), vertices[indices[i + 2]].getPos());
					obj.mesh.normals[i] = normal;
				}

				for(int iv = 0; iv < 3; iv++) {
					Vertex v = obj.mesh.vertices[indices[i + iv]];
					vertexBuilder
							.func_227888_a_(matrix4f, v.getPos().getX(), v.getPos().getY(), v.getPos().getZ()) //pos
							.func_227885_a_(color.getX(), color.getY(), color.getZ(), color.getW()) //color
							.func_225583_a_(v.getTexCoords().x + (textureOffset.x * 0.01f), 1f - (v.getTexCoords().y + (textureOffset.y * 0.01f))) //uv
							.func_225585_a_(0, 10) //color fade
							.func_227886_a_(brightness) //brightness 240 = full
							.func_227887_a_(matrix3f, normal.getX(), normal.getY(), normal.getZ()) //normal
							.endVertex();
				}
			}

			return;
		}

    	// Fallback Builder:
        Tessellator tess = Tessellator.getInstance();
		vertexBuilder = tess.getBuffer();
		if (VERTEX_FORMAT == null) {
			VERTEX_FORMAT = DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL;
			//VERTEX_FORMAT = new VertexFormat(ImmutableList.builder().add(DefaultVertexFormats.POSITION_3F).add(DefaultVertexFormats.TEX_2F).add(DefaultVertexFormats.COLOR_4UB).add(DefaultVertexFormats.NORMAL_3B).add(DefaultVertexFormats.PADDING_1B).build());
		}
		((BufferBuilder)vertexBuilder).begin(GL11.GL_TRIANGLES, VERTEX_FORMAT);

        for(int i = 0; i < indices.length; i += 3) {

        	// Normal:
			Vector3f normal = obj.mesh.normals[i];
			if(normal == null) {
				normal = this.getNormal(vertices[indices[i]].getPos(), vertices[indices[i + 1]].getPos(), vertices[indices[i + 2]].getPos());
				obj.mesh.normals[i] = normal;
			}

            for(int iv = 0; iv < 3; iv++) {
                Vertex v = obj.mesh.vertices[indices[i + iv]];
				vertexBuilder
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
    }


    @Override
    public boolean fireEvent(ObjEvent event) {
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
