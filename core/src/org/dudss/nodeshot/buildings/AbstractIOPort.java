package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;

public abstract class AbstractIOPort extends AbstractStorage {
	
	Chunk importerChunk;
	Chunk buildingChunk;
	
	AbstractStorage assignedStorage;
	
	public AbstractIOPort(float cx, float cy, float width, float height) {
		super(cx, cy, width, height);
		
		this.maxStorage = 1;
	}

	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeMisc(this);
		input.remove();
		
		clearBuildingChunks();
		updateFogOfWar(false);
	}
	
	public AbstractStorage getAssignedStorage() {
		return assignedStorage;
	}
}
