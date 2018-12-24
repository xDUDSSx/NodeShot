package org.dudss.nodeshot.buildings;

import static org.dudss.nodeshot.screens.GameScreen.buildingManager;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class PowerGenerator extends AbstractBuilding {
	
	static float width = Base.CHUNK_SIZE*2, height = Base.CHUNK_SIZE*2;
	
	Animation<TextureRegion> genAnimation;
	Animation<TextureRegion> genOutlinedAnimation;
		
	
	int generationSpeed = 30;
	int nextUpdate = SimulationThread.simTick + generationSpeed;
	
	public PowerGenerator(float cx, float cy) {
		super(cx, cy, width, height);
		genAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.genanimFrames);	
		genOutlinedAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.genanimoutlineFrames);
	}

	@Override
	public void update() {
		if (nextUpdate < SimulationThread.simTick) {
			GameScreen.resourceManager.addPower(1);
			nextUpdate = SimulationThread.simTick + generationSpeed;
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
		buildingManager.addBuilding(this);
		
		updateFogOfWar(true);
	}

	@Override
	public void demolish() {
		GameScreen.buildingManager.removeBuilding(this);

		clearBuildingChunks();
		updateFogOfWar(false);	
	}
}


