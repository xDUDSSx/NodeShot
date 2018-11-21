package org.dudss.nodeshot.screens;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.buildings.Building;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.nodes.InputNode;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.inputs.DesktopInputProcessor;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.misc.BuildingHandler;
import org.dudss.nodeshot.misc.BulletHandler;
import org.dudss.nodeshot.misc.ConnectorHandler;
import org.dudss.nodeshot.misc.PackageHandler;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.Chunks;
import org.dudss.nodeshot.terrain.Section;
import org.dudss.nodeshot.ui.BuildMenu;
import org.dudss.nodeshot.ui.HudMenu;
import org.dudss.nodeshot.ui.PauseMenu;
import org.dudss.nodeshot.ui.RightClickMenuManager;
import org.dudss.nodeshot.utils.Selector;
import org.dudss.nodeshot.utils.Shaders;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
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
import com.kotcrab.vis.ui.VisUI;

/**The main game screen*/
public class GameScreen implements Screen {

    public static Game nodeshotGame;

    //new variables
    public static int WIDTH;
    public static int HEIGHT;
    public static float aspectRatio;

	public static Logger LOGGER = Logger.getLogger(GameScreen.class.getSimpleName());
    
    public static boolean startedOnce = false;
    
	public static long currentSimTimeTick;
	public static long nextSimTimeTick;

	public static SimulationThread simulationThread;
	
	public static int sfps;
	public static int simFrameCount;
	public static double simFac;
	public static boolean gamePaused = false;
	
	public static Vector2 mousePos = new Vector2();

	//Collections
	public static CopyOnWriteArrayList<Node> nodelist = new CopyOnWriteArrayList<Node>();
	public static CopyOnWriteArrayList<Package> packagelist = new CopyOnWriteArrayList<Package>();

	//Handlers
	public static PackageHandler packageHandler;
	
	//Handlers with internal collections
	public static ConnectorHandler connectorHandler;
	public static BuildingHandler buildingHandler;
	public static BulletHandler bulletHandler;

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

	public static Boolean debug = false;
	static Boolean drawString = false;
	static String stringToWrite = "";

	//Terrain
	public static Chunks chunks;
	public static Chunk hoverChunk = null;
	public static int terrainLayerSelected = 2;
	public static int terrainBrushSize = 2;
	
	public static float viewportWidth = 312f;
    FreeTypeFontGenerator generator;
	
	public static boolean buildMode = false;
	public static Building builtBuilding = null;
	public static Node builtConnector = null;
	
    //libGDX
    static SpriteBatch batch;
    Texture img;
    ShapeRenderer r;
    
    public static BitmapFont font;
    public static BitmapFont fontLarge;
    GlyphLayout layout;

    public static OrthographicCamera cam;
    public static Vector3 lastCamPos;
    public static float lastZoom;
    public static Boolean zooming = false;
    //Background shader helper vars
    public static float off = 0.5f;
    public static Vector3 prevCameraOffset;
    
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
    public static PauseMenu pauseMenu;
    
    public static GLProfiler glProfiler;
    
    public static List<FrameBuffer> corrBuffers;
    
    public static FrameBuffer terrainBuffer;
    public static FrameBuffer blurBuffer;
    
