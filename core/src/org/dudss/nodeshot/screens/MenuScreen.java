package org.dudss.nodeshot.screens;

import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.utils.Shaders;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {

    Game nodeshotGame;
    
    float aspectRatio;
    
    public static String ver;
    
    private SpriteBatch batch;
    protected Stage stage;
    private FillViewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    protected Skin skin;

    Table mainTable;
    
    Label version;
    Label emptyLabel;
    
    TextButton playButton;
    TextButton sendButton;
    TextButton exitButton;
    TextButton generateButton;
    TextButton closeButton;
    
    ShapeRenderer sR;
    SpriteBatch b;
    
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
        	
        	logoTex = new Texture(Gdx.files.internal("nodeenginelogo.png"));
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	atlas = new TextureAtlas("res/data/uiskin.atlas");
        	skin = new Skin(Gdx.files.classpath("res/data/uiskin.json"), atlas);
        	logoTex = new Texture(Gdx.files.classpath("res/data/nodeenginelogo.png"));
        }
       
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FillViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        version = new Label(ver + " - ALPHA", skin, "font30");        
        emptyLabel = new Label("", skin, "font30");
        
        playButton = new TextButton("Start", skin, "hoverfont60");
        sendButton = new TextButton("Send", skin, "hoverfont30");
        exitButton = new TextButton("Exit", skin, "hoverfont60");
        generateButton = new TextButton("Generate terrain", skin, "hoverfont30");
        closeButton = new TextButton("Close node", skin, "hoverfont30");

        logo = new Image(logoTex);

        stage = new Stage(viewport, batch);
    }

    @Override
    public void show() {
        //Stage should control input:
        Gdx.input.setInputProcessor(stage);

        sR = new ShapeRenderer();
        sR.setColor(Color.DARK_GRAY);
        
        b = new SpriteBatch();
        
        //Create Table
        mainTable = new Table();
        
        mainTable.setSize((float)(Gdx.graphics.getWidth()) * 0.5f, Gdx.graphics.getHeight() * 0.9f);
        mainTable.top();
        //mainTable.debugAll();        
        mainTable.setPosition((Gdx.graphics.getWidth()) * 0.25f, 100);
        
        //Add buttons to table
        logo.setScaling(Scaling.fit);
        //logo.setScale(0.6f);
        mainTable.add(logo).fill(true).colspan(2);
        mainTable.row();
        mainTable.add(version).pad(10).padTop(4).center().fill(true);
        mainTable.row();
        mainTable.add(playButton).pad(10).colspan(2).fill(true).padTop(60);
        mainTable.row();
        mainTable.add(exitButton).pad(10).colspan(2).fill(true);
        mainTable.row();
        /*mainTable.add(generateButton).pad(10).colspan(2).fill(true);
        mainTable.row();
        mainTable.add(coalheightmap).fill(true).width(mainTable.getWidth()/2 - 100).height(mainTable.getWidth()/2 - 100);
        mainTable.add(ironheightmap).fill(true).width(mainTable.getWidth()/2 - 100).height(mainTable.getWidth()/2 - 100);
        mainTable.row();
        mainTable.add(new Label("coal", skin, "font30")).fill(true);
        mainTable.add(new Label("iron ore", skin, "font30")).fill(true);
        */
        //mainTable.add(newImg).fill(true);
        
        //Add table to stage
        stage.addActor(mainTable);
        
        if (GameScreen.startedOnce) {
        	playButton.setText("Resume");
        }
        
        //Add listeners to buttons
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {           	
                nodeshotGame.setScreen(BaseClass.mainGameScreen);
                if (GameScreen.startedOnce == false) {
                	GameScreen.startSimulationThread();
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

    private void updateMenuTable(int width, int height) {
    	 mainTable.setSize(width * 0.5f, height * 0.8f);
         mainTable.top();
         //mainTable.debugAll();        
         mainTable.setPosition(width * 0.25f, 100);
    }
    
    @Override
    public void render(float delta) {
    	b.setProjectionMatrix(camera.combined);
        sR.setProjectionMatrix(camera.combined);
         
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackgroundClouds(b);
        
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
		aspectRatio = (float)width/(float)height;
	     
	    camera.viewportWidth = width;
	    camera.viewportHeight = height;
	    camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
	    camera.update();
		        
        Viewport stageViewport = new FillViewport(width, height);
        stage.setViewport(stageViewport);
        stage.getViewport().update(width, height, true);
        
        updateMenuTable(width, height);      
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
    
    public void drawBackgroundClouds(SpriteBatch batch) {
        //Getting the time this application has run for, the is fed to the cloud shader and makes it "animated"
    	float secondsSinceStartup = ((System.currentTimeMillis() - BaseClass.startTime) / 1000f);
    	
    	//Creating a basic screen matrix
        Matrix4 uiMatrix = new Matrix4();
        uiMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        //Mesh vertexes that correspond to a single quad covering the screen
        float[] verts = new float[] {0, 0, Color.toFloatBits(1f, 0, 0, 1f), 0, 0, Gdx.graphics.getWidth(), 0, Color.toFloatBits(1f, 0, 0, 1f), 1, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Color.toFloatBits(1f, 0, 0, 1f), 1, 1, 0, Gdx.graphics.getHeight(), Color.toFloatBits(1f, 0, 0, 1f), 0, 1};        
        //cloud scale that is normalized to some predefined values
        float clampedScale = 1f;
        
        //Setting the shader uniforms
 		Shaders.rotatingCloudShader.begin();
 		//Flag indicating that the shader does not need all the uniforms that are expected by LibGdx (eg. texture, which is not used)
 		Shaders.rotatingCloudShader.pedantic = false;
 		//Setting the projection matrix
 		Shaders.rotatingCloudShader.setUniformMatrix("u_projTrans", uiMatrix);
 		//Setting the time uniform
 		Shaders.rotatingCloudShader.setUniformf("time", secondsSinceStartup);
 		//Setting the cloud scale
 		Shaders.rotatingCloudShader.setUniformf("zoom", clampedScale);
 		//Screen resolution
 		Shaders.rotatingCloudShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
 		//Offset from the original coordinates used to make the clouds scale up/down from the center of the screen rather than bottom right corner
 		Shaders.rotatingCloudShader.setUniformf("offset", clampedScale / 2f);
 		//Offset vector that reacts to the in-game camera position, making it look like the clouds are part of the scene
 		Shaders.rotatingCloudShader.setUniformf("pos", 0, 0);
 		Shaders.rotatingCloudShader.end(); 		        
        
        batch.begin();
        batch.setShader(Shaders.rotatingCloudShader);       
        //This particular batch function requires a texture to bind, so I'm binding the texture used later in chunk rendering 
        //TODO: stop using spritebatch and call the render from an actual Mesh object (will be cleaner and will not require an extra texture bind)
        batch.draw(SpriteLoader.tileAtlas.findRegion("tiledCoal").getTexture(), verts, 0, 20);
 		batch.end();
 		batch.setShader(Shaders.defaultShader);
    }
}