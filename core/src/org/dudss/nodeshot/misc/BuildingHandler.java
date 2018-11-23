package org.dudss.nodeshot.misc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.buildings.Building;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BuildingHandler {

	List<Building> buildings;

	public BuildingHandler() {
		buildings = new CopyOnWriteArrayList<Building>();
	}
	
	public void updateAll() {
		for (Building b : buildings) {
			b.update();
		}
	}
	
	public void drawAll(ShapeRenderer r, SpriteBatch batch) {
		for (Building b : buildings) {
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
}
