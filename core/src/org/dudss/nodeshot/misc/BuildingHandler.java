package org.dudss.nodeshot.misc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.buildings.CreeperGenerator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**Manages all buildings in the game world. Calls logic updates and render calls*/
public class BuildingHandler {

	List<AbstractBuilding> buildings;
	List<AbstractBuilding> generators;
	
	public BuildingHandler() {
		buildings = new CopyOnWriteArrayList<AbstractBuilding>();
		generators = new CopyOnWriteArrayList<AbstractBuilding>();
	}
	
	public void updateAll() {
		for (AbstractBuilding b : buildings) {
			b.update();
		}
	}
	
	public void updateAllGenerators() {
		for (AbstractBuilding b : generators) {
			b.update();
		}
	}
	
	public void drawAll(ShapeRenderer r, SpriteBatch batch) {
		for (AbstractBuilding b : buildings) {
			b.draw(r, batch);
		}
	}
	
	public void drawAllGenerators(ShapeRenderer r, SpriteBatch batch) {
		for (AbstractBuilding b : generators) {
			b.draw(r, batch);
		}
	}
	
	public void addBuilding(AbstractBuilding b) {
		buildings.add(b);
	}
	
	public void removeBuilding(AbstractBuilding b) {
		buildings.remove(b);
	}
	
	public List<AbstractBuilding> getAllBuildings() {
		return buildings;
	}
	
	public void addGenerator(AbstractBuilding b) {
		generators.add(b);
	}
	
	public void removeGenerator(AbstractBuilding b) {
		generators.remove(b);
	}
	
	public List<AbstractBuilding> getAllGenerators() {
		return generators;
	}	
}
