package org.dudss.nodeshot.items;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Iron extends Package {
	
	Sprite ironSprite;
	
	//Package representing an item
	public Iron(Node from, Node to) {
		super(from, to);
		System.out.println("Setting coal sprite!");
		ironSprite = new Sprite(GameScreen.spriteSheet, 17, 34, 16, 16);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		ironSprite.setScale(0.6f);
		ironSprite.setPosition((float) x, (float) y); 
		ironSprite.draw(batch);
	}
	
}
