package org.dudss.nodeshot.buildings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Conveyor;
import org.dudss.nodeshot.entities.ConveyorNode;
import org.dudss.nodeshot.entities.InputNode;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.OutputNode;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

public class Furnace implements Building, Storage {

	float x,y;
	float cx,cy;
	float width = 64;
	float height = 64;
	
	private Color prefabColor = new Color(244f/255f, 100f/255f, 17f/255f, 0.5f);
	private Color color = new Color(244f/255f, 100f/255f, 17f/255f, 1f);
	
	InputNode input1;
	InputNode input2;
	
	OutputNode output;
	
	Node export;
	
	Connector firstConnector;
	
	int coalStorage = 0;
	int ironStorage = 0;
	int maxCoalStorage = 1;
	int maxIronStorage = 1;
	
	//List of items accepted by this building
	List<ItemType> accepted;
	
	public Furnace(float cx, float cy) {
		this.cx = cx;
		this.cy = cy;
		
		x = cx - (width/2);
		y = cy - (height/2);
		
		accepted = Arrays.asList(ItemType.COAL, ItemType.IRON);
	}
		
	@Override
	public void update() {
		if (ironStorage < maxIronStorage || coalStorage < maxCoalStorage) {
			input1.update();
			input2.update();
		}
	
		if (coalStorage >= maxCoalStorage && ironStorage >= maxIronStorage) {			
			generate();
			coalStorage--;
			ironStorage--;
		}
	}

	private void generate() {
		System.out.println("Factory generate! at " + System.currentTimeMillis());
		if (this.output.getAllConnectedNodes().size() > 0) {
			if (this.firstConnector.checkEntrance(output, Base.PACKAGE_BLOCK_RANGE)) {
				Package p = new Package(this.output);
				output.sendPackage(p);
				System.out.println("sending product");
			}
		}
	}
	
	@Override
	public void setLocation(float cx, float cy) {
		this.cx = cx;
		this.cy = cy;
		
		x = cx - (width/2);
		y = cy - (height/2);
	}

	@Override
	public void draw(ShapeRenderer r) {
		r.set(ShapeType.Filled);
		r.setColor(this.color);
		r.rect(x, y, width, height);	
	}

	@Override
	public void drawPrefab(ShapeRenderer r, float cx, float cy) {		
		float x = cx - (width/2);
		float y = cy - (height/2);
		
		r.set(ShapeType.Filled);
		r.setColor(this.prefabColor);
		r.rect(x, y, width, height);	
	}

	@Override
	public void alert(Package p) {
		if (p instanceof Coal) {
			coalStorage++;
		}
		
		if (p instanceof Iron) {
			ironStorage++;
		}
	}

	@Override
	public void build() {
		input1 = new InputNode(x + (width/6), y + (height * 0.15f), Base.RADIUS, this);
		input2 = new InputNode(x + ((width/6) * 5), y + (height * 0.15f), Base.RADIUS, this);
		output = new OutputNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		
		export = new ConveyorNode(x + (width/2), y + (height * 0.9f), Base.RADIUS);
		output.connectTo(export);
		
		firstConnector = GameScreen.connectorHandler.getConnectorInbetween(output, export, export.getConnectors());
		
		GameScreen.nodelist.add(input1);
		GameScreen.nodelist.add(input2);
		GameScreen.nodelist.add(output);
		GameScreen.nodelist.add(export);
		GameScreen.buildingHandler.addBuilding(this);
	}

	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeBuilding(this);
		this.input1.remove();
		this.input2.remove();
		this.export.remove();
		this.output.remove();
	}

	@Override
	public boolean canStore(ItemType type) {
		if (accepted.contains(type)) {
			if (type == ItemType.COAL) {			
				if (coalStorage < maxCoalStorage) {
					return true;
				}
			}
			
			if (type == ItemType.IRON) {			
				if (ironStorage < maxIronStorage) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void setAccepted(List<ItemType> accepted) {
		this.accepted = accepted;
	}

	@Override
	public List<ItemType> getAccepted() {
		return accepted;
	}
}