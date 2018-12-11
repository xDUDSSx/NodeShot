package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.entities.nodes.InputNode;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Importer extends AbstractIOPort {
	
	static float width = Base.CHUNK_SIZE*1, height = Base.CHUNK_SIZE*1;
	
	//TODO: ROTATION !! - as of right now, the importer is oriented up the y axis;
	//TODO: implement IONode, remove Input/Output nodes, join them together
	public Importer(float cx, float cy) {
		super(cx, cy, width, height);	
	}

	@Override
	public void update() {
		super.update();
		
		if (assignedStorage != null) {
			if (this.storage.size() > 0 && assignedStorage instanceof AbstractStorage) {				
				if (assignedStorage.alert(this.storage.get(0))) this.storage.remove(0);
			}
		}
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		batch.draw(SpriteLoader.importerTop, x, y - Base.CHUNK_SIZE, width, height + Base.CHUNK_SIZE);
		//input.draw(batch);
		batch.end();
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(SpriteLoader.importerTop, getPrefabX(cx, snap), getPrefabY(cy, snap)  - Base.CHUNK_SIZE, width, height + Base.CHUNK_SIZE);
		batch.end();		
	}

	@Override
	public void build() {
		this.input = new InputNode(x + Base.CHUNK_SIZE/2, y + Base.CHUNK_SIZE/2, Base.RADIUS, this);	
		GameScreen.nodelist.add(input);
		
		importerChunk = GameScreen.chunks.getChunkAtWorldSpace(x, y);
		buildingChunk = GameScreen.chunks.getChunkAtTileSpace(importerChunk.getAX(), importerChunk.getAY() - 1);		
		
		assignedStorage = (AbstractStorage) buildingChunk.getBuilding();
		if (assignedStorage == null) {
			BaseClass.logger.warning("IOPort assigned building is null!");
		} else
		if (!(assignedStorage instanceof AbstractStorage)) {
			BaseClass.logger.warning("Importer assigned building is not a storage!");
		}
		this.setAccepted(assignedStorage.getAccepted());
		
		GameScreen.buildingHandler.addMisc(this);
	}
}
