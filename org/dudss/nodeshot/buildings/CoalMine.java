package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class CoalMine implements Building {
	
	//Building node - outputs coal
	Node output;
	//Target node - where is coal sent
	Node target;
	
	float x,y;
	float cx,cy;
	float width = 32;
	float height = 32;
	
	public int productionRate = 150;
	public int nextSimTick = -1;
	
	public CoalMine(float cx, float cy) {
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
			for (Building b : GameScreen.buildingHandler.getAllBuildings()) {
				if (b instanceof Storage) {
					Coal coal = new Coal(this.output, this.output.getAllConnectedNodes().get(0));
					output.sendPackage(((Storage) b).getInputNode(), coal);
					System.out.println("sending");
				}
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
	public void build() {
		init();
		output = new Node(x + (width/2), y + (height/2), Base.RADIUS);
		GameScreen.nodelist.add(output);
	}

	private void init() {
		nextSimTick = SimulationThread.simTick + productionRate;
	}
	
	@Override
	public void demolish() {
		
	}
}
