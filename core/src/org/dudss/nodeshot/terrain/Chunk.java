package org.dudss.nodeshot.terrain;

import java.util.Arrays;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks.OreType;
import org.dudss.nodeshot.terrain.Chunks.Visibility;
import org.dudss.nodeshot.terrain.datasubsets.AtlasRegionContainer;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

/**A square tile representation.
 * @see Managed by {@link Chunks}.
 * */
public class Chunk {
	float x, y;
	
	/**World space width and height of this {@linkplain Chunk}*/
	float size = Base.CHUNK_SIZE;
	
	/**Fog of war visibility value
	 * 0.8f -> deactivated
	 * 0.4f -> activated but not in vision
	 * 0f -> active vision
	 * */
	public float visibility = deactivated;
	int visionProviderNumber = 0;
	
	public static float active = 0f;
	public static float semiactive = 0.2f;
	public static float deactivated = 0.35f;
	
	boolean borderChunk = false;
	
	boolean isOreOutlined = false;
	
	/**Building atop of this chunk*/
	AbstractBuilding building = null;
	
	/**Section that contains this chunk*/
	Section section;

	/**Coal ore level*/
	float coalOre = 1f;
	/**Iron ore level*/
	float ironOre = 0f;
	
	/**The creeper level*/ 
	float creeper = 0;	
	
	/**THe minimum creeper level*/
	float creeperMinimum = 0.01f;
	
	/**Absolute creeper ({@link #c_height} + {@link #creeper} when {@linkplain #creeper} > 0)*/
	float absCreeper = 0;
	
	float flowRate = 0.1f; //0.25
	float spreadThreshold = 0.2f; //0.5
	
	/**A buffer that holds the creeper change between simulation ticks*/
	public float creeperChange = 0;
	
	long lastUpdate = SimulationThread.simTick;
	long updateRate = 5;		
	
	/**Terrain height*/
	int height;
	
	/**<b>Terrain height modified for corruption spread</b><br>
	 * When part of a diagonal edge ({@link #diagonalEdges}) c_height represents height of the lower half of the edge.<br>
	 * All creeper updates and edge detection uses c_height instead of {@linkplain height} and this allows it to partially flood
	 * terrain edges (which removes visual gaps in-between terrain and corruption edges).*/
	int c_height;
	
	/**The offset between {@link height} and {@link c_height}. Tells what terrain layer is the lower terrain part of the edge.<br>
	 * <b>Example:</b> An edge between height 5 and 2. Height is 5, edgeLowerHeight is (5-2) 3 and thus c_height is (height - edgeLowerHeight) 2.*/
	int edgeLowerHeight = 0;
	
	/**Types of edges*/
	public enum EdgeType {
		BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT,
		END_LEFT, END_RIGHT, END_TOP, END_BOTTOM,
		STRAIGHT_LEFT, STRAIGHT_RIGHT, STRAIGHT_TOP, STRAIGHT_BOTTOM,
		DOUBLE_X, DOUBLE_Y,
		SINGLE, NONE
	}
	
	/**List of all {@link EdgeType}s that are considered diagonal and creeper may partially flow into the chunk*/
	public List<EdgeType> diagonalEdges = Arrays.asList(
			EdgeType.BOTTOM_LEFT,
			EdgeType.BOTTOM_RIGHT,
			EdgeType.TOP_LEFT,
			EdgeType.TOP_RIGHT
	);
	
	/**Flags this tile as a terrain edge*/
	boolean terrainEdge = false;
	EdgeType terrainEdgeType = EdgeType.NONE;
	
	/**Flags this tile as a corruption edge*/
	boolean corrEdge = false;
	EdgeType corrEdgeType = EdgeType.NONE;
	
	/**X coordinate in tile space*/
	int ax;
	/**Y coordinate in tile space*/
	int ay;
	
	Chunk minusx;
	Chunk plusx;
	Chunk minusy;
	Chunk plusy;
	
	Chunk cornerTopLeft;
	Chunk cornerTopRight;
	Chunk cornerBottomLeft;
	Chunk cornerBottomRight;
	
	/**Array of surrounding neighbour chunks.<br>
		neighbours[0] = plusy;<br>
		neighbours[1] = cornerTopRight;<br>
		neighbours[2] = plusx;<br>
		neighbours[3] = cornerBottomRight;<br>
		neighbours[4] = minusy;<br>
		neighbours[5] = cornerBottomLeft;<br>
		neighbours[6] = minusx;<br>
		neighbours[7] = cornerTopLeft;
	 */
	Chunk[] neighbours;
	
