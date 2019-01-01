package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**A building representing a segment of a certain {@link Conveyor}.
 * This building is built and managed by the {@link Conveyor} itself.
 * It also builds itself immediately after initialisation.*/
public class ConveyorBuilding extends AbstractBuilding {
	
	static float width = Base.CHUNK_SIZE*1, height = Base.CHUNK_SIZE*1;
	
	Conveyor assignedConveyor;
	
	/**A building representing a segment of a certain {@link Conveyor}.
	 * This building is built and managed by the {@link Conveyor} itself.
	 * It also builds itself immediately after initialisation.*/
	public ConveyorBuilding(float cx, float cy, Conveyor c) {
		super(cx, cy, width, height);
		this.assignedConveyor = c;
		this.fogOfWarRadius = 6;
		this.setLocation(cx, cy, true);
		this.build();
	}

	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		//This building CANNOT be drawn.
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		//This building CANNOT be drawn.	
	}
	
	@Override
	public void build() {
		GameScreen.buildingManager.addMisc(this);
		
		updateFogOfWar(true);
	}
	
	@Override
	public void demolish() {
		GameScreen.buildingManager.removeMisc(this);
		
		clearBuildingChunks();
		updateFogOfWar(false);	
	}
	
	/**Get the assigned {@link Conveyor}.*/
	public Conveyor getConveyor() {
		return assignedConveyor;
	}
}
