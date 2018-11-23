package org.dudss.nodeshot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

import org.dudss.nodeshot.error.ErrorManager;
import org.dudss.nodeshot.error.ErrorReporter;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.MenuScreen;

public class BaseClass extends Game {

	public static Screen mainGameScreen;
	public static Screen menuScreen;

	/**Time of the application start*/
	public static long startTime = System.currentTimeMillis();
	
	/**Global error manager used to invoke individual {@link ErrorReporter} objects*/
	public static ErrorManager errorManager;
	
	@Override
	public void create() {
			errorManager = new ErrorManager();
		
			//Creating game screen
			mainGameScreen = new GameScreen(this);		
			//Starting up menu
			setScreen(new MenuScreen(this));							
	}
}