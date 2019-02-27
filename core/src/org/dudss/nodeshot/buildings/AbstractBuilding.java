package org.dudss.nodeshot.buildings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.effects.Explosion;
import org.dudss.nodeshot.misc.BuildingManager;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.Chunks;
import org.dudss.nodeshot.terrain.Section;
import org.dudss.nodeshot.utils.Selector;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**The basic skeletal representation of a building. All buildings are subclasses of this abstract class.*/
public abstract class AbstractBuilding implements Entity {
	/**Building types used for render layering.*/
	enum BuildingType {
		BUILDING, MISC, GENERATOR
	}
	
	BuildingType buildingType = BuildingType.BUILDING;
	
	float x,y;
	float cx,cy;
	
	float width;
	float height;
	
	boolean outlined = false;
	boolean isBuilt = false;
	boolean isUsingEnergy = true;

	int fogOfWarRadius = Base.SECTION_SIZE;
	
	Chunk[] buildingChunks;
	
	int id;
	
	/**Initialises the building, the building still needs to be placed with {@link #setLocation(float, float, boolean)} and {@link #build()}.*/
	public AbstractBuilding(float cx, float cy, float width, float height) {		
		this.id = java.lang.System.identityHashCode(this);
		
		this.cx = cx;
		this.cy = cy;
		
		this.width = width;
		this.height = height;
		
		x = cx - (width/2);
		y = cy - (height/2);
	}
	
	/**Sets all the necessary position attributes and sets up building chunks array at the specified position.
	 * This method does not check for building obstacles!.
	 * @param cx Building centre X coordinate.
	 * @param cy Building centre Y coordinate.
	 * @param snap Whether to snap the coordinates to the nearest {@link Chunk}.
	 * @see #canBeBuiltAt(float, float, boolean)
	 */ 
	public void setLocation(float cx, float cy, boolean snap) {
		setCoordinates(cx, cy, snap);
		
		int index = 0;
		int buildintWidthInTileSpace = Math.round(width/Base.CHUNK_SIZE);
		int buildingHeightInTileSpace = Math.round(height/Base.CHUNK_SIZE);
		
		buildingChunks = new Chunk[buildintWidthInTileSpace*buildingHeightInTileSpace];
		for (int gx = 0; gx < buildintWidthInTileSpace; gx++) {
			for (int gy = 0; gy < buildingHeightInTileSpace; gy++) {
				Chunk c = GameScreen.chunks.getChunkAtTileSpace((int)(this.x/Base.CHUNK_SIZE) + gx, (int)(this.y/Base.CHUNK_SIZE) + gy);
				if (c != null) {
					buildingChunks[index++] = c;	
				}
			}
		}
		
		for (int i = 0; i < buildingChunks.length; i++) {
			buildingChunks[i].setBuilding(this);
		}
	}
	
