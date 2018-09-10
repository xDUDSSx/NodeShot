package org.dudss.nodeshot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.dudss.nodeshot.Base.PACKAGE_BLOCK_RANGE;
import static org.dudss.nodeshot.Base.PACKAGE_SPEED;

public class Connector implements Entity{
	Node from;
	Node to;
	
	Color red = new Color(255, 0, 0, 1f);
	Color green = new Color(0, 255, 0, 1f);
	
	int id;
	
	double lenght;
	boolean jammed = false;
	
	List<Package> packages;
	
	public Connector(Node from, Node to) {
		this.from = from;
		this.to = to;
		
		this.id = java.lang.System.identityHashCode(this);
		
		packages = new CopyOnWriteArrayList<Package>();
		
		lenght = Math.hypot(from.getCX() - to.getCX(), from.getCY() - to.getCY());
		
		System.out.println("New connector lenght" + lenght);
	}
	
	/**
	* Draw the connector
	*/
	public void draw(ShapeRenderer sR) {
		if (GameScreen.selectedID == this.getID()) {
			sR.setColor(Color.WHITE);
			sR.rectLine(from.getCX(), from.getCY(), to.getCX(), to.getCY(), Base.lineWidth + 1);
		}
		if (packages.size() == 0) {
			sR.setColor(red);
		} else {
			sR.setColor(green);
		}
		
		if (jammed == true) {
			sR.setColor(Color.YELLOW);
		}
		
		sR.rectLine(from.getCX(), from.getCY(), to.getCX(), to.getCY(), Base.lineWidth);
		sR.setColor(Color.WHITE);	
	} 
		
	/**
	* Call a connector update, updating logic
	*/
	public void update() {
		
		//If any of the packages is moving within the connector during this update call, it means it's not jammed
		boolean packageMovement = false;
		
		//For every package in the connector
       	for (Package packAge : packages) {
	 		if(packAge.going == true) {
	 			Vector2 p1 = new Vector2(packAge.from.getCX(), packAge.from.getCY());
                Vector2 p2 = new Vector2(packAge.to.getCX(), packAge.to.getCY());
                Vector2 vector = new Vector2(p2.x - p1.x, p2.y - p1.y);
	   	 		
	   	 		Boolean packageJam = false;
	   	 		
	   	 		double lenghtFactor = 100/lenght;
	   	 		
	   	 		double ACTUAL_PACKAGE_SPEED = PACKAGE_SPEED * lenghtFactor;
	   	 		double ACTUAL_PACKAGE_BLOCK_RANGE = PACKAGE_BLOCK_RANGE * lenghtFactor;
	   	 		
	   	 		//For every OTHER package in the connector
	   	 		for (Package p : packages) {
	   	 			if (p != packAge) {
	   	 				//Checking the direction
	   	 				if (p.from == packAge.from) {
	   	 					//Check all packages for colliding movement zones
		   	 				if (((p.percentage <= (packAge.percentage + ACTUAL_PACKAGE_SPEED + ACTUAL_PACKAGE_BLOCK_RANGE)) && (packAge.percentage <= (p.percentage + ACTUAL_PACKAGE_BLOCK_RANGE))) && p.isFinished() == false) {		   	 				
		   	 					/*- If the package we are currently checking IS colliding
		   	 					  	check if the package its colliding with is AHEAD of BEHIND in the current package direction
			   	 					  BEHIND -> keep going
			   	 					  AHEAD -> stop (jam)
			   	 					  
		   	 					  - If the packages are colliding and both have the same percentages -> make the one created earlier jam
		   	 					  (comes to play when spawning packages)
		   	 					*/
		   	 					if (packAge.percentage > p.percentage) {
		   	 						packageJam = false;
		   	 					} else 
		   	 					if (packAge.percentage == p.percentage) {
			   	 					if(!(this.packages.indexOf(packAge) > this.packages.indexOf(p))) {
			   	 						packageJam = true;
				   	 				}	   	 					
		   	 					} else {
		   	 						packageJam = true;
		   	 					}
		   	 				} 
	   	 				} else {
	   	 					//TODO: Interaction with the packages in the opposite direction
		   	 			}
	   	 			}
	   	 		}	   	
	   	 		//Moving packages (when possible)
	   	 		if (!(packAge.percentage >= 100) && packageJam == false) {
	   	 			
	 				packAge.percentage += ACTUAL_PACKAGE_SPEED;	 	
	 				
	 				//Entire connector jam indicator
	 				packageMovement = true;
	 				
	 				Vector2 finalVector = new Vector2((float)(vector.x * (0.01 * packAge.percentage)), (float)(vector.y * (0.01 * packAge.percentage)));
	 				
	 				packAge.transform((float)((p1.x - packAge.radius/2) + finalVector.x), (float)((p1.y - packAge.radius/2) + finalVector.y));
	 				packAge.currentMovePos = new Vector2((float)((p1.x - packAge.radius/2) + finalVector.x),  (float)((p1.y - packAge.radius/2) + finalVector.y));
	 			
	 			//Handling finished packages
	   	 		} else if (packAge.percentage >= 100 && packageJam == false && packAge.to.isClosed() == false) {  	 			
	   	 			//Resetting package to a state picked up by the path handlers
   	 				packAge.alert();	   	 			
	   	 		}
	 		}
       	}
       	
       	//Unless there are no packages. Was there any package movement? 
       	if (packageMovement == false && packages.size() != 0) {
       		this.jammed = true;
       	} else {
       		jammed = false;
       	}
	}
	
