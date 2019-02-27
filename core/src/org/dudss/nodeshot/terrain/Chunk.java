package org.dudss.nodeshot.terrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks.OreType;
import org.dudss.nodeshot.terrain.datasubsets.AtlasRegionContainer;
import org.dudss.nodeshot.terrain.datasubsets.TerrainEdge;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

/**A square tile representation.
 * @see Chunks
 * */
public class Chunk {
	float x, y;
	
	/**World space width and height of this {@linkplain Chunk}*/
	float size = Base.CHUNK_SIZE;
	
	/**Fog of war visibility value
	 * 0.54f -> deactivated
	 * 0.3f -> activated but not in vision
	 * 0f -> active vision
	 */
	public float visibility = Base.DEFAULT_VISIBILITY;
	
	/**Number of {@link AbstractBuilding}s that are providing vision to this chunk.*/
	public int visionProviderNumber = 0;
	
	/**Transparency of active state.*/
	public static float active = 0f;
	/**Transparency of active state.*/
	public static float semiactive = 0.3f;
	/**Transparency of active state.*/
	public static float deactivated = 0.54f;
	
	/**Whether this chunk is on an edge of a {@link Section}.
	 * This is used for flagging {@linkplain Section}s as active (performance optimisation).
	 */
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
	
	/**Percentage of difference between two creeper levels to be added to the lower one.*/
	float flowRate = 0.1f; //0.25 MAX
	
	/**Testing purposes - unused*/
	float spreadThreshold = 0.2f; //0.5
	/**Testing purposes - unused*/
	float[] spreadWeights = new float[] {1f, 0.5f, 1f, 0.5f, 1f, 0.5f, 1f, 0.5f};
	/**Testing purposes - unused*/
	float[] hdiff = new float[8];
	
	/**A buffer that holds the creeper change between simulation ticks*/
	public float creeperChange = 0;
	
	long lastUpdate = SimulationThread.simTick;
	long updateRate = 5;		
	
	/**Damage caused by explosions
	 * NOT IMPLEMENTED YET*/
	float damage = 0f;
	
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
		BR_CORNER_TOP, BR_CORNER_BOTTOM,
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
	
	/*public boolean update() {	
		if (this.getCreeperLevel() > 0) {
			if (x >= Base.CHUNK_SIZE && y >= Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
				for (int i = 0; i < neighbours.length; i+=2) {
					average(i);
				}
				return true;
			}
		}
		return false;
	}
	
	private void average(int index) {
		float nLevel = neighbours[index].getCreeperLevel() + neighbours[index].getCHeight();
		float tLevel = this.getAbsoluteCreeperLevel();
		
		if (tLevel > nLevel) {
			float desiredLevel = (tLevel + nLevel)/2f;
			float delta = tLevel - desiredLevel;
			float availableChange = (this.creeper*0.8f)/4f;
			
			float change = 0;
			if (availableChange >= delta) {
				change = delta;
			} else {
				change = availableChange;
			}
			
			neighbours[index].creeperChange += change;
			this.creeperChange += -change;
		} 
	}*/
	
