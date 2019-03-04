package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.buildings.AbstractIOStorage;
import org.dudss.nodeshot.buildings.AbstractStorage;
import org.dudss.nodeshot.buildings.AmmoProcessor;
import org.dudss.nodeshot.buildings.ArtilleryCannon;
import org.dudss.nodeshot.buildings.BasicMine;
import org.dudss.nodeshot.buildings.ConveyorBuilding;
import org.dudss.nodeshot.buildings.CreeperGenerator;
import org.dudss.nodeshot.buildings.Exporter;
import org.dudss.nodeshot.buildings.Factory;
import org.dudss.nodeshot.buildings.Headquarters;
import org.dudss.nodeshot.buildings.Importer;
import org.dudss.nodeshot.buildings.NodeBuilding;
import org.dudss.nodeshot.buildings.PowerGenerator;
import org.dudss.nodeshot.buildings.Shipdock;
import org.dudss.nodeshot.buildings.Turret;
import org.dudss.nodeshot.buildings.UniversalStorage;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.misc.BuildingManager;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.Selector;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

/**The main game tool-bar menu*/
public class ToolbarMenu extends VisWindow {
	VisTable mainTable;
	
	VisTable buildWrapper;
	VisTable buildPanel;
	VisList<String> buildList;
	
	VisTable selectWrapper;
	
	BuildTable structures;
	BuildTable utils;
	BuildTable transfer;
	BuildTable weapons;
	
	VisLabel bitLabel;
	VisLabel powerLabel;
	VisLabel generatorLabel;
	
	/**Current entity selection menu (if an entity is selected)*/
	SelectTable currentSelectTable;
	
	/**The main game tool-bar menu.
	 * 
	 * @param title Title of the {@link VisWindow}.
	 */
	public ToolbarMenu(String title) {
		super("Toolbar", true);	
		TableUtils.setSpacingDefaults(this);
		setResizable(false);
		setMovable(false);
		getTitleTable().clear();	

		structures = new BuildTable();
		//structures.addBuildingTile(SpriteLoader.hqDrawable, "Headquarters", new BuildListener(new Headquarters(0, 0)));	
		structures.addBuildingTile(SpriteLoader.mineDrawable, "Mine", new BuildListener(new BasicMine(0, 0)));	
		structures.addBuildingTile(SpriteLoader.genDrawable, "Power generator", new BuildListener(new PowerGenerator(0, 0)));
		structures.addBuildingTile(SpriteLoader.factoryDrawable, "Factory", new BuildListener(new Factory(0, 0)));	
		structures.addBuildingTile(SpriteLoader.storageDrawable, "Universal storage", new BuildListener(new UniversalStorage(0, 0)));	
		structures.addBuildingTile(SpriteLoader.ammoProcessorDrawable, "Ammo processor", new BuildListener(new AmmoProcessor(0, 0)));	
		structures.addBuildingTile(SpriteLoader.shipdockDrawable, "Shipdock", new BuildListener(new Shipdock(0, 0)));	
		utils = new BuildTable();
		utils.addBuildingTile(SpriteLoader.creepergenDrawable, "Creeper generator", new BuildListener(new CreeperGenerator(0, 0)));	
		transfer = new BuildTable();
		transfer.addBuildingTile(SpriteLoader.nodeDrawable, "Conveyor", new BuildListener(new NodeBuilding(0, 0)));		
		transfer.addBuildingTile(SpriteLoader.importerTopDrawable, "Importer", new BuildListener(new Importer(0, 0)));		
		transfer.addBuildingTile(SpriteLoader.exporterTopDrawable, "Exporter", new BuildListener(new Exporter(0, 0)));
		weapons = new BuildTable();
		weapons.addBuildingTile(SpriteLoader.turretDrawable, "Turret", new BuildListener(new Turret(0, 0)));
		weapons.addBuildingTile(SpriteLoader.artilleryDrawable, "Artillery Cannon", new BuildListener(new ArtilleryCannon(0, 0)));
		
		buildPanel = new VisTable();
		buildList = new VisList<String>();
		buildList.setItems("STRUCTURES", "TRANSFER", "WEAPONS", "UTILS"); 		
		buildList.addCaptureListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	updateMainPanel();
            }
        });
		
		//initial selection
		buildList.setSelected("STRUCTURES");		
		buildPanel.add(structures).expand().fill();
		
		top();
		defaults().top();
		
		mainTable = new VisTable();
		mainTable.align(Align.topLeft);
		TableUtils.setSpacingDefaults(mainTable);
		
		VisTable infoTable = new VisTable();
		infoTable.top();
		TableUtils.setSpacingDefaults(infoTable);	
		this.getTitleTable().left();
		bitLabel = new VisLabel("Bits: " + Base.START_BITS);
		powerLabel = new VisLabel("Power: " + GameScreen.resourceManager.getPower() + " / " + GameScreen.resourceManager.getMaxPower());
		generatorLabel = new VisLabel("Creeper generators left: " + GameScreen.buildingManager.getAllGenerators().size());
		this.getTitleTable().add(bitLabel).width(bitLabel.getWidth() + 50).pad(5);
		this.getTitleTable().add(powerLabel).width(powerLabel.getWidth() + 50).pad(5);	
		this.getTitleTable().add(generatorLabel).width(generatorLabel.getWidth() + 50).pad(5).right();
		mainTable.add(infoTable).fill();
		
		buildWrapper = new VisTable();
		buildWrapper.align(Align.topLeft);
		TableUtils.setSpacingDefaults(buildWrapper);
		buildWrapper.add(buildList);
		buildWrapper.addSeparator(true);
		buildWrapper.add(buildPanel).expand().fill();
		mainTable.add(buildWrapper).expand().fill();
		
		selectWrapper = new VisTable();
		selectWrapper.top();
		TableUtils.setSpacingDefaults(selectWrapper);
		
		add(mainTable).expand().fill();
		
		//debugAll();
		
		pack();
		updateBounds();	
		updateMainPanel();
	}
	
	/**Update the menu selection. Should be called when the menu should switch panels.*/
	public void updateMainPanel() {
		if (GameScreen.selectedEntity == null) {
			mainTable.clearChildren();
			mainTable.add(buildWrapper);
			buildPanel.clearChildren();
	        switch(buildList.getSelected()) {
	        	case "STRUCTURES": buildPanel.add(structures).expand().fill(); break;
	        	case "TRANSFER": buildPanel.add(transfer).expand().fill(); break;
	        	case "WEAPONS": buildPanel.add(weapons).expand().fill(); break;
	        	case "UTILS":  buildPanel.add(utils).expand().fill(); break;
	        }
	        currentSelectTable = null;
		} else {
			mainTable.clearChildren();
			mainTable.add(selectWrapper);
			selectWrapper.clearChildren();
			currentSelectTable = new SelectTable(GameScreen.selectedEntity);
			selectWrapper.add(currentSelectTable);
		}
	}
	
	/**Updates the menu info data*/
	public void updateInfoPanel() {
		bitLabel.setText("Bits: " + GameScreen.resourceManager.getBits());
		powerLabel.setText("Power: " + GameScreen.resourceManager.getPower() + " / " + GameScreen.resourceManager.getMaxPower());
		generatorLabel.setText("Creeper generators left: " + GameScreen.buildingManager.getAllGenerators().size());
	
		if (currentSelectTable != null) {
			currentSelectTable.updateInfo(GameScreen.selectedEntity);
		}
	}
	
	public void updateBounds() {
		if (Gdx.graphics.getWidth() > 1200) {
			setSize(700, this.getHeight());
		} else {
			setSize(Gdx.graphics.getWidth()/2, this.getHeight());
		}
		setPosition(Gdx.graphics.getWidth()/2 - this.getWidth()/2, 0);
	}
	
	private class SelectTable extends VisTable {
		VisLabel infoLabel;
		VisTextButton moveBtn;
		VisTextButton demoBtn;
		VisTextButton demoConveyorBtn;
		VisTextButton reverseBtn;
		
		VisLabel storageValue;
		VisLabel processedValue;
		VisLabel healthDamage;
		VisLabel percentageLabel;	
		
		public SelectTable(Entity e) {
			super(true);
			
			TableUtils.setSpacingDefaults(this);
			align(Align.topLeft);						
			
			VisTable leftPanel = new VisTable(true);	
			Image thumbnail = new Image(getImageForCurrentEntity(e));
			leftPanel.add(thumbnail);
			VisTable btns = new VisTable();
			leftPanel.add(btns).right().fillY();
			
			moveBtn = new VisTextButton("Move");
			moveBtn.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {	 	
					if (e instanceof AbstractBuilding) {
						GameScreen.buildingManager.startBuildMode((AbstractBuilding) e, true);
					}
			    }
		    });	
			demoBtn = new VisTextButton("Demolish");
			demoBtn.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {	 	
					if (e instanceof AbstractBuilding) {
						((AbstractBuilding) e).demolish(true);
						Selector.deselect();
					}
			    }
		    });	
			reverseBtn = new VisTextButton("Reverse");
			reverseBtn.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {	 	
					if (e instanceof Conveyor) {
						((Conveyor) e).reverse();
					}
			    }
		    });	
			demoConveyorBtn = new VisTextButton("Disconnect");
			demoConveyorBtn.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {	 	
					if (e instanceof Conveyor) {
						((Conveyor) e).getFrom().disconnect(((Conveyor) e).getTo());
						Selector.deselect();
					}
			    }
		    });	
			if (e instanceof AbstractBuilding && !(e instanceof CreeperGenerator)) {							
				if (!(e instanceof Headquarters)) {
					btns.add(moveBtn).fillX();		
				}
				if (!(e instanceof Headquarters) && !(e instanceof ConveyorBuilding)) {
					btns.row();
					btns.add(demoBtn).fillX();
				}				
			}
			
			if (e instanceof Conveyor) {
				btns.row();
				btns.add(demoConveyorBtn).fillX();
				//btns.row();
				//btns.add(reverseBtn).fillX();
				//TODO: fix conveyor reverse
			}
			
			switch(e.getType()) {			
				case CONVEYOR_BUILDING: 
					if (((ConveyorBuilding)e).getConveyors().size() > 1) {
						infoLabel = new VisLabel("Conveyor (Junction)"); 
					} else {
						infoLabel = new VisLabel("Conveyor"); 
					}					
					break;
				default: 
					infoLabel = new VisLabel(e.getType().toString()); 
					break;
			}
			
			add(infoLabel).top().left().expandX();
			row();
			add(leftPanel).left().top();
			
			VisTable dataTable = new VisTable(true);
			add(dataTable).top().fill();
			
			storageValue = new VisLabel("Storage: placeholder");
			processedValue = new VisLabel("Processed: placeholder");
			healthDamage = new VisLabel("Damage: placeholder");
			percentageLabel = new VisLabel("Percentage: placeholder");
			
			if (e instanceof AbstractStorage) {
				dataTable.add(storageValue).left().fillX().top();
				dataTable.row();
			}
			if (e instanceof AbstractIOStorage) {				
				dataTable.add(processedValue).left().fillX().top();
				dataTable.row();
			}
			if (e instanceof CreeperGenerator) {
				dataTable.add(healthDamage).left().fillX().top();
				dataTable.row();
			}
			if (e instanceof Package) {
				dataTable.add(percentageLabel).left().fillX().top();
				dataTable.row();
			}
		}
		
		/**Updates the selection data.*/
		public void updateInfo(Entity e) {
			if (e instanceof AbstractStorage) storageValue.setText("Storage: " + ((AbstractStorage)e).getStoredItems().size() + "/" + ((AbstractStorage)e).getMaxStorage());
			if (e instanceof AbstractIOStorage) processedValue.setText("Processed: " + ((AbstractIOStorage)e).getProcessedStorage().size() + "/" + ((AbstractIOStorage)e).getMaxProcessedStorage());
			if (e instanceof CreeperGenerator) healthDamage.setText("Health: " + ((((CreeperGenerator)e).health) - ((CreeperGenerator)e).damage));
			if (e instanceof Package) percentageLabel.setText("Percentage: " + ((Package)e).percentage);
		}
		
		private Drawable getImageForCurrentEntity(Entity e) {
			switch(e.getType()) {
				case FACTORY: return SpriteLoader.factoryDrawable;
				case POWER_GENERATOR: return SpriteLoader.genDrawable;
				case CONVEYOR_BUILDING: return SpriteLoader.nodeDrawable;
				case NODE_BUILDING: return SpriteLoader.nodeDrawable;
				case NODE: return SpriteLoader.nodeDrawable;
				case CREEPER_GENERATOR: return SpriteLoader.creepergenDrawable;
				case TURRET: return SpriteLoader.turretDrawable;				
				case ARTILLERY_CANNON: return SpriteLoader.artilleryDrawable;
				case EXPORTER: return SpriteLoader.exporterTopDrawable;
				case IMPORTER: return SpriteLoader.importerTopDrawable;
				case SHIPDOCK: return SpriteLoader.shipdockDrawable;
				case AMMO_PROCESSOR: return SpriteLoader.ammoProcessorDrawable;
				case MINE: return SpriteLoader.mineDrawable;
				case HQ: return SpriteLoader.hqDrawable;		
				case UNIVERSAL_STORAGE: return SpriteLoader.storageDrawable;
				case CONVEYOR: return SpriteLoader.beltDrawable;
				default: return SpriteLoader.missingImage;
			}
		}
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
			buildingTable.padBottom(20);
			buildingTable.padLeft(5);
			buildingTable.padRight(5);
			buildingTable.padTop(2);
			
			VisScrollPane scrollPane = new VisScrollPane(buildingTable);
			scrollPane.setFadeScrollBars(true);
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
			addActor(btn);
			//addActor(new VisLabel(this.desc));
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