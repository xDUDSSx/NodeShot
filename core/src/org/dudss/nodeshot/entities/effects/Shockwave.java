package org.dudss.nodeshot.entities.effects;

import org.dudss.nodeshot.entities.VisualEffect;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.Shaders;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**A shock-wave effect that distorts space and radially expands.
 * Needs to be rendered inside of a gray displacement screen buffer that is rendered using the {@link Shaders#waveShader}.
 * @see GameScreen#displacementBuffer
 */
public class Shockwave extends VisualEffect {
	
	Sprite displacementSprite;
	
	float tick = 0;
	float duration;
	float fadeOutStart;
	float opacity = 1f;
	
	float expansionRate;
	float fadeOutRate;
	
	float size;	
	float initialSize;
	float finalSize;
	
	/**Create and play a shock-wave effect at world space coordinates.
	 * @param cx Origin x coordinate.
	 * @param cy Origin y coordinate.
	 * @param initialSize Initial size of the shock-wave in world space units.
	 * @param finalSize Final size of the shock-wave in world space units. This is the size of the shock-wave after the specified duration.
	 * @param duration Duration of the shock-wave in simulation ticks.
	 * @param fadeOutStart Should be smaller than duration. Number of ticks after which the shock-wave will start fading out.
	 */
	public Shockwave(float cx, float cy, float initialSize, float finalSize, float duration, float fadeOutStart) {
		super();
		displacementSprite = new Sprite(SpriteLoader.shockwave);
		this.duration = duration;
		this.fadeOutStart = fadeOutStart;
		this.initialSize = initialSize;
		this.finalSize = finalSize;
		
		this.expansionRate = (finalSize - initialSize) / duration;
		this.fadeOutRate = 1f / (duration - fadeOutStart);
		this.size = initialSize;
		
		this.x = cx - size/2f;
		this.y = cy - size/2f;
		
		GameScreen.effectManager.addDisplacementEffect(this);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		displacementSprite.setSize(size, size);
		displacementSprite.setPosition(x, y);
		displacementSprite.setColor(1f, 1f, 1f, opacity);
		displacementSprite.draw(batch); 		
		displacementSprite.setColor(1f, 1f, 1f, 1f);
	}

	@Override
	public void update() {
		size = size += expansionRate;
		this.x -= expansionRate / 2f;
		this.y -= expansionRate / 2f;
		tick++;
		
		//System.out.println("shockwave update: " + " opacity: " + opacity + " duration: " + duration);
		
		if (tick >= fadeOutStart) {
			if (opacity - fadeOutRate > 0f) opacity -= fadeOutRate;	
			else opacity = 0f;
		}
		
		if (size >= finalSize) {
			GameScreen.effectManager.removeDisplacementEffect(this);
			//System.out.println("removing shockwave effect at size: " + size);
		}
	}

}
