package org.dudss.nodeshot.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.dudss.nodeshot.SimulationThread;

/**An {@link Entity} representing an in-game effect.*/
public abstract class VisualEffect implements Entity {
	
	public float x = 0;
	public float y = 0;

	public int id;
	
	/**An {@link Entity} representing an in-game effect.*/
	public VisualEffect() {
		this.id = System.identityHashCode(this);
	}
	
	/**Called on the rendering thread every frame.*/
	abstract public void draw(SpriteBatch batch);	
	/**Called on the {@link SimulationThread} every simulation update.*/
	abstract public void update();
	
	@Override
	public int getID() {
		return id;
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@Override
	public EntityType getType() {
		return EntityType.VISUAL_EFFECT;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}
	
}
