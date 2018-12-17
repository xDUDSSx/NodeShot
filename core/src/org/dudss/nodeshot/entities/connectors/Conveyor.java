package org.dudss.nodeshot.entities.connectors;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.nodes.Node;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class Conveyor extends Connector {

	public boolean reversed = false;
	
	public Conveyor(Node from, Node to) {
		super(from, to);
	}
	
	@Override
	public void draw(ShapeRenderer sR) {
		super.draw(sR);
		
		float distanceVector = ((float) this.lenght / 6);		
		int numberOfArrows = Math.round((float)(this.lenght / distanceVector));
		
		float[] verts = new float[10 * numberOfArrows];
				
		int index = 0;
		
		float lPX;
		float lPY;
		Vector2 connectorVector;
		if (!reversed) {
			lPX = from.getCX();
			lPY = from.getCY();
			connectorVector = new Vector2((to.getCX() - from.getCX()), (to.getCY() - from.getCY()));
		} else {
			lPX = to.getCX();
			lPY = to.getCY();
			connectorVector = new Vector2((from.getCX() - to.getCX()), (from.getCY() - to.getCY()));
		}
		
		Vector2 connectorNormalVector = new Vector2(connectorVector.y, -(connectorVector.x));
		double perc = this.lenght / 100;
		float linePerc = (float) (((float) Base.lineWidth/2) / perc);		
		float distanceVectorPerc = (float) ((float) distanceVector / perc);
		
		for (int i = 0; i < numberOfArrows; i++) {
			verts[index++] = lPX;
			verts[index++] = lPY;
			
			float a1 = lPX + connectorVector.x * (distanceVectorPerc/100);
			float a2 = lPY + connectorVector.y * (distanceVectorPerc/100);
			
			lPX = Float.valueOf(a1);
			lPY = Float.valueOf(a2);
			
			verts[index++] = lPX;
			verts[index++] = lPY;
			
			verts[index++] = new Float(lPX) + connectorNormalVector.x * (linePerc/100) - connectorVector.x * (linePerc/100);
			verts[index++] = new Float(lPY) + connectorNormalVector.y * (linePerc/100) - connectorVector.y * (linePerc/100);

			verts[index++] = lPX;
			verts[index++] = lPY;
			
			verts[index++] = new Float(lPX)- connectorNormalVector.x * (linePerc/100) - connectorVector.x * (linePerc/100);
			verts[index++] = new Float(lPY) - connectorNormalVector.y * (linePerc/100) - connectorVector.y * (linePerc/100);
		}

		sR.set(ShapeType.Line);
		Color c = sR.getColor();;
		Color newColor = new Color(c.r * 0.6f, c.g * 0.6f, c.b * 0.6f,1.0f);
		sR.setColor(newColor);	
		sR.polyline(verts);
		sR.set(ShapeType.Filled);
	}
	
	public void reverse() {
		reversed = !reversed;
	}
	
	public boolean isReversed() {
		return reversed;
	}
	
	/**Whether this conveyor faces the same direction as conveyor c, eg. Whether this connector can accept packages from connector c.*/
	public boolean facesTheSameDirection(Conveyor c) {
		if (!this.isReversed() && !c.isReversed()) {
			if (this.getFrom() != c.getFrom() && this.getTo() != c.getTo()) {
				return true;
			} else {
				return false;
			}
		} else
		if (this.isReversed() && !c.isReversed()) {
			if (this.getTo() != c.getFrom() && this.getFrom() != c.getTo()) {
				return true;
			} else {
				return false;
			}
		}
		new RuntimeException(this.getClass().getName() + " facesTheSameDirection() exception!");
		return false;
	}
	
	/**Whether a package sent from the {@link Node} n can access this conveyor.*/
	public boolean facesTheSameDirection(Node n) {
		if (!this.isReversed()) {
			if (this.getFrom() == n) {
				return true;
			}
		} else {
			if (this.getTo() == n) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.CONVEYOR;
	}
}
