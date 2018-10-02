package org.dudss.nodeshot.screens;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.algorithms.SimplexNoiseGenerator;
import org.dudss.nodeshot.buildings.Building;
import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.InputNode;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.OutputNode;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.inputs.DesktopInputProcessor;
import org.dudss.nodeshot.inputs.MobileGestureListener;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.misc.BuildingHandler;
import org.dudss.nodeshot.misc.ConnectorHandler;
import org.dudss.nodeshot.misc.PackageHandler;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.Chunks;
import org.dudss.nodeshot.ui.BuildMenu;
import org.dudss.nodeshot.ui.HudMenu;
import org.dudss.nodeshot.ui.RightClickMenuManager;
import org.dudss.nodeshot.utils.Selector;
import org.dudss.nodeshot.utils.Shaders;
import org.dudss.nodeshot.utils.SpriteLoader;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    public static Game nodeshotGame;

    //new variables
    public static int WIDTH;
    public static int HEIGHT;
    
    public static String debugMessage = "Debug message";	
	public static Logger LOGGER = Logger.getLogger(GameScreen.class.getSimpleName());
    
    public static boolean startedOnce = false;
    
	public static long currentSimTimeTick;
	public static long nextSimTimeTick;

	public static int sfps;
	public static int simFrameCount;
	public static double simFac;

	public static Vector2 mousePos = new Vector2();

	//Collections
	public static CopyOnWriteArrayList<Node> nodelist = new CopyOnWriteArrayList<Node>();
	public static CopyOnWriteArrayList<Package> packagelist = new CopyOnWriteArrayList<Package>();

	//Handlers
	public static PackageHandler packageHandler;
	public static ConnectorHandler connectorHandler;
	public static BuildingHandler buildingHandler;

	static Boolean NodeInfoHidden = false;
	public static Boolean NodeSelectiveInfo = true;
	static Boolean NodeConnectRadiusHidden = true;

	public static Boolean toggleConnectMode = false;
	
	//Android old UI
	public static Boolean oldNodeBuildMode = false;
	
	public static Boolean activeNewConnection = false;
	public static int newConnectionFromIndex;
	public static Boolean draggingConnection = false;

	public static int mouseX;
	public static int mouseY;

	//edited
	public static Vector3 lastMousePress = new Vector3(0,0,0);

	public static enum MouseType {
		MOUSE_1, MOUSE_2, MOUSE_3
	}
	public static MouseType lastMousePressType;

	public static int selectedID = -1;
	public static int selectedIndex = -1;
	public static EntityType selectedType = EntityType.NONE;

	static Boolean drawString = false;
	static String stringToWrite = "";

	//Terrain
	public static Chunks chunks;
	static Pixmap pixmap;
	static Pixmap pixmap2;
	static Texture pixtex;
	static Sprite pixsprite;
	public static Chunk hoverChunk = null;

	public static Rectangle viewBounds;	
	public static Rectangle imageBounds;
	
	public static Sprite toolSprite;

	public static float viewportWidth = 300f;
    FreeTypeFontGenerator generator;
	
	public static boolean buildMode = false;
	public static Building builtBuilding = null;
	public static Node builtConnector = null;
	
    //libGDX
    SpriteBatch batch;
    Texture img;
    ShapeRenderer r;
    
    public static BitmapFont font;
    GlyphLayout layout;

    public static OrthographicCamera cam;
    public static Vector3 lastCamePos;
    public float lastZoom;
    public static Boolean zooming = false;
    public static GLProfiler glProfiler;
    
    TextureAtlas atlas;
    
    //UI
    public static Rectangle backButton = new Rectangle();
    public static Rectangle buildButton = new Rectangle();
    public static Rectangle deleteButton = new Rectangle();

    public static Skin skin;
    public static Stage stage;
    public static Viewport stageViewport;
    public static RightClickMenuManager rightClickMenuManager;
    public static BuildMenu buildMenu;
    public static HudMenu hudMenu;
    
    public static FrameBuffer corrBuffer;
    public static FrameBuffer fboA;
    public static FrameBuffer blurBuffer;
    
    public GameScreen(Game game)
    {
        nodeshotGame = game;
        glProfiler = new GLProfiler(Gdx.graphics);
        glProfiler.enable();
        packageHandler = new PackageHandler();
        connectorHandler = new ConnectorHandler();
        buildingHandler = new BuildingHandler();       
        chunks = new Chunks();        
        rightClickMenuManager = new RightClickMenuManager();             
	
        if (Gdx.app.getType() == ApplicationType.Android) {
        	Texture tooltex = new Texture(Gdx.files.internal("res/tools_icon_button64.png"));
        	toolSprite = new Sprite(tooltex);
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	Texture tooltex = new Texture("res/tools_icon_button64.png");
        	toolSprite = new Sprite(tooltex);
        }
        
        //Loading item sprites
        Shaders.load();
        SpriteLoader.loadAll();
        
        //LineWidth
        Gdx.gl.glLineWidth(2);
        
        //Terrain generation
        SimplexNoiseGenerator sn = new SimplexNoiseGenerator();
        float[][] coalMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 3, 0.5f, 0.015f);
        sn.randomizeMutatorTable();
        float[][] ironMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 3, 0.5f, 0.015f);
        
        pixmap = new Pixmap(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, Format.RGBA8888);
        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {   
        		//Sometimes values extend beyond the accepted [-1.0,1.0] range, correct that
        		if (coalMap[x][y] > 1) {
        			coalMap[x][y] = 1.0f;
        	    }
        	    if (coalMap[x][y] < -1) {
        	    	coalMap[x][y] = -1.0f;
        	    }
        	    
        	    //Converting [-1.0,1.0] to [0,1]
        	    float val = (((coalMap[x][y] - (-1.0f)) * (1.0f - 0)) / (1.0f - (-1.0f))) + 0;
        	    pixmap.setColor(Color.rgba8888(val, val, val, 1.0f));
        		pixmap.drawPixel(x, y);
        	}
        }
        
        pixmap2 = new Pixmap(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, Format.RGBA8888);
        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {   
        		//Sometimes values extend beyond the accepted [-1.0,1.0] range, correct that
        		if (ironMap[x][y] > 1) {
        			ironMap[x][y] = 1.0f;
        	    }
        	    if (ironMap[x][y] < -1) {
        	    	ironMap[x][y] = -1.0f;
        	    }
        	    
        	    //Converting [-1.0,1.0] to [0,1]
        	    float val = (((ironMap[x][y] - (-1.0f)) * (1.0f - 0)) / (1.0f - (-1.0f))) + 0;
        	    pixmap2.setColor(Color.rgba8888(val, val, val, 1.0f));
        		pixmap2.drawPixel(x, y);
        	}
        }
        
        pixtex = new Texture(pixmap);
        pixsprite = new Sprite(pixtex);
    }

    public static int getWidth() {return WIDTH;}
    public static int getHeight() {return HEIGHT;}

    @Override
    public void show() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
               
        //Cam
        cam = new OrthographicCamera(viewportWidth , viewportWidth * (HEIGHT / WIDTH));
        if (lastCamePos == null) {
            cam.position.set(Base.WORLD_SIZE / 2f, Base.WORLD_SIZE / 2f, 0);
            cam.zoom = 3f;
        } else {
            cam.position.set(lastCamePos);
            cam.zoom = lastZoom;
        }
        cam.update();
        lastCamePos = cam.position;
        lastZoom = cam.zoom;
        
        Shaders.blurShader.begin();
        Shaders.blurShader.setUniformf("resolution", cam.zoom * 100);
        Shaders.blurShader.end();

		blurBuffer = new FrameBuffer(Format.RGBA8888, WIDTH/2, HEIGHT/2, false);
		corrBuffer = new FrameBuffer(Format.RGBA8888, WIDTH/2, HEIGHT/2, false);
		
        batch = new SpriteBatch();
        r = new ShapeRenderer();
        
        //font generation
        font = new BitmapFont();
        layout = new GlyphLayout();         
        generator = new FreeTypeFontGenerator(Gdx.files.classpath("res/Helvetica-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Base.HUD_FONT_SIZE;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!'()>?:%+-*/";        
        font = generator.generateFont(parameter);
        generator.dispose();
       
        //User Interface
        if (Gdx.app.getType() == ApplicationType.Android) {
        	atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        	skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	atlas = new TextureAtlas("res/uiskin.atlas");
        	skin = new Skin(Gdx.files.classpath("res/uiskin.json"), atlas);
        }
        stageViewport = new StretchViewport(WIDTH, HEIGHT);
        stage = new Stage(stageViewport);         

        buildMenu = new BuildMenu("Build menu", skin);
        TextButton imgButton = new TextButton("Build", skin, "hoverfont15");
        imgButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	buildMenu.setVisible(!(buildMenu.isVisible()));
            	GameScreen.hudMenu.setVisible(!(buildMenu.isVisible()));
            }
        });
        imgButton.setSize(64, 64);
        imgButton.setPosition(10, 10);        
        stage.addActor(imgButton);
        stage.addActor(buildMenu);
        
        hudMenu = new HudMenu("HUD menu", skin);
        stage.addActor(hudMenu);
        
        //Ingame HUD buttons (Android) (//TODO: remove, utilize proper scene UI) //Kinda finished, not for mobile tho
        if (Gdx.app.getType() == ApplicationType.Android) {
	        backButton.set( 10, 10, 180, 180);
	        deleteButton.set( 10 , (200)*1 + 10, 180, 180);
	        buildButton.set( 10, (200)*2 + 10, 180, 180);
        }
        
        //Input processors
        InputMultiplexer multiplexer = new InputMultiplexer();       
        multiplexer.addProcessor(stage);
        if (Gdx.app.getType() == ApplicationType.Android) {
        	MobileGestureListener mgl = new MobileGestureListener();
        	multiplexer.addProcessor(new GestureDetector(mgl));
        } else {
        	DesktopInputProcessor dip = new DesktopInputProcessor();
        	multiplexer.addProcessor(dip);
        }  
        Gdx.input.setInputProcessor(multiplexer); 
        
        //Generate terrain if not generated already
        if (chunks.created == false) {
        	chunks.generateAll();
        }
        viewBounds = new Rectangle();
        imageBounds = new Rectangle();
        
        //A render optimization rectangle that tells the renderer which objects are out of sight
        float width = cam.viewportWidth * cam.zoom;
		float height = cam.viewportHeight * cam.zoom;
		float w = width * Math.abs(cam.up.y) + height * Math.abs(cam.up.x);
		float h = height * Math.abs(cam.up.y) + width * Math.abs(cam.up.x);
        viewBounds.set(cam.position.x - w / 2 - 50, cam.position.y - h / 2 - 50, w + 100, h + 100);
        
        Circle c = new Circle(Base.WORLD_SIZE/2, Base.WORLD_SIZE/2, Base.WORLD_SIZE/2 - 200);
        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
    		for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
    			Rectangle rect = new Rectangle(chunks.getChunk(x, y).getX(), chunks.getChunk(x, y).getY(), chunks.getChunk(x, y).getSize(), chunks.getChunk(x, y).getSize());    			
        		if (!(Intersector.overlaps(c, rect))) {
        			chunks.getChunk(x, y).setCreeperLevel(1f);
        			chunks.getChunk(x, y).setPlagueLevel(0f);
        		}
            }
        }
        
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    
    }

    @Override
    public void hide() {

    }
    
    private void drawTexture(FrameBuffer fboA, FrameBuffer fboB, Texture texture, float x, float y) {
    	fboB.begin();
    	Shaders.blurShader.begin();
    	Shaders.blurShader.setUniformf("dir", 1.0f, 0.0f);
    	Shaders.blurShader.setUniformf("radius", 1f);
        Shaders.blurShader.setUniformf("resolution", cam.zoom * 50);
    	Shaders.blurShader.end();
		batch.setShader(Shaders.blurShader);   	
				
		Sprite s = new Sprite(texture);
		Matrix4 m = new Matrix4();
		m.setToOrtho2D(0, 0, fboA.getWidth(), fboA.getHeight());
		batch.setProjectionMatrix(m);	

		s.flip(false, true);
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		s.draw(batch);
		batch.end();
		fboB.end();
		
		Shaders.blurShader.begin();
    	Shaders.blurShader.setUniformf("dir", 0.0f, 1.0f);
    	Shaders.blurShader.setUniformf("radius", 1f);
    	Shaders.blurShader.setUniformf("resolution", cam.zoom * 50);
    	Shaders.blurShader.end();
		batch.setShader(Shaders.blurShader);   	
		s = new Sprite(fboB.getColorBufferTexture());
		m.setToOrtho2D(0, 0, fboB.getWidth(), fboB.getHeight());		
		batch.setProjectionMatrix(m);
		
		s.flip(false, true);
		batch.begin();
		s.draw(batch);
		batch.end();
		
		batch.setProjectionMatrix(cam.combined);
	}   
    
    void drawCorruption(SpriteBatch batch) {
    	corrBuffer.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
 		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        chunks.drawCorruption(batch);
 		batch.end();
 		corrBuffer.end();
 						
		/*Sprite s = new Sprite(corrBuffer.getColorBufferTexture());
		Matrix4 m = new Matrix4();
		m.setToOrtho2D(0, 0, corrBuffer.getWidth(), corrBuffer.getHeight());		
		batch.setProjectionMatrix(m);
		
		s.flip(false, true);
		batch.begin();
		s.draw(batch);
 		batch.end();
 		*/
 		
 		drawTexture(corrBuffer, blurBuffer, corrBuffer.getColorBufferTexture(), 0, 0);
    }
    
    @Override
    public void render (float delta) {
        handleInput();
        hudMenu.update();
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        r.setProjectionMatrix(cam.combined);
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
     
        glProfiler.reset();        
        
        batch.begin();
        drawBackgroundSand(batch);      
        batch.end();
        
        batch.begin();
		chunks.draw(r, batch); 
		batch.end();
		
		drawCorruption(batch);
		
		batch.setShader(Shaders.defaultShader);
        
        LOGGER.info("\n\nDraw calls: " + glProfiler.getDrawCalls() + 
        			"\nCalls: " + glProfiler.getCalls() +
        			"\nTexture binding " + glProfiler.getTextureBindings() + 
        			"\nShaderSwitches: " + glProfiler.getShaderSwitches()
        );
        

        r.setAutoShapeType(true);
        
        if (buildMode == true) {
	        r.begin(ShapeType.Filled);
	        r.setColor(Color.WHITE);
	        for (int i = 0; i < Base.CHUNK_AMOUNT; i++) {
	        	r.rectLine(i * Base.CHUNK_SIZE, 0, i * Base.CHUNK_SIZE, Base.WORLD_SIZE, 0.5f);
	        	r.rectLine(0, i * Base.CHUNK_SIZE, Base.WORLD_SIZE, i * Base.CHUNK_SIZE, 0.5f);
	        }
	        r.set(ShapeType.Filled);
	        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
	        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	   
	        		float width = cam.viewportWidth * cam.zoom;
	        		float height = cam.viewportHeight * cam.zoom;
	        		float w = width * Math.abs(cam.up.y) + height * Math.abs(cam.up.x);
	        		float h = height * Math.abs(cam.up.y) + width * Math.abs(cam.up.x);
	        	    viewBounds.set(cam.position.x - w / 2 - 50, cam.position.y - h / 2 - 50, w + 100, h + 100);
	        		imageBounds.set(chunks.getChunk(x, y).getX(), chunks.getChunk(x, y).getY(), chunks.getChunk(x, y).getSize(), chunks.getChunk(x, y).getSize());		                

	        		if (viewBounds.contains(imageBounds) || viewBounds.overlaps(imageBounds)) {	 
		        		if (chunks.getChunk(x, y).getOreLevel() > 0) {
		        			float n = chunks.getChunk(x, y).getOreLevel() * 100;
		        			//System.out.println(n);
		        			if (n == 100) {
		        				n = 99;
		        			}
		        			float rc = (255 * n) / 100;
		        			float g = (255 * (100 - n)) / 100 ;
		        			float b = 0;
		        			//System.out.println(rc + " " + g + " " + b);
		        			Color c = new Color(Color.rgba8888(rc/255f, g/255f, b/255f, 1.0f));
		        			r.setColor(c);
		        			r.rect((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE), Base.CHUNK_SIZE, Base.CHUNK_SIZE);
		        		}	      		
		        	}
	        	}
	        }
	        r.end();
	    }


        r.begin(ShapeType.Filled);
        buildingHandler.drawAll(r);
        r.end();
        
        //Highlight of the chunk the mouse is hovering on
        if (hoverChunk != null) {
        	if (buildMode == true && builtConnector != null) {
        		r.begin(ShapeType.Filled);
	        	r.setColor(Color.WHITE);
	        	r.rect(hoverChunk.getX(), hoverChunk.getY(), hoverChunk.getSize(), hoverChunk.getSize());
	        	r.end();
        	} else if ((hoverChunk.getCoalLevel() > 0 || hoverChunk.getIronLevel() > 0)) {
	        	r.begin(ShapeType.Line);
	        	r.setColor(Color.WHITE);
	        	r.rect(hoverChunk.getX(), hoverChunk.getY(), hoverChunk.getSize(), hoverChunk.getSize());
	        	r.end();
        	}
        }
      
        //grid rendering
        drawConnectors(r);
    
        if (buildMode && builtBuilding != null) {
        	r.begin(ShapeType.Filled);
        	Gdx.gl.glEnable(GL20.GL_BLEND);       
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);                  	
        	drawPrefab(r);
        	r.end();         	 
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
         
       
       
        batch.begin();
        //Drawing packages
        for(int i = 0; i < packagelist.size(); i++) {
            Package p = packagelist.get(i);
            
            if (selectedID == p.getID()) {
            	packagelist.get(selectedIndex).drawHighlight(batch);
            }          
            
            p.draw(batch);
        }
        
        //Drawing nodes & highlights
        for (int i = 0; i < nodelist.size(); i++) {
            Node n = nodelist.get(i);  
            if (n instanceof OutputNode || n instanceof InputNode) {
            	n.setScale(0.8f);
            } else {
            	n.setScale(0.45f);
            }
            
            n.draw(batch);
            
            if (n.getID() == selectedID) {
                Sprite s = new Sprite(SpriteLoader.highlightSprite);              
                s.setPosition(n.getX(), n.getY());
                s.setOrigin(n.radius/2, n.radius/2);
                
                if (n instanceof OutputNode || n instanceof InputNode) {
                	s.setScale(0.85f);
                } else {
                	s.setScale(0.50f);
                }
                s.draw(batch);
            }
           
        }
        //HUD, draw last
        //setting UI matrix
        setHudProjectionMatrix(batch);
        setHudProjectionMatrix(r);
        
        if (Gdx.app.getType() == ApplicationType.Android) {
	        batch.end();
	        drawButtons(batch, r);
	        batch.begin();
	    }      
        drawStats(batch);
        drawFps(batch);
        //drawInfo(batch);
        drawCoords(batch);
        drawDebugMessage(debugMessage, batch);

        batch.end();
        
        //Stage UI drawing
        stage.act();
        stage.draw();
        
        glProfiler.reset();
    }

    void drawCorruptionMesh(SpriteBatch batch) {
    	TextureRegion corrReg = SpriteLoader.tileAtlas.findRegion("corr16");
		Texture corrTex = corrReg.getTexture();
		corrTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		/*int n = 0;
		
		int width = Base.CHUNK_AMOUNT;
		int height = Base.CHUNK_AMOUNT;
		
		int numberOfTiles = width * height;
		int numberOfVerticesPerTile = 4;
		int numberOfVerts = 5 * numberOfVerticesPerTile;
		
		//new float array with the maximum number of verts
		float verts[] = new float[numberOfTiles*numberOfVerts];
		
		for (int i = 0; i < height; i++) {
			for (int ii = 0; ii < width; ii++) {
				Chunk c = chunks.getChunk(0 + ii, 0 + i);
				
				if (c.getCreeperLevel() > 0) {
					final float x1 = c.getX();
					final float y1 = c.getY();
					
					final int cx = (int) (x1/c.getSize());
					final int cy = (int) (y1/c.getSize());
					
					final float x2 = x1 + corrReg.getRegionWidth() * 1;
					final float y2 = y1 + corrReg.getRegionHeight() * 1;
					
					if (cx >= Base.CHUNK_AMOUNT && cy >= Base.CHUNK_AMOUNT) {
						if (chunks.getChunk(cx + 1, cy).getCreeperLevel() == 0 && chunks.getChunk(cx, cy + 1).getCreeperLevel() == 0) {
							verts[n++] = x1;
							verts[n++] = y1;
							verts[n++] = 0;
							verts[n++] = 0;
							verts[n++] = 0;
				
							verts[n++] = x2;
							verts[n++] = y1;
							verts[n++] = 0;
							verts[n++] = 0;
							verts[n++] = 1;
				
							verts[n++] = x1;
							verts[n++] = y2;
							verts[n++] = 0;
							verts[n++] = 1;
							verts[n++] = 1;
						
							verts[n++] = x1;
							verts[n++] = y1;
							verts[n++] = 0;
							verts[n++] = 0;
							verts[n++] = 0;
						}
					} else {					
						verts[n++] = x1;
						verts[n++] = y1;
						verts[n++] = 0;
						verts[n++] = 0;
						verts[n++] = 0;
			
						verts[n++] = x1;
						verts[n++] = y2;
						verts[n++] = 0;
						verts[n++] = 0;
						verts[n++] = 1;
			
						verts[n++] = x2;
						verts[n++] = y2;
						verts[n++] = 0;
						verts[n++] = 1;
						verts[n++] = 1;
					
						verts[n++] = x2;
						verts[n++] = y1;
						verts[n++] = 0;
						verts[n++] = 0;
						verts[n++] = 0;
					}
				}
			}
		}
		*/
		Shaders.testShader.begin();
		Shaders.testShader.pedantic = false;
		//System.out.println("isCompiled: " + Shaders.testShader.isCompiled());
		//System.out.println("log: " + Shaders.testShader.getLog());
		Shaders.testShader.setUniformf("newColor", 0f, 1f, 0f, 0.8f);
		Shaders.testShader.end();

		//TODO: finalize
		float[] verts = generateCorruptionMesh();
		
		List<PolygonPoint> points = new ArrayList<PolygonPoint>();
		for (int i = 0; i < verts.length; i+=2) {
			points.add(new PolygonPoint(verts[i], verts[i+1]));
		}
	    		
		//Create the polygon passing a List of PolygonPoints
		org.poly2tri.geometry.polygon.Polygon polygon = new org.poly2tri.geometry.polygon.Polygon(points);
		//Here you could add holes as needed, passing them as Polygons
		//polygon.addHole(someHoleYouCreated);
		
		//Next, proceed to calculate the triangulation of the polygon 
		Poly2Tri.triangulate(polygon);
		//Finally, obtain the resulting triangles
		
		List<DelaunayTriangle> triangles = polygon.getTriangles();
		float[] tVerts = new float[triangles.size() * 6];
		
		//System.out.println("TRIANGLES: --");	
		
		int k = 0;
		for (DelaunayTriangle d : triangles) {
			for (TriangulationPoint p : d.points) {
				//System.out.println(p.getXf());		
				//System.out.println(p.getYf());	
				tVerts[k++] = p.getXf();
				tVerts[k++] = p.getYf();
			}
		}
		//System.out.println("-- END");
		
		
		int n = 0;
		int m = 0;	
		
		float[] glVerts = new float[((tVerts.length/2)*5)];
		//System.out.println("vert: size " + tVerts.length + " glV: " + ((tVerts.length/2)*5));
		for (int i = 0; i < glVerts.length ; i += 5) {
			glVerts[n++] = tVerts[m++];
			glVerts[n++] = tVerts[m++];
			glVerts[n++] = 0;
			glVerts[n++] = 0;
			glVerts[n++] = 0;
		}		
	
		batch.setShader(Shaders.testShader);
		batch.begin();	
		batch.draw(corrTex, glVerts, 0, glVerts.length);
		batch.end();
		batch.setShader(Shaders.defaultShader);
		
		System.out.println("\n");
		float[] lines = generateCorruptionMesh();
		for (float f : lines) {
			//System.out.println(f);
		}
    }
    
    float[] generateCorruptionMesh() {
    	//Generating border vertices (for every tile)
    	//array with the maximum size
    	float[] lines = new float[25*25*4*4];
    	
    	//line index
    	int li = 0;
    	
    	for(int y = 0; y < 25; y++) {
    		for(int x = 0; x < 25; x++) {   	    	
    	    	Chunk c = chunks.getChunk(Base.CHUNK_AMOUNT/2 + x, Base.CHUNK_AMOUNT/2 + y);
    	    	if (c.getCreeperLevel() > 0) {
    	    		final float x1 = c.getX();
					final float y1 = c.getY();
					
					final int cx = (int) (x1/c.getSize());
					final int cy = (int) (y1/c.getSize());
					
					final float x2 = x1 + c.getSize();
					final float y2 = y1 + c.getSize();
					
    	    		if (chunks.getChunk(cx + 1, cy).getCreeperLevel() == 0) {
    	    			//Create right vertices
    	    			lines[li++] = x2;
    	    			lines[li++] = y2;
    	    			lines[li++] = x2;
    	    			lines[li++] = y1;
    	    		} else {
    	    			lines[li++] = -1;
    	    			li += 3;
    	    		}
    	    		if (chunks.getChunk(cx - 1, cy).getCreeperLevel() == 0) {
    	    			//Create left vertices
    	    			lines[li++] = x1;
    	    			lines[li++] = y1;
    	    			lines[li++] = x1;
    	    			lines[li++] = y2;
    	    		} else {
    	    			lines[li++] = -1;
    	    			li += 3;
    	    		}
    	    		if (chunks.getChunk(cx, cy + 1).getCreeperLevel() == 0) {
    	    			//Create top vertices
    	    			lines[li++] = x1;
    	    			lines[li++] = y2;
    	    			lines[li++] = x2;
    	    			lines[li++] = y2;
    	    		} else {
    	    			lines[li++] = -1;
    	    			li += 3;
    	    		}	
    	    		if (chunks.getChunk(cx, cy - 1).getCreeperLevel() == 0) {
    	    			//Create bottom vertices
    	    			lines[li++] = x2;
    	    			lines[li++] = y1;
    	    			lines[li++] = x1;
    	    			lines[li++] = y1;
    	    		} else {
    	    			lines[li++] = -1;
    	    			li += 3;
    	    		}
    	    	}
    		}
    	}
    	
    	float[] verts = new float[25*25*4*4];
    	int index = 0;
    	
    	boolean first = false;
    	
    	//Constructing mesh vertices
    	for (int i = 0; i < li; i += 4) {
    		//x1 lines[i]
    		//y1 lines[i+1]
    		//x2 lines[i+2]
    		//y2 lines[i+3]
    		
    		//Skipping 4 steps
    		if (lines[i] == -1) {
    			continue;
    		}
    		
    		if (first == false) { 			
	    		verts[index++] = lines[i];
	    		verts[index++] = lines[i+1];
	    		verts[index++] = lines[i+2];
	    		verts[index++] = lines[i+3];
    		
	    		lines[i] = -1;
    		    lines[i+1] = -1; 
    		    lines[i+2] = -1; 
    		    lines[i+3] = -1; 
    		    first = true;
    		}
    		
    		for (int e = 0; e < lines.length; e += 4) {
    			if (lines[e] != -1) {
    				if (verts[index - 2] == lines[e] && verts[index - 1] == lines[e + 1]) {
    					//verts[index++] = lines[e]; 
    					//verts[index++] = lines[e+1]; 
    					verts[index++] = lines[e+2]; 
    					verts[index++] = lines[e+3]; 
    					
    					lines[e] = -1;
    	    		    lines[e+1] = -1; 
    	    		    lines[e+2] = -1; 
    	    		    lines[e+3] = -1; 
    				}
    			}
    		}
    	}
    	
    	float finalVerts[] = new float[index];
    	int k = 0;
    	for(float f : verts) {
    		if (f != 0) {
    			finalVerts[k++] = f;
    		}
    	}
    	
    	return finalVerts;
    }
    
    void drawBackgroundSand(SpriteBatch batch) {
		Texture sandTex = new Texture(SpriteLoader.savanaTex.getTextureData());
		sandTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		int n = 0;
		
		int width = 1;
		int height = 1;
		
		int numberOfTiles = width * height;
		int numberOfVerticesPerTile = 4;
		int numberOfVerts = 5 * numberOfVerticesPerTile;
		
		float verts[] = new float[numberOfTiles*numberOfVerts];
		
		for (int i = 0; i < height; i++) {
			for (int ii = 0; ii < width; ii++) {
				float x1 = 0;
				float y1 = 0;
				
				float x2 = x1 + Base.WORLD_SIZE;
				float y2 = y1 + Base.WORLD_SIZE;
				
				verts[n++] = x1;
				verts[n++] = y1;
				verts[n++] = 0;
				verts[n++] = 0;
				verts[n++] = 0;
	
				verts[n++] = x1;
				verts[n++] = y2;
				verts[n++] = 0;
				verts[n++] = 0;
				verts[n++] = Base.WORLD_SIZE/32;
	
				verts[n++] = x2;
				verts[n++] = y2;
				verts[n++] = 0;
				verts[n++] = Base.WORLD_SIZE/32;
				verts[n++] = Base.WORLD_SIZE/32;
			
				verts[n++] = x2;
				verts[n++] = y1;
				verts[n++] = 0;
				verts[n++] = Base.WORLD_SIZE/32;
				verts[n++] = 0;
				
			}
		}
		
		batch.setShader(Shaders.defaultShader);
		//batch.begin();	
		batch.draw(sandTex, verts, 0, numberOfTiles*numberOfVerts);
		//batch.end();		
    }
    
    void drawConnectors(ShapeRenderer sR) {

    	r.begin(ShapeType.Filled);
        r.setColor(Color.WHITE);
        //ConnectMode line
        if ((toggleConnectMode == true && activeNewConnection == true) || draggingConnection == true) {
            Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
            r.rectLine(nodelist.get(newConnectionFromIndex).getCX(), nodelist.get(newConnectionFromIndex).getCY(), worldPos.x, worldPos.y, Base.lineWidth);
        }
        r.end();
        
        if(!nodelist.isEmpty()) {
            connectorHandler.drawAll(sR);
        }
    }

    void drawPrefab(ShapeRenderer sR) {
    	Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
    	builtBuilding.drawPrefab(r, worldPos.x, worldPos.y, true);
    }
    
    void drawStats(SpriteBatch batch) {
        float textheight = font.getCapHeight();
        String stat = "Nodes: " + nodelist.size();
        String stat2 = "Packages: " + packagelist.size();
        String stat3 = "Connectors: " + connectorHandler.getAllConnectors().size();
        String stat41 = "Buildings: " + buildingHandler.getAllBuildings().size(); 
        String stat4 = "PackagePaths: " + packageHandler.getNumberOfPaths();
        String stat5 = "ConnectMode: " + toggleConnectMode;
        String stat6 = "activeNewConnection: " + activeNewConnection;
        String stat7 = "selectedID: " + selectedID;
        String stat8 = "selectedType: " + selectedType;
        String stat9 = "selectedIndex: " + selectedIndex;  
        String stat10 = "zoom: " + cam.zoom;
        String stat11 = "draggingConnection: " + draggingConnection;
        String stat12 = "newConnectionFromIndex: " + newConnectionFromIndex;   

        layout.setText(font, stat);
        float textstatwidth = layout.width;
        layout.setText(font, stat2);
        float textstat2width = layout.width;
        layout.setText(font, stat3);
        float textstat3width = layout.width;
        layout.setText(font, stat41);
        float textstat41width = layout.width;
        layout.setText(font, stat4);
        float textstat4width = layout.width;
        layout.setText(font, stat5);
        float textstat5width = layout.width;
        layout.setText(font, stat6);
        float textstat6width = layout.width;
        layout.setText(font, stat7);
        float textstat7width = layout.width;
        layout.setText(font, stat8);
        float textstat8width = layout.width;
        layout.setText(font, stat9);
        float textstat9width = layout.width;
        layout.setText(font, stat10);
        float textstat10width = layout.width;
        layout.setText(font, stat11);
        float textstat11width = layout.width;
        layout.setText(font, stat12);
        float textstat12width = layout.width;

        font.draw(batch, stat, WIDTH - textstatwidth - 10, HEIGHT - textheight);
        font.draw(batch, stat2, WIDTH - textstat2width - 10, HEIGHT - (textheight+2)*2);
        font.draw(batch, stat3, WIDTH - textstat3width - 10, HEIGHT - (textheight+2)*3);
        font.draw(batch, stat41, WIDTH - textstat41width - 10, HEIGHT - (textheight+2)*4);
        font.draw(batch, stat4, WIDTH - textstat4width - 10, HEIGHT - (textheight+2)*5);
        font.draw(batch, stat5, WIDTH - textstat5width - 10, HEIGHT - (textheight+2)*6);
        font.draw(batch, stat6, WIDTH - textstat6width - 10, HEIGHT - (textheight+2)*7);
        font.draw(batch, stat7, WIDTH - textstat7width - 10, HEIGHT - (textheight+2)*8);
        font.draw(batch, stat8, WIDTH - textstat8width - 10, HEIGHT - (textheight+2)*9);
        font.draw(batch, stat9, WIDTH - textstat9width - 10, HEIGHT - (textheight+2)*10);
        font.draw(batch, stat10, WIDTH - textstat10width - 10, HEIGHT - (textheight+2)*11);
        font.draw(batch, stat11, WIDTH - textstat11width - 10, HEIGHT - (textheight+2)*12);
        font.draw(batch, stat12, WIDTH - textstat12width - 10, HEIGHT - (textheight+2)*13);

    }

    void drawFps(SpriteBatch batch) {
        float textheight = font.getCapHeight();

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        
        layout.setText(font, "FPS: " + Gdx.graphics.getFramesPerSecond());
        float textwidth = layout.width;
        layout.setText(font, "sFPS: " + sfps);
        float text2width = layout.width;
        layout.setText(font, "simFac: " + df.format(simFac));
        float text3width = layout.width;
        
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond() , 5, HEIGHT - textheight + 2);
        font.draw(batch, "sFPS: " + sfps , 5 + 5 + textwidth, HEIGHT - textheight + 2);
        font.draw(batch, "simFac: " + df.format(simFac) , 5, HEIGHT - textheight*2 - 2);
        font.draw(batch, "simTick: " + SimulationThread.simTick , 5 + 5 + text3width, HEIGHT - textheight*2 - 2);
    }

    void drawDebugMessage(String message, SpriteBatch batch) {
        float textheight = font.getCapHeight();

        layout.setText(font, message);
        float textwidth = layout.width;

        font.draw(batch, message, WIDTH/2 - textwidth/2, textheight + 5);
    }

    void drawInfo(SpriteBatch batch) {
        float textheight = font.getCapHeight();
        font.setColor(Color.YELLOW);

        switch(selectedType) {
            case NODE:
                if(selectedID != -1) {
                    for(Node n : nodelist) {
                        if(n.getID() == selectedID) {
                            Vector3 v = cam.project(new Vector3(n.getX() + n.radius, n.getY() + n.radius, 0));

                            double x2 = v.x;
                            double x = x2 + 10;
                            double y = v.y;

                            font.draw(batch, "ID: " + n.getID(), (int)x, (int)y);
                            font.draw(batch, "Index: " + nodelist.indexOf(n), (int)x, (int)y - textheight);
                            font.draw(batch, "Node X: " + n.getX(), (int)x, (int)y - textheight*2);
                            font.draw(batch, "Node Y: " + n.getY(), (int)x, (int)y - textheight*3);
                            font.draw(batch, "Radius: " + n.radius, (int)x, (int)y - textheight*4);
                            font.draw(batch, "Connections: " + n.getNumberOfConnections(), (int)x, (int)y - textheight*5);
                            font.draw(batch, "Connectable: " + n.connectable, (int)x, (int)y - textheight*6);
                            font.draw(batch, "Connected To: " + Base.nodeListToString(n.connected_to), (int)x, (int)y - textheight*7);
                            font.draw(batch, "Connected By: " + Base.nodeListToString(n.connected_by), (int)x, (int)y - textheight*8);
                            font.draw(batch, "Connectors: " + Base.nodeConnectorListToString(n.connectors), (int)x, (int)y - textheight*9);
                            font.draw(batch, "Closed: " + n.isClosed(), (int)x, (int)y - textheight*10);
                        }
                    }
                }             
                break;
            case CONNECTOR:
                for (Connector nC : connectorHandler.getAllConnectors()) {
                    if (nC.getID() == selectedID) {
                        double x1, y1, x2, y2;
                        x1 = nC.getFrom().getCX();
                        x2 = nC.getTo().getCX();
                        y1 = nC.getFrom().getCY();
                        y2 = nC.getTo().getCY();

                        double vX = (x2 - x1) / 2;
                        double vY = (y2 - y1) / 2;

                        float fX = (float) (x1 + vX);
                        float fY = (float) (y1 + vY);

                        Vector3 v = cam.project(new Vector3(fX, fY, 0));

                        font.draw(batch, "cID: " + nC.getID(), v.x + 5, v.y);
                        font.draw(batch, "pN: " + nC.getPackages().size(), v.x + 5, v.y - textheight);
                    }
                }
            case PACKAGE:
                for (Package p : packagelist) {
                    if (p.getID() == selectedID) {
                        Vector3 v = cam.project(new Vector3(p.getX(), p.getY(), 0));

                        double x = v.x;
                        double y = v.y;

                        font.draw(batch, "ID: " + p.getID(), (int)x + 35, (int)y + (textheight+2) + 5);
                        if (p instanceof Coal) font.draw(batch, "COAL", (int)x + 35, (int)y + 5);
                        if (p instanceof Iron) font.draw(batch, "IRON", (int)x + 35, (int)y + 5);
                        font.draw(batch, "Percentage: " + p.percentage + " %", (int)x + 35, (int)y - (textheight+2)*1 + 5);
                        font.draw(batch, "Going: " + p.going, (int)x + 35, (int)y - (textheight+2)*2 + 5);
                        font.draw(batch, "Finished: " + p.finished, (int)x + 35, (int)y - (textheight+2)*3 + 5);
                        font.draw(batch, "From: " + p.from.getID(), (int)x + 35, (int)y - (textheight+2)*4 + 5);
                        font.draw(batch, "To: " + p.to.getID(), (int)x + 35, (int)y - (textheight+2)*5 + 5);
                    }
                }
            default: break;
        }

        font.setColor(Color.WHITE);
    }

    void drawCoords(SpriteBatch batch) {
        float textheight = font.getCapHeight();
        Vector3 v = cam.unproject(new Vector3(mouseX, mouseY, 0));

        layout.setText(font, "x: " + v.x + " y: " + v.y);
        float textwidth = layout.width;

        font.draw(batch, "x: " + v.x + " y: " + v.y, WIDTH - textwidth - 5, textheight + 5);
    }

    void drawButtons(SpriteBatch batch, ShapeRenderer r) {
        r.begin(ShapeRenderer.ShapeType.Filled);
        r.setColor(Color.LIGHT_GRAY);
        r.rect(backButton.x, backButton.y, backButton.width, backButton.height);
        r.rect(deleteButton.x, deleteButton.y, deleteButton.width, deleteButton.height);
        if (oldNodeBuildMode == true) r.setColor(Color.GREEN);
        r.rect(buildButton.x, buildButton.y, buildButton.width, buildButton.height);
        r.setColor(Color.WHITE);
        r.end();

        batch.begin();
        //Back
        layout.setText(font, "Back");
        float textwidth = layout.width;
        float textheight = font.getCapHeight();

        //Delete
        layout.setText(font, "Delete");
        float textwidth1 = layout.width;
        float textheight1 = font.getCapHeight();

        //Build
        layout.setText(font, "Build");
        float textwidth2 = layout.width;
        float textheight2 = font.getCapHeight();

        font.setColor(Color.RED);
        font.draw(batch, "Back", backButton.x + backButton.width/2 - textwidth/2, backButton.y + backButton.height/2 + textheight/2);
        font.draw(batch, "Delete", deleteButton.x + deleteButton.width/2 - textwidth1/2, deleteButton.y + deleteButton.height/2 + textheight/2);
        font.draw(batch, "Build", buildButton.x + buildButton.width/2 - textwidth2/2, buildButton.y + buildButton.height/2 + textheight/2);
        font.setColor(Color.WHITE);
        batch.end();
    }

    void setHudProjectionMatrix(SpriteBatch batch) {
        Matrix4 uiMatrix = cam.combined.cpy();
        uiMatrix.setToOrtho2D(0, 0, WIDTH, HEIGHT);
        batch.setProjectionMatrix(uiMatrix);
    }

    void setHudProjectionMatrix(ShapeRenderer sR) {
        Matrix4 uiMatrix = cam.combined.cpy();
        uiMatrix.setToOrtho2D(0, 0, WIDTH, HEIGHT);
        sR.setProjectionMatrix(uiMatrix);
    }

    Matrix4 getHudProjectionMatrix() {
        Matrix4 uiMatrix = cam.combined.cpy();
        uiMatrix.setToOrtho2D(0, 0, WIDTH, HEIGHT);
        return uiMatrix;
    }
    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
            cam.zoom += 0.08;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            cam.zoom -= 0.08;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)) {
        	int prevTick = SimulationThread.TICKS_PER_SECOND;
        	if (prevTick != Integer.MAX_VALUE - 1) SimulationThread.recalculateSpeed(++prevTick);
        	System.out.println("Increasing simSpeed! (+) current tick rate: " + SimulationThread.TICKS_PER_SECOND);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) {
        	int prevTick = SimulationThread.TICKS_PER_SECOND;
        	if (prevTick != 1) {SimulationThread.recalculateSpeed(--prevTick);}
        	System.out.println("Decreasing simSpeed! (-) current tick rate: " + SimulationThread.TICKS_PER_SECOND);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_5)) {
        	SimulationThread.recalculateSpeed(30);
        	System.out.println("Resetting simSpeed! (reset) current tick rate: " + SimulationThread.TICKS_PER_SECOND);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            cam.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            cam.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            cam.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            cam.translate(0, 3, 0);
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
        	lastZoom = cam.zoom;
        	lastCamePos = cam.position;
        	nodeshotGame.setScreen(new MenuScreen(nodeshotGame));
        }

        //Zoom clamping, min max
        cam.zoom = MathUtils.clamp(cam.zoom, 0.2f, Base.WORLD_SIZE/cam.viewportWidth - 3f);

        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        //Making sure the camera doesnt go beyond the world limit
        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, Base.WORLD_SIZE - effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, Base.WORLD_SIZE - effectiveViewportHeight / 2f);
        
        chunks.updateCam(cam);
    }

    public static Entity checkHighlights(boolean select) {
        Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
        Rectangle rect = new Rectangle(worldPos.x, worldPos.y, 1, 1);;
        
        Entity highlightedEntity;
        
        highlightedEntity = checkNodes(rect, worldPos, select);
        if (highlightedEntity == null) {
        	highlightedEntity = checkPackages(rect, worldPos, select);
        	if (highlightedEntity == null) {
        		highlightedEntity = checkConnectors(rect, worldPos, select);
        		if (highlightedEntity == null) {
        			return null;
        		}
        	}
        }
        
        return highlightedEntity;
    }

    static Node checkNodes(Rectangle rect, Vector3 worldPos, boolean select) {
    	Boolean nodeIntersected = false;
    	Node intersectedNode = null;
        if ((nodelist.size() > 0)) {
            for(int i = 0; i < nodelist.size(); i++) {
                Node n = nodelist.get(i);
                if(n.getBoundingRectangle().overlaps(rect)) {
                	if (select) Selector.selectNode(n);
                    nodeIntersected = true;
                    intersectedNode = n;
                    break;
                }
            }
            if (!nodeIntersected) {
                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                	if (select) Selector.deselect();
                }
            }
        }
        return intersectedNode;
    }
    static Package checkPackages(Rectangle rect, Vector3 worldPos, boolean select) {
    	Boolean packageIntersected = false;
    	Package intersectedPackage = null;
    	if ((packagelist.size() > 0)) {
            for(int i = 0; i < packagelist.size(); i++) {
                Package p = packagelist.get(i);
                if(Intersector.intersectRectangles(new Rectangle((float)(p.x + (p.radius*0.2)) , (float)(p.y + (p.radius*0.2)), (float)(p.radius*0.6), (float)(p.radius*0.6)), new Rectangle(worldPos.x, worldPos.y, 1,1), new Rectangle()) == true) {
                	if (select) Selector.selectPackage(p);
                    packageIntersected = true;
                    intersectedPackage = p;
                    break;
                }
            }
            if (!packageIntersected) {             
                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                	if (select) Selector.deselect();
                }
            }
        }
    	return intersectedPackage;
    }
    static Connector checkConnectors(Rectangle rect, Vector3 worldPos, boolean select) {
        Boolean connectorIntersected = false;
        Connector intersectedConnector = null;
        for (int i = 0; i < connectorHandler.getAllConnectors().size(); i++) {
            Node n1 = connectorHandler.getAllConnectors().get(i).getFrom();
            Node n2 = connectorHandler.getAllConnectors().get(i).getTo();

            float x1 = n1.getCX();
            float y1 = n1.getCY();
            float x2 = n2.getCX();
            float y2 = n2.getCY();

            Polygon p = new Polygon(new float[] {
                    rect.getX(),
                    rect.getY(),
                    (rect.getX()+8f),
                    rect.getY(),
                    (rect.getX()+8f),
                    (rect.getY()+8f),
                    rect.getX(),
                    (rect.getY()+8f)
            });

            if (Intersector.distanceSegmentPoint(x1, y1, x2, y2, worldPos.x, worldPos.y) <= 3)
            {
                if (select) Selector.selectNodeConnector(connectorHandler.getAllConnectors().get(i));
               
                connectorIntersected = true;
                intersectedConnector = connectorHandler.getAllConnectors().get(i);
                break;
            }
        }

        if (!connectorIntersected) {
            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            	if (select) Selector.deselect();
            }
        }
        
        return intersectedConnector;
    }
   
    public static void startSim() {
        //Simulation thread
        SimulationThread sT = new SimulationThread();      
        Thread simulationThread = new Thread(sT);
        simulationThread.setDaemon(true);
        simulationThread.start();  
    }
    
    @Override
    public void resize(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        
        cam.viewportWidth = viewportWidth;
        cam.viewportHeight = viewportWidth * height/width;
        cam.update();

        stageViewport = new FillViewport(width, height);
        stage.setViewport(stageViewport);
        stage.getViewport().update(width, height, true);
        
        buildMenu.resize();
        hudMenu.resize();
    }

    @Override
    public void dispose () {
        batch.dispose();
        //img.dispose();
        r.dispose();
        fboA.dispose();
		blurBuffer.dispose();
    }  
}