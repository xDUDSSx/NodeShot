package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.effects.Explosion;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter;

/**The starting building*/
public class Headquarters extends AbstractBuilding {

	static float width = Base.CHUNK_SIZE*4, height = Base.CHUNK_SIZE*4;
	
	Animation<TextureRegion> hqAnimation;
	Animation<TextureRegion> hqOutlinedAnimation;
	
	public Headquarters(float cx, float cy) {
		super(cx, cy, width, height);
		hqAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.hqanimFrames);	
		hqOutlinedAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.hqanimoutlineFrames);	
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.setColor(1f, 1f, 1f, 1f);		
		TextureRegion currentFrame = hqAnimation.getKeyFrame(GameScreen.stateTime, true);
		batch.draw(currentFrame, x, y, width, height);
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		TextureRegion currentFrame = hqAnimation.getKeyFrame(GameScreen.stateTime, true);
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(currentFrame, getPrefabVector(cx, cy, snap).x, getPrefabVector(cx, cy, snap).y, width, height);
		batch.end();	
	}
	
	@Override
	public void explode() {
		super.explode();
		Dialogs.showOKDialog(GameScreen.stage, "Game over", "Your HQ was destroyed! You lose!");
	}
	
	@Override
	public EntityType getType() {
		return EntityType.HQ;
	}
}
