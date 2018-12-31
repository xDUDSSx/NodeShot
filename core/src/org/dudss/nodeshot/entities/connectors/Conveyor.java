package org.dudss.nodeshot.entities.connectors;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.ConveyorBuilding;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class Conveyor extends Connector {

	public boolean reversed = false;
	
	List<ConveyorBuilding> conveyorBuildings;
	
	public Conveyor(Node from, Node to) {
		super(from, to);
		conveyorBuildings = new ArrayList<ConveyorBuilding>();
		
		List<Chunk> intersectedChunks = findLine((int) (from.getCX() / Base.CHUNK_SIZE),
				(int) (from.getCY() / Base.CHUNK_SIZE),
				(int)(to.getCX() / Base.CHUNK_SIZE), 
				(int) (to.getCY() / Base.CHUNK_SIZE));
		for (Chunk c : intersectedChunks) {
			conveyorBuildings.add(new ConveyorBuilding(c.getCX(), c.getCY(), this));
		}
	}
	
	public List<Chunk> findLine(int x0, int y0, int x1, int y1) 
    {                    
        List<Chunk> line = new ArrayList<Chunk>();
 
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
 
        int sx = x0 < x1 ? 1 : -1; 
        int sy = y0 < y1 ? 1 : -1; 
 
        int err = dx-dy;
        int e2;
 
        while (true) 
        {
            line.add(GameScreen.chunks.getChunkAtTileSpace(x0, y0));
 
            if (x0 == x1 && y0 == y1) 
                break;
 
            e2 = 2 * err;
            if (e2 > -dy) 
            {
                err = err - dy;
                x0 = x0 + sx;
            }
 
            if (e2 < dx) 
            {
                err = err + dx;
                y0 = y0 + sy;
            }
        }                                
        return line;
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
