package org.dudss.nodeshot.entities.nodes;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.buildings.AlertableBuilding;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.misc.DefinitePathHandler;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

/**A special {@link Node} that handles {@link Package} transfer between various package systems. Currently supports {@link org.dudss.nodeshot.misc.IndefinitePathHandler}s only!*/
public class IONode extends ConveyorNode {

	AlertableBuilding building;
	
	public IONode(float cx, float cy, int radius, AbstractBuilding building) {
		super(cx, cy, radius);		
		
		this.building = (AlertableBuilding) building;
		this.setConnectable(false);		
		this.set(SpriteLoader.nodeOutputSprite);
		this.setPosition(x, y);
	}
	
	public void update() {
		if (this.getConnectors().size() > 0) {
			for (Connector c : this.getConnectors()) {
				Package p = c.recievePackage(this);		
				if (p != null) {					
					//Check if the package is an accepted type and if the storage is able to contain it
					if (building.alert(p.getStorable())) {
						switch (p.getPathHandler().getType()) {
							//Check if this IONode is the path destination of a definite path handler (if not, let it go through)
							case DefinitePathHandler: 
								DefinitePathHandler ph = (DefinitePathHandler) p.getPathHandler();
								if (ph.to == this) {
									c.remove(p);
									p.destroy();
									//building.alert(p.getStorable());
								}
								break;
							case IndefinitePathHandler: 
								c.remove(p);
								p.destroy();
								//building.alert(p.getStorable());
								break;
						}
					}
				}			
			}
		}
	}
	
	/**Whether this node can send a Package*/
	public boolean canSendPackage() {
		//Picks a random accessible conveyor
		List<Conveyor> possiblePaths = new ArrayList<Conveyor>();
		for (Connector c : this.getConnectors()) {
			if (c instanceof Conveyor) {
				if (((Conveyor) c).facesTheSameDirection(this)) {
					if(c.checkEntrance(this, Base.PACKAGE_BLOCK_RANGE)) {
						possiblePaths.add((Conveyor)c);
					}
				}
			}
		}
		if (possiblePaths.size() > 0) {
			return true;
		} 
		return false;
	}
	
	/**Sends a package to a random accessible {@link Conveyor} using the {@link PackageHandler#addIndefinitePath} method.
	 * @return Whether the operation was successful.*/
	public boolean sendIOPackage(Package export) {
		//Picks a random accessible conveyor
		List<Conveyor> possiblePaths = new ArrayList<Conveyor>();
		for (Connector c : this.getConnectors()) {
			if (c instanceof Conveyor) {
				if (((Conveyor) c).facesTheSameDirection(this)) {
					if(c.checkEntrance(this, Base.PACKAGE_BLOCK_RANGE)) {
						possiblePaths.add((Conveyor)c);
					}
				}
			}
		}
		//Sends the package
		if (possiblePaths.size() > 0) {
			Conveyor targetConveyor = null;
			targetConveyor = possiblePaths.get(Base.getRandomIntNumberInRange(0, possiblePaths.size()-1));
			GameScreen.packageHandler.addIndefinitePath(export, targetConveyor);
			return true;
		}
		return false;
	}
	
	public void setInputSprite() {
		this.set(SpriteLoader.nodeInputSprite);
		this.setPosition(x, y);
	}
	
	public void setOutputSprite() {
		this.set(SpriteLoader.nodeOutputSprite);
		this.setPosition(x, y);
	}
	
	public AbstractBuilding getBuilding() {
		return building;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.IONODE;
	}

}
