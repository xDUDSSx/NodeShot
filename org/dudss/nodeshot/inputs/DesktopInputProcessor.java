package org.dudss.nodeshot.inputs;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.Selector;

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
import org.dudss.nodeshot.buildings.CoalMine;
import org.dudss.nodeshot.buildings.IronMine;
import org.dudss.nodeshot.buildings.Storage;

public class DesktopInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
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

				GameScreen.checkHighlights();
							
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					Node newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
					Selector.selectNode(newnode);
					nodelist.add(newnode);
				}			
				
				if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
					Building coalMine = new CoalMine(worldPos.x, worldPos.y);
					coalMine.build();
					buildingHandler.addBuilding(coalMine);
				}
			break;
			
			case Input.Buttons.RIGHT: System.out.println("RIGHTdown");
				mouseX = Gdx.input.getX();
				mouseY = Gdx.input.getY();		
				mousePos.x = mouseX;
				mousePos.y = mouseY;		

				worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
				lastMousePress = worldPos;
				lastMousePressType = MouseType.MOUSE_3;
				
				GameScreen.checkHighlights();
				
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					if(nodelist.size() != 0) {
						nodelist.get(nodelist.size() - 1).remove();
					}
				}			
				if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
					Building coalStorage = new Storage(worldPos.x, worldPos.y);
					coalStorage.build();
					buildingHandler.addBuilding(coalStorage);
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
					buildingHandler.addBuilding(ironMine);
				}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		switch (button) {
			case Input.Buttons.LEFT: System.out.println("LEFTup");
				mouseX = Gdx.input.getX();
				mouseY = Gdx.input.getY();
				
				System.out.println("mX: " + mouseX + " mY: " + mouseY);
				System.out.println("sX: " + screenX + " sY: " + screenY);
				
				mousePos.x = mouseX;
				mousePos.y = mouseY;
				
				Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
				
				lastMousePress = worldPos;
				
				if (draggingConnection == true) {
					Rectangle rect = new Rectangle(worldPos.x-4, worldPos.y-4, 8, 8);
					
					//If there is no node yet, create one
					if ((nodelist.size() > 0)) {
						Boolean nodeIntersected = false;
						for(int i = 0; i < nodelist.size(); i++) {		
							if(nodelist.get(i).getBoundingRectangle().overlaps(rect)) {
							if (nodelist.get(i) != nodelist.get(selectedIndexo)) {
								nodelist.get(newConnectionFromIndex).connectTo(nodelist.get(i));
							}
							
							Selector.selectNode(nodelist.get(i));
							newConnectionFromIndex = -1;
							
							nodeIntersected = true;
							break;
						}
					}				
					if (!nodeIntersected) {
						Node newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
						nodelist.add(newnode);
						nodelist.get(selectedIndexo).connectTo(newnode);
						
						Selector.selectNode(newnode);
						
						newConnectionFromIndex = selectedIndexo;				
					}
						draggingConnection = false;
					}
				}
			break;
			
			case Input.Buttons.RIGHT: System.out.println("RIGHTup");break;
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
		
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if (selectedIndexo != -1 && selectedType == EntityType.NODE && draggingConnection == false) {
				Node highlightedNode = nodelist.get(selectedIndexo);
	            double distance = Math.hypot( highlightedNode.getCX() - lastMousePress.x,  highlightedNode.getCY() - lastMousePress.y);
	            System.out.println("DISTANCE IS: " + distance);
	            System.out.println("worldPos.x: " + worldPos.x + " y: " + worldPos.y + "  -  mousePos.x" + lastMousePress.x + " y: " + lastMousePress.y);
	            
	            //Basically, if the cursor is still in the node area when first drag is called, initiate a new dragging connection
	            //A way to prevent bugs, a more simple way could be used, but this should not cause issues
	            if (distance <= highlightedNode.radius) {
	                newConnectionFromIndex = highlightedNode.getIndex();
	                draggingConnection = true;
	            }
			} else if (selectedIndexo != -1 && draggingConnection == true ) {
				//Dragging a connection action //TODO: Maybe implement some info later
				//System.out.println("drag action --");
			} 			
			if (draggingConnection == false) {
				float xPos = previousWorldPos.x - worldPos.x;
				float yPos = previousWorldPos.y - worldPos.y;
				//System.out.println("xPos: " + xPos + " yPos: " + yPos);
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
		if (amount == 1) {
			cam.zoom += 0.08;
		} else {
			cam.zoom -= 0.08;
		}
		return false;
	}
}
