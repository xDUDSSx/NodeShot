package org.dudss.nodeshot.screens;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.algorithms.SimplexNoiseGenerator;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {

    Game nodeshotGame;

    public static String ver;
     
    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    protected Skin skin;

    Label version;
    Label emptyLabel;
    
    TextButton playButton;
    TextButton sendButton;
    TextButton exitButton;
    TextButton generateButton;
    TextButton closeButton;
    
    ShapeRenderer sR;
    SpriteBatch b;
    
    Sprite background;
    
    Image logo;

    Image coalheightmap;
    Image ironheightmap;
    
    Color semi = new Color(Color.rgba8888(0, 0, 0, 0.3f));
    
    public MenuScreen(Game game)
    {
        this.nodeshotGame = game;

        Texture logoTex = null;
        
        if (Gdx.app.getType() == ApplicationType.Android) {
        	atlas = new TextureAtlas(Gdx.files.internal("uiskin.atlas"));
        	skin = new Skin(Gdx.files.internal("uiskin.json"), atlas);
        	
        	logoTex = new Texture(Gdx.files.internal("nodelogo.png"));
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	atlas = new TextureAtlas("res/uiskin.atlas");
        	skin = new Skin(Gdx.files.classpath("res/uiskin.json"), atlas);
        	logoTex = new Texture(Gdx.files.classpath("res/nodelogo.png"));
        }
       
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        version = new Label(ver + " - ALPHA", skin, "font30");        
        emptyLabel = new Label("", skin, "font30");
        
        playButton = new TextButton("Play", skin, "hoverfont60");
        sendButton = new TextButton("Send", skin, "hoverfont30");
        exitButton = new TextButton("Exit", skin, "hoverfont60");
        generateButton = new TextButton("Generate terrain", skin, "hoverfont30");
        closeButton = new TextButton("Close node", skin, "hoverfont30");

        logo = new Image(logoTex);
        
        coalheightmap = new Image();
        ironheightmap = new Image();

        stage = new Stage(viewport, batch);
    }

    @Override
    public void show() {
        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        sR = new ShapeRenderer();
        sR.setColor(Color.DARK_GRAY);
        
        b = new SpriteBatch();
        
        background = new Sprite(new Texture("res/nodeintro.png"));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background.setPosition(0, 0);
        
        //Create Table
        Table mainTable = new Table();
        
        mainTable.setSize((float)(Gdx.graphics.getWidth()) * 0.5f, Gdx.graphics.getHeight() * 0.9f);
        mainTable.top();
        //mainTable.debugAll();        
        mainTable.setPosition((Gdx.graphics.getWidth()) * 0.25f, 100);
        
        //Add buttons to table
        logo.setScaling(Scaling.fit);
        logo.setScale(0.6f);
        mainTable.add(logo).fill(true).colspan(2);
        mainTable.row();
        mainTable.add(version).pad(10).center().fill(true);
        mainTable.row();
        mainTable.add(playButton).pad(10).colspan(2).fill(true).padTop(60);
        mainTable.row();
        mainTable.add(exitButton).pad(10).colspan(2).fill(true);
        mainTable.row();
        mainTable.add(generateButton).pad(10).colspan(2).fill(true);
        mainTable.row();
        mainTable.add(coalheightmap).fill(true).width(mainTable.getWidth()/2 - 100).height(mainTable.getWidth()/2 - 100);
        mainTable.add(ironheightmap).fill(true).width(mainTable.getWidth()/2 - 100).height(mainTable.getWidth()/2 - 100);
        mainTable.row();
        mainTable.add(new Label("coal", skin, "font30")).fill(true);
        mainTable.add(new Label("iron ore", skin, "font30")).fill(true);
        //mainTable.add(newImg).fill(true);
        
        //Add table to stage
        stage.addActor(mainTable);
        
        //Add listeners to buttons
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {           	
                nodeshotGame.setScreen(BaseClass.mainGameScreen);
                if (GameScreen.startedOnce == false) {
                	GameScreen.startSim();
                	GameScreen.startedOnce = true;
                }
            }
        });
        sendButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {              
            	//FREE BUTTON
            }
        });
        
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

       // b.begin();
       // background.draw(b);
       // b.end();
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        sR.begin(ShapeType.Filled);
        sR.rect((Gdx.graphics.getWidth()) * 0.25f - 25, 0,((float)(Gdx.graphics.getWidth()) * 0.5f) + 50 , Gdx.graphics.getHeight(), semi, semi, Color.BLACK, Color.BLACK);
        sR.end();
        
        Gdx.gl.glDisable(GL20.GL_BLEND);   
        
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
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
    public void dispose() {
        skin.dispose();
        atlas.dispose();
        sR.dispose();
        b.dispose();
    }
}