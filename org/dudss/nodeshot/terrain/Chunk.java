package org.dudss.nodeshot.terrain;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks.OreType;
import org.dudss.nodeshot.terrain.datasubsets.Quad;
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
		BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT, END_LEFT, END_RIGHT, END_TOP, END_BOTTOM, SINGLE
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
	
	/**Returns a TextureRegion represeting this tile, if corr is true the method will return tile corruption representation**/
	public TextureRegion getAppropriateTexture(boolean corr) {
		if (corr) {
			if (creeper != 0) {
				boolean triangleDrawn = false;
				TextureRegion desiredRegion = SpriteLoader.tileAtlas.findRegion("corr32");
				if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {
					int ax = Math.round(x/Base.CHUNK_SIZE);
					int ay = Math.round(y/Base.CHUNK_SIZE);
					
					Chunk minusx = GameScreen.chunks.getChunk(ax - 1, ay);
					Chunk plusx = GameScreen.chunks.getChunk(ax + 1, ay);
					Chunk minusy = GameScreen.chunks.getChunk(ax, ay - 1);
					Chunk plusy = GameScreen.chunks.getChunk(ax, ay + 1);
					
					/*Chunk cornerTopLeft = GameScreen.chunks.getChunk(ax - 1, ay + 1);
					Chunk cornerTopRight = GameScreen.chunks.getChunk(ax + 1, ay + 1);
					Chunk corneBottomLeft = GameScreen.chunks.getChunk(ax - 1, ay - 1);
					Chunk cornerBottomRight = GameScreen.chunks.getChunk(ax + 1, ay - 1);
					*/
					
					if (plusy.getCreeperLevel() == 0 && 
						plusx.getCreeperLevel() == 0 &&
						minusx.getCreeperLevel() > 0 && 
						minusy.getCreeperLevel() > 0)
					{
						return SpriteLoader.tileAtlas.findRegion("corrTL");
					} else
					if (plusy.getCreeperLevel() == 0 && 
						minusx.getCreeperLevel() == 0 &&
						plusx.getCreeperLevel() > 0 &&
					    minusy.getCreeperLevel() > 0)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrTR");
					} else
					if (minusy.getCreeperLevel() == 0 && 
						plusx.getCreeperLevel() == 0 &&
					    plusy.getCreeperLevel() > 0 &&
					    minusx.getCreeperLevel() > 0)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrBL");
					} else
					if (minusy.getCreeperLevel() == 0 && 
						minusx.getCreeperLevel() == 0 && 
					    plusx.getCreeperLevel() > 0 &&
					    plusy.getCreeperLevel() > 0)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrBR");
					} else
					//Straight line borders
					if (plusy.getCreeperLevel() > 0 && 
						plusx.getCreeperLevel() > 0 && 
						minusy.getCreeperLevel() > 0 &&
						minusx.getCreeperLevel() == 0)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrSL");
					} else
					if (plusy.getCreeperLevel() > 0 && 
						minusx.getCreeperLevel() > 0 && 
						minusy.getCreeperLevel() > 0 &&
						plusx.getCreeperLevel() == 0)  
					{	
						return SpriteLoader.tileAtlas.findRegion("corrSR");
					} else
					if (plusy.getCreeperLevel() > 0 && 
						plusx.getCreeperLevel() > 0 && 
						minusx.getCreeperLevel() > 0 &&
						minusy.getCreeperLevel() == 0)  
					{	
						return SpriteLoader.tileAtlas.findRegion("corrST");
					} else
					if (minusy.getCreeperLevel() > 0 && 
						plusx.getCreeperLevel() > 0 && 
						minusx.getCreeperLevel() > 0 &&
						plusy.getCreeperLevel() == 0)  
					{
						return SpriteLoader.tileAtlas.findRegion("corrSB");	
					} else 
						
					//Tile ends (currently unused)	
					if (plusy.getCreeperLevel() == 0 && 
						plusx.getCreeperLevel() == 0 && 
						minusy.getCreeperLevel() == 0 &&
						minusx.getCreeperLevel() > 0)  
					{
						return desiredRegion;		
					} else
					if (plusy.getCreeperLevel() == 0 && 
						minusx.getCreeperLevel() == 0 && 
						minusy.getCreeperLevel() == 0 &&
						plusx.getCreeperLevel() > 0)  
					{	
						return desiredRegion;	
					} else
					if (plusy.getCreeperLevel() == 0 && 
						plusx.getCreeperLevel() == 0 && 
						minusx.getCreeperLevel() == 0 &&
						minusy.getCreeperLevel() > 0)  
					{	
						return desiredRegion;
					} else
					if (minusy.getCreeperLevel() == 0 && 
						plusx.getCreeperLevel() == 0 && 
						minusx.getCreeperLevel() == 0 &&
						plusy.getCreeperLevel() > 0)  
					{
						return desiredRegion;		
					} else 
					if (minusy.getCreeperLevel() == 0 && 
						plusx.getCreeperLevel() == 0 && 
						minusx.getCreeperLevel() == 0 &&
						plusy.getCreeperLevel() == 0)  
					{
						return desiredRegion;	
					}
				}
				return desiredRegion;
			}
			return SpriteLoader.tileAtlas.findRegion("transparent16");
		} else {
			if (coalOre != 0) {	
				TextureRegion desiredRegion = null;
				if (coalOre <= 0.25) {
					desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoallow");
				} else 
				if (coalOre <= 0.5) {		
					desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoallower");
				} else
				if (coalOre > 0.5) {
					desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledCoal");
				}		
				
				boolean triangleDrawn = false;
				
				if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {
					int ax = Math.round(x/Base.CHUNK_SIZE);
					int ay = Math.round(y/Base.CHUNK_SIZE);
					
					Chunk minusx = GameScreen.chunks.getChunk(ax - 1, ay);
					Chunk plusx = GameScreen.chunks.getChunk(ax + 1, ay);
					Chunk minusy = GameScreen.chunks.getChunk(ax, ay - 1);
					Chunk plusy = GameScreen.chunks.getChunk(ax, ay + 1);
					
					/*Chunk cornerTopLeft = GameScreen.chunks.getChunk(ax - 1, ay + 1);
					Chunk cornerTopRight = GameScreen.chunks.getChunk(ax + 1, ay + 1);
					Chunk corneBottomLeft = GameScreen.chunks.getChunk(ax - 1, ay - 1);
					Chunk cornerBottomRight = GameScreen.chunks.getChunk(ax + 1, ay - 1);
					*/
					
					if (plusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 &&
						minusx.getOreLevel() > 0 && 
						minusy.getOreLevel() > 0)
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_LEFT, batch, false);
						//triangleDrawn = true;
						return SpriteLoader.tileAtlas.findRegion("tiledCoalTL");
					} else
					if (plusy.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 &&
						plusx.getOreLevel() > 0 &&
					    minusy.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_RIGHT, batch, false);
						//triangleDrawn = true;
						return SpriteLoader.tileAtlas.findRegion("tiledCoalTR");
					} else
					if (minusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 &&
					    plusy.getOreLevel() > 0 &&
					    minusx.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.TOP_LEFT, batch, false);
						//triangleDrawn = true;
						return SpriteLoader.tileAtlas.findRegion("tiledCoalBL");
					} else
					if (minusy.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 && 
					    plusx.getOreLevel() > 0 &&
					    plusy.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.TOP_RIGHT, batch, false);
						//triangleDrawn = true;
						return SpriteLoader.tileAtlas.findRegion("tiledCoalBR");
					} else
					if (plusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 && 
						minusy.getOreLevel() == 0 &&
						minusx.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.END_RIGHT, batch, false);
						//triangleDrawn = true;	
						return SpriteLoader.tileAtlas.findRegion("tiledCoalEL");
					} else
					if (plusy.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 && 
						minusy.getOreLevel() == 0 &&
						plusx.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.END_LEFT, batch, false);
						//triangleDrawn = true;		
						return SpriteLoader.tileAtlas.findRegion("tiledCoalER");
					} else
					if (plusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 &&
						minusy.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.END_TOP, batch, false);
						//triangleDrawn = true;		
						return SpriteLoader.tileAtlas.findRegion("tiledCoalET");
					} else
					if (minusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 &&
						plusy.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.END_BOTTOM, batch, false);
						//triangleDrawn = true;			
						return SpriteLoader.tileAtlas.findRegion("tiledCoalEB");
					} else 
					if (minusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 &&
						plusy.getOreLevel() == 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.SINGLE, batch, false);
						//triangleDrawn = true;			
						return SpriteLoader.tileAtlas.findRegion("tiledCoalS");
					}
				}
			
				if (!triangleDrawn) {
					return desiredRegion;
					//drawTile(desiredRegion, batch, false);
				}
			} else
			if (ironOre != 0) {
				boolean triangleDrawn = false;
				TextureRegion desiredRegion = SpriteLoader.tileAtlas.findRegion("tiledIron");
				if (x >= 64 && y >= 64 && x < Base.WORLD_SIZE-64 && y < Base.WORLD_SIZE-64) {
					int ax = Math.round(x/Base.CHUNK_SIZE);
					int ay = Math.round(y/Base.CHUNK_SIZE);
					
					Chunk minusx = GameScreen.chunks.getChunk(ax - 1, ay);
					Chunk plusx = GameScreen.chunks.getChunk(ax + 1, ay);
					Chunk minusy = GameScreen.chunks.getChunk(ax, ay - 1);
					Chunk plusy = GameScreen.chunks.getChunk(ax, ay + 1);
					
					/*Chunk cornerTopLeft = GameScreen.chunks.getChunk(ax - 1, ay + 1);
					Chunk cornerTopRight = GameScreen.chunks.getChunk(ax + 1, ay + 1);
					Chunk corneBottomLeft = GameScreen.chunks.getChunk(ax - 1, ay - 1);
					Chunk cornerBottomRight = GameScreen.chunks.getChunk(ax + 1, ay - 1);
					*/
					
					if (plusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 &&
						minusx.getOreLevel() > 0 && 
						minusy.getOreLevel() > 0)
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_LEFT, batch, false);
						//triangleDrawn = true;
						return SpriteLoader.tileAtlas.findRegion("tiledIronTL");
					} else
					if (plusy.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 &&
						plusx.getOreLevel() > 0 &&
					    minusy.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.BOTTOM_RIGHT, batch, false);
						//triangleDrawn = true;
						return SpriteLoader.tileAtlas.findRegion("tiledIronTR");
					} else
					if (minusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 &&
					    plusy.getOreLevel() > 0 &&
					    minusx.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.TOP_LEFT, batch, false);
						//triangleDrawn = true;
						return SpriteLoader.tileAtlas.findRegion("tiledIronBL");
					} else
					if (minusy.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 && 
					    plusx.getOreLevel() > 0 &&
					    plusy.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.TOP_RIGHT, batch, false);
						//triangleDrawn = true;
						return SpriteLoader.tileAtlas.findRegion("tiledIronBR");
					} else
					if (plusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 && 
						minusy.getOreLevel() == 0 &&
						minusx.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.END_RIGHT, batch, false);
						//triangleDrawn = true;		
						return desiredRegion;
					} else
					if (plusy.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 && 
						minusy.getOreLevel() == 0 &&
						plusx.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.END_LEFT, batch, false);
						//triangleDrawn = true;		
						return desiredRegion;	
					} else
					if (plusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 &&
						minusy.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.END_TOP, batch, false);
						//triangleDrawn = true;	
						return desiredRegion;
					} else
					if (minusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 &&
						plusy.getOreLevel() > 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.END_BOTTOM, batch, false);
						//triangleDrawn = true;		
						return desiredRegion;
					} else 
					if (minusy.getOreLevel() == 0 && 
						plusx.getOreLevel() == 0 && 
						minusx.getOreLevel() == 0 &&
						plusy.getOreLevel() == 0)  
					{
						//drawTileTriangle(desiredRegion, TriangleOrientation.SINGLE, batch, false);
						//triangleDrawn = true;		
						return desiredRegion;
					}
				}
			
				if (!triangleDrawn) {
					//drawTile(SpriteLoader.tileAtlas.findRegion("tiledIron"), batch, false);
					return desiredRegion;
				}
			}
			//return default sand texture
			return SpriteLoader.tileAtlas.findRegion("seamlesssand16");
		}
	}
	
	public void setCoalLevel(float level) {
		if (level == -1) {
			coalOre = 0;
		} else {
			coalOre = level;
		}
	}
	public void setIronLevel(float level) {
		if (level == -1) {
			ironOre = 0;
		} else {
			ironOre = level;
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