	/**A square tile representation, x and y coordinates specify the lower left corner position in world space.*/
	Chunk(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	//TODO: rewrite the update -> simpler more "fluid" like behaviour 
	/**Current corruption update method, creeper is distributed along the tiles with lower creeper in relation to the difference from this chunks {@link #creeper}.
	 * The resulting creeper level changes are saved into respectable {@link #creeperChange} variables and the actual creeper is set later
	 * using the {@link #applyUpdate()} method.
	 * This is done to remove an axial bias in the direction of the subsequent updates.
	 * @return Returns whether an actual corruption update happened or not, used for optimisation.
	 */
	public boolean update() {		
		if (this.getCreeperLevel() != 0 && visibility != deactivated) {
	        float thisTotal = this.c_height + this.getCreeperLevel();
			
	        //Top neighbour
	        if (this.ay < Base.CHUNK_AMOUNT - 1) {
	        	this.transferCreeper(thisTotal, plusy);
	        }        
	        //Bottom neighbour
	        if (this.ay > 0) {
	        	this.transferCreeper(thisTotal, minusy);
	        }
	        //Right neighbour
	        if (this.ax < Base.CHUNK_AMOUNT - 1) {
	        	this.transferCreeper(thisTotal, plusx);
	        }
	        //Left neighbour
	        if (this.ax > 0) {
	        	this.transferCreeper(thisTotal, minusx);
	        }
	        return true;
		}
		return false;
	}
	
	private void transferCreeper(float thisTotal, Chunk to) {
        if (to.c_height >= 0) {
            if (this.getCreeperLevel() > 0 || to.getCreeperLevel() > 0) {
                float targetTotal = to.c_height + to.getCreeperLevel();
                float delta = 0f;
                if (thisTotal > targetTotal) {
                    delta = thisTotal - targetTotal;
                    if (delta > this.getCreeperLevel()) {
                    	delta = this.getCreeperLevel();
                    }
                    float adjustedDelta = delta * flowRate;
                    this.creeperChange -= adjustedDelta;
                    to.creeperChange += adjustedDelta;
                    if (to.isBorderChunk()) {
                    	if (to.getSection() != this.getSection()) {
                    		to.getSection().setActive(true);
                    	}
                    }
                }
            }
        }
	}
	
	/**Called after all the other {@link Chunk}s had been updated using the {@link #update()} method in the current simulation tick*/
	public void applyUpdate() {
		setCreeperLevel(creeper + creeperChange);
		creeperChange = 0;
	}
		
	/**Returns an {@link AtlasRegion} representing this {@linkplain Chunk}s terrain.*/
	public AtlasRegionContainer getTerrainTexture() {			
		AtlasRegionContainer arc = new AtlasRegionContainer();
		
		//Draw mine outlines
		if (this.isOreOutlined) {
			AtlasRegion outline = resolveOutlineEdges();
			if (outline != null) {
				arc.addTexture(outline);
			}
		}		
		
		//Ore detection
		if (Base.drawOres) {
			if (this.getOreLevel() > 0) {
				AtlasRegion ore = getOreTexture();
				if (ore != null) {
					arc.addTexture(ore);
				}
			}
		}
		
		//Draw build outlines
		if (GameScreen.buildMode) {
			if (this.isDiagonalTerrainEdge() || this.getBuilding() != null) {
				arc.addTexture(SpriteLoader.tileAtlas.findRegion("buildOverlayRed"));
			} else {
				arc.addTexture(SpriteLoader.tileAtlas.findRegion("buildOverlayGreen"));
			}
		}
		
		
		switch(height) {
			case 0: this.setTerrainEdge(false, 0, EdgeType.NONE); return null; //TODO: ore at level 0?
			case 1: arc.addContainer(resolveTerrainEdges(1)); break;
			case 2: arc.addContainer(resolveTerrainEdges(2)); break;
			case 3: arc.addContainer(resolveTerrainEdges(3)); break;
			case 4: arc.addContainer(resolveTerrainEdges(4)); break;
			case 5: arc.addContainer(resolveTerrainEdges(5)); break;
			case 6: arc.addContainer(resolveTerrainEdges(6)); break;
			case 7: arc.addContainer(resolveTerrainEdges(7)); break;
			case 8: arc.addContainer(resolveTerrainEdges(8)); break;
			case 9: arc.addContainer(resolveTerrainEdges(9)); break;
			case 10: arc.addContainer(resolveTerrainEdges(10)); break;	
		}
		return arc;		
	}

	/**Returns the appropriate {@link AtlasRegion} for the specified terrain height.
	 * @param level Target height level.
	 * */
	private AtlasRegionContainer resolveTerrainEdges(int level) {
		if (x >= Base.CHUNK_SIZE && y >= Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
			int lowerLevel = 0;
			//Diagonal edges
			if (plusy.getHeight() < level &&
				plusx.getHeight() < level &&
				minusx.getHeight() >= level && 
				minusy.getHeight() >= level) 
			{	
				int h1 = (int) plusx.getHeight();
				int h2 = (int) plusy.getHeight();
				if (h1 == h2) {
					lowerLevel = level - h1;
					setTerrainEdge(true, lowerLevel, EdgeType.BOTTOM_LEFT);
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "BL"),
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
					setTerrainEdge(true, lowerLevel, EdgeType.BOTTOM_RIGHT);
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "BR"),
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
					setTerrainEdge(true, lowerLevel, EdgeType.TOP_LEFT);
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "TL"),
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
					setTerrainEdge(true, lowerLevel, EdgeType.TOP_RIGHT);
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "TR"),
													SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
			} else
				