	/*public boolean updateNonPreserving() {	
		if (this.getCreeperLevel() > 0) {
			if (x >= Base.CHUNK_SIZE && y >= Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
				for (int i = 0; i < neighbours.length; i++) {
					averageNonPreserving(i);
				}
				return true;
			}
		}
		return false;
	}
	
	private void averageNonPreserving(int index) {
		float nLevel = neighbours[index].getCreeperLevel() + neighbours[index].getCHeight();
		float tLevel = this.getAbsoluteCreeperLevel();
		
		if (tLevel > nLevel) {
			float average = (tLevel + nLevel)/2f;
			float change = (average - nLevel)*0.25f;
			neighbours[index].creeperChange += change;
			this.creeperChange += -change;
		} 
		
		//if (neighbours[index].getAbsoluteCreeperLayer() < this.getAbsoluteCreeperLayer() && neighbours[index].c_height < this.getAbsoluteCreeperLevel()) {
		//	neighbours[index].creeperChange += 0.05f;
		//}
	}
	*/
	
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
	        	this.averageCreeper(thisTotal, plusy);
	        }        
	        //Bottom neighbour
	        if (this.ay > 0) {
	        	this.averageCreeper(thisTotal, minusy);
	        }
	        //Right neighbour
	        if (this.ax < Base.CHUNK_AMOUNT - 1) {
	        	this.averageCreeper(thisTotal, plusx);
	        }
	        //Left neighbour
	        if (this.ax > 0) {
	        	this.averageCreeper(thisTotal, minusx);
	        }
	        return true;
		}
		return false;
	}

	private void averageCreeper(float thisTotal, Chunk to) {
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
	
	/*public boolean updateTest() {		
		if (this.getCreeperLevel() != 0 && visibility != deactivated) {
			if (x >= Base.CHUNK_SIZE && y >= Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
				float totalDelta = 0;
				for(int i = 0; i < neighbours.length; i++) {
					  if (neighbours[i].c_height >= 0) {
			            	 if (this.getAbsoluteCreeperLevel() > neighbours[i].getAbsoluteCreeperLevel() && this.getAbsoluteCreeperLayer() != 0) {
			            		 float delta = this.getAbsoluteCreeperLevel() - neighbours[i].getAbsoluteCreeperLevel();
			                     if (delta > this.getCreeperLevel()) {
			                     	delta = this.getCreeperLevel();
			                     }
			                     hdiff[i] = delta * flowRate * spreadWeights[i];
			                     totalDelta += hdiff[i];
			                 }				            
					  }
				}
				float rangeFactor = 1f;
				if (totalDelta > this.creeper) {
					rangeFactor = this.creeper / totalDelta;
				}
				
				for(int i = 0; i < neighbours.length; i++) {
					float change = hdiff[i] * rangeFactor;
					this.creeperChange -= change;
					neighbours[i].creeperChange += change;
					if (neighbours[i].isBorderChunk()) {
						if (neighbours[i].getSection() != this.getSection()) {
							neighbours[i].getSection().setActive(true);
						}
					}
				}
				return true;
			}
			
			//float thisTotal = this.c_height + this.getCreeperLevel();
			
	        //Top neighbour
	        //if (this.ay < Base.CHUNK_AMOUNT - 1) {
	        //	this.transferCreeper(thisTotal, plusy);
	        //}        
	        //Bottom neighbour
	        //if (this.ay > 0) {
	        //	this.transferCreeper(thisTotal, minusy);
	        //}
	        //Right neighbour
	        //if (this.ax < Base.CHUNK_AMOUNT - 1) {
	        //	this.transferCreeper(thisTotal, plusx);
	        //}
	        //Left neighbour
	        //if (this.ax > 0) {
	        //	this.transferCreeper(thisTotal, minusx);
	        //}
	        return false;
		}
		return false;
	}
	*/
	
	/**Called after all the other {@link Chunk}s had been updated using the {@link #update()} method in the current simulation tick*/
	public void applyUpdate() {
		setCreeperLevel(creeper + creeperChange);
		creeperChange = 0;
	}
		
	/**Returns an {@link AtlasRegionContainer} representing this {@linkplain Chunk}s terrain.
	 * The container contains edge resolved combined textures.
	 * @see TerrainEdgeResolver
	 */
	public AtlasRegionContainer getTerrainTexture() {			
		AtlasRegionContainer misc = new AtlasRegionContainer();
		
		//Draw mine outlines
		if (this.isOreOutlined) {
			AtlasRegion outline = resolveOutlineEdges();
			if (outline != null) {
				misc.addTexture(outline);
			}
		}		
		
		//Ore detection
		if (Base.drawOres) {
			if (this.getOreLevel() > 0) {
				AtlasRegion ore = getOreTexture();
				if (ore != null) {
					misc.addTexture(ore);
				}
			}
		}
		
		//Draw build outlines
		if (GameScreen.buildMode) {
			if (this.isDiagonalTerrainEdge() || this.getBuilding() != null) {
				misc.addTexture(SpriteLoader.terrainAtlas.findRegion("buildOverlayRed"));
			} else {
				misc.addTexture(SpriteLoader.terrainAtlas.findRegion("buildOverlayGreen"));
			}
		}
		
		AtlasRegionContainer terrain = new AtlasRegionContainer();
		
		if (!Base.disableEdges) {
			switch(height) {
				case 0: this.setTerrainEdge(false, 0, EdgeType.NONE); return null; //TODO: ore at level 0?
				case 1: terrain.addContainer(resolveTerrainEdges(1)); break;
				case 2: terrain.addContainer(resolveTerrainEdges(2)); break;
				case 3:	terrain.addContainer(resolveTerrainEdges(3)); break;
				case 4: terrain.addContainer(resolveTerrainEdges(4)); break;
				case 5: terrain.addContainer(resolveTerrainEdges(5)); break;
				case 6: terrain.addContainer(resolveTerrainEdges(6)); break;
				case 7: terrain.addContainer(resolveTerrainEdges(7)); break;
				case 8: terrain.addContainer(resolveTerrainEdges(8)); break;
				case 9: terrain.addContainer(resolveTerrainEdges(9)); break;
				case 10: terrain.addContainer(resolveTerrainEdges(10)); break;	
			}
		} else {
			switch(height) {
				case 0: this.setTerrainEdge(false, 0, EdgeType.NONE); return null; //TODO: ore at level 0?
				case 1: terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[1]))); break;
				case 2: terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[2]))); break;
				case 3:	terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[3]))); break;
				case 4: terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[4]))); break;
				case 5: terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[5]))); break;
				case 6: terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[6]))); break;
				case 7: terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[7]))); break;
				case 8: terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[8]))); break;
				case 9: terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[9]))); break;
				case 10: terrain.addContainer(new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[10]))); break;
			}
		}
		return misc.addContainer(terrain);
	}
	
	/**Returns the appropriate {@link AtlasRegionContainer} for the specified terrain height.
	 * 
	 * @see TerrainEdgeResolver
	 * @param level Target height level.
	 * */
	private AtlasRegionContainer resolveTerrainEdges(int level) {
		TerrainEdge topLayer = null;
		TerrainEdge bottomLayer = null;
		
		//Height of the highest neighbour that is lower than the current height
		int lowerLevelHeight = 0;
		//Height of the lowest neighbour that is lower than the current height
		int lowestLevelHeight = Chunks.terrainLayerNames.length - 1;
		
		//Array check
		if (x >= Base.CHUNK_SIZE && y >= Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
			//The neighbours height difference, 0 -> the same or higher level, 1 -> lower level
			char[] topDiffs = new char[8];
			for (int i = 0; i < 8; i++) {
				topDiffs[i] = (char) (neighbours[i].getHeight() < height ? '1' : '0');
			}
			
			//Generate chunk outer edge mask, additional height differences mask
			char[] outerDiffs = new char[4]; 
			for (int i = 0; i < outerDiffs.length*2; i+=2) {
				if (this.neighbours[i].neighbours[i] != null) {
					outerDiffs [i/2] = (char) (this.neighbours[i].neighbours[i].height < height ? '1' : '0');
				} else {
					outerDiffs [i/2] = '0';
				}
			}
			
			boolean diagonal = false;
			
			//Checks for a diagonal edge
			topLayer = TerrainEdgeResolver.resolveDiagonalEdges(topDiffs);
			//Diagonal edge requires a bottom layer texture underneath it
			if (topLayer != null) {
				diagonal = true;
				//Checks for lower and lowest levels
				for (int i = 0; i < topLayer.mask.length; i++) {
					if (topLayer.mask[i] == '1') {
						if (neighbours[i].getHeight() > lowerLevelHeight) {
							lowerLevelHeight = (int) neighbours[i].getHeight();
						}
						if (neighbours[i].getHeight() < lowestLevelHeight) {
							lowestLevelHeight = (int) neighbours[i].getHeight();
						}
					}
				}
				
				//Calculates bottom layer height differences
				char[] bottomDiffs = new char[8];
				for (int i = 0; i < 8; i++) {
					bottomDiffs[i] = (char) (neighbours[i].getHeight() < lowerLevelHeight ? '1' : '0');
				}
				//Generate chunk outer edge mask
				char[] bottomOuterDiffs = new char[4]; 
				for (int i = 0; i < bottomOuterDiffs.length*2; i+=2) {
					if (this.neighbours[i].neighbours[i] != null) {
						bottomOuterDiffs [i/2] = (char) (this.neighbours[i].neighbours[i].getHeight() < lowerLevelHeight ? '1' : '0');
					} else {
						bottomOuterDiffs [i/2] = '0';
					}
				}
				//Resolves the bottom layer edge
				bottomLayer = TerrainEdgeResolver.resolveSolidEdges(bottomDiffs, bottomOuterDiffs, lowerLevelHeight);
			} else {
				//Top layer solid, no bottom layer necessary
				topLayer = TerrainEdgeResolver.resolveSolidEdges(topDiffs, outerDiffs, this.height);
			}
			//Final terrain container
			AtlasRegionContainer terrainCont = new AtlasRegionContainer();			
			terrainCont.addTexture(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[level] + topLayer.name));
			if (bottomLayer == null) {
				if (diagonal) {
					terrainCont.addTexture(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[lowestLevelHeight]));
				}
				setTerrainEdge(topLayer.isTerrainEdge, height, topLayer.type);
				return terrainCont;
			}
			terrainCont.addTexture(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[lowerLevelHeight] + bottomLayer.name));
			setTerrainEdge(topLayer.isTerrainEdge, height - lowerLevelHeight, topLayer.type);
			return terrainCont;
		}
		setTerrainEdge(false, 0, EdgeType.NONE);
		return new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion(Chunks.terrainLayerNames[level]));
	}

	/**Returns the appropriate ore {@link AtlasRegion} for this {@linkplain Chunk}.*/
	private AtlasRegion getOreTexture() {
		if (coalOre != 0) {	
			if (coalOre > 0.25f) {
				return SpriteLoader.terrainAtlas.findRegion("renderedCoalLevel1");
			} else {
				return SpriteLoader.terrainAtlas.findRegion("renderedCoalLevel0");
			}
		}	
	
		if (ironOre != 0) {
			if (ironOre > 0.25f) {
				return SpriteLoader.terrainAtlas.findRegion("renderedIronLevel1");
			} else {
				return SpriteLoader.terrainAtlas.findRegion("renderedIronLevel0");
			}		
		}
		return null;
	}
	
	/**Returns the appropriate texture for mine ore outlining. TODO: probably scrap or something*/
	public AtlasRegion resolveOutlineEdges() {
		if (x >= Base.CHUNK_SIZE && y >= Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
			if (plusy.isOreOutlined() &&
				plusx.isOreOutlined() &&
				minusx.isOreOutlined() && 
				minusy.isOreOutlined()) 
			{					
				return SpriteLoader.terrainAtlas.findRegion("outlineEmpty");
			} else
			
			//Diagonal edges
			if (plusy.isOreOutlined() &&
				plusx.isOreOutlined() &&
				minusx.isOreOutlined() == false && 
				minusy.isOreOutlined() == false) 
			{					
				return SpriteLoader.terrainAtlas.findRegion("outlineBL");
			} else
			if (plusy.isOreOutlined() && 
				plusx.isOreOutlined() == false &&
				minusx.isOreOutlined() &&				 
			    minusy.isOreOutlined() == false)  
			{
				return SpriteLoader.terrainAtlas.findRegion("outlineBR");
			} else
			if (minusy.isOreOutlined() && 
				plusx.isOreOutlined() &&
			    plusy.isOreOutlined() == false &&
			    minusx.isOreOutlined() == false)  
			{
				return SpriteLoader.terrainAtlas.findRegion("outlineTL");
			} else
			if (minusy.isOreOutlined() && 
				minusx.isOreOutlined()&& 
			    plusx.isOreOutlined() == false &&
			    plusy.isOreOutlined() == false)  
			{
				return SpriteLoader.terrainAtlas.findRegion("outlineTR");								
			} else
				
			//Straight line borders
			if (plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() && 
				plusy.isOreOutlined() &&
				minusy.isOreOutlined())  
			{
				return SpriteLoader.terrainAtlas.findRegion("outlineSR");
			} else
			if (plusx.isOreOutlined() && 
				minusx.isOreOutlined() && 
				plusy.isOreOutlined() &&
				minusy.isOreOutlined() == false)  
			{
				return SpriteLoader.terrainAtlas.findRegion("outlineSB");
			} else
			if (plusx.isOreOutlined() && 
				minusx.isOreOutlined() == false && 
				plusy.isOreOutlined() &&
				minusy.isOreOutlined())  
			{
				return SpriteLoader.terrainAtlas.findRegion("outlineSL");
			} else
			if (plusx.isOreOutlined() && 
				minusx.isOreOutlined() && 
				plusy.isOreOutlined() == false &&
				minusy.isOreOutlined())  
			{
				return SpriteLoader.terrainAtlas.findRegion("outlineST");
			} else
				
			//Tile ends
			if (plusy.isOreOutlined() == false && 
				plusx.isOreOutlined() == false && 
				minusy.isOreOutlined() == false &&
				minusx.isOreOutlined())  
			{
				return SpriteLoader.terrainAtlas.findRegion("outlineRB");
			} else 
			if (plusy.isOreOutlined() == false && 
				minusx.isOreOutlined() == false && 
				minusy.isOreOutlined() == false &&
				plusx.isOreOutlined())  
			{	
				return SpriteLoader.terrainAtlas.findRegion("outlineLB");
			} else
			if (plusy.isOreOutlined() == false && 
				plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() == false &&
				minusy.isOreOutlined())  
			{	
				return SpriteLoader.terrainAtlas.findRegion("outlineTB");
			} else
			if (minusy.isOreOutlined() == false && 
				plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() == false &&
				plusy.isOreOutlined())  
			{			
				return SpriteLoader.terrainAtlas.findRegion("outlineBB");
			} else 
				
			//Single creeper tile
			if (minusy.isOreOutlined() == false && 
				plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() == false &&
				plusy.isOreOutlined() == false)  
			{ 
				return SpriteLoader.terrainAtlas.findRegion("outlineSingle");
			} else
			
			if (minusy.isOreOutlined() == false && 
				plusx.isOreOutlined() && 
				minusx.isOreOutlined() &&
				plusy.isOreOutlined() == false)  
			{ 
				return SpriteLoader.terrainAtlas.findRegion("outlineYBS");
			} else
			if (minusy.isOreOutlined() && 
				plusx.isOreOutlined() == false && 
				minusx.isOreOutlined() == false &&
				plusy.isOreOutlined())  
			{ 
				return SpriteLoader.terrainAtlas.findRegion("outlineXBS");
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
	 * @see #resolveCorruptionEdges()*/
	public AtlasRegionContainer getCorruptionTexture() {
 		if (this.creeper != 0) {
 			if (!Base.disableEdges) {
 				return resolveCorruptionEdges();					
 			} else {
 				AtlasRegionContainer cont = new AtlasRegionContainer();
 				cont.addTexture(SpriteLoader.terrainAtlas.findRegion("corr"));			
 				cont.addTexture(SpriteLoader.terrainAtlas.findRegion("corr")); 					
 				cont.setSecondaryShade(creeper);
 				return cont;
 			}
 		} 
		return null;
	}
	
	/**Returns the appropriate edge {@link AtlasRegionContainer}.
	 * {@link AtlasRegionContainer} allows us to return multiple textures in a certain order. It can also hold a special secondary shade value ({@link AtlasRegionContainer#getSecondaryShade()})
	 * As of right now the corruption renderer and shader supports 2 individual textures that can be overlaid over each other. (Thats why there is just one secondary shade value).
	 * 
	 * The edges are resolved using the same resolver and method as the terrain itself. But the resolve algorithm is slightly modified.
	 * @see TerrainEdgeResolver
	 * */
	private AtlasRegionContainer resolveCorruptionEdges() {
		TerrainEdge topLayer = null;
		TerrainEdge bottomLayer = null;
		
		//Height of the highest neighbour that is lower than the current height
		int lowerLevelHeight = 0;
		//Height of the lowest neighbour that is lower than the current height
		int lowestLevelHeight = Base.MAX_CREEP;
		
		//Array check
		if (x >= Base.CHUNK_SIZE && y >= Base.CHUNK_SIZE && x < Base.WORLD_SIZE-Base.CHUNK_SIZE && y < Base.WORLD_SIZE-Base.CHUNK_SIZE) { 
			//The neighbours height difference, 0 -> the same or higher level, 1 -> lower level
			char[] topDiffs = new char[8];
			for (int i = 0; i < 8; i++) {
				topDiffs[i] = (char) (neighbours[i].getAbsoluteCreeperLayer() < this.getAbsoluteCreeperLayer() ? '1' : '0');
			}
			
			//Generate chunk outer edge mask, additional height differences mask
			char[] outerDiffs = new char[4]; 
			for (int i = 0; i < outerDiffs.length*2; i+=2) {
				if (this.neighbours[i].neighbours[i] != null) {
					outerDiffs [i/2] = (char) (this.neighbours[i].neighbours[i].getAbsoluteCreeperLayer() < this.getAbsoluteCreeperLayer() ? '1' : '0');
				} else {
					outerDiffs [i/2] = '0';
				}
			}
			
			boolean diagonal = false;
			int lowerLeveIndex = -1;
			int lowestLevelIndex = -1;
			
			//Checks for a diagonal edge
			topLayer = TerrainEdgeResolver.resolveDiagonalEdges(topDiffs);
			//Diagonal edge requires a bottom layer texture underneath it
			if (topLayer != null) {
				diagonal = true;
				//Checks for lower and lowest levels
				for (int i = 0; i < topLayer.mask.length; i++) {
					if (topLayer.mask[i] == '1') {
						if (neighbours[i].getAbsoluteCreeperLayer() > lowerLevelHeight) {
							lowerLevelHeight = (int) neighbours[i].getAbsoluteCreeperLayer();
							lowerLeveIndex = i;
						}
						if (neighbours[i].getAbsoluteCreeperLayer() < lowestLevelHeight) {
							lowestLevelHeight = (int) neighbours[i].getAbsoluteCreeperLayer();
							lowestLevelIndex = i;
						}
					}
				}
				
				//Calculates bottom layer height differences
				char[] bottomDiffs = new char[8];
				for (int i = 0; i < 8; i++) {
					bottomDiffs[i] = (char) (neighbours[i].getAbsoluteCreeperLayer() < lowerLevelHeight ? '1' : '0');
				}
				//Generate chunk outer edge mask
				char[] bottomOuterDiffs = new char[4]; 
				for (int i = 0; i < bottomOuterDiffs.length*2; i+=2) {
					if (this.neighbours[i].neighbours[i] != null) {
						bottomOuterDiffs [i/2] = (char) (this.neighbours[i].neighbours[i].getAbsoluteCreeperLayer() < lowerLevelHeight ? '1' : '0');
					} else {
						bottomOuterDiffs [i/2] = '0';
					}
				}
				//Resolves the bottom layer edge
				if (c_height != height) {
					if (neighbours[0].creeper > 0 && neighbours[2].creeper > 0 && neighbours[4].creeper > 0 && neighbours[6].creeper > 0) {
						bottomLayer = TerrainEdgeResolver.resolveSolidEdges(bottomDiffs, bottomOuterDiffs, lowerLevelHeight);
					}
				} else {
					if (creeper > 0) {
						bottomLayer = TerrainEdgeResolver.resolveSolidEdges(bottomDiffs, bottomOuterDiffs, lowerLevelHeight);
					}
				}
			} else {
				//Top layer solid, no bottom layer necessary
				topLayer = TerrainEdgeResolver.resolveSolidEdges(topDiffs, outerDiffs, this.getAbsoluteCreeperLayer());
			}
			//Final terrain container
			AtlasRegionContainer creeperCont = new AtlasRegionContainer();			
			creeperCont.addTexture(SpriteLoader.terrainAtlas.findRegion("corr" + topLayer.name));
			if (bottomLayer != null) {
				creeperCont.addTexture(SpriteLoader.terrainAtlas.findRegion("corr" + bottomLayer.name));
				if (lowerLeveIndex == -1) {
					creeperCont.setSecondaryShade(creeper);
				} else {
					creeperCont.setSecondaryShade(neighbours[lowerLeveIndex].creeper);
				}
			} else
			/*if (this.isDiagonalTerrainEdge() && this.getTerrainEdgeType() == topLayer.type) {			
				terrainCont.addTexture(SpriteLoader.terrainAtlas.findRegion("corr"));
				terrainCont.setSecondaryShade(lowestLevelHeight);
				setCorruptionEdge(topLayer.isTerrainEdge, topLayer.type);
				return terrainCont;
			}*/
			
			setCorruptionEdge(topLayer.isTerrainEdge, topLayer.type);
			return creeperCont;
		}
		setCorruptionEdge(false, EdgeType.NONE);
		return new AtlasRegionContainer(SpriteLoader.terrainAtlas.findRegion("corr"));
		
	}
	
	/**Generates corruption heigh difference masks that can be passed to {@link TerrainEdgeResolver}.*/
	private TerrainEdgeInfo generateCorruptionDiffs(int compareTo) {
		int highestNeighbourLayer = 0;
		//Upper layer
		char[] topDiffs = new char[8];
		for (int i = 0; i < 8; i++) {
			topDiffs[i] = (char) (neighbours[i].getAbsoluteCreeperLayer() < compareTo ? '1' : '0');
			if (topDiffs[i] == '1') {
				if (neighbours[i].getAbsoluteCreeperLayer() > highestNeighbourLayer) {
					highestNeighbourLayer = (int) neighbours[i].getAbsoluteCreeperLayer();
				}
			}
		}
		
		//Generate chunk outer edge mask
		char[] outerDiffs = new char[4]; 
		for (int i = 0; i < outerDiffs.length*2; i+=2) {
			if (this.neighbours[i].neighbours[i] != null) {
				outerDiffs [i/2] = (char) (this.neighbours[i].neighbours[i].getAbsoluteCreeperLayer() < compareTo ? '1' : '0');
			} else {
				outerDiffs [i/2] = '0';
			}
		}

		return new TerrainEdgeInfo(topDiffs, outerDiffs, highestNeighbourLayer);
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
		neighbours[n++] = cornerTopRight;
		neighbours[n++] = plusx;
		neighbours[n++] = cornerBottomRight;
		neighbours[n++] = minusy;
		neighbours[n++] = cornerBottomLeft;
		neighbours[n++] = minusx;
		neighbours[n++] = cornerTopLeft;
		
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
	 * If there is no creeper -1 will be returned.
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
	 * @see Chunk.EdgeType
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
	
	/**Calculates the shade color factor that is used to tint the final texture for the specific creeper value.*/
	public static float calculateShadeForValue(float creeperVal) {
		return (int)creeperVal/(float)Base.MAX_CREEP;
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
	 * @see #diagonalEdges
	 * */
	public boolean isDiagonalTerrainEdge() {
		return diagonalEdges.contains(terrainEdgeType);
	}
	
	/**Flag/Unflag this {@link Chunk} as a corr edge.
	 * @param edge Edge boolean.
	 * @param edgetype Type of this edge.
	 * @see Chunk.EdgeType
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
	
	/**Returns a list of all chunk neighbours.*/
	public List<Chunk> getNeighbours() {
		return new ArrayList<Chunk>(Arrays.asList(neighbours));
	}
	
	/**Set the section this chunk is located in.*/
	public void setSection(Section s) {
		section = s;
	}
	
	/**Returns the section this chunk is located in.*/
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
	
	/**@return Current terrain {@link #damage}.*/
	public float getDamage() {
		return damage;
	}
	
	/**Unused - testing purposes*/
	public void setDamage(float d) {
		damage = d;
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
	
	/**@return World space x coordinate of this {@linkplain Chunk} centre.*/
	public float getCX() {
		return x + Base.CHUNK_SIZE/2;	
	}
	
	/**@return World space y coordinate of this {@linkplain Chunk} centre.*/
	public float getCY() {
		return y + Base.CHUNK_SIZE/2;
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
