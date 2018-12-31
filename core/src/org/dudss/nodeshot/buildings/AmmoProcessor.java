package org.dudss.nodeshot.buildings;

import java.util.Arrays;

import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class AmmoProcessor extends Factory {

	TextureRegion t = new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("ammoProcessor"));
	
	public AmmoProcessor(float cx, float cy) {
		super(cx, cy);
		
		accepted = Arrays.asList(ItemType.PROCESSED_MATERIAL);
	}
	
	@Override
	protected void generate() {	
		this.processedStorage.add(new StorableItem(ItemType.AMMO));
		this.storage.remove(storage.size()-1);
		System.out.println("generating at " + SimulationThread.simTick + " storage: " + storage.size() + " processedStorage: " + processedStorage.size());
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		batch.draw(t, x, y, width, height);	
		batch.end();
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {				
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(t, getPrefabVector(cx, cy, snap).x, getPrefabVector(cx, cy, snap).y, width, height);
		batch.end();	
	}
}
