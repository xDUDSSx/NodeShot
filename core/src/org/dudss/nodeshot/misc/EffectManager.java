package org.dudss.nodeshot.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.VisualEffect;
import org.dudss.nodeshot.utils.Shaders;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**A manager that manages 3 separate visual effect types.
 * 1. Displacement effects - space distortion effects that are using a custom shader ({@link Shaders#waveShader}.
 * 2. Regular effects - custom effects that are managed by my own code.
 * 3. LibGDX particle effects - Particle effects handled by libGDX {@link ParticleEffect}.
 * */
public class EffectManager {
	List<VisualEffect> allEffects;
	List<VisualEffect> displacementEffects;
	//List<VisualEffect> regularEffects;
	
	Map<Integer, List<VisualEffect>> regularEffects;
	
	int nOfLayers = 3;
	
	public EffectManager() {
		displacementEffects = new CopyOnWriteArrayList<VisualEffect>();
		regularEffects = new ConcurrentHashMap<Integer, List<VisualEffect>>();
		for (int i = 0; i < nOfLayers; i++) {
			regularEffects.put(i, new CopyOnWriteArrayList<VisualEffect>());
		}		
		allEffects =  new CopyOnWriteArrayList<VisualEffect>();
	}
	
	public void drawDisplacementEffects(SpriteBatch batch) {
		batch.begin();
		for (VisualEffect v : displacementEffects) {
			v.draw(batch);
		}
		batch.end();
	}
	
	public void drawRegularEffects(SpriteBatch batch) {
		batch.begin();
		for (int i = 0; i < this.nOfLayers; i++) {
			for (VisualEffect v : regularEffects.get(i)) {
				v.draw(batch);
			}
		}
		batch.end();
	}
	
	public void updateAllEffects() {
		for (VisualEffect v : allEffects) {
			v.update();
		}
	}
	
	public void addRegularEffect(VisualEffect e, int layer) {
		if (!(layer >= 0 && layer < nOfLayers)) {
			BaseClass.logger.info("Invalid effect layer addition!"); 
			return;
		}
		this.regularEffects.get(layer).add(e);
		this.allEffects.add(e);
	}
	
	public void removeRegularEffect(VisualEffect e, int layer) {
		if (!(layer >= 0 && layer < nOfLayers)) {
			BaseClass.logger.info("Invalid effect layer removal!"); 
			return;
		}
		this.regularEffects.get(layer).remove(e);
		this.allEffects.remove(e);
	}
		
	public void addDisplacementEffect(VisualEffect e) {
		this.displacementEffects.add(e);
		this.allEffects.add(e);
	}
	
	public void removeDisplacementEffect(VisualEffect e) {
		this.displacementEffects.remove(e);
		this.allEffects.remove(e);
	}
}
