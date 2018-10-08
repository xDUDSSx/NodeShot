 package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class IronMine extends BasicMine {
	
	public IronMine(float cx, float cy) {
		super(cx, cy);
		prefabColor = new Color(0, 0, 1, 0.5f);
	}

	public void generate() {
		if (canGenerate) {
			if (this.output.getAllConnectedNodes().size() > 0 ) {
				if (this.firstConnector.checkEntrance(output, Base.PACKAGE_BLOCK_RANGE)) {
					Iron coal = new Iron(this.output);
					output.sendPackage(coal);
				}
			}
		}
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {	
		r.set(ShapeType.Filled);
		r.setColor(Color.BLUE);
		r.rect(x, y, width, height);
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, float cx, float cy, boolean snap) {		
		float prefX; 
		float prefY;
		
		if (snap) {
			float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
			float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
			
			prefX = nx - (width/2);
			prefY= ny - (height/2);	
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
		output = new OutputNode(x + (width/2), (float) (y + (height*0.75)), Base.RADIUS, this);
		export = new ConveyorNode(x + (width/2), (float) (y + height*0.15), Base.RADIUS);
		output.connectTo(export);
		
		firstConnector = GameScreen.connectorHandler.getConnectorInbetween(output, export, export.getConnectors());
		
		GameScreen.nodelist.add(output);
		GameScreen.nodelist.add(export);
		buildingHandler.addBuilding(this);
		
		int tileX = (int) (this.x / Base.CHUNK_SIZE);
		int tileY = (int) (this.y / Base.CHUNK_SIZE);
		
		float totalOreLevel = 
				GameScreen.chunks.getChunk(tileX, tileY).getIronLevel() + 
				GameScreen.chunks.getChunk(tileX + 1, tileY).getIronLevel() +
				GameScreen.chunks.getChunk(tileX, tileY + 1).getIronLevel() + 
				GameScreen.chunks.getChunk(tileX + 1, tileY + 1).getIronLevel();
		
		if (totalOreLevel > 0) {
			canGenerate = true;
			productionRate = Math.round((productionRate / totalOreLevel));
		}
			
		nextSimTick = SimulationThread.simTick + productionRate;		
	}
}

