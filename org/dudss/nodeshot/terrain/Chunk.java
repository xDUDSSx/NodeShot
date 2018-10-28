package org.dudss.nodeshot.terrain;

import java.util.ArrayList;
import java.util.Collections;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks.OreType;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class Chunk {
	float x, y;
	float size = Base.CHUNK_SIZE;
	
	//0 to 1.0 range
	float coalOre = 0f;
	float ironOre = 0f;
	
	//0 to Base.MAX_CREEP 
	float creeper = 0;	
	float flowRate = 0.15f;
	public float creeperChange = 0;
			
	int height = 1;
	
	boolean[] edges;
	
	//TODO: remove plague, probably not going to use it
	float plague = 0;

	boolean toExpand = false;
	long lastUpdate = System.currentTimeMillis();
	long updateRate = 500;
	
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
		
		edges = new boolean[Base.MAX_CREEP];
		for (int i = 0; i < Base.MAX_CREEP; i++) {
			edges[i] = false;
		}
	}
	
	//TODO: rewrite the update -> simpler more "fluid" like behaviour 
	/**Current corruption update method, creeper is distributed along the tiles with lower creeper in relation to the difference from this chunks {@link #creeper}
	 * The resulting creeper level changes are saved into respectable {@link #creeperChange} variables and the actual creeper is set later
	 * using the {@link #applyUpdate()} method.
	 * This is done to remove an axial bias in the direction of the subsequent updates
	 */
	public void update() {		
		if (lastUpdate + updateRate <= System.currentTimeMillis() && height + creeper > height + 0.05f) {
			
			float spore = creeper*flowRate;
			
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
					neighbours[i].creeperChange += toAdd;
					totalAdded += toAdd;					
				}
			}		
			
			creeperChange -= totalAdded;
			//System.out.println("creeper: " + creeper + " spore: " + spore + " change: " + currentCreeperChange);
			
			lastUpdate = System.currentTimeMillis();			
		} else {
			return;
		}
	}
	
	/**Called after all the other {@link Chunk}s had been updated using the {@link #update()} method in the current simulation tick*/
	public void applyUpdate() {
		setCreeperLevel(creeper + creeperChange);
		creeperChange = 0;
	}
	
	/**Returns an AtlasRegion representing this tile, if corr is true the method will return tile corruption representation**/
	public AtlasRegion getAppropriateTexture(boolean corr) {
		if (coalOre != 0) {	
			AtlasRegion desiredRegion = null;
			if (coalOre <= 0.25) {
				desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoallow");
			} else 
			if (coalOre <= 0.5) {		;
				desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoallower");
			} else
			if (coalOre > 0.5) {
				desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoal");
			}		
			
			if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {				
				
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
					minusx.getOreLevel() > 0 && 
					minusy.getOreLevel() > 0)
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalTL");
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0 &&
				    minusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalTR");
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
				    plusy.getOreLevel() > 0 &&
				    minusx.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalBL");
				} else
				if (minusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
				    plusx.getOreLevel() > 0 &&
				    plusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalBR");
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					minusx.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalEL");
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalER");
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					minusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalET");
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() > 0)  
				{		
					return SpriteLoader.tileAtlas.findRegion("tiledCoalEB");
				} else 
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() == 0)  
				{		
					return SpriteLoader.tileAtlas.findRegion("tiledCoalS");
				}
			}
				
			return desiredRegion;
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
		
		return SpriteLoader.tileAtlas.findRegion("transparent16");				
	}
	
	public AtlasRegion getCorruptionTexture(int level) {
		if (creeper != 0) {
			if (creeper + height > level) { 						
				if ((creeper + height > level + 1) && (level + 1 < Base.MAX_CREEP)) {
					this.edges[level] = false;
					
					if (this.edges[level + 1] == true) {			
						if (plusx.getCreeperLevel() + height > level && plusx.getCreeperLevel() != 0 &&
							plusy.getCreeperLevel() + height > level && plusy.getCreeperLevel() != 0 &&
							minusx.getCreeperLevel() + height > level && minusx.getCreeperLevel() != 0 &&
							minusy.getCreeperLevel() + height > level && minusy.getCreeperLevel() != 0)  
						{
							this.edges[level] = false;
							return SpriteLoader.tileAtlas.findRegion("corr32");							
						}
					} else {
						return null;
					}
				} else {
					return resolveCorruptionEdges(level, true);
				}			
			} else {
				this.edges[level] = false;
				return null;
			}
		}
		return null;
	}
	
	private AtlasRegion resolveCorruptionEdges(int level, boolean toplevel) {
		if (x > 16 && y > 16 && x < Base.WORLD_SIZE-16 && y < Base.WORLD_SIZE-16) { 
			if (plusy.getCreeperLevel() + height <= level &&
				plusx.getCreeperLevel() + height <= level &&
				minusx.getCreeperLevel() + height > level && 
				minusy.getCreeperLevel() + height > level)
			{
				this.edges[level] = true;
				return SpriteLoader.tileAtlas.findRegion("corrTL");
			} else
			if (plusy.getCreeperLevel() + height <= level && 
				minusx.getCreeperLevel() + height <= level &&
				plusx.getCreeperLevel() + height > level &&
			    minusy.getCreeperLevel() + height > level)  
			{
				this.edges[level] = true;
				return SpriteLoader.tileAtlas.findRegion("corrTR");
			} else
			if (minusy.getCreeperLevel() + height <= level && 
				plusx.getCreeperLevel() + height <= level &&
			    plusy.getCreeperLevel() + height > level &&
			    minusx.getCreeperLevel() + height > level)  
			{
				this.edges[level] = true;
				return SpriteLoader.tileAtlas.findRegion("corrBL");
			} else
			if (minusy.getCreeperLevel() + height <= level && 
				minusx.getCreeperLevel() + height <= level && 
			    plusx.getCreeperLevel() + height > level &&
			    plusy.getCreeperLevel() + height > level)  
			{
				this.edges[level] = true;
				return SpriteLoader.tileAtlas.findRegion("corrBR");
			} else
			//Straight line borders
			if (plusy.getCreeperLevel() + height > level && 
				plusx.getCreeperLevel() + height > level && 
				minusy.getCreeperLevel() + height > level &&
				minusx.getCreeperLevel() + height <= level)  
			{
				this.edges[level] = true;
				return SpriteLoader.tileAtlas.findRegion("corrSL");
			} else
			if (plusy.getCreeperLevel() + height > level && 
				minusx.getCreeperLevel() + height > level && 
				minusy.getCreeperLevel() + height > level &&
				plusx.getCreeperLevel() + height <= level)  
			{	
				this.edges[level] = true;
				return SpriteLoader.tileAtlas.findRegion("corrSR");
			} else
			if (plusy.getCreeperLevel() + height > level && 
				plusx.getCreeperLevel() + height > level &&
				minusx.getCreeperLevel() + height > level &&
				minusy.getCreeperLevel() + height <= level)  
			{	
				this.edges[level] = true;
				return SpriteLoader.tileAtlas.findRegion("corrST");
			} else
			if (minusy.getCreeperLevel() + height > level && 
				plusx.getCreeperLevel() + height > level && 
				minusx.getCreeperLevel() + height > level &&
				plusy.getCreeperLevel() + height <= 0)  
			{
				this.edges[level] = true;
				return SpriteLoader.tileAtlas.findRegion("corrSB");	
			} else
				
			//Tile ends (currently unused)	
			if (plusy.getCreeperLevel() + height <= level && 
				plusx.getCreeperLevel() + height <= level && 
				minusy.getCreeperLevel() + height <= level &&
				minusx.getCreeperLevel() + height > level)  
			{
				this.edges[level] = false;
				return SpriteLoader.tileAtlas.findRegion("corr32");	
			} else
			if (plusy.getCreeperLevel() + height <= level && 
				minusx.getCreeperLevel() + height <= level && 
				minusy.getCreeperLevel() + height <= level &&
				plusx.getCreeperLevel() + height > level)  
			{	
				this.edges[level] = false;
				return SpriteLoader.tileAtlas.findRegion("corr32");
			} else
			if (plusy.getCreeperLevel() + height <= level && 
				plusx.getCreeperLevel() + height <= level && 
				minusx.getCreeperLevel() + height <= level &&
				minusy.getCreeperLevel() + height > level)  
			{	
				this.edges[level] = false;
				return SpriteLoader.tileAtlas.findRegion("corr32");
			} else
			if (minusy.getCreeperLevel() + height <= level && 
				plusx.getCreeperLevel() + height <= level && 
				minusx.getCreeperLevel() + height <= level &&
				plusy.getCreeperLevel() + height > level)  
			{
				this.edges[level] = false;
				return SpriteLoader.tileAtlas.findRegion("corr32");	
			} else 
			if (minusy.getCreeperLevel() + height <= level && 
				plusx.getCreeperLevel() + height <= level && 
				minusx.getCreeperLevel() + height <= level &&
				plusy.getCreeperLevel() + height <= level)  
			{
				this.edges[level] = false;
				return SpriteLoader.tileAtlas.findRegion("corr32");
			}
		}
		this.edges[level] = false;
		return SpriteLoader.tileAtlas.findRegion("corr32");
	}
	
	public AtlasRegion getCorruptionTexture2(int level) {
		if (creeper != 0) {
			if (creeper + height > level && creeper + height < level + 1) {
				AtlasRegion desiredRegion = SpriteLoader.tileAtlas.findRegion("corr32");

				if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {
					
					if (plusy.getCreeperLevel() + height <= level &&
						plusx.getCreeperLevel() + height <= level &&
						minusx.getCreeperLevel() + height > level && 
						minusy.getCreeperLevel() + height > level)
					{
						return SpriteLoader.tileAtlas.findRegion("corrTL");
					} else
					if (plusy.getCreeperLevel() + height <= level && 
						minusx.getCreeperLevel() + height <= level &&
						plusx.getCreeperLevel() + height > level &&
					    minusy.getCreeperLevel() + height > level)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrTR");
					} else
					if (minusy.getCreeperLevel() + height <= level && 
						plusx.getCreeperLevel() + height <= level &&
					    plusy.getCreeperLevel() + height > level &&
					    minusx.getCreeperLevel() + height > level)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrBL");
					} else
					if (minusy.getCreeperLevel() + height <= level && 
						minusx.getCreeperLevel() + height <= level && 
					    plusx.getCreeperLevel() + height > level &&
					    plusy.getCreeperLevel() + height > level)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrBR");
					} else
					//Straight line borders
					if (plusy.getCreeperLevel() + height > level && 
						plusx.getCreeperLevel() + height > level && 
						minusy.getCreeperLevel() + height > level &&
						minusx.getCreeperLevel() + height <= level)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrSR");
					} else
					if (plusy.getCreeperLevel() + height > level && 
						minusx.getCreeperLevel() + height > level && 
						minusy.getCreeperLevel() + height > level &&
						plusx.getCreeperLevel() + height <= level)  
					{	
						return SpriteLoader.tileAtlas.findRegion("corrSL");
					} else
					if (plusy.getCreeperLevel() + height > level && 
						plusx.getCreeperLevel() + height > level &&
						minusx.getCreeperLevel() + height > level &&
						minusy.getCreeperLevel() + height <= level)  
					{	
						return SpriteLoader.tileAtlas.findRegion("corrST");
					} else
					if (minusy.getCreeperLevel() + height > level && 
						plusx.getCreeperLevel() + height > level && 
						minusx.getCreeperLevel() + height > level &&
						plusy.getCreeperLevel() + height <= 0)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrSB");	
					} else
						
					//Tile ends (currently unused)	
					if (plusy.getCreeperLevel() + height <= level && 
						plusx.getCreeperLevel() + height <= level && 
						minusy.getCreeperLevel() + height <= level &&
						minusx.getCreeperLevel() + height > level)  
					{
						return desiredRegion;		
					} else
					if (plusy.getCreeperLevel() + height <= level && 
						minusx.getCreeperLevel() + height <= level && 
						minusy.getCreeperLevel() + height <= level &&
						plusx.getCreeperLevel() + height > level)  
					{	
						return desiredRegion;	
					} else
					if (plusy.getCreeperLevel() + height <= level && 
						plusx.getCreeperLevel() + height <= level && 
						minusx.getCreeperLevel() + height <= level &&
						minusy.getCreeperLevel() + height > level)  
					{	
						return desiredRegion;
					} else
					if (minusy.getCreeperLevel() + height <= level && 
						plusx.getCreeperLevel() + height <= level && 
						minusx.getCreeperLevel() + height <= level &&
						plusy.getCreeperLevel() + height > level)  
					{
						return desiredRegion;		
					} else 
					if (minusy.getCreeperLevel() + height <= level && 
						plusx.getCreeperLevel() + height <= level && 
						minusx.getCreeperLevel() + height <= level &&
						plusy.getCreeperLevel() + height <= level)  
					{
						return desiredRegion;	
					}
				}
				return desiredRegion;
			}
			return null;
		}
		return null;
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
		if (level > Base.MAX_CREEP) {
			level = Base.MAX_CREEP;
		}
		if (level < 0.05f) {
			level = 0;
		}
		
		creeper = level;
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
