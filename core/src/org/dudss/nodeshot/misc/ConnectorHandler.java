package org.dudss.nodeshot.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.entities.nodes.Node;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**A manager class that manages {@link Connector} logic updates and draw calls.*/
public class ConnectorHandler {
	List<Connector> connectors;
	
	public ConnectorHandler() {
		//connectors = Collections.synchronizedList(new ArrayList<NodeConnector>());
		connectors = new CopyOnWriteArrayList<Connector>();
	}
	
	/**Calls a logic update on all connectors.*/
	public void update() {
		for (Connector nC : connectors) {
			nC.update();
		}
	}
	
	/**Adds a connector to the manager-*/
	public void addConnector(Connector nC) {
		connectors.add(nC);
	}
	
	/**Removes a connector from the manager-*/
	public void removeConnector(Connector nC) {
		for (Package p : nC.getPackages()) {
			p.destroy();
		}
		
		nC.getTo().connectors.remove(nC);
		nC.getFrom().connectors.remove(nC);
		
		if (nC instanceof Conveyor) ((Conveyor) nC).clearBuildingChunks();
		
		connectors.remove(nC); 
	}
	
	/**Returns the {@link Connector} connecting two {@linkplain Node}s.
	 * This method looks through all the {@linkplain Connector}s in this manager.
	 * @param from Node 1
	 * @param to Node 2
	 */
	public Connector getConnectorInbetween(Node from, Node to) {
		for (Connector nC : connectors) {
			if ((nC.getFrom() == from && nC.getTo() == to) || (nC.getTo() == from && nC.getFrom() == to)) {
				return nC;
			}
		}
		return null;
	}
	
	/**Returns the {@link Connector} connecting two {@linkplain Node}s.
	 * This method looks for the {@linkplain Connector} within the specified list.
	 * @param from Node 1
	 * @param to Node 2
	 * @param pool The list of {@linkplain Connector}s to look through. 
	 */
	public Connector getConnectorInbetween(Node from, Node to, List<Connector> pool) {
		for (Connector nC : pool) {
			if ((nC.getFrom() == from && nC.getTo() == to) || (nC.getTo() == from && nC.getFrom() == to)) {
				return nC;
			}
		}
		return null;
	}
	

	/**Deprecated, don't use anymore, ineffective*/ //TODO: ?? still used, solve
	@Deprecated
	public List<Connector> getAllConnectorsToNode(Node n) {
		List<Connector> list = new ArrayList<Connector>();
		for (Connector nC : connectors) {
			if (nC.getFrom() == n || nC.getTo() == n) {
				list.add(nC);
			}
		}
		return list;
	}
	
	/**Removes all {@link Package}s from all the {@link Connector}s in this manager.*/
	public void removeAllPackagesInConnectors() {
		for (Connector nC : connectors) {
			nC.removeAllPackages();
		}
	}
	
	/**Draws all {@link Connector}s in this manager.*/
	public void drawAll(ShapeRenderer sR, SpriteBatch batch) {
		sR.begin(ShapeType.Filled);
		for (Connector nC : connectors) {
			nC.draw(sR, batch);
		}
		sR.end();
	}
	
	/**@return A list of all the {@link Connector}s in this manager.*/
	public List<Connector> getAllConnectors() {
		return connectors;
	}
	
}
