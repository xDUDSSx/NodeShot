package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**Building that generates items at a rate.*/
public abstract class BasicMine extends Building {

		//Building node - outputs Item
		OutputNode output;
		//The iron mine boundary node
		Node export;
		//First and the only connector of the output node
		Connector firstConnector;
		//Target node - where is Item sent
		Node target;

		static float width = 48;
		static float height = 48;
		
		public int productionRate = 600;
		public int nextSimTick = -1;
		
		public boolean canGenerate = false;
		
		Color prefabColor;
		
		public BasicMine(float cx, float cy) {
			super(cx, cy, width, height);
		}
		
		@Override
		public void update() {
			if (nextSimTick <= SimulationThread.simTick) {
				nextSimTick = SimulationThread.simTick + productionRate; 
				generate();
			}
		}

		abstract public void generate();
		
		@Override
		public void draw(ShapeRenderer r, SpriteBatch batch) {	
			r.set(ShapeType.Filled);
			r.setColor(new Color(Color.argb8888(0.2f, 0.2f, 0.2f, 1f)));
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
					GameScreen.chunks.getChunk(tileX, tileY).getCoalLevel() + 
					GameScreen.chunks.getChunk(tileX + 1, tileY).getCoalLevel() +
					GameScreen.chunks.getChunk(tileX, tileY + 1).getCoalLevel() + 
					GameScreen.chunks.getChunk(tileX + 1, tileY + 1).getCoalLevel();
			
			if (totalOreLevel > 0) {
				canGenerate = true;
				productionRate = Math.round((productionRate / totalOreLevel));
			}
				
			nextSimTick = SimulationThread.simTick + productionRate;		
		}
		
		@Override
		public void demolish() {
			GameScreen.buildingHandler.removeBuilding(this);
			this.output.remove();
			this.export.remove();
		}

		@Override
		public void alert(Package p) {
			// TODO Auto-generated method stub
			
		}

}
