package com.lycanitesmobs.client.obj;

import com.lycanitesmobs.LycanitesMobs;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ObjModel {
	public String filename;
	public List<ObjPart> objParts = new ArrayList<>();

    public ObjModel(ResourceLocation resourceLocation) {
		this.filename = resourceLocation.getPath();
        String path = resourceLocation.toString();
        try {
			InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(resourceLocation).getInputStream();
            String content = new String(read(inputStream), "UTF-8");
            String startPath = path.substring(0, path.lastIndexOf('/') + 1);
            HashMap<ObjPart, IndexedModel> map = new OBJLoader().loadModel(startPath, content);
			this.objParts.clear();
            Set<ObjPart> keys = map.keySet();
            Iterator<ObjPart> it = keys.iterator();
            while(it.hasNext()) {
                ObjPart objPart = it.next();
                Mesh mesh = new Mesh();
                objPart.mesh = mesh;
                this.objParts.add(objPart);
                map.get(objPart).toMesh(mesh);
            }
        }
        catch(Exception e) {
			LycanitesMobs.logWarning("", "Unable to load model: " + resourceLocation);
            e.toString();
        }
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

    public void renderAll(IVertexBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness, Vector4f color, Vec2f textureOffset) {
        Collections.sort(this.objParts, (a, b) -> {
			Vec3d v = Minecraft.getInstance().getRenderViewEntity().getPositionVector();
			double aDist = v.distanceTo(new Vec3d(a.center.getX(), a.center.getY(), a.center.getZ()));
			double bDist = v.distanceTo(new Vec3d(b.center.getX(), b.center.getY(), b.center.getZ()));
			return Double.compare(aDist, bDist);
		});
        for(ObjPart objPart : this.objParts) {
            this.renderPart(vertexBuilder, matrix3f, matrix4f, brightness, objPart, color, textureOffset);
        }
    }

    public void renderPartGroup(IVertexBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness, Vector4f color, Vec2f textureOffset, String group) {
        for(ObjPart objPart : this.objParts) {
            if(objPart.getName().equals(group)) {
                renderPart(vertexBuilder, matrix3f, matrix4f, brightness, objPart, color, textureOffset);
            }
        }
    }

    public void renderPart(IVertexBuilder vertexBuilder, Matrix3f matrix3f, Matrix4f matrix4f, int brightness, ObjPart objPart, Vector4f color, Vec2f textureOffset) {
		// Mesh data:
		if(objPart.mesh == null) {
			return;
		}
		int[] indices = objPart.mesh.indices;
		Vertex[] vertices = objPart.mesh.vertices;
		if(objPart.mesh.normals == null) {
			objPart.mesh.normals = new Vector3f[indices.length];
		}

    	// New Builder:
    	if(vertexBuilder instanceof BufferBuilder) {
			for(int i = 0; i < indices.length; i += 3) {

				// Normal:
				Vector3f normal = objPart.mesh.normals[i];
				if(normal == null) {
					normal = this.getNormal(vertices[indices[i]].getPos(), vertices[indices[i + 1]].getPos(), vertices[indices[i + 2]].getPos());
					objPart.mesh.normals[i] = normal;
				}

				for(int iv = 0; iv < 3; iv++) {
					Vertex v = objPart.mesh.vertices[indices[i + iv]];
					Vector4f vertexPos = new Vector4f(v.getPos().getX(), v.getPos().getY(), v.getPos().getZ(), 1.0F);
					vertexPos.func_229372_a_(matrix4f);
					vertexBuilder
							.func_225582_a_(vertexPos.getX(), vertexPos.getY(), vertexPos.getZ()) //pos
							.func_227885_a_(color.getX(), color.getY(), color.getZ(), color.getW()) //color
							.func_225583_a_(v.getTexCoords().x + (textureOffset.x * 0.01f), 1f - (v.getTexCoords().y + (textureOffset.y * 0.01f))) //texture
							.func_225585_a_(0, 10) //color fade
							.func_227886_a_(brightness) //brightness 240 = full
							.func_227887_a_(matrix3f, normal.getX(), normal.getY(), normal.getZ()) //normal
							.endVertex();
				}
			}

			return;
		}

    	/*/ Fallback Builder:
        Tessellator tess = Tessellator.getInstance();
		vertexBuilder = tess.getBuffer();
		((BufferBuilder)vertexBuilder).begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

        for(int i = 0; i < indices.length; i += 3) {

        	// Normal:
			Vector3f normal = objPart.mesh.normals[i];
			if(normal == null) {
				normal = this.getNormal(vertices[indices[i]].getPos(), vertices[indices[i + 1]].getPos(), vertices[indices[i + 2]].getPos());
				objPart.mesh.normals[i] = normal;
			}

            for(int iv = 0; iv < 3; iv++) {
                Vertex v = objPart.mesh.vertices[indices[i + iv]];
				vertexBuilder
                        .func_225582_a_(v.getPos().getX(), v.getPos().getY(), v.getPos().getZ()) //pos
                        .func_225583_a_(v.getTexCoords().x + (textureOffset.x * 0.01f), 1f - (v.getTexCoords().y + (textureOffset.y * 0.01f))) //tex
                        .func_227885_a_(color.getX(), color.getY(), color.getZ(), color.getW()) //color
						.func_225584_a_(normal.getX(), normal.getY(), normal.getZ()) //normal
                        .endVertex();
            }
        }

        // Draw Buffer:
		tess.draw();*/
    }
}
