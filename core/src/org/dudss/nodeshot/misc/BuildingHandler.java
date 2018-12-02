package org.dudss.nodeshot.misc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.buildings.Building;
import org.dudss.nodeshot.buildings.CreeperGenerator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**Manages all buildings in the game world. Calls logic updates and render calls*/
public class BuildingHandler {

	List<Building> buildings;
	List<Building> generators;
	
	public BuildingHandler() {
		buildings = new CopyOnWriteArrayList<Building>();
		generators = new CopyOnWriteArrayList<Building>();
	}
	
	public void updateAll() {
		for (Building b : buildings) {
			b.update();
		}
	}
	
	public void updateAllGenerators() {
		for (Building b : generators) {
			b.update();
		}
	}
	
	public void drawAll(ShapeRenderer r, SpriteBatch batch) {
		for (Building b : buildings) {
			b.draw(r, batch);
		}
	}
	
	public void drawAllGenerators(ShapeRenderer r, SpriteBatch batch) {
		for (Building b : generators) {
			b.draw(r, batch);
		}
	}
	
	public void addBuilding(Building b) {
		buildings.add(b);
	}
	
	public void removeBuilding(Building b) {
		buildings.remove(b);
	}
	
	public List<Building> getAllBuildings() {
		return buildings;
	}
	
	public void addGenerator(Building b) {
		generators.add(b);
	}
	
	public void removeGenerator(Building b) {
		generators.remove(b);
	}
	
	public List<Building> getAllGenerators() {
		return generators;
	}	
}
