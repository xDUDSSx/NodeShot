package org.dudss.nodeshot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.dudss.nodeshot.error.ErrorManager;
import org.dudss.nodeshot.error.ErrorReporter;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.MenuScreen;

public class BaseClass extends Game {

	public static Screen mainGameScreen;
	public static Screen menuScreen;
	
	/**The global logger*/
	public static Logger logger;
	
	/**Time of the application start*/
	public static long startTime = System.currentTimeMillis();
	
	/**Global error manager used to invoke individual {@link ErrorReporter} objects*/
	public static ErrorManager errorManager;
	
	@Override
	public void create() {
			System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
			//System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$s]: %5$s %n");
			//System.setProperty("java.util.logging.SimpleFormatter.format", "[%p] %t: %m");
		
			logger = Logger.getLogger(Base.class.getSimpleName());
			logger.setLevel(Level.FINER);
			
			errorManager = new ErrorManager();
			
			MenuScreen menuScreen = new MenuScreen(this);
			
			//Creating game screen
			mainGameScreen = new GameScreen(this);		
			//Starting up menu
			setScreen(menuScreen);							
	}
}