package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class CoalMine extends BasicMine {
	
	Color color = new Color(Color.argb8888(0.2f, 0.2f, 0.2f, 1f));
	
	public CoalMine(float cx, float cy) {
		super(cx, cy);
		prefabColor = new Color(0.2f, 0.2f, 0.2f, 0.5f);
	}

	public void generate() {
		if (canGenerate) {
			if (this.output.getAllConnectedNodes().size() > 0 ) {
				if (this.firstConnector.checkEntrance(output, Base.PACKAGE_BLOCK_RANGE)) {
					Coal coal = new Coal(this.output);
					output.sendPackage(coal);
				}
			}
		}
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {	
		r.set(ShapeType.Filled);
		r.setColor(new Color(Color.argb8888(0.2f, 0.2f, 0.2f, 1f)));
		r.rect(x, y, width, height);
	}
	
	@Override
	public void build() {
		output = new OutputNode(x + (width/2), (float) (y + (height/2)), Base.RADIUS, this);
		export = new ConveyorNode(x + (width/2), (float) (y + Base.CHUNK_SIZE/2), Base.RADIUS);
		output.connectTo(export);
		
		firstConnector = GameScreen.connectorHandler.getConnectorInbetween(output, export, export.getConnectors());
		
		GameScreen.nodelist.add(output);
		GameScreen.nodelist.add(export);
		buildingHandler.addBuilding(this);
		
		int tileX = (int) (this.x / Base.CHUNK_SIZE);
		int tileY = (int) (this.y / Base.CHUNK_SIZE);
		
		float totalOreLevel = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (GameScreen.chunks.getChunk(tileX + x, tileY + y) != null) {
				totalOreLevel += GameScreen.chunks.getChunk(tileX + x, tileY + y).getCoalLevel();				
				}
			}
		}
		
		if (totalOreLevel > 0) {
			canGenerate = true;
			productionRate = Math.round((productionRate / totalOreLevel));
		}
			
		nextSimTick = SimulationThread.simTick + productionRate;		
	}	
}


