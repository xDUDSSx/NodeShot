package org.dudss.nodeshot.screens;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.nodes.InputNode;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.inputs.DesktopInputProcessor;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.misc.BuildingManager;
import org.dudss.nodeshot.misc.BulletHandler;
import org.dudss.nodeshot.misc.ConnectorHandler;
import org.dudss.nodeshot.misc.PackageHandler;
import org.dudss.nodeshot.misc.ResourceManager;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.Chunks;
import org.dudss.nodeshot.terrain.Section;
import org.dudss.nodeshot.ui.BuildMenu;
import org.dudss.nodeshot.ui.PauseMenu;
import org.dudss.nodeshot.ui.RightClickMenuManager;
import org.dudss.nodeshot.ui.ToolbarMenu;
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

    public static boolean startedOnce = false;
    
	public static long currentSimTimeTick;
	public static long nextSimTimeTick;

	public static SimulationThread simulationThread;
	
	public static int sfps;
	public static int simFrameCount;
	public static double simFac;
	public static boolean gamePaused = false;
	
	/**Animation state time, used for animation frame looping*/
	public static float stateTime = 0f;

	//Collections
	public static CopyOnWriteArrayList<Node> nodelist = new CopyOnWriteArrayList<Node>();
	public static CopyOnWriteArrayList<Package> packagelist = new CopyOnWriteArrayList<Package>();

	//Handlers
	public static PackageHandler packageHandler;
	public static ResourceManager resourceManager;
	public static ConnectorHandler connectorHandler;
	public static BuildingManager buildingManager;
	public static BulletHandler bulletHandler;
	
	public static Vector2 mousePos = new Vector2();
	public static int mouseX;
	public static int mouseY;
	public static Vector3 lastMousePress = new Vector3(0,0,0);
	public static enum MouseType {
		MOUSE_1, MOUSE_2, MOUSE_3
	}
	public static MouseType lastMousePressType;

	//Selecting
	public static Entity selectedEntity = null;
	public static int selectedID = -1;
	public static int selectedIndex = -1;
	public static EntityType selectedType = EntityType.NONE;
	
	//Terrain
	public static Chunks chunks;
	public static Chunk hoverChunk = null;
	public static int terrainLayerSelected = 2;
	public static int terrainBrushSize = 2;
	
	public static float viewportWidth = 312f;
    FreeTypeFontGenerator generator;
	
    //Build mode
	public static boolean buildMode = false;
	public static AbstractBuilding builtBuilding = null;
	public static int activeRotation = 0;

	//public static AbstractBuilding expandedNodeBuilding;
	public static ConveyorNode expandedConveyorNode;
	public static boolean expandingANode = false;
	
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
    public static PauseMenu pauseMenu;
    public static ToolbarMenu toolbarMenu;
    
    public static GLProfiler glProfiler;
    
    public static FrameBuffer corrBuffer;
    public static FrameBuffer blurBuffer;
    
    /**The main game screen
     * @param game The {@link Game} object that is passed on {@link Screen} switches.*/
    public GameScreen(Game game)
    {
        nodeshotGame = game;
        glProfiler = new GLProfiler(Gdx.graphics);
        
        if (Base.enableGlProgilerLogging) glProfiler.enable();  
        
        //Loading resources
        Shaders.load();
        SpriteLoader.loadAll();
        VisUI.load();
        
        packageHandler = new PackageHandler();
        resourceManager = new ResourceManager(Base.START_POWER, Base.START_BITS);
        connectorHandler = new ConnectorHandler();
        buildingManager = new BuildingManager();       
        bulletHandler = new BulletHandler();  
        chunks = new Chunks();        
        rightClickMenuManager = new RightClickMenuManager();             
        
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        aspectRatio = (float)WIDTH/(float)HEIGHT;
        
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
        
        //Initialises chunks and sections
        chunks.create();
        //Generate terrain if not generated already
        if (chunks.generated == false) {       	
        	chunks.generateAll();
        }       
        //Updates the view
        chunks.updateView(cam);
        
        blurBuffer = new FrameBuffer(Format.RGBA8888, WIDTH, HEIGHT, false);
		corrBuffer = new FrameBuffer(Format.RGBA8888, WIDTH, HEIGHT, false);
		
        batch = new SpriteBatch();
        r = new ShapeRenderer();
        r.setAutoShapeType(true);
        
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
                
        toolbarMenu = new ToolbarMenu("Menu");
        stage.addActor(toolbarMenu);
        
        pauseMenu = new PauseMenu(false);
        stage.addActor(pauseMenu);
        
        TextButton imgButton = new TextButton("Build", skin, "hoverfont15");
        imgButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	buildMenu.setVisible(!(buildMenu.isVisible()));
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
		       
        //Input processors
        InputMultiplexer multiplexer = new InputMultiplexer();           
        DesktopInputProcessor dip = new DesktopInputProcessor();
        multiplexer.addProcessor(stage);
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
    	stateTime += delta;
    	 	
        handleInput();
        cam.update();	        	
        toolbarMenu.updateInfoPanel();
        
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
     
        //OpenGL performance logging
        if (Base.enableGlProgilerLogging) glProfiler.reset();           
		
        //Background cloud rendering
        drawBackgroundClouds(batch);
 		
        //Setting the world space projection matrix
        batch.setProjectionMatrix(cam.combined);
        r.setProjectionMatrix(cam.combined);
        
        //Rendering the terrain
        chunks.drawTerrain();
        
		//OpenGL performance logging
        if (Base.enableGlProgilerLogging) {
        	BaseClass.logger.info(
        	"\nDraw calls: " + glProfiler.getDrawCalls() + 			
			"\nTexture binding " + glProfiler.getTextureBindings() + 
			"\nShaderSwitches: " + glProfiler.getShaderSwitches() +
			"\nVertexCount: " + glProfiler.getVertexCount().average +
			"\nCalls: " + glProfiler.getCalls()
        	);
        }
        
        buildingManager.drawAllMisc(r, batch);
        buildingManager.drawAllBuildings(r, batch);

        //Connector rendering
        drawConnectors(r);
    
        if (buildMode && builtBuilding != null) {
        	r.begin(ShapeType.Filled);
        	Gdx.gl.glEnable(GL20.GL_BLEND);       
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);                  	
        	drawPrefab(r, batch);
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
        batch.end();     
        
        //Drawing the visible corruption. Corruption is no longer rendered as individual mesh layers (since v5.0 30.11.2018)
        chunks.drawCorruption();
        
        //Drawing creeper generators on top of creeper itself
        buildingManager.drawAllGenerators(r, batch);
        
        //Drawing the fog of war.
        chunks.drawFogOfWar();
        
        bulletHandler.drawAll(r, batch);

        //Draw debug infographics
        drawDebug(r);
           
        //HUD, draw last
        //setting screen matrix    
        setHudProjectionMatrix(batch);
        setHudProjectionMatrix(r); 
        
        batch.begin(); 
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
        
        if (Base.enableGlProgilerLogging) glProfiler.reset(); 
    }   
    
    /**Draws all debug infographics if enabled*/
    public static void drawDebug(ShapeRenderer r) {
    	//Shows terrain edges in blue
        if (Base.drawTerrainEdges) {
	        r.begin(ShapeType.Line);
	        r.setColor(Color.WHITE);
	        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
	        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	    
	        		if (chunks.getChunkAtTileSpace(x, y).isTerrainEdge() == true) {       			
	        			Color c = new Color(Color.rgba8888(0/255f, 0/255f, 255/255f, 1.0f));
	        			r.setColor(c);
	        			r.rect((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE), Base.CHUNK_SIZE, Base.CHUNK_SIZE);
	        		}	        		
	        	}
	        }
	        r.end();
        }
        
        //Shows corruption edges in red
        if (Base.drawCorruptionEdges) {
	        r.begin(ShapeType.Line);
	        r.setColor(Color.WHITE);
	        Gdx.gl.glLineWidth(1);
	        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
	        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	    
	        		if (chunks.getChunkAtTileSpace(x, y).isCorruptionEdge()) {       			
	        			Color c = new Color(Color.rgba8888(255/255f, 0/255f, 0/255f, 1.0f));
	        			r.setColor(c);	        			
	        			r.rect((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE), Base.CHUNK_SIZE, Base.CHUNK_SIZE);
	        		}	      
	        	}
	        }
	        Gdx.gl.glLineWidth(2);
	        r.end();
        }
        
        //Shows chunks where height != c_height in green
        if (Base.drawCHeightInequality) {
	        r.begin(ShapeType.Line);
	        r.setColor(Color.WHITE);
	        //Gdx.gl.glLineWidth(1);
	        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
	        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	    
	        		if (chunks.getChunkAtTileSpace(x, y).getCHeight() != chunks.getChunkAtTileSpace(x, y).getHeight()) {       			
	        			Color c = new Color(Color.rgba8888(0/255f, 255/255f, 0/255f, 1.0f));
	        			r.setColor(c);	        			
	        			r.rect((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE), Base.CHUNK_SIZE, Base.CHUNK_SIZE);
	        		}	      
	        	}
	        }
	        //Gdx.gl.glLineWidth(2);
	        r.end();
        }    
        
        //Shows chunks at the edges of Sections in yellow
        if (Base.drawBorderChunks) {
	        r.begin(ShapeType.Line);
	        r.setColor(Color.WHITE);
	        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
	        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	    
	        		if (chunks.getChunkAtTileSpace(x, y).isBorderChunk()) {       			
	        			Color c = new Color(Color.rgba8888(255/255f, 255/255f, 0/255f, 1.0f));
	        			r.setColor(c);	        			
	        			r.rect((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE), Base.CHUNK_SIZE, Base.CHUNK_SIZE);
	        		}	      
	        	}
	        }
	        r.end();
        }    
        
        //Shows sections that are currently being updated by the corruption thread in sky-blue
        if (Base.drawActiveSections ) {
	        r.begin(ShapeType.Line);
	        r.setColor(Color.WHITE);
	        for (int x = 0; x < Base.SECTION_AMOUNT; x++) {
	        	for (int y = 0; y < Base.SECTION_AMOUNT; y++) {	    
	        		if (chunks.getSection(x, y).isActive()) {       			
	        			Color c = new Color(Color.rgba8888(0/255f, 255/255f, 255/255f, 1.0f));
	        			r.setColor(c);	        			
	        			r.rect((float) (x * Base.SECTION_SIZE*Base.CHUNK_SIZE), (float) (y * Base.SECTION_SIZE*Base.CHUNK_SIZE), Base.SECTION_SIZE*Base.CHUNK_SIZE, Base.SECTION_SIZE*Base.CHUNK_SIZE);
	        			r.line((x * Base.SECTION_SIZE*Base.CHUNK_SIZE), (y * Base.SECTION_SIZE*Base.CHUNK_SIZE), (x * Base.SECTION_SIZE*Base.CHUNK_SIZE) + Base.SECTION_SIZE*Base.CHUNK_SIZE, (y * Base.SECTION_SIZE*Base.CHUNK_SIZE) + Base.SECTION_SIZE*Base.CHUNK_SIZE);
	        			r.line((x * Base.SECTION_SIZE*Base.CHUNK_SIZE) + Base.SECTION_SIZE*Base.CHUNK_SIZE, (y * Base.SECTION_SIZE*Base.CHUNK_SIZE), (x * Base.SECTION_SIZE*Base.CHUNK_SIZE), (y * Base.SECTION_SIZE*Base.CHUNK_SIZE) + Base.SECTION_SIZE*Base.CHUNK_SIZE);
	        		}	      
	        	}
	        }
	        r.end();
        }    
        
        if (Base.drawBuildingTiles) {
	        r.begin(ShapeType.Line);
	        r.setColor(Color.WHITE);
	        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
	        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	    
	        		if (chunks.getChunkAtTileSpace(x, y).getBuilding() != null) {       			
	        			Color c = new Color(Color.rgba8888(197/255f, 37/255f, 237/255f, 1.0f));
	        			r.setColor(c);	        			
	        			r.rect((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE), Base.CHUNK_SIZE, Base.CHUNK_SIZE);
	        			r.line((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE), (x * Base.CHUNK_SIZE) + Base.CHUNK_SIZE, (float) (y * Base.CHUNK_SIZE) + Base.CHUNK_SIZE);
	        			r.line((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE) + Base.CHUNK_SIZE, (x * Base.CHUNK_SIZE) + Base.CHUNK_SIZE, (float) (y * Base.CHUNK_SIZE));
	        		}	      
	        	}
	        }
	        r.end();
        }    
        
        //Highlight of the chunk the mouse is hovering on
        if (hoverChunk != null) {
        	if (buildMode == true) {
        		/*r.begin(ShapeType.Filled);
	        	r.setColor(Color.WHITE);
	        	r.rect(hoverChunk.getX(), hoverChunk.getY(), hoverChunk.getSize(), hoverChunk.getSize());
	        	r.end();
	        	*/        	
        	} else if (Base.hoverChunkHighlight) {
        		r.begin(ShapeType.Line);
	        	r.setColor(Color.WHITE);
	        	r.rect(hoverChunk.getX(), hoverChunk.getY(), hoverChunk.getSize(), hoverChunk.getSize());
	        	r.end();
        	}
        }
        
        if (Base.drawSectionBorders) {
	        r.begin(ShapeType.Line);
	        for (Section s : chunks.sectionsInView) {	   
	        	Chunk c = s.getChunk(0, 0);	        	
	        	r.setColor(Color.YELLOW);
	        	r.rectLine(c.getX(), c.getY(), c.getX() + Base.SECTION_SIZE*Base.CHUNK_SIZE, c.getY(), 5);
	        	r.rectLine(c.getX() + Base.SECTION_SIZE*Base.CHUNK_SIZE, c.getY(), c.getX() + Base.SECTION_SIZE*Base.CHUNK_SIZE, c.getY() + Base.SECTION_SIZE*Base.CHUNK_SIZE, 5);
	        	r.rectLine(c.getX() + Base.SECTION_SIZE*Base.CHUNK_SIZE, c.getY() + Base.SECTION_SIZE*Base.CHUNK_SIZE, c.getX(), c.getY() + Base.SECTION_SIZE*Base.CHUNK_SIZE, 5);
	        	r.rectLine(c.getX(), c.getY() + Base.SECTION_SIZE*Base.CHUNK_SIZE, c.getX(), c.getY(), 5);
	        }      
	        r.end();
        }
        
        if (Base.drawCreeperLevel) {
	        r.begin(ShapeType.Filled);
	        r.setColor(Color.WHITE);
	        for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
	        	for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {	    
	        		if (chunks.getChunkAtTileSpace(x, y).getCreeperLevel() > 0) {
	        			float n = chunks.getChunkAtTileSpace(x, y).getCreeperLevel() * 100;	        			
	        			if (n == 100) {
	        				n = 99;
	        			}
	        			float rc = (255 * n) / 100;
	        			float g = (255 * (100 - n)) / 100 ;
	        			float b = 0;
	        			Color c = new Color(Color.rgba8888(rc/255f, g/255f, b/255f, 1.0f));	        			
	        			r.setColor(c);
	        			r.rect((float) (x * Base.CHUNK_SIZE), (float) (y * Base.CHUNK_SIZE), Base.CHUNK_SIZE, Base.CHUNK_SIZE);
	        		}
	        	}
	        }
	        r.end();
        }
    }
    
    //Shader related rendering methods
    public static void blurBuffer(FrameBuffer fboA, FrameBuffer fboB, Texture texture, float x, float y) {
    	fboB.begin();
    	Shaders.blurShader.begin();
    	Shaders.blurShader.setUniformf("dir", 1.0f, 0.0f);
    	Shaders.blurShader.setUniformf("radius", 0.05f);
        Shaders.blurShader.setUniformf("resolution", (cam.zoom * 300) * aspectRatio);
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
    	Shaders.blurShader.setUniformf("radius", 0.05f);
    	Shaders.blurShader.setUniformf("resolution", cam.zoom * 300);
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
        if (expandingANode && expandedConveyorNode != null) {
            Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
            r.rectLine(	expandedConveyorNode.getCX(),
            			expandedConveyorNode.getCY(),
            			builtBuilding.getPrefabX(worldPos.x, true) + builtBuilding.getWidth()/2,
            			builtBuilding.getPrefabY(worldPos.y, true) + builtBuilding.getHeight()/2, 
            			Base.lineWidth);
        }
        r.end();
        
        if(!nodelist.isEmpty()) {
            connectorHandler.drawAll(sR);
        }
    }

    void drawPrefab(ShapeRenderer sR, SpriteBatch batch) {
    	Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
    	builtBuilding.drawPrefab(r, batch, worldPos.x, worldPos.y, true);
    }
    
    void drawStats(SpriteBatch batch) {
        float textheight = font.getCapHeight();
        String stat = "Nodes: " + nodelist.size();
        String stat2 = "Packages: " + packagelist.size();
        String stat3 = "Connectors: " + connectorHandler.getAllConnectors().size();
        String stat41 = "Buildings: " + buildingManager.getAllBuildings().size(); 
        String stat4 = "PackagePaths: " + packageHandler.getNumberOfPaths();
       // String stat5 = "ConnectMode: " + toggleConnectMode;
        //String stat6 = "activeNewConnection: " + activeNewConnection;
        String stat7 = "selectedID: " + selectedID;
        String stat8 = "selectedType: " + selectedType;
        String stat9 = "selectedIndex: " + selectedIndex;  
        String stat10 = "zoom: " + cam.zoom;
       // String stat11 = "draggingConnection: " + draggingConnection;
       // String stat12 = "newConnectionFromIndex: " + newConnectionFromIndex;   

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
        //layout.setText(font, stat5);
        float textstat5width = layout.width;
        //layout.setText(font, stat6);
        float textstat6width = layout.width;
        layout.setText(font, stat7);
        float textstat7width = layout.width;
        layout.setText(font, stat8);
        float textstat8width = layout.width;
        layout.setText(font, stat9);
        float textstat9width = layout.width;
        layout.setText(font, stat10);
        float textstat10width = layout.width;
        //layout.setText(font, stat11);
        float textstat11width = layout.width;
       // layout.setText(font, stat12);
        float textstat12width = layout.width;

        font.draw(batch, stat, WIDTH - textstatwidth - 10, HEIGHT - textheight);
        font.draw(batch, stat2, WIDTH - textstat2width - 10, HEIGHT - (textheight+2)*2);
        font.draw(batch, stat3, WIDTH - textstat3width - 10, HEIGHT - (textheight+2)*3);
        font.draw(batch, stat41, WIDTH - textstat41width - 10, HEIGHT - (textheight+2)*4);
        font.draw(batch, stat4, WIDTH - textstat4width - 10, HEIGHT - (textheight+2)*5);
       //font.draw(batch, stat5, WIDTH - textstat5width - 10, HEIGHT - (textheight+2)*6);
        //font.draw(batch, stat6, WIDTH - textstat6width - 10, HEIGHT - (textheight+2)*7);
        font.draw(batch, stat7, WIDTH - textstat7width - 10, HEIGHT - (textheight+2)*8);
        font.draw(batch, stat8, WIDTH - textstat8width - 10, HEIGHT - (textheight+2)*9);
        font.draw(batch, stat9, WIDTH - textstat9width - 10, HEIGHT - (textheight+2)*10);
        font.draw(batch, stat10, WIDTH - textstat10width - 10, HEIGHT - (textheight+2)*11);
        //font.draw(batch, stat11, WIDTH - textstat11width - 10, HEIGHT - (textheight+2)*12);
        //font.draw(batch, stat12, WIDTH - textstat12width - 10, HEIGHT - (textheight+2)*13);

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
			sb.append("c_Height: " + GameScreen.hoverChunk.getCHeight());
			sb.append(", ");
			sb.append("Creeper: " + GameScreen.hoverChunk.getCreeperLevel());
			sb.append(", ");
			sb.append("AbsCreeper: " + GameScreen.hoverChunk.getAbsoluteCreeperLevel());
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
        		highlightedEntity = checkBuildings(rect, worldPos, select);
        		if (highlightedEntity == null) {
        			highlightedEntity = checkConnectors(rect, worldPos, select);
        			if (highlightedEntity == null) {
        				return null;
        			}
        		}
        	}
        }
        
        return highlightedEntity;
    }

    private static AbstractBuilding checkBuildings(Rectangle rect, Vector3 worldPos, boolean select) {
    	Boolean buildingIntersected = false;
    	AbstractBuilding intersectedBuilding = null;
        if ((buildingManager.getAllBuildings().size() > 0)) {
            for(int i = 0; i < buildingManager.getAllBuildings().size(); i++) {
                AbstractBuilding b = buildingManager.getAllBuildings().get(i);
                Rectangle r = new Rectangle(b.getX(), b.getY(), b.getWidth(), b.getHeight());
                if(r.overlaps(rect)) {
                	if (select) Selector.selectBuilding(b);
                    buildingIntersected = true;
                    intersectedBuilding = b;
                    break;
                }
            }
            if (!buildingIntersected) {
                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                	if (select) Selector.deselect();
                }
            }
        }
        return intersectedBuilding;
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
        try {
			simulationThread = new SimulationThread();
		} catch (InterruptedException e) {
			BaseClass.errorManager.report(e, "SimulationThread initialisation error.");
		}        
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
        toolbarMenu.updateBounds();
        pauseMenu.resize();
    }

    @Override
    public void dispose () {
        batch.dispose();
        font.dispose();
        fontLarge.dispose();
        r.dispose();
        corrBuffer.dispose();
		blurBuffer.dispose();
		stage.dispose();
		SpriteLoader.tileAtlas.dispose();
		skin.dispose();
		VisUI.dispose();
    }  
}