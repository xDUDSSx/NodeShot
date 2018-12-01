package org.dudss.nodeshot.terrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.terrain.datasubsets.MeshVertexData;

import com.badlogic.gdx.graphics.Mesh;

/**Represents a rectangular grid of {@link Chunk}s with a fixed size ({@link Base#SECTION_SIZE}).
 * It also holds terrain and corruption {@link Mesh} data for this particular map section.*/
public class Section {
	Chunk[][] sectionChunks;
	
	int size = Base.SECTION_SIZE;
	 
	boolean full = false;
	
	Mesh terrainMesh;
	MeshVertexData terrainVertexData;
	boolean terrainUpdate = false;
	
	Mesh corrMesh;
	MeshVertexData corrVertexData;
	boolean corrUpdate = false;
 	
	/**Section is an object representing a square grid of chunks with a fixed size.
	 * It also holds vertex info about under laying chunk terrain and corruption.
	 * @param chunks {@linkplain Chunk}s that form this {@linkplain Section}. The array size must be the same as {@link Base#SECTION_SIZE}.
	 */
	public Section(Chunk[][] chunks) {
		sectionChunks = chunks;
		
		terrainVertexData = new MeshVertexData(null, null);
		corrVertexData = new MeshVertexData(null, null);
	}
	
	/**@param x coordinate
	 * @param y coordinate
	 * @return A {@link Chunk} at coordinates relative to the bottom left {@linkplain Section} corner
	 * */
	public Chunk getChunk(int x, int y) {
		return sectionChunks[x][y];
	}
	
	/**Updates the {@linkplain Section} terrain mesh data. Can be called from other threads. 
	 * The actual terrain {@link #terrainMesh} will not be updated directly. Use in combination with {@link #requestTerrainUpdate()}
	 * to request {@link #terrainMesh} update from the OpenGL draw thread.*/
	public void updateTerrainMesh(float[] verts, short[] indices) {
		terrainVertexData.setVerts(verts);
		terrainVertexData.setIndices(indices);	
	}
	
	public float[] getTerrainVerts() {
		return terrainVertexData.getVerts();
	}
	
	public short[] getTerrainIndices() {
		return terrainVertexData.getIndices();
	}
	
	/**@return The sections {@link #terrainMesh}*/
	public Mesh getTerrainMesh() {
		return terrainMesh;
	}
	
	/**Nullifies the {@link #requestTerrainUpdate()} call. Flags the {@link #terrainMesh} as updated*/
	public void updatedTerrain() {
		terrainUpdate = false;
	}
	
	/**Requests {@link #terrainMesh} update from the OpenGL draw thread.
	 * The mesh cannot be updated directly because OpenGL context is single-threaded.*/
	public void requestTerrainUpdate() {
		terrainUpdate = true;
	}
	
	/**@return Whether the {@link #terrainMesh} update is requested*/
	public boolean needsTerrainUpdate() {
		return terrainUpdate;
	}
	
	/**Updates the {@linkplain Section} corruption mesh data. Can be called from other threads. 
	 * The actual corruption mesh will not be updated directly. Use in combination with {@link #requestCorruptionUpdate()}
	 * to request corruption mesh update from the OpenGL draw thread.
	 * */
	public void updateCorruptionMesh(float[] verts, short[] indices) {
		this.corrVertexData.setVerts(verts);
		this.corrVertexData.setIndices(indices);	
	}
	
	public float[] getCorruptionVerts() {
		return corrVertexData.getVerts();
	}
	
	public short[] getCorruptionIndices() {
		return corrVertexData.getIndices();
	}
	
	/**@return The sections corruption mesh of this {@link Section}.
	 * */
	public Mesh getCorruptionMesh() {
		return corrMesh;
	}
	
	/**Nullifies the {@link #requestCorruptionUpdate()} call. Flags the corruption mesh as updated.*/
	public void updatedCorruptionMesh() {
		corrUpdate = false;
	}
	
	/**Requests a corruption mesh update of the particular layer from the OpenGL draw thread.
	 * The mesh cannot be updated directly because OpenGL context is single-threaded.*/
	public void requestCorruptionUpdate() {
		corrUpdate = true;
	}
	
	/**@return Whether the corruption mesh update.*/
	public boolean needsCorruptionMeshUpdate() {
		return corrUpdate;
	}
}
