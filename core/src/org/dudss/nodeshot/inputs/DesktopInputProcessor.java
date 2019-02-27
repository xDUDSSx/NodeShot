package org.dudss.nodeshot.inputs;

import static org.dudss.nodeshot.screens.GameScreen.buildMode;
import static org.dudss.nodeshot.screens.GameScreen.builtBuilding;
import static org.dudss.nodeshot.screens.GameScreen.cam;
import static org.dudss.nodeshot.screens.GameScreen.expandingANode;
import static org.dudss.nodeshot.screens.GameScreen.lastMousePress;
import static org.dudss.nodeshot.screens.GameScreen.lastMousePressType;
import static org.dudss.nodeshot.screens.GameScreen.mousePos;
import static org.dudss.nodeshot.screens.GameScreen.mouseX;
import static org.dudss.nodeshot.screens.GameScreen.mouseY;
import static org.dudss.nodeshot.screens.GameScreen.nodelist;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.buildings.AbstractIOPort;
import org.dudss.nodeshot.buildings.Connectable;
import org.dudss.nodeshot.buildings.NodeBuilding;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.entities.effects.Explosion;
import org.dudss.nodeshot.entities.effects.Shockwave;
import org.dudss.nodeshot.entities.effects.SmokePoof;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.nodes.IONode;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.GameScreen.MouseType;
import org.dudss.nodeshot.terrain.TerrainEditor;
import org.dudss.nodeshot.utils.Selector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**A LibGDX user I/O processor that handles user keyboard and mouse input.*/
public class DesktopInputProcessor implements InputProcessor {

	@Override
	public boolean keyDown(int keycode) {		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Keys.NUMPAD_0: TerrainEditor.terrainLayerSelected = 0; break;
			case Keys.NUMPAD_1: TerrainEditor.terrainLayerSelected = 1; break;
			case Keys.NUMPAD_2: TerrainEditor.terrainLayerSelected = 2; break;
			case Keys.NUMPAD_3: TerrainEditor.terrainLayerSelected = 3; break;
			case Keys.NUMPAD_4: TerrainEditor.terrainLayerSelected = 4; break;
			case Keys.NUMPAD_5: TerrainEditor.terrainLayerSelected = 5; break;
			case Keys.NUMPAD_6: TerrainEditor.terrainLayerSelected = 6; break;
			case Keys.NUMPAD_7: TerrainEditor.terrainLayerSelected = 7; break;
			case Keys.NUMPAD_8: TerrainEditor.terrainLayerSelected = 8; break;
			case Keys.NUMPAD_9: TerrainEditor.terrainLayerSelected = 9; break;
			case Keys.STAR: TerrainEditor.terrainLayerSelected = 10; break;
		}
		
		if (keycode == Keys.ESCAPE) {
			GameScreen.callPauseMenu();		
		} else
		if (keycode == Keys.R && GameScreen.buildMode == true && GameScreen.builtBuilding instanceof AbstractIOPort) {
			((AbstractIOPort)GameScreen.builtBuilding).rotateRight();
		} else 
		if (keycode == Keys.E && GameScreen.buildMode == true && GameScreen.builtBuilding instanceof AbstractIOPort) {
			((AbstractIOPort)GameScreen.builtBuilding).rotateLeft();
		} else 
		if (keycode == Keys.Q) {
			GameScreen.buildingManager.disableBuildMode();
		}
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
								
				//new Explosion(worldPos.x, worldPos.y);

