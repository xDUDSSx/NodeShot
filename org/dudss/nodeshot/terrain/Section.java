package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Section {
	Chunk[][] sectionChunks;
	
	int size = Base.SECTION_SIZE;
	 
	boolean full = false;
	
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
	}
	
	public void draw(SpriteBatch batch) {		

		boolean b = false;
		if (!full) {
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {	
					if (sectionChunks[x][y].getCreeperLevel() == 0) {
						b = true;
					}
					sectionChunks[x][y].draw(batch , 0, 0);
				}
			}
		
			if (b == false) {
				full = true;
			}
		} else { 
			batch.draw(SpriteLoader.tileAtlas.findRegion("corr16section"), sectionChunks[0][0].getX(), sectionChunks[0][0].getY());
		}
	
		/*generateGeometry();
		
		TextureRegion coalRegion = SpriteLoader.tileAtlas.findRegion("tiledCoal");
		batch.draw(coalRegion.getTexture(), verts, 0, numberOfTiles*numberOfVerts);
		*/
	}
	
	/*void generateGeometry() {		
		n = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				addTile(SpriteLoader.tileAtlas.findRegion("transparent16"), x, y);	
			}
		}
	}
	
	private void addTile(TextureRegion region, int x, int y) {
		float x1 = sectionChunks[x][y].getX();
		float y1 = sectionChunks[x][y].getY();
		
		float x2 = x1 + Base.CHUNK_SIZE;
		float y2 = y1 + Base.CHUNK_SIZE;
		
		float u1 = region.getU();
		float v1 = region.getV();
		float u2 = region.getU2();
		float v2 = region.getV2();

		verts[n++] = x1;
		verts[n++] = y1;
		verts[n++] = 0;
		verts[n++] = u1;
		verts[n++] = v1;

		verts[n++] = x1;
		verts[n++] = y2;
		verts[n++] = 0;
		verts[n++] = u1;
		verts[n++] = v2;

		verts[n++] = x2;
		verts[n++] = y2;
		verts[n++] = 0;
		verts[n++] = u2;
		verts[n++] = v2;
	
		verts[n++] = x2;
		verts[n++] = y1;
		verts[n++] = 0;
		verts[n++] = u2;
		verts[n++] = v1;
	}
	*/
}
