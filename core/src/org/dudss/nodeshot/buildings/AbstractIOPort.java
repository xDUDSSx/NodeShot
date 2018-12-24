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
	
	int ioSpeed = 50;
	int nextOperation = SimulationThread.simTick + ioSpeed;
	
	int spriteRotation = GameScreen.activeRotation;
	
	public AbstractIOPort(float cx, float cy, float width, float height) {
		super(cx, cy, width, height);
	}
	
	@Override
	public void update() {
		input.update();
	}
	
	@Override
	public void demolish() {	
		GameScreen.buildingManager.removeMisc(this);
		input.remove();
		
		clearBuildingChunks();
		updateFogOfWar(false);
	}
	
	public void rotateRight() {
		spriteRotation += 90;
		spriteRotation %= 360;
		GameScreen.activeRotation = spriteRotation;
	}

	public void rotateLeft() {
		if (spriteRotation == 0) {
			spriteRotation = 270;
		} else {
			this.spriteRotation -= 90;
		}
		GameScreen.activeRotation = spriteRotation;
	}
	
	public AbstractBuilding getBuilding() {
		return this.buildingChunk.getBuilding();
	}
}
