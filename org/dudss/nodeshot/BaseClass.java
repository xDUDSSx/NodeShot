package org.dudss.nodeshot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.MenuScreen;

public class BaseClass extends Game {

	public static Screen mainGameScreen;
	public static Screen menuScreen;

	public static long startTime = System.currentTimeMillis();
	
	@Override
	public void create() {
		//Creating game screen
		mainGameScreen = new GameScreen(this);
		
		setScreen(new MenuScreen(this));	
	}
}