package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.buildings.AbstractBuilding.BuildingType;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.entities.nodes.IONode;
import org.dudss.nodeshot.items.Item;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.misc.IndefinitePathHandler;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**An {@link AbstractIOPort} that takes {@link StorableItem}s out of {@link AbstractStorage}s.*/
public class Exporter extends AbstractIOPort {
	
	static float width = Base.CHUNK_SIZE*1, height = Base.CHUNK_SIZE*1;
	Sprite sprite;
	
	public Exporter(float cx, float cy) {
		super(cx, cy, width, height);
		sprite = new Sprite(SpriteLoader.exporterTop);
	}
	
	@Override
	public void update() {
		super.update();
		if (this.nextOperation < SimulationThread.simTick) {
			AbstractBuilding b = buildingChunk.getBuilding();
			if (b != null) {
				if (b instanceof AbstractStorage) {							
					exportAnItem(b);	
				}
			}
		}
	}
	
	/**Removes a {@link StorableItem} from the assigned {@link AbstractStorage} and initialises a new {@link Package} and if a {@link Conveyor} is connected,
	 * this {@linkplain Item} is sent along it using the {@link IndefinitePathHandler}.
	 * When a {@link AbstractIOStorage} is assigned, it will first export items from its secondary processed storage.
	 * @param b The {@link AbstractBuilding} we are exporting from.
	 * */
	public void exportAnItem(AbstractBuilding b) {
		if (ioNode.getAllConnectedNodes().size() > 0 || ioNode.getConnectors().size() > 0) {
			StorableItem exportItem;
			if (b instanceof AbstractIOStorage) {			
				AbstractIOStorage ioStorage = (AbstractIOStorage) b;
				if (ioStorage.getProcessedStorage().size() > 0) {
					exportItem = ioStorage.getProcessedStorage().get(0);
				} else {
					return; //Storage is empty
				}
			} else {
				AbstractStorage storage = (AbstractStorage) b;
				if (storage.getStoredItems().size() > 0) {
					exportItem = storage.getStoredItems().get(0);
				} else {
					return; //Storage is empty
				}
			}
			
			if (ioNode.canSendPackage()) {
				Package exportPackage = exportItem.getPackage();
				if (exportPackage.notSet == true) {
					exportPackage.setParams(ioNode, null);
				}
				this.ioNode.sendIOPackage(exportPackage);
				if (b instanceof AbstractIOStorage) {
					((AbstractIOStorage)b).getProcessedStorage().remove(0);
				} else
				if (b instanceof AbstractStorage) {
					((AbstractStorage)b).getStoredItems().remove(0);
				}
				
				this.nextOperation = SimulationThread.simTick + ioSpeed;
			}
		}
	}

	@Override
	public boolean alert(StorableItem p) {
		return false;
	}

	@Override
	public boolean canStore(StorableItem p) {
		return false;
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.setColor(1f, 1f, 1f, 1f);		
		sprite.setOrigin(Base.CHUNK_SIZE/2, Base.CHUNK_SIZE + Base.CHUNK_SIZE/2);
		sprite.setRotation(this.spriteRotation);
		sprite.setSize(width, height + Base.CHUNK_SIZE);
		sprite.setPosition(x, y - Base.CHUNK_SIZE);
		sprite.draw(batch);			
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);	
		sprite.setOrigin(Base.CHUNK_SIZE/2, Base.CHUNK_SIZE + Base.CHUNK_SIZE/2);
		sprite.setRotation(this.spriteRotation);
		sprite.setSize(width, height + Base.CHUNK_SIZE);
		sprite.setPosition(getPrefabVector(cx, cy, snap).x, getPrefabVector(cx, cy, snap).y - Base.CHUNK_SIZE);
		sprite.draw(batch);		
		//batch.draw(sprite, getPrefabX(cx, snap), getPrefabY(cy, snap)  - Base.CHUNK_SIZE, width, height + Base.CHUNK_SIZE);
		batch.end();		
		
	}

	@Override
	public void build() {
		super.build();		
		ioNode.setOutputSprite();
		
		importerChunk = GameScreen.chunks.getChunkAtWorldSpace(x, y);
		
		switch(Math.abs(this.spriteRotation)) {
			case 0: buildingChunk = GameScreen.chunks.getChunkAtTileSpace(importerChunk.getAX(), importerChunk.getAY() - 1); break;
			case 90: buildingChunk = GameScreen.chunks.getChunkAtTileSpace(importerChunk.getAX() + 1, importerChunk.getAY()); break;
			case 180: buildingChunk = GameScreen.chunks.getChunkAtTileSpace(importerChunk.getAX(), importerChunk.getAY() + 1); break;
			case 270: buildingChunk = GameScreen.chunks.getChunkAtTileSpace(importerChunk.getAX() - 1, importerChunk.getAY()); break;
		}		
		
		if (buildingChunk.getBuilding() != null) {
			if (buildingChunk.getBuilding() instanceof AbstractStorage) {
				this.setAccepted(((AbstractStorage) buildingChunk.getBuilding()).getAccepted());
			} else {
				BaseClass.logger.warning("Exporter assigned building is not a storage!");
			}
		} else {
			BaseClass.logger.warning("IOPort assigned building is null!");
		}
	}

	@Override
	public EntityType getType() {
		return EntityType.EXPORTER;
	}
}
