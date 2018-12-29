package org.dudss.nodeshot.items;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ProcessedMaterial extends Package implements Item {
	
	public TextureRegion materialSprite = new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("processedMaterial"));
	
	//Package representing an item
	public ProcessedMaterial(Node from, Node to) {
		super(from, to);	
		highlightSprite = new Sprite(materialSprite);
	}
	
	public ProcessedMaterial(Node from) {
		super(from);	
		highlightSprite = new Sprite(materialSprite);
	}
	
	public ProcessedMaterial() {
		super();	
		highlightSprite = new Sprite(materialSprite);
	}

	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(materialSprite, this.x, this.y, Base.PACKAGE_RADIUS, Base.PACKAGE_RADIUS);
	}
	
	@Override
	public void drawHighlight(SpriteBatch batch) {		
		batch.draw(materialSprite, this.x, this.y, Base.PACKAGE_RADIUS, Base.PACKAGE_RADIUS);	
	}
	
	@Override
	public ItemType getItemType() {
		return ItemType.PROCESSED_MATERIAL;
	}
}


