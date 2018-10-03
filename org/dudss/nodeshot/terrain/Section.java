package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Section {
	Chunk[][] sectionChunks;
	
	int size = Base.SECTION_SIZE;
	 
	boolean full = false;
	
	SubSection sw;
	SubSection se;
	SubSection nw;
	SubSection ne;
	
	/*int width = size;
	int height = size;
	int numberOfTiles = width * height;
	int numberOfVerticesPerTile = 4;
	int numberOfVerts = 5 * numberOfVerticesPerTile;
	
	float verts[] = new float[numberOfTiles*numberOfVerts];	
	private int n = 0;
	*/
	
	public Section(Chunk[][] chunks) {
		sectionChunks = chunks;
		
		Chunk[][] swChunks = new Chunk[size/2][size/2];
		Chunk[][] seChunks = new Chunk[size/2][size/2];
		Chunk[][] nwChunks = new Chunk[size/2][size/2];
		Chunk[][] neChunks = new Chunk[size/2][size/2];
		
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {	
				if (y < size/2 && x < size/2) {
					swChunks[x][y] = sectionChunks[x][y];
				}
				if (y < size/2 && x >= size/2) {
					seChunks[x-8][y] = sectionChunks[x][y];
				}
				if (y >= size/2 && x < size/2) {
					nwChunks[x][y-8] = sectionChunks[x][y];
				}
				if (y >= size/2 && x >= size/2) {
					neChunks[x-8][y-8] = sectionChunks[x][y];
				}
			}
		}
		
		sw = new SubSection(swChunks, size/2);
		se = new SubSection(seChunks, size/2);
		nw = new SubSection(nwChunks, size/2);
		ne = new SubSection(neChunks, size/2);
	}
	
	public void draw(SpriteBatch batch) {		
		
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
	
	private void drawFullTile() {
		float[] vertices = new float[20];
		
		float x1 = sectionChunks[0][0].getX();
		float y1 = sectionChunks[0][0].getY();
		float x2 = sectionChunks[0][0].getX() + this.size*Base.CHUNK_SIZE;
		float y2 = sectionChunks[0][0].getY() + this.size*Base.CHUNK_SIZE;
		
		float u1 = SpriteLoader.tileAtlas.findRegion("corr16").getU();
		float u2 = SpriteLoader.tileAtlas.findRegion("corr16").getU2();
		float v1 = SpriteLoader.tileAtlas.findRegion("corr16").getV();
		float v2 = SpriteLoader.tileAtlas.findRegion("corr16").getV2();
		
		vertices[Batch.X1] = x1;
		vertices[Batch.Y1] = y1;
		vertices[Batch.C1] = 0;
		vertices[Batch.U1] = u1;
		vertices[Batch.V1] = v1;

		vertices[Batch.X2] = x1;
		vertices[Batch.Y2] = y2;
		vertices[Batch.C2] = 0;
		vertices[Batch.U2] = u1;
		vertices[Batch.V2] = v2;

		vertices[Batch.X3] = x2;
		vertices[Batch.Y3] = y2;
		vertices[Batch.C3] = 0;
		vertices[Batch.U3] = u2;
		vertices[Batch.V3] = v2;

		vertices[Batch.X4] = x2;
		vertices[Batch.Y4] = y1;
		vertices[Batch.C4] = 0;
		vertices[Batch.U4] = u2;
		vertices[Batch.V4] = v1;
		
		GameScreen.chunks.corruptionQuads.add(new Quad(vertices));
	}
	
	/**
	int i =>
	+-----+-----+
	| nw3 | ne4 |
	+-----+-----+
	| sw1 | se2 |
	+-----+-----+
	*/
	private void drawFullSubTile(int i) {
		float[] vertices = new float[20];
		float x1 = 0;
		float x2 = 0;
		float y1 = 0;
		float y2 = 0;
		
		switch(i) {
			case 1: 
				x1 = sectionChunks[0][0].getX();
				y1 = sectionChunks[0][0].getY();
				x2 = sectionChunks[0][0].getX() + (this.size/2)*Base.CHUNK_SIZE;
				y2 = sectionChunks[0][0].getY() + (this.size/2)*Base.CHUNK_SIZE;		
				break;
			case 2: 
				x1 = sectionChunks[8][0].getX();
				y1 = sectionChunks[8][0].getY();
				x2 = sectionChunks[8][0].getX() + (this.size/2)*Base.CHUNK_SIZE;
				y2 = sectionChunks[8][0].getY() + (this.size/2)*Base.CHUNK_SIZE;		
				break;
			case 3: 
				x1 = sectionChunks[0][8].getX();
				y1 = sectionChunks[0][8].getY();
				x2 = sectionChunks[0][8].getX() + (this.size/2)*Base.CHUNK_SIZE;
				y2 = sectionChunks[0][8].getY() + (this.size/2)*Base.CHUNK_SIZE;		
				break;
			case 4: 
				x1 = sectionChunks[8][8].getX();
				y1 = sectionChunks[8][8].getY();
				x2 = sectionChunks[8][8].getX() + (this.size/2)*Base.CHUNK_SIZE;
				y2 = sectionChunks[8][8].getY() + (this.size/2)*Base.CHUNK_SIZE;		
				break;
		}
		
		float u1 = SpriteLoader.tileAtlas.findRegion("corr16").getU();
		float u2 = SpriteLoader.tileAtlas.findRegion("corr16").getU2();
		float v1 = SpriteLoader.tileAtlas.findRegion("corr16").getV();
		float v2 = SpriteLoader.tileAtlas.findRegion("corr16").getV2();
		
		vertices[Batch.X1] = x1;
		vertices[Batch.Y1] = y1;
		vertices[Batch.C1] = 0;
		vertices[Batch.U1] = u1;
		vertices[Batch.V1] = v1;

		vertices[Batch.X2] = x1;
		vertices[Batch.Y2] = y2;
		vertices[Batch.C2] = 0;
		vertices[Batch.U2] = u1;
		vertices[Batch.V2] = v2;

		vertices[Batch.X3] = x2;
		vertices[Batch.Y3] = y2;
		vertices[Batch.C3] = 0;
		vertices[Batch.U3] = u2;
		vertices[Batch.V3] = v2;

		vertices[Batch.X4] = x2;
		vertices[Batch.Y4] = y1;
		vertices[Batch.C4] = 0;
		vertices[Batch.U4] = u2;
		vertices[Batch.V4] = v1;
		
		GameScreen.chunks.corruptionQuads.add(new Quad(vertices));
	}
}
