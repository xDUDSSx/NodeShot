package org.dudss.nodeshot.buildings;

import java.util.Arrays;

import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Factory extends AbstractIOStorage {
	static float width = 48;
	static float height = 48;
	
	Animation<TextureRegion> factoryAnimation;
	Animation<TextureRegion> factoryOutlinedAnimation;

	float processingSpeed = 100;
	long lastProcess = SimulationThread.simTick;
	
	public Factory(float cx, float cy) {
		super(cx, cy, width, height);
		accepted = Arrays.asList(ItemType.COAL);
		
		factoryAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.factoryanimFrames);	
		factoryOutlinedAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.factoryanimoutlineFrames);	
	}
		
	@Override
	public void update() {
		super.update();
		
		if (lastProcess + processingSpeed < SimulationThread.simTick && storage.size() > 0) {
			if (processedStorage.size() < maxProcessedStorage) {
				generate();
				lastProcess = SimulationThread.simTick;
			}
		}
	}

	private void generate() {	
		this.processedStorage.add(new StorableItem(ItemType.AMMO));
		this.storage.remove(storage.size()-1);
		System.out.println("generating at " + SimulationThread.simTick + " storage: " + storage.size() + " processedStorage: " + processedStorage.size());
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		if (outlined) {
			TextureRegion currentFrame = factoryOutlinedAnimation.getKeyFrame(GameScreen.stateTime, true);
			batch.draw(currentFrame, x, y, width, height);
		} else {
			TextureRegion currentFrame = factoryAnimation.getKeyFrame(GameScreen.stateTime, true);
			batch.draw(currentFrame, x, y, width, height);
		}		
		
		input.draw(batch);
		batch.end();
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {				
		TextureRegion currentFrame = factoryAnimation.getKeyFrame(GameScreen.stateTime, true);
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(currentFrame, getPrefabX(cx, snap), getPrefabY(cy, snap), width, height);
		batch.end();	
	}
}
