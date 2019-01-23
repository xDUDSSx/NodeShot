package org.dudss.nodeshot.entities.effects;

import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.VisualEffect;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**A single explosion animated sprite*/
public class Explosion extends VisualEffect {

	Animation<TextureRegion> explosionAnimation;
	float startTime;
	
	int layer = 0;
	float speedFac = 0.5f;
	
	/**A single explosion animated sprite*/
	public Explosion(float x, float y) {
		super();
		explosionAnimation = new Animation<TextureRegion>(0.021f, SpriteLoader.explosionFrames);
		explosionAnimation.setPlayMode(PlayMode.NORMAL);
		if(GameScreen.sfps > 0) {
			explosionAnimation.setFrameDuration(((1000f/GameScreen.sfps)/1000f)*speedFac);
		}	
		
		this.x = x;
		this.y = y;
		
		startTime = SimulationThread.stateTime;
		
		GameScreen.effectManager.addRegularEffect(this, layer);
		
		new Shockwave(x, y, 1, 400, 30, 10);		
		new SmokePoof(x, y);	
		GameScreen.terrainEditor.explosion(new Vector3(x, y, 0), 11);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		TextureRegion currentFrame = explosionAnimation.getKeyFrame(SimulationThread.stateTime - startTime, false);
		batch.draw(currentFrame, x - 100f/2, y - 135f/2 + 0.3f*135f, 100f, 135f);
	}

	@Override
	public void update() {
		if(GameScreen.sfps > 0) {
			explosionAnimation.setFrameDuration(((1000f/GameScreen.sfps)/1000f)*speedFac);
		}	
		if (explosionAnimation.isAnimationFinished(SimulationThread.stateTime - startTime)) {
			GameScreen.effectManager.removeRegularEffect(this, layer);
		}
	}
	
}
