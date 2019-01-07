package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.Chunks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**The basic skeletal representation of a building. All buildings are subclasses of this abstract class.*/
public abstract class AbstractBuilding implements Entity {
	float x,y;
	float cx,cy;
	
	float width;
	float height;
	
	boolean outlined = false;
	boolean isBuilt = false;
	
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
	
	/**Sets all the necessary position attributes and sets up building chunks array
	 * @return Returns whether this building intersects another building
	 * */
	public boolean setLocation(float cx, float cy, boolean snap) {
		Vector2 newCoords = getCoordinates(cx, cy, snap);
		x = newCoords.x;
		y = newCoords.y;
		
		int index = 0;
		int buildintWidthInTileSpace = Math.round(width/Base.CHUNK_SIZE);
		int buildingHeightInTileSpace = Math.round(height/Base.CHUNK_SIZE);
		
		boolean intersectsAnotherBuilding = false;
		
		buildingChunks = new Chunk[buildintWidthInTileSpace*buildingHeightInTileSpace];
		for (int gx = 0; gx < buildintWidthInTileSpace; gx++) {
			for (int gy = 0; gy < buildingHeightInTileSpace; gy++) {
				Chunk c = GameScreen.chunks.getChunkAtTileSpace((int)(this.x/Base.CHUNK_SIZE) + gx, (int)(this.y/Base.CHUNK_SIZE) + gy);
				if (c != null) {
					buildingChunks[index++] = c;		
					if (c.getBuilding() != null) {
						intersectsAnotherBuilding = true;
					}
				} else {
					return false;
				}
			}
		}
		
		if (intersectsAnotherBuilding) {
			return false;
		} else {
			for (int i = 0; i < buildingChunks.length; i++) {
				buildingChunks[i].setBuilding(this);
			}
			return true;
		}
	}
	
	/**Retrieves the coordinates that would apply for the following world-space cursor coordinates*/
	protected Vector2 getCoordinates(float cx, float cy, boolean snap) {
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
		
		return new Vector2(newX, newY);
	}
	
	public Vector2 getPrefabVector(float cx, float cy, boolean snap) {
		return getCoordinates(cx, cy, snap);
	}
	
	/**Building update method, updated by the {@link SimulationThread}.
	 * The default {@link AbstractBuilding} update method handles creeper damage and building demolishing.
	 * So if a building extending this object calls super.update() within its update method, it will be harmed by the creeper.*/
	public void update() {
		for (int i = 0; i < buildingChunks.length; i++) {
			if (buildingChunks[i] != null) {
				if (buildingChunks[i].getCreeperLevel() > 0) {
					this.explode();
				}
			}
		}
	}	
	
	/**Draw and prefab draw methods (prefab is the building representation following the cursor when in build mode)*/
	public abstract void draw(ShapeRenderer r, SpriteBatch batch);
	
	/**Draw and prefab draw methods (prefab is the building representation following the cursor when in build mode)*/
	public abstract void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap);	
	
	/**Called when the building is built*/
	public void build() {
		GameScreen.buildingManager.addBuilding(this);
		
		updateFogOfWar(true);
	}	
	/**Called upon demolition*/
	public void demolish() {
		GameScreen.buildingManager.removeBuilding(this);
		
		clearBuildingChunks();
		updateFogOfWar(false);	
	}
	/**Called when demolised by force*/
	public void explode() {
		//Do explosion related stuff
		this.demolish();
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
		return GameScreen.buildingManager.getAllBuildings().indexOf(this);
	}

	@Override
	public EntityType getType() {
		return EntityType.BUILDING;
	}

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