	/**Method that checks whether a building can be built at this location.
	 * Should be called before {@link #setLocation(float, float, boolean)}.
	 * This method also takes terrain height into consideration.
	 */
	public boolean canBeBuiltAt(float cx, float cy, boolean snap) {
		Vector2 newCoords = getCoordinates(cx, cy, snap);
		
		int index = 0;
		int buildintWidthInTileSpace = Math.round(width/Base.CHUNK_SIZE);
		int buildingHeightInTileSpace = Math.round(height/Base.CHUNK_SIZE);
		
		boolean intersectsAnotherBuilding = false;
		
		List<Chunk> oldBuildingChunks = null;		
		if (buildingChunks != null) {
			oldBuildingChunks = new ArrayList<Chunk>(Arrays.asList(buildingChunks));
		}
		int buildingHeight = (int) GameScreen.chunks.getChunkAtTileSpace((int)(newCoords.x/Base.CHUNK_SIZE) + 0, (int)(newCoords.y/Base.CHUNK_SIZE) + 0).getHeight();
		Chunk[] buildingChunks = new Chunk[buildintWidthInTileSpace*buildingHeightInTileSpace];	
		for (int gx = 0; gx < buildintWidthInTileSpace; gx++) {
			for (int gy = 0; gy < buildingHeightInTileSpace; gy++) {
				Chunk c = GameScreen.chunks.getChunkAtTileSpace((int)(newCoords.x/Base.CHUNK_SIZE) + gx, (int)(newCoords.y/Base.CHUNK_SIZE) + gy);
				if (c != null) {
					buildingChunks[index++] = c;	
					
					if (oldBuildingChunks != null) {
						if (!oldBuildingChunks.contains(c) && c.getBuilding() != null) {
							intersectsAnotherBuilding = true;
						}
					} else {
						if (c.getBuilding() != null) {
							intersectsAnotherBuilding = true;
						}
					}
					if (c.isDiagonalTerrainEdge() || c.getHeight() != buildingHeight) {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		if (intersectsAnotherBuilding) {
			return false;
		}
		return true;
	}
	
	/**Recalculates the building coordinates that apply for the following world-space cursor coordinates
	 * @param cx Building centre X coordinate.
	 * @param cy Building centre Y coordinate.
	 * @param snap Whether to snap the coordinates to the nearest {@link Chunk}.
	 */
	protected void setCoordinates(float cx, float cy, boolean snap) {
		float newX;
		float newY;
		
		if (snap) {
			float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
			float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
			
			newX = nx - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
			newY = ny - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
			
			this.cx = nx + Base.CHUNK_SIZE/2;
			this.cy = ny + Base.CHUNK_SIZE/2;
		} else {
			this.cx = cx;
			this.cy = cy;
			
			newX = cx - (width/2);
			newY = cy - (height/2);
		}
		
		this.x = newX;
		this.y = newY;
	}
	
	/**Retrieves the coordinates that would apply for the following world-space cursor coordinates.
	 * @param cx Building centre X coordinate.
	 * @param cy Building centre Y coordinate.
	 * @param snap Whether to snap the coordinates to the nearest {@link Chunk}.
	 */
	public Vector2 getCoordinates(float cx, float cy, boolean snap) {
		float newX;
		float newY;
		
		if (snap) {
			float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
			float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
			
			newX = nx - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
			newY = ny - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
		} else {
			
			newX = cx - (width/2);
			newY = cy - (height/2);
		}
		
		return new Vector2(newX, newY);
	}
	
	public Vector2 getPrefabVector(float cx, float cy, boolean snap) {
		return getCoordinates(cx, cy, snap);
	}
	
	/**Building update method, updated by the {@link SimulationThread}.
	 * The default {@link AbstractBuilding} update method handles creeper damage, explosion and resource costs.
	 * So if a building extending this object calls super.update() within its update method, it will be harmed by the creeper.
	 */
	public void update() {
		for (int i = 0; i < buildingChunks.length; i++) {
			if (buildingChunks[i] != null) {
				if (buildingChunks[i].getCreeperLevel() > 0) {
					this.explode();
					break;
				}
			}
		}
	}	
	
	/**Draw method*/
	public abstract void draw(SpriteBatch batch);
	
	/**Prefab draw method (prefab is the building representation following the cursor when in build mode)*/
	public abstract void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap);	
	
	/**Called when the building is built. Registers it to its {@link BuildingManager}.*/
	public void build() {
		build(true);
	}
	
	/**Called when the building is built.
	 * @param register Whether to register the building to its {@link BuildingManager} immediately.
	 */
	public void build(boolean register) {
		GameScreen.resourceManager.removeBits(getBuildCost());
		GameScreen.resourceManager.removePower(getEnergyCost());
		
		if (buildingType != BuildingType.GENERATOR) updateFogOfWar(true);	
		if (register) register();
	}	
	
	/**Registers the building to a {@link BuildingManager}. Making it drawable and updateable.
	 * Some buildings need this to be called at the end of their {@link #build()} methods.
	 * <br><br>If a building needs to initialise some variables before starting its update cycle.
	 * You can call super.build(false) and then this.register at the end of the overriden build method.*/
	protected void register() {
		switch(buildingType) {
			case BUILDING: GameScreen.buildingManager.addBuilding(this); break;
			case MISC: GameScreen.buildingManager.addMisc(this); break;
			case GENERATOR: GameScreen.buildingManager.addGenerator(this); break;
		}
	}
	
	/**Builds the building but flattens the terrain under the building first.
	 * @param register Whether to register the building to its {@link BuildingManager} immediately.
	 */
	public void buildAndLevel(boolean register) {
		levelTerrainOfBuildingChunks();
		build(register);
	}
	
	/**Builds the building but flattens the terrain under the building first. The building is registered to its {@link BuildingManager}. */
	public void buildAndLevel() {
		levelTerrainOfBuildingChunks();
		build(true);
	}
	
	/**Called upon demolition. Adds the building to a {@link BuildingManager} and updates fog of war.
	 * @param returnBits Whether to return a portion of the buildings build cost.
	 */
	public void demolish(boolean returnBits) {
		switch(buildingType) {
			case BUILDING: GameScreen.buildingManager.removeRegularBuilding(this); break;
			case MISC: GameScreen.buildingManager.removeMisc(this); break;
			case GENERATOR: GameScreen.buildingManager.removeGenerator(this); break;
		}
		
		clearBuildingChunks();
		
		if (returnBits) GameScreen.resourceManager.addBits((int) (getBuildCost()*Base.DEMOLISH_RETURN_VALUE));
		if (buildingType != BuildingType.GENERATOR) updateFogOfWar(false);	
		if (GameScreen.selectedEntity == this) Selector.deselect();
	}
	/**Called when demolished by force*/
	public void explode() {			
		new Explosion(cx, cy);
		this.demolish(false);
	}
	
	/**Flags the building as outlined/selected*/
	public void outline(boolean outline) {
		outlined = outline;
	}
	
	/**Updates the fog of war in a {@link #fogOfWarRadius}.
	 * @param show When true, fog of war will be revealed, when false it will be deactivated.
	 * */
	protected void updateFogOfWar(boolean show) {
		if (show) {
			GameScreen.chunks.setVisibility(this.cx, this.cy, fogOfWarRadius, Chunks.Visibility.ACTIVE);
		} else {
			GameScreen.chunks.setVisibility(this.cx, this.cy, fogOfWarRadius, Chunks.Visibility.SEMIACTIVE);
		}
		
	}
	
	/**All the {@link Chunk}s this building is occupying*/
	public Chunk[] getBuildingChunks() {
		return buildingChunks;
	}
	
	/**Clears the building reference of all the {@link #buildingChunks}*/
	public void clearBuildingChunks() {
		for(int i = 0; i < buildingChunks.length; i++) {
			buildingChunks[i].setBuilding(null);
		}		
	}
	
	/**Levels all the {@link Chunk}s under this building (Makes them the same terrain height) based on surrounding values.
	 * Updates terrain accordingly.*/
	public void levelTerrainOfBuildingChunks() {
		HashSet<Chunk> chunks = new HashSet<Chunk>();
		ArrayList<Chunk> bChunks = new ArrayList<Chunk>(Arrays.asList(buildingChunks));
		for(Chunk c : bChunks) {
			chunks.addAll(c.getNeighbours());
		}
		 
		List<Chunk> borderChunks = new ArrayList<Chunk>(chunks);
		Iterator<Chunk> i = borderChunks.iterator();
		while (i.hasNext()) {
		   Chunk c = i.next();
		   if (bChunks.contains(c)) i.remove();   
		}
		
		int total = 0;
		int count = 0;
		for (Chunk c : borderChunks) {
			total += (int) c.getHeight();
			count++;
		}
		
		Set<Section> toUpdate = new HashSet<Section>();
		int avg = (int) Math.ceil((double) total / count);
		for (Chunk c : bChunks) {
			c.setHeight(avg);
			toUpdate.add(c.getSection());
		}
		for (Chunk c : borderChunks) {
			c.setHeight(avg);
			toUpdate.add(c.getSection());
		}
		for (Section s : toUpdate) {
			GameScreen.chunks.updateSectionMesh(s, false);
		}
	}
	
	/**@return Build cost of the building.*/
	public int getBuildCost() {
		return Base.DEFAULT_BUILD_COST;
	}
	
	/**@return Energy cost of the building.*/
	public int getEnergyCost() {
		return Base.DEFAULT_ENERGY_COST;
	}
	
	/**@return Energy usage of the building.*/
	public int getEnergyUsage() {
		return Base.DEFAULT_ENERGY_USAGE;
	}
	
	public boolean isUsingEnergy() {
		return isUsingEnergy;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	@Override
	public int getID() {
		return id;
	}

	@Override
	public int getIndex() {
		return GameScreen.buildingManager.getAllRegularBuildings().indexOf(this);
	}

	@Override
	public abstract EntityType getType();

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	/**Returns the X coordinate of the center of the building*/
	public float getCX() {
		return cx;
	}

	/**Returns the Y coordinate of the center of the building*/
	public float getCY() {
		return cy;
	}
}
