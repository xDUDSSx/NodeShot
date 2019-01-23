package org.dudss.nodeshot.entities;

import org.dudss.nodeshot.entities.effects.Explosion;

import com.badlogic.gdx.math.Vector2;

/**An entity representing a shell fired by {@link ArtilleryCannon}.*/
public class ArtilleryShell extends Bullet {
	/**
	 * Initialises a new artillery shell. All coordinates are in world-space.
	 * @param x The starting x coord.
	 * @param y The starting y coord.	
	 * @param target The target vector.
	 */
	
	public ArtilleryShell(float x, float y, Vector2 target, float speed, float inaccuracy) {
		super(x, y, target, speed, inaccuracy);
	}
	
	@Override
	protected void explode() {
		new Explosion(targetCoords.x, targetCoords.y);
	}
}
