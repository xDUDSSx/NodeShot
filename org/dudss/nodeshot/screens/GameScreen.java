package org.dudss.nodeshot.screens;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.buildings.Building;
import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.inputs.DesktopInputProcessor;
import org.dudss.nodeshot.inputs.MobileGestureListener;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.misc.BuildingHandler;
import org.dudss.nodeshot.misc.ConnectorHandler;
import org.dudss.nodeshot.misc.PackageHandler;
import org.dudss.nodeshot.ui.BuildMenu;
import org.dudss.nodeshot.ui.RightClickMenuManager;
import org.dudss.nodeshot.utils.Selector;
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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
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

	public static Sprite mapSprite;
	public static Sprite[] mapTiles;

	public static Sprite toolSprite;

	public static int WORLD_SIZE;

	public static float viewportWidth = 300f;
    
	public static boolean buildMode = false;
	public static Building builtBuilding = null;
	
    //libGDX
    SpriteBatch batch;
    Texture img;
    ShapeRenderer r;

    BitmapFont font;
    GlyphLayout layout;

    public static OrthographicCamera cam;
    public static Vector3 lastCamePos;
    public static Boolean zooming = false;

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
    
    public GameScreen(Game game)
    {
        nodeshotGame = game;

        packageHandler = new PackageHandler();
        connectorHandler = new ConnectorHandler();
        buildingHandler = new BuildingHandler();
        
        rightClickMenuManager = new RightClickMenuManager();
             
        //Map generation
        int size_x = 3;
        int size_y = 3;
        WORLD_SIZE = (size_x > size_y ? size_x : size_y) * 1000;

        mapTiles = new Sprite[size_x*size_y];

        if (Gdx.app.getType() == ApplicationType.Android) {
        	Texture tooltex = new Texture(Gdx.files.internal("res/tools_icon_button64.png"));
        	toolSprite = new Sprite(tooltex);
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	Texture tooltex = new Texture("res/tools_icon_button64.png");
        	toolSprite = new Sprite(tooltex);
        }
        
        SpriteLoader.loadAll();
        
        Texture mapTex = new Texture("res/4Kmap.png");
        mapSprite = new Sprite(mapTex);
        mapSprite.setPosition(0, 0);
        mapSprite.setSize(1000, 1000);

        //Creates a grid with size_x, size_y dimensions, stores sprites into a tile field
        for (int i = 0; i < size_y; i++) {
            System.out.println("OuterL: " + i);
            for (int y = 0; y < size_x; y++) {
                Sprite s = new Sprite(mapTex);
                s.setSize(1000, 1000);
                s.setPosition(1000*y, 1000*i);
                mapTiles[i+y+((size_x-1)*i)] = s;
            }
        }
        System.out.println("lenght: " + mapTiles.length);
        
        //Simulation thread
        Thread simulationThread = new Thread(new SimulationThread());
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    public static int getWidth() {return WIDTH;}
    public static int getHeight() {return HEIGHT;}

    @Override
    public void show() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();
        
        System.out.println("WIDTH: " + WIDTH + " -- HEIGHT: " + HEIGHT);

        batch = new SpriteBatch();
        r = new ShapeRenderer();
        font = new BitmapFont();
        layout = new GlyphLayout();  

        InputMultiplexer multiplexer = new InputMultiplexer();       
        if (Gdx.app.getType() == ApplicationType.Android) {
        	atlas = new TextureAtlas(Gdx.files.internal("data/uiskin.atlas"));
        	skin = new Skin(Gdx.files.internal("data/uiskin.json"), atlas);
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	atlas = new TextureAtlas("res/data/uiskin.atlas");
        	skin = new Skin(Gdx.files.classpath("res/data/uiskin.json"), atlas);
        }
    
        //FreeTypeFontGenerator 
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Helvetica-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Base.HUD_FONT_SIZE;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!'()>?:%+-*/";
        font = generator.generateFont(parameter);
        generator.dispose();
    
        //LineWidth
        Gdx.gl.glLineWidth(2);

        //Cam
        cam = new OrthographicCamera(viewportWidth , viewportWidth * (HEIGHT / WIDTH));
        if (lastCamePos == null) {
            cam.position.set(WORLD_SIZE / 2f, WORLD_SIZE / 2f, 0);
        } else {
            cam.position.set(lastCamePos);
        }
        cam.update();
        lastCamePos = cam.position;
        
        //UI STAGE INIT
        stageViewport = new StretchViewport(WIDTH, HEIGHT);
        stage = new Stage(stageViewport);         
             
        //UI
        buildMenu = new BuildMenu("Build menu", skin);
        
        //TextureRegion textureRegion = new TextureRegion(toolSprite);
        //TextureRegionDrawable texRegionDrawable = new TextureRegionDrawable(textureRegion);
        TextButton imgButton = new TextButton("Build", skin, "hoverfont15");
        imgButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	buildMenu.setVisible(!(buildMenu.isVisible()));
            }
        });
        imgButton.setSize(64, 64);
        imgButton.setPosition(10, 25);
        
        stage.addActor(imgButton);
        stage.addActor(buildMenu);
        
        //Input processors
        multiplexer.addProcessor(stage);
        if (Gdx.app.getType() == ApplicationType.Android) {
        	MobileGestureListener mgl = new MobileGestureListener();
        	multiplexer.addProcessor(new GestureDetector(mgl));
        } else {
        	DesktopInputProcessor dip = new DesktopInputProcessor();
        	multiplexer.addProcessor(dip);
        }  
        Gdx.input.setInputProcessor(multiplexer);
        
        recalculateWindow();
        
        //Ingame HUD buttons (Android) (//TODO: remove, utilize proper scene UI)
        if (Gdx.app.getType() == ApplicationType.Android) {
	        backButton.set( 10, 10, 180, 180);
	        deleteButton.set( 10 , (200)*1 + 10, 180, 180);
	        buildButton.set( 10, (200)*2 + 10, 180, 180);
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
    	recalculateWindow();
    }

    @Override
    public void render (float delta) {
        handleInput();
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        r.setProjectionMatrix(cam.combined);
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        //Map
        batch.begin();
        //mapSprite.draw(batch);
        for (Sprite s : mapTiles) {
            s.draw(batch);
        }
        batch.end();
        
    	Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        r.begin(ShapeType.Filled);
        buildingHandler.drawAll(r);
             
        //grid rendering
        drawGrid(r);
        
        if (buildMode) {
        	drawPrefab(r);
        }
        
        r.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        batch.begin();
        //Drawing packages
        for(int i = 0; i < packagelist.size(); i++) {
            Package p = packagelist.get(i);

            if (selectedID == p.getID()) {
                Sprite packageSprite = SpriteLoader.nodeHighlightSprite;
                if (packagelist.get(selectedIndex) instanceof Coal) {
                	packageSprite = SpriteLoader.coalHighlightSprite;
                }
                if (packagelist.get(selectedIndex) instanceof Iron) {
                	packageSprite = SpriteLoader.ironHighlightSprite;
                }
                
                packageSprite.setPosition(p.getX(), p.getY());
                packageSprite.setOrigin(p.radius/2, p.radius/2);
                packageSprite.setScale(0.65f);
                packageSprite.draw(batch);
            }

            p.draw(batch);
        }

        //Drawing nodes & highlights
        for (int i = 0; i < nodelist.size(); i++) {
            Node n = nodelist.get(i);         
            n.setScale(0.8f);
            n.draw(batch);
            
            if (n.getID() == selectedID) {
                Sprite s = new Sprite(SpriteLoader.highlightSprite);              
                s.setPosition(n.getX(), n.getY());
                s.setOrigin(n.radius/2, n.radius/2);
                s.setScale(0.85f);
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
    }

    void drawGrid(ShapeRenderer sR) {

        r.setColor(Color.WHITE);
        //ConnectMode line
        if ((toggleConnectMode == true && activeNewConnection == true) || draggingConnection == true) {
            Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
            r.rectLine(nodelist.get(newConnectionFromIndex).getCX(), nodelist.get(newConnectionFromIndex).getCY(), worldPos.x, worldPos.y, Base.lineWidth);
        }

        if(!nodelist.isEmpty()) {
            connectorHandler.drawAll(sR);
        }
    }

    void drawPrefab(ShapeRenderer sR) {
    	Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
    	builtBuilding.drawPrefab(r, worldPos.x, worldPos.y);
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
        //layout.setText(font, stat10);
        //float textstat10width = layout.width;
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
        //font.draw(batch, stat10, WIDTH - textstat10width - 10, HEIGHT - (textheight+2)*11);
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
            System.out.println("Wut+");
            cam.zoom += 0.08;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            cam.zoom -= 0.08;
            System.out.println("Was-");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)) {
        	int prevTick = SimulationThread.TICKS_PER_SECOND;
        	if (prevTick != Integer.MAX_VALUE - 1) SimulationThread.recalculateSpeed(++prevTick);
        	System.out.println("Wut+++");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) {
        	int prevTick = SimulationThread.TICKS_PER_SECOND;
        	if (prevTick != 1) {SimulationThread.recalculateSpeed(--prevTick);}
            System.out.println("Was---");
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
        	lastCamePos = cam.position;
        	nodeshotGame.setScreen(new MenuScreen(nodeshotGame));
        }

        //Zoom clamping, min max
        cam.zoom = MathUtils.clamp(cam.zoom, 0.2f, WORLD_SIZE/cam.viewportWidth);

        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        //Making sure the camera doesnt go beyond the world limit
        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, WORLD_SIZE - effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, WORLD_SIZE - effectiveViewportHeight / 2f);
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
        			System.out.println("Entity null");
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
                if(p.getBoundingRectangle().contains(new Vector2(worldPos.x, worldPos.y))) {
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

            if (Intersector.intersectSegmentPolygon(new Vector2(x1,y1), new Vector2(x2,y2), p))
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
    }

    @Override
    public void dispose () {
        batch.dispose();
        //img.dispose();
        r.dispose();
    }
    
    private void recalculateWindow() {
    	/*WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		System.out.println("recalculate: " + WIDTH + " - " + HEIGHT);

		cam.viewportWidth = viewportWidth;
		cam.viewportHeight = viewportWidth * HEIGHT/WIDTH;
		//cam.setToOrtho(false, cam.viewportWidth, cam.viewportHeight);
		cam.update();
    
		stageViewport.update(WIDTH , HEIGHT);
		*/
    }
    
}