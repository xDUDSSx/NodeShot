package org.dudss.nodeshot.buildings;

import java.util.HashMap;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.ArtilleryShell;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**A cannon firing {@link ArtilleryShell}s. What makes it special is its firing animation. This animation is unique for all 16 angles of rotation.*/
public class ArtilleryCannon extends Turret {
	
	boolean fireAnimPlaying = false;
	float fireAnimStartTime = 1;
	HashMap<Integer, Animation<TextureRegion>> fireAnims;	
	int[] rotations = new int[] {0, 22, 45, 67, 90, 112, 135, 157, 180, 202, 225, 247, 270, 292, 315, 337};
	int currentRotation = 0;	
	float animTime = 0;
	
	float inaccuracy = 64;
	float projectileSpeed = 14f;
	float archingFactor = 28;
	
	public ArtilleryCannon(float cx, float cy) {
		super(cx, cy);
		
		for (int i = 0; i < 50; i++) {
			this.storage.add(new StorableItem(ItemType.AMMO));
		}
		
		this.fogOfWarRadius = 40;
		inaccuracy = 64;
		projectileSpeed = 15f;
		rechargeRate = 220;
		rotationSpeed = 2.5f;
		ammoPerShot = 3;
		this.targetPollingRate = 90;
		
		this.minDiameter = 512;
		this.maxDiameter = 72*16*2;
		
		fireAnims = new HashMap<Integer, Animation<TextureRegion>>();
		for (int i = 0; i < 16; i++) {
			fireAnims.put(rotations[i], new Animation<TextureRegion>(0.042f, SpriteLoader.artilleryFiringFrames.get(rotations[i])));
			if (GameScreen.sfps > 0) {
				fireAnims.get(rotations[i]).setFrameDuration(((1000f/GameScreen.sfps)/1000f));
			}
			fireAnims.get(rotations[i]).setPlayMode(PlayMode.NORMAL);
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		if(GameScreen.sfps > 0) {
			fireAnims.get(currentRotation).setFrameDuration(((1000f/GameScreen.sfps)/1000f));
		}	
	}
	
	@Override
	public void draw(SpriteBatch batch) {			
		batch.setColor(1f, 1f, 1f, 1f);
		
		if (fireAnimPlaying && fireAnims.get(currentRotation).isAnimationFinished(animTime)) {
			animTime = 0;
			fireAnimStartTime = 1;
			fireAnimPlaying = false;
		}
		
		float modAng = (this.angle - 180) % 360;
		if (modAng < 0) {
			modAng += 360;
		}
		currentRotation = (int)(Math.round(modAng / 22.5f) * 22.5f);
		if (currentRotation == 360) {currentRotation = 0;}
		
		if (fireAnimPlaying) {			
			animTime = SimulationThread.stateTime - this.fireAnimStartTime;
			TextureRegion currentFrame = fireAnims.get(currentRotation).getKeyFrame(animTime, true);
			batch.draw(SpriteLoader.artilleryBase, x - width*4.36f*0.385f , y - height*4.36f*0.385f, width*4.36f, height*4.36f);
			batch.draw(currentFrame, x - width*5.37f*0.407f , y - height*5.37f*0.407f, width*5.37f, height*5.37f);
		} else {
			batch.draw(SpriteLoader.artilleryBase, x - width*4.36f*0.385f , y - height*4.36f*0.385f, width*4.36f, height*4.36f);
			batch.draw(SpriteLoader.artilleryFiringFrames.get(currentRotation)[0], x - width*5.37f*0.407f , y - height*5.37f*0.407f, width*5.37f, height*5.37f);
		}
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(SpriteLoader.artilleryBase, getPrefabVector(cx, cy, snap).x - width*4.36f*0.385f, getPrefabVector(cx, cy, snap).y - height*4.36f*0.385f, width*4.36f, height*4.36f);
		batch.end();		
	}
	
	@Override
	protected void fire() {
		ArtilleryShell b = new ArtilleryShell(this.cx, this.cy, target, projectileSpeed, inaccuracy, archingFactor);
		GameScreen.bulletHandler.addBullet(b);
		
		fireAnimPlaying = true;
		fireAnimStartTime = SimulationThread.stateTime;
		
		lastShot = SimulationThread.simTick;
		nextShot = SimulationThread.simTick + rechargeRate;
		lastTarget = target;						
		target = null;
		aimed = false;
	}
	
	/*@Override
	protected Vector2 findTarget() {
		Vector3 mouse = GameScreen.cam.unproject(new Vector3(GameScreen.mouseX, GameScreen.mouseY, 0));
	
		//Chunk targetChunk = GameScreen.chunks.getClosestCorruptionChunkToWorldSpace(this.cx, this.cy, minRadius, maxRadius, 0f);
		
		//if (targetChunk == null) {
			//return null;
		//}
		
		//target = new Vector2(targetChunk.getX() + (Base.CHUNK_SIZE/2), targetChunk.getY() + (Base.CHUNK_SIZE/2));		
		target = new Vector2(mouse.x, mouse.y);
		return target;
	}*/

	@Override
	protected Vector2 findTarget() {
		Vector2 target = null;
		
		//Checking for closest creeper generators first
		Chunk targetChunk = null;
		float closestDist = Float.MAX_VALUE;
		for (AbstractBuilding b : GameScreen.buildingManager.getAllGenerators()) {
			if (b != null) {
				float dist = (float) Math.hypot(x - b.getX(), y - b.getY());
				if (dist < closestDist && dist > minDiameter && dist < maxDiameter) {
					targetChunk = GameScreen.chunks.getChunkAtWorldSpace(b.getCX(), b.getCY());
					closestDist = dist;				
				}
			}			
		} 
		if (targetChunk == null) {
			targetChunk = GameScreen.chunks.getClosestCorruptionChunkToWorldSpace(this.cx, this.cy, minDiameter, maxDiameter, 0f);
			
			if (targetChunk == null) {
				return null;
			}
		}
		target = new Vector2(targetChunk.getX() + (Base.CHUNK_SIZE/2), targetChunk.getY() + (Base.CHUNK_SIZE/2));		
		return target;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.ARTILLERY_CANNON;
	}
}

