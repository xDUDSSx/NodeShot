package org.dudss.nodeshot.entities.connectors;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.nodes.Node;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class Conveyor extends Connector {

	public Conveyor(Node from, Node to) {
		super(from, to);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void draw(ShapeRenderer sR) {
		super.draw(sR);
		
		float distanceVector = ((float) this.lenght / 6);		
		int numberOfArrows = Math.round((float)(this.lenght / distanceVector));
		
		float[] verts = new float[10 * numberOfArrows];
		
		float lPX = from.getCX();
		float lPY = from.getCY();
		
		int index = 0;
		
		Vector2 connectorVector = new Vector2((to.getCX() - from.getCX()), (to.getCY() - from.getCY()));
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
		
			float test = new Float(lPX);
			float test1 = new Float(lPY);
			
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
		Node newFrom;
		Node newTo;
		
		newFrom = this.to;
		newTo = this.from;
		
		this.from = newFrom;
		this.to = newTo;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.CONVEYOR;
	}
}
