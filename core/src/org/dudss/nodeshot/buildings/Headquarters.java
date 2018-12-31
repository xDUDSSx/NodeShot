package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Headquarters extends AbstractBuilding {

	static float width = Base.CHUNK_SIZE*4, height = Base.CHUNK_SIZE*4;
	
	Animation<TextureRegion> hqAnimation;
	Animation<TextureRegion> hqOutlinedAnimation;
	
	public Headquarters(float cx, float cy) {
		super(cx, cy, width, height);
		hqAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.hqanimFrames);	
		hqOutlinedAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.hqanimoutlineFrames);	
	}

	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		if (outlined) {
			TextureRegion currentFrame = hqOutlinedAnimation.getKeyFrame(GameScreen.stateTime, true);
			batch.draw(currentFrame, x, y, width, height);
		} else {
			TextureRegion currentFrame = hqAnimation.getKeyFrame(GameScreen.stateTime, true);
			batch.draw(currentFrame, x, y, width, height);
		}		
		batch.end();
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		TextureRegion currentFrame = hqAnimation.getKeyFrame(GameScreen.stateTime, true);
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(currentFrame, getPrefabVector(cx, cy, snap).x, getPrefabVector(cx, cy, snap).y, width, height);
		batch.end();	
	}

	@Override
	public void build() {
		GameScreen.buildingManager.addBuilding(this);
		
		updateFogOfWar(true);
	}
	
	@Override
	public void demolish() {
		GameScreen.buildingManager.removeBuilding(this);
	
		clearBuildingChunks();
		updateFogOfWar(false);	
	}
}
