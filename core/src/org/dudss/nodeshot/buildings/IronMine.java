 package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.entities.connectors.Conveyor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class IronMine extends AbstractMine {
	
	public IronMine(float cx, float cy) {
		super(cx, cy);
		prefabColor = new Color(0, 0, 1, 0.5f);
	}

	public void generate() {
		if (canGenerate) {
			if (this.output.getAllConnectedNodes().size() > 0 ) {
				if (this.firstConveyor.checkEntrance(output, Base.PACKAGE_BLOCK_RANGE)) {
					Iron coal = new Iron(this.output);
					output.sendPackage(coal, firstConveyor);
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
	public void build() {
		output = new OutputNode(x + (width/2), (float) (y + (height/2)), Base.RADIUS, this);
		export = new ConveyorNode(x + (width/2), (float) (y + Base.CHUNK_SIZE/2), Base.RADIUS);
		output.connectTo(export);
		
		firstConveyor = (Conveyor) GameScreen.connectorHandler.getConnectorInbetween(output, export, export.getConnectors());
		
		GameScreen.nodelist.add(output);
		GameScreen.nodelist.add(export);
		buildingHandler.addBuilding(this);
		

		int tileX = (int) (this.x / Base.CHUNK_SIZE);
		int tileY = (int) (this.y / Base.CHUNK_SIZE);
		
		float totalOreLevel = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (GameScreen.chunks.getChunkAtTileSpace(tileX + x, tileY + y) != null) {
				totalOreLevel += GameScreen.chunks.getChunkAtTileSpace(tileX + x, tileY + y).getIronLevel();				
				}
			}
		}
		
		if (totalOreLevel > 0) {
			canGenerate = true;
			productionRate = Math.round((productionRate / totalOreLevel));
		}
			
		nextSimTick = SimulationThread.simTick + productionRate;		
		
		updateFogOfWar(true);
	}
}

