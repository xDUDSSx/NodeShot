package org.dudss.nodeshot.misc;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.algorithms.NodePathfindingAlgorithm;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.NodeConnector;
import org.dudss.nodeshot.entities.Package;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

class PackagePathHandler {
	
	Queue<Node> nodesToGo;
	
	List<Node> allNodes;
	int currentIndex;
	
	Node from;
	Node to;
	
	Package currentPackage;
	
	Color color;
	
	int id;
	
	Boolean started = false;
	Boolean done = false;
	
	PackagePathHandler(Node from, Node to) {
		this.from = from;
		this.to = to;
		
		this.id = System.identityHashCode(this);
		
		allNodes = calculatePath();
		nodesToGo = new LinkedList<Node>(allNodes);
		
		currentIndex = 0;
	}
	
	void start() {
		if (nodesToGo.size() <= 1) {
			System.out.println("Can't send package to the same node!");
		} else {
			Node n1 = nodesToGo.remove();
			Node n2 = nodesToGo.element();
		
			System.out.println("index: " + currentIndex + " size: " + allNodes.size() + " allNodesNODE - " + allNodes.get(currentIndex).getID() + " queueNODE - " + n1.getID());		
			
			generatePackage(n1, n2);
	
			currentIndex++;		
			
			System.out.println("Start p: " + n1.getIndex() + " to " + n2.getIndex());
			NodeConnector nC = BaseClass.nodeConnectorHandler.getConnectorInbetween(n1, n2);
			nC.add(currentPackage);	
			currentPackage.go();System.out.println("Package sent: at: " + System.currentTimeMillis());
		}
		
		started = true;
	}
	
	void update() {
		//Check if pathHandlern start method has executed, a "concurrency failsafe"
		if(started) {
			if (nodesToGo.size() <= 1) {
				if (currentPackage.going == false) {
					finish();
				}
			} else 
			if (currentPackage.going == false) {
				Node n1 = nodesToGo.remove();
				Node n2 = nodesToGo.element();
	
				generatePackage(n1, n2);
	
				currentIndex++;
			
				System.out.println("Start p: " + n1.getIndex() + " to " + n2.getIndex());
				NodeConnector nC = BaseClass.nodeConnectorHandler.getConnectorInbetween(n1, n2);
				nC.add(currentPackage);	
				currentPackage.go();
				System.out.println("Package sent: at: " + System.currentTimeMillis());
			}
		}
	}
	
	private void generatePackage(Node from, Node to) {
		System.out.println("Package created: at: " + System.currentTimeMillis());
		if (currentPackage == null) {
			Package p = new Package(from, to);	

			if (nodesToGo.size() != 1) {
				p.setNextNode(allNodes.get(currentIndex + 2));
			} else {
				p.setNextNode(null);
			}	
			if (this.color != null) {
				p.setColor(color);
			}
			currentPackage = p;
		} else {
			currentPackage.reset(from, to);
		
			if (nodesToGo.size() != 1) {
				currentPackage.setNextNode(allNodes.get(currentIndex + 2));
			} else {
				currentPackage.setNextNode(null);
			}
		}
		
	}
	
	void finish() {
		done = true;
		System.out.println("Path at |" + from.getIndex() + "| to |" + to.getIndex() + "| finished!");
	}
		
	private List<Node> calculatePath() {
		//Creates new NodePathfindingAlgorithm
		NodePathfindingAlgorithm pFA = new NodePathfindingAlgorithm(from, to);
		
		//Check if pFA returned -1 (-> Nodes in different webs)
		if (pFA.getSteps() == -1) {
			System.out.println("Cannot create path!");
			return null;
		}
		
		//Retrieving path from pFA
		List<List<Node>> possiblePathways = pFA.getPathway();
		
		List<Node> path;
		//If there is only one possible pathway, use that one
		if (possiblePathways.size() == 1) {
			path = possiblePathways.get(0);
		} else {
			//Gets random path //TODO: additional distance calculations
			path = possiblePathways.get(Base.getRandomIntNumberInRange(0, possiblePathways.size()-1));
		}
	
		//Printing possible pathways
	/*	int a = 0;
		System.out.println("\n");
		for (List<Node> aN : possiblePathways) {
			a++;
			System.out.println("Pathway " + a);
			for(Node n : aN) {
				System.out.print(n.getIndex() + " - ");
			}
			System.out.println("\n");
		}
	*/
		return path;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	
	Boolean isDone() {
		return done;
	}
}
