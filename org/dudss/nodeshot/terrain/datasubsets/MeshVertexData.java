package org.dudss.nodeshot.terrain.datasubsets;

/**
 * An object wrapper enclosing {@link Mesh} vertices and indices data arrays.
 */
public class MeshVertexData {
	float[] verts;
	short[] indices;
	
	public MeshVertexData(float[] verts, short[] indices) {
		this.verts = verts;
		this.indices = indices;
	}
	
	public float[] getVerts() {
		return verts;
	}
	
	public void setVerts(float[] verts) {
		this.verts = verts;
	}
	
	public short[] getIndices() {
		return indices;
	}
	
	public void setIndices(short[] indices) {
		this.indices = indices;
	}
}
