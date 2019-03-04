package org.dudss.nodeshot;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.dudss.nodeshot.error.ErrorManager;
import org.dudss.nodeshot.error.ErrorReporter;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.MenuScreen;
import org.dudss.nodeshot.screens.SplashScreen;
import org.dudss.nodeshot.utils.Shaders;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;

/**The core game setup class.*/
public class BaseClass extends Game {
	public static Screen mainGameScreen;
	public static Screen menuScreen;
	
	public static SplashScreen splashScreen;
	
	/**Version string*/
	public static String ver;
	
	/**The global logger*/
	public static Logger logger;
	
	/**Time of the application start*/
	public static long startTime = System.currentTimeMillis();
	
	/**Global error manager used to invoke individual {@link ErrorReporter} objects*/
	public static ErrorManager errorManager;
	
	public BaseClass(String ver) {
		this.ver = ver;
	}
	
	@Override
	public void create() {		
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$s]: %5$s %n");
		//System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
		
		logger = Logger.getAnonymousLogger();
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINER);
		ConsoleHandler handler = new ConsoleHandler();		
        handler.setLevel(Level.FINER);
        
        logger.addHandler(handler);

		errorManager = new ErrorManager();
		//Splash screen is going to be listening to loader information
		splashScreen = new SplashScreen();
		//Loading resources
        Shaders.load();
        VisUI.load(new Skin(Gdx.files.local("textureData/assets/neutralizerui/neutralizer-ui.json")));
        SpriteLoader.loadAll();
        
        //Waiting for the splash screen to finish (should happen immediately)
        while(!splashScreen.isLoaded()) {
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				errorManager.report(e, "Launcher interrupted!");
			}
        }
        
        //Starting up
        menuScreen = new MenuScreen(this);		
		setScreen(menuScreen);	
		
		mainGameScreen = new GameScreen(this);		
	}
	
	@Override
	public void dispose() {
		menuScreen.dispose();
		mainGameScreen.dispose();
		SpriteLoader.unloadAll();
	}
}