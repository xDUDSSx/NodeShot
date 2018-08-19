package org.dudss.nodeshot.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.NodeConnector;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class NodeConnectorHandler {
	List<NodeConnector> connectors;
	
	public NodeConnectorHandler() {
		//connectors = Collections.synchronizedList(new ArrayList<NodeConnector>());
		connectors = new CopyOnWriteArrayList<NodeConnector>();
	}
	
	//Call updates to Connectors
	public void update() {
		for (NodeConnector nC : connectors) {
			nC.update();
		}
	}
	
	public void addConnector(NodeConnector nC) {
		connectors.add(nC);
	}
	
	public void createConnector(Node from, Node to) {
		connectors.add(new NodeConnector(from, to));
		//System.out.println("added new connector, from: " + from.getIndex() + " to: " + to.getIndex() + " connectorsSize: " + connectors.size());
	}
	
	public void removeConnector(NodeConnector nC) {
		connectors.remove(nC);
	}
	
	public NodeConnector getConnectorInbetween(Node from, Node to) {
		for (NodeConnector nC : connectors) {
			if ((nC.getFrom() == from && nC.getTo() == to) || (nC.getTo() == from && nC.getFrom() == to)) {
				return nC;
			}
		}
		return null;
	}
	
	public NodeConnector getConnectorInbetween(Node from, Node to, List<NodeConnector> pool) {
		for (NodeConnector nC : pool) {
			if ((nC.getFrom() == from && nC.getTo() == to) || (nC.getTo() == from && nC.getFrom() == to)) {
				return nC;
			}
		}
		return null;
	}
	
	public List<NodeConnector> getAllConnectorsToNode(Node n) {
		List<NodeConnector> list = new ArrayList<NodeConnector>();
		for (NodeConnector nC : connectors) {
			if (nC.getFrom() == n || nC.getTo() == n) {
				list.add(nC);
			}
		}
		return list;
	}
	
	public void removeAllPackagesInConnectors() {
		for (NodeConnector nC : connectors) {
			nC.removeAllPackages();
		}
	}
	
	public void drawAll(ShapeRenderer sR) {
		for (NodeConnector nC : connectors) {
			nC.draw(sR);
		}
	}
	
	public void draw(NodeConnector nC, ShapeRenderer sR) {
		nC.draw(sR);
	}
	
	public List<NodeConnector> getAllConnectors() {
		return connectors;
	}
	
}
