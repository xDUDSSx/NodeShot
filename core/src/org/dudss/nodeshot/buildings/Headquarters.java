package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.nodes.BuildingNode;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Headquarters extends AbstractBuilding {

	static float width = Base.CHUNK_SIZE*3, height = Base.CHUNK_SIZE*3;
	
	Animation<TextureRegion> hqAnimation;
	Animation<TextureRegion> hqOutlinedAnimation;
	
	BuildingNode hqNode;
	
	public Headquarters(float cx, float cy) {
		super(cx, cy, width, height);
		hqAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.hqanimFrames);	
		hqOutlinedAnimation = new Animation<TextureRegion>(0.042f, SpriteLoader.hqanimoutlineFrames);	
	}

	@Override
	public void update() {
		
	}

	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		if (outlined) {
			TextureRegion currentFrame = hqOutlinedAnimation.getKeyFrame(GameScreen.stateTime, true);
			batch.draw(currentFrame, x, y, width, height);
		} else {
			TextureRegion currentFrame = hqAnimation.getKeyFrame(GameScreen.stateTime, true);
			batch.draw(currentFrame, x, y, width, height);
		}		
		batch.end();
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		TextureRegion currentFrame = hqAnimation.getKeyFrame(GameScreen.stateTime, true);
		
		float prefX;
		float prefY;
		
		if (snap) {
			float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
			float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
			
			prefX = nx - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
			prefY = ny - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;	
		} else {
			prefX = cx - (width/2);
			prefY = cy - (height/2);
		}
		
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(currentFrame, prefX, prefY, width, height);
		batch.end();	
	}

	@Override
	public void alert(Package p) {
		// TODO Auto-generated method stub
	}

	@Override
	public void build() {
		hqNode = new BuildingNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		//hqNode.add();
		GameScreen.buildingHandler.addBuilding(this);
		
		updateFogOfWar(true);
	}
	
	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeBuilding(this);
		hqNode.remove();
		
		updateFogOfWar(false);	
	}
}
