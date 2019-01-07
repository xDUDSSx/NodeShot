package org.dudss.nodeshot.utils;
 
import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.terrain.Chunks;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

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
	public static TextureAtlas terrainAtlas;
	
	public static TextureRegion[] hqanimFrames;
	public static TextureRegion[] hqanimoutlineFrames;
	public static TextureRegion[] genanimFrames;
	public static TextureRegion[] genanimoutlineFrames;
	public static TextureRegion[] factoryanimFrames;
	public static TextureRegion[] factoryanimoutlineFrames;
	public static TextureRegion[] turretFrames;
	public static Texture[] conveyorHorizontal;
	public static Texture[] conveyorVertical;
	
	public static TextureRegion creeperGenOnFrame;
	public static TextureRegion creeperGenOffFrame;
	
	public static TextureRegion factoryOffFrame;
	public static TextureRegion outlineFactoryOffFrame;
	
	public static TextureRegion importerTop;
	public static TextureRegion exporterTop;
	
	public static TextureRegion mineOff;
	public static TextureRegion mineOn;
	
	public static TextureRegion node;
	
	public static Texture conveyorTexture;
	
	public static Drawable creepergenDrawable;
	public static Drawable hqDrawable;
	public static Drawable genDrawable;
	public static Drawable factoryDrawable;
	public static Drawable importerTopDrawable;
	public static Drawable exporterTopDrawable;
	public static Drawable mineDrawable;
	public static Drawable nodeDrawable;
	public static Drawable turretDrawable;
	public static Drawable ammoProcessorDrawable;
	public static Drawable shipdockDrawable;
	
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
		
		turretFrames = new TextureRegion[72];
		for (int i = 0; i < 72; i++) {
			String n;
			if (i < 9) {
				n = "0" + Integer.toString(i);
			} else {
				n = Integer.toString(i + 1);
			}
			turretFrames[i] = (TextureRegion) hqanimAtlas.findRegion("turretFrame00" + n);
		}
		
		conveyorHorizontal = new Texture[48];
		for (int i = 0; i < 48; i++) {
			String n;
			if (i < 9) {
				n = "0" + Integer.toString(i);
			} else {
				n = Integer.toString(i + 1);
			}
			conveyorHorizontal[i] = new Texture("res/conveyorFrames/conveyorHorizontal00" + n + ".png");
			conveyorHorizontal[i].setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		}
		
		conveyorVertical = new Texture[60];
		for (int i = 0; i < 60; i++) {
			String n;
			if (i < 9) {
				n = "0" + Integer.toString(i);
			} else {
				n = Integer.toString(i + 1);
			}
			conveyorVertical[i] = new Texture("res/conveyorFrames/conveyorVertical00" + n + ".png");
			conveyorVertical[i].setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		}
		
		creeperGenOffFrame = hqanimAtlas.findRegion("creepergenOFFframe");
		creeperGenOnFrame = hqanimAtlas.findRegion("creepergenONframe");
		
		factoryOffFrame = hqanimAtlas.findRegion("factoryOFFframe");
		outlineFactoryOffFrame = hqanimAtlas.findRegion("outline_factoryOFFframe");
		
		importerTop = hqanimAtlas.findRegion("importerTOP");
		exporterTop = hqanimAtlas.findRegion("exporterTOP");
		
		mineOff = hqanimAtlas.findRegion("mineOFF");
		mineOn = hqanimAtlas.findRegion("mineON");
		
		node = hqanimAtlas.findRegion("nodeNew");
		
		creepergenDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("creeperGeneratorPreview512")));
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
		
		importerTopDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("importerPreview")));
		importerTopDrawable.setMinHeight(Base.buildMenuImgSize);
		importerTopDrawable.setMinWidth(Base.buildMenuImgSize);
		
		exporterTopDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("exporterPreview")));
		exporterTopDrawable.setMinHeight(Base.buildMenuImgSize);
		exporterTopDrawable.setMinWidth(Base.buildMenuImgSize);
		
		nodeDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("nodePreview512")));
		nodeDrawable.setMinHeight(Base.buildMenuImgSize);
		nodeDrawable.setMinWidth(Base.buildMenuImgSize);
		
		mineDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("minePreview512")));
		mineDrawable.setMinHeight(Base.buildMenuImgSize);
		mineDrawable.setMinWidth(Base.buildMenuImgSize);
		
		turretDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("turretPreview512")));
		turretDrawable.setMinHeight(Base.buildMenuImgSize);
		turretDrawable.setMinWidth(Base.buildMenuImgSize);
		
		ammoProcessorDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("ammoProcessorPreview512")));
		ammoProcessorDrawable.setMinHeight(Base.buildMenuImgSize);
		ammoProcessorDrawable.setMinWidth(Base.buildMenuImgSize);
		
		shipdockDrawable = new TextureRegionDrawable(new TextureRegion(SpriteLoader.hqanimAtlas.findRegion("shipdockPreview512")));
		shipdockDrawable.setMinHeight(Base.buildMenuImgSize);
		shipdockDrawable.setMinWidth(Base.buildMenuImgSize);		
		
		conveyorTexture = new Texture("res/conveyorHorizontal0000.png");		
		conveyorTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		
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
		BaseClass.logger.info("Generating terrain textures ...");	
		double genTimeStart = System.currentTimeMillis();
		
		for (int i = 1; i < Chunks.terrainLayerNames.length; i++) {
			generateTexture("maskBR", "overlayBR", Chunks.terrainLayerNames[i], "BR");
			generateTexture("maskFULL", "overlayBR_corner_bottom", Chunks.terrainLayerNames[i], "BR_corner_bottom");
			generateTexture("maskFULL", "overlayBR_corner_top", Chunks.terrainLayerNames[i], "BR_corner_top");
			generateTexture("maskFULL", "overlayBR_fill", Chunks.terrainLayerNames[i], "BR_fill");
			generateTexture("maskFULL", "overlayBR_fill_corner_BR_SL", Chunks.terrainLayerNames[i], "BR_fill_corner_BR_SL");
			generateTexture("maskFULL", "overlayBR_fill_corner_ST_BR", Chunks.terrainLayerNames[i], "BR_fill_corner_ST_BR");
			
			generateTexture("maskBL", "overlayBL", Chunks.terrainLayerNames[i], "BL");
			generateTexture("maskFULL", "overlayBL_corner_bottom", Chunks.terrainLayerNames[i], "BL_corner_bottom");
			generateTexture("maskFULL", "overlayBL_corner_top", Chunks.terrainLayerNames[i], "BL_corner_top");
			generateTexture("maskFULL", "overlayBL_fill", Chunks.terrainLayerNames[i], "BL_fill");
			generateTexture("maskFULL", "overlayBL_fill_corner_BL_ST", Chunks.terrainLayerNames[i], "BL_fill_corner_BL_ST");
			generateTexture("maskFULL", "overlayBL_fill_corner_SR_BL", Chunks.terrainLayerNames[i], "BL_fill_corner_SR_BL");
			
			generateTexture("maskTR", "overlayTR", Chunks.terrainLayerNames[i], "TR");
			generateTexture("maskFULL", "overlayTR_corner_bottom", Chunks.terrainLayerNames[i], "TR_corner_bottom");
			generateTexture("maskFULL", "overlayTR_corner_top", Chunks.terrainLayerNames[i], "TR_corner_top");
			generateTexture("maskFULL", "overlayTR_fill", Chunks.terrainLayerNames[i], "TR_fill");
			generateTexture("maskFULL", "overlayTR_fill_corner_SB_TR", Chunks.terrainLayerNames[i], "TR_fill_corner_SB_TR");
			generateTexture("maskFULL", "overlayTR_fill_corner_TR_SL", Chunks.terrainLayerNames[i], "TR_fill_corner_TR_SL");
			
			generateTexture("maskTL", "overlayTL", Chunks.terrainLayerNames[i], "TL");
			generateTexture("maskFULL", "overlayTL_corner_bottom", Chunks.terrainLayerNames[i], "TL_corner_bottom");
			generateTexture("maskFULL", "overlayTL_corner_top", Chunks.terrainLayerNames[i], "TL_corner_top");
			generateTexture("maskFULL", "overlayTL_fill", Chunks.terrainLayerNames[i], "TL_fill");
			generateTexture("maskFULL", "overlayTL_fill_corner_SR_TL", Chunks.terrainLayerNames[i], "TL_fill_corner_SR_TL");
			generateTexture("maskFULL", "overlayTL_fill_corner_TL_SB", Chunks.terrainLayerNames[i], "TL_fill_corner_TL_SB");
			
			generateTexture("maskFULL", "overlaySR", Chunks.terrainLayerNames[i], "SR");
			generateTexture("maskFULL", "overlaySL", Chunks.terrainLayerNames[i], "SL");
			generateTexture("maskFULL", "overlayST", Chunks.terrainLayerNames[i], "ST");
			generateTexture("maskFULL", "overlaySB", Chunks.terrainLayerNames[i], "SB");
		}
		
		double genTimeEnd = System.currentTimeMillis();	
		BaseClass.logger.info("Terrain textures generated! (time: " + (genTimeEnd - genTimeStart) + " ms)");
		BaseClass.logger.info("Packing terrain textures...");
		double packTimeStart = System.currentTimeMillis();
		
		Settings settings = new Settings();
		settings.maxWidth = 2048*2;
		settings.maxHeight = 2048*2;
		settings.minWidth = 16;
		settings.minHeight = 16;

		settings.alphaThreshold = 0;
		settings.filterMin = Texture.TextureFilter.MipMapLinearNearest;
		settings.filterMag = Texture.TextureFilter.Nearest;
		
		settings.paddingX = 64;
		settings.paddingY = 64;
		settings.wrapX = Texture.TextureWrap.ClampToEdge;
		settings.wrapY = Texture.TextureWrap.ClampToEdge;
		settings.edgePadding = true;
		settings.bleed = true;
		settings.stripWhitespaceX = false;
		settings.stripWhitespaceY = false;
		settings.duplicatePadding = true;
		settings.pot = true;
		settings.alias = true;
		settings.useIndexes = true;
		settings.limitMemory = true;
		
		if (TexturePacker.isModified(System.getProperty("user.dir") + "/textureData", System.getProperty("user.dir") + "/textureData/atlas", "terrainAtlas", settings)) {
			TexturePacker.process(settings, System.getProperty("user.dir") + "/textureData", System.getProperty("user.dir") + "/textureData/atlas", "terrainAtlas");		
		}
		
		terrainAtlas = new TextureAtlas(System.getProperty("user.dir") + "/textureData/atlas/terrainAtlas.atlas");
		
		double packTimeEnd = System.currentTimeMillis();	
		BaseClass.logger.info("Packed! (time: " + (packTimeEnd - packTimeStart) + " ms)");
	}
	
	/**Composes a texture out of a solid color alpha mask, an upper layer overlay that is laid on top of a base texture.
	 * In this particular use-case, a solid terrain texture is clipped with a mask and then a specific semi-transparent edge overlay is laid on top.*/
	private static void generateTexture(String mask, String overlay, String base, String suffix) {
		Texture maskBR = new Texture("res/terrain/overlays/" + mask + ".png");
		if (!maskBR.getTextureData().isPrepared()) {
		    maskBR.getTextureData().prepare();
		}
		Pixmap maskBRpixmap = maskBR.getTextureData().consumePixmap();
		Texture overlayBR = new Texture("res/terrain/overlays/" + overlay + ".png");
		if (!overlayBR.getTextureData().isPrepared()) {
		    overlayBR.getTextureData().prepare();
		}
		Pixmap overlayBRpixmap = overlayBR.getTextureData().consumePixmap();
		Texture baseBR = new Texture("res/terrain/" + base + ".png");
		if (!baseBR.getTextureData().isPrepared()) {
		    baseBR.getTextureData().prepare();
		}
		Pixmap baseBRpixmap = baseBR.getTextureData().consumePixmap();

		Pixmap emptyPixmap = new Pixmap(maskBR.getWidth(), maskBR.getHeight(), Pixmap.Format.RGBA8888);
		
		for (int x = 0; x < emptyPixmap.getWidth(); x++) {
			for (int y = 0; y < emptyPixmap.getWidth(); y++) {
				Color maskColor = new Color(maskBRpixmap.getPixel(x, y));
				
				if (maskColor.a > 0) {
					Color baseColor =  new Color(baseBRpixmap.getPixel(x, y));
					Color overlayColor = new Color(overlayBRpixmap.getPixel(x, y)); 
					
					float r = (float) (overlayColor.r * overlayColor.a + baseColor.r * (1.0 - overlayColor.a));
					float g = (float) (overlayColor.g * overlayColor.a + baseColor.g * (1.0 - overlayColor.a));
					float b = (float) (overlayColor.b * overlayColor.a + baseColor.b * (1.0 - overlayColor.a));
					float a = maskColor.a;
					
					emptyPixmap.drawPixel(x, y, Color.rgba8888(r, g, b, a));
				} else {
					emptyPixmap.drawPixel(x, y, Color.rgba8888(0, 0, 0, 0));
				}
			}
		}
		
		PixmapIO.writePNG(Gdx.files.local("/textureData/" + base + suffix + ".png"), emptyPixmap);
	}
}
