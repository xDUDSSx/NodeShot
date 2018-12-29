package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.buildings.AmmoProcessor;
import org.dudss.nodeshot.buildings.BasicMine;
import org.dudss.nodeshot.buildings.CreeperGenerator;
import org.dudss.nodeshot.buildings.Exporter;
import org.dudss.nodeshot.buildings.Factory;
import org.dudss.nodeshot.buildings.Headquarters;
import org.dudss.nodeshot.buildings.Importer;
import org.dudss.nodeshot.buildings.NodeBuilding;
import org.dudss.nodeshot.buildings.PowerGenerator;
import org.dudss.nodeshot.buildings.Turret;
import org.dudss.nodeshot.misc.BuildingManager;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

public class ToolbarMenu extends VisWindow {
	
	VisTable mainTable;
	
	VisLabel bitLabel;
	VisLabel powerLabel;
	
	public ToolbarMenu(String title) {
		super("Toolbar", false);	
		TableUtils.setSpacingDefaults(this);
		setResizable(false);
		setMovable(false);
		getTitleTable().clear();	
		
		BuildTable structures = new BuildTable();
		structures.addBuildingTile(SpriteLoader.hqDrawable, "Headquarters", new BuildListener(new Headquarters(0, 0)));	
		structures.addBuildingTile(SpriteLoader.mineDrawable, "Mine", new BuildListener(new BasicMine(0, 0)));	
		structures.addBuildingTile(SpriteLoader.genDrawable, "Power generator", new BuildListener(new PowerGenerator(0, 0)));
		structures.addBuildingTile(SpriteLoader.factoryDrawable, "Factory", new BuildListener(new Factory(0, 0)));	
		structures.addBuildingTile(SpriteLoader.ammoProcessorDrawable, "Ammo processor", new BuildListener(new AmmoProcessor(0, 0)));	
		BuildTable utils = new BuildTable();
		utils.addBuildingTile(SpriteLoader.creepergenDrawable, "Creeper generator", new BuildListener(new CreeperGenerator(0, 0)));	
		BuildTable transfer = new BuildTable();
		transfer.addBuildingTile(SpriteLoader.nodeDrawable, "Conveyor", new BuildListener(new NodeBuilding(0, 0)));		
		transfer.addBuildingTile(SpriteLoader.importerTopDrawable, "Importer", new BuildListener(new Importer(0, 0)));		
		transfer.addBuildingTile(SpriteLoader.importerTopDrawable, "Exporter", new BuildListener(new Exporter(0, 0)));
		BuildTable weapons = new BuildTable();
		weapons.addBuildingTile(SpriteLoader.turretDrawable, "Turret", new BuildListener(new Turret(0, 0)));
		
		final VisTable buildPanel = new VisTable();
		VisList<String> buildList = new VisList<String>();
		buildList.setItems("STRUCTURES", "TRANSFER", "WEAPONS", "UTILS"); 		
		buildList.addCaptureListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	buildPanel.clearChildren();
                switch(buildList.getSelected()) {
                	case "STRUCTURES": buildPanel.add(structures).expand().fill(); break;
                	case "TRANSFER": buildPanel.add(transfer).expand().fill(); break;
                	case "WEAPONS": buildPanel.add(weapons).expand().fill(); break;
                	case "UTILS":  buildPanel.add(utils).expand().fill(); break;
                }
            }
        });
		
		//initial selection
		buildList.setSelected("STRUCTURES");		
		buildPanel.add(structures).expand().fill();
		
		top();
		defaults().top();
		
		mainTable = new VisTable();
		mainTable.top();
		mainTable.defaults().top();
		TableUtils.setSpacingDefaults(mainTable);
		
		VisTable infoTable = new VisTable();
		infoTable.top();
		TableUtils.setSpacingDefaults(infoTable);
		//this.getTitleLabel().setText("Bits: " + Base.START_BITS);
		
		this.getTitleTable().left();
		bitLabel = new VisLabel("Bits: " + Base.START_BITS);
		powerLabel = new VisLabel("Power: " + GameScreen.resourceManager.getPower() + " / " + GameScreen.resourceManager.getMaxPower());
		this.getTitleTable().add(bitLabel).width(bitLabel.getWidth() + 50);
		this.getTitleTable().add(powerLabel);
		
		//infoTable.add(bitLabel).fillX().left().top();
		//infoTable.row();
		//infoTable.add(powerLabel).fillX().left().top();
		mainTable.add(infoTable).fill();
		
		VisTable buildTable = new VisTable();
		buildTable.top();
		TableUtils.setSpacingDefaults(buildTable);
		buildTable.add(buildList);
		buildTable.addSeparator(true);
		buildTable.add(buildPanel).expand().fill();
		mainTable.add(buildTable).expand().fill();
		
		add(mainTable).expand().fill();
		
		//debugAll();
		
		updateBounds();
		
		pack();		
	}
	
	public void updateInfoPanel() {
		//bitLabel.setText("Bits: " + GameScreen.resourceManager.getBits());
		bitLabel.setText("Bits: " + GameScreen.resourceManager.getBits());
		powerLabel.setText("Power: " + GameScreen.resourceManager.getPower() + " / " + GameScreen.resourceManager.getMaxPower());
	}
	
	public void updateBounds() {
		if (Gdx.graphics.getWidth() > 1200) {
			setSize(700, this.getHeight());
		} else {
			setSize(Gdx.graphics.getWidth()/2, this.getHeight());
		}
		setPosition(Gdx.graphics.getWidth()/2 - this.getWidth()/2, 0);
	}
	
	/**A {@link Table} that holds individual building {@link BuildingTile}s. Supports horizontal scrolling.*/
	private class BuildTable extends VisTable {
		VisTable buildingTable;
		
		/**A {@link Table} that holds individual building {@link BuildingTile}s. Supports horizontal scrolling.*/
		public BuildTable() {
			super(true);
			
			TableUtils.setSpacingDefaults(this);
			align(Align.topLeft);						
			
			buildingTable = new VisTable();
			TableUtils.setSpacingDefaults(buildingTable);
			buildingTable.align(Align.topLeft);
			
			VisScrollPane scrollPane = new VisScrollPane(buildingTable);
			scrollPane.setFadeScrollBars(false);
			scrollPane.setFlickScroll(false);
			scrollPane.setOverscroll(false, false);
			scrollPane.setScrollingDisabled(false, true);
			add(scrollPane).grow().fill();
			
			//Disables the scrollPane mouse wheel listener!
			scrollPane.getListeners().removeIndex(scrollPane.getListeners().size-1);
		}
		
		/**Adds a {@link BuildingTile} to the table.
		 * @param d The image of the image button.
		 * @param desc The text displayed below the building image button.
		 * @param listener The click listener of the building image button.
		 */
		public void addBuildingTile(Drawable d, String desc, ClickListener listener) {
			BuildingTile tile = new BuildingTile(new VisImageButton(d), desc);
			tile.addClickListener(listener);
			buildingTable.add(tile);
		}
		
		@Override
		public void pack() {
			buildingTable.pack();
			pack();
		}
	}
	
	/**{@link VerticalGroup} representing a single building button. A combination of an image button and a label.*/
	private class BuildingTile extends VerticalGroup {
		VisImageButton btn;
		String desc;
		
		/**{@link VerticalGroup} representing a single building button. A combination of an image button and a label.*/
		BuildingTile(VisImageButton btn, String desc) {
			this.btn = btn;
			this.desc = desc;
			
			addActor(this.btn);
			addActor(new VisLabel(this.desc));
		}
		
		/**Adds a click listener to the assigned {@link VisImageButton}.*/
		public void addClickListener(ClickListener listener) {
			btn.addListener(listener);
		}
	}
	
	/**A special {@link ClickListener} that makes a direct call to the {@link BuildingManager} and initialises build mode with the assigned {@link AbstractBuilding}.*/
	class BuildListener extends ClickListener {
		AbstractBuilding b;
		
		/**A special {@link ClickListener} that makes a direct call to the {@link BuildingManager} and initialises build mode with the assigned {@link AbstractBuilding}.*/
		BuildListener(AbstractBuilding b) {
			super();
			this.b = b;
		}
		
		@Override
		public void clicked(InputEvent event, float x, float y) {	 		
			GameScreen.buildingManager.startBuildMode(b);
	    }
	}
}