package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

/**A building that produces power.*/
public class PowerGenerator extends AbstractBuilding {
	
	static float width = Base.CHUNK_SIZE*2, height = Base.CHUNK_SIZE*2;
	
	Animation<TextureRegion> genAnimation;
	Animation<TextureRegion> genOutlinedAnimation;
		
	
	int generationSpeed = 6;
	int nextUpdate = SimulationThread.simTick + generationSpeed;
	
	public PowerGenerator(float cx, float cy) {
		super(cx, cy, width, height);
		genAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.genanimFrames);	
		genOutlinedAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.genanimoutlineFrames);
	}

	@Override
	public void update() {
		super.update();
		if (nextUpdate < SimulationThread.simTick) {
			GameScreen.resourceManager.addPower(1);
			nextUpdate = SimulationThread.simTick + generationSpeed;
		}
	}
	
	@Override
	public void draw(SpriteBatch batch) {	
		batch.setColor(1f, 1f, 1f, 1f);		
		TextureRegion currentFrame = genAnimation.getKeyFrame(GameScreen.stateTime, true);
		batch.draw(currentFrame, x, y, width, height);		
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		TextureRegion currentFrame = genAnimation.getKeyFrame(GameScreen.stateTime, true);		
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(currentFrame, getPrefabVector(cx, cy, snap).x, getPrefabVector(cx, cy, snap).y, width, height);		
		batch.end();		
	}
	
	@Override
	public void explode() {
		super.explode();
		GameScreen.terrainEditor.explosion(new Vector3(x, y, 0), 11);
	}

	@Override
	public EntityType getType() {
		return EntityType.POWER_GENERATOR;
	}
}


