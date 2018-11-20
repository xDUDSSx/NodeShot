package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.MenuScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

public class PauseMenu extends VisWindow {

	VisTable table;
	
	public PauseMenu(boolean showWindowBorder) {
		super("Pause menu" , showWindowBorder);
		setVisible(false);
		setMovable(true);
		setResizable(true);
		resize();

		align(Align.top);
		addVisWidgets();
	}

	public void resize() {
		setBounds();
	}
	
	private void setBounds() {
		setPosition(Gdx.graphics.getWidth()*0.27f, Gdx.graphics.getHeight()*0.25f);
		setSize(Gdx.graphics.getWidth()*0.46f, Gdx.graphics.getHeight()*0.5f);		
	}
	
	private void addVisWidgets() {
		VisTextButton mainMenuButton = new VisTextButton("Back to main menu");

		VisTable buttonTable = new VisTable(true);
		buttonTable.align(Align.center);
		buttonTable.add(mainMenuButton);
		
		add(buttonTable).row();
				
		mainMenuButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				GameScreen.lastZoom = GameScreen.cam.zoom;
				GameScreen.lastCamPos = GameScreen.cam.position;
				GameScreen.nodeshotGame.setScreen(new MenuScreen(GameScreen.nodeshotGame));		
		    }
	    });
	}
}
