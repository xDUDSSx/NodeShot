package org.dudss.nodeshot.entities;

import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.Section;
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
	
	float percentage = 0;
	float speed = 5f;
	float effectiveSpeed;
	float damage = 1f;
	
	float radius = 6.5f;
	
	/**A bullet object that travels from point A (turret) to point B (target) at a speed and damages corruption around its target (point B)
	 * @param x Starting x coordinate.
	 * @param y Starting y coordinate.
	 * @param target Coordinates of the target.
	 * */
	public Bullet(float x, float y, Vector2 target) {
		super();
		this.id = java.lang.System.identityHashCode(this);
		
		startX = x;
		startY = y;
		
		targetCoords = target;
		trajectoryVector = new Vector2(targetCoords.x - x, targetCoords.y - y);
		effectiveSpeed = speed * (float)(100/Math.hypot(trajectoryVector.x, trajectoryVector.y));
		
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
		for (Chunk c : GameScreen.chunks.getChunksAroundWorldSpacePoint(targetCoords.x, targetCoords.y, radius)) {
			c.setCreeperLevel(c.getCreeperLevel() - damage);
		}
		for (Section s : GameScreen.chunks.getSectionsAroundWorldSpacePoint(targetCoords.x, targetCoords.y)) {
			GameScreen.chunks.updateSectionMesh(s, true);
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
