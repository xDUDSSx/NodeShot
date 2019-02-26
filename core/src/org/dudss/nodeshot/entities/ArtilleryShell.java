package org.dudss.nodeshot.entities;

import org.dudss.nodeshot.buildings.ArtilleryCannon;
import org.dudss.nodeshot.entities.effects.Explosion;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**An entity representing a shell fired by {@link ArtilleryCannon}.*/
public class ArtilleryShell extends Bullet {
	/**
	 * Initialises a new artillery shell. All coordinates are in world-space.
	 * @param x Starting x coordinate.
	 * @param y Starting y coordinate.
	 * @param target Coordinates of the target.
	 * @param inaccuracy A radius of inaccuracy in world-units.
	 * @param archingFactor How much the shell arches in the air.
	 * */
	public ArtilleryShell(float x, float y, Vector2 target, float speed, float inaccuracy, float archingFactor) {
		super(x, y, target, speed, inaccuracy, archingFactor);
		this.damage = 30;
		this.diameter = 9;
	}
	
	@Override
	protected void explode() {
		GameScreen.terrainEditor.creeperExplosion(new Vector3(targetCoords.x, targetCoords.y, 0), this.diameter, this.damage);
		new Explosion(targetCoords.x, targetCoords.y);
	}
}
