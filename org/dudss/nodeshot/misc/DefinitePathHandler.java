package org.dudss.nodeshot.misc;

import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.algorithms.PathfindingDistanceAlgorithm;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Package;

import com.badlogic.gdx.graphics.Color;

public class DefinitePathHandler implements PathHandler {
	
	List<Node> nodesToGo;
	
	public Node from;
	public Node to;
	
	Package currentPackage;
	
	Color color;
	
	int id;
	
	Boolean started = false;
	Boolean done = false;
	Boolean ended = false;
	Boolean failed = false;
	
	Boolean removedFromQueue = false;
	
	Node n1;
	Node n2;
	
	DefinitePathHandler(Node from, Node to) {
		this.from = from;
		this.to = to;
		
		this.id = System.identityHashCode(this);
		
		try {
			nodesToGo = calculatePath(from, to);
		} catch (RuntimeException ex) {
			//System.out.println("Cannot create path!");
			failed = true;
		}
	}

	DefinitePathHandler(Node from, Node to, Package p) {
		this.from = from;
		this.to = to;
		
		this.id = System.identityHashCode(this);
		
		currentPackage = p;
		
		nodesToGo = calculatePath(from, to);
	
		if (nodesToGo == null) {
			failed = true;
		}
	}
	
	public void start() {
		if (!failed) {
			if (nodesToGo.size() <= 1) {
				System.out.println("Can't send package to the same node!");
			} else {
				this.n1 = nodesToGo.get(0);
				this.n2 = nodesToGo.get(1);
				nodesToGo.remove(0);
		
				generatePackage(n1, n2);
		
				Connector nC = GameScreen.connectorHandler.getConnectorInbetween(n1, n2);
				nC.add(currentPackage);	
				currentPackage.go();
			}
			
			started = true;
		}
	}
	
	public void update() {
		//Check if pathHandlern start method has executed, a "concurrency failsafe"
		if(started == true && done == false) {			
			if (currentPackage.going == false) {				
				
				//If the current package is waiting at the destination
				if (currentPackage.to == to) {
					return;
				}
				
				//Calculate the path
				nodesToGo = calculatePath(currentPackage.to, to);
				
				//Path cannot be made, repeat
				if (nodesToGo == null) {
					return;
				}
				
				Node n1;
				Node n2;				
				if (!removedFromQueue) {
					n1 = nodesToGo.get(0);
					n2 = nodesToGo.get(1);
					nodesToGo.remove(0);
					
					this.n1 = n1;
					this.n2 = n2;
					
					removedFromQueue = true;
				} else {
					n1 = this.n1;
					n2 = this.n2;
				}
				
				Connector nextConnector = GameScreen.connectorHandler.getConnectorInbetween(n1, n2, n1.getConnectors());
				
				boolean isNextConnectorClear = nextConnector.checkEntrance(n1, Base.PACKAGE_BLOCK_RANGE);

				//Sending package to the next connector
				if (isNextConnectorClear) {
					generatePackage(n1, n2);
					removedFromQueue = false;
					nextConnector.add(currentPackage);	
					currentPackage.go();
				}
			}
		}
	}

	private List<Node> calculatePath(Node from, Node to) {
		//Creates new PathfindingDistanceAlgorithm
		PathfindingDistanceAlgorithm pDA = new PathfindingDistanceAlgorithm(from, to);
		
		//(-> Nodes in different webs || from and to are the same node)
		if (pDA.failed() == true) {
			//Cannot create path, returning null
			return null;
		}
		
		List<Node> path = pDA.getShortestPath();
		
		return path;
	}
	
	private void generatePackage(Node from, Node to) {
		if (currentPackage == null) {
			Package p = new Package(from, to);	

			if (this.color != null) {
				p.setColor(color);
			}
			currentPackage = p;
		} else {
			Connector previousConnector = GameScreen.connectorHandler.getConnectorInbetween(currentPackage.from, currentPackage.to, from.getConnectors());
			previousConnector.remove(currentPackage);
			currentPackage.reset(from, to);
		}
		
	}
	
	public void finish() {
		GameScreen.packageHandler.getAllPathHandlers().remove(this);
		done = true;		
	}
	
	public Node getNextNode() {
		return nodesToGo.get(1);
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public void done() {
		done = true;
	}

	@Override
	public PathHandlerType getType() {
		return PathHandlerType.DefinitePathHandler;
	}
}
