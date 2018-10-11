package org.dudss.nodeshot.terrain;

import java.util.ArrayList;
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
	List<MeshVertexData> corrVertexData;
	
	/**Section is an object representing a square grid of chunks with a fixed size.
	 * It also holds vertex info about under laying chunk terrain and corruption
	 * @param chunks
	 */
	public Section(Chunk[][] chunks) {
		sectionChunks = chunks;
		
		corrVertexData = new ArrayList<MeshVertexData>();
		corrMeshes = new ArrayList<Mesh>();
		
		for (int i = 0; i < 10; i++) {
			corrMeshes.add(null);
			corrVertexData.add(new MeshVertexData(null, null));
		}
		
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
	
	/*public void draw(SpriteBatch batch) {		
		
		boolean swFull = sw.isFull();
		boolean seFull = se.isFull();
		boolean nwFull = nw.isFull();
		boolean neFull = ne.isFull();
		
		if (swFull && seFull && nwFull && neFull) {
			full = true;
			drawFullTile();
			return;
		} else {
			full = false;
		}
		
		if (swFull) {
			drawFullSubTile(1);
		} else {
			for (int y = 0; y < size/2; y++) {
				for (int x = 0; x < size/2; x++) {	
					sw.subSectionChunks[x][y].draw(batch, 0, 0);
				}
			}
		}
		
		if (seFull) {
			drawFullSubTile(2);
		} else {
			for (int y = 0; y < size/2; y++) {
				for (int x = 0; x < size/2; x++) {	
					se.subSectionChunks[x][y].draw(batch, 0, 0);
				}
			}
		}
		if (nwFull) {
			drawFullSubTile(3);
		} else {
			for (int y = 0; y < size/2; y++) {
				for (int x = 0; x < size/2; x++) {	
					nw.subSectionChunks[x][y].draw(batch, 0, 0);
				}
			}
		}
		if (neFull) {
			drawFullSubTile(4);
		} else {
			for (int y = 0; y < size/2; y++) {
				for (int x = 0; x < size/2; x++) {	
					ne.subSectionChunks[x][y].draw(batch, 0, 0);
				}
			}
		}
	}
	*/ 	
	
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
}
