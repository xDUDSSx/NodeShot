package org.dudss.nodeshot.desktop;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.SwingUtilities;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.screens.MenuScreen;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**Desktop launcher that initialises and configures the lwjgl application.*/
public class DesktopLauncher {
	
	/**NodeEngine development version*/
	public static final String ver = "v4.0";
	
	/**Global error manager used to invoke individual {@link ErrorReporter} objects*/
	public static ErrorManager errorManager;
	
	/**The main method*/
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = Base.defaultWindowSize.width;
		config.height = Base.defaultWindowSize.height;
		config.foregroundFPS = Base.foregroundFps;
		config.samples = Base.MSAAsamples;
		config.vSyncEnabled = Base.vSyncEnabled;
		config.title = "NodeEngine." + ver + " OpenGL";
		config.fullscreen = Base.fullscreen;
		
		errorManager = new ErrorManager();
		
		//Dialogs.showErrorDialog(GameScreen.stage, "A runtime error occured.", ex);
		
		//Uncaught error handling
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException (Thread thread, final Throwable ex) {	  	        					
				//Call the global error manager and invoke a reporter to display the exception.
				errorManager.report(ex, "An unexpected runtime error occurred!");				
			}
		});
		
		new LwjglApplication(new BaseClass(), config);
		MenuScreen.ver = ver;
	}
}
