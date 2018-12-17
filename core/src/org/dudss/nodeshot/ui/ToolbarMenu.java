package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.CoalMine;
import org.dudss.nodeshot.buildings.CreeperGenerator;
import org.dudss.nodeshot.buildings.Exporter;
import org.dudss.nodeshot.buildings.Factory;
import org.dudss.nodeshot.buildings.Headquarters;
import org.dudss.nodeshot.buildings.Importer;
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
	
	public ToolbarMenu(String title) {
		super("Toolbar", false);
		TableUtils.setSpacingDefaults(this);
		setResizable(false);
		setMovable(false);
			
		BuildTable structures = new BuildTable();
		structures.addBuildingTile(SpriteLoader.hqDrawable, "Headquarters", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new Headquarters(0, 0);
				}		
		    }
		});
		
		structures.addBuildingTile(SpriteLoader.mineDrawable, "Mine", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new CoalMine(0, 0);
				}
		    }
		});
		
		structures.addBuildingTile(SpriteLoader.genDrawable, "Generator", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new CoalMine(0, 0);
				}
		    }
		});
		
		structures.addBuildingTile(SpriteLoader.factoryDrawable, "Factory", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new Factory(0, 0);
				}		
		    }
		});
		
		BuildTable utils = new BuildTable();
		utils.addBuildingTile(SpriteLoader.creepergenDrawable, "Creeper generator", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new CreeperGenerator(0, 0);
				}		
		    }
		});		
		
		BuildTable transfer = new BuildTable();
		transfer.addBuildingTile(SpriteLoader.importerTopDrawable, "Importer", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new Importer(0, 0);
				}		
		    }
		});		
		
		transfer.addBuildingTile(SpriteLoader.importerTopDrawable, "Exporter", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new Exporter(0, 0);
				}		
		    }
		});		
		
		final VisTable container = new VisTable();
		VisList<String> list = new VisList<String>();
		list.setItems("STRUCTURES", "TRANSFER", "WEAPONS", "UTILS"); 		
		list.addCaptureListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	container.clearChildren();
                switch(list.getSelected()) {
                	case "STRUCTURES": container.add(structures).expand().fill(); break;
                	case "TRANSFER": container.add(transfer).expand().fill(); break;
                	case "WEAPONS": break;
                	case "UTILS":  container.add(utils).expand().fill(); break;
                }
            }
        });
		
		//initial selection
		list.setSelected("STRUCTURES");		
		container.add(structures).expand().fill();
		
		top();
		defaults().top();
		
		mainTable = new VisTable();
		mainTable.top();
		mainTable.defaults().top();
		TableUtils.setSpacingDefaults(mainTable);
			
		mainTable.add(list);
		mainTable.addSeparator(true);
		mainTable.add(container).expand().fill().prefHeight(Base.buildMenuImgSize + 50);
		add(mainTable).expand().fill();
		
		//debugAll();
		
		updateBounds();
		
		pack();		
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
}