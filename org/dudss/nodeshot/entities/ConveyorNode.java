package org.dudss.nodeshot.entities;

import org.dudss.nodeshot.utils.SpriteLoader;

public class ConveyorNode extends Node {

	public ConveyorNode(float cx, float cy, int radius) {
		super(cx, cy, radius);
		
		this.set(SpriteLoader.nodeConveyorSprite);
		this.setPosition(x, y);
	}
}
