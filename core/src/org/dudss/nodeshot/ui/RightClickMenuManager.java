package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.screens.GameScreen;

public class RightClickMenuManager {
	public static RightClickWindow rightClickMenu;
	
	public void createMenu(Entity e) {
		if (e != null) {
			if (rightClickMenu != null) {
				rightClickMenu.remove();
			}
			rightClickMenu = new RightClickWindow(GameScreen.skin, e);
	        GameScreen.stage.addActor(rightClickMenu);
	        //System.out.println("Added new rightClickMenu");
		} else if (GameScreen.selectedID == -1){
			if (rightClickMenu != null) {
				rightClickMenu.remove();
			}
			rightClickMenu = new RightClickWindow(GameScreen.skin);
	        GameScreen.stage.addActor(rightClickMenu);
	        //System.out.println("Added new null rightClickMenu");
		}
	}
	
	public void removeMenu() {
		if (rightClickMenu != null) {
			rightClickMenu.remove();
			rightClickMenu = null;
		}
	}	
	
	public void update() {
		if (rightClickMenu != null) {
			rightClickMenu.refreshText();
		}
	}
}
