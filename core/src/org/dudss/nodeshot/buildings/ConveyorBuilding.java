package org.dudss.nodeshot.buildings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.entities.effects.Explosion;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**A building representing a segment of a certain {@link Conveyor}.
 * This building is built and managed by the {@link Conveyor} itself.
 * It also builds itself immediately after initialisation.
 * 
 * This building can act as a junction when more than 1 {@linkplain Conveyor} is passing above it.*/
public class ConveyorBuilding extends AbstractBuilding {
	
	static float width = Base.CHUNK_SIZE*1, height = Base.CHUNK_SIZE*1;
	
	List<Conveyor> assignedConveyors;
	
	/**A building representing a segment of a certain {@link Conveyor}.
	 * This building is built and managed by the {@link Conveyor} itself.
	 * It also builds itself immediately after initialisation.*/
	public ConveyorBuilding(float cx, float cy, Conveyor c) {
		super(cx, cy, width, height);
		this.assignedConveyors = new ArrayList<Conveyor>(Arrays.asList(c));
		this.buildingType = BuildingType.MISC;
		this.fogOfWarRadius = 6;
		this.selectable = false;
		this.setLocation(cx, cy, true);
		this.build();
	}

	@Override
	public void draw(SpriteBatch batch) {
		int i = assignedConveyors.indexOf(GameScreen.selectedEntity);
		if (i != -1) {
			batch.draw(SpriteLoader.selectReticleBig, getX(), getY(), getWidth(), getHeight());
		}		
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		//This building CANNOT be drawn.	
	}
	
	@Override
	public void explode() {
		new Explosion(cx, cy);
		for (Conveyor c : assignedConveyors) {
			c.getFrom().disconnect(c.getTo());
		}
	}
	
	/**Adds {@link Conveyor} to this conveyor building.*/
	public void addConveyor(Conveyor c) {
		assignedConveyors.add(c);
		BaseClass.logger.fine("Added conveyor to junction: " + this.assignedConveyors.size());
	}
	
	/**Removes a {@link Conveyor} from this conveyor building.*/
	public void removeConveyor(Conveyor c) {
		assignedConveyors.remove(c);
		BaseClass.logger.fine("Removed conveyor from junction: " + this.assignedConveyors.size());
	}
	
	/**Get the assigned {@link Conveyor}.*/
	public List<Conveyor> getConveyors() {
		return assignedConveyors;
	}
	
	public int getBuildCost() {
		return Base.CONVEYOR_BUILD_COST;
	}
	
	public int getEnergyCost() {
		return Base.CONVEYOR_ENERGY_COST;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.CONVEYOR_BUILDING;
	}
}
