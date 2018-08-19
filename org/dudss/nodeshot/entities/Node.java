package org.dudss.nodeshot.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.algorithms.NodePathfindingAlgorithm;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Node object that holds position, transform, state and connection data
 * Extends Gdx sprite
 * @author Dan
 *
 */

public class Node extends Sprite implements Entity {
	
	public float x;
	public float y;
	
	public float cx;
	public float cy;
	
	public int id;
	
	public int radius;
	public int connections;
	public Boolean connectable = true;
	
	public Boolean closed = false;
	
	public List<Node> connected_to;
	public List<Node> connected_by;
	
	public List<NodeConnector> connectors;
	
	public String state = "";
	
	public Point movementVector;
	
	/**
	* Creates a node that passes arguments to a new Ellipse2D.Double
	* @param x  <x coordinate>
	* @param y  <y coordinate>
	* @param radius <Node radius>
	*/
	public Node(float cx, float cy, int radius) {
		this.cx = cx;
		this.cy = cy;
		//Hardcoded TODO: fix radius
		this.radius = 16;
		this.id = java.lang.System.identityHashCode(this);
		
		x = cx - (radius/2);
		y = cy - (radius/2);
		
		connected_to = new CopyOnWriteArrayList<Node>();
		connected_by = new CopyOnWriteArrayList<Node>();
		
		connectors = new CopyOnWriteArrayList<NodeConnector>();
		
		this.set(new Sprite(BaseClass.spriteSheet, 16, 0, 16, 16));
		this.setPosition(x, y);
	}
	
	public void recalculateCoords(int cx, int cy) {
		this.cx = cx;
		this.cy = cy;
		x = cx - (radius/2);
		y = cy - (radius/2);
	}
	
	//Draw replaced by Sprite.draw();
	public void setSprite(Sprite s) {
		this.set(s);
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}

	public float getCX(){
		return x + radius/2;
	}
	public float getCY(){
		return y + radius/2;
	}
	public int getRadius(){
		return radius;
	}
	public void setConnectable(Boolean b) {
		this.connectable = b;
	}
	public Boolean getConnectable() {
		return this.connectable;
	}
	public void setState(String state){
		this.state = state;
	}
	public String getState(){
		return state;
	}

	public double getDistance(Node node) {
		double nodex = (double) node.getCX();
		double nodey = (double) node.getCY();
		
		double thisx = (double) this.cx;
		double thisy = (double) this.cy;
		
		double X = nodex - thisx;
		double Y = nodey - thisy;
		
		double distance = Math.sqrt((Math.pow(X, 2)) + (Math.pow(Y, 2)));
		return distance;
	}
	public void connectTo(Node targetnode) {
		if((connected_to.size() + connected_by.size()) >= Base.MAX_CONNECTIONS) {
			//too many connections
		} else 
		if ((targetnode.connected_to.size() + targetnode.connected_by.size()) >= Base.MAX_CONNECTIONS) {
			//too many connections
		} else {
			if(!connected_by.contains(targetnode) && !connected_to.contains(targetnode)) {
				connected_to.add(targetnode);
				targetnode.connected_by.add(this);
				connections++;
				targetnode.connections++;
				if(targetnode.connections >= Base.MAX_CONNECTIONS) {
					targetnode.setConnectable(false);
				}
				
				NodeConnector nC = new NodeConnector(this, targetnode);
				BaseClass.nodeConnectorHandler.addConnector(nC);	
				this.connectors.add(nC);
				targetnode.connectors.add(nC);
				
			} else {
				System.out.println("Nodes - " + this.getX() + ":" + this.getY() + " and " + targetnode.getX() + ":" + targetnode.getY() + " are already connected");		
			}
		}
	}
	
	public int getNumberOfConnections() {
		return connections;
	}
	
	public void moveRandom() {
		Point vector = new Point((int) Base.getRandomFloatNumberInRange(0, 40) - 20, (int) Base.getRandomFloatNumberInRange(0, 40) - 20);
		//Point vector = new Point(0, 0);
		Point currentPoint = new Point ((int)this.getX(),(int) this.getY());
		
		Point newPoint = new Point(currentPoint.x + vector.x, currentPoint.y + vector.y);
		
		if(newPoint.y <= 0) {
			newPoint.setLocation(currentPoint.x + vector.x, currentPoint.y - vector.y);
		}
		if(newPoint.x <= 0) {
			newPoint.setLocation(currentPoint.x - vector.x, currentPoint.y + vector.y);
		}
		if(newPoint.y >= getHeight()) {
			newPoint.setLocation(currentPoint.x + vector.x, currentPoint.y - vector.y);
		}
		if(newPoint.x >= getWidth()) {
			newPoint.setLocation(currentPoint.x - vector.x, currentPoint.y + vector.y);
		}
		
		//Connecting / Disconnecting
        for(Node n : BaseClass.nodelist) {
			if(this.getDistance(n) <= Base.CONNECT_DISTANCE) {
				if (!connected_by.contains(n) && !connected_to.contains(n)) {
					this.connectTo(n);
				}
			} else {
				disconnect(n);
			}
		}	
        
        //Positioning the Node
        recalculateCoords((int) newPoint.getX(), (int) newPoint.getY());
	}
	