				//Building
				if (GameScreen.buildMode == true && GameScreen.builtBuilding != null) {		
					if (GameScreen.resourceManager.bits >= GameScreen.builtBuilding.getBuildCost() && GameScreen.resourceManager.power >= GameScreen.builtBuilding.getEnergyCost()) {
						if (GameScreen.builtBuilding.canBeBuiltAt(worldPos.x, worldPos.y, true)) {							
							if (GameScreen.buildMoving) {
								GameScreen.builtBuilding.demolish(true);							
							}
							GameScreen.builtBuilding.setLocation(worldPos.x, worldPos.y, true);
							GameScreen.builtBuilding.build();
							
							if (GameScreen.expandingANode && builtBuilding instanceof NodeBuilding) {
								GameScreen.expandedConveyorNode.connectTo(((NodeBuilding)builtBuilding).getNode());	
								if (!((Conveyor) GameScreen.expandedConveyorNode.getConnectorConnecting(((NodeBuilding)builtBuilding).getNode())).isBuiltProperly()) {
									GameScreen.expandedConveyorNode.disconnect(((NodeBuilding)builtBuilding).getNode());	
									GameScreen.builtBuilding.demolish(true);
									return false;
								}
							}
									
							if (!GameScreen.buildMoving) {
								if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {					
									try {					
										if (builtBuilding instanceof NodeBuilding) {
											GameScreen.expandingANode = true;
											GameScreen.expandedConveyorNode = ((NodeBuilding) builtBuilding).getNode();
										}
										Class<? extends AbstractBuilding> buildingClass = GameScreen.builtBuilding.getClass();
										Constructor buildingConstructor;
										buildingConstructor = buildingClass.getConstructor(new Class[] {float.class, float.class});
										Object[] buildingArgs = new Object[] { new Float(0), new Float(0) };
										GameScreen.builtBuilding = (AbstractBuilding) buildingConstructor.newInstance(buildingArgs);		
									} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException| InvocationTargetException e) {
										BaseClass.errorManager.report(e, "An exception occurred while reinitialising a new building object");
									}													
								} else {
									GameScreen.buildingManager.disableBuildMode();
								}			
							} else {
								GameScreen.buildingManager.disableBuildMode();
							}
						} else		
						if (expandingANode) {					
							Vector2 nbCoords = GameScreen.builtBuilding.getCoordinates(worldPos.x, worldPos.y, true);
							if (GameScreen.chunks.getChunkAtWorldSpace(nbCoords.x,  nbCoords.y).getBuilding() instanceof Connectable) {
								if (((Connectable)GameScreen.chunks.getChunkAtWorldSpace(nbCoords.x, nbCoords.y).getBuilding()).getNode() != null) {
									if(!((Connectable)GameScreen.chunks.getChunkAtWorldSpace(nbCoords.x, nbCoords.y).getBuilding()).getNode().getAllConnectedNodes().contains(GameScreen.expandedConveyorNode)) {
										if(((Connectable)GameScreen.chunks.getChunkAtWorldSpace(nbCoords.x, nbCoords.y).getBuilding()).getNode() != GameScreen.expandedConveyorNode) {			
											GameScreen.expandedConveyorNode.connectTo(((Connectable)GameScreen.chunks.getChunkAtWorldSpace(nbCoords.x, nbCoords.y).getBuilding()).getNode());
											if (!(((Conveyor) GameScreen.expandedConveyorNode.getConnectorConnecting(((Connectable)GameScreen.chunks.getChunkAtWorldSpace(nbCoords.x, nbCoords.y).getBuilding()).getNode())).isBuiltProperly())) {
												GameScreen.expandedConveyorNode.disconnect(((Connectable)GameScreen.chunks.getChunkAtWorldSpace(nbCoords.x, nbCoords.y).getBuilding()).getNode());	
												return false;
											}
											if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {				
												GameScreen.expandedConveyorNode = (ConveyorNode) ((Connectable)GameScreen.chunks.getChunkAtWorldSpace(worldPos.x, worldPos.y).getBuilding()).getNode();
											} else {
												GameScreen.buildingManager.disableBuildMode();
											}
										}
									}			
								}
							}
						}
						GameScreen.chunks.updateAllSectionMeshes(false);
					} else {
						BaseClass.logger.warning("Cannot build building: " + GameScreen.builtBuilding.getType().toString() + "! Not enough resources!");
					}
				} else {
					GameScreen.checkHighlights(true);	
				}
				
				//Cancel right click menu if one is present
				GameScreen.rightClickMenuManager.removeMenu();			
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

				if (buildMode) {
					GameScreen.buildingManager.disableBuildMode();
				}
				
				if (clickedEntity instanceof NodeBuilding || clickedEntity instanceof ConveyorNode || clickedEntity instanceof IONode) {
					if (clickedEntity instanceof IONode) {
						GameScreen.expandedConveyorNode = (IONode) clickedEntity;
					} else if (clickedEntity instanceof ConveyorNode) {
						GameScreen.expandedConveyorNode =(ConveyorNode) clickedEntity;
					} else {
						GameScreen.expandedConveyorNode = ((NodeBuilding) clickedEntity).getNode();
					}
					
					GameScreen.expandingANode = true;
					GameScreen.buildingManager.startBuildMode(new NodeBuilding(0, 0));
				}
				
				/*
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
				*/			
			break;
				case Input.Buttons.MIDDLE: mouseX = Gdx.input.getX();
				mouseY = Gdx.input.getY();		
				mousePos.x = mouseX;
				mousePos.y = mouseY;		
	
				worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
				lastMousePress = worldPos;
				lastMousePressType = MouseType.MOUSE_2;
				
				if (GameScreen.buildMode == true && GameScreen.builtBuilding instanceof AbstractIOPort) {
					((AbstractIOPort)GameScreen.builtBuilding).rotateRight();
				} 
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//if (!GameScreen.gamePaused) {
			switch (button) {
				case Input.Buttons.RIGHT:
					mouseX = Gdx.input.getX();
					mouseY = Gdx.input.getY();
					
					mousePos.x = mouseX;
					mousePos.y = mouseY;
					
					Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
					
					lastMousePress = worldPos;
				break;				
				case Input.Buttons.LEFT: break;
			}
		//}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		//if (!gamePaused) {
			Vector2 previousMousePos = new Vector2(mousePos.x, mousePos.y);
			
			updateMousePos(screenX, screenY);
			
			Vector3 worldMousePos = cam.unproject(new Vector3(mouseX, mouseY, 0));
			Vector3 previousWorldMousePos = cam.unproject(new Vector3(previousMousePos.x, previousMousePos.y, 0));
			
			if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
				//Cancel right click menu if one is present
				GameScreen.rightClickMenuManager.removeMenu();
			} else if (Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.input.isKeyPressed(Keys.C)) {
				GameScreen.terrainEditor.modifyCreeper(worldMousePos, true);
			} else if (Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.input.isKeyPressed(Keys.V)) {
				GameScreen.terrainEditor.modifyCreeper(worldMousePos, false);	
			} else if (Gdx.input.isButtonPressed(Buttons.LEFT) && Gdx.input.isKeyPressed(Keys.T)) {	
				GameScreen.terrainEditor.setTerrain(worldMousePos);
			} else if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
					float xPos = previousWorldMousePos.x - worldMousePos.x;
					float yPos = previousWorldMousePos.y - worldMousePos.y;
					cam.translate(xPos, yPos, 0);	
					GameScreen.chunks.updateView(cam);
			} else if (Gdx.input.isButtonPressed(Buttons.MIDDLE)) {
					float xPos = previousWorldMousePos.x - worldMousePos.x;
					float yPos = previousWorldMousePos.y - worldMousePos.y;
					cam.translate(xPos, yPos, 0);					
					GameScreen.chunks.updateView(cam);
			}
		//}
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
			
		GameScreen.hoverChunk = GameScreen.chunks.getChunkAtTileSpace((int)nx, (int)ny);
	}
	
	@Override
	public boolean scrolled(int amount) {
			//Cancel right click menu if one is present
			GameScreen.rightClickMenuManager.removeMenu();
			
			if (amount == 1) {
				if (Gdx.input.isKeyPressed(Keys.T)) {
					TerrainEditor.terrainBrushSize -= 2;
					if (TerrainEditor.terrainBrushSize < 1) {
						TerrainEditor.terrainBrushSize = 1;
					}
				} else {				
					GameScreen.zoomTo(cam.zoom + 1f, 0.5f);
				}
			} else {
				if (Gdx.input.isKeyPressed(Keys.T)) {
					TerrainEditor.terrainBrushSize += 2;
				} else {
					if (cam.zoom > Base.MIN_ZOOM) {
						GameScreen.zoomTo(cam.zoom - 1f, 0.5f);
					}
				}
			}
		return true;
	}
}
