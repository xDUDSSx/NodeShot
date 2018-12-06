package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.datasubsets.MeshVertexData;

import com.badlogic.gdx.graphics.Mesh;

/**Represents a rectangular grid of {@link Chunk}s with a fixed size ({@link Base#SECTION_SIZE}).
 * It also holds terrain and corruption {@link Mesh} data for this particular map section.*/
public class Section {
	Chunk[][] sectionChunks;
	
	int size = Base.SECTION_SIZE;
	 
	/**Corruption update optimisation*/
	boolean active = false;

	boolean creeperUpdateOccured = false;
	
	public Section[] neighbours;
	
	Mesh terrainMesh;
	MeshVertexData terrainVertexData;
	boolean terrainUpdate = false;
	
	Mesh corrMesh;
	MeshVertexData corrVertexData;
	boolean corrUpdate = false;
 	
	Mesh fogMesh;
	MeshVertexData fogVertexData;
	boolean fogUpdate = false;
	
	/**Section is an object representing a square grid of chunks with a fixed size.
	 * It also holds vertex info about under laying chunk terrain and corruption.
	 * @param chunks {@linkplain Chunk}s that form this {@linkplain Section}. The array size must be the same as {@link Base#SECTION_SIZE}.
	 */
	public Section(Chunk[][] chunks) {
		sectionChunks = chunks;
		
		terrainVertexData = new MeshVertexData(null, null);
		corrVertexData = new MeshVertexData(null, null);
		fogVertexData = new MeshVertexData(null, null);
	}
	
	/**@param x coordinate
	 * @param y coordinate
	 * @return A {@link Chunk} at coordinates relative to the bottom left {@linkplain Section} corner
	 * */
	public Chunk getChunk(int x, int y) {
		return sectionChunks[x][y];
	}
	
	/**Sets the section chunks array*/
	public void setChunks(Chunk[][] chunks) {
		sectionChunks = chunks;
	}
	
	/**Sends an update call to every {@link Chunk} in this section. Also determines if this section needs further updates.
	 * @since <b>v5.1</b> (3.12.18) Part of the corruption optimisation update.*/
	public void updateAll() {
		creeperUpdateOccured = false;
		for (int x = 0; x < Base.SECTION_SIZE; x++) {
			for (int y = 0; y < Base.SECTION_SIZE; y++) {
				boolean b = sectionChunks[x][y].update();
				if (b && !creeperUpdateOccured) {
					creeperUpdateOccured = b;
				}
			}
		}
		if (!creeperUpdateOccured) {
			setActive(false);
			//BaseClass.logger.info("Section update deactivation");
		}
	}
	
	/**Calls the {@link Chunk#applyUpdate()} method for every {@link Chunk} in the section.*/
	public void applyUpdates() {
		for (int x = 0; x < Base.SECTION_SIZE; x++) {
			for (int y = 0; y < Base.SECTION_SIZE; y++) {
				sectionChunks[x][y].applyUpdate();
			}
		}
	}
	
	public void updateNeighbours() {
		int ax = this.sectionChunks[0][0].ax/Base.SECTION_SIZE;
		int ay = this.sectionChunks[0][0].ay/Base.SECTION_SIZE;
		
		neighbours = new Section[8];
		neighbours[0] = GameScreen.chunks.getSection(ax, ay + 1);
		neighbours[1] = GameScreen.chunks.getSection(ax + 1, ay + 1);
		neighbours[2] = GameScreen.chunks.getSection(ax + 1, ay);
		neighbours[3] = GameScreen.chunks.getSection(ax + 1, ay - 1);
		neighbours[4] = GameScreen.chunks.getSection(ax, ay - 1);
		neighbours[5] = GameScreen.chunks.getSection(ax - 1, ay - 1);
		neighbours[6] = GameScreen.chunks.getSection(ax - 1, ay);
		neighbours[7] = GameScreen.chunks.getSection(ax + 1, ay - 1);
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
	
	/**Requests a corruption mesh update from the OpenGL draw thread.
	 * The mesh cannot be updated directly because OpenGL context is single-threaded.*/
	public void requestCorruptionUpdate() {
		corrUpdate = true;
	}
	
	/**@return Whether the corruption mesh needs update.*/
	public boolean needsCorruptionMeshUpdate() {
		return corrUpdate;
	}
	
	/**Updates the {@linkplain Section} FogOfWar mesh data. Can be called from other threads. 
	 * The actual FogOfWar mesh will not be updated directly. Use in combination with {@link #requestFogOfWarUpdate()}
	 * to request FogOfWar mesh update from the OpenGL draw thread.
	 * */
	public void updateFogOfWarMesh(float[] verts, short[] indices) {
		this.fogVertexData.setVerts(verts);
		this.fogVertexData.setIndices(indices);	
	}
	
	public float[] getFogOfWarVerts() {
		return fogVertexData.getVerts();
	}
	
	public short[] getFogOfWarIndices() {
		return fogVertexData.getIndices();
	}
	
	/**@return The sections FogOfWar mesh of this {@link Section}.
	 * */
	public Mesh getFogOfWarMesh() {
		return fogMesh;
	}
	
	/**Nullifies the {@link #requestFogOfWarUpdate()} call. Flags the FogOfWar mesh as updated.*/
	public void updatedFogOfWarMesh() {
		fogUpdate = false;
	}
	
	/**Requests a FogOfWar mesh update from the OpenGL draw thread.
	 * The mesh cannot be updated directly because OpenGL context is single-threaded.*/
	public void requestFogOfWarUpdate() {
		fogUpdate = true;
	}
	
	/**@return Whether the FogOfWar mesh update is requested.*/
	public boolean needsFogOfWarMeshUpdate() {
		return fogUpdate;
	}
	
	/**Corruption update optimisation
	 * @since <b>v5.1</b> (3.12.18) Part of the corruption optimisation update.*/
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**Corruption update optimisation
	 * @since <b>v5.1</b> (3.12.18) Part of the corruption optimisation update.*/
	public boolean isActive() {
		return active;
	}
}
