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
	
	double rotation;
	
	float percentage;
	float speed;
	float effectiveSpeed;
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
		speed = 5f;
		effectiveSpeed = speed * (float)(100/Math.hypot(trajectoryVector.x, trajectoryVector.y));
		damage = 1.0f;
		radius = 1;
		
		this.set(new Sprite(SpriteLoader.bullet));
		this.setOrigin(4, 2);
		this.setPosition(x, y);
	}
	
	/**Updating the bullets position or triggering its explosion*/
	public void update() {
		percentage += effectiveSpeed;
		if (percentage < 100) {
			Vector2 finalVector = new Vector2((float)(trajectoryVector.x * (0.01 * percentage)), (float)(trajectoryVector.y * (0.01 * percentage)));				
			this.setPosition((float)((startX - 4) + finalVector.x), (float)((startY - 2) + finalVector.y));			
			double aimLenght = Math.hypot(trajectoryVector.x, trajectoryVector.y);
			double alpha = Math.asin(trajectoryVector.y/aimLenght);
			
			if (targetCoords.x <= startX) {
				rotation = 360 - Math.toDegrees(alpha);
			} else {
				rotation = 180 + Math.toDegrees(alpha);
			}
			this.setRotation((float)rotation);
		} else {
			explode();
			GameScreen.bulletHandler.removeBullet(this);
		}
	}
	
	/**Called when bullet reaches its target, damages corruption*/
	protected void explode() {
		for (int y = -3; y < 3; y++) {
			for (int x = -3; x < 3; x++) {
				Chunk current = GameScreen.chunks.getChunk((int)(targetCoords.x/Base.CHUNK_SIZE) + x, (int)(targetCoords.y/Base.CHUNK_SIZE) + y); if (current != null) {current.setCreeperLevel(current.getCreeperLevel() - 0.4f);}
			}
		}		
		int sx = (int)(targetCoords.x / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
		int sy = (int)(targetCoords.y / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
		GameScreen.chunks.updateSectionMesh(GameScreen.chunks.sections[sx][sy], true, -1);
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