			//Straight line borders
			if (plusx.getHeight() < level && 
				minusx.getHeight() >= level && 
				plusy.getHeight() >= level &&
				minusy.getHeight() >= level)  
			{
				setTerrainEdge(true, 0, EdgeType.STRAIGHT_RIGHT);
				lowerLevel = level - (int)plusx.getHeight();
				return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "SR"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
			} else
			if (plusx.getHeight() >= level && 
				minusx.getHeight() >= level && 
				plusy.getHeight() >= level &&
				minusy.getHeight() < level)  
			{	
				setTerrainEdge(true, 0, EdgeType.STRAIGHT_BOTTOM);
				lowerLevel = level - (int)minusy.getHeight();
				return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "SB"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
			} else
			if (plusx.getHeight() >= level && 
				minusx.getHeight() < level &&
				plusy.getHeight()  >= level &&
				minusy.getHeight()  >= level)  
			{	
				setTerrainEdge(true, 0, EdgeType.STRAIGHT_LEFT);
				lowerLevel = level - (int)minusx.getHeight();
				return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "SL"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
			} else
			if (plusx.getHeight()  >= level && 
				minusx.getHeight() >= level && 
				plusy.getHeight()  < level &&
				minusy.getHeight() >= level)  
			{
				setTerrainEdge(true, 0, EdgeType.STRAIGHT_TOP);
				lowerLevel = level - (int)plusy.getHeight();
				return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "ST"),
												SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
			} else
				
			//Tile ends
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
					setTerrainEdge(true, lowerLevel, EdgeType.END_RIGHT);
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "RB"),
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
					setTerrainEdge(true, lowerLevel, EdgeType.END_LEFT);
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "LB"),
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
					setTerrainEdge(true, lowerLevel, EdgeType.END_TOP);
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "TB"),
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
					setTerrainEdge(true, lowerLevel, EdgeType.END_BOTTOM);
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "BB"),
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
					setTerrainEdge(true, lowerLevel, EdgeType.SINGLE);
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level] + "Single"),
													SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level-lowerLevel]));
				}
			}
		}
		setTerrainEdge(false, 0, EdgeType.NONE);
		return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion(Chunks.terrainLayerNames[level]));
	}
	
	/**Returns the appropriate ore {@link AtlasRegion} for this {@linkplain Chunk}.*/
	private AtlasRegion getOreTexture() {
		if (coalOre != 0) {	
			if (coalOre > 0.25f) {
				return SpriteLoader.tileAtlas.findRegion("renderedCoalLevel1");
			} else {
				return SpriteLoader.tileAtlas.findRegion("renderedCoalLevel0");
			}
		}	
	
		if (ironOre != 0) {
			if (ironOre > 0.25f) {
				return SpriteLoader.tileAtlas.findRegion("renderedIronLevel1");
			} else {
				return SpriteLoader.tileAtlas.findRegion("renderedIronLevel0");
			}		
		}
		return null;
	}
	
	/**Returns the appropriate texture for mine ore outlining*/
	public AtlasRegion resolveOutlineEdges() {
		if (x >= Base.CHUNK_SIZE && y >= Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
			if (plusy.isOreOutlined() &&
				plusx.isOreOutlined() &&
				minusx.isOreOutlined() && 
				minusy.isOreOutlined()) 
			{					
				return SpriteLoader.tileAtlas.findRegion("outlineEmpty");
			} else
			
			//Diagonal edges
			if (plusy.isOreOutlined() &&
				plusx.isOreOutlined() &&
				minusx.isOreOutlined() == false && 
				minusy.isOreOutlined() == false) 
			{					
				return SpriteLoader.tileAtlas.findRegion("outlineBL");
			} else
			if (plusy.isOreOutlined() && 
				plusx.isOreOutlined() == false &&
				minusx.isOreOutlined() &&				 
			    minusy.isOreOutlined() == false)  
			{
				return SpriteLoader.tileAtlas.findRegion("outlineBR");
			} else
			if (minusy.isOreOutlined() && 
				plusx.isOreOutlined() &&
			    plusy.isOreOutlined() == false &&
			    minusx.isOreOutlined() == false)  
			{
				return SpriteLoader.tileAtlas.findRegion("outlineTL");
			} else
			if (minusy.isOreOutlined() && 
				minusx.isOreOutlined()&& 
			    plusx.isOreOutlined() == false &&
			    plusy.isOreOutlined() == false)  
			{
				return SpriteLoader.tileAtlas.findRegion("outlineTR");								
			} else
				
			//Straight line borders
			if (plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() && 
				plusy.isOreOutlined() &&
				minusy.isOreOutlined())  
			{
				return SpriteLoader.tileAtlas.findRegion("outlineSR");
			} else
			if (plusx.isOreOutlined() && 
				minusx.isOreOutlined() && 
				plusy.isOreOutlined() &&
				minusy.isOreOutlined() == false)  
			{
				return SpriteLoader.tileAtlas.findRegion("outlineSB");
			} else
			if (plusx.isOreOutlined() && 
				minusx.isOreOutlined() == false && 
				plusy.isOreOutlined() &&
				minusy.isOreOutlined())  
			{
				return SpriteLoader.tileAtlas.findRegion("outlineSL");
			} else
			if (plusx.isOreOutlined() && 
				minusx.isOreOutlined() && 
				plusy.isOreOutlined() == false &&
				minusy.isOreOutlined())  
			{
				return SpriteLoader.tileAtlas.findRegion("outlineST");
			} else
				
			//Tile ends
			if (plusy.isOreOutlined() == false && 
				plusx.isOreOutlined() == false && 
				minusy.isOreOutlined() == false &&
				minusx.isOreOutlined())  
			{
				return SpriteLoader.tileAtlas.findRegion("outlineRB");
			} else 
			if (plusy.isOreOutlined() == false && 
				minusx.isOreOutlined() == false && 
				minusy.isOreOutlined() == false &&
				plusx.isOreOutlined())  
			{	
				return SpriteLoader.tileAtlas.findRegion("outlineLB");
			} else
			if (plusy.isOreOutlined() == false && 
				plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() == false &&
				minusy.isOreOutlined())  
			{	
				return SpriteLoader.tileAtlas.findRegion("outlineTB");
			} else
			if (minusy.isOreOutlined() == false && 
				plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() == false &&
				plusy.isOreOutlined())  
			{			
				return SpriteLoader.tileAtlas.findRegion("outlineBB");
			} else 
				
			//Single creeper tile
			if (minusy.isOreOutlined() == false && 
				plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() == false &&
				plusy.isOreOutlined() == false)  
			{ 
				return SpriteLoader.tileAtlas.findRegion("outlineSingle");
			} else
			
			if (minusy.isOreOutlined() == false && 
				plusx.isOreOutlined() && 
				minusx.isOreOutlined() &&
				plusy.isOreOutlined() == false)  
			{ 
				return SpriteLoader.tileAtlas.findRegion("outlineYBS");
			} else
			if (minusy.isOreOutlined() && 
				plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() == false &&
				plusy.isOreOutlined())  
			{ 
				return SpriteLoader.tileAtlas.findRegion("outlineXBS");
			}
		}
		return null;
	}
	
	/**Returns the appropriate corruption texture of this {@linkplain Chunk}.
	 * Does all necessary edge checks and returns null when needed.
	 * @since <b>v5.0 (30.11.18)</b><br>The tile texture resolve rewritten to use a single mesh on a single layer. Edges are not resolved by layer anymore.
	 * Before, there were {@link Base#MAX_CREEP} individual meshes for every {@link Section}. So multiple corruption layers were rendered on top of each other (with culling).
	 * By unifying all these meshes into a single textured mesh performance will increase (We no longer need to cycle all chunks multiple times).
	 * This change was allowed because of the usage of a glsl shader that combines multiple textures within a single draw.
	 * @see {@link #resolveCorruptionEdges()}*/
	public AtlasRegionContainer getCorruptionTexture() {
 		if (this.creeper != 0) {
			return resolveCorruptionEdges();					
 		} 
		return null;
	}
	
	/**Returns the appropriate edge {@link AtlasRegionContainer}.
	 * {@link AtlasRegionContainer} allows us to return multiple textures in a certain order. It can also hold a special secondary shade value ({@link AtlasRegionContainer#getSecondaryShade()})
	 * As of right now the corruption renderer and shader supports 2 individual textures that can be overlaid over each other. (Thats why there is just one secondary shade value).
	 * @see {@link #getCorruptionTexture()}.
	 * */
	private AtlasRegionContainer resolveCorruptionEdges() {
		if (x >= Base.CHUNK_SIZE && y >= Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) {
			//Diagonal edges
			float level = this.getAbsoluteCreeperLayer();
			if ((plusy.getAbsoluteCreeperLayer() < level )&& 
				(plusx.getAbsoluteCreeperLayer() < level )&&
				minusx.getAbsoluteCreeperLayer() >= level &&
				minusy.getAbsoluteCreeperLayer() >= level)
			{
				setCorruptionEdge(true, EdgeType.BOTTOM_LEFT);
				if (plusx.getCreeperLevel() == 0 && plusy.getCreeperLevel() == 0) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrBL"));	
				} else if (plusx.getAbsoluteCreeperLayer() == plusy.getAbsoluteCreeperLayer()) {
					return new AtlasRegionContainer(plusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBL"), SpriteLoader.tileAtlas.findRegion("corr32"));									
				} else if (plusx.getAbsoluteCreeperLayer() > plusy.getAbsoluteCreeperLayer()) {
					return new AtlasRegionContainer(plusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBL"), SpriteLoader.tileAtlas.findRegion("corrST"));	
				} else {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBL"), SpriteLoader.tileAtlas.findRegion("corrSR"));	
				}
			} else
			if (plusy.getAbsoluteCreeperLayer() < level && 
				plusx.getAbsoluteCreeperLayer() >= level &&
				minusx.getAbsoluteCreeperLayer() < level &&				 
			    minusy.getAbsoluteCreeperLayer() >= level) //&&
			{
				setCorruptionEdge(true, EdgeType.BOTTOM_RIGHT);		
				if (minusx.getCreeperLevel() == 0 && plusy.getCreeperLevel() == 0) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrBR"));	
				} else if ((minusx.getCreeperLevel() == 0 && minusx.getHeight() > level) && (plusy.getCreeperLevel() == 0 && plusy.getHeight() > level)) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrCBR"));	
				} else if (minusx.getAbsoluteCreeperLayer() == plusy.getAbsoluteCreeperLayer()) {
					return new AtlasRegionContainer(minusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBR"), SpriteLoader.tileAtlas.findRegion("corr32"));									
				} else if (minusx.getAbsoluteCreeperLayer() > plusy.getAbsoluteCreeperLayer()) {
					return new AtlasRegionContainer(minusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBR"), SpriteLoader.tileAtlas.findRegion("corrST"));	
				} else {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBR"), SpriteLoader.tileAtlas.findRegion("corrSL"));	
				}		
			} else
			if (minusy.getAbsoluteCreeperLayer() < level && 
				plusx.getAbsoluteCreeperLayer() < level &&
			    plusy.getAbsoluteCreeperLayer() >= level &&
			    minusx.getAbsoluteCreeperLayer() >= level) //&&
			{
				setCorruptionEdge(true, EdgeType.TOP_LEFT);				
				if (minusy.getCreeperLevel() == 0 && plusx.getCreeperLevel() == 0) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrTL"));					
				} else if (minusy.getAbsoluteCreeperLayer() == plusx.getAbsoluteCreeperLayer()) {
					return new AtlasRegionContainer(plusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTL"), SpriteLoader.tileAtlas.findRegion("corr32"));									
				} else if (minusy.getAbsoluteCreeperLayer() > plusx.getAbsoluteCreeperLayer()) {
					return new AtlasRegionContainer(minusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTL"), SpriteLoader.tileAtlas.findRegion("corrSR"));	
				} else {
					return new AtlasRegionContainer(plusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTL"), SpriteLoader.tileAtlas.findRegion("corrSB"));	
				}
			} else
			if (minusy.getAbsoluteCreeperLayer() < level && 
				minusx.getAbsoluteCreeperLayer() < level && 
			    plusx.getAbsoluteCreeperLayer() >= level &&
			    plusy.getAbsoluteCreeperLayer() >= level) //&& 
			{
				setCorruptionEdge(true, EdgeType.TOP_RIGHT);	
				if (minusx.getCreeperLevel() == 0 && minusy.getCreeperLevel() == 0) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrTR"));	
				} else if ((minusx.getCreeperLevel() == 0 && minusx.getHeight() > level) && (minusy.getCreeperLevel() == 0 && minusy.getHeight() > level)) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrCTR"));	
				} else if (minusx.getAbsoluteCreeperLayer() == minusy.getAbsoluteCreeperLayer()) {
					return new AtlasRegionContainer(minusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTR"), SpriteLoader.tileAtlas.findRegion("corr32"));									
				} else if (minusx.getAbsoluteCreeperLayer() > minusy.getAbsoluteCreeperLayer()) {
					return new AtlasRegionContainer(minusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTR"), SpriteLoader.tileAtlas.findRegion("corrSB"));	
				} else {
					return new AtlasRegionContainer(minusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTR"), SpriteLoader.tileAtlas.findRegion("corrSL"));	
				}
			} else
				
			//Straight line borders
			if (plusx.getAbsoluteCreeperLayer() < level && 
				minusx.getAbsoluteCreeperLayer() >= level && 
				plusy.getAbsoluteCreeperLayer() >= level &&
				minusy.getAbsoluteCreeperLayer() >= level)  
			{
				setCorruptionEdge(true, EdgeType.STRAIGHT_RIGHT);
				return new AtlasRegionContainer(plusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrSR"), SpriteLoader.tileAtlas.findRegion("corr32"));									
			} else
			if (plusx.getAbsoluteCreeperLayer() >= level && 
				minusx.getAbsoluteCreeperLayer() >= level && 
				plusy.getAbsoluteCreeperLayer() >= level &&
				minusy.getAbsoluteCreeperLayer() < level)  
			{	
				setCorruptionEdge(true, EdgeType.STRAIGHT_BOTTOM);
				return new AtlasRegionContainer(minusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrSB"), SpriteLoader.tileAtlas.findRegion("corr32"));		
			} else
			if (plusx.getAbsoluteCreeperLayer() >= level && 
				minusx.getAbsoluteCreeperLayer() < level &&
				plusy.getAbsoluteCreeperLayer()  >= level &&
				minusy.getAbsoluteCreeperLayer()  >= level)  
			{	
				setCorruptionEdge(true, EdgeType.STRAIGHT_LEFT);
				return new AtlasRegionContainer(minusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrSL"), SpriteLoader.tileAtlas.findRegion("corr32"));		
			} else
			if (plusx.getAbsoluteCreeperLayer()  >= level && 
				minusx.getAbsoluteCreeperLayer() >= level && 
				plusy.getAbsoluteCreeperLayer()  < level &&
				minusy.getAbsoluteCreeperLayer() >= level)  
			{
				setCorruptionEdge(true, EdgeType.STRAIGHT_TOP);
				return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrST"), SpriteLoader.tileAtlas.findRegion("corr32"));		
			} else
			
			//Double edges
			if (plusx.getAbsoluteCreeperLayer() < level && 
				minusx.getAbsoluteCreeperLayer() < level && 
				plusy.getAbsoluteCreeperLayer() >= level &&
				minusy.getAbsoluteCreeperLayer() >= level)  
			{
				setCorruptionEdge(true, EdgeType.DOUBLE_X);
				return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrXBS"));									
			} else
			if (plusx.getAbsoluteCreeperLayer() >= level && 
				minusx.getAbsoluteCreeperLayer() >= level && 
				plusy.getAbsoluteCreeperLayer() < level &&
				minusy.getAbsoluteCreeperLayer() < level)  
			{	
				setCorruptionEdge(true, EdgeType.DOUBLE_Y);
				return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrYBS"));		
			} else
				
			//Tile ends
			if (plusy.getAbsoluteCreeperLayer() < level && 
				plusx.getAbsoluteCreeperLayer() < level && 
				minusy.getAbsoluteCreeperLayer() < level &&
				minusx.getAbsoluteCreeperLayer() >= level)  
			{
				setCorruptionEdge(true, EdgeType.END_LEFT);
				int h1 = (int) plusy.getAbsoluteCreeperLayer();
				int h2 = (int) plusx.getAbsoluteCreeperLayer();
				int h3 = (int) minusy.getAbsoluteCreeperLayer();
				if (plusy.getCreeperLevel() == 0 || plusx.getCreeperLevel() == 0 || minusy.getCreeperLevel() == 0) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrLB"));	
				} else if ((h1 == h2) && (h2 == h3)) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrLB"), SpriteLoader.tileAtlas.findRegion("corr32"));									
				} else if (h1 == h3 && h3 != h2) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrLB"), SpriteLoader.tileAtlas.findRegion("corrSR"));
				} else if (h1 == h2 && h2 != h3) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrLB"), SpriteLoader.tileAtlas.findRegion("corrSB"));
				} else if (h2 == h3 && h3 != h1) {
					return new AtlasRegionContainer(minusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrLB"), SpriteLoader.tileAtlas.findRegion("corrST"));
				}
			} else
			if (plusy.getAbsoluteCreeperLayer() < level && 
				minusx.getAbsoluteCreeperLayer() < level && 
				minusy.getAbsoluteCreeperLayer() < level &&
				plusx.getAbsoluteCreeperLayer() >= level)  
			{	
				setCorruptionEdge(true, EdgeType.END_RIGHT);
				int h1 = (int) plusy.getAbsoluteCreeperLayer();
				int h2 = (int) minusx.getAbsoluteCreeperLayer();
				int h3 = (int) minusy.getAbsoluteCreeperLayer();
				if (plusy.getCreeperLevel() == 0 || minusx.getCreeperLevel() == 0 || minusy.getCreeperLevel() == 0) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrRB"));	
				} else if ((h1 == h2) && (h2 == h3)) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrRB"), SpriteLoader.tileAtlas.findRegion("corr32"));									
				} else if (h1 == h3 && h3 != h2) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrRB"), SpriteLoader.tileAtlas.findRegion("corrSL"));
				} else if (h1 == h2 && h2 != h3) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrRB"), SpriteLoader.tileAtlas.findRegion("corrSB"));
				} else if (h2 == h3 && h3 != h1) {
					return new AtlasRegionContainer(minusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrRB"), SpriteLoader.tileAtlas.findRegion("corrST"));
				}	
			} else
			if (plusy.getAbsoluteCreeperLayer() < level && 
				plusx.getAbsoluteCreeperLayer() < level && 
				minusx.getAbsoluteCreeperLayer() < level &&
				minusy.getAbsoluteCreeperLayer() >= level)  
			{	
				setCorruptionEdge(true, EdgeType.END_TOP);
				int h1 = (int) minusx.getAbsoluteCreeperLayer();
				int h2 = (int) plusy.getAbsoluteCreeperLayer();
				int h3 = (int) plusx.getAbsoluteCreeperLayer();
				if (plusy.getCreeperLevel() == 0 || plusx.getCreeperLevel() == 0 || minusx.getCreeperLevel() == 0) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrTB"));	
				} else if ((h1 == h2) && (h2 == h3)) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTB"), SpriteLoader.tileAtlas.findRegion("corr32"));									
				} else if (h1 == h3 && h3 != h2) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTB"), SpriteLoader.tileAtlas.findRegion("corrST"));
				} else if (h1 == h2 && h2 != h3) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTB"), SpriteLoader.tileAtlas.findRegion("corrSR"));
				} else if (h2 == h3 && h3 != h1) {
					return new AtlasRegionContainer(minusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrTB"), SpriteLoader.tileAtlas.findRegion("corrSL"));
				}
			} else
			if (minusy.getAbsoluteCreeperLayer() < level && 
				plusx.getAbsoluteCreeperLayer() < level && 
				minusx.getAbsoluteCreeperLayer() < level &&
				plusy.getAbsoluteCreeperLayer() >= level)  
			{
				setCorruptionEdge(true, EdgeType.END_BOTTOM);
				int h1 = (int) minusx.getAbsoluteCreeperLayer();
				int h2 = (int) minusy.getAbsoluteCreeperLayer();
				int h3 = (int) plusx.getAbsoluteCreeperLayer();
				if (minusy.getCreeperLevel() == 0 || plusx.getCreeperLevel() == 0 || minusx.getCreeperLevel() == 0) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrBB"));	
				} else if ((h1 == h2) && (h2 == h3)) {
					return new AtlasRegionContainer(plusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBB"), SpriteLoader.tileAtlas.findRegion("corr32"));									
				} else if (h1 == h3 && h3 != h2) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBB"), SpriteLoader.tileAtlas.findRegion("corrSB"));
				} else if (h1 == h2 && h2 != h3) {
					return new AtlasRegionContainer(plusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBB"), SpriteLoader.tileAtlas.findRegion("corrSR"));
				} else if (h2 == h3 && h3 != h1) {
					return new AtlasRegionContainer(minusy.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrBB"), SpriteLoader.tileAtlas.findRegion("corrSL"));
				}	
			} else 
				
			//Single creeper tile
			if (minusy.getAbsoluteCreeperLayer() < level && 
				plusx.getAbsoluteCreeperLayer() < level && 
				minusx.getAbsoluteCreeperLayer() < level &&
				plusy.getAbsoluteCreeperLayer() < level)  
			{
				setCorruptionEdge(true, EdgeType.SINGLE);
				int h1 = (int) plusx.getAbsoluteCreeperLayer();
				int h2 = (int) plusy.getAbsoluteCreeperLayer();
				int h3 = (int) minusx.getAbsoluteCreeperLayer();
				int h4 = (int) minusy.getAbsoluteCreeperLayer();
				if (plusy.getCreeperLevel() == 0 && plusx.getCreeperLevel() == 0 && minusy.getCreeperLevel() == 0 && minusx.getCreeperLevel() == 0) {
					return new AtlasRegionContainer(SpriteLoader.tileAtlas.findRegion("corrSingle"));	
				} else 
				if ((h1 == h2) && (h2 == h3) && (h3 == h4)) {
					return new AtlasRegionContainer(plusx.calculateShade(), SpriteLoader.tileAtlas.findRegion("corrSingle"), SpriteLoader.tileAtlas.findRegion("corr32"));	
				}
			}
		}
		setCorruptionEdge(false, EdgeType.NONE);
		return new AtlasRegionContainer(1f, SpriteLoader.tileAtlas.findRegion("corr32"));					
	}
	
	/**Method that populates all the neighbour chunks. Called by {@link Chunks#create()} at initialisation.*/
	public void updateNeighbour() {
		ax = Math.round(x/Base.CHUNK_SIZE);
		ay = Math.round(y/Base.CHUNK_SIZE);
		
		minusx = GameScreen.chunks.getChunkAtTileSpace(ax - 1, ay);
		plusx = GameScreen.chunks.getChunkAtTileSpace(ax + 1, ay);
		minusy = GameScreen.chunks.getChunkAtTileSpace(ax, ay - 1);
		plusy = GameScreen.chunks.getChunkAtTileSpace(ax, ay + 1);
		
		cornerTopLeft = GameScreen.chunks.getChunkAtTileSpace(ax - 1, ay + 1);
		cornerTopRight = GameScreen.chunks.getChunkAtTileSpace(ax + 1, ay + 1);
		cornerBottomLeft = GameScreen.chunks.getChunkAtTileSpace(ax - 1, ay - 1);
		cornerBottomRight = GameScreen.chunks.getChunkAtTileSpace(ax + 1, ay - 1);
				
		//Putting these chunks into array for accessibility
		neighbours = new Chunk[8];	
		int n = 0;
		neighbours[n++] = plusy;
		//neighbours[n++] = cornerTopRight;
		neighbours[n++] = plusx;
		//neighbours[n++] = cornerBottomRight;
		neighbours[n++] = minusy;
		//neighbours[n++] = cornerBottomLeft;
		neighbours[n++] = minusx;
		//neighbours[n++] = cornerTopLeft;
		
		//null checks TOOD: fix map edge
		/*
		for(int i = 0; i < 8; i++) {
			if (neighbours[i] == null) {
				neighbours[i] = new Chunk();
			}
		}
		*/
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
		if (newCreeper > Base.MAX_CREEP - c_height) {
			newCreeper = Base.MAX_CREEP - c_height;
		}
		if (newCreeper < creeperMinimum) {
			newCreeper = 0;
		}			
		creeper = newCreeper;
		
		if (newCreeper != 0) {
			absCreeper = newCreeper + c_height;
		} else {
			absCreeper = 0;
		}
	}
	
	/**@return Current {@link #creeper} level.*/
	public float getCreeperLevel() {
		return creeper;
	}
	
	/**Returns the combined value of {@linkplain #creeper} and {@linkplain #height}.
	 * @return Current {@link #absCreeper} level.*/
	public float getAbsoluteCreeperLevel() {
		return absCreeper;
	}
	
	/**Returns the {@link #getAbsoluteCreeperLevel()} value but its rounded to the closest lower integer. This represents the creeper "layer".
	 * @return The {@link #getAbsoluteCreeperLevel()} value cast to an int.*/
	public int getAbsoluteCreeperLayer() {
		if (creeper == 0) {
			return -1;
		}
		return (int)this.getAbsoluteCreeperLevel();
	}
	
	/**Sets the terrain {@linkplain #height} of this chunk.
	 * @param level The new terrain height level.
	 * */
	public void setHeight(int level) {
		if (level > Base.MAX_HEIGHT) {
			level = Base.MAX_HEIGHT;
		}
		if (level < 0) {
			level = 0;
		}
		
		height = level;
		c_height = height;
		
		if(this.isDiagonalTerrainEdge()) {
			if (height > 0) {
				c_height = height - edgeLowerHeight;
			}
		}
	}
	
	/**Flag/Unflag this {@link Chunk} as a terrain edge.
	 * @param edge Edge boolean.
	 * @param lowerLevel Height difference between the topmost and lowest layer heights in this edge.
	 * @param edgetype Type of this edge.
	 * @see {@link Chunk.EdgeType}
	 * */
	public void setTerrainEdge(boolean edge, int lowerLevel,  EdgeType edgetype) {
		this.terrainEdge = edge;
		this.terrainEdgeType = edgetype;
		this.edgeLowerHeight = lowerLevel;
		if(!edge) {
			terrainEdgeType = EdgeType.NONE;
			this.edgeLowerHeight = 0;
		} else {
			if (this.diagonalEdges.contains(edgetype)) {
				if (c_height == height && height > 0) {
					c_height = height - edgeLowerHeight;
				}
			} else {
				c_height = height;
			}
		}
	}
	
	/**Calculates the shade color factor that is used to tint the final texture*/
	public float calculateShade() {
		return (int)creeper/(float)Base.MAX_CREEP;
		//return (1f - Interpolation.exp5Out.apply(0.45f, 1f, ((int)creeper/(float)Base.MAX_CREEP)));
	}
	
	/**@return Returns the {@link EdgeType} of this chunk, makes sure that this chunk is a terrain edge.*/
	public EdgeType getTerrainEdgeType() {
		if (terrainEdge == true) {
			return terrainEdgeType;
		}
		return EdgeType.NONE;
	}
	
	/**@return Returns true if this tile is a terrain edge.*/
	public boolean isTerrainEdge() {
		return terrainEdge;
	}
	
	/**@return Returns true if this tile is a diagonal terrain edge.
	 * @see {@link #diagonalEdges}
	 * */
	public boolean isDiagonalTerrainEdge() {
		return diagonalEdges.contains(terrainEdgeType);
	}
	
	/**Flag/Unflag this {@link Chunk} as a corr edge.
	 * @param edge Edge boolean.
	 * @param edgetype Type of this edge.
	 * @see {@link Chunk.EdgeType}
	 * */
	public void setCorruptionEdge(boolean edge, EdgeType edgetype) {
		this.corrEdge = edge;
		this.corrEdgeType = edgetype;
		if(!edge) {
			corrEdgeType = EdgeType.NONE;
		}
	}
	
	/**@return Returns the {@link EdgeType} of this chunk, makes sure that this chunk is a corr edge.*/
	public EdgeType getCorruptionEdgeType() {
		if (corrEdge == true) {
			return corrEdgeType;
		}
		return EdgeType.NONE;
	}
	
	/**@return Returns true if this tile is a corr edge.*/
	public boolean isCorruptionEdge() {
		return corrEdge;
	}
	
	public void setBorderChunk(boolean border) {
		borderChunk = border;
	}
	
	public boolean isBorderChunk() {
		return borderChunk;
	}
	
	public void setOreOutlined(boolean b) {
		this.isOreOutlined = b;
	}
	
	public boolean isOreOutlined() {
		return this.isOreOutlined;
	}
	
	public void setSection(Section s) {
		section = s;
	}
	
	public Section getSection() {
		return section;
	}
	
	/**Sets the building that stands on top of this chunk, (set null if there is none)*/
	public void setBuilding(AbstractBuilding b) {
		building = b;
	}
	
	/**Gets the building that is on top of this chunk, (null if there is none).*/
	public AbstractBuilding getBuilding() {
		return building;
	}
	
	/**@return Current terrain {@link #height}.*/
	public float getHeight() {
		return height;
	}
	
	/**@return Current terrain {@link #c_height}.*/
	public float getCHeight() {
		return c_height;
	}
	
	/**@return World space x coordinate of this {@linkplain Chunk}.*/
	public float getX() {
		return x;	
	}
	
	/**@return World space y coordinate of this {@linkplain Chunk}.*/
	public float getY() {
		return y;
	}
	
	/**@return Tile space x coordinate of this {@linkplain Chunk}.*/
	public int getAX() {
		return ax;	
	}
	
	/**@return Tile space y coordinate of this {@linkplain Chunk}.*/
	public int getAY() {
		return ay;
	}
	
	/**@return World space width/height of this {@linkplain Chunk}.*/
	public float getSize() {
		return size;
	}
}
