package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.nodes.IONode;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;

/**An Input/Output building that consists of two tiles and can interact with an {@link AbstractStorage} and its storage.*/
public abstract class AbstractIOPort extends AlertableBuilding {
	
	Chunk importerChunk;
	Chunk buildingChunk;
	
	IONode input;
	
	int ioSpeed = 100;
	int nextOperation = SimulationThread.simTick + ioSpeed;
	
	public AbstractIOPort(float cx, float cy, float width, float height) {
		super(cx, cy, width, height);
	}
	
	@Override
	public void update() {
		input.update();
	}
	
	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeMisc(this);
		input.remove();
		
		clearBuildingChunks();
		updateFogOfWar(false);
	}
	
	public AbstractBuilding getBuilding() {
		return this.buildingChunk.getBuilding();
	}
}
