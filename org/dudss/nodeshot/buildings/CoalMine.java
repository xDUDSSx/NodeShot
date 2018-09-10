package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.OutputNode;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class CoalMine implements Building {
	
	//Building node - outputs coal
	OutputNode output;
	//Target node - where is coal sent
	Node target;
	
	float x,y;
	float cx,cy;
	float width = 32;
	float height = 32;
	
	public int productionRate = 150;
	public int nextSimTick = -1;
	
	private Color prefabColor = new Color(0, 0, 0, 0.5f);
	
	public CoalMine(float cx, float cy) {
		this.cx = cx;
		this.cy = cy;
		
		x = cx - (width/2);
		y = cy - (height/2);
	}
	
	public void setLocation(float cx, float cy) {
		this.cx = cx;
		this.cy = cy;
		
		x = cx - (width/2);
		y = cy - (height/2);
	}
	
	@Override
	public void update() {
		if (nextSimTick <= SimulationThread.simTick) {
			nextSimTick = SimulationThread.simTick + productionRate; 
			generate();
		}
	}

	public void generate() {
		System.out.println("CoalMine generate! at " + System.currentTimeMillis());
		if (this.output.getAllConnectedNodes().size() > 0) {
			Storage storage = null;
			double dist = 0;
			boolean storageFound = false;
			for (Building b : GameScreen.buildingHandler.getAllBuildings()) {
				if (b instanceof Storage) {
					storageFound = true;
					double newDist = this.output.getDistance(((Storage) b).getInputNode());
					if (newDist < dist || dist == 0) {
						storage = (Storage) b;
						dist = newDist;
					}
				}
			}
			
			if (storageFound) {
				Coal coal = new Coal(this.output, this.output.getAllConnectedNodes().get(0));
				output.sendPackage(storage.getInputNode(), coal);
				System.out.println("sending");
			}
		}
	}
	
	@Override
	public void draw(ShapeRenderer r) {	
		r.set(ShapeType.Filled);
		r.setColor(Color.BLACK);
		r.rect(x, y, width, height);
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, float cx, float cy) {	
		float x = cx - (width/2);
		float y = cy - (height/2);
		
		r.set(ShapeType.Filled);
		r.setColor(prefabColor);
		r.rect(x, y, width, height);
	}
	
	@Override
	public void build() {
		init();
		output = new OutputNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		GameScreen.nodelist.add(output);
		buildingHandler.addBuilding(this);
	}

	private void init() {
		nextSimTick = SimulationThread.simTick + productionRate;
	}
	
	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeBuilding(this);
		this.output.remove();
	}

	@Override
	public void alert() {

	}


	@Override
	public void alert(Package p) {
		// TODO Auto-generated method stub
		
	}
}
