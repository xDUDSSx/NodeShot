package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.entities.effects.Explosion;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

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
		this.buildingType = BuildingType.MISC;
		this.fogOfWarRadius = 6;
		this.setLocation(cx, cy, true);
		this.build();
	}

	@Override
	public void draw(SpriteBatch batch) {
		//This building CANNOT be drawn.	
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		//This building CANNOT be drawn.	
	}
	
	@Override
	public void explode() {
		new Explosion(cx, cy);
		assignedConveyor.getFrom().disconnect(assignedConveyor.getTo());
	}
	
	/**Get the assigned {@link Conveyor}.*/
	public Conveyor getConveyor() {
		return assignedConveyor;
	}
	
	public int getBuildCost() {
		return Base.CONVEYOR_BUILD_COST;
	}
	
	public int getEnergyCost() {
		return Base.CONVEYOR_ENERGY_COST;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.CONVEYOR;
	}
}
