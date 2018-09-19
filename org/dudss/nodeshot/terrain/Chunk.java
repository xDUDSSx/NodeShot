package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Chunk {
	float x, y;
	float size = Base.CHUNK_SIZE;
	
	//0 to 1.0 range
	float coalOre = 0f;
	float ironOre = 0f;
	
	//TODO: implement
	float creeper = 0;
	
	Sprite dirtTile;
	Sprite coalTile;
	Sprite ironTile;

	Chunk(float x, float y) {
		this.x = x;
		this.y = y;
		
		dirtTile = new Sprite(SpriteLoader.bigdirtTileSprite);
		coalTile = new Sprite(SpriteLoader.coalTileSprite);
		ironTile = new Sprite(SpriteLoader.ironTileSprite);
	}
	
	public void update() {
		
	}
	
	public void draw(SpriteBatch batch, int column, int row) {
		if ((column % 2 == 0 || column == 0) && (row % 2 == 0 | row == 0)) {
			dirtTile.setPosition(x, y);
			dirtTile.setSize(size*2, size*2);
			dirtTile.draw(batch);
		}
		
		if (coalOre != 0) {
			float level = Base.range(coalOre, Base.COAL_THRESHOLD, 1, 0.7f, 1);
			coalTile.setPosition(x, y);
			coalTile.setAlpha(level);
			coalTile.draw(batch);
		} else
		if (ironOre != 0) {
			float level = Base.range(ironOre, Base.IRON_THRESHOLD, 1, 0.7f, 1);
			ironTile.setPosition(x, y);
			ironTile.setAlpha(level);
			ironTile.draw(batch);
		}		
	}
	
	public void setCoalLevel(float level) {
		coalOre = level;
	}
	public void setIronLevel(float level) {
		ironOre = level;
	}
}