    /**The main game screen
     * @param game The {@link Game} object that is passed on {@link Screen} switches.*/
    public GameScreen(Game game)
    {
        nodeshotGame = game;
        glProfiler = new GLProfiler(Gdx.graphics);
        glProfiler.enable();
        packageHandler = new PackageHandler();
        connectorHandler = new ConnectorHandler();
        buildingHandler = new BuildingHandler();       
        bulletHandler = new BulletHandler();  
        chunks = new Chunks();        
        rightClickMenuManager = new RightClickMenuManager();             
        
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        aspectRatio = (float)WIDTH/(float)HEIGHT;
        
        //Loading item sprites
        Shaders.load();
        SpriteLoader.loadAll();
        VisUI.load();
        
        //LineWidth
        Gdx.gl.glLineWidth(2);
        
        //Cam
        cam = new OrthographicCamera(viewportWidth , viewportWidth * (HEIGHT / WIDTH));
        if (lastCamPos == null) {
            cam.position.set(Base.WORLD_SIZE / 2f, Base.WORLD_SIZE / 2f, 0);
            cam.zoom = 3f;           
        } else {
            cam.position.set(lastCamPos);
            cam.zoom = lastZoom;
        }
        prevCameraOffset = new Vector3(cam.position);
        cam.update();
        lastCamPos = cam.position;
        lastZoom = cam.zoom;
        
        //Initializes chunks and sections
        chunks.create();
        //Generate terrain if not generated already
        if (chunks.generated == false) {       	
        	chunks.generateAll();
        }       
        //Updates the view
        chunks.updateView(cam);
        
        blurBuffer = new FrameBuffer(Format.RGBA8888, WIDTH, HEIGHT, false);
		
		corrBuffers = new ArrayList<FrameBuffer>();
		for (int i = 0; i < Base.MAX_CREEP; i++) {
			corrBuffers.add(new FrameBuffer(Format.RGBA8888, WIDTH, HEIGHT, false));
		}
		
        batch = new SpriteBatch();
        r = new ShapeRenderer();
        
        //font generation
        font = new BitmapFont();
        fontLarge = new BitmapFont();
        layout = new GlyphLayout();
        generator = new FreeTypeFontGenerator(Gdx.files.classpath("res/data/Helvetica-Regular.ttf"));
        
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Base.HUD_FONT_SIZE;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!'()>?:%+-*/";        
        font = generator.generateFont(parameter);
        
        FreeTypeFontGenerator.FreeTypeFontParameter parameterLarge = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterLarge.size = Base.HUD_FONT_LARGE_SIZE;
        parameterLarge.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!'()>?:%+-*/";     
        fontLarge = generator.generateFont(parameterLarge);
        generator.dispose();
       
        //User Interface             
        if (Gdx.app.getType() == ApplicationType.Android) {
        	atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        	skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	atlas = new TextureAtlas("res/data/uiskin.atlas");
        	skin = new Skin(Gdx.files.classpath("res/data/uiskin.json"), atlas);
        }
        stageViewport = new StretchViewport(WIDTH, HEIGHT);
        stage = new Stage(stageViewport);         
       
        buildMenu = new BuildMenu("Build menu", skin);    
        stage.addActor(buildMenu);
                
        hudMenu = new HudMenu("HUD menu", skin);
        //stage.addActor(hudMenu);
        
        pauseMenu = new PauseMenu(false);
        stage.addActor(pauseMenu);
        
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
    }

    public static int getWidth() {return WIDTH;}
    public static int getHeight() {return HEIGHT;}

    @Override
    public void show() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        aspectRatio = (float)WIDTH/(float)HEIGHT;
                       
        Shaders.blurShader.begin();
        Shaders.blurShader.setUniformf("resolution", cam.zoom * 100);
        Shaders.blurShader.end();
		       
        //Ingame HUD buttons (Android) (//TODO: remove, utilize proper scene UI) //Kinda finished, not for mobile tho
        if (Gdx.app.getType() == ApplicationType.Android) {
	        backButton.set( 10, 10, 180, 180);
	        deleteButton.set( 10 , (200)*1 + 10, 180, 180);
	        buildButton.set( 10, (200)*2 + 10, 180, 180);
        }
        
        //Input processors
        InputMultiplexer multiplexer = new InputMultiplexer();       
        multiplexer.addProcessor(stage);
        
        DesktopInputProcessor dip = new DesktopInputProcessor();
    	multiplexer.addProcessor(dip);
        
        //Android not supported anymore
        /*if (Gdx.app.getType() == ApplicationType.Android) {
        	MobileGestureListener mgl = new MobileGestureListener();
        	multiplexer.addProcessor(new GestureDetector(mgl));
        } else {
        	DesktopInputProcessor dip = new DesktopInputProcessor();
        	multiplexer.addProcessor(dip);
        }  
        */     	
        Gdx.input.setInputProcessor(multiplexer); 
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

