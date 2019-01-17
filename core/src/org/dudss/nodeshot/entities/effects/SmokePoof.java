package org.dudss.nodeshot.entities.effects;

import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.VisualEffect;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**A wrapper class for a smoke puff {@link ParticleEffect}.*/
public class SmokePoof extends VisualEffect {
	ParticleEffect p;
	
	int layer = 1;
	
	float startDur;
	float endDur;
	float duration = 0;
	
	int defaultDuration = 800;
	int defaultRate = 30;
	
	public SmokePoof(float cx, float cy) {
		super();
		p = new ParticleEffect();
		p.load(Gdx.files.local("/textureData/smokePuff/explosionSmokePuff.p"), SpriteLoader.smokePuffAtlas);
		p.start();
		p.setPosition(cx, cy);
		p.scaleEffect(0.5f);
		startDur = System.currentTimeMillis();
		if(GameScreen.sfps > 0) {
			if (GameScreen.sfps > defaultRate) {
				duration = ((float)defaultRate/(float)GameScreen.sfps)*(float)defaultDuration;
			} else {
				duration = ((float)GameScreen.sfps/(float)defaultRate)*(float)defaultDuration;								
			}
			endDur = System.currentTimeMillis() + duration;
			System.out.println("dur: " + duration);
			p.setDuration((int)duration);		
		}	
		
		GameScreen.effectManager.addRegularEffect(this, layer);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		p.draw(batch);
	}

	@Override
	public void update() {
		p.update(SimulationThread.getDelta());
		if (System.currentTimeMillis() > endDur) {
			GameScreen.effectManager.removeRegularEffect(this, layer);
		}
	}
}
