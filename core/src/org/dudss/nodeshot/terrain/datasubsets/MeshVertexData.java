package org.dudss.nodeshot.terrain.datasubsets;

import com.badlogic.gdx.graphics.Mesh;

/**
 * An object wrapper enclosing {@link Mesh} vertices and indices data arrays.
 */
public class MeshVertexData {
	float[] verts;
	short[] indices;
	
	/**An object wrapper enclosing {@link Mesh} vertices and indices data arrays.
	 * @param verts Mesh vertices.
	 * @param indices Mesh indices.
	 * */
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
