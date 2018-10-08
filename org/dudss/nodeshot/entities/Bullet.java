package org.dudss.nodeshot.entities;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends Sprite implements Entity {

	int id;
	
	Vector2 targetCoords;
	Vector2 trajectoryVector;
	
	float startX;
	float startY;
	
	float percentage;
	float speed;
	float damage;
	int radius;
	
	/**A bullet object that travels from point A (turret) to point B (target) at a speed and damages corruption around its target (point B)*/
	public Bullet(float x, float y, Vector2 target) {
		super();
		this.id = java.lang.System.identityHashCode(this);
		
		startX = x;
		startY = y;
		
		targetCoords = target;
		trajectoryVector = new Vector2(targetCoords.x - x, targetCoords.y - y);
		percentage = 0;
		speed = 3f;
		damage = 1.0f;
		radius = 1;
		
		this.set(new Sprite(SpriteLoader.bullet));
		this.setPosition(x, y);
	}
	
	/**Updating the bullets position or triggering its explosion*/
	public void update() {
		percentage += speed;
		if (percentage < 100) {
			Vector2 finalVector = new Vector2((float)(trajectoryVector.x * (0.01 * percentage)), (float)(trajectoryVector.y * (0.01 * percentage)));				
			this.setPosition((float)((startX) + finalVector.x), (float)((startY) + finalVector.y));			
		} else {
			explode();
			GameScreen.bulletHandler.removeBullet(this);
		}
	}
	
	/**Called when bullet reaches its target, damages corruption*/
	protected void explode() {
		for (int y = -3; y < 3; y++) {
			for (int x = -3; x < 3; x++) {
				Chunk current = GameScreen.chunks.getChunk((int)(targetCoords.x/Base.CHUNK_SIZE) + x, (int)(targetCoords.y/Base.CHUNK_SIZE) + y); if (current != null) {current.setCreeperLevel(0);}
			}
		}		
	}
	
	@Override
	public int getID() {
		return id; 
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public EntityType getType() {
		return EntityType.BULLET;
	}
}
