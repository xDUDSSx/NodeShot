package org.dudss.nodeshot.utils;
 
import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**Class that holds and loads all texture resources*/
public class SpriteLoader {
	
	public static Texture spriteSheet;
	public static Texture gridOverlay;
	public static Texture gridOverlay2;
	public static Texture sectionOutline;
	
	public static Texture savanaTex;
	
	public static Texture ironTex;
	public static Texture coalTex;
	public static Texture coalLowerTex;
	public static Texture coalLowTex;

	public static Sprite packageSprite;
	public static Sprite packageHighlightSprite;
	public static Sprite nodeSprite;
	public static Sprite nodeHighlightSprite;
	public static Sprite highlightSprite;
	public static Sprite coalSprite;
	public static Sprite ironSprite;
	public static Sprite coalHighlightSprite;
	public static Sprite ironHighlightSprite;
	public static Sprite nodeClosedSprite;
	public static Sprite nodeInputSprite;
	public static Sprite nodeOutputSprite;
	public static Sprite nodeCoalSprite;
	public static Sprite nodeIronSprite;                                         
	public static Sprite nodeConveyorSprite;
	
	public static Sprite dirtTileSprite;
	public static Sprite bigdirtTileSprite;
	
	public static Sprite coalTileSprite;
	public static Sprite coalTileLowSprite;
	public static Sprite coalTileLowerSprite;
	
	public static Sprite ironTileSprite;
	
	public static TextureAtlas tileAtlas;
	
	public static TextureAtlas hqanimAtlas;
	public static TextureRegion[] hqanimFrames;
	public static TextureRegion[] hqanimoutlineFrames;
	public static TextureRegion[] genanimFrames;
	public static TextureRegion[] genanimoutlineFrames;
	public static TextureRegion[] factoryanimFrames;
	public static TextureRegion[] factoryanimoutlineFrames;
	
	
	public static TextureRegion creeperGenOnFrame;
	public static TextureRegion creeperGenOffFrame;
	
	public static TextureRegion factoryOffFrame;
	public static TextureRegion outlineFactoryOffFrame;
	
	public static TextureRegion importerTop;
	
	public static TextureRegion mineOff;
	public static TextureRegion mineOn;
	
	
	public static Drawable creepergenDrawable;
	public static Drawable hqDrawable;
	public static Drawable genDrawable;
	public static Drawable factoryDrawable;
	public static Drawable importerTopDrawable;
	public static Drawable mineDrawable;
	public static Drawable nodeDrawable;
	
	public static Sprite turret;
	public static Sprite turretHead;
	public static Sprite bullet;
	