	public void move() {
		
		Point vector = null;
		
		int rangeXfrom = -5;
		int rangeXto = 5;
		int rangeYfrom = -5;
		int rangeYto = 5;
		
		if(this.movementVector == null) {
			movementVector = new Point(Base.getRandomIntNumberInRange(rangeXfrom, rangeXto), Base.getRandomIntNumberInRange(rangeYfrom, rangeYto));	
		}
		
		vector = movementVector;
		
		Point currentPoint = new Point ((int)this.getCX(),(int)this.getCY());
		
		if(currentPoint.y <= 0) {
			vector.setLocation(vector.x, -vector.y);
		}
		if(currentPoint.x <= 0) {
			vector.setLocation(-vector.x, vector.y);
		}
		if(currentPoint.y >= BaseClass.WORLD_SIZE - radius) {
			vector.setLocation(vector.x, -vector.y);
		}
		if(currentPoint.x >= BaseClass.WORLD_SIZE - radius) {
			vector.setLocation(-vector.x, vector.y);
		}
		
		Point newPoint = new Point(currentPoint.x + vector.x, currentPoint.y + vector.y);
		movementVector = vector;

        //Connecting / Disconnecting
        for(Node n : BaseClass.nodelist) {
			if(this.getDistance(n) <= Base.CONNECT_DISTANCE) {
				if (!connected_by.contains(n) && !connected_to.contains(n)) {
					if(n != this) {
						this.connectTo(n);
					}
				}
			} else {
				if (this.getConnectedToNodes().contains(n)) {
					disconnect(n);
				}
			}
		}	
        
        //Positioning the Node
        recalculateCoords(newPoint.x, newPoint.y);
        this.setPosition(x, y);
   	}
	
	public void disconnect(Node node) {
		this.connected_to.remove(node);
		this.connections -= 1;
		if (this.connections < Base.MAX_CONNECTIONS) {
			this.setConnectable(true);
		}
		node.connected_by.remove(this);
		node.connections -= 1;
		if (node.connections < Base.MAX_CONNECTIONS) {
			node.setConnectable(true);
		}
		
		NodeConnector toBeRemoved = null;
		for (NodeConnector nC : this.connectors) {
			if (nC.to == node) {
				toBeRemoved = nC;
			}
			if (toBeRemoved != null) {
				break;
			}
		}
		if(toBeRemoved != null) {
			BaseClass.nodeConnectorHandler.removeConnector(toBeRemoved);
		}
	}
	
	public void remove() {
		for(Node n : connected_to) {
			n.connected_by.remove(this);
			n.connections -= 1;
			if (n.connections < Base.MAX_CONNECTIONS) {
				n.setConnectable(true);
			}
		}
		for(Node n : connected_by) {
			n.connected_to.remove(this);
			n.connections -= 1;
			if (n.connections < Base.MAX_CONNECTIONS) {
				n.setConnectable(true);
			}
		}
		
		//Removing connectors to this node
		for (NodeConnector nC : BaseClass.nodeConnectorHandler.getAllConnectorsToNode(this)) {
			BaseClass.nodeConnectorHandler.removeConnector(nC);
		}
		//Highlight fix
		if (BaseClass.indexOfHighlightedNode == this.getIndex()) {
			BaseClass.indexOfHighlightedNode = -1;
		}
		
		if(BaseClass.nodelist.size() != 0) { 
    		BaseClass.nodelist.remove(this);
    	}
		
	}
	public List<Node> getConnectedToNodes() {
		return this.connected_to;
	}
	
	public List<Node> getConnectedByNodes() {
		return this.connected_by;
	}
	
	public ArrayList<Node> getAllConnectedNodes() {
		ArrayList<Node> mergedArray = new ArrayList<Node>();
		mergedArray.addAll(this.connected_by);
		mergedArray.addAll(this.connected_to);
		return mergedArray;
	}
	
	public int getStepsTo(Node target) {
		NodePathfindingAlgorithm nPA = new NodePathfindingAlgorithm(this, target);
		return nPA.getSteps();
	}
	
	public void sendPackage(Node target) {
		BaseClass.packageHandler.addPath(this, target); //TODO: Make PackageHandler handle the finish action
	}	
	
	public void sendPackage(Node target, Color c) {
		BaseClass.packageHandler.addPath(this, target, c); //TODO: Make PackageHandler handle the finish action
	}	
	
	public int getID() {
		return this.id;
	}
	
	public int getIndex() {
		return BaseClass.nodelist.indexOf(this);
	}
	
	public Boolean isClosed() {
		return closed;
	}
	public void setClosed(Boolean closed) {
		this.closed = closed;
		if (closed == true) {
			this.set(new Sprite(BaseClass.spriteSheet, 32, 0, 16, 16));
			this.setPosition(x, y);
		} else {
			this.set(new Sprite(BaseClass.spriteSheet, 16, 0, 16, 16));
			this.setPosition(x, y);
		}
	}
}

