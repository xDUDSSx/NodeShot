package org.dudss.nodeshot.terrain;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.algorithms.SimplexNoiseGenerator;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Chunks {
	
	Chunk[][] chunks;
	public Section[][] sections;
	
	List<Quad> corruptionQuads;
	
	public List<Section> sectionsInView;
	
	public boolean created = false;
	
	Color transparent = new Color(0, 0, 0, 0.0f);
	
	public enum OreType {
		COAL, IRON, NONE
	}
	
	Rectangle viewBounds;
	Rectangle imageBounds = new Rectangle();
	
	//No viewport set, needs to have updateCam() called
	public void create() {
		
		chunks = new Chunk[Base.CHUNK_AMOUNT][Base.CHUNK_AMOUNT];
		corruptionQuads = new ArrayList<Quad>();		
		sections = new Section[Base.CHUNK_AMOUNT/Base.SECTION_SIZE][Base.CHUNK_AMOUNT/Base.SECTION_SIZE];
		sectionsInView = new ArrayList<Section>();
		
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y] = new Chunk(x * Base.CHUNK_SIZE, y * Base.CHUNK_SIZE);
			}
		}
		
		viewBounds = new Rectangle();
		imageBounds = new Rectangle();
		created = true;
	}
	
	public void create(OrthographicCamera cam) {		
		create();		
		updateCam(cam);	
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
		sectionsInView.clear();
		for (int y = 0; y < Base.SECTION_AMOUNT; y++) {
			for (int x = 0; x < Base.SECTION_AMOUNT; x++) {
				imageBounds.set(sections[x][y].sectionChunks[0][0].x, sections[x][y].sectionChunks[0][0].y, Base.SECTION_SIZE*Base.CHUNK_SIZE, Base.SECTION_SIZE*Base.CHUNK_SIZE);	
				if (viewBounds.contains(imageBounds) || viewBounds.overlaps(imageBounds)) {
					sectionsInView.add(sections[x][y]);
				}						 
			}
		}		
	}
	
	public void updateAll() {
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y].update();
			}
		}
	}
				
	public void draw(ShapeRenderer sR, SpriteBatch batch) {
		corruptionQuads.clear();
		for (int x = 0; x < Base.SECTION_AMOUNT; x++) {
			for (int y = 0; y < Base.SECTION_AMOUNT; y++) {
				imageBounds.set(sections[x][y].sectionChunks[0][0].x, sections[x][y].sectionChunks[0][0].y, Base.SECTION_SIZE*Base.CHUNK_SIZE, Base.SECTION_SIZE*Base.CHUNK_SIZE);	
				if (viewBounds.contains(imageBounds) || viewBounds.overlaps(imageBounds)) {
					sections[x][y].draw(batch);		 
				}						
			}
		}	
	}
	
	public void drawCorruption(SpriteBatch batch) {
		/*for (int x = 0; x < Base.SECTION_AMOUNT; x++) {
			for (int y = 0; y < Base.SECTION_AMOUNT; y++) {
				imageBounds.set(sections[x][y].sectionChunks[0][0].x, sections[x][y].sectionChunks[0][0].y, Base.SECTION_SIZE*Base.CHUNK_SIZE, Base.SECTION_SIZE*Base.CHUNK_SIZE);	
				if (viewBounds.contains(imageBounds) || viewBounds.overlaps(imageBounds)) {
					sections[x][y].drawCorruption(batch);		 
				}						
			}
		}	
		*/
		for (int i = 0; i < GameScreen.chunks.corruptionQuads.size(); i++) {				
			batch.draw(SpriteLoader.tileAtlas.findRegion("corr16").getTexture(), GameScreen.chunks.corruptionQuads.get(i).getVertices(), 0, 20);
		}
	}	
	
	public void setCoalLevel(float level, int x, int y) {
		chunks[x][y].setCoalLevel(level);
	}
	
	public void setIronLevel(float level, int x, int y) {
		chunks[x][y].setIronLevel(level);
	}
	
	public Chunk getChunk(int x, int y) {
		if (x < 0 || y < 0 || x > Base.CHUNK_AMOUNT-1 || y > Base.CHUNK_AMOUNT-1) {
			return null;
		}
		return chunks[x][y];
	}
	
	public void setChunk(int x, int y, Chunk chunk) {
		chunks[x][y] = chunk;
	}
	
	public void generateSections() {
		for (int y = 0; y < Base.CHUNK_AMOUNT; y+=Base.SECTION_SIZE) {
			for (int x = 0; x < Base.CHUNK_AMOUNT; x+=Base.SECTION_SIZE) {

				Chunk[][] sectionChunks = new Chunk[Base.SECTION_SIZE][Base.SECTION_SIZE];
				for(int y1 = 0; y1 < Base.SECTION_SIZE; y1++) {
					for (int x1 = 0; x1 < Base.SECTION_SIZE; x1++) {
						sectionChunks[x1][y1] = chunks[x + x1][y + y1];
					}
				}

				sections[x/Base.SECTION_SIZE][y/Base.SECTION_SIZE] = new Section(sectionChunks);
			}
		}
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
         create();
         System.out.println("Generating coal ore");
         pixmap = generateCoalPatches(pixmap);
         System.out.println("Generating iron ore");
         pixmap2 = generateIronPatches(pixmap2);
         System.out.println("Generating sections");
         generateSections();
	}
}
