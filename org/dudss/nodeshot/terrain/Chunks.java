package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.algorithms.SimplexNoiseGenerator;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class Chunks {
	
	Chunk[][] chunks;
	
	public boolean created = false;
	
	Color transparent = new Color(0, 0, 0, 0.0f);
	
	public enum OreType {
		COAL, IRON, NONE
	}
	
	Rectangle viewBounds;
	Rectangle imageBounds = new Rectangle();
	
	public void create(OrthographicCamera cam) {
		
		chunks = new Chunk[Base.CHUNK_AMOUNT][Base.CHUNK_AMOUNT];
		
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y] = new Chunk(x * Base.CHUNK_SIZE, y * Base.CHUNK_SIZE);
			}
		}

		viewBounds = new Rectangle();
		imageBounds = new Rectangle();
		
		updateCam(cam);
		created = true;
	}
	
	//No viewport set, needs to have updateCam() called
	public void create() {
		
		chunks = new Chunk[Base.CHUNK_AMOUNT][Base.CHUNK_AMOUNT];
		
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y] = new Chunk(x * Base.CHUNK_SIZE, y * Base.CHUNK_SIZE);
			}
		}

		viewBounds = new Rectangle();
		imageBounds = new Rectangle();
		created = true;
	}
	
	public void update(Chunk ch) {
		ch.update();
	}
	
	public void updateCam(OrthographicCamera cam) {
		float width = cam.viewportWidth * cam.zoom;
		float height = cam.viewportHeight * cam.zoom;
		float w = width * Math.abs(cam.up.y) + height * Math.abs(cam.up.x);
		float h = height * Math.abs(cam.up.y) + width * Math.abs(cam.up.x);
		
		viewBounds.set(cam.position.x - w / 2 - 50, cam.position.y - h / 2 - 50, w + 100, h + 100);
	}
	
	public void updateAll() {
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y].update();
			}
		}	
	}
				
	public void draw(ShapeRenderer sR, SpriteBatch batch) {
		batch.begin();
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				imageBounds.set(chunks[x][y].x, chunks[x][y].y, chunks[x][y].size, chunks[x][y].size);	
				//System.out.println("ImageBounds: width: " + chunks[x][y].size + " height: " + chunks[x][y].size + " x: " + chunks[x][y].x + " y: " +  chunks[x][y].y);
				if (viewBounds.contains(imageBounds) || viewBounds.overlaps(imageBounds)) {
					chunks[x][y].draw(batch, x , y); 		 
				}				
			}
		}
		batch.end();
		/*sR.setColor(transparent);
		sR.begin(ShapeType.Filled);
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y].draw(sR);
			}
		}
		sR.end();
		*/		
	}
	
	public void setCoalLevel(float level, int x, int y) {
		chunks[x][y].setCoalLevel(level);
	}
	
	public void setIronLevel(float level, int x, int y) {
		chunks[x][y].setIronLevel(level);
	}
	
	public Chunk getChunk(int x, int y) {
		return chunks[x][y];
	}
	
	public void setChunk(int x, int y, Chunk chunk) {
		chunks[x][y] = chunk;
	}
	
	public Pixmap generateCoalPatches(Pixmap pixmap) {
		Pixmap patchPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				Color c = new Color(pixmap.getPixel(x, y));
				//System.out.println("Color val: " + c.r);
				if (c.r > Base.COAL_THRESHOLD && c.r <= 1.0) {
					chunks[x][y].setCoalLevel(c.r);
					patchPixmap.drawPixel(x, y, Color.WHITE.toIntBits());
				} else {
					chunks[x][y].setCoalLevel(-1);
					patchPixmap.drawPixel(x, y, Color.BLACK.toIntBits());
				}
			}
		}
		return patchPixmap;
	}
	public Pixmap generateIronPatches(Pixmap pixmap) {
		Pixmap patchPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				Color c = new Color(pixmap.getPixel(x, y));
				//System.out.println("Color val: " + c.r);
				if (c.r > Base.IRON_THRESHOLD && c.r <= 1.0) {
					chunks[x][y].setIronLevel(c.r);
					patchPixmap.drawPixel(x, y, Color.WHITE.toIntBits());
				} else {
					chunks[x][y].setIronLevel(-1);
					patchPixmap.drawPixel(x, y, Color.BLACK.toIntBits());
				}
			}
		}
		return patchPixmap;
	}
	
	public void generateAll() {
		 SimplexNoiseGenerator sn = new SimplexNoiseGenerator();
         System.out.println("\nGenerating noise (1/2)");
         float[][] coalMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 4, 0.35f, 0.018f);
         sn.randomizeMutatorTable();
         System.out.println("Generating noise (2/2)");
         float[][] ironMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 4, 0.35f, 0.018f);
         
         System.out.println("Creating pixmaps");
         Pixmap pixmap = new Pixmap(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, Format.RGBA8888);
         for (int x1 = 0; x1 < Base.CHUNK_AMOUNT; x1++) {
         	for (int y1 = 0; y1 < Base.CHUNK_AMOUNT; y1++) {   
         		//Sometimes values extend beyond the accepted [-1.0,1.0] range, correct that
         		if (coalMap[x1][y1] > 1) {
         			coalMap[x1][y1] = 1.0f;
         	    }
         	    if (coalMap[x1][y1] < -1) {
         	    	coalMap[x1][y1] = -1.0f;
         	    }
         	    
         	    //Converting [-1.0,1.0] to [0,1]
         	    float val = (((coalMap[x1][y1] - (-1.0f)) * (1.0f - 0)) / (1.0f - (-1.0f))) + 0;
         	    pixmap.setColor(Color.rgba8888(val, val, val, 1.0f));
         		pixmap.drawPixel(x1, y1);
         	}
         }
         
         Pixmap pixmap2 = new Pixmap(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, Format.RGBA8888);
         for (int x2 = 0; x2 < Base.CHUNK_AMOUNT; x2++) {
         	for (int y2 = 0; y2 < Base.CHUNK_AMOUNT; y2++) {   
         		//Sometimes values extend beyond the accepted [-1.0,1.0] range, correct that
         		if (ironMap[x2][y2] > 1) {
         			ironMap[x2][y2] = 1.0f;
         	    }
         	    if (ironMap[x2][y2] < -1) {
         	    	ironMap[x2][y2] = -1.0f;
         	    }
         	    
         	    //Converting [-1.0,1.0] to [0,1]
         	    float val = (((ironMap[x2][y2] - (-1.0f)) * (1.0f - 0)) / (1.0f - (-1.0f))) + 0;
         	    pixmap2.setColor(Color.rgba8888(val, val, val, 1.0f));
         		pixmap2.drawPixel(x2, y2);
         	}
         }

         //CHUNK GEN
         System.out.println("Generating chunks (n. " + Base.CHUNK_AMOUNT*Base.CHUNK_AMOUNT + ")");
         GameScreen.chunks.create();
         System.out.println("Generating coal ore");
         pixmap = GameScreen.chunks.generateCoalPatches(pixmap);
         System.out.println("Generating iron ore");
         pixmap2 = GameScreen.chunks.generateIronPatches(pixmap2);
	}
}