	/**
	* Check if the area around {@link Node} n (0 to blockRange) is clear for a new {@link Package} to transfer
	* @param n
	*/
	public Boolean checkEntrance(Node n, float blockRange) {
		Boolean clear = true;
		
		double lenghtFactor = 100/lenght;
	 		
	 	double ACTUAL_PACKAGE_BLOCK_RANGE = blockRange * lenghtFactor;
		
		for (Package p : this.packages) {
			//Check direction (only packages in the same direction can block)
			//Ignore the package in the other direction
			if (n == p.from) {  
				if (this.from == n) {
					if (p.from == this.from) {
						if (0 <= (p.percentage +  ACTUAL_PACKAGE_BLOCK_RANGE) && p.percentage <= (0 +  ACTUAL_PACKAGE_BLOCK_RANGE) && p.isFinished() == false) {
							clear = false;				
				 		}	
					} else {
						if ((100 -  ACTUAL_PACKAGE_BLOCK_RANGE) <= (p.percentage +  ACTUAL_PACKAGE_BLOCK_RANGE) && p.percentage <= 100 && p.isFinished() == false) {
							clear = false;				
				 		}	
					}
				} else {
					if (p.from == this.from) {
						if ((100 -  ACTUAL_PACKAGE_BLOCK_RANGE) <= (p.percentage +  ACTUAL_PACKAGE_BLOCK_RANGE) && p.percentage <= 100 && p.isFinished() == false) {
							clear = false;				
				 		}	
					} else {
						if (0 <= (p.percentage +  ACTUAL_PACKAGE_BLOCK_RANGE) && p.percentage <= (0 +  ACTUAL_PACKAGE_BLOCK_RANGE) && p.isFinished() == false) {
							clear = false;				
				 		}	
					}
				}	
			}
		}
		return clear;
	}
	
	public Package recievePackage(Node n) {
		Package recievedPackage = null;
		
		for (Package p : this.packages) {	
			if (n == p.to) { 
				if (p.percentage >= 100) {
					recievedPackage = p;
					break;
				}	
			}
		}
		return recievedPackage;
	}
	
	/**Add pkg to connector*/
	public void add(Package pkg) {
		if (pkg.from == this.from) {
			pkg.transform(from.getCX() - pkg.radius/2, from.getCY() - pkg.radius/2);
		} else {
			pkg.transform(to.getCX() - pkg.radius/2, to.getCY() - pkg.radius/2);
		}
		
		packages.add(pkg);
	}
	
	/**Remove pkg from connector*/
	public void remove(Package pkg) {
		packages.remove(pkg);
	}
	
	/**Remove all packages from connector*/
	public void removeAllPackages() {
		packages.clear();
	}
	
	public Node getFrom() {
		return from;
	}
	
	public Node getTo() {
		return to;
	}
	
	public int getID() {
		return id;
	}
	
	@Override
	public int getIndex() {
		return GameScreen.connectorHandler.getAllConnectors().indexOf(this);
	}

	public Node getFurtherNode(Node from) {
		if (this.from == from) {
			return this.to;
		} else {
			return this.from;
		}
	}
	
	public List<Package> getPackages() {
		return packages;
	}
	
	public double getLenght() {
		return lenght;
	}

	public boolean isJammed() {
		return this.jammed;	
	}
	@Override
	public EntityType getType() {
		return EntityType.CONNECTOR;
	}
	
	@Override
	public float getX() {
		double x1, x2;
		
        x1 = from.getCX();
        x2 = to.getCX();
       
        double vX = (x2 - x1) / 2;     

        float fX = (float) (x1 + vX);
		return fX;
	}

	@Override
	public float getY() {
		double y1, y2;
        y1 = from.getCY();
        y2 = to.getCY();

        double vY = (y2 - y1) / 2;

        float fY = (float) (y1 + vY);
		return fY;
	}	
}
