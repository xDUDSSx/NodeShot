package org.dudss.nodeshot.terrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.terrain.datasubsets.MeshVertexData;

import com.badlogic.gdx.graphics.Mesh;

public class Section {
	Chunk[][] sectionChunks;
	
	int size = Base.SECTION_SIZE;
	 
	boolean full = false;
	
	SubSection sw;
	SubSection se;
	SubSection nw;
	SubSection ne;
	
	Mesh terrainMesh;
	
	float[] terrainVerts;
	short[] terrainIndices;
	
	List<Mesh> corrMeshes;
	boolean terrainUpdate = false;
	
	List<MeshVertexData> corrVertexData;
	List<Boolean> updates;
 	
	/**Section is an object representing a square grid of chunks with a fixed size.
	 * It also holds vertex info about under laying chunk terrain and corruption
	 * @param chunks
	 */
	public Section(Chunk[][] chunks) {
		sectionChunks = chunks;
		
		corrVertexData = new ArrayList<MeshVertexData>();
		corrMeshes = new ArrayList<Mesh>();
		
		for (int i = 0; i < Base.MAX_CREEP; i++) {
			corrMeshes.add(null);
			corrVertexData.add(new MeshVertexData(null, null));
		}
		
		updates = new ArrayList<>(Collections.nCopies(60, false));
		
		Chunk[][] swChunks = new Chunk[size/2][size/2];
		Chunk[][] seChunks = new Chunk[size/2][size/2];
		Chunk[][] nwChunks = new Chunk[size/2][size/2];
		Chunk[][] neChunks = new Chunk[size/2][size/2];
		
		int h = (int)size/2;
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {	
				if (y < size/2 && x < size/2) {
					swChunks[x][y] = sectionChunks[x][y];
				}
				if (y < size/2 && x >= size/2) {
					seChunks[x-h][y] = sectionChunks[x][y];
				}
				if (y >= size/2 && x < size/2) {
					nwChunks[x][y-h] = sectionChunks[x][y];
				}
				if (y >= size/2 && x >= size/2) {
					neChunks[x-h][y-h] = sectionChunks[x][y];
				}
			}
		}
		
		sw = new SubSection(swChunks, size/2);
		se = new SubSection(seChunks, size/2);
		nw = new SubSection(nwChunks, size/2);
		ne = new SubSection(neChunks, size/2);
	}
	
	public Chunk getChunk(int x, int y) {
		return sectionChunks[x][y];
	}
	
	public void updateTerrainMesh(float[] verts, short[] indices) {
		this.terrainVerts = verts;
		this.terrainIndices = indices;		
	}
	
	public float[] getTerrainVerts() {
		return terrainVerts;
	}
	
	public short[] getTerrainIndices() {
		return terrainIndices;
	}
	
	public Mesh getTerrainMesh() {
		return terrainMesh;
	}
	
	public void updatedTerrain() {
		terrainUpdate = false;
	}
	
	public void requestTerrainUpdate() {
		terrainUpdate = true;
	}
	
	public boolean needsTerrainUpdate() {
		return terrainUpdate;
	}
	
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
	
	public Mesh getCorruptionMesh(int layer) {
		return corrMeshes.get(layer);
	}
	
	public void updatedCorruptionMesh(int layer) {
		updates.set(layer, false);
	}
	
	public void requestCorruptionUpdate(int layer) {
		updates.set(layer, true);
	}
	
	public boolean needsCorruptionMeshUpdate(int layer) {
		return updates.get(layer);
	}
}
