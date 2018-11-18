package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
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
	
	//Absolute creeper (height + creeper when creeper > 0)
	float absCreeper = 0;
	
	float flowRate = 0.15f;
	public float creeperChange = 0;
			
	int height = 1;
	
	boolean[] newEdges;
	boolean[] edges;
	
	//TODO: remove plague, probably not going to use it
	float plague = 0;

	boolean toExpand = false;
	long lastUpdate = SimulationThread.simTick;
	long updateRate = 5;
	
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
		
		newEdges = new boolean[Base.MAX_CREEP];
		for (int i = 0; i < Base.MAX_CREEP; i++) {
			newEdges[i] = false;
		}
	}
	
	//TODO: rewrite the update -> simpler more "fluid" like behaviour 
	/**Current corruption update method, creeper is distributed along the tiles with lower creeper in relation to the difference from this chunks {@link #creeper}
	 * The resulting creeper level changes are saved into respectable {@link #creeperChange} variables and the actual creeper is set later
	 * using the {@link #applyUpdate()} method.
	 * This is done to remove an axial bias in the direction of the subsequent updates
	 */
	public void update() {		
		if (lastUpdate + updateRate <= SimulationThread.simTick && height + creeper > height + 0.05f) {
			
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
			
			lastUpdate = SimulationThread.simTick;	
		} else {
			return;
		}
	}
	
	/**Called after all the other {@link Chunk}s had been updated using the {@link #update()} method in the current simulation tick*/
	public void applyUpdate() {
		setCreeperLevel(creeper + creeperChange);
		creeperChange = 0;
	}
	
	/**Returns an AtlasRegion representing this tiles terrain*/
	public AtlasRegion getTerrainTexture2() {
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
					return SpriteLoader.tileAtlas.findRegion("tiledCoalBL");
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0 &&
				    minusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalBR");
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
				    plusy.getOreLevel() > 0 &&
				    minusx.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalTL");
				} else
				if (minusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
				    plusx.getOreLevel() > 0 &&
				    plusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalTR");
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
					return SpriteLoader.tileAtlas.findRegion("tiledIronBL");
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0 &&
				    minusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledIronBR");
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
				    plusy.getOreLevel() > 0 &&
				    minusx.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledIronTL");
				} else
				if (minusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
				    plusx.getOreLevel() > 0 &&
				    plusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledIronTR");
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					minusx.getOreLevel() > 0)  
				{	
					return desiredRegion;
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0)  
				{
					return desiredRegion;	
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					minusy.getOreLevel() > 0)  
				{
					return desiredRegion;
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() > 0)  
				{	
					return desiredRegion;
				} else 
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() == 0)  
				{
					return desiredRegion;
				}
			}
		
			if (!triangleDrawn) {
				return desiredRegion;
			}
		}
		
		switch(height) {
			case 0: return null;
			case 1: return SpriteLoader.tileAtlas.findRegion("1rock");
			case 2: return SpriteLoader.tileAtlas.findRegion("2rock");
			case 3: return SpriteLoader.tileAtlas.findRegion("3rock");
			case 4: return SpriteLoader.tileAtlas.findRegion("4sand");
			case 5: return SpriteLoader.tileAtlas.findRegion("5sand");
			case 6: return SpriteLoader.tileAtlas.findRegion("6dirt");
			case 7: return SpriteLoader.tileAtlas.findRegion("7grass");
			case 8: return SpriteLoader.tileAtlas.findRegion("8stone");
			case 9: return SpriteLoader.tileAtlas.findRegion("9concrete");
			case 10: return SpriteLoader.tileAtlas.findRegion("10concrete");			
		}
		
		return SpriteLoader.tileAtlas.findRegion("transparent16");				
	}
	
	private AtlasRegion resolveOres() {
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
					return SpriteLoader.tileAtlas.findRegion("tiledCoalBL");
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0 &&
				    minusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalBR");
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
				    plusy.getOreLevel() > 0 &&
				    minusx.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalTL");
				} else
				if (minusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
				    plusx.getOreLevel() > 0 &&
				    plusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledCoalTR");
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
					return SpriteLoader.tileAtlas.findRegion("tiledIronBL");
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0 &&
				    minusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledIronBR");
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 &&
				    plusy.getOreLevel() > 0 &&
				    minusx.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledIronTL");
				} else
				if (minusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
				    plusx.getOreLevel() > 0 &&
				    plusy.getOreLevel() > 0)  
				{
					return SpriteLoader.tileAtlas.findRegion("tiledIronTR");
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					minusx.getOreLevel() > 0)  
				{	
					return desiredRegion;
				} else
				if (plusy.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 && 
					minusy.getOreLevel() == 0 &&
					plusx.getOreLevel() > 0)  
				{
					return desiredRegion;	
				} else
				if (plusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					minusy.getOreLevel() > 0)  
				{
					return desiredRegion;
				} else
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() > 0)  
				{	
					return desiredRegion;
				} else 
				if (minusy.getOreLevel() == 0 && 
					plusx.getOreLevel() == 0 && 
					minusx.getOreLevel() == 0 &&
					plusy.getOreLevel() == 0)  
				{
					return desiredRegion;
				}
			}
		
			if (!triangleDrawn) {
				return desiredRegion;
			}
		}
		return null;
	}
	
	/**Returns an AtlasRegion representing this tiles terrain*/
	public AtlasRegion getTerrainTexture() {	
		if (this.getOreLevel() > 0) {
			AtlasRegion ore = resolveOres();
			if (ore != null) {
				return ore;
			}
		}
		
		switch(height) {
			case 0: return null;
			case 1: return resolveTerrainEdges(1, "1rock");
			case 2: return resolveTerrainEdges(2, "2rock");
			case 3: return resolveTerrainEdges(3, "3rock");
			case 4: return resolveTerrainEdges(4, "4sand");
			case 5: return resolveTerrainEdges(5, "5sand");
			case 6: return resolveTerrainEdges(6, "6dirt");
			case 7: return resolveTerrainEdges(7, "7grass");
			case 8: return resolveTerrainEdges(8, "8stone");
			case 9: return resolveTerrainEdges(9, "9concrete");
			case 10: return resolveTerrainEdges(10, "10concrete");			
		}
		
		return SpriteLoader.tileAtlas.findRegion("transparent16");				
	}
	
	/**Returns the appropriate {@link AtlasRegion} for the specified height
	 * @param level Target height level
	 * @param textureRoot Name of the default texture variation <br> (names are defined in {@link SpriteLoader#tileAtlas})
	 * */
	private AtlasRegion resolveTerrainEdges(int level, String textureRoot) {
		if (x > Base.CHUNK_SIZE && y > Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
			if (plusy.getHeight() < level &&
				plusx.getHeight() < level &&
				minusx.getHeight() >= level && 
				minusy.getHeight() >= level)
			{
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "BL");
			} else
			if (plusy.getHeight() < level && 
				plusx.getHeight() >= level &&
				minusx.getHeight() < level &&				 
			    minusy.getHeight() >= level)  
			{
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "BR");
			} else
			if (minusy.getHeight() < level && 
				plusx.getHeight() < level &&
			    plusy.getHeight() >= level &&
			    minusx.getHeight() >= level)  
			{
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "TL");
			} else
			if (minusy.getHeight() < level && 
				minusx.getHeight() < level && 
			    plusx.getHeight() >= level &&
			    plusy.getHeight() >= level)  
			{
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "TR");
			} else
				
			//Straight line borders
			if (plusx.getHeight() < level && 
				minusx.getHeight() >= level && 
				plusy.getHeight() >= level &&
				minusy.getHeight() >= level)  
			{
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "SR");
			} else
			if (plusx.getHeight() >= level && 
				minusx.getHeight() >= level && 
				plusy.getHeight() >= level &&
				minusy.getHeight() < level)  
			{	
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "SB");
			} else
			if (plusx.getHeight() >= level && 
				minusx.getHeight() < level &&
				plusy.getHeight()  >= level &&
				minusy.getHeight()  >= level)  
			{	
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "SL");
			} else
			if (plusx.getHeight()  >= level && 
				minusx.getHeight() >= level && 
				plusy.getHeight()  < level &&
				minusy.getHeight() >= level)  
			{
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "ST");	
			} else
				
			//Tile ends
			if (plusy.getHeight() < level && 
				plusx.getHeight() < level && 
				minusy.getHeight() < level &&
				minusx.getHeight() >= level)  
			{
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "LB");	
			} else
			if (plusy.getHeight() < level && 
				minusx.getHeight() < level && 
				minusy.getHeight() < level &&
				plusx.getHeight() >= level)  
			{	
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "RB");
			} else
			if (plusy.getHeight() < level && 
				plusx.getHeight() < level && 
				minusx.getHeight() < level &&
				minusy.getHeight() >= level)  
			{	
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "TB");
			} else
			if (minusy.getHeight() < level && 
				plusx.getHeight() < level && 
				minusx.getHeight() < level &&
				plusy.getHeight() >= level)  
			{
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "BB");	
			} else 
				
			//Single creeper tile
			if (minusy.getHeight() < level && 
				plusx.getHeight() < level && 
				minusx.getHeight() < level &&
				plusy.getHeight() < level)  
			{
				return SpriteLoader.tileAtlas.findRegion(textureRoot + "Single");  
			}
		}
		return SpriteLoader.tileAtlas.findRegion(textureRoot);
	}
	
	public AtlasRegion getCorruptionTexture(int level) {
 		if (this.creeper != 0) {
			if (this.getAbsoluteCreeperLevel() > level) {
				if (this.getAbsoluteCreeperLevel() <= level + 1) {
					return resolveCorruptionEdges(level);
				}
				
				if (this.getAbsoluteCreeperLevel() > level + 1 && this.getAbsoluteCreeperLevel() <= level + 2 && this.getCreeperLevel() > 1) {		
					if (resolveCorruptionEdges(level+1).name != SpriteLoader.tileAtlas.findRegion("corr32").name) {
						return SpriteLoader.tileAtlas.findRegion("corr32");
					}					
				}
			} 					
		}
		return null;
	}
	
	private AtlasRegion resolveCorruptionEdges(int level) {
		if (x > Base.CHUNK_SIZE && y > Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
			if (plusy.getAbsoluteCreeperLevel() <= level &&
				plusx.getAbsoluteCreeperLevel() <= level &&
				minusx.getAbsoluteCreeperLevel() > level && 
				minusy.getAbsoluteCreeperLevel() > level)
			{
				return SpriteLoader.tileAtlas.findRegion("corrBL");
			} else
			if (plusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() > level &&
				minusx.getAbsoluteCreeperLevel() <= level &&				 
			    minusy.getAbsoluteCreeperLevel() > level)  
			{
				return SpriteLoader.tileAtlas.findRegion("corrBR");
			} else
			if (minusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level &&
			    plusy.getAbsoluteCreeperLevel() > level &&
			    minusx.getAbsoluteCreeperLevel() > level)  
			{
				return SpriteLoader.tileAtlas.findRegion("corrTL");
			} else
			if (minusy.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level && 
			    plusx.getAbsoluteCreeperLevel() > level &&
			    plusy.getAbsoluteCreeperLevel() > level)  
			{
				return SpriteLoader.tileAtlas.findRegion("corrTR");
			} else
				
			//Straight line borders
			if (plusx.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() > level && 
				plusy.getAbsoluteCreeperLevel() > level &&
				minusy.getAbsoluteCreeperLevel() > level)  
			{
				return SpriteLoader.tileAtlas.findRegion("corrSR");
			} else
			if (plusx.getAbsoluteCreeperLevel() > level && 
				minusx.getAbsoluteCreeperLevel() > level && 
				plusy.getAbsoluteCreeperLevel() > level &&
				minusy.getAbsoluteCreeperLevel() <= level)  
			{	
				return SpriteLoader.tileAtlas.findRegion("corrSB");
			} else
			if (plusx.getAbsoluteCreeperLevel() > level && 
				minusx.getAbsoluteCreeperLevel() <= level &&
				plusy.getAbsoluteCreeperLevel()  > level &&
				minusy.getAbsoluteCreeperLevel()  > level)  
			{	
				return SpriteLoader.tileAtlas.findRegion("corrSL");
			} else
			if (plusx.getAbsoluteCreeperLevel()  > level && 
				minusx.getAbsoluteCreeperLevel() > level && 
				plusy.getAbsoluteCreeperLevel()  <= level &&
				minusy.getAbsoluteCreeperLevel() > level)  
			{
				return SpriteLoader.tileAtlas.findRegion("corrST");	
			} else
				
			//Tile ends
			if (plusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level && 
				minusy.getAbsoluteCreeperLevel() <= level &&
				minusx.getAbsoluteCreeperLevel() > level)  
			{
				return SpriteLoader.tileAtlas.findRegion("corrLB");	
			} else
			if (plusy.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level && 
				minusy.getAbsoluteCreeperLevel() <= level &&
				plusx.getAbsoluteCreeperLevel() > level)  
			{	
				return SpriteLoader.tileAtlas.findRegion("corrRB");
			} else
			if (plusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level &&
				minusy.getAbsoluteCreeperLevel() > level)  
			{	
				return SpriteLoader.tileAtlas.findRegion("corrTB");
			} else
			if (minusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level &&
				plusy.getAbsoluteCreeperLevel() > level)  
			{
				return SpriteLoader.tileAtlas.findRegion("corrBB");	
			} else 
				
			//Single creeper tile
			if (minusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level &&
				plusy.getAbsoluteCreeperLevel() <= level)  
			{
				return SpriteLoader.tileAtlas.findRegion("corrSingle");
			}
		}
		return SpriteLoader.tileAtlas.findRegion("corr32");
	}
	
	/**Method that populates all the neighbour chunks*/
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
	
	/**Sets the {@linkplain #creeper} and {@linkplain #absCreeper} values. Also makes sure the {@linkplain #creeper} is within it's bounds.
	 * It makes sure the creeper doesn't exceed the {@link Base#MAX_CREEP} value with respect to this chunks terrain {@link #height}.
	 * 
	 * @param newCreeper The new creeper value
	 * */
	public void setCreeperLevel(float newCreeper) {	
		if (newCreeper > Base.MAX_CREEP - height) {
			newCreeper = Base.MAX_CREEP - height;
		}
		if (newCreeper < 0.05f) {
			newCreeper = 0;
		}			
		creeper = newCreeper;
		
		if (newCreeper != 0) {
			absCreeper = newCreeper + height;
		} else {
			absCreeper = 0;
		}
	}
	
	/**@return Current {@link #creeper} level */
	public float getCreeperLevel() {
		return creeper;
	}
	
	/**Returns the combined value of {@linkplain #creeper} and {@linkplain #height} 
	 * @return Current {@link #absCreeper} level */
	public float getAbsoluteCreeperLevel() {
		return absCreeper;
	}
	
	/**Sets the terrain {@linkplain #height} of this chunk
	 * @param level The new terrain height level
	 * */
	public void setHeight(int level) {
		if (level > Base.MAX_HEIGHT) {
			level = Base.MAX_HEIGHT;
		}
		if (level < 0) {
			level = 0;
		}
		
		height = level;
	}
	
	/**@return Current terrain {@link #height}*/
	public float getHeight() {
		return height;
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
