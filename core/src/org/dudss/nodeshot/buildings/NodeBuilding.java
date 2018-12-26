package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class NodeBuilding extends AbstractBuilding {
	
	static float width = Base.CHUNK_SIZE*1, height = Base.CHUNK_SIZE*1;
	
	TextureRegion s = new TextureRegion(SpriteLoader.node);
	
	ConveyorNode n;
	
	public NodeBuilding(float cx, float cy) {
		super(cx, cy, width, height);
	}

	@Override
	public void update() {

	}

	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		 
		batch.draw(s, x, y, width, height);		
		batch.end();
		
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(s, getPrefabX(cx, snap), getPrefabY(cy, snap), width, height);
		batch.end();		
		
	}

	@Override
	public void build() {
		n = new ConveyorNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		n.add();
		GameScreen.buildingManager.addBuilding(this);
		//Nodes are not updating fog of war
		//updateFogOfWar(true);
	}

	@Override
	public void demolish() {
		GameScreen.buildingManager.removeBuilding(this);
		n.remove();
	
		clearBuildingChunks();
		updateFogOfWar(false);	
	}

	public ConveyorNode getNode() {
		return n;
	}
}
