package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.utils.SpriteLoader;
import org.dudss.nodeshot.entities.Package;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BasicMine extends AbstractMine {
	
	Sprite on;
	Sprite off;
	
	Animation<TextureRegion> genAnimation;
	Animation<TextureRegion> genOutlinedAnimation;
	
	Color color = new Color(Color.argb8888(0.2f, 0.2f, 0.2f, 1f));
	
	public BasicMine(float cx, float cy) {
		super(cx, cy);
		on = new Sprite(SpriteLoader.mineOn);
		off = new Sprite(SpriteLoader.mineOff);
		activateIONode(true);
	}

	public void generate() {
		if (canGenerate) {	
			if (ioNode.canSendPackage()) {
				Package p = oreType.getPackage();
				p.setParams(ioNode, null);
				ioNode.sendIOPackage(p);		
			} 
		}
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {	
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		if (canGenerate) {
			batch.draw(SpriteLoader.mineOn, x, y, width, height);
		} else {
			batch.draw(SpriteLoader.mineOff, x, y, width, height);
		}		
		batch.end();
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(SpriteLoader.mineOff, getPrefabVector(cx, cy, snap).x, getPrefabVector(cx, cy, snap).y, width, height);
		batch.end();		
	}
}


