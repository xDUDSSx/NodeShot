package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks.OreType;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Chunk {
	float x, y;
	float size = Base.CHUNK_SIZE;
	
	//0 to 1.0 range
	float coalOre = 0f;
	float ironOre = 0f;
	
	//TODO: implement
	float creeper = 0;

	TextureRegion dirtTr;
	TextureRegion coalTr;
	TextureRegion coalLowerTr;
	TextureRegion coalLowTr;	
	TextureRegion ironTr;
	
	protected final int NUM_VERTICES = 20;
	protected float vertices[] = new float[NUM_VERTICES];
	
	Chunk(float x, float y) {
		this.x = x;
		this.y = y;
		
		dirtTr = new TextureRegion(SpriteLoader.savanaTex);
		coalTr = new TextureRegion(SpriteLoader.coalTex);
		coalLowerTr = new TextureRegion(SpriteLoader.coalLowerTex);
		coalLowTr = new TextureRegion(SpriteLoader.coalLowTex);
		ironTr = new TextureRegion(SpriteLoader.ironTex);	
	}
	
	public void update() {
		
	}
	
	public void draw(SpriteBatch batch, int column, int row) {
		if ((column % 2 == 0 || column == 0) && (row % 2 == 0 | row == 0)) {
			drawTile(dirtTr, batch);
		}		
		
		if (coalOre != 0) {	
			if (coalOre <= 0.25) {
				drawTile(coalLowTr, batch);
			} else 
			if (coalOre <= 0.5) {
				drawTile(coalLowerTr, batch);
			} else
			if (coalOre > 0.5) {
				drawTile(coalTr, batch);
			}		
		} else
		if (ironOre != 0) {
			drawTile(ironTr, batch);
		}
	}
	
	protected void drawTile(TextureRegion region, SpriteBatch batch) {
		final float u1 = region.getU();
		final float v1 = region.getV2();
		final float u2 = region.getU2();
		final float v2 = region.getV();
		
		final Color batchColor = batch.getColor();
		final float color = Color.toFloatBits(batchColor.r,batchColor.g, batchColor.b, batchColor.a * 1.0f);
		
		final float x1 = x * 1;
		final float y1 = y * 1;
		
		final float x2 = x1 + region.getRegionWidth() * 1;
		final float y2 = y1 + region.getRegionHeight() * 1;
		
		vertices[Batch.X1] = x1;
		vertices[Batch.Y1] = y1;
		vertices[Batch.C1] = color;
		vertices[Batch.U1] = u1;
		vertices[Batch.V1] = v1;

		vertices[Batch.X2] = x1;
		vertices[Batch.Y2] = y2;
		vertices[Batch.C2] = color;
		vertices[Batch.U2] = u1;
		vertices[Batch.V2] = v2;

		vertices[Batch.X3] = x2;
		vertices[Batch.Y3] = y2;
		vertices[Batch.C3] = color;
		vertices[Batch.U3] = u2;
		vertices[Batch.V3] = v2;

		vertices[Batch.X4] = x2;
		vertices[Batch.Y4] = y1;
		vertices[Batch.C4] = color;
		vertices[Batch.U4] = u2;
		vertices[Batch.V4] = v1;

		batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
	}
	
	public void setCoalLevel(float level) {
		if (level == -1) {
			coalOre = 0;
		} else {
		coalOre = Base.range(level, Base.COAL_THRESHOLD, 1.0f, 0f, 1.0f);
		}
	}
	public void setIronLevel(float level) {
		if (level == -1) {
			ironOre = 0;
		} else {
			ironOre = Base.range(level, Base.IRON_THRESHOLD, 1.0f, 0f, 1.0f);
		}
	}
	
	public float getCoalLevel() {
		return coalOre;
	}
	
	public float getIronLevel() {
		return ironOre;
	}
	
	public OreType getOreType() {
		if (this.coalOre != 0) {
			return OreType.COAL;
		} else 
		if (this.ironOre != 0) { 
			return OreType.IRON;
		} else {
			return OreType.NONE;
		}
	}
	
	public float getOreLevel() {
		if (coalOre > 0) {
			return this.coalOre;
		} else 
		if (ironOre > 0) { 
			return this.ironOre;
		} else {
			return 0;
		}
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}	
	public float getSize() {
		return size;
	}
}
