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
	
	List<Mesh> corrMeshes;
	List<MeshVertexData> corrVertexData;
	List<Boolean> updates;
 	
	/**Section is an object representing a square grid of chunks with a fixed size.
	 * It also holds vertex info about under laying chunk terrain and corruption.
	 * @param chunks {@linkplain Chunk}s that form this {@linkplain Section}. The array size must be the same as {@link Base#SECTION_SIZE}.
	 */
	public Section(Chunk[][] chunks) {
		sectionChunks = chunks;
		
		terrainVertexData = new MeshVertexData(null, null);
		corrVertexData = new ArrayList<MeshVertexData>();
		corrMeshes = new ArrayList<Mesh>();
		
		for (int i = 0; i < Base.MAX_CREEP; i++) {
			corrMeshes.add(null);
			corrVertexData.add(new MeshVertexData(null, null));
		}
		
		updates = new ArrayList<>(Collections.nCopies(60, false));
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
	
	/**Updates the {@linkplain Section} corruption mesh data of the specified layer. Can be called from other threads. 
	 * The actual corruption mesh will not be updated directly. Use in combination with {@link #requestCorruptionUpdate(int)}
	 * to request corruption mesh update from the OpenGL draw thread.
	 * @param layer Layer of corruption mesh that should be updated.
	 * */
	public void updateCorruptionMesh(int layer, float[] verts, short[] indices) {
		this.corrVertexData.get(layer).setVerts(verts);
		this.corrVertexData.get(layer).setIndices(indices);	
	}
	
	public float[] getCorruptionVerts(int layer) {
		return corrVertexData.get(layer).getVerts();
	}
	
	public short[] getCorruptionIndices(int layer) {
		return corrVertexData.get(layer).getIndices();
	}
	
	/**@return The sections corruption mesh of a particular layer.
	 * @param layer Layer of the corruption mesh
	 * */
	public Mesh getCorruptionMesh(int layer) {
		return corrMeshes.get(layer);
	}
	
	/**Nullifies the {@link #requestCorruptionUpdate(int)} call. Flags the corruption mesh of that particular layer as updated.
	 * @param layer Layer of the corruption mesh
	 * */
	public void updatedCorruptionMesh(int layer) {
		updates.set(layer, false);
	}
	
	/**Requests a corruption mesh update of the particular layer from the OpenGL draw thread.
	 * The mesh cannot be updated directly because OpenGL context is single-threaded.*/
	public void requestCorruptionUpdate(int layer) {
		updates.set(layer, true);
	}
	
	/**@return Whether the corruption mesh update of the particular layer is requested.
	 * @param layer Layer of the corruption mesh*/
	public boolean needsCorruptionMeshUpdate(int layer) {
		return updates.get(layer);
	}
}
