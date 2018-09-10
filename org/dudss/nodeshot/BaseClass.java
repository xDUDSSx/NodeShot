package org.dudss.nodeshot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.MenuScreen;

public class BaseClass extends Game {

	public static Screen mainGameScreen;
	public static Screen menuScreen;

	@Override
	public void create() {
		//Creating game screens
		mainGameScreen = new GameScreen(this);
		//menuScreen = new MenuScreen(this);
		//Setting game screen
		//setScreen(menuScreen);
		setScreen(new MenuScreen(this));	
	}
}