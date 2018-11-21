package org.dudss.nodeshot.terrain;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks.OreType;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

/**A square tile representation*/
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
	
	//Flags this tile as a terrain edge
	boolean edge = false;
	float edgeCreeper = 0f;
	
	boolean toExpand = false;
	long lastUpdate = SimulationThread.simTick;
	long updateRate = 5;
	
	enum TriangleOrientation {
		BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT, END_LEFT, END_RIGHT, END_TOP, END_BOTTOM, SINGLE
	}
	
	//Coordinates in tile space
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
	
	/**A square tile representation, x and y coordinates specify the lower left corner position*/
	Chunk(float x, float y) {
		this.x = x;
		this.y = y;	
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
		
	/**Container class that can hold multiple {@link AtlasRegion}s*/
	class TextureContainer {	
		int size = 0;
		AtlasRegion[] textures;
		
		/**Container class that can hold multiple {@link AtlasRegion}s
		 * @param textures Array of specified textures, they can be accessed with {@link #getTexture(int)} method
		 * with an index corresponding to their position in this array*/
		public TextureContainer(AtlasRegion... textures) {
			size = textures.length;
			this.textures = new AtlasRegion[textures.length];
			for (int i = 0; i < textures.length; i++) {
				this.textures[i] = textures[i];
			}
		}
		
		/**Returns an {@linkplain AtlasRegion} corresponding to the index
		 * @param index Index of the {@linkplain AtlasRegion}
		 * @return {@link AtlasRegion}, null if the index is out of bounds
		 * */
		public AtlasRegion getTexture(int index) {
			if (index > textures.length - 1) {
				return null;
			}
			return textures[index];
		}
		
		/**Get number of textures in this container*/
		public int getSize() {
			return size;
		}
	}
	
	/**Returns an AtlasRegion representing this tiles terrain*/
	public TextureContainer getTerrainTexture() {	
		//Ore detection
		if (Base.drawOres) {
			if (this.getOreLevel() > 0) {
				AtlasRegion ore = getOreTexture();
				if (ore != null) {
					return new TextureContainer(ore);
				}
			}
		}
		
		switch(height) {
			case 0: return null;
			case 1: return resolveTerrainEdges(1);
			case 2: return resolveTerrainEdges(2);
			case 3: return resolveTerrainEdges(3);
			case 4: return resolveTerrainEdges(4);
			case 5: return resolveTerrainEdges(5);
			case 6: return resolveTerrainEdges(6);
			case 7: return resolveTerrainEdges(7);
			case 8: return resolveTerrainEdges(8);
			case 9: return resolveTerrainEdges(9);
			case 10: return resolveTerrainEdges(10);			
		}
		
		return null;		
	}
	
	/**Returns the appropriate {@link AtlasRegion} for the specified height
	 * @param level Target height level
	 * @param textureRoot Name of the default texture variation <br> (names are defined in {@link SpriteLoader#tileAtlas})
	 * */
	private TextureContainer resolveTerrainEdges(int level) {
		if (x > Base.CHUNK_SIZE && y > Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
			edge = true;
			int lowerLevel = 0;
			if (plusy.getHeight() < level &&
				plusx.getHeight() < level &&
				minusx.getHeight() >= level && 
				minusy.getHeight() >= level) 
			{	
				int h1 = (int) plusx.getHeight();
				int h2 = (int) plusy.getHeight();
				if (h1 == h2) {
					lowerLevel = level - h1;
					return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "BL"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
				
			} else
			if (plusy.getHeight() < level && 
				plusx.getHeight() >= level &&
				minusx.getHeight() < level &&				 
			    minusy.getHeight() >= level)  
			{
				int h1 = (int) plusy.getHeight();
				int h2 = (int) minusx.getHeight();
				if (h1 == h2) {
					lowerLevel = level - h1;
					return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "BR"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
			} else
			if (minusy.getHeight() < level && 
				plusx.getHeight() < level &&
			    plusy.getHeight() >= level &&
			    minusx.getHeight() >= level)  
			{
				int h1 = (int) minusy.getHeight();
				int h2 = (int) plusx.getHeight();
				if (h1 == h2) {
					lowerLevel = level - h1;
					return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "TL"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
			} else
			if (minusy.getHeight() < level && 
				minusx.getHeight() < level && 
			    plusx.getHeight() >= level &&
			    plusy.getHeight() >= level)  
			{
				int h1 = (int) minusy.getHeight();
				int h2 = (int) minusx.getHeight();
				if (h1 == h2) {
					lowerLevel = level - h1;
					return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "TR"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
			} else
				
			//Straight line borders
			edge = false;
			if (plusx.getHeight() < level && 
				minusx.getHeight() >= level && 
				plusy.getHeight() >= level &&
				minusy.getHeight() >= level)  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "SR"));
			} else
			if (plusx.getHeight() >= level && 
				minusx.getHeight() >= level && 
				plusy.getHeight() >= level &&
				minusy.getHeight() < level)  
			{	
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "SB"));
			} else
			if (plusx.getHeight() >= level && 
				minusx.getHeight() < level &&
				plusy.getHeight()  >= level &&
				minusy.getHeight()  >= level)  
			{	
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "SL"));
			} else
			if (plusx.getHeight()  >= level && 
				minusx.getHeight() >= level && 
				plusy.getHeight()  < level &&
				minusy.getHeight() >= level)  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "ST"));	
			} else
				
			//Tile ends
			edge = true;
			if (plusy.getHeight() < level && 
				plusx.getHeight() < level && 
				minusy.getHeight() < level &&
				minusx.getHeight() >= level)  
			{
				int h1 = (int) plusx.getHeight();
				int h2 = (int) plusy.getHeight();
				int h3 = (int) minusy.getHeight();
				if ((h1 == h2) && (h2 == h3)) {
					lowerLevel = level - h1;
					return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "RB"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
			} else
			if (plusy.getHeight() < level && 
				minusx.getHeight() < level && 
				minusy.getHeight() < level &&
				plusx.getHeight() >= level)  
			{	
				int h1 = (int) plusy.getHeight();
				int h2 = (int) minusx.getHeight();
				int h3 = (int) minusy.getHeight();
				if ((h1 == h2) && (h2 == h3)) {
					lowerLevel = level - h1;
					return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "LB"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
			} else
			if (plusy.getHeight() < level && 
				plusx.getHeight() < level && 
				minusx.getHeight() < level &&
				minusy.getHeight() >= level)  
			{	
				int h1 = (int) plusx.getHeight();
				int h2 = (int) plusy.getHeight();
				int h3 = (int) minusx.getHeight();
				if ((h1 == h2) && (h2 == h3)) {
					lowerLevel = level - h1;
					return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "TB"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
			} else
			if (minusy.getHeight() < level && 
				plusx.getHeight() < level && 
				minusx.getHeight() < level &&
				plusy.getHeight() >= level)  
			{
				int h1 = (int) plusx.getHeight();
				int h2 = (int) minusy.getHeight();
				int h3 = (int) minusx.getHeight();
				if ((h1 == h2) && (h2 == h3)) {
					lowerLevel = level - h1;
					return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "BB"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));	
				}
			} else 
				
			//Single creeper tile
			if (minusy.getHeight() < level && 
				plusx.getHeight() < level && 
				minusx.getHeight() < level &&
				plusy.getHeight() < level)  
			{ 
				int h1 = (int) plusx.getHeight();
				int h2 = (int) plusy.getHeight();
				int h3 = (int) minusx.getHeight();
				int h4 = (int) minusy.getHeight();
				if ((h1 == h2) && (h2 == h3) && (h3 == h4)) {
					lowerLevel = level - h1;
					return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "Single"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
			}
		}
		edge = false;
		return new TextureContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level]));
	}
	
	private AtlasRegion getOreTexture() {
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
	
	public TextureContainer getCorruptionTexture(int level) {
		this.edgeCreeper = 0;
 		if (this.creeper != 0) {
			if (this.getAbsoluteCreeperLevel() > level) {
				if (this.getAbsoluteCreeperLevel() <= level + 1) {
					return resolveCorruptionEdges(level);
				}
				
				if (this.getAbsoluteCreeperLevel() > level + 1 && this.getAbsoluteCreeperLevel() <= level + 2 && this.getCreeperLevel() > 1) {		
					if (resolveCorruptionEdges(level+1).getTexture(0).name != SpriteLoader.tileAtlas.findRegion("corr32").name) {
						return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corr32"));
					}					
				}
			} 					
		}
 		
 		/*if (this.edge) {
 			if (plusx.getAbsoluteCreeperLevel() > level &&
				plusy.getAbsoluteCreeperLevel() <= level &&
				minusx.getAbsoluteCreeperLevel() <= level && 
				minusy.getAbsoluteCreeperLevel() > level && 
 				minusy.getAbsoluteCreeperLevel() <= level + 1)
			{
 				edgeCreeper = level;
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrBR"));
			} else
 			if (plusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() > level &&  
				minusy.getAbsoluteCreeperLevel() > level && 	
 				minusy.getAbsoluteCreeperLevel() <= level + 1)
 			{
 				edgeCreeper = level;
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrBL"));
 			} else
 			if (minusy.getAbsoluteCreeperLevel() <= level &&
				plusx.getAbsoluteCreeperLevel() <= level && 
				plusy.getAbsoluteCreeperLevel() > level &&   
				minusx.getAbsoluteCreeperLevel() > level &&    
 				minusx.getAbsoluteCreeperLevel() <= level + 1)
 			{
 				edgeCreeper = level;
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrTL"));
 			} else
 			if (minusy.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() > level &&    
				plusy.getAbsoluteCreeperLevel() > level &&      
 				plusx.getAbsoluteCreeperLevel() <= level + 1)
 			{
 				edgeCreeper = level;
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrTR"));
 			}
 		}
 		*/
		return null;
	}
	
	private TextureContainer resolveCorruptionEdges(int level) {
		if (x > Base.CHUNK_SIZE && y > Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
			if (plusy.getAbsoluteCreeperLevel() <= level &&
				plusx.getAbsoluteCreeperLevel() <= level &&
				minusx.getAbsoluteCreeperLevel() > level && 
				minusy.getAbsoluteCreeperLevel() > level) //&&
		    	//(!plusx.isEdge() || !plusy.isEdge()))				
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrBL"));
			} else
			if (plusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() > level &&
				minusx.getAbsoluteCreeperLevel() <= level &&				 
			    minusy.getAbsoluteCreeperLevel() > level) //&&
			    //(!plusy.isEdge() || !minusx.isEdge()))  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrBR"));
			} else
			if (minusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level &&
			    plusy.getAbsoluteCreeperLevel() > level &&
			    minusx.getAbsoluteCreeperLevel() > level) //&&
		    	//(!plusx.isEdge() || !minusy.isEdge()))  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrTL"));
			} else
			if (minusy.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level && 
			    plusx.getAbsoluteCreeperLevel() > level &&
			    plusy.getAbsoluteCreeperLevel() > level) //&&
		    	//(!minusx.isEdge() || !minusy.isEdge()))  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrTR"));
			} else
				
			//Straight line borders
			if (plusx.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() > level && 
				plusy.getAbsoluteCreeperLevel() > level &&
				minusy.getAbsoluteCreeperLevel() > level)  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrSR"));
			} else
			if (plusx.getAbsoluteCreeperLevel() > level && 
				minusx.getAbsoluteCreeperLevel() > level && 
				plusy.getAbsoluteCreeperLevel() > level &&
				minusy.getAbsoluteCreeperLevel() <= level)  
			{	
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrSB"));
			} else
			if (plusx.getAbsoluteCreeperLevel() > level && 
				minusx.getAbsoluteCreeperLevel() <= level &&
				plusy.getAbsoluteCreeperLevel()  > level &&
				minusy.getAbsoluteCreeperLevel()  > level)  
			{	
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrSL"));
			} else
			if (plusx.getAbsoluteCreeperLevel()  > level && 
				minusx.getAbsoluteCreeperLevel() > level && 
				plusy.getAbsoluteCreeperLevel()  <= level &&
				minusy.getAbsoluteCreeperLevel() > level)  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrST"));	
			} else
				
			//Tile ends
			if (plusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level && 
				minusy.getAbsoluteCreeperLevel() <= level &&
				minusx.getAbsoluteCreeperLevel() > level)  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrLB"));	
			} else
			if (plusy.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level && 
				minusy.getAbsoluteCreeperLevel() <= level &&
				plusx.getAbsoluteCreeperLevel() > level)  
			{	
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrRB"));
			} else
			if (plusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level &&
				minusy.getAbsoluteCreeperLevel() > level)  
			{	
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrTB"));
			} else
			if (minusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level &&
				plusy.getAbsoluteCreeperLevel() > level)  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrBB"));	
			} else 
				
			//Single creeper tile
			if (minusy.getAbsoluteCreeperLevel() <= level && 
				plusx.getAbsoluteCreeperLevel() <= level && 
				minusx.getAbsoluteCreeperLevel() <= level &&
				plusy.getAbsoluteCreeperLevel() <= level)  
			{
				return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corrSingle"));
			}
		}
		return new TextureContainer(SpriteLoader.tileAtlas.findRegion("corr32"));
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
	
	/**@return Returns true if this tile is a terrain edge*/
	public boolean isEdge() {
		return edge;
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
