package org.dudss.nodeshot.inputs;

import org.dudss.nodeshot.entities.ConveyorNode;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.Selector;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static org.dudss.nodeshot.screens.GameScreen.*;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.Building;
import org.dudss.nodeshot.buildings.IronMine;
import org.dudss.nodeshot.buildings.ManualCoalMine;
import org.dudss.nodeshot.buildings.Storage;

public class DesktopInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown(int keycode) {		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		switch (button) {
			case Input.Buttons.LEFT: 				
				mouseX = Gdx.input.getX();
				mouseY = Gdx.input.getY();		
				mousePos.x = mouseX;;
				mousePos.y = mouseY;		
	
				Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
				lastMousePress = worldPos;
				lastMousePressType = MouseType.MOUSE_1;
				
				//Building
				if (GameScreen.buildMode == true && GameScreen.builtBuilding != null) {
					GameScreen.builtBuilding.setLocation(worldPos.x, worldPos.y);
					GameScreen.builtBuilding.build();
					GameScreen.builtBuilding = null;
					GameScreen.builtConnector = null;
					GameScreen.buildMode = false;
				} else if (GameScreen.builtConnector != null) {
					Node newNode = null;
					if (builtConnector instanceof ConveyorNode) {					
						newNode = new ConveyorNode(worldPos.x, worldPos.y, Base.RADIUS);
					} else {
						newNode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
					}					
					Selector.selectNode(newNode);
					nodelist.add(newNode);
					newNode.recalculateCoords(worldPos.x, worldPos.y);
					
					GameScreen.builtBuilding = null;
					GameScreen.builtConnector = null;
					GameScreen.buildMode = false;
				} else {
					GameScreen.checkHighlights(true);	
				}
				
				//Cancel right click menu if one is present
				GameScreen.rightClickMenuManager.removeMenu();
				
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					Node newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
					Selector.selectNode(newnode);
					nodelist.add(newnode);
				}			
				
				if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
					Building coalMine = new ManualCoalMine(worldPos.x, worldPos.y);
					coalMine.build();
					
				}
			break;
			
			case Input.Buttons.RIGHT:
				mouseX = Gdx.input.getX();
				mouseY = Gdx.input.getY();		
				mousePos.x = mouseX;
				mousePos.y = mouseY;		

				worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
				lastMousePress = worldPos;
				lastMousePressType = MouseType.MOUSE_3;

				Entity clickedEntity = GameScreen.checkHighlights(true);				

				GameScreen.buildMode = false;
				GameScreen.builtBuilding = null;
				
				if (clickedEntity == null && GameScreen.selectedID != -1){
					Selector.deselect();
					GameScreen.rightClickMenuManager.removeMenu();
				} else {					
					if (rightClickMenuManager.rightClickMenu != null) {
						if (!(clickedEntity == rightClickMenuManager.rightClickMenu.getAssignedEntity())) {
							GameScreen.rightClickMenuManager.createMenu(clickedEntity);
						}
					} else {
						GameScreen.rightClickMenuManager.createMenu(clickedEntity);
					}
				}	
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					if(nodelist.size() != 0) {
						nodelist.get(nodelist.size() - 1).remove();
					}
				}			
				if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
					Building coalStorage = new Storage(worldPos.x, worldPos.y);
					coalStorage.build();
				}
			break;
				case Input.Buttons.MIDDLE: mouseX = Gdx.input.getX();
				mouseY = Gdx.input.getY();		
				mousePos.x = mouseX;
				mousePos.y = mouseY;		
	
				worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
				lastMousePress = worldPos;
				lastMousePressType = MouseType.MOUSE_2;
				
				if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
					Building ironMine = new IronMine(worldPos.x, worldPos.y);
					ironMine.build();
				}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		switch (button) {
			case Input.Buttons.RIGHT:
				mouseX = Gdx.input.getX();
				mouseY = Gdx.input.getY();
				
				mousePos.x = mouseX;
				mousePos.y = mouseY;
				
				Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
				
				lastMousePress = worldPos;
				
				if (draggingConnection == true) {
					Rectangle rect = new Rectangle(worldPos.x-0.5f, worldPos.y-0.5f, 1, 1);
					
					//If there is no node yet, create one
					if ((nodelist.size() > 0)) {
						Boolean nodeIntersected = false;
						for(int i = 0; i < nodelist.size(); i++) {		
							if(nodelist.get(i).getBoundingRectangle().overlaps(rect)) {
							if (nodelist.get(i) != nodelist.get(selectedIndex)) {
								nodelist.get(newConnectionFromIndex).connectTo(nodelist.get(i));
							}
							
							Selector.selectNode(nodelist.get(i));
							newConnectionFromIndex = -1;
							
							nodeIntersected = true;
							break;
						}
					}				
					if (!nodeIntersected) {
						Node newnode = null;
						if (nodelist.get(selectedIndex) instanceof ConveyorNode) {
							newnode = new ConveyorNode(worldPos.x, worldPos.y, Base.RADIUS);
						} else {
							newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
						}
						nodelist.add(newnode);
						nodelist.get(selectedIndex).connectTo(newnode);
						
						Selector.selectNode(newnode);
						
						newConnectionFromIndex = selectedIndex;				
					}
						draggingConnection = false;
					}
				}
			break;
			
			case Input.Buttons.LEFT: break;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		mouseX = Gdx.input.getX();
		mouseY = Gdx.input.getY();
		
		//Getting old pos
		Vector2 previousMousePos = new Vector2(mousePos.x, mousePos.y);
		
		//Setting current (new) pos
		mousePos.x = mouseX;
		mousePos.y = mouseY;
		
		Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));

		Vector3 previousWorldPos = cam.unproject(new Vector3(previousMousePos.x, previousMousePos.y, 0));
		
		if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			//Cancel right click menu if one is present
			GameScreen.rightClickMenuManager.removeMenu();
			
			if (selectedIndex != -1 && selectedType == EntityType.NODE && draggingConnection == false) {
				Node highlightedNode = nodelist.get(selectedIndex);
	            double distance = Math.hypot( highlightedNode.getCX() - lastMousePress.x,  highlightedNode.getCY() - lastMousePress.y);
	            
	            //Basically, if the cursor is still in the node area when first drag is called, initiate a new dragging connection
	            //A way to prevent bugs, a more simple way could be used, but this should not cause issues
	            if (distance <= highlightedNode.radius) {
	                newConnectionFromIndex = highlightedNode.getIndex();
	                draggingConnection = true;
	            }
			} else if (selectedIndex != -1 && draggingConnection == true ) {
				//Dragging a connection action //TODO: Maybe implement some info later
				//System.out.println("drag action --");
			} 			
			
		} else if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if (draggingConnection == false) {
				float xPos = previousWorldPos.x - worldPos.x;
				float yPos = previousWorldPos.y - worldPos.y;
				cam.translate(xPos, yPos, 0);		
			}
		}
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		mouseX = screenX;
		mouseY = screenY;
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		//Cancel right click menu if one is present
		GameScreen.rightClickMenuManager.removeMenu();
		
		if (amount == 1) {
			cam.zoom += 0.08;
		} else {
			cam.zoom -= 0.08;
		}
		return false;
	}
}
