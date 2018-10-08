package org.dudss.nodeshot.entities.nodes;

import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class ConveyorNode extends Node {

	public ConveyorNode(float cx, float cy, int radius) {
		super(cx, cy, radius);
		
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
	
}
