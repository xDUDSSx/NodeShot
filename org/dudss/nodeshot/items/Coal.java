package org.dudss.nodeshot.items;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Coal extends Package {
	
	public Sprite coalSprite = SpriteLoader.coalSprite;
	
	//Package representing an item
	public Coal(Node from, Node to) {
		super(from, to);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		packageSprite = coalSprite;
		coalSprite.setScale(0.6f);
		coalSprite.setPosition((float) x, (float) y); 
		coalSprite.draw(batch);
	}
	
}
