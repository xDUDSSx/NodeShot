package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.nodes.IONode;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;

/**An Input/Output building that consists of two tiles and can interact with an {@link AbstractStorage} and its storage.*/
public abstract class AbstractIOPort extends AlertableBuilding implements Connectable {
	
	Chunk importerChunk;
	Chunk buildingChunk;
	
	IONode ioNode;
	
	int ioSpeed = 50;
	int nextOperation = SimulationThread.simTick + ioSpeed;
	
	int spriteRotation = GameScreen.activeRotation;
	
	public AbstractIOPort(float cx, float cy, float width, float height) {
		super(cx, cy, width, height);
		buildingType = BuildingType.MISC;
	}
	
	@Override
	public void update() {
		super.update();
		if (ioNode != null) {
			ioNode.update();
		}
	}
	
	@Override
	public void demolish(boolean returnBits) {	
		super.demolish(returnBits);
		ioNode.remove();
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
	
	
	@Override
	public void build() {
		super.build(false);
		this.ioNode = new IONode(x + Base.CHUNK_SIZE/2, y + Base.CHUNK_SIZE/2, Base.RADIUS, this);
		GameScreen.nodelist.add(ioNode);
		this.register();
	}
	
	public AbstractBuilding getBuilding() {
		return this.buildingChunk.getBuilding();
	}
	
	public int getBuildCost() {
		return Base.IOPORT_BUILD_COST;
	}
	
	public int getEnergyCost() {
		return Base.IOPORT_ENERGY_COST;
	}
	
	@Override  
	public Node getNode() {
		return ioNode;
	}
}
