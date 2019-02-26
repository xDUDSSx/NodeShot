package org.dudss.nodeshot.buildings;

import java.util.Arrays;
import java.util.Iterator;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**A building that can exchange {@link ItemType#PROCESSED_MATERIAL} for bits.*/
public class Shipdock extends AbstractStorage {

	static float width = Base.CHUNK_SIZE*3, height = Base.CHUNK_SIZE*3;
	
	TextureRegion t = new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("shipdock"));	
	
	int generatedBits = 5;
	
	public Shipdock(float cx, float cy) {
		super(cx, cy, width, height);
		accepted = Arrays.asList(ItemType.PROCESSED_MATERIAL);
		maxStorage = 1;
	}
	
	@Override 
	public void update() {
		super.update();
		if (this.storage.size() > 0) {
			Iterator<StorableItem> i = storage.iterator(); 
	        while (i.hasNext()) 
	        { 
	        	i.next();
	        	i.remove();
	        	GameScreen.resourceManager.addBits(generatedBits);
	        	System.out.println("Selling processed material for 5 bits!");
	        }
		}
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.setColor(1f, 1f, 1f, 1f);		
		batch.draw(t, x, y, width, height);	
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {				
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(t, getPrefabVector(cx, cy, snap).x, getPrefabVector(cx, cy, snap).y, width, height);
		batch.end();	
	}
	
	@Override
	public EntityType getType() {
		return EntityType.SHIPDOCK;
	}
}
