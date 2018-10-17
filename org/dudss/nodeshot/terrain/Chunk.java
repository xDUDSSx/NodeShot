package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks.OreType;
import org.dudss.nodeshot.terrain.datasubsets.Quad;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Chunk {
	float x, y;
	float size = Base.CHUNK_SIZE;
	
	//0 to 1.0 range
	float coalOre = 0f;
	float ironOre = 0f;
	
	float creeper = 0;
	float spore = 0;
	
	int height = 1;
	
	//TODO: remove plague, probably not going to use it
	float plague = 0;

	boolean toExpand = false;
	long lastUpdate = System.currentTimeMillis();
	long updateRate = 500;
	
	TextureRegion dirtTr;
	TextureRegion coalTr;
	TextureRegion coalLowerTr;
	TextureRegion coalLowTr;	
	TextureRegion ironTr;
	
	protected final int NUM_VERTICES = 20;
	protected float vertices[] = new float[NUM_VERTICES];
	
	enum TriangleOrientation {
		BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT, END_LEFT, END_RIGHT, END_TOP, END_BOTTOM, SINGLE
	}
	
	int ax;
	int ay;
	
	Chunk minusx;
	Chunk plusx;
	Chunk minusy;
	Chunk plusy;
	
	Chunk cornerTopLeft;
	Chunk cornerTopRight;
	Chunk cornerBottomLeft;
	Chunk cornerBottomRight;
	
	Chunk[] neighbours;
	
	Chunk(float x, float y) {
		this.x = x;
		this.y = y;
		
		/*dirtTr = new TextureRegion(SpriteLoader.savanaTex);
		coalTr = new TextureRegion(SpriteLoader.coalTex);
		coalLowerTr = new TextureRegion(SpriteLoader.coalLowerTex);
		coalLowTr = new TextureRegion(SpriteLoader.coalLowTex);
		ironTr = new TextureRegion(SpriteLoader.ironTex);	
		*/
	}
	
	//TODO: rewrite the update -> simpler more "fluid" like behaviour 
	public void update() {			
		if (lastUpdate + updateRate <= System.currentTimeMillis() && height + creeper > height + 0.05f) {
			//loat flowRate = 0.5f;
			spore = creeper/2f;
			
			float[] diffs = new float[8];
			for (int i = 0; i < 8; i++) {
				if (neighbours[i] != null && neighbours[i].height + 0.25f < height + creeper) {
					diffs[i] = (height + creeper) - (neighbours[i].height + neighbours[i].creeper);
				} else {
					diffs[i] = 0;
				}
			}		
			
			float total = 0;
			for (int i = 0; i < 8; i++) {
				if (!(diffs[i] <= 0)) {
					total += diffs[i];
				}
			}
			
			float totalAdded = 0;
			for (int i = 0; i < 8; i++) {
				if (!(diffs[i] <= 0)) {
					float toAdd = (diffs[i]/total) * spore;
					neighbours[i].setCreeperLevel(neighbours[i].getCreeperLevel() + toAdd);
					totalAdded += toAdd;					
				}
			}		
			
			//System.out.println("totalAdded: " + totalAdded + " spore: " + spore + " creeper " + creeper);
			setCreeperLevel(creeper -= totalAdded);
			
			lastUpdate = System.currentTimeMillis();			
		} else {
			return;
		}
	}
	
	/**Returns a TextureRegion represeting this tile, if corr is true the method will return tile corruption representation**/
	public AtlasRegion getAppropriateTexture(boolean corr) {
		if (coalOre != 0) {	
			AtlasRegion desiredRegion = null;
			if (coalOre <= 0.25) {
				desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoallow");
			} else 
			if (coalOre <= 0.5) {		
				desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoallower");
			} else
			if (coalOre > 0.5) {
				desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoal");
			}		
			
			boolean triangleDrawn = false;
			
			if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {				
				
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
					minusx.getOreLevel() > 0 && 
					minusy.getOreLevel() > 0)
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_LEFT, batch, false);
					//triangleDrawn = true;
					return SpriteLoader.tileAtlas.findRegion("tiledCoalTL");
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0 &&
				    minusy.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_RIGHT, batch, false);
					//triangleDrawn = true;
					return SpriteLoader.tileAtlas.findRegion("tiledCoalTR");
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
				    plusy.getOreLevel() > 0 &&
				    minusx.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.TOP_LEFT, batch, false);
					//triangleDrawn = true;
					return SpriteLoader.tileAtlas.findRegion("tiledCoalBL");
				} else
				if (minusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
				    plusx.getOreLevel() > 0 &&
				    plusy.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.TOP_RIGHT, batch, false);
					//triangleDrawn = true;
					return SpriteLoader.tileAtlas.findRegion("tiledCoalBR");
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					minusx.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.END_RIGHT, batch, false);
					//triangleDrawn = true;	
					return SpriteLoader.tileAtlas.findRegion("tiledCoalEL");
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.END_LEFT, batch, false);
					//triangleDrawn = true;		
					return SpriteLoader.tileAtlas.findRegion("tiledCoalER");
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					minusy.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.END_TOP, batch, false);
					//triangleDrawn = true;		
					return SpriteLoader.tileAtlas.findRegion("tiledCoalET");
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.END_BOTTOM, batch, false);
					//triangleDrawn = true;			
					return SpriteLoader.tileAtlas.findRegion("tiledCoalEB");
				} else 
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() == 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.SINGLE, batch, false);
					//triangleDrawn = true;			
					return SpriteLoader.tileAtlas.findRegion("tiledCoalS");
				}
			}
		
			if (!triangleDrawn) {
				return desiredRegion;
				//drawTile(desiredRegion, batch, false);
			}
		} else
		if (ironOre != 0) {
			boolean triangleDrawn = false;
			AtlasRegion desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledIron");
			if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {
				
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
					minusx.getOreLevel() > 0 && 
					minusy.getOreLevel() > 0)
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_LEFT, batch, false);
					//triangleDrawn = true;
					return SpriteLoader.tileAtlas.findRegion("tiledIronTL");
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0 &&
				    minusy.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_RIGHT, batch, false);
					//triangleDrawn = true;
					return SpriteLoader.tileAtlas.findRegion("tiledIronTR");
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
				    plusy.getOreLevel() > 0 &&
				    minusx.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.TOP_LEFT, batch, false);
					//triangleDrawn = true;
					return SpriteLoader.tileAtlas.findRegion("tiledIronBL");
				} else
				if (minusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
				    plusx.getOreLevel() > 0 &&
				    plusy.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.TOP_RIGHT, batch, false);
					//triangleDrawn = true;
					return SpriteLoader.tileAtlas.findRegion("tiledIronBR");
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					minusx.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.END_RIGHT, batch, false);
					//triangleDrawn = true;		
					return desiredRegion;
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.END_LEFT, batch, false);
					//triangleDrawn = true;		
					return desiredRegion;	
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					minusy.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.END_TOP, batch, false);
					//triangleDrawn = true;	
					return desiredRegion;
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() > 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.END_BOTTOM, batch, false);
					//triangleDrawn = true;		
					return desiredRegion;
				} else 
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() == 0)  
				{
					//drawTileTriangle(desiredRegion, TriangleOrientation.SINGLE, batch, false);
					//triangleDrawn = true;		
					return desiredRegion;
				}
			}
		
			if (!triangleDrawn) {
				//drawTile(SpriteLoader.tileAtlas.findRegion("tiledIron"), batch, false);
				return desiredRegion;
			}
		}
		
		switch(height) {
			case 0: return SpriteLoader.tileAtlas.findRegion("rock0");
			case 1: return SpriteLoader.tileAtlas.findRegion("rock1");
			case 2: return SpriteLoader.tileAtlas.findRegion("dirt2");
			case 3: return SpriteLoader.tileAtlas.findRegion("sand3");
			case 4: return SpriteLoader.tileAtlas.findRegion("sand4");
		}
		
		//return default sand texture
		return SpriteLoader.tileAtlas.findRegion("transparent16");
				
	}
	
	public AtlasRegion getCorruptionTexture(int level) {
		if (creeper > level) {
			AtlasRegion desiredRegion = SpriteLoader.tileAtlas.findRegion("corr32");
			if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {
				
				if (plusy.getCreeperLevel() <= level &&
					plusx.getCreeperLevel() <= level &&
					minusx.getCreeperLevel() > level && 
					minusy.getCreeperLevel() > level)
				{
					return SpriteLoader.tileAtlas.findRegion("corrTL");
				} else
				if (plusy.getCreeperLevel() <= level && 
					minusx.getCreeperLevel() <= level &&
					plusx.getCreeperLevel() > level &&
				    minusy.getCreeperLevel() > level)  
				{
					return SpriteLoader.tileAtlas.findRegion("corrTR");
				} else
				if (minusy.getCreeperLevel() <= level && 
					plusx.getCreeperLevel() <= level &&
				    plusy.getCreeperLevel() > level &&
				    minusx.getCreeperLevel() > level)  
				{
					return SpriteLoader.tileAtlas.findRegion("corrBL");
				} else
				if (minusy.getCreeperLevel() <= level && 
					minusx.getCreeperLevel() <= level && 
				    plusx.getCreeperLevel() > level &&
				    plusy.getCreeperLevel() > level)  
				{
					return SpriteLoader.tileAtlas.findRegion("corrBR");
				} else
				//Straight line borders
				if (plusy.getCreeperLevel() > level && 
					plusx.getCreeperLevel() > level && 
					minusy.getCreeperLevel() > level &&
					minusx.getCreeperLevel() <= level)  
				{
					return SpriteLoader.tileAtlas.findRegion("corrSR");
				} else
				if (plusy.getCreeperLevel() > level && 
					minusx.getCreeperLevel() > level && 
					minusy.getCreeperLevel() > level &&
					plusx.getCreeperLevel() <= level)  
				{	
					return SpriteLoader.tileAtlas.findRegion("corrSL");
				} else
				if (plusy.getCreeperLevel() > level && 
					plusx.getCreeperLevel() > level &&
					minusx.getCreeperLevel() > level &&
					minusy.getCreeperLevel() <= level)  
				{	
					return SpriteLoader.tileAtlas.findRegion("corrST");
				} else
				if (minusy.getCreeperLevel() > level && 
					plusx.getCreeperLevel() > level && 
					minusx.getCreeperLevel() > level &&
					plusy.getCreeperLevel() <= 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("corrSB");	
				} else
					
				//Tile ends (currently unused)	
				if (plusy.getCreeperLevel() <= level && 
					plusx.getCreeperLevel() <= level && 
					minusy.getCreeperLevel() <= level &&
					minusx.getCreeperLevel() > level)  
				{
					return desiredRegion;		
				} else
				if (plusy.getCreeperLevel() <= level && 
					minusx.getCreeperLevel() <= level && 
					minusy.getCreeperLevel() <= level &&
					plusx.getCreeperLevel() > level)  
				{	
					return desiredRegion;	
				} else
				if (plusy.getCreeperLevel() <= level && 
					plusx.getCreeperLevel() <= level && 
					minusx.getCreeperLevel() <= level &&
					minusy.getCreeperLevel() > level)  
				{	
					return desiredRegion;
				} else
				if (minusy.getCreeperLevel() <= level && 
					plusx.getCreeperLevel() <= level && 
					minusx.getCreeperLevel() <= level &&
					plusy.getCreeperLevel() > level)  
				{
					return desiredRegion;		
				} else 
				if (minusy.getCreeperLevel() <= level && 
					plusx.getCreeperLevel() <= level && 
					minusx.getCreeperLevel() <= level &&
					plusy.getCreeperLevel() <= level)  
				{
					return desiredRegion;	
				}
			}
			return desiredRegion;
		}
		return SpriteLoader.tileAtlas.findRegion("transparent16");
	}
	
	public void updateNeighbour() {
		ax = Math.round(x/Base.CHUNK_SIZE);
		ay = Math.round(y/Base.CHUNK_SIZE);
		
		minusx = GameScreen.chunks.getChunk(ax - 1, ay);
		plusx = GameScreen.chunks.getChunk(ax + 1, ay);
		minusy = GameScreen.chunks.getChunk(ax, ay - 1);
		plusy = GameScreen.chunks.getChunk(ax, ay + 1);
		
		cornerTopLeft = GameScreen.chunks.getChunk(ax - 1, ay + 1);
		cornerTopRight = GameScreen.chunks.getChunk(ax + 1, ay + 1);
		cornerBottomLeft = GameScreen.chunks.getChunk(ax - 1, ay - 1);
		cornerBottomRight = GameScreen.chunks.getChunk(ax + 1, ay - 1);
		
		//Putting these chunks into array for accessibility
		neighbours = new Chunk[8];		
		neighbours[0] = plusx;
		neighbours[1] = plusy;
		neighbours[2] = minusx;
		neighbours[3] = minusy;
		neighbours[4] = cornerTopLeft;
		neighbours[5] = cornerTopRight;
		neighbours[6] = cornerBottomLeft;
		neighbours[7] = cornerBottomRight;
	}
	
	public void setCoalLevel(float level) {
		if (level == -1) {
			coalOre = 0;
		} else {
			coalOre = level;
		}
	}
	public void setIronLevel(float level) {
		if (level == -1) {
			ironOre = 0;
		} else {
			ironOre = level;
		}
	}
	
	public float getCoalLevel() {
		return coalOre;
	}
	
	public float getIronLevel() {
		return ironOre;
	}
	
	public OreType getOreType() {
		if (this.coalOre != 0) {
			return OreType.COAL;
		} else 
		if (this.ironOre != 0) { 
			return OreType.IRON;
		} else {
			return OreType.NONE;
		}
	}
	
	public float getOreLevel() {
		if (coalOre > 0) {
			return this.coalOre;
		} else 
		if (ironOre > 0) { 
			return this.ironOre;
		} else {
			return 0;
		}
	}
	
	public void setCreeperLevel(float level) {
		creeper = level;
		if (creeper > Base.MAX_CREEP) {
			creeper = Base.MAX_CREEP;
		}
		if (creeper < 0.05f) {
			creeper = 0;
		}
	}
	public float getCreeperLevel() {
		return creeper;
	}
	
	public void setHeight(int level) {
		height = level;
	}
	public float getHeight() {
		return height;
	}
	
	public void setPlagueLevel(float level) {
		plague = level;
		if (plague > 1f) {
			plague = 1f;
		}
		if (plague < 0f) {
			plague = 0f;
		}
	}
	public float getPlagueLevel() {
		return plague;
	}

	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}	
	public float getSize() {
		return size;
	}
}
