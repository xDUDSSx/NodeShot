package org.dudss.nodeshot.desktop;

import java.lang.Thread.UncaughtExceptionHandler;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.screens.MenuScreen;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**Desktop launcher that initialises and configures the lwjgl application.*/
public class DesktopLauncher {
	
	/**NodeEngine development version
	 * v5.0 - corruption optimisation update
	 * v5.1 - fog of war update
	 * v6.0 - building update
	 * v6.1 - package system update
	 * v6.2 - build mode interface update
	 * v6.3 - turret update
	 * v6.4 - conveyor update
	 * v7.0 - terrain update
	 * v7.1 - effects update
	 * */
	public static final String ver = "v7.1";
	
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
		config.addIcon("textureData/assets/icon32.png", FileType.Local);
		
		//Dialogs.showErrorDialog(GameScreen.stage, "A runtime error occured.", ex);

		MenuScreen.ver = ver;
		MenuScreen.subver = "Effects update";
		
		//Uncaught error handling
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException (Thread thread, final Throwable ex) {	  	        					
				//Call the global error manager and invoke a reporter to display the exception.
				org.dudss.nodeshot.BaseClass.errorManager.report(ex, "An unexpected runtime error occurred! (in Thread: " + thread.getName() + ")");						
			}
		});
		
		new LwjglApplication(new BaseClass(ver), config);
	}
}
