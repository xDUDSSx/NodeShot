package org.dudss.nodeshot.entities.nodes;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.Selector;
import org.dudss.nodeshot.utils.SpriteLoader;
import org.dudss.nodeshot.algorithms.PathfindingDistanceAlgorithm;
import org.dudss.nodeshot.algorithms.PathfindingStepAlgorithm;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.connectors.Conveyor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Node object that holds position, transform, state and connection data, extends a
 * Gdx sprite
 * 
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

	public int maxConnections;
	
	public Boolean closed = false;

	public List<Node> connected_to;
	public List<Node> connected_by;

	public List<Connector> connectors;

	public String state = "";

	public Point movementVector;

	/**
	 * Creates a new Node that functions as a Sprite
	 * 
	 * @param cx - the X center coordinate 
	 * @param cy - the Y center coordinate
	 * @param radius - the sprite radius (16 hardcoded TODO: fix)
	 */
	public Node(float cx, float cy, int radius) {
		this.cx = cx;
		this.cy = cy;
		
		// Hardcoded TODO: fix radius
		this.radius = 16;
		
		this.maxConnections = Base.MAX_CONNECTIONS;
		if (maxConnections == 1) {
			this.setConnectable(false);
		}
		
		this.id = java.lang.System.identityHashCode(this);

		x = cx - (radius / 2);
		y = cy - (radius / 2);

		connected_to = new CopyOnWriteArrayList<Node>();
		connected_by = new CopyOnWriteArrayList<Node>();

		connectors = new CopyOnWriteArrayList<Connector>();

		this.set(new Sprite(SpriteLoader.nodeSprite));
		this.setPosition(x, y);
	}
	
	public void setLocation(float cx, float cy) {
		this.cx = cx;
		this.cy = cy;
		x = cx - (radius / 2);
		y = cy - (radius / 2);
		this.setPosition(x, y);
	}
	
	
	public void setLocation(float cx, float cy, Boolean snap) {
		if (snap) {
			float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
			float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
			
			x = nx - (radius/2) + Base.CHUNK_SIZE/2;
			y = ny - (radius/2) + Base.CHUNK_SIZE/2;
			
			this.cx = nx;
			this.cy = ny;
			
			//Placing the node in the grid center
			this.setPosition(x , y );
		} else {
			this.cx = cx;
			this.cy = cy;
			x = cx - (radius / 2);
			y = cy - (radius / 2);
			
			this.setPosition(x, y);
		}	
	}

	// Draw replaced by Sprite.draw();
	public void setSprite(Sprite s) {
		this.set(s);
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public float getCX() {
		return x + radius / 2;
	}

	public float getCY() {
		return y + radius / 2;
	}

	public int getRadius() {
		return radius;
	}

	public void setConnectable(Boolean b) {
		this.connectable = b;
	}

	public Boolean getConnectable() {
		return this.connectable;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getState() {
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
		if ((connected_to.size() + connected_by.size()) >= this.maxConnections) {
			// too many connections
		} else if ((targetnode.connected_to.size() + targetnode.connected_by.size()) >= targetnode.maxConnections) {
			// too many connections
		} else {
			if (!connected_by.contains(targetnode) && !connected_to.contains(targetnode)) {
				connected_to.add(targetnode);
				targetnode.connected_by.add(this);
				connections++;
				targetnode.connections++;
				if (targetnode.connections >= targetnode.maxConnections) {
					targetnode.setConnectable(false);
				}

				Connector nC = null;
				if (this instanceof ConveyorNode || targetnode instanceof ConveyorNode) {
					nC = new Conveyor(this, targetnode);
				} else {
					nC = new Connector(this, targetnode);
				}
				GameScreen.connectorHandler.addConnector(nC);
				this.connectors.add(nC);
				targetnode.connectors.add(nC);

			} else {
				System.out.println("Nodes - " + this.getX() + ":" + this.getY() + " and " + targetnode.getX() + ":"
						+ targetnode.getY() + " are already connected");
			}
		}
	}
	
	public void connectTo(Node targetnode, EntityType eT) {
		if ((connected_to.size() + connected_by.size()) >= this.maxConnections) {
			// too many connections
		} else if ((targetnode.connected_to.size() + targetnode.connected_by.size()) >= targetnode.maxConnections) {
			// too many connections
		} else {
			if (!connected_by.contains(targetnode) && !connected_to.contains(targetnode)) {
				connected_to.add(targetnode);
				targetnode.connected_by.add(this);
				connections++;
				targetnode.connections++;
				if (targetnode.connections >= targetnode.maxConnections) {
					targetnode.setConnectable(false);
				}

				Connector nC = null;
				if (eT == EntityType.CONVEYOR) {
					nC = new Conveyor(this, targetnode);
				} else {
					nC = new Connector(this, targetnode);
				}
				GameScreen.connectorHandler.addConnector(nC);
				this.connectors.add(nC);
				targetnode.connectors.add(nC);

			} else {
				System.out.println("Nodes - " + this.getX() + ":" + this.getY() + " and " + targetnode.getX() + ":"
						+ targetnode.getY() + " are already connected");
			}
		}
	}
	
	public int getNumberOfConnections() {
		return connections;
	}

	public void disconnect(Node node) {
		this.connected_to.remove(node);
		this.connections -= 1;
		if (this.connections < this.maxConnections) {
			this.setConnectable(true);
		}
		node.connected_by.remove(this);
		node.connections -= 1;
		if (node.connections < node.maxConnections) {
			node.setConnectable(true);
		}

		Connector toBeRemoved = null;
		for (Connector nC : this.connectors) {
			if (nC.getTo() == node) {
				toBeRemoved = nC;
			}
			if (toBeRemoved != null) {
				break;
			}
		}
		
		this.connectors.remove(toBeRemoved);
		node.connectors.remove(toBeRemoved);
		
		if (toBeRemoved != null) {
			GameScreen.connectorHandler.removeConnector(toBeRemoved);
		}
	}

	public void remove() {
		for (Node n : connected_to) {
			n.connected_by.remove(this);
			n.connections -= 1;
			if (n.connections < n.maxConnections) {
				n.setConnectable(true);
			}
		}
		for (Node n : connected_by) {
			n.connected_to.remove(this);
			n.connections -= 1;
			if (n.connections < n.maxConnections) {
				n.setConnectable(true);
			}
		}

		// Removing connectors to this node
		for (Connector nC : GameScreen.connectorHandler.getAllConnectorsToNode(this)) {
			GameScreen.connectorHandler.removeConnector(nC);
		}
		// Highlight fix
		if (GameScreen.selectedID == this.getID()) {
			Selector.deselect();
		}

		if (GameScreen.nodelist.size() != 0) {
			GameScreen.nodelist.remove(this);
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

	public List<Connector> getConnectors() {
		return connectors;
	}
	
	public int getStepsTo(Node target) {
		PathfindingStepAlgorithm pSA = new PathfindingStepAlgorithm(this, target);
		return pSA.getSteps();
	}
	
	public double getShortestDistanceTo(Node target) {
		PathfindingDistanceAlgorithm pDA = new PathfindingDistanceAlgorithm(this, target);
		if (!(pDA.failed())) {
			return pDA.getShortestDistance();
		} else {
			return -1;
		}
	}

	public void sendPackage(Node target) {
		GameScreen.packageHandler.addPath(this, target);
	}

	public void sendPackage(Node target, Color c) {
		GameScreen.packageHandler.addPath(this, target, c); 
	}

	public void sendPackage(Node target, Package p) {
		GameScreen.packageHandler.addPath(this, target, p);
	}
	
	public void sendPackage(Package p) {
		Connector c = connectors.get(Base.getRandomIntNumberInRange(0, connectors.size() - 1));
		GameScreen.packageHandler.addIndefinitePath(p, c); //TODO: indefinite package + conveyor connector
	}
	
	
	public int getID() {
		return this.id;
	}

	@Override
	public int getIndex() {
		return GameScreen.nodelist.indexOf(this);
	}

	public Boolean isClosed() {
		return closed;
	}

	public void setClosed(Boolean closed) {
		this.closed = closed;
		if (closed == true) {
			this.set(new Sprite(SpriteLoader.nodeClosedSprite));
			this.setPosition(x, y);
		} else {
			this.set(new Sprite(SpriteLoader.nodeSprite));
			this.setPosition(x, y);
		}
	}

	public void setMaxConnections(int max) {
		this.maxConnections = max;
	} 
	
	@Override
	public EntityType getType() {
		return EntityType.NODE;
	}
}
