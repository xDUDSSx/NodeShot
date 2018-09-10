package org.dudss.nodeshot.misc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;

public class IndefinitePathHandler implements PathHandler {

	Package currentPackage;
	Connector currentConnector;
	//Determines the direction
	Node medianNode;
		
	boolean done = false;
	
	IndefinitePathHandler(Package p, Connector c) {
		currentPackage = p;
		currentConnector = c;
	}
	
	@Override
	public void start() {
		if (currentPackage.to == null) {
			currentPackage.reset(currentPackage.from, currentConnector.getFurtherNode(currentPackage.from));
		}
		medianNode = currentPackage.to;
		currentConnector.add(currentPackage);	
		currentPackage.go();
		System.out.println("Indefinite package sent: at: " + System.currentTimeMillis());
	}
	
	@Override
	public void update() {
		if (currentPackage.going == false) {		
			//Gets all possible connectors to go
			List<Connector> connectors = new CopyOnWriteArrayList<Connector>(currentPackage.to.getConnectors());
			System.out.println(" preremov of connectors: " + connectors.size());
			//Removing currentConnector (we wont go again)
			connectors.remove(currentConnector);
			
			System.out.println("Handler handles at: " + System.currentTimeMillis() + " n of connectors: " + connectors.size());
			
			//Selecting route
			if (connectors.size() != 0) {
				System.out.println("Getting next connector");
				//Selecting a random connector
				Connector nextConnector = connectors.get(Base.getRandomIntNumberInRange(0, connectors.size() - 1));		
				
				//Directions
				if (nextConnector.getFrom() == medianNode) {	
					System.out.println("One WAY!!");
					boolean isNextConnectorClear = nextConnector.checkEntrance(nextConnector.getFrom(), Base.PACKAGE_BLOCK_RANGE, Base.PACKAGE_SPEED);
					System.out.println("nextConnectorClear: " + isNextConnectorClear);
					if (isNextConnectorClear) {
						System.out.println("Clear");
						currentPackage.reset(nextConnector.getFrom(), nextConnector.getTo());	
						System.out.println("nextNode set at " + System.currentTimeMillis() + " index: " + nextConnector.getTo().getIndex());
						medianNode = nextConnector.getTo();
						
						currentConnector.remove(currentPackage);
						nextConnector.add(currentPackage);
						currentConnector = nextConnector;	
						currentPackage.go();
						System.out.println("Indefinite package sent: at: " + System.currentTimeMillis());				
					}
				} else {
					System.out.println("Other WAY!!");
					boolean isNextConnectorClear = nextConnector.checkEntrance(nextConnector.getTo(), Base.PACKAGE_BLOCK_RANGE, Base.PACKAGE_SPEED);
					System.out.println("nextConnectorClear: " + isNextConnectorClear);
					if (isNextConnectorClear) {
						System.out.println("Clear");
						currentPackage.reset(nextConnector.getTo(), nextConnector.getFrom());	
						System.out.println("nextNode set at " + System.currentTimeMillis());
						medianNode = nextConnector.getFrom();
						
						currentConnector.remove(currentPackage);
						nextConnector.add(currentPackage);
						currentConnector = nextConnector;	
						currentPackage.go();
						System.out.println("Indefinite package sent: at: " + System.currentTimeMillis());				
					}
				}
			//Dead end, no connectors to go
			} else {
				if (currentPackage.going == false) {
					//System.out.println("Dead end");
				}
			}
		}
	}
	
	@Override
	public void finish() {
		done = true;
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	@Override
	public PathHandlerType getType() {
		return PathHandlerType.IndefinitePathHandler;
	}
}
