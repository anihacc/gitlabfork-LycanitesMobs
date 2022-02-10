package com.lycanitesmobs.client.obj;

import org.lwjgl.opengl.GL21;

import com.lycanitesmobs.client.renderer.CustomRenderStates;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.math.vector.Vector3f;

public class Mesh {

	public int[] indices;
	public Vertex[] vertices;
	public Vector3f[] normals;
	private VertexBuffer vbo;

	public VertexBuffer getVbo() {
		if (this.vbo == null) {
			if (this.normals == null) {
				this.normals = new Vector3f[this.indices.length];
				for (int i = 0; i < this.normals.length / 3; i++) {
					Vector3f v1 = this.vertices[this.indices[i * 3]].getPos();
					Vector3f v2 = this.vertices[this.indices[i * 3 + 1]].getPos();
					Vector3f v3 = this.vertices[this.indices[i * 3 + 2]].getPos();
					Vector3f normal = calcNormal(v1, v2, v3);
					this.normals[i * 3] = normal;
					this.normals[i * 3 + 1] = normal;
					this.normals[i * 3 + 2] = normal;
				}
			}

			Tessellator tesselator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tesselator.getBuilder();

			bufferBuilder.begin(GL21.GL_TRIANGLES, CustomRenderStates.POS_TEX_NORMAL);
			for (int i = 0; i < this.indices.length; i++) {
				Vertex vertex = this.vertices[this.indices[i]];
				Vector3f normal = this.normals[i];
				bufferBuilder.vertex(vertex.getPos().x(), vertex.getPos().y(), vertex.getPos().z());
				bufferBuilder.uv(vertex.getTexCoords().x, 1.0F - vertex.getTexCoords().y);
				bufferBuilder.uv2(0, 0);
				bufferBuilder.normal(normal.x(), normal.y(), normal.z());
				bufferBuilder.endVertex();
			}
			bufferBuilder.end();

			this.vbo = new VertexBuffer(CustomRenderStates.POS_TEX_NORMAL);
			this.vbo.upload(bufferBuilder);
		}

		return this.vbo;
	}

	private static Vector3f calcNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
		Vector3f output = new Vector3f();

		// Calculate Edges:
		Vector3f calU = new Vector3f(v2.x() - v1.x(), v2.y() - v1.y(), v2.z() - v1.z());
		Vector3f calV = new Vector3f(v3.x() - v1.x(), v3.y() - v1.y(), v3.z() - v1.z());

		// Cross Edges
		output.setX(calU.y() * calV.z() - calU.z() * calV.y());
		output.setY(calU.z() * calV.x() - calU.x() * calV.z());
		output.setZ(calU.x() * calV.y() - calU.y() * calV.x());

		output.normalize();
		return output;
	}

	public void delete() {
		this.vbo.close();
		this.vbo = null;
	}

}
