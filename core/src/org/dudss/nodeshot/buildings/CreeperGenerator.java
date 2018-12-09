package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**A creeper generator building that generates creeper*/
public class CreeperGenerator extends AbstractGenerator {

	OutputNode output;
	protected Color prefabColor = new Color(49f/255f, 209f/255f, 12f/255f, 0.5f);
	protected Color color = new Color(35/255f, 175/255f, 3/255f, 1f);
	
	/**Height of the spawned creeper*/
	public float spawnRate = Base.MAX_CREEP;
	
	//Building constructor
	public CreeperGenerator(float cx, float cy) {
		super(cx, cy);
	}
	
	@Override
	public void update() {
		if (GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE)).visibility != Chunk.deactivated) {
			this.active = true;
			generate();
		}
	}
	
	@Override
	protected void generate() {
		if (active) {
			int tileX = (int) ((this.x + Base.CHUNK_SIZE) / Base.CHUNK_SIZE);
			int tileY = (int) ((this.y + Base.CHUNK_SIZE) / Base.CHUNK_SIZE);
			GameScreen.chunks.getChunk(tileX, tileY).setCreeperLevel(spawnRate);	
		}
	}

	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		if (active) {
			batch.draw(SpriteLoader.creeperGenOnFrame, x, y, width, height);
		} else {
			batch.draw(SpriteLoader.creeperGenOffFrame, x, y, width, height);
		}		
		batch.end();
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		if (active) {
			batch.draw(SpriteLoader.creeperGenOnFrame, getPrefabX(cx, snap), getPrefabY(cy, snap), width, height);
		} else {
			batch.draw(SpriteLoader.creeperGenOffFrame, getPrefabX(cx, snap), getPrefabY(cy, snap), width, height);
		}		
		batch.end();	
	}

	@Override
	public void build() { 
		output = new OutputNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		buildingHandler.addGenerator(this);
		GameScreen.nodelist.add(output);
		
		Base.locateSectionByWorldSpace(x, y).setActive(true);
		GameScreen.chunks.updateFogOfWarMesh(Base.locateSectionByWorldSpace(x, y));
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
