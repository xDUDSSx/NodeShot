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
	
	float creeper = 0;
	float plague = 0;

	boolean toExpand = false;
	
	TextureRegion dirtTr;
	TextureRegion coalTr;
	TextureRegion coalLowerTr;
	TextureRegion coalLowTr;	
	TextureRegion ironTr;
	
	protected final int NUM_VERTICES = 20;
	protected float vertices[] = new float[NUM_VERTICES];
	
	enum TriangleOrientation {
		BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT
	}
	
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
		/*
		if (toExpand) {
			if (this.creeper <= 1f && Base.getRandomFloatNumberInRange(0, 1) <= (0.1f + (this.creeper*0.1f))) {
				switch (Base.getRandomIntNumberInRange(1, 4)) {
				
					case 1: if (this.x != 0) {
								Chunk c1 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) - 1, (int)(this.y/Base.CHUNK_SIZE));
								c1.setCreeperLevel(Base.round(c1.getCreeperLevel() + 0.2f, 2));
								c1.setPlagueLevel(Base.round(c1.getPlagueLevel() - 0.05f, 2));
								break;
							}
					case 2: if ((this.x/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1 ) {
								Chunk c2 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) + 1, (int)(this.y/Base.CHUNK_SIZE));
								c2.setCreeperLevel(Base.round(c2.getCreeperLevel() + 0.2f, 2));
								c2.setPlagueLevel(Base.round(c2.getPlagueLevel() - 0.05f, 2));
								break;
							}
					case 3: if ((this.y/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1) {
								Chunk c3 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) + 1);					
								c3.setCreeperLevel(Base.round(c3.getCreeperLevel() + 0.2f, 2));
								c3.setPlagueLevel(Base.round(c3.getPlagueLevel() - 0.05f, 2));
								break;
							}
					case 4: if (this.y != 0) {
								Chunk c4 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) - 1);
								c4.setCreeperLevel(Base.round(c4.getCreeperLevel() + 0.2f, 2));
								c4.setPlagueLevel(Base.round(c4.getPlagueLevel() - 0.05f, 2));
								break;
							}
				}

				//this.setCreeperLevel(Base.round(this.getCreeperLevel() - 0.25f, 2));
				//this.setPlagueLevel(Base.round(this.getPlagueLevel() + 0.05f, 2));
			}
			toExpand = false;
		}
	
		if (this.creeper >= 0.8) {
			toExpand = true;
		}
		
		if (Base.getRandomFloatNumberInRange(0, 1) <= ((0.005f + (0.005f / this.creeper)) + (0.005f * this.plague))) {
			//this.setCreeperLevel(Base.round(this.getCreeperLevel() - (this.getCreeperLevel()*0.5f), 2));
			this.setCreeperLevel(0);
			
			toExpand = false;
			
			if (this.x != 0) {
				Chunk c1 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) - 1, (int)(this.y/Base.CHUNK_SIZE));			
				c1.setPlagueLevel(Base.round(c1.getPlagueLevel() + 0.1f, 2));
			}
			if ((this.x/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1 ) {
				Chunk c2 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) + 1, (int)(this.y/Base.CHUNK_SIZE));
				c2.setPlagueLevel(Base.round(c2.getPlagueLevel() + 0.1f, 2));
			}
			if ((this.y/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1) {
				Chunk c3 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) + 1);
				c3.setPlagueLevel(Base.round(c3.getPlagueLevel() + 0.1f, 2));
			}
			if (this.y != 0) {
				Chunk c4 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) - 1);
				c4.setPlagueLevel(Base.round(c4.getPlagueLevel() + 0.1f, 2));
			}
		}
		*/
		/*
		if (toExpand) {
			if (this.creeper <= 0.8f && Base.getRandomFloatNumberInRange(0, 1) <= 0.002f) this.creeper += 0.25f;			
			toExpand = false;
		}
		
		if (this.x != 0 && this.y != 0 && (this.x/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1 && (this.y/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1) {
			if (
					GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) - 1, (int)(this.y/Base.CHUNK_SIZE)).getCreeperLevel() >= (0.8f - this.creeper) ||
					GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) + 1, (int)(this.y/Base.CHUNK_SIZE)).getCreeperLevel() >= (0.8f - this.creeper) ||
					GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) + 1).getCreeperLevel() >= (0.8f - this.creeper) ||
					GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) - 1).getCreeperLevel() >= (0.8f - this.creeper)
			) {
				toExpand = true;				
			}
		}
		
		if (this.creeper >= 0.5) {
			if (Base.getRandomFloatNumberInRange(0, 1) <= 0.0001f) {
				this.creeper = 0;
				toExpand = false;
				System.out.println("ded");
			}
		}
		*/
		if (toExpand) {
			if (this.creeper <= 1f && Base.getRandomFloatNumberInRange(0, 1) <= (0.1f + (this.creeper*0.1f))) {
				switch (Base.getRandomIntNumberInRange(1, 4)) {		
					case 1: if (this.x != 0) {
								Chunk c1 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) - 1, (int)(this.y/Base.CHUNK_SIZE));
								c1.setCreeperLevel(Base.round(c1.getCreeperLevel() + 0.2f, 2));
								//c1.setPlagueLevel(Base.round(c1.getPlagueLevel() - 0.05f, 2));
								break;
							}
					case 2: if ((this.x/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1 ) {
								Chunk c2 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) + 1, (int)(this.y/Base.CHUNK_SIZE));
								c2.setCreeperLevel(Base.round(c2.getCreeperLevel() + 0.2f, 2));
								//c2.setPlagueLevel(Base.round(c2.getPlagueLevel() - 0.05f, 2));
								break;
							}
					case 3: if ((this.y/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1) {
								Chunk c3 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) + 1);					
								c3.setCreeperLevel(Base.round(c3.getCreeperLevel() + 0.2f, 2));
								//c3.setPlagueLevel(Base.round(c3.getPlagueLevel() - 0.05f, 2));
								break;
							}
					case 4: if (this.y != 0) {
								Chunk c4 = GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) - 1);
								c4.setCreeperLevel(Base.round(c4.getCreeperLevel() + 0.2f, 2));
								//c4.setPlagueLevel(Base.round(c4.getPlagueLevel() - 0.05f, 2));
								break;
							}
				}

				//this.setCreeperLevel(Base.round(this.getCreeperLevel() - 0.25f, 2));
				//this.setPlagueLevel(Base.round(this.getPlagueLevel() + 0.05f, 2));
			}
			toExpand = false;
		}
		
		if (this.x != 0 && this.y != 0 && (this.x/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1 && (this.y/Base.CHUNK_SIZE) < Base.CHUNK_AMOUNT - 1) {
			if (
					GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) - 1, (int)(this.y/Base.CHUNK_SIZE)).getCreeperLevel() >= (0.8f - this.creeper) ||
					GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE) + 1, (int)(this.y/Base.CHUNK_SIZE)).getCreeperLevel() >= (0.8f - this.creeper) ||
					GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) + 1).getCreeperLevel() >= (0.8f - this.creeper) ||
					GameScreen.chunks.getChunk((int)(this.x/Base.CHUNK_SIZE), (int)(this.y/Base.CHUNK_SIZE) - 1).getCreeperLevel() >= (0.8f - this.creeper)
			) {
				toExpand = true;				
			}
		}
		
		/*if (this.creeper >= 0.5) {
			if (Base.getRandomFloatNumberInRange(0, 1) <= 0.0001f) {
				this.creeper = 0;
				toExpand = false;
				System.out.println("ded");
			}
		}
		*/
		
	}
	
	public void draw(SpriteBatch batch, int column, int row) {
		/*if ((column % 2 == 0 || column == 0) && (row % 2 == 0 | row == 0)) {
			drawTile(SpriteLoader.tileAtlas.findRegion("seamlesssand32"), batch);
		}
		*/		
	
		if (coalOre != 0) {	
			TextureRegion desiredRegion = null;
			if (coalOre <= 0.25) {
				desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoallow");
				//drawTile(SpriteLoader.tileAtlas.findRegion("tiledCoallow"), batch);
			} else 
			if (coalOre <= 0.5) {		
				desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoallower");
				//drawTile(SpriteLoader.tileAtlas.findRegion("tiledCoallower"), batch);
			} else
			if (coalOre > 0.5) {
				desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoal");
				//drawTile(SpriteLoader.tileAtlas.findRegion("tiledCoal"), batch);
			}		
			
			boolean triangleDrawn = false;
			
			if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) + 1).getOreLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) + 1, Math.round(y/16)).getOreLevel() == 0) 
				{
					drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_LEFT, batch);
					triangleDrawn = true;
				}
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) + 1).getOreLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) - 1, Math.round(y/16)).getOreLevel() == 0) 
				{
					drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_RIGHT, batch);
					triangleDrawn = true;
				}			
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) - 1).getOreLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) + 1, Math.round(y/16)).getOreLevel() == 0) 
				{
					drawTileTriangle(desiredRegion, TriangleOrientation.TOP_LEFT, batch);
					triangleDrawn = true;
				}
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) - 1).getOreLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) - 1, Math.round(y/16)).getOreLevel() == 0) 
				{
					drawTileTriangle(desiredRegion, TriangleOrientation.TOP_RIGHT, batch);
					triangleDrawn = true;
				}	
			}
		
			if (!triangleDrawn) {
				drawTile(desiredRegion, batch);
			}
		} else
		if (ironOre != 0) {
			boolean triangleDrawn = false;
			
			if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) + 1).getOreLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) + 1, Math.round(y/16)).getOreLevel() == 0) 
				{
					drawTileTriangle(SpriteLoader.tileAtlas.findRegion("tiledIron"), TriangleOrientation.BOTTOM_LEFT, batch);
					triangleDrawn = true;
				}
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) + 1).getOreLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) - 1, Math.round(y/16)).getOreLevel() == 0) 
				{
					drawTileTriangle(SpriteLoader.tileAtlas.findRegion("tiledIron"), TriangleOrientation.BOTTOM_RIGHT, batch);
					triangleDrawn = true;
				}			
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) - 1).getOreLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) + 1, Math.round(y/16)).getOreLevel() == 0) 
				{
					drawTileTriangle(SpriteLoader.tileAtlas.findRegion("tiledIron"), TriangleOrientation.TOP_LEFT, batch);
					triangleDrawn = true;
				}
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) - 1).getOreLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) - 1, Math.round(y/16)).getOreLevel() == 0) 
				{
					drawTileTriangle(SpriteLoader.tileAtlas.findRegion("tiledIron"), TriangleOrientation.TOP_RIGHT, batch);
					triangleDrawn = true;
				}	
			}
		
			if (!triangleDrawn) {
				drawTile(SpriteLoader.tileAtlas.findRegion("tiledIron"), batch);
			}
		}
		
		if (creeper != 0) {
			boolean triangleDrawn = false;
			
			if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) + 1).getCreeperLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) + 1, Math.round(y/16)).getCreeperLevel() == 0) 
				{
					drawTileTriangle(SpriteLoader.tileAtlas.findRegion("corr16"), TriangleOrientation.BOTTOM_LEFT, batch);
					triangleDrawn = true;
				}
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) + 1).getCreeperLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) - 1, Math.round(y/16)).getCreeperLevel() == 0) 
				{
					drawTileTriangle(SpriteLoader.tileAtlas.findRegion("corr16"), TriangleOrientation.BOTTOM_RIGHT, batch);
					triangleDrawn = true;
				}			
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) - 1).getCreeperLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) + 1, Math.round(y/16)).getCreeperLevel() == 0) 
				{
					drawTileTriangle(SpriteLoader.tileAtlas.findRegion("corr16"), TriangleOrientation.TOP_LEFT, batch);
					triangleDrawn = true;
				}
				if (GameScreen.chunks.getChunk(Math.round(x/16), Math.round(y/16) - 1).getCreeperLevel() == 0 && 
					GameScreen.chunks.getChunk(Math.round(x/16) - 1, Math.round(y/16)).getCreeperLevel() == 0) 
				{
					drawTileTriangle(SpriteLoader.tileAtlas.findRegion("corr16"), TriangleOrientation.TOP_RIGHT, batch);
					triangleDrawn = true;
				}	
			}
		
			if (!triangleDrawn) {
				drawTile(SpriteLoader.tileAtlas.findRegion("corr16"), batch);
			}
		}
	}
	
	protected void drawTile(TextureRegion region, SpriteBatch batch) {
		final float u1 = region.getU();
		final float v1 = region.getV2();
		final float u2 = region.getU2();
		final float v2 = region.getV();
		
		final float x1 = x * 1;
		final float y1 = y * 1;
		
		final float x2 = x1 + region.getRegionWidth() * 1;
		final float y2 = y1 + region.getRegionHeight() * 1;
		
		vertices[Batch.X1] = x1;
		vertices[Batch.Y1] = y1;
		vertices[Batch.C1] = 0;
		vertices[Batch.U1] = u1;
		vertices[Batch.V1] = v1;

		vertices[Batch.X2] = x1;
		vertices[Batch.Y2] = y2;
		vertices[Batch.C2] = 0;
		vertices[Batch.U2] = u1;
		vertices[Batch.V2] = v2;

		vertices[Batch.X3] = x2;
		vertices[Batch.Y3] = y2;
		vertices[Batch.C3] = 0;
		vertices[Batch.U3] = u2;
		vertices[Batch.V3] = v2;

		vertices[Batch.X4] = x2;
		vertices[Batch.Y4] = y1;
		vertices[Batch.C4] = 0;
		vertices[Batch.U4] = u2;
		vertices[Batch.V4] = v1;

		batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
	}
	
	protected void drawTileTriangle(TextureRegion region, TriangleOrientation orientation, SpriteBatch batch) {
		final float u1 = region.getU();
		final float v1 = region.getV2();
		final float u2 = region.getU2();
		final float v2 = region.getV();
		
		final float x1 = x * 1;
		final float y1 = y * 1;
		
		final float x2 = x1 + region.getRegionWidth() * 1;
		final float y2 = y1 + region.getRegionHeight() * 1;
		
		switch (orientation) {
		case BOTTOM_LEFT: 
			vertices[Batch.X1] = x1;
			vertices[Batch.Y1] = y1;
			vertices[Batch.C1] = 0;
			vertices[Batch.U1] = u1;
			vertices[Batch.V1] = v1;

			vertices[Batch.X2] = x2;
			vertices[Batch.Y2] = y1;
			vertices[Batch.C2] = 0;
			vertices[Batch.U2] = u2;
			vertices[Batch.V2] = v1;

			vertices[Batch.X3] = x1;
			vertices[Batch.Y3] = y2;
			vertices[Batch.C3] = 0;
			vertices[Batch.U3] = u1;
			vertices[Batch.V3] = v2;
			
			vertices[Batch.X4] = x1;
			vertices[Batch.Y4] = y1;
			vertices[Batch.C4] = 0;
			vertices[Batch.U4] = u1;
			vertices[Batch.V4] = v1;
			break;
		case BOTTOM_RIGHT: 
			vertices[Batch.X1] = x1;
			vertices[Batch.Y1] = y1;
			vertices[Batch.C1] = 0;
			vertices[Batch.U1] = u1;
			vertices[Batch.V1] = v1;

			vertices[Batch.X2] = x2;
			vertices[Batch.Y2] = y1;
			vertices[Batch.C2] = 0;
			vertices[Batch.U2] = u2;
			vertices[Batch.V2] = v1;

			vertices[Batch.X3] = x2;
			vertices[Batch.Y3] = y2;
			vertices[Batch.C3] = 0;
			vertices[Batch.U3] = u2;
			vertices[Batch.V3] = v2;
			
			vertices[Batch.X4] = x1;
			vertices[Batch.Y4] = y1;
			vertices[Batch.C4] = 0;
			vertices[Batch.U4] = u1;
			vertices[Batch.V4] = v1;
			break;
		case TOP_LEFT: 
			vertices[Batch.X1] = x1;
			vertices[Batch.Y1] = y1;
			vertices[Batch.C1] = 0;
			vertices[Batch.U1] = u1;
			vertices[Batch.V1] = v1;

			vertices[Batch.X2] = x1;
			vertices[Batch.Y2] = y2;
			vertices[Batch.C2] = 0;
			vertices[Batch.U2] = u1;
			vertices[Batch.V2] = v2;

			vertices[Batch.X3] = x2;
			vertices[Batch.Y3] = y2;
			vertices[Batch.C3] = 0;
			vertices[Batch.U3] = u2;
			vertices[Batch.V3] = v2;
			
			vertices[Batch.X4] = x1;
			vertices[Batch.Y4] = y1;
			vertices[Batch.C4] = 0;
			vertices[Batch.U4] = u1;
			vertices[Batch.V4] = v1;
			break;
		case TOP_RIGHT: 
			vertices[Batch.X1] = x2;
			vertices[Batch.Y1] = y1;
			vertices[Batch.C1] = 0;
			vertices[Batch.U1] = u2;
			vertices[Batch.V1] = v1;

			vertices[Batch.X2] = x2;
			vertices[Batch.Y2] = y2;
			vertices[Batch.C2] = 0;
			vertices[Batch.U2] = u2;
			vertices[Batch.V2] = v2;

			vertices[Batch.X3] = x1;
			vertices[Batch.Y3] = y2;
			vertices[Batch.C3] = 0;
			vertices[Batch.U3] = u1;
			vertices[Batch.V3] = v2;
			
			vertices[Batch.X4] = x2;
			vertices[Batch.Y4] = y1;
			vertices[Batch.C4] = 0;
			vertices[Batch.U4] = u2;
			vertices[Batch.V4] = v1;
			break;
		}
		
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
	
	public void setCreeperLevel(float level) {
		creeper = level;
		if (creeper > 1) {
			creeper = 1;
		}
		if (creeper < 0) {
			creeper = 0;
		}
	}
	public float getCreeperLevel() {
		return creeper;
	}
	public void setPlagueLevel(float level) {
		plague = level;
		if (plague > 1f) {
			plague = 1f;
		}
		if (plague < 0f) {
			plague = 0f;
		}
	}
	public float getPlagueLevel() {
		return plague;
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
