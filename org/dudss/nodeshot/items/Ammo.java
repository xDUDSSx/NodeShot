package org.dudss.nodeshot.items;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Ammo extends Package implements Item {
	
	public Sprite ammoSprite = SpriteLoader.packageSprite;
	
	//Package representing an item
	public Ammo(Node from, Node to) {
		super(from, to);
		
		highlightSprite = SpriteLoader.packageHighlightSprite;
	}
	
	public Ammo(Node from) {
		super(from);
		
		highlightSprite = SpriteLoader.packageHighlightSprite;
	}

	@Override
	public void draw(SpriteBatch batch) {
		packageSprite = ammoSprite;
		ammoSprite.setScale(0.6f);
		ammoSprite.setPosition((float) x, (float) y); 
		ammoSprite.draw(batch);
	}
	
	@Override
	public void drawHighlight(SpriteBatch batch) {		
		highlightSprite.setPosition(x, y);
		highlightSprite.setOrigin(radius/2, radius/2);
		highlightSprite.setScale(0.65f);
		highlightSprite.draw(batch);		
	}
	
	@Override
	public ItemType getItemType() {
		return ItemType.AMMO;
	}
	
}

