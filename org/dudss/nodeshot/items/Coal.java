package org.dudss.nodeshot.items;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Coal extends Package {
	
	Sprite coalSprite;
	
	//Package representing an item
	public Coal(Node from, Node to) {
		super(from, to);
		System.out.println("Setting coal sprite!");
		coalSprite = new Sprite(GameScreen.spriteSheet, 34, 17, 16, 16);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		coalSprite.setScale(0.6f);
		coalSprite.setPosition((float) x, (float) y); 
		coalSprite.draw(batch);
	}
	
}
