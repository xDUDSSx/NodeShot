package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.nodes.IONode;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**An {@link AbstractIOPort} that stores {@link Package}s as {@link StorableItem}s to {@link AbstractStorage}s.*/
public class Importer extends AbstractIOPort {
	
	static float width = Base.CHUNK_SIZE*1, height = Base.CHUNK_SIZE*1;
	Sprite sprite;
	
	//TODO: ROTATION !! - as of right now, the importer is oriented up the y axis;
	//TODO: implement IONode, remove Input/Output nodes, join them together
	public Importer(float cx, float cy) {
		super(cx, cy, width, height);	
		sprite = new Sprite(SpriteLoader.importerTop);
	}
	
	@Override
	public boolean alert(StorableItem p) {
		if (this.nextOperation < SimulationThread.simTick) {
			if (canStore(p)) {
				((AlertableBuilding) buildingChunk.getBuilding()).alert(p);
				this.nextOperation = SimulationThread.simTick + ioSpeed;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canStore(StorableItem p) {
		if (buildingChunk.getBuilding() instanceof AlertableBuilding) {
			return ((AlertableBuilding) buildingChunk.getBuilding()).canStore(p);
		}
		
		return false;
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		sprite.setOrigin(Base.CHUNK_SIZE/2, Base.CHUNK_SIZE + Base.CHUNK_SIZE/2);
		sprite.setRotation(this.spriteRotation);
		sprite.setSize(width, height + Base.CHUNK_SIZE);
		sprite.setPosition(x, y - Base.CHUNK_SIZE);
		sprite.draw(batch);		
		//batch.draw(sprite, x, y - Base.CHUNK_SIZE, width, height + Base.CHUNK_SIZE);
		//input.draw(batch);
		batch.end();
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		sprite.setOrigin(Base.CHUNK_SIZE/2, Base.CHUNK_SIZE + Base.CHUNK_SIZE/2);
		sprite.setRotation(this.spriteRotation);
		sprite.setSize(width, height + Base.CHUNK_SIZE);
		sprite.setPosition(getPrefabX(cx, snap), getPrefabY(cy, snap)  - Base.CHUNK_SIZE);
		sprite.draw(batch);		
		//batch.draw(sprite, getPrefabX(cx, snap), getPrefabY(cy, snap)  - Base.CHUNK_SIZE, width, height + Base.CHUNK_SIZE);
		batch.end();		
	}

	@Override
	public void build() {
		this.input = new IONode(x + Base.CHUNK_SIZE/2, y + Base.CHUNK_SIZE/2, Base.RADIUS, this);	
		input.setInputSprite();
		GameScreen.nodelist.add(input);
		
		importerChunk = GameScreen.chunks.getChunkAtWorldSpace(x, y);
		
		switch(Math.abs(this.spriteRotation)) {
			case 0: buildingChunk = GameScreen.chunks.getChunkAtTileSpace(importerChunk.getAX(), importerChunk.getAY() - 1); break;
			case 90: buildingChunk = GameScreen.chunks.getChunkAtTileSpace(importerChunk.getAX() + 1, importerChunk.getAY()); break;
			case 180: buildingChunk = GameScreen.chunks.getChunkAtTileSpace(importerChunk.getAX(), importerChunk.getAY() + 1); break;
			case 270: buildingChunk = GameScreen.chunks.getChunkAtTileSpace(importerChunk.getAX() - 1, importerChunk.getAY()); break;
		}		
		
		if (buildingChunk.getBuilding() != null) {
			if (buildingChunk.getBuilding() instanceof AlertableBuilding) {
				this.setAccepted(((AlertableBuilding) buildingChunk.getBuilding()).getAccepted());
			} else {
				BaseClass.logger.warning("Importer assigned building is not alertable!");
			}
		} else {
			BaseClass.logger.warning("IOPort assigned building is null!");
		}
		
		GameScreen.buildingHandler.addMisc(this);
	}
}
