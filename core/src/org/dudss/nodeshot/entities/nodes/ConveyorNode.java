package org.dudss.nodeshot.entities.nodes;

import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.buildings.NodeBuilding;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class ConveyorNode extends Node {

	/**The building this conveyor node is a part of.*/
	NodeBuilding building;
	
	public ConveyorNode(float cx, float cy, int radius) {
		super(cx, cy, radius);
		
		this.set(SpriteLoader.nodeConveyorSprite);
		this.setPosition(x, y);
	}
	
	public ConveyorNode(float cx, float cy, int radius, NodeBuilding building) {
		super(cx, cy, radius);
		
		this.building = building;
		this.set(SpriteLoader.nodeConveyorSprite);
		this.setPosition(x, y);
	}
	
	@Override
	public void setClosed(Boolean closed) {
		this.closed = closed;
		if (closed == true) {
			this.set(new Sprite(SpriteLoader.nodeClosedSprite));
			this.setPosition(x, y);
		} else {
			this.set(new Sprite(SpriteLoader.nodeConveyorSprite));
			this.setPosition(x, y);
		}
	}
	
	/**Returns the assigned {@link AbstractBuilding}.*/
	public AbstractBuilding getBuilding() {
		return this.building;
	}
}
