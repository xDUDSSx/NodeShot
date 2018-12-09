package org.dudss.nodeshot.buildings;

import java.util.Arrays;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.nodes.InputNode;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.entities.nodes.OutputNode;
import org.dudss.nodeshot.items.Ammo;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Furnace extends AbstractBuilding implements Storage {
	static float width = 48;
	static float height = 48;
	
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
		super(cx, cy, width, height);
		accepted = Arrays.asList(ItemType.COAL, ItemType.IRON);
	}
		
	@Override
	public void update() {
		if (ironStorage < maxIronStorage || coalStorage < maxCoalStorage) {
			input1.update();
			input2.update();
		}
	
		if (coalStorage >= maxCoalStorage && ironStorage >= maxIronStorage) {		
			if (this.firstConnector.checkEntrance(output, Base.PACKAGE_BLOCK_RANGE)) {
				generate();
				coalStorage--;
				ironStorage--;
			}
		}
	}

	private void generate() {		
		if (this.output.getAllConnectedNodes().size() > 0) {
			if (this.firstConnector.checkEntrance(output, Base.PACKAGE_BLOCK_RANGE)) {
				Ammo p = new Ammo(this.output);
				output.sendPackage(p);
			}
		}
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {
		r.set(ShapeType.Filled);
		r.setColor(this.color);
		r.rect(x, y, width, height);	
		
		r.end();
		batch.begin();
		input1.draw(batch);
		input2.draw(batch);
		output.draw(batch);
		batch.end();
		r.begin();
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {				
		r.set(ShapeType.Filled);
		r.setColor(prefabColor);
		r.rect(getPrefabX(cx, snap), getPrefabY(cy, snap), width, height);
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
		input1 = new InputNode(x + Base.CHUNK_SIZE/2, y + Base.CHUNK_SIZE/2, Base.RADIUS, this);
		input2 = new InputNode(x + 2*Base.CHUNK_SIZE + Base.CHUNK_SIZE/2, y + Base.CHUNK_SIZE/2, Base.RADIUS, this);
		output = new OutputNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		
		export = new ConveyorNode(x + (width/2), y + Base.CHUNK_SIZE*2 + Base.CHUNK_SIZE/2, Base.RADIUS);
		output.connectTo(export);
		
		firstConnector = GameScreen.connectorHandler.getConnectorInbetween(output, export, export.getConnectors());
		
		GameScreen.nodelist.add(input1);
		GameScreen.nodelist.add(input2);
		GameScreen.nodelist.add(output);
		GameScreen.nodelist.add(export);
		GameScreen.buildingHandler.addBuilding(this);
		
		updateFogOfWar(true);
	}

	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeBuilding(this);
		this.input1.remove();
		this.input2.remove();
		this.export.remove();
		this.output.remove();
		
		updateFogOfWar(false);
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
