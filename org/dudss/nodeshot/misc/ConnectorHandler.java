package org.dudss.nodeshot.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.Connector;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class ConnectorHandler {
	List<Connector> connectors;
	
	public ConnectorHandler() {
		//connectors = Collections.synchronizedList(new ArrayList<NodeConnector>());
		connectors = new CopyOnWriteArrayList<Connector>();
	}
	
	//Call updates to Connectors
	public void update() {
		for (Connector nC : connectors) {
			nC.update();
		}
	}
	
	public void addConnector(Connector nC) {
		connectors.add(nC);
	}
	
	public void createConnector(Node from, Node to) {
		connectors.add(new Connector(from, to));
		//System.out.println("added new connector, from: " + from.getIndex() + " to: " + to.getIndex() + " connectorsSize: " + connectors.size());
	}
	
	public void removeConnector(Connector nC) {
		for (Package p : nC.getPackages()) {
			p.destroy();
		}
		
		nC.getTo().connectors.remove(nC);
		nC.getFrom().connectors.remove(nC);
		
		connectors.remove(nC); 
	}
	
	public Connector getConnectorInbetween(Node from, Node to) {
		for (Connector nC : connectors) {
			if ((nC.getFrom() == from && nC.getTo() == to) || (nC.getTo() == from && nC.getFrom() == to)) {
				return nC;
			}
		}
		return null;
	}
	
	public Connector getConnectorInbetween(Node from, Node to, List<Connector> pool) {
		for (Connector nC : pool) {
			if ((nC.getFrom() == from && nC.getTo() == to) || (nC.getTo() == from && nC.getFrom() == to)) {
				return nC;
			}
		}
		return null;
	}
	

	/**Deprecated, don't use anymore, ineffective*/ //TODO: ?? still used, solve
	public List<Connector> getAllConnectorsToNode(Node n) {
		List<Connector> list = new ArrayList<Connector>();
		for (Connector nC : connectors) {
			if (nC.getFrom() == n || nC.getTo() == n) {
				list.add(nC);
			}
		}
		return list;
	}
	
	public void removeAllPackagesInConnectors() {
		for (Connector nC : connectors) {
			nC.removeAllPackages();
		}
	}
	
	public void drawAll(ShapeRenderer sR) {
		sR.begin(ShapeType.Filled);
		for (Connector nC : connectors) {
			nC.draw(sR);
		}
		sR.end();
	}
	
	public void draw(Connector nC, ShapeRenderer sR) {
		sR.begin(ShapeType.Filled);
		nC.draw(sR);
		sR.end();
	}
	
	public List<Connector> getAllConnectors() {
		return connectors;
	}
	
}
