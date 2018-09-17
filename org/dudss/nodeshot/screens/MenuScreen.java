package org.dudss.nodeshot.screens;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.entities.Connector;

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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {

    Game nodeshotGame;

    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    protected Skin skin;

    TextButton playButton;
    TextButton sendButton;
    TextButton exitButton;
    TextButton keepButton;
    TextButton closeButton;

    final TextField textField1;
    final TextField textField2;
    final TextField textField5;

    Image logo;

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

        playButton = new TextButton("Play", skin, "hoverfont30");
        sendButton = new TextButton("Send", skin, "hoverfont30");
        exitButton = new TextButton("Exit", skin, "hoverfont30");
        keepButton = new TextButton("Keep", skin, "hoverfont30");
        closeButton = new TextButton("Close node", skin, "hoverfont30");

        textField1 = new TextField("", skin, "font30");
        textField2 = new TextField("", skin, "font30");
        textField5 = new TextField("", skin, "font30");

        logo = new Image(logoTex);

        stage = new Stage(viewport, batch);
    }

    @Override
    public void show() {
        //Stage should controll input:
        Gdx.input.setInputProcessor(stage);

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        //mainTable.setFillParent(true);
        mainTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //Set alignment of contents in the table.
        mainTable.top();
        //mainTable.debugAll();
        //Create buttons

        System.out.println("ww " + mainTable.getWidth() + mainTable.getHeight());
        
        /*playButton.padLeft(100);
        playButton.padRight(100);

        sendButton.padLeft(100);
        sendButton.padRight(100);

        exitButton.padLeft(100);
        exitButton.padRight(100);

        keepButton.padLeft(100);
        keepButton.padRight(100);

        closeButton.padLeft(100);
        closeButton.padRight(100);
         */

        //Add listeners to buttons
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nodeshotGame.setScreen(BaseClass.mainGameScreen);
            }
        });
        sendButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String valueFrom = textField1.getText();
                String valueTo = textField2.getText();
                int indexFrom = Integer.parseInt(valueFrom);
                int indexTo = Integer.parseInt(valueTo);

                System.out.println("from: " + valueFrom + " to " + valueTo + " sending!");

                com.badlogic.gdx.graphics.Color color = new com.badlogic.gdx.graphics.Color((Base.getRandomIntNumberInRange(0, 255) / 255f),(Base.getRandomFloatNumberInRange(0, 255) / 255f),(Base.getRandomFloatNumberInRange(0, 255) / 255f), 1.0f);
                GameScreen.nodelist.get(indexFrom).sendPackage(GameScreen.nodelist.get(indexTo), color);
            }
        });
        keepButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final Color color = new Color((Base.getRandomIntNumberInRange(0, 255) / 255f),(Base.getRandomFloatNumberInRange(0, 255) / 255f),(Base.getRandomFloatNumberInRange(0, 255) / 255f), 1.0f);

                class generatePackages implements Runnable {
                    int loops = 0;

                    int from = Integer.valueOf(textField1.getText());
                    int to = Integer.valueOf(textField2.getText());

                    @Override
                    public void run() {
                        if (loops >= 200) {
                            return;
                        }

                        Boolean isClear = true;
                        for (Connector nC : GameScreen.connectorHandler.getAllConnectorsToNode(GameScreen.nodelist.get(from))) {
                            Boolean clear = nC.checkEntrance(GameScreen.nodelist.get(from), Base.PACKAGE_BLOCK_RANGE);
                            if (clear == false) {
                                isClear = false;
                            }
                        }

                        if (isClear) {
                            GameScreen.nodelist.get(from).sendPackage(GameScreen.nodelist.get(to), color);
                            loops++;
                        }
                    }

                    int getLoops() {
                        return loops;
                    }
                }

                generatePackages gP = new generatePackages();

                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                service.scheduleAtFixedRate(gP, 0, 1000, TimeUnit.MILLISECONDS);

                if (gP.getLoops() >= 100) {
                    service.shutdown();
                }
            }
        });

        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Boolean isClosed = GameScreen.nodelist.get(Integer.valueOf(textField5.getText())).isClosed();
                GameScreen.nodelist.get(Integer.valueOf(textField5.getText())).setClosed(!isClosed);
            }
        });

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        //Add buttons to table
        logo.setScaling(Scaling.fit);
        mainTable.add(logo).expand().fill().colspan(2);
        mainTable.row();
        mainTable.add(playButton).pad(10).colspan(2).fill(true);
        mainTable.row();
        mainTable.add(textField1).pad(10).fill(true);
        mainTable.add(textField2).pad(10).fill(true);
        mainTable.row();
        mainTable.add(sendButton).pad(10).fill(true);
        mainTable.add(keepButton).pad(10).fill(true);
        mainTable.row();
        mainTable.add(textField5).pad(10).fill(true).colspan(2);
        mainTable.row();
        mainTable.add(closeButton).pad(10).colspan(2).fill(true);
        mainTable.row();
        mainTable.add(exitButton).pad(10).colspan(2).fill(true);

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
    }
}