package org.dudss.nodeshot.buildings;

import java.util.ArrayList;
import java.util.Arrays;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**Storage building that stores any {@link StorableItem}.*/
public class UniversalStorage extends AbstractStorage {
	static float width = Base.CHUNK_SIZE*3, height = Base.CHUNK_SIZE*3;
	
	public UniversalStorage(float cx, float cy) {
		super(cx, cy, width, height);
		accepted = new ArrayList<ItemType>();
		this.maxStorage = 50;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.setColor(1f, 1f, 1f, 1f);	
		if (this.storage.size() < this.maxStorage) {
			batch.draw(SpriteLoader.hqanimAtlas.findRegion("storageOn"), x, y, width, height);		
		} else {
			batch.draw(SpriteLoader.hqanimAtlas.findRegion("storageOff"), x, y, width, height);	
		}
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {				
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(SpriteLoader.hqanimAtlas.findRegion("storageOn"), getPrefabVector(cx, cy, snap).x, getPrefabVector(cx, cy, snap).y, width, height);
		batch.end();	
	}

	@Override
	public EntityType getType() {
		return EntityType.UNIVERSAL_STORAGE;
	}
}
