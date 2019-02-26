package org.dudss.nodeshot.entities;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.effects.Shockwave;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**A bullet object that travels from point A ({@linkplain Turret}) to point B (target) at a speed and damages corruption around its target (point B)*/
public class Bullet extends Sprite implements Entity {

	int id;
	
	Vector2 targetCoords;
	Vector2 trajectoryVector;
	
	float startX;
	float startY;
	
	double rotation;
	
	float percentage = 0;
	float effectiveSpeed;
	
	/**Damage of the bullet.*/
	float damage = 10f;
	/**Whether the bullet arcs and how much.*/
	float archingFactor;
	/**Diameter of the area where damage is dealt.*/
	float diameter = 15f;
	
	/**A bullet object that travels from point A (turret) to point B (target) at a speed and damages corruption around its target (point B)
	 * @param x Starting x coordinate.
	 * @param y Starting y coordinate.
	 * @param target Coordinates of the target.
	 * @param inaccuracy A radius of inaccuracy in world-units.
	 * @param archingFactor How much the bullet arches in the air.
	 * */
	public Bullet(float x, float y, Vector2 target, float speed, float inaccuracy, float archingFactor) {
		super();
		this.id = java.lang.System.identityHashCode(this);
		
		startX = x;
		startY = y;
		
		target.x += Base.getRandomFloatNumberInRange(0f, inaccuracy) - inaccuracy/2f;
		target.y += Base.getRandomFloatNumberInRange(0f, inaccuracy) - inaccuracy/2f;
		
		this.archingFactor = archingFactor;
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
			double aimLenght = Math.hypot(trajectoryVector.x, trajectoryVector.y);
			double alpha = Math.asin(trajectoryVector.y/aimLenght);
			
			float perFac = 0;
			if (percentage <= 50) {
				perFac = percentage/100f;
				perFac = Base.range(perFac, 0, 0.5f, 0, 1);
			} else {
				perFac = percentage/100f;
				perFac = 1f - Base.range(perFac, 0.5f, 1, 0, 1);
			}					
			float appliedOffset = Interpolation.pow2Out.apply(perFac);
			//System.out.println("fac:" + perFac);
			//System.out.println(appliedOffset);
			this.setPosition((float)((startX - 4) + finalVector.x), (float)((startY - 2) + finalVector.y + archingFactor*appliedOffset));	
			
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
		GameScreen.terrainEditor.creeperExplosion(new Vector3(targetCoords.x, targetCoords.y, 0), this.diameter, this.damage);
		new Shockwave(targetCoords.x, targetCoords.y, 1, 200, 18, 1);		
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
