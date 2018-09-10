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
		//System.out.println("Indefinite package sent: at: " + System.currentTimeMillis());
	}
	
	@Override
	public void update() {
		if (currentPackage.going == false) {		
			//Gets all possible connectors to go
			List<Connector> connectors = new CopyOnWriteArrayList<Connector>(currentPackage.to.getConnectors());
			
			//Removing currentConnector (we wont go again)
			connectors.remove(currentConnector);
			
			//Selecting route
			if (connectors.size() != 0) {
				//Selecting a random connector
				Connector nextConnector = connectors.get(Base.getRandomIntNumberInRange(0, connectors.size() - 1));		
				
				//Directions
				if (nextConnector.getFrom() == medianNode) {	
					boolean isNextConnectorClear = nextConnector.checkEntrance(nextConnector.getFrom(), Base.PACKAGE_BLOCK_RANGE);
					if (isNextConnectorClear) {
						currentPackage.reset(nextConnector.getFrom(), nextConnector.getTo());	

						medianNode = nextConnector.getTo();
						
						currentConnector.remove(currentPackage);
						nextConnector.add(currentPackage);
						currentConnector = nextConnector;	
						currentPackage.go();			
					}
				} else {
					boolean isNextConnectorClear = nextConnector.checkEntrance(nextConnector.getTo(), Base.PACKAGE_BLOCK_RANGE);
					if (isNextConnectorClear) {
						currentPackage.reset(nextConnector.getTo(), nextConnector.getFrom());	
						medianNode = nextConnector.getFrom();
						
						currentConnector.remove(currentPackage);
						nextConnector.add(currentPackage);
						currentConnector = nextConnector;	
						currentPackage.go();			
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
