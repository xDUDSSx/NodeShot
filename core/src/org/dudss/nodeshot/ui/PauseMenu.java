package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.MenuScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class PauseMenu extends VisWindow {

	VisTable table;
	
	public PauseMenu(boolean showWindowBorder) {
		super(" Pause menu" , true);
		setVisible(false);
		setMovable(true);
		setResizable(true);
		resize();
		//debugAll();
		align(Align.top);
		addVisWidgets();
		
	}

	public void resize() {
		setBounds();
	}
	
	private void setBounds() {
		setPosition(Gdx.graphics.getWidth()/2 - this.getWidth()/2, Gdx.graphics.getHeight()/2 - this.getHeight()/2);
		//setSize(Gdx.graphics.getWidth()*0.46f, Gdx.graphics.getHeight()*0.5f);		
	}
	
	private void addVisWidgets() {
		VisTable buttonTable = new VisTable(true);
		buttonTable.align(Align.center);
		VisTextButton mainMenuButton = new VisTextButton("Back to main menu");	
		mainMenuButton.padLeft(100).padRight(100);
		buttonTable.add(mainMenuButton).fill(true);
		buttonTable.row();
		VisTextButton settingsButton = new VisTextButton("Settings");	
		buttonTable.add(settingsButton).fill(true);
		buttonTable.row();
		VisTextButton regenerateTerrain = new VisTextButton("Regenerate terrain");
		buttonTable.add(regenerateTerrain).fill();
		buttonTable.row();
		
		VisTextButton closeButton = new VisTextButton("Unpause");
		buttonTable.addSeparator().padBottom(50);
		buttonTable.add(closeButton).fill(true);
		buttonTable.row();
		
		VisTextButton exitButton = new VisTextButton("Exit");
		buttonTable.add(exitButton).fill(true);
		buttonTable.row();
		
		add(buttonTable).row();
		
		pack();
		
		mainMenuButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				GameScreen.lastZoom = GameScreen.cam.zoom;
				GameScreen.lastCamPos = GameScreen.cam.position;
				GameScreen.nodeshotGame.setScreen(new MenuScreen(GameScreen.nodeshotGame));		
		    }
	    });
		settingsButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (!Base.settingsOpened) {
					Base.settingsOpened = true;
					SettingsMenu settings = new SettingsMenu("Settings", GameScreen.skin);	
					GameScreen.stage.addActor(settings);
				}
		    }
	    });
		regenerateTerrain.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				GameScreen.chunks.generateAll();
				GameScreen.chunks.updateAllSectionMeshes(false);
		    }
	    });		
		closeButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				GameScreen.callPauseMenu();
		    }
	    });
		exitButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Gdx.app.exit();
		    }
	    });
	}
}
