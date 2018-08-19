package org.dudss.nodeshot.screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.NodeConnector;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.misc.NodeConnectorHandler;
import org.dudss.nodeshot.misc.PackageHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static org.dudss.nodeshot.BaseClass.EntityType;
import static org.dudss.nodeshot.BaseClass.MouseType;
import static org.dudss.nodeshot.BaseClass.NodeSelectiveInfo;
import static org.dudss.nodeshot.BaseClass.WORLD_SIZE;
import static org.dudss.nodeshot.BaseClass.activeNewConnection;
import static org.dudss.nodeshot.BaseClass.buildMode;
import static org.dudss.nodeshot.BaseClass.debugMessage;
import static org.dudss.nodeshot.BaseClass.draggingConnection;
import static org.dudss.nodeshot.BaseClass.highlightSprite;
import static org.dudss.nodeshot.BaseClass.highlightedIndex;
import static org.dudss.nodeshot.BaseClass.indexOfHighlightedNode;
import static org.dudss.nodeshot.BaseClass.lastMousePress;
import static org.dudss.nodeshot.BaseClass.lastMousePressType;
import static org.dudss.nodeshot.BaseClass.mapSprite;
import static org.dudss.nodeshot.BaseClass.mapTiles;
import static org.dudss.nodeshot.BaseClass.mousePos;
import static org.dudss.nodeshot.BaseClass.mouseX;
import static org.dudss.nodeshot.BaseClass.mouseY;
import static org.dudss.nodeshot.BaseClass.newConnectionFromIndex;
import static org.dudss.nodeshot.BaseClass.nodeConnectorHandler;
import static org.dudss.nodeshot.BaseClass.nodelist;
import static org.dudss.nodeshot.BaseClass.packageHandler;
import static org.dudss.nodeshot.BaseClass.packagelist;
import static org.dudss.nodeshot.BaseClass.selectedIndex;
import static org.dudss.nodeshot.BaseClass.selectedType;
import static org.dudss.nodeshot.BaseClass.sfps;
import static org.dudss.nodeshot.BaseClass.simFac;
import static org.dudss.nodeshot.BaseClass.spriteSheet;
import static org.dudss.nodeshot.BaseClass.toggleConnectMode;
import static org.dudss.nodeshot.BaseClass.viewportWidth;

public class GameScreen implements Screen, GestureDetector.GestureListener, InputProcessor {

    Game nodeshotGame;

    //new variables
    static int WIDTH;
    static int HEIGHT;

    //libGDX
    SpriteBatch batch;
    Texture img;
    ShapeRenderer r;

    BitmapFont font;
    GlyphLayout layout;

    OrthographicCamera cam;
    public static Vector3 lastCamePos;

    Rectangle backButton = new Rectangle();
    Rectangle buildButton = new Rectangle();
    Rectangle deleteButton = new Rectangle();

    Boolean zooming = false;

    public GameScreen(Game game)
    {
        this.nodeshotGame = game;

        packageHandler = new PackageHandler();
        nodeConnectorHandler = new NodeConnectorHandler();

        //Map generation
        int size_x = 3;
        int size_y = 3;
        WORLD_SIZE = (size_x > size_y ? size_x : size_y) * 1000;

        mapTiles = new Sprite[size_x*size_y];

        if (Gdx.app.getType() == ApplicationType.Android) {
        	spriteSheet = new Texture(Gdx.files.internal("spritesheet16x16.png"));
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	spriteSheet = new Texture("res/spritesheet16x16.png");
        }
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

        highlightSprite = new Sprite(spriteSheet, 16, 16, 16, 16);

        //Simulation thread
        Thread simulationThread = new Thread(new SimulationThread());
        simulationThread.start();
    }

    public static int getWidth() {return WIDTH;}
    public static int getHeight() {return HEIGHT;}

    @Override
    public void show() {
        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        System.out.println("WIDTH: " + WIDTH + " -- HEIGHT: " + HEIGHT);

        //Ingame HUD buttons
        backButton.set(  10, 10, 180, 180);
        deleteButton.set(  10 , (200)*1 + 10, 180, 180);
        buildButton.set( 10, (200)*2 + 10, 180, 180);

        batch = new SpriteBatch();
        r = new ShapeRenderer();
        font = new BitmapFont();
        layout = new GlyphLayout();

        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle(new File("Helvetica-Regular.ttf")));
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Helvetica-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Base.HUD_FONT_SIZE;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:%+-*/";

        font = generator.generateFont(parameter);
        generator.dispose();

        InputMultiplexer im = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        im.addProcessor(gd);
        im.addProcessor(this);
        Gdx.input.setInputProcessor(im);

        Gdx.gl.glLineWidth(2);

        cam = new OrthographicCamera(viewportWidth , viewportWidth * (HEIGHT / WIDTH));

        if (lastCamePos == null) {
            cam.position.set(WORLD_SIZE / 2f, WORLD_SIZE / 2f, 0);
        } else {
            cam.position.set(lastCamePos);
        }
        cam.update();

        lastCamePos = cam.position;
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

        //grid rendering
        drawGrid(r);

        batch.begin();
        //Drawing packages
        for(int i = 0; i < packagelist.size(); i++) {
            Package p = packagelist.get(i);

            if (highlightedIndex == p.getID()) {
                Sprite packageSprite = new Sprite(spriteSheet, 0, 16, 16, 16);
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
            n.draw(batch);

            if (n.getID() == highlightedIndex) {
                Sprite s = new Sprite(highlightSprite);
                s.setPosition(n.getX(), n.getY());
                s.draw(batch);
            }
        }

        //HUD, draw last
        //setting UI matrix
        setHudProjectionMatrix(batch);
        setHudProjectionMatrix(r);

        batch.end();
        drawButtons(batch, r);
        batch.begin();

        drawStats(batch);
        drawFps(batch);
        drawInfo(batch);
        drawCoords(batch);
        drawDebugMessage(debugMessage, batch);

        batch.end();
    }

    void drawGrid(ShapeRenderer sR) {
        r.begin(ShapeRenderer.ShapeType.Filled);

        //ConnectMode line
        if ((toggleConnectMode == true && activeNewConnection == true) || draggingConnection == true) {
            Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
            r.rectLine(nodelist.get(newConnectionFromIndex).getCX(), nodelist.get(newConnectionFromIndex).getCY(), worldPos.x, worldPos.y, Base.lineWidth);
        }

        if(!nodelist.isEmpty()) {
            nodeConnectorHandler.drawAll(sR);
        }
        r.end();
    }

    void drawStats(SpriteBatch batch) {
        float textheight = font.getCapHeight();
        String stat = "Nodes: " + nodelist.size();
        String stat2 = "Packages: " + packagelist.size();
        String stat3 = "Connectors: " + nodeConnectorHandler.getAllConnectors().size();
        String stat4 = "PackagePaths: " + packageHandler.getNumberOfPaths();
        String stat5 = "ConnectMode: " + toggleConnectMode;
        String stat6 = "activeNewConnection: " + activeNewConnection;
        String stat7 = "selectedIndex: " + selectedIndex;
        String stat8 = "selectedType: " + selectedType;
        String stat9 = "highlightedIndex: " + highlightedIndex;
        String stat10 = "indexOfHighlightedNode: " + indexOfHighlightedNode;
        String stat11 = "draggingConnection: " + draggingConnection;
        String stat12 = "newConnectionFromIndex: " + newConnectionFromIndex;

        layout.setText(font, stat);
        float textstatwidth = layout.width;
        layout.setText(font, stat2);
        float textstat2width = layout.width;
        layout.setText(font, stat3);
        float textstat3width = layout.width;
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
        font.draw(batch, stat4, WIDTH - textstat4width - 10, HEIGHT - (textheight+2)*4);
        font.draw(batch, stat5, WIDTH - textstat5width - 10, HEIGHT - (textheight+2)*5);
        font.draw(batch, stat6, WIDTH - textstat6width - 10, HEIGHT - (textheight+2)*6);
        font.draw(batch, stat7, WIDTH - textstat7width - 10, HEIGHT - (textheight+2)*7);
        font.draw(batch, stat8, WIDTH - textstat8width - 10, HEIGHT - (textheight+2)*8);
        font.draw(batch, stat9, WIDTH - textstat9width - 10, HEIGHT - (textheight+2)*9);
        font.draw(batch, stat10, WIDTH - textstat10width - 10, HEIGHT - (textheight+2)*10);
        font.draw(batch, stat11, WIDTH - textstat11width - 10, HEIGHT - (textheight+2)*11);
        font.draw(batch, stat12, WIDTH - textstat12width - 10, HEIGHT - (textheight+2)*12);

    }

    void drawFps(SpriteBatch batch) {
        float textheight = font.getCapHeight();

        layout.setText(font, "FPS: " + Gdx.graphics.getFramesPerSecond());
        float textwidth = layout.width;
        layout.setText(font, "sFPS: " + sfps);
        float text2width = layout.width;

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);

        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond() , 5, HEIGHT - textheight + 2);
        font.draw(batch, "sFPS: " + sfps , 5 + 5 + textwidth, HEIGHT - textheight + 2);
        font.draw(batch, "simFac: " + df.format(simFac) , 5, HEIGHT - textheight*2 - 2);
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
        if (highlightedIndex != -1) {
            for(Node n : nodelist) {
                if(n.getID() == highlightedIndex) {
                    Vector3 v = cam.project(new Vector3(n.getX(), n.getY(), 0));

                    double x = v.x;
                    double y = v.y;

                    font.draw(batch, "ID: " + n.getID(), (int)x + 35, (int)y + textheight*8 + 5);
                    font.draw(batch, "Index: " + nodelist.indexOf(n), (int)x + 35, (int)y + textheight*7 + 5);
                    font.draw(batch, "Node X: " + n.getX(), (int)x + 35, (int)y + textheight*6 + 5);
                    font.draw(batch, "Node Y: " + n.getY(), (int)x + 35, (int)y + textheight*5 + 5);
                    font.draw(batch, "Radius: " + n.radius, (int)x + 35, (int)y + textheight*4 + 5);
                    font.draw(batch, "Connections: " + n.getNumberOfConnections(), (int)x + 35, (int)y + textheight*3 + 5);
                    font.draw(batch, "Connectable: " + n.connectable, (int)x + 35, (int)y + textheight*2 + 5);
                    font.draw(batch, "Connected To: " + Base.nodeListToString(n.connected_to), (int)x + 35, (int)y + 5);
                    font.draw(batch, "Connected By: " + Base.nodeListToString(n.connected_by), (int)x + 35, (int)y - textheight + 5);
                    font.draw(batch, "Connectors: " + Base.nodeConnectorListToString(n.connectors), (int)x + 35, (int)y - textheight*2 + 5);
                    font.draw(batch, "Closed: " + n.isClosed(), (int)x + 35, (int)y - textheight*3 + 5);
                }
            }
            for(Package p : packagelist) {
                if(p.getID() == highlightedIndex) {
                    Vector3 v = cam.project(new Vector3(p.getX(), p.getY(), 0));

                    double x = v.x;
                    double y = v.y;

                    font.draw(batch, "ID: " + p.getID(), (int)x + 35, (int)y + (textheight+2) + 5);
                    font.draw(batch, "Percentage: " + p.percentage + " %", (int)x + 35, (int)y - (textheight+2)*1 + 5);
                    font.draw(batch, "Going: " + p.going, (int)x + 35, (int)y - (textheight+2)*2 + 5);
                    font.draw(batch, "Finished: " + p.finished, (int)x + 35, (int)y - (textheight+2)*3 + 5);
                    font.draw(batch, "From: " + p.from.getID(), (int)x + 35, (int)y - (textheight+2)*4 + 5);
                    font.draw(batch, "To: " + p.to.getID(), (int)x + 35, (int)y - (textheight+2)*5 + 5);
                }
            }
            for(NodeConnector nC : nodeConnectorHandler.getAllConnectors()) {
                if(nC.getID() == highlightedIndex) {
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
        }

        switch(selectedType) {
            case NODE:
                if(NodeSelectiveInfo == true) {
                    if(selectedIndex != -1) {
                        for(Node n : nodelist) {
                            if(n.getID() == selectedIndex) {
                                Vector3 v = cam.project(new Vector3(n.getX(), n.getY(), 0));

                                double x = v.x;
                                double y = v.y;

                                font.draw(batch, "ID: " + n.getID(), (int)x + 35, (int)y + textheight*8 + 5);
                                font.draw(batch, "Index: " + nodelist.indexOf(n), (int)x + 35, (int)y + textheight*7 + 5);
                                font.draw(batch, "Node X: " + n.getX(), (int)x + 35, (int)y + textheight*6 + 5);
                                font.draw(batch, "Node Y: " + n.getY(), (int)x + 35, (int)y + textheight*5 + 5);
                                font.draw(batch, "Radius: " + n.radius, (int)x + 35, (int)y + textheight*4 + 5);
                                font.draw(batch, "Connections: " + n.getNumberOfConnections(), (int)x + 35, (int)y + textheight*3 + 5);
                                font.draw(batch, "Connectable: " + n.connectable, (int)x + 35, (int)y + textheight*2 + 5);
                                font.draw(batch, "Connected To: " + Base.nodeListToString(n.connected_to), (int)x + 35, (int)y + 5);
                                font.draw(batch, "Connected By: " + Base.nodeListToString(n.connected_by), (int)x + 35, (int)y - textheight + 5);
                                font.draw(batch, "Connectors: " + Base.nodeConnectorListToString(n.connectors), (int)x + 35, (int)y - textheight*2 + 5);
                                font.draw(batch, "Closed: " + n.isClosed(), (int)x + 35, (int)y - textheight*3 + 5);
                            }
                        }
                    }
                } else {
                    for (Node n : nodelist) {
                        Vector3 v = cam.project(new Vector3(n.getX(), n.getY(), 0));

                        double x = v.x;
                        double y = v.y;

                        font.draw(batch, "ID: " + n.getID(), (int)x + 35, (int)y + textheight*8 + 5);
                        font.draw(batch, "Index: " + nodelist.indexOf(n), (int)x + 35, (int)y + textheight*7 + 5);
                        font.draw(batch, "Node X: " + n.getX(), (int)x + 35, (int)y + textheight*6 + 5);
                        font.draw(batch, "Node Y: " + n.getY(), (int)x + 35, (int)y + textheight*5 + 5);
                        font.draw(batch, "Radius: " + n.radius, (int)x + 35, (int)y + textheight*4 + 5);
                        font.draw(batch, "Connections: " + n.getNumberOfConnections(), (int)x + 35, (int)y + textheight*3 + 5);
                        font.draw(batch, "Connectable: " + n.connectable, (int)x + 35, (int)y + textheight*2 + 5);
                        font.draw(batch, "Connected To: " + Base.nodeListToString(n.connected_to), (int)x + 35, (int)y + 5);
                        font.draw(batch, "Connected By: " + Base.nodeListToString(n.connected_by), (int)x + 35, (int)y - textheight + 5);
                        font.draw(batch, "Connectors: " + Base.nodeConnectorListToString(n.connectors), (int)x + 35, (int)y - textheight*2 + 5);
                        font.draw(batch, "Closed: " + n.isClosed(), (int)x + 35, (int)y - textheight*3 + 5);

                    }
                }
                break;
            case CONNECTOR:
                for (NodeConnector nC : nodeConnectorHandler.getAllConnectors()) {
                    if (nC.getID() == selectedIndex) {
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
                    if (p.getID() == selectedIndex) {
                        Vector3 v = cam.project(new Vector3(p.getX(), p.getY(), 0));

                        double x = v.x;
                        double y = v.y;

                        font.draw(batch, "ID: " + p.getID(), (int)x + 35, (int)y + (textheight+2) + 5);
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
        if (buildMode == true) r.setColor(Color.GREEN);
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

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.PLUS)) {
            System.out.println("Wut+");
            cam.zoom += 0.08;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            cam.zoom -= 0.08;
            System.out.println("Was-");
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

        //Zoom clamping, min max
        cam.zoom = MathUtils.clamp(cam.zoom, 0.2f, WORLD_SIZE/cam.viewportWidth);

        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        //Making sure the camera doesnt go beyond the world limit
        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f, WORLD_SIZE - effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f, WORLD_SIZE - effectiveViewportHeight / 2f);
    }

    Boolean checkHighlights() {
        Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
        Rectangle rect = new Rectangle(worldPos.x-4, worldPos.y-4, 8, 8);

        checkNodes(rect);
        if (selectedIndex == -1) checkPackages(rect);
        if (selectedIndex == -1) checkConnectors(rect);

        if (selectedIndex == -1) {
            return false;
        } else {
            return true;
        }
    }

    void checkNodes(Rectangle rect) {
        if ((nodelist.size() > 0)) {
            Boolean nodeIntersected = false;
            for(int i = 0; i < nodelist.size(); i++) {
                Node n = nodelist.get(i);
                if(n.getBoundingRectangle().overlaps(rect)) {
                    selectedIndex = n.getID();
                    selectedType = EntityType.NODE;
                    //if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    highlightedIndex = n.getID();
                    //System.out.println("Node highlighted " + highlightedIndex);
                    //	}
                    nodeIntersected = true;
                    break;
                }
            }
            if (!nodeIntersected) {
                selectedIndex = -1;
                selectedType = EntityType.NONE;
                //if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                highlightedIndex = -1;
                //}
            }
        }
    }
    void checkPackages(Rectangle rect) {
        if ((packagelist.size() > 0)) {
            Boolean packageIntersected = false;
            for(int i = 0; i < packagelist.size(); i++) {
                Package p = packagelist.get(i);
                if(p.getBoundingRectangle().overlaps(rect)) {
                    selectedIndex = p.getID();
                    selectedType = EntityType.PACKAGE;
                    //if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    highlightedIndex = p.getID();
                    System.out.println("Package highlighted " + highlightedIndex);
                    //}
                    packageIntersected = true;
                    break;
                }
            }
            if (!packageIntersected) {
                selectedIndex = -1;
                selectedType = EntityType.NONE;
                //if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                highlightedIndex = -1;
                //}
            }
        }
    }
    void checkConnectors(Rectangle rect) {
        Boolean connectorIntersected = false;
        for (int i = 0; i < nodeConnectorHandler.getAllConnectors().size(); i++) {
            Node n1 = nodeConnectorHandler.getAllConnectors().get(i).getFrom();
            Node n2 = nodeConnectorHandler.getAllConnectors().get(i).getTo();

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
                NodeConnector nC = nodeConnectorHandler.getAllConnectors().get(i);
                selectedIndex =  nC.getID();
                selectedType = EntityType.CONNECTOR;
                //if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    highlightedIndex = nC.getID();
                    System.out.println("Connector highlighted " + highlightedIndex);
                //}
                connectorIntersected = true;
                break;
            }
        }

        if (!connectorIntersected) {
            selectedIndex = -1;
            selectedType = EntityType.NONE;
            //if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                highlightedIndex = -1;
            //}
        }
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = viewportWidth;
        cam.viewportHeight = viewportWidth * height/width;
        cam.update();

        WIDTH = width;
        HEIGHT = height;
    }

    @Override
    public void dispose () {
        batch.dispose();
        //img.dispose();
        r.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        mouseX = (int) x;
        mouseY = (int) y;

        mousePos.x = mouseX;
        mousePos.y = mouseY;

        Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
        lastMousePress = worldPos;

        System.out.println("WorldPos.x: " + worldPos.x + " WorldPos.y: " + worldPos.y);

        Rectangle rect = new Rectangle(worldPos.x-4, worldPos.y-4, 8, 8);

        Boolean nodeIntersected = false;
        for(int i = 0; i < nodelist.size(); i++) {
            //If cursor hits a node -> highlight it
            if(nodelist.get(i).getBoundingRectangle().overlaps(rect)) {
                indexOfHighlightedNode = nodelist.get(i).getIndex();
                System.out.println("HHighlighted: " + nodelist.get(i).getIndex());
                nodeIntersected = true;
                break;

            }
        }

        if (!nodeIntersected) {
            indexOfHighlightedNode = -1;
        }

        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        lastMousePressType = MouseType.MOUSE_1;
        mouseX = (int) x;
        mouseY = (int) y;

        System.out.println("mX: " + mouseX + " mY: " + mouseY);
        System.out.println("sX: " + x + " sY: " + y);

        mousePos.x = mouseX;
        mousePos.y = mouseY;

        Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));

        lastMousePress = worldPos;

        Rectangle rect = new Rectangle(worldPos.x-4, worldPos.y-4, 8, 8);

        Boolean buttonIntersected = false;
        //Back button detection
        if (backButton.contains(mouseX, (HEIGHT - mouseY))) {
            buttonIntersected = true;
            lastCamePos = cam.position;
            nodeshotGame.setScreen(BaseClass.menuScreen);
        }

        //Delete button detection
        if (deleteButton.contains(mouseX, (HEIGHT - mouseY))) {
            buttonIntersected = true;

            if (selectedType == EntityType.NODE && selectedIndex != -1) {
                System.out.println("CHECKING !!!!!!!!!!!!!!!!!!!!!!");
                for (Node n : nodelist) {
                    if (n.getID() == selectedIndex) {
                        n.remove();
                        System.out.println("DELETING !    !!!!!!");
                        break;
                    }
                }
            } else
            if(nodelist.size() != 0) {
                nodelist.get(nodelist.size() - 1).remove();
            }
        }

        //Build button detection
        if (buildButton.contains(mouseX, (HEIGHT - mouseY))) {
            buttonIntersected = true;
            buildMode = !buildMode;
        }

        //Highlighting
        if (!buttonIntersected) checkHighlights();

        if (indexOfHighlightedNode == -1 && buttonIntersected == false && buildMode == true) {
            //&& Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
            Node newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
            nodelist.add(newnode);
            buildMode = false;
        }
        //	break;

			/*case Input.Buttons.RIGHT: System.out.println("RIGHTdown");
				lastMousePressType = MouseType.MOUSE_3;

				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					if(nodelist.size() != 0) {
						nodelist.get(nodelist.size() - 1).remove();
					}
				}
			break;
		}
		*/

        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        debugMessage = "Flinging";
        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        debugMessage = "Panning dX" + deltaX + " dY: " + deltaY;
        mouseX = (int) x;
        mouseY = (int) y;

        //Getting old pos
        Vector2 previousMousePos = new Vector2(mousePos.x, mousePos.y);

        //Setting current (new) pos
        mousePos.x = mouseX;
        mousePos.y = mouseY;

        Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));

        lastMousePress = worldPos;

        Vector3 previousWorldPos = cam.unproject(new Vector3(previousMousePos.x, previousMousePos.y, 0));

        if (indexOfHighlightedNode != -1 && draggingConnection == false) {
            Node n = nodelist.get(indexOfHighlightedNode);
            double distance = Math.hypot(n.getCX() - lastMousePress.x, n.getCY() - lastMousePress.y);

            Node highlightedNode = nodelist.get(indexOfHighlightedNode);
            System.out.println("DISTANCE IS: " + distance);

            System.out.println("worldPos.x: " + worldPos.x + " y: " + worldPos.y + "  -  mousePos.x" + lastMousePress.x + " y: " + lastMousePress.y);
            //Basically, if the cursor is still in the node area when first drag is called, initiate a new dragging connection
            //A way to prevent bugs, a more simple way could be used, but this should not cause issues
            if (distance <= highlightedNode.radius) {
                newConnectionFromIndex = highlightedNode.getIndex();
                draggingConnection = true;
            }
        } else
        if (indexOfHighlightedNode != -1 && draggingConnection == true ) {
            //Dragging a connection action //TODO: Maybe implement some info later
            System.out.println("drag action --");
        } else {
            float xPos = previousWorldPos.x - worldPos.x;
            float yPos = previousWorldPos.y - worldPos.y;
            System.out.println("xPos: " + xPos + " yPos: " + yPos);
            if (!zooming) {
                cam.translate(xPos, yPos, 0);
            }
        }

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        mouseX = (int) x;
        mouseY = (int) y;

        debugMessage = "- TOUCH UP AT " + x + " " + y;

        System.out.println("mX: " + mouseX + " mY: " + mouseY);
        System.out.println("sX: " + x + " sY: " + y);

        mousePos.x = mouseX;
        mousePos.y = mouseY;

        Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));

        lastMousePress = worldPos;

        if (draggingConnection == true) {
            Rectangle rect = new Rectangle(worldPos.x-4, worldPos.y-4, 8, 8);

            //If there is no node yet, create one
            if ((nodelist.size() > 0)) {
                Boolean nodeIntersected = false;
                for(int i = 0; i < nodelist.size(); i++) {
                    if(nodelist.get(i).getBoundingRectangle().overlaps(rect)) {
                        if (nodelist.get(i) != nodelist.get(indexOfHighlightedNode)) {
                            nodelist.get(newConnectionFromIndex).connectTo(nodelist.get(i));
                        }

                        indexOfHighlightedNode = nodelist.get(i).getIndex();
                        newConnectionFromIndex = -1;

                        nodeIntersected = true;
                        break;
                    }
                }
                if (!nodeIntersected) {
                    Node newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
                    nodelist.add(newnode);
                    nodelist.get(indexOfHighlightedNode).connectTo(newnode);
                    indexOfHighlightedNode = newnode.getIndex();
                    highlightedIndex = newnode.getID();
                    selectedIndex = newnode.getID();
                    newConnectionFromIndex = indexOfHighlightedNode;
                }
                draggingConnection = false;
            }
        }
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        zooming = true;
        debugMessage = "Zooming iD: " + initialDistance + " dist: " + distance;

        float difference = distance - initialDistance;

        if (difference >= 0) {
            cam.zoom -= 0.03;
        } else {
            cam.zoom += 0.03;
        }

        zooming = false;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2)
    {
        /*float deltaX = pointer2.x - pointer1.x;
        float deltaY = pointer2.y - pointer1.y;

        double distance = Math.hypot(pointer1.x - pointer2.x, pointer1.x - pointer2.y);
        double initialDistance = Math.hypot(initialPointer1.x - initialPointer2.x, initialPointer1.x - initialPointer2.y);

        debugMessage = "Pinching " + deltaX + " " + deltaY + " distance " + distance + " initialDist? " + initialDistance;

        if (distance > initialDistance) {
            cam.zoom -= 0.08;
        } else {
            cam.zoom += 0.08;
        }
        */
        return true;
    }

    @Override
    public void pinchStop() {
        debugMessage = "pinchStop";
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		/*mouseX = screenX;
		mouseY = screenY;

		debugMessage = "- TOUCH UP AT " + screenX + " " + screenY;

		System.out.println("mX: " + mouseX + " mY: " + mouseY);
		System.out.println("sX: " + screenX + " sY: " + screenY);

		mousePos.x = mouseX;
		mousePos.y = mouseY;

		Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));

		lastMousePress = worldPos;

		if (draggingConnection == true) {
			Rectangle rect = new Rectangle(worldPos.x-4, worldPos.y-4, 8, 8);

			//If there is no node yet, create one
			if ((nodelist.size() > 0)) {
				Boolean nodeIntersected = false;
				for(int i = 0; i < nodelist.size(); i++) {
					if(nodelist.get(i).getBoundingRectangle().overlaps(rect)) {
						if (nodelist.get(i) != nodelist.get(indexOfHighlightedNode)) {
							nodelist.get(newConnectionFromIndex).connectTo(nodelist.get(i));
						}

						indexOfHighlightedNode = nodelist.get(i).getIndex();
						newConnectionFromIndex = -1;

						nodeIntersected = true;
						break;
					}
				}
				if (!nodeIntersected) {
					Node newnode = new Node(worldPos.x, worldPos.y, radius);
					nodelist.add(newnode);
					nodelist.get(indexOfHighlightedNode).connectTo(newnode);
					indexOfHighlightedNode = newnode.getIndex();
					newConnectionFromIndex = indexOfHighlightedNode;
				}
				draggingConnection = false;B
			}
		}*/
        Gdx.app.log("BaseClass", "Touch up !");
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}