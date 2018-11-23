package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class CreeperGenerator extends Building {
	
	OutputNode output;
	
	protected Color prefabColor = new Color(49f/255f, 209f/255f, 12f/255f, 0.5f);
	protected Color color = new Color(35/255f, 175/255f, 3/255f, 1f);
	
	public int productionRate = 20;
	public int nextSimTick = -1;
	
	public CreeperGenerator(float cx, float cy) {		
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
		int tileX = (int) (this.x / Base.CHUNK_SIZE);
		int tileY = (int) (this.y / Base.CHUNK_SIZE);

		if (GameScreen.chunks.getChunk(tileX + 1, tileY + 1) != null) {
			GameScreen.chunks.getChunk(tileX + 1, tileY + 1).setCreeperLevel(10f);	
		}	
	}

	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		r.set(ShapeType.Filled);
		r.setColor(color);
		r.rect(x, y, width, height);
	}

	@Override
	public void drawPrefab(ShapeRenderer r, float cx, float cy, boolean snap) {
		float prefX;
		float prefY;
		
		if (snap) {
			float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
			float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
			
			prefX = nx - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
			prefY = ny - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;	
		} else {
			prefX = cx - (width/2);
			prefY = cy - (height/2);
		}
		
		r.set(ShapeType.Filled);
		r.setColor(prefabColor);
		r.rect(prefX, prefY, width, height);	
	}

	@Override
	public void build() {
		nextSimTick = SimulationThread.simTick + productionRate;
		output = new OutputNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		buildingHandler.addBuilding(this);
		GameScreen.nodelist.add(output);
	}

	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeBuilding(this);	
		this.output.remove();
	}

	@Override
	public void alert(Package p) {
		// TODO Auto-generated method stub	
	}

}