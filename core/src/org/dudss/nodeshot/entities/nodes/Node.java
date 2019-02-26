package org.dudss.nodeshot.entities.nodes;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.Selector;
import org.dudss.nodeshot.utils.SpriteLoader;
import org.dudss.nodeshot.algorithms.PathfindingDistanceAlgorithm;
import org.dudss.nodeshot.algorithms.PathfindingStepAlgorithm;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.terrain.Chunk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Node object that holds position, transform, state and connection data. Individual nodes are connected via {@link Connector}s.
 */
public class Node extends Sprite implements Entity {

	public float x;
	public float y;

	public float cx;
	public float cy;

	public int id;

	public int radius;
	public Boolean connectable = true;

	public int maxConnections;
	
	public Boolean closed = false;

	public List<Connector> connectors;

	public String state = "";

	public Point movementVector;

	/**
	 * Creates a new Node that functions as a Sprite
	 * 
	 * @param cx - the X center coordinate 
	 * @param cy - the Y center coordinate
	 * @param radius - the sprite radius
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

		connectors = new CopyOnWriteArrayList<Connector>();

		this.set(new Sprite(SpriteLoader.nodeSprite));
		this.setPosition(x, y);
	}
	
	/**Positions the node precisely in world-space*/
	public void setLocation(float cx, float cy) {
		setLocation(cx, cy, false);
	}
	
	/**Positions the node in world-space and snaps it to the nearest {@link Chunk}.*/
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

	/**Sets the {@link Sprite} representing this node.*/
	public void setSprite(Sprite s) {
		this.set(s);
	}

	@Deprecated
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**Return the X coordinate of its centre.*/
	public float getCX() {
		return x + radius / 2;
	}
	
	/**Return the Y coordinate of its centre.*/
	public float getCY() {
		return y + radius / 2;
	}

	@Deprecated
	public int getRadius() {
		return radius;
	}

	/**Whether other {@link Node}s can connect to this one.*/
	public void setConnectable(Boolean b) {
		this.connectable = b;
	}
	
	public Boolean isConnectable() {
		return this.connectable;
	}

	@Deprecated
	public void setState(String state) {
		this.state = state;
	}

	@Deprecated
	public String getState() {
		return state;
	}

	/**Returns distance between the nodes in world units.*/
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

	/**Connects this node to the specified target node with an appropriate {@link Connector}.*/
	public void connectTo(Node targetnode) {
		if (targetnode != null) {
			if (!(connectors.size() >= this.maxConnections) && !(targetnode.getConnectors().size() >= targetnode.maxConnections)) {				
				if (!targetnode.isConnectedTo(this)) {							
					if (targetnode.getConnectors().size() >= targetnode.maxConnections) {
						targetnode.setConnectable(false);
					}
	
					Connector nC = null;
					if (this instanceof ConveyorNode || targetnode instanceof ConveyorNode || this instanceof IONode || targetnode instanceof IONode) {
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
	}
	
	/*@Deprecated
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
	}*/
	
	public int getNumberOfConnections() {
		return connectors.size();
	}

	/**Disconnects this node from any connections affiliated with the other {@link Node}.*/
	public void disconnect(Node node) {
		//Shared connector
		Connector c = this.getConnectorConnecting(node);
		
		if (c != null) {
			this.connectors.remove(c);
			node.getConnectors().remove(c);
			
			if (this.getNumberOfConnections() < this.maxConnections) {
				this.setConnectable(true);
			}
			if (node.getNumberOfConnections() < node.maxConnections) {
				node.setConnectable(true);
			}
			GameScreen.connectorHandler.removeConnector(c);
		}
	}

	/**Adds this node the the game list of nodes. The node will get rendered and updated by the {@link SimulationThread}.*/
	public void add() {
		GameScreen.nodelist.add(this);
	}
	
	/**Removes this node from the game list of nodes and disconnects it from any other connected {@linkplain Node}.*/
	public void remove() {
		//Disconnects from all connected nodes
		for (Node n : this.getAllConnectedNodes()) {
			this.disconnect(n);
		}
		
		// Highlight fix
		if (GameScreen.selectedID == this.getID()) {
			Selector.deselect();
		}

		if (GameScreen.nodelist.size() != 0) {
			GameScreen.nodelist.remove(this);
		}
	}

	/**Returns a list of all {@link Node}s that share a {@link Connector} with this node.*/
	public ArrayList<Node> getAllConnectedNodes() {
		ArrayList<Node> mergedArray = new ArrayList<Node>();
		for (Connector c : this.connectors) {
			if (c.getTo() != this) {
				mergedArray.add(c.getTo());			
			} else {
				mergedArray.add(c.getFrom());
			};
		}
		return mergedArray;
	}

	public List<Connector> getConnectors() {
		return connectors;
	}
	
	/**Returns true if this and targetnode share a common {@link Connector}*/
	public boolean isConnectedTo(Node targetnode) {
		for (Connector tc : targetnode.getConnectors()) {
			for (Connector c : connectors) {
				if (tc == c) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**Returns the {@link Connector} shared between these {@link Node}s.
	 * null is returned where no common Connector was found.*/
	public Connector getConnectorConnecting(Node targetnode) {
		for (Connector tc : targetnode.getConnectors()) {
			for (Connector c : connectors) {
				if (tc == c) {
					return c;
				}
			}
		}
		return null;
	}
	
	@Deprecated
	public int getStepsTo(Node target) {
		PathfindingStepAlgorithm pSA = new PathfindingStepAlgorithm(this, target);
		return pSA.getSteps();
	}
	
	@Deprecated
	public double getShortestDistanceTo(Node target) {
		PathfindingDistanceAlgorithm pDA = new PathfindingDistanceAlgorithm(this, target);
		if (!(pDA.failed())) {
			return pDA.getShortestDistance();
		} else {
			return -1;
		}
	}

	@Deprecated
	public void sendPackage(Node target) {
		GameScreen.packageHandler.addPath(this, target);
	}

	@Deprecated
	public void sendPackage(Node target, Color c) {
		GameScreen.packageHandler.addPath(this, target, c); 
	}

	@Deprecated
	public void sendPackage(Node target, Package p) {
		GameScreen.packageHandler.addPath(this, target, p);
	}
	
	@Deprecated
	public void sendPackage(Package p, Conveyor c) {
		GameScreen.packageHandler.addIndefinitePath(p, c);
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

	/**Flags this node as closed so that {@link Package}s can't pass through it.*/
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
