package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Chunks {
	
	Chunk[][] chunks;
	
	Color transparent = new Color(0, 0, 0, 0.0f);
	
	public void create() {
		chunks = new Chunk[Base.CHUNK_AMOUNT][Base.CHUNK_AMOUNT];
		
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y] = new Chunk(x * Base.CHUNK_SIZE, y * Base.CHUNK_SIZE);
			}
		}
	}
	
	public void update(Chunk ch) {
		ch.update();
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
				chunks[x][y].draw(batch, x , y); 		 
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
	
	public void generateCoalPatches(Pixmap pixmap) {
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				Color c = new Color(pixmap.getPixel(x, y));
				//System.out.println("Color val: " + c.r);
				if (c.r > Base.COAL_THRESHOLD && c.r <= 1.0) {
					chunks[x][y].setCoalLevel(c.r);
				} else {
					chunks[x][y].setCoalLevel(0);
				}
			}
		}
	}
	public void generateIronPatches(Pixmap pixmap) {
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				Color c = new Color(pixmap.getPixel(x, y));
				//System.out.println("Color val: " + c.r);
				if (c.r > Base.IRON_THRESHOLD && c.r <= 1.0) {
					chunks[x][y].setIronLevel(c.r);
				} else {
					chunks[x][y].setIronLevel(0);
				}
			}
		}
	}
}