	/**Loads all textures*/
	public static void loadAll() {
		BaseClass.logger.info("Loading sprites ...");
		double currentTime = System.currentTimeMillis();
		if (Gdx.app.getType() == ApplicationType.Android) {
        	//Not supported
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	spriteSheet = new Texture("res/spritesheet16x16.png");
        	gridOverlay = new Texture("res/GridOverlay64x32.png");
        	ironTex = new Texture("res/tiledIron.png");
        	coalTex = new Texture("res/tiledCoal.png");
        	coalLowerTex = new Texture("res/tiledCoallower.png");
        	coalLowTex = new Texture("res/tiledCoallow.png");       	
        	tileAtlas = new TextureAtlas("res/tiles.atlas");
        	hqanimAtlas = new TextureAtlas("res/animtiles.atlas");
        	sectionOutline = new Texture("res/sectionOutline.png");
        }
		
		Sprite s = new Sprite();
		hqanimFrames = new TextureRegion[72];
		for (int i = 0; i < 72; i++) {
			String n;
			if (i < 9) {
				n = "0" + Integer.toString(i + 1);
			} else {
				n = Integer.toString(i + 1);
			}
			hqanimFrames[i] = (TextureRegion) hqanimAtlas.findRegion("hqframe00" + n);
		}
		
		hqanimoutlineFrames = new TextureRegion[72];
		for (int i = 0; i < 72; i++) {
			String n;
			if (i < 9) {
				n = "0" + Integer.toString(i + 1);
			} else {
				n = Integer.toString(i + 1);
			}
			hqanimoutlineFrames[i] = (TextureRegion) hqanimAtlas.findRegion("outline_hqframe00" + n);
		}
		
		genanimFrames = new TextureRegion[72];
		for (int i = 0; i < 72; i++) {
			String n;
			if (i < 9) {
				n = "0" + Integer.toString(i + 1);
			} else {
				n = Integer.toString(i + 1);
			}
			genanimFrames[i] = (TextureRegion) hqanimAtlas.findRegion("genframe00" + n);
		}
		
		genanimoutlineFrames = new TextureRegion[72];
		for (int i = 0; i < 72; i++) {
			String n;
			if (i < 9) {
				n = "0" + Integer.toString(i + 1);
			} else {
				n = Integer.toString(i + 1);
			}
			genanimoutlineFrames[i] = (TextureRegion) hqanimAtlas.findRegion("outline_genframe00" + n);
		}
		
		factoryanimFrames = new TextureRegion[48];
		for (int i = 0; i < 48; i++) {
			String n;
			if (i < 9) {
				n = "0" + Integer.toString(i + 1);
			} else {
				n = Integer.toString(i + 1);
			}
			factoryanimFrames[i] = (TextureRegion) hqanimAtlas.findRegion("factoryFrame00" + n);
		}
		
		
		factoryanimoutlineFrames = new TextureRegion[48];
		for (int i = 0; i < 48; i++) {
			String n;
			if (i < 9) {
				n = "0" + Integer.toString(i + 1);
			} else {
				n = Integer.toString(i + 1);
			}
			factoryanimoutlineFrames[i] = (TextureRegion) hqanimAtlas.findRegion("outline_factoryFrame00" + n);
		}
		
		creeperGenOffFrame = hqanimAtlas.findRegion("creepergenOFFframe");
		creeperGenOnFrame = hqanimAtlas.findRegion("creepergenONframe");
		
		factoryOffFrame = hqanimAtlas.findRegion("factoryOFFframe");
		outlineFactoryOffFrame = hqanimAtlas.findRegion("outline_factoryOFFframe");
		
		importerTop = hqanimAtlas.findRegion("importerTOP");
		
		mineOff = hqanimAtlas.findRegion("mineOFF");
		mineOn = hqanimAtlas.findRegion("mineON");
		
		creepergenDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("creepergenPreview512")));
		creepergenDrawable.setMinHeight(Base.buildMenuImgSize);
		creepergenDrawable.setMinWidth(Base.buildMenuImgSize);
		
		hqDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("hqPreview512")));
		hqDrawable.setMinHeight(Base.buildMenuImgSize);
		hqDrawable.setMinWidth(Base.buildMenuImgSize);
		
		genDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("generatorPreview512")));
		genDrawable.setMinHeight(Base.buildMenuImgSize);
		genDrawable.setMinWidth(Base.buildMenuImgSize);
		
		factoryDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("factoryPreview512")));
		factoryDrawable.setMinHeight(Base.buildMenuImgSize);
		factoryDrawable.setMinWidth(Base.buildMenuImgSize);
		
		importerTopDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("importerTOPpreview256")));
		importerTopDrawable.setMinHeight(Base.buildMenuImgSize);
		importerTopDrawable.setMinWidth(Base.buildMenuImgSize);
		
		nodeDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("nodePreview512")));
		nodeDrawable.setMinHeight(Base.buildMenuImgSize);
		nodeDrawable.setMinWidth(Base.buildMenuImgSize);
		
		
		mineDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("minePreview512")));
		mineDrawable.setMinHeight(Base.buildMenuImgSize);
		mineDrawable.setMinWidth(Base.buildMenuImgSize);
		
		packageSprite = new Sprite(spriteSheet, 0, 0, 16, 16);
		packageHighlightSprite = new Sprite(spriteSheet, 17, 0, 16, 16);
		
		nodeSprite = new Sprite(spriteSheet, 34, 0, 16, 16);
		nodeHighlightSprite = new Sprite(spriteSheet, 51, 0, 16, 16);
		
		highlightSprite = new Sprite(spriteSheet, 68, 0, 16, 16);
		
		coalSprite = new Sprite(spriteSheet, 0, 17, 16, 16);
		ironSprite = new Sprite(spriteSheet, 0, 34, 16, 16);
		coalHighlightSprite = new Sprite(spriteSheet, 17, 17, 16, 16);
		ironHighlightSprite = new Sprite(spriteSheet, 17, 34, 16, 16);
		
		nodeClosedSprite = new Sprite(spriteSheet, 34, 17, 16, 16);
		nodeInputSprite = new Sprite(spriteSheet, 68, 17, 16, 16);
		nodeOutputSprite = new Sprite(spriteSheet, 51, 17, 16, 16);
		nodeCoalSprite = new Sprite(spriteSheet, 34, 34, 16, 16);
		nodeIronSprite = new Sprite(spriteSheet, 51, 34, 16, 16);	
		nodeConveyorSprite = new Sprite(spriteSheet, 68, 34, 16, 16);
		
		turret = new Sprite(spriteSheet, 0, 68, 48, 48);
		turretHead = new Sprite(spriteSheet, 49, 68, 64, 23);
		bullet = new Sprite(spriteSheet, 49, 92, 7, 3);
				
		double nextTime = System.currentTimeMillis();	
		BaseClass.logger.info("Sprites loaded! (time: " + (nextTime - currentTime) + " ms)");
	}
}