    @Override
    public void render (float delta) {
        handleInput();
        //hudMenu.update();
        cam.update();	
                
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
     
        //OpenGL performance logging
        glProfiler.reset();        
		
        //Background cloud rendering
        drawBackgroundClouds(batch);
 		
        batch.setProjectionMatrix(cam.combined);
        r.setProjectionMatrix(cam.combined);
        
        //Rendering the terrain
        chunks.drawTerrain();
        
		if (buildMode == true) {
			batch.begin();    	        
	        for (Section s : chunks.sectionsInView) {	   
	        	Chunk c = s.getChunk(0, 0);	        	
	        	batch.draw(SpriteLoader.gridOverlay2, c.getX() , c.getY(), 256, 256);	
	        }
	        batch.end();
		}
        
		//OpenGL performance logging
        if (Base.enableGlProgilerLogging) {
        	LOGGER.info(
        	"\n\nDraw calls: " + glProfiler.getDrawCalls() + 
			"\nCalls: " + glProfiler.getCalls() +
			"\nTexture binding " + glProfiler.getTextureBindings() + 
			"\nShaderSwitches: " + glProfiler.getShaderSwitches()
        	);
        }
        
        r.setAutoShapeType(true);      
        if (buildMode == true) {
	        r.begin(ShapeType.Filled);
	        r.setColor(Color.WHITE);
	        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
	        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	    
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
	        r.end();
	    }
        
        //Shows terrain edges in blue
        if (Base.drawTerrainEdges) {
	        r.begin(ShapeType.Line);
	        r.setColor(Color.WHITE);
	        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
	        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	    
	        		if (chunks.getChunk(x, y).isEdge() == true) {       			
	        			Color c = new Color(Color.rgba8888(0/255f, 0/255f, 255/255f, 1.0f));
	        			r.setColor(c);
	        			r.rect((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE), Base.CHUNK_SIZE, Base.CHUNK_SIZE);
	        		}	      
	        	}
	        }
	        r.end();
        }
         
 
        r.begin(ShapeType.Filled);
        buildingHandler.drawAll(r, batch);
        r.end();
        
        //Highlight of the chunk the mouse is hovering on
        if (hoverChunk != null) {
        	if (buildMode == true && builtConnector != null) {
        		r.begin(ShapeType.Filled);
	        	r.setColor(Color.WHITE);
	        	r.rect(hoverChunk.getX(), hoverChunk.getY(), hoverChunk.getSize(), hoverChunk.getSize());
	        	r.end();        	
        	} else if (Base.hoverChunkHighlight) {
        		r.begin(ShapeType.Line);
	        	r.setColor(Color.WHITE);
	        	r.rect(hoverChunk.getX(), hoverChunk.getY(), hoverChunk.getSize(), hoverChunk.getSize());
	        	r.end();
        	}
        }
      
        //Connector rendering
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
        //Drawing nodes & highlights
        for (int i = 0; i < nodelist.size(); i++) {
            Node n = nodelist.get(i);  
            if (n instanceof OutputNode || n instanceof InputNode) {
            	n.setScale(0.8f);
            } else {
            	n.setScale(0.45f);
            	n.draw(batch);
            }
   
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
        
        //Drawing packages
        for(int i = 0; i < packagelist.size(); i++) {
            Package p = packagelist.get(i);
            
            if (selectedID == p.getID()) {
            	packagelist.get(selectedIndex).drawHighlight(batch);
            }          
            
            p.draw(batch);
        }           
        batch.end();
        
        //Drawing all the corruption layers
        //Corruption is currently drawn as individual mesh layers
        for(int i = 0; i < Base.MAX_CREEP; i++) {
        	chunks.drawCorruption(i);
        }
        
        bulletHandler.drawAll(r, batch);
        
        //Draw debug infographics
        if (debug) drawDebug(batch, r);
           
        //HUD, draw last
        //setting screen matrix    
        setHudProjectionMatrix(batch);
        setHudProjectionMatrix(r); 
        
        batch.begin();
        if (Gdx.app.getType() == ApplicationType.Android) {
	        batch.end();
	        drawButtons(batch, r);
	        batch.begin();
	    }      
        if (Base.drawGeneralStats) drawStats(batch);
        drawFps(batch);
        //drawInfo(batch);
        drawCoords(batch);
        drawTerrainInfo(batch);
        if (gamePaused) {
	        batch.setColor(Color.RED);
	        drawPauseIndicator(batch);
	        batch.setColor(Color.WHITE);
        }
        
        batch.end();
        
        //Stage UI drawing
        stage.act();
        stage.draw();
        
        glProfiler.reset();
    }   
    
    public static void drawDebug(SpriteBatch batch, ShapeRenderer r) {
    	batch.begin();    	        
        for (Section s : chunks.sectionsInView) {	   
        	Chunk c = s.getChunk(0, 0);	        	
        	batch.draw(SpriteLoader.sectionOutline, c.getX() , c.getY(), 256, 256);	
        	//font.draw(batch, s.mesh, x, y)
        }      
        batch.end();   

        r.begin(ShapeType.Filled);
        r.setColor(Color.WHITE);
        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	    
        		if (chunks.getChunk(x, y).getCreeperLevel() > 0) {
        			//float n = Base.range(chunks.getChunk(x, y).getCreeperLevel(), 0, 5, 0, 1) * 100;
        			float n = chunks.getChunk(x, y).getCreeperLevel() * 100;
        			
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
        r.end();
    }
    
    //Shader related rendering methods
    public static void blurBuffer(FrameBuffer fboA, FrameBuffer fboB, Texture texture, float x, float y) {
    	fboB.begin();
    	Shaders.blurShader.begin();
    	Shaders.blurShader.setUniformf("dir", 1.0f, 0.0f);
    	Shaders.blurShader.setUniformf("radius", 0.0f);
        Shaders.blurShader.setUniformf("resolution", (cam.zoom * 200) * aspectRatio);
    	Shaders.blurShader.end();
		batch.setShader(Shaders.blurShader);   	
				
		Sprite s = new Sprite(texture);
		Matrix4 m = new Matrix4();
		m.setToOrtho2D(0, 0, fboA.getWidth(), fboA.getHeight());
		batch.setProjectionMatrix(m);	

		s.flip(false, false);
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		s.draw(batch);
		batch.end();
		fboB.end();
		
		Shaders.blurShader.begin();
    	Shaders.blurShader.setUniformf("dir", 0.0f, 1.0f);
    	Shaders.blurShader.setUniformf("radius", 0.0f);
    	Shaders.blurShader.setUniformf("resolution", cam.zoom * 200);
    	Shaders.blurShader.end();
		  	
    	batch.setShader(Shaders.blurShader);   	
		s = new Sprite(fboB.getColorBufferTexture());
		
		m.setToOrtho2D(0, 0, fboB.getWidth(), fboB.getHeight());		
		batch.setProjectionMatrix(m);
		
		s.flip(false, false);
		batch.begin();
		s.draw(batch);
		batch.end();
		
		batch.setProjectionMatrix(cam.combined);
		batch.setShader(Shaders.defaultShader);
	}   
    
    public void drawBackgroundClouds(SpriteBatch batch) {
        //Getting the time this application has run for, the is fed to the cloud shader and makes it "animated"
    	float secondsSinceStartup = ((System.currentTimeMillis() - BaseClass.startTime) / 1000f);
    	
    	//Creating a basic screen matrix
        Matrix4 uiMatrix = cam.combined.cpy();
        uiMatrix.setToOrtho2D(0, 0, WIDTH, HEIGHT);
        
        //Mesh vertexes that correspond to a single quad covering the screen
        float[] verts = new float[] {0, 0, Color.toFloatBits(1f, 0, 0, 1f), 0, 0, WIDTH, 0, Color.toFloatBits(1f, 0, 0, 1f), 1, 0, WIDTH, HEIGHT, Color.toFloatBits(1f, 0, 0, 1f), 1, 1, 0, HEIGHT, Color.toFloatBits(1f, 0, 0, 1f), 0, 1};        
        //cloud scale that is normalized to some predefined values
        float clampedScale = Base.range(cam.zoom*0.5f, Base.MIN_ZOOM, Base.WORLD_SIZE/cam.viewportWidth, 2f, 4f);
        
        //Setting the shader uniforms
 		Shaders.solidCloudShader.begin();
 		//Flag indicating that the shader does not need all the uniforms that are expected by LibGdx (eg. texture, which is not used)
 		Shaders.solidCloudShader.pedantic = false;
 		//Setting the projection matrix
 		Shaders.solidCloudShader.setUniformMatrix("u_projTrans", uiMatrix);
 		//Setting the time uniform
 		Shaders.solidCloudShader.setUniformf("time", secondsSinceStartup);
 		//Setting the cloud scale
 		Shaders.solidCloudShader.setUniformf("zoom", clampedScale);
 		//Screen resolution
 		Shaders.solidCloudShader.setUniformf("resolution", WIDTH, HEIGHT);
 		//Offset from the original coordinates used to make the clouds scale up/down from the center of the screen rather than bottom right corner
 		Shaders.solidCloudShader.setUniformf("offset", clampedScale / 2f);
 		//Offset vector that reacts to the in-game camera position, making it look like the clouds are part of the scene
 		Shaders.solidCloudShader.setUniformf("pos", cam.position.x, cam.position.y);
 		Shaders.solidCloudShader.end(); 		        
        
        batch.begin();
        batch.setShader(Shaders.solidCloudShader);       
        //This particular batch function requires a texture to bind, so I'm binding the texture used later in chunk rendering 
        //TODO: stop using spritebatch and call the render from an actual Mesh object (will be cleaner and will not require an extra texture bind)
        batch.draw(SpriteLoader.tileAtlas.findRegion("tiledCoal").getTexture(), verts, 0, 20);
 		batch.end();
 		batch.setShader(Shaders.defaultShader);
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
        if (GameScreen.gamePaused) {
        	layout.setText(font, "sFPS: " + "PAUSED" + " (" + SimulationThread.TICKS_PER_SECOND + ")");
        } else {
        	layout.setText(font, "sFPS: " + sfps + " (" + SimulationThread.TICKS_PER_SECOND + ")");
        }
        float text2width = layout.width;
        layout.setText(font, "simFac: " + df.format(simFac));
        float text3width = layout.width;
        
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond() , 5, HEIGHT - textheight + 2);
        if (GameScreen.gamePaused) {
        	font.draw(batch, "sFPS: " + "PAUSED" + " (" + SimulationThread.TICKS_PER_SECOND + ")", 5 + 5 + textwidth, HEIGHT - textheight + 2);
        } else {
        	font.draw(batch, "sFPS: " + sfps + " (" + SimulationThread.TICKS_PER_SECOND + ")", 5 + 5 + textwidth, HEIGHT - textheight + 2);
        }
        font.draw(batch, "simFac: " + df.format(simFac) , 5, HEIGHT - textheight*2 - 2);
        font.draw(batch, "simTick: " + SimulationThread.simTick , 5 + 5 + text3width, HEIGHT - textheight*2 - 2);
    }

    void drawTerrainInfo(SpriteBatch batch) {
    	if (GameScreen.hoverChunk != null) {
    		float textheight = font.getCapHeight();
	
			StringBuilder sb = new StringBuilder();
			sb.append("Height: " + GameScreen.hoverChunk.getHeight());
			sb.append(", ");
			sb.append("Creeper: " + Base.round(GameScreen.hoverChunk.getCreeperLevel(), 3));
			sb.append(", ");
			sb.append("AbsCreeper: " + Base.round(GameScreen.hoverChunk.getAbsoluteCreeperLevel(), 3));
			sb.append(", ");
			sb.append("Ore level: (" + GameScreen.hoverChunk.getOreType().toString() + ") " + Base.round(GameScreen.hoverChunk.getOreLevel(), 3));
			
			StringBuilder sb2 = new StringBuilder();
			sb2.append("Layer: " + GameScreen.terrainLayerSelected);
			sb2.append(", ");
			sb2.append("Brush: " + GameScreen.terrainBrushSize);
			
			layout.setText(font, sb.toString());
			float textwidth = layout.width;
			layout.setText(font, sb2.toString());
			float textwidth2 = layout.width;
	
			font.draw(batch, sb.toString(), WIDTH/2 - textwidth/2, HEIGHT - textheight + 2);    
			font.draw(batch, sb2.toString(), WIDTH/2 - textwidth2/2, HEIGHT - textheight*2 - 2);    
    	}
    }

    void drawPauseIndicator(SpriteBatch batch) {
		float textheight = fontLarge.getCapHeight();
		String s = "SIM PAUSED";
		layout.setText(fontLarge, s);
		float textwidth = layout.width;
		fontLarge.setColor(Color.RED);
		fontLarge.draw(batch, s, WIDTH/2 - textwidth/2, HEIGHT - textheight*3);     
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
            cam.zoom += 0.1f;
			GameScreen.chunks.updateView(cam);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            cam.zoom -= 0.1f;
			GameScreen.chunks.updateView(cam);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
        	int prevTick = SimulationThread.TICKS_PER_SECOND;
        	if (prevTick != Integer.MAX_VALUE - 1) simulationThread.recalculateSpeed(++prevTick);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)) {
        	int prevTick = SimulationThread.TICKS_PER_SECOND;
        	if (prevTick != 1) {simulationThread.recalculateSpeed(--prevTick);}
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
        	simulationThread.recalculateSpeed(30);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            cam.translate(-3, 0, 0);
          	GameScreen.chunks.updateView(cam);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            cam.translate(3, 0, 0);            
          	GameScreen.chunks.updateView(cam);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            cam.translate(0, -3, 0);     
            GameScreen.chunks.updateView(cam);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            cam.translate(0, 3, 0);  
          	GameScreen.chunks.updateView(cam);
        }
        
        //Initial clamping fix TODO: resolve
        if (SimulationThread.simTick < 1) {
        	chunks.updateView(cam);
        }
        
        //Zoom clamping, min max
        cam.zoom = MathUtils.clamp(cam.zoom, Base.MIN_ZOOM, Base.WORLD_SIZE*3/cam.viewportWidth);

        /*
        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;  
        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f - Base.WORLD_SIZE*0.2f, Base.WORLD_SIZE*1.2f - effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f -  Base.WORLD_SIZE*0.2f, Base.WORLD_SIZE*1.2f - effectiveViewportHeight / 2f);
   		*/
        
        
        //Making sure the camera doesn't go beyond the world limit
        cam.position.x = MathUtils.clamp(cam.position.x, 0 - Base.WORLD_SIZE*0.2f, Base.WORLD_SIZE * 1.2f);
        cam.position.y = MathUtils.clamp(cam.position.y, 0 - Base.WORLD_SIZE*0.2f, Base.WORLD_SIZE * 1.2f);   		
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
   
    public static void callPauseMenu() {
    	GameScreen.pauseMenu.setVisible(!GameScreen.pauseMenu.isVisible());
		GameScreen.gamePaused = GameScreen.pauseMenu.isVisible();
		
		if (GameScreen.gamePaused) {
			GameScreen.simulationThread.pauseSim();
		} else {
			GameScreen.simulationThread.resumeSim();
		}
    }
    
    public static void startSimulationThread() {
        simulationThread = new SimulationThread();        
        simulationThread.start();  
    }
    
    @Override
    public void resize(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
        
        aspectRatio = (float)WIDTH/(float)HEIGHT;
        
        cam.viewportWidth = viewportWidth;
        cam.viewportHeight = viewportWidth * height/width;
        cam.update();

        stageViewport = new FillViewport(width, height);
        stage.setViewport(stageViewport);
        stage.getViewport().update(width, height, true);
        
        buildMenu.resize();
        //hudMenu.resize();
        pauseMenu.resize();
    }

    @Override
    public void dispose () {
        batch.dispose();
        font.dispose();
        fontLarge.dispose();
        r.dispose();
        terrainBuffer.dispose();
		blurBuffer.dispose();
		stage.dispose();
		
		for (int i = 0; i < Base.MAX_CREEP; i++) {
			corrBuffers.get(i).dispose();
		}
		
		VisUI.dispose();
		
		System.out.println("DISPOSE");
    }  
}