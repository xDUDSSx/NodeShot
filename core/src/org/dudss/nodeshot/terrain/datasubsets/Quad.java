package org.dudss.nodeshot.terrain.datasubsets;

import com.badlogic.gdx.graphics.g2d.Batch;

/**Holds vertex data of a single quad (rectangle)*/
public class Quad {
	float[] vertices;
	
	public Quad(float[] verts) {
		vertices = new float[20];
		
		vertices[Batch.X1] = verts[Batch.X1];
		vertices[Batch.Y1] = verts[Batch.Y1];
		vertices[Batch.C1] = verts[Batch.C1];
		vertices[Batch.U1] = verts[Batch.U1];
		vertices[Batch.V1] = verts[Batch.V1];
                                            
		vertices[Batch.X2] = verts[Batch.X2];
		vertices[Batch.Y2] = verts[Batch.Y2];
		vertices[Batch.C2] = verts[Batch.C2];
		vertices[Batch.U2] = verts[Batch.U2];
		vertices[Batch.V2] = verts[Batch.V2];
                                            
		vertices[Batch.X3] = verts[Batch.X3];
		vertices[Batch.Y3] = verts[Batch.Y3];
		vertices[Batch.C3] = verts[Batch.C3];
		vertices[Batch.U3] = verts[Batch.U3];
		vertices[Batch.V3] = verts[Batch.V3];
                                            
		vertices[Batch.X4] = verts[Batch.X4];
		vertices[Batch.Y4] = verts[Batch.Y4];
		vertices[Batch.C4] = verts[Batch.C4];
		vertices[Batch.U4] = verts[Batch.U4];
		vertices[Batch.V4] = verts[Batch.V4];
	}
	
	public float[] getVertices() {
		return vertices;
	}
}
