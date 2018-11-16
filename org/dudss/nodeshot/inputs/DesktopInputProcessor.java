package org.dudss.nodeshot.inputs;

import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.utils.Selector;
import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.buildings.Building;

import static org.dudss.nodeshot.screens.GameScreen.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class DesktopInputProcessor implements InputProcessor {
	@Override
	public boolean keyDown(int keycode) {		
		if (keycode == Keys.SPACE) {
			SimulationThread.pauseSim();
		}
		return true;
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
					GameScreen.builtBuilding.setLocation(worldPos.x, worldPos.y, true);
					GameScreen.builtBuilding.build();
					
					if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {					
						try {					
							Class<? extends Building> buildingClass = GameScreen.builtBuilding.getClass();
							Constructor buildingConstructor;
							buildingConstructor = buildingClass.getConstructor(new Class[] {float.class, float.class});
							Object[] buildingArgs = new Object[] { new Float(0), new Float(0) };
							GameScreen.builtBuilding = (Building) buildingConstructor.newInstance(buildingArgs);		
						} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							e.printStackTrace();
						}
											
					} else {
						GameScreen.builtBuilding = null;
						GameScreen.builtConnector = null;
						GameScreen.buildMode = false;
					}
				} else if (GameScreen.builtConnector != null) {
					Node newNode = null;
					if (builtConnector instanceof ConveyorNode) {					
						newNode = new ConveyorNode(worldPos.x, worldPos.y, Base.RADIUS);
					} else {
						newNode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
					}					
					Selector.selectNode(newNode);
					nodelist.add(newNode);
					newNode.setLocation(worldPos.x, worldPos.y, true);
					
					GameScreen.builtBuilding = null;
					GameScreen.builtConnector = null;
					GameScreen.buildMode = false;
				} else if (draggingConnection != true){
					GameScreen.checkHighlights(true);	
				}
				
				//Cancel right click menu if one is present
				GameScreen.rightClickMenuManager.removeMenu();
				
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					Node newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
					Selector.selectNode(newnode);
					nodelist.add(newnode);
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
				
				if (clickedEntity == null && GameScreen.selectedID != -1) {
					Selector.deselect();
					GameScreen.rightClickMenuManager.removeMenu();
				} else {					
					if (rightClickMenuManager.rightClickMenu != null && (!(clickedEntity == rightClickMenuManager.rightClickMenu.getAssignedEntity()))) {
						GameScreen.rightClickMenuManager.createMenu(clickedEntity);
					} else {
						GameScreen.rightClickMenuManager.createMenu(clickedEntity);
					}
				}	
				if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
					if(nodelist.size() != 0) {
						nodelist.get(nodelist.size() - 1).remove();
					}
				}			
			break;
				case Input.Buttons.MIDDLE: mouseX = Gdx.input.getX();
				mouseY = Gdx.input.getY();		
				mousePos.x = mouseX;
				mousePos.y = mouseY;		
	
				worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
				lastMousePress = worldPos;
				lastMousePressType = MouseType.MOUSE_2;
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
						Node newNode = null;
						if (nodelist.get(selectedIndex) instanceof ConveyorNode) {
							newNode = new ConveyorNode(worldPos.x, worldPos.y, Base.RADIUS);
						} else {
							newNode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
						}
						nodelist.add(newNode);
						nodelist.get(selectedIndex).connectTo(newNode);
						
						Selector.selectNode(newNode);
						newNode.setLocation(worldPos.x, worldPos.y, true);
						
						newConnectionFromIndex = selectedIndex;				
					}
					draggingConnection = false;
					buildMode = false;
					}
				}
			break;
			
			case Input.Buttons.LEFT: break;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {		
		//Getting old pos
		Vector2 previousMousePos = new Vector2(mousePos.x, mousePos.y);
		
		updateMousePos(screenX, screenY);
		
		Vector3 worldMousePos = cam.unproject(new Vector3(mouseX, mouseY, 0));
		Vector3 previousWorldMousePos = cam.unproject(new Vector3(previousMousePos.x, previousMousePos.y, 0));
		
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
	                buildMode = true;
	            }
			} else if (selectedIndex != -1 && draggingConnection == true ) {
				//Dragging a connection action //TODO: Maybe implement some info later
				//System.out.println("drag action --");
			} 			
		} else if (Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.input.isKeyPressed(Keys.C)) {
			if (draggingConnection == false) {
				for (int y = -1; y < 1; y++) {
					for (int x = -1; x < 1; x++) {
						Chunk c = GameScreen.chunks.getChunk((int)(worldMousePos.x/Base.CHUNK_SIZE) + x, (int)(worldMousePos.y/Base.CHUNK_SIZE) + y);
						if (c != null) {
							c.setCreeperLevel(GameScreen.chunks.getChunk((int)(worldMousePos.x/Base.CHUNK_SIZE) + x, (int)(worldMousePos.y/Base.CHUNK_SIZE) + y).getCreeperLevel() + 1);
						}
					}
				}		
				int sx = (int)(worldMousePos.x / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
				int sy = (int)(worldMousePos.y / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
				if (!(sx < 0 || sx > Base.SECTION_AMOUNT-1 || sy < 0 || sy > Base.SECTION_AMOUNT-1)) {						
					GameScreen.chunks.updateSectionMesh(GameScreen.chunks.sections[sx][sy], true, -1);
				}
				
			}
		} else if (Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.input.isKeyPressed(Keys.V)) {
			if (draggingConnection == false) {
				for (int y = -5; y < 5; y++) {
					for (int x = -5; x < 5; x++) {
						Chunk c = GameScreen.chunks.getChunk((int)(worldMousePos.x/Base.CHUNK_SIZE) + x, (int)(worldMousePos.y/Base.CHUNK_SIZE) + y);
						if (c != null) {
							c.setCreeperLevel(GameScreen.chunks.getChunk((int)(worldMousePos.x/Base.CHUNK_SIZE) + x, (int)(worldMousePos.y/Base.CHUNK_SIZE) + y).getCreeperLevel() - Base.MAX_CREEP * 0.02f);
						}
					}
				}		
					
				int sx = (int)(worldMousePos.x / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
				int sy = (int)(worldMousePos.y / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
				if (!(sx < 0 || sx > Base.SECTION_AMOUNT-1 || sy < 0 || sy > Base.SECTION_AMOUNT-1)) {						
					GameScreen.chunks.updateSectionMesh(GameScreen.chunks.sections[sx][sy], true, -1);
				}
				
			}
		} else if (Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.input.isKeyPressed(Keys.B)) {
			if (draggingConnection == false) {			
				Chunk c = GameScreen.chunks.getChunk((int)(worldMousePos.x/Base.CHUNK_SIZE), (int)(worldMousePos.y/Base.CHUNK_SIZE));
				if (c != null) {
					c.setHeight((int)GameScreen.chunks.getChunk((int)(worldMousePos.x/Base.CHUNK_SIZE), (int)(worldMousePos.y/Base.CHUNK_SIZE)).getHeight() + 1);
				}
				
				int sx = (int)(worldMousePos.x / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
				int sy = (int)(worldMousePos.y / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
				if (!(sx < 0 || sx > Base.SECTION_AMOUNT-1 || sy < 0 || sy > Base.SECTION_AMOUNT-1)) {						
					GameScreen.chunks.updateSectionMesh(GameScreen.chunks.sections[sx][sy], false, -1);
				}
				
			}	
		}
		else if (Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.input.isKeyPressed(Keys.N)) {
			if (draggingConnection == false) {
				for (int y = 0; y < 1; y++) {
					for (int x = 0; x < 1; x++) {
						Chunk c = GameScreen.chunks.getChunk((int)(worldMousePos.x/Base.CHUNK_SIZE) + x, (int)(worldMousePos.y/Base.CHUNK_SIZE) + y);
						if (c != null) {
							c.setHeight((int)GameScreen.chunks.getChunk((int)(worldMousePos.x/Base.CHUNK_SIZE) + x, (int)(worldMousePos.y/Base.CHUNK_SIZE) + y).getHeight() - 1);
						}
					}
				}		
				int sx = (int)(worldMousePos.x / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
				int sy = (int)(worldMousePos.y / (Base.SECTION_SIZE * Base.CHUNK_SIZE));
				if (!(sx < 0 || sx > Base.SECTION_AMOUNT-1 || sy < 0 || sy > Base.SECTION_AMOUNT-1)) {						
					GameScreen.chunks.updateSectionMesh(GameScreen.chunks.sections[sx][sy], false, -1);
				}
				
			}
		} else if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			if (draggingConnection == false) {
				float xPos = previousWorldMousePos.x - worldMousePos.x;
				float yPos = previousWorldMousePos.y - worldMousePos.y;
				cam.translate(xPos, yPos, 0);	
				GameScreen.chunks.updateView(cam);
			}
		} else if (Gdx.input.isButtonPressed(Buttons.MIDDLE)) {
			if (draggingConnection == false) {
				float xPos = previousWorldMousePos.x - worldMousePos.x;
				float yPos = previousWorldMousePos.y - worldMousePos.y;
				cam.translate(xPos, yPos, 0);					
				GameScreen.chunks.updateView(cam);
			}
		}
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		updateMousePos(screenX, screenY);
		return false;
	}

	private void updateMousePos(int screenX, int screenY) {
		mouseX = screenX;
		mouseY = screenY;	
		
		mousePos.x = mouseX;
		mousePos.y = mouseY;
		
		Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
		
		float nx = Math.round((worldPos.x - (worldPos.x % Base.CHUNK_SIZE)) / Base.CHUNK_SIZE);
		float ny = Math.round((worldPos.y - (worldPos.y % Base.CHUNK_SIZE)) / Base.CHUNK_SIZE);
		
			
		GameScreen.hoverChunk = GameScreen.chunks.getChunk((int)nx, (int)ny);
		GameScreen.hudMenu.update();
	}
	
	@Override
	public boolean scrolled(int amount) {
		//Cancel right click menu if one is present
		GameScreen.rightClickMenuManager.removeMenu();
		
		if (amount == 1) {
			cam.zoom += 0.2f;
			cam.zoom = Base.round(cam.zoom, 2);
			GameScreen.chunks.updateView(cam);
		} else {
			cam.zoom -= 0.2f;
			cam.zoom = Base.round(cam.zoom, 2);						
			GameScreen.chunks.updateView(cam);
		}
		return false;
	}
}
