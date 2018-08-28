package org.dudss.nodeshot.misc;

import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;

public class IndefinitePathHandler implements PathHandler{

	Package currentPackage;
	Connector currentConnector;
	//Determines the direction
	Node medianNode;
		
	IndefinitePathHandler(Package p, Connector c) {
		currentPackage = p;
		currentConnector = c;
	}
	
	@Override
	public void start() {
		medianNode = currentPackage.to;
		currentConnector.add(currentPackage);	
		currentPackage.go();
		System.out.println("Indefinite package sent: at: " + System.currentTimeMillis());
	}
	
	@Override
	public void update() {
		if (currentPackage.going == false) {		
			//Gets all possible connectors to go
			List<Connector> connectors = currentPackage.to.getConnectors();
			
			//Selecting route
			if (connectors.size() != 0) {
				//Selecting a random connector
				Connector nextConnector = connectors.get(Base.getRandomIntNumberInRange(0, connectors.size() - 1));		
				//The next connector cannot be the current one
				if (nextConnector != currentConnector) {
					//Directions
					if (nextConnector.getFrom() == medianNode) {	
						System.out.println("One WAY!!");
						currentPackage.reset(nextConnector.getFrom(), nextConnector.getTo());
						System.out.println("nextNode set at " + System.currentTimeMillis() + " index: " + nextConnector.getTo().getIndex());
						currentPackage.setNextNode(nextConnector.getTo());
						medianNode = nextConnector.getTo();
					} else {
						System.out.println("Other WAY!!");
						currentPackage.reset(nextConnector.getTo(), nextConnector.getFrom());	
						System.out.println("nextNode set at " + System.currentTimeMillis());
						currentPackage.setNextNode(nextConnector.getFrom());
						medianNode = nextConnector.getFrom();
					}
					
					currentConnector.remove(currentPackage);
					nextConnector.add(currentPackage);
					currentConnector = nextConnector;	
					currentPackage.go();
					System.out.println("Indefinite package sent: at: " + System.currentTimeMillis());	
				}
			//Dead end, no connectors to go
			} else {
				if (currentPackage.going == false) {
					System.out.println("Dead end");
				}
			}
		}
	}
	
	@Override
	public void finish() {
		
	}

	@Override
	public boolean isDone() {
		return false;
	}
	
}
