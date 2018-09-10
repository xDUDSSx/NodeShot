 package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.OutputNode;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class IronMine implements Building {
	
	//Building node - outputs Iron
	OutputNode output;
	//Target node - where is Iron sent
	Node target;
	
	float x,y;
	float cx,cy;
	float width = 32;
	float height = 32;
	
	public int productionRate = 200;
	public int nextSimTick = -1;
	
	Color prefabColor = new Color(0, 0, 1, 0.5f);
	
	public IronMine(float cx, float cy) {
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
		System.out.println("IronMine generate! at " + System.currentTimeMillis());
		if (this.output.getAllConnectedNodes().size() > 0) {
			/*for (Building b : GameScreen.buildingHandler.getAllBuildings()) {
				if (b instanceof Storage) {
					Iron Iron = new Iron(this.output, this.output.getAllConnectedNodes().get(0));
					output.sendPackage(((Storage) b).getInputNode(), Iron);					
					System.out.println("sending");
				}
			}*/
			Iron iron = new Iron(this.output);
			output.sendPackage(iron);
			System.out.println("sending");
		}
	}
	
	@Override
	public void draw(ShapeRenderer r) {	
		r.set(ShapeType.Filled);
		r.setColor(Color.BLUE);
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
		//Sets the inital production time
		nextSimTick = SimulationThread.simTick + productionRate;			
	}
	
	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeBuilding(this);
		this.output.remove();
	}

	@Override
	public void alert() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void alert(Package p) {
		// TODO Auto-generated method stub
		
	}
}

