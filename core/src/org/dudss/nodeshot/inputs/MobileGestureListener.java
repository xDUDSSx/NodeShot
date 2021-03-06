/*package org.dudss.nodeshot.inputs;

import static org.dudss.nodeshot.screens.GameScreen.HEIGHT;
import static org.dudss.nodeshot.screens.GameScreen.backButton;
import static org.dudss.nodeshot.screens.GameScreen.buildButton;
import static org.dudss.nodeshot.screens.GameScreen.oldNodeBuildMode;
import static org.dudss.nodeshot.screens.GameScreen.cam;
import static org.dudss.nodeshot.screens.GameScreen.debugMessage;
import static org.dudss.nodeshot.screens.GameScreen.deleteButton;
import static org.dudss.nodeshot.screens.GameScreen.draggingConnection;
import static org.dudss.nodeshot.screens.GameScreen.lastCamPos;
import static org.dudss.nodeshot.screens.GameScreen.lastMousePress;
import static org.dudss.nodeshot.screens.GameScreen.lastMousePressType;
import static org.dudss.nodeshot.screens.GameScreen.mousePos;
import static org.dudss.nodeshot.screens.GameScreen.mouseX;
import static org.dudss.nodeshot.screens.GameScreen.mouseY;
import static org.dudss.nodeshot.screens.GameScreen.newConnectionFromIndex;
import static org.dudss.nodeshot.screens.GameScreen.nodelist;
import static org.dudss.nodeshot.screens.GameScreen.nodeshotGame;
import static org.dudss.nodeshot.screens.GameScreen.selectedID;
import static org.dudss.nodeshot.screens.GameScreen.selectedIndex;
import static org.dudss.nodeshot.screens.GameScreen.selectedType;
import static org.dudss.nodeshot.screens.GameScreen.zooming;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.GameScreen.MouseType;
import org.dudss.nodeshot.utils.Selector;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MobileGestureListener implements GestureDetector.GestureListener{
	
	/**This is an old class that is not updated to new principles, the game will probably not support mobile so
	 * this class will be most likely deleted in the future
	 
	
	@Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        mouseX = (int) x;
        mouseY = (int) y;

        mousePos.x = mouseX;
        mousePos.y = mouseY;

        Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));
        lastMousePress = worldPos;

        System.out.println("WorldPos.x: " + worldPos.x + " WorldPos.y: " + worldPos.y);

        Rectangle rect = new Rectangle(worldPos.x-4, worldPos.y-4, 8, 8);

        Boolean nodeIntersected = false;
        for(int i = 0; i < nodelist.size(); i++) {
            //If cursor hits a node -> highlight it
            if(nodelist.get(i).getBoundingRectangle().overlaps(rect)) {
                Selector.selectNode(nodelist.get(i));              
                nodeIntersected = true;
                break;

            }
        }

        if (!nodeIntersected) {
        	Selector.deselect();
        }

        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        lastMousePressType = MouseType.MOUSE_1;
        mouseX = (int) x;
        mouseY = (int) y;

        System.out.println("mX: " + mouseX + " mY: " + mouseY);
        System.out.println("sX: " + x + " sY: " + y);

        mousePos.x = mouseX;
        mousePos.y = mouseY;

        Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));

        lastMousePress = worldPos;

        Boolean buttonIntersected = false;
        //Back button detection
        if (backButton.contains(mouseX, (HEIGHT - mouseY))) {
            buttonIntersected = true;
            lastCamPos = cam.position;
            nodeshotGame.setScreen(BaseClass.menuScreen);
        }

        //Delete button detection
        if (deleteButton.contains(mouseX, (HEIGHT - mouseY))) {
            buttonIntersected = true;

            if (selectedType == EntityType.NODE && selectedID != -1) {
                for (Node n : nodelist) {
                    if (n.getID() == selectedID) {
                        n.remove();
                        break;
                    }
                }
            } else
            if(nodelist.size() != 0) {
                nodelist.get(nodelist.size() - 1).remove();
            }
        }

        //Build button detection
        if (buildButton.contains(mouseX, (HEIGHT - mouseY))) {
            buttonIntersected = true;
            oldNodeBuildMode = !oldNodeBuildMode;
        }

        //Highlighting
        if (!buttonIntersected) GameScreen.checkHighlights(true);

        if (selectedIndex == -1 && buttonIntersected == false && oldNodeBuildMode == true) {
            //&& Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
            Node newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
            nodelist.add(newnode);
            oldNodeBuildMode = false;
        }

        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        debugMessage = "Flinging";
        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        debugMessage = "Panning dX" + deltaX + " dY: " + deltaY;
        mouseX = (int) x;
        mouseY = (int) y;

        //Getting old pos
        Vector2 previousMousePos = new Vector2(mousePos.x, mousePos.y);

        //Setting current (new) pos
        mousePos.x = mouseX;
        mousePos.y = mouseY;

        Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));

        lastMousePress = worldPos;

        Vector3 previousWorldPos = cam.unproject(new Vector3(previousMousePos.x, previousMousePos.y, 0));

        if (selectedIndex != -1 && draggingConnection == false) {
          	Node highlightedNode = nodelist.get(selectedIndex);
	        double distance = Math.hypot( highlightedNode.getCX() - lastMousePress.x,  highlightedNode.getCY() - lastMousePress.y);
            System.out.println("DISTANCE IS: " + distance);

            System.out.println("worldPos.x: " + worldPos.x + " y: " + worldPos.y + "  -  mousePos.x" + lastMousePress.x + " y: " + lastMousePress.y);
            //Basically, if the cursor is still in the node area when first drag is called, initiate a new dragging connection
            //A way to prevent bugs, a more simple way could be used, but this should not cause issues
            if (distance <= highlightedNode.radius) {
                newConnectionFromIndex = highlightedNode.getIndex();
                draggingConnection = true;
            }
        } else
        if (selectedIndex != -1 && draggingConnection == true ) {
            //Dragging a connection action //TODO: Maybe implement some info later
            System.out.println("drag action --");
        } else {
            float xPos = previousWorldPos.x - worldPos.x;
            float yPos = previousWorldPos.y - worldPos.y;
            System.out.println("xPos: " + xPos + " yPos: " + yPos);
            if (!zooming) {
                cam.translate(xPos, yPos, 0);
            }
        }

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        mouseX = (int) x;
        mouseY = (int) y;

        debugMessage = "- TOUCH UP AT " + x + " " + y;

        System.out.println("mX: " + mouseX + " mY: " + mouseY);
        System.out.println("sX: " + x + " sY: " + y);

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
                    Node newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
                    nodelist.add(newnode);
                    nodelist.get(selectedIndex).connectTo(newnode);
                    Selector.selectNode(newnode);
                    newConnectionFromIndex = selectedIndex;
                }
                draggingConnection = false;
            }
        }
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        zooming = true;
        debugMessage = "Zooming iD: " + initialDistance + " dist: " + distance;

        float difference = distance - initialDistance;

        if (difference >= 0) {
            cam.zoom -= 0.03;
        } else {
            cam.zoom += 0.03;
        }

        zooming = false;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2)
    {       
        return true;
    }

    @Override
    public void pinchStop() {
        debugMessage = "pinchStop";
    }
}
*/