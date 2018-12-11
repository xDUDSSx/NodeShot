package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CoalMine extends AbstractMine {
	
	Animation<TextureRegion> genAnimation;
	Animation<TextureRegion> genOutlinedAnimation;
	
	Color color = new Color(Color.argb8888(0.2f, 0.2f, 0.2f, 1f));
	
	public CoalMine(float cx, float cy) {
		super(cx, cy);
		genAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.genanimFrames);	
		genOutlinedAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.genanimoutlineFrames);
		prefabColor = new Color(0.2f, 0.2f, 0.2f, 0.5f);
	}

	public void generate() {
		if (canGenerate) {
			if (this.output.getAllConnectedNodes().size() > 0 ) {
				if (this.firstConveyor.checkEntrance(output, Base.PACKAGE_BLOCK_RANGE)) {
					Coal coal = new Coal(this.output);
					output.sendPackage(coal, firstConveyor);
				}
			}
		}
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {	
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		if (outlined) {
			TextureRegion currentFrame = genOutlinedAnimation.getKeyFrame(GameScreen.stateTime, true);
			batch.draw(currentFrame, x, y, width, height);
		} else {
			TextureRegion currentFrame = genAnimation.getKeyFrame(GameScreen.stateTime, true);
			batch.draw(currentFrame, x, y, width, height);
		}		
		batch.end();
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		TextureRegion currentFrame = genAnimation.getKeyFrame(GameScreen.stateTime, true);		
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(currentFrame, getPrefabX(cx, snap), getPrefabY(cy, snap), width, height);
		batch.end();		
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
					totalOreLevel += GameScreen.chunks.getChunkAtTileSpace(tileX + x, tileY + y).getCoalLevel();				
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


