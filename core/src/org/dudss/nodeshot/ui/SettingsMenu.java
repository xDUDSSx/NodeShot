package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

/**A window containing engine and debug settings*/
public class SettingsMenu extends Window {

	final Window window = this;
	
	public SettingsMenu(String title, Skin skin) {
		super(title, skin);
		setVisible(true);
		setMovable(true);
		setResizable(true);
		setPosition(10, 50);
		align(Align.top);
		addWidgets();
	}
	
	private void addWidgets() {
		VisTable table = new VisTable(true);
		
		VisTextButton engineSettingsButton = new VisTextButton("Engine settings");
		VisTable engineSettingsTable = new VisTable(true);
		CollapsibleWidget engineSettingsCollapsibleWidget = new CollapsibleWidget(engineSettingsTable);	
		engineSettingsTable.defaults().left();
		
		engineSettingsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				engineSettingsCollapsibleWidget.setCollapsed(!engineSettingsCollapsibleWidget.isCollapsed());
			}
		});
		
		VisCheckBox loggingCheckbox = new VisCheckBox("Enable GlProfiler logging", Base.enableGlProgilerLogging);
		VisCheckBox drawGeneralStatsCheckbox = new VisCheckBox("Draw general stats", Base.drawGeneralStats);
		VisCheckBox hoverChunkHighlightCheckbox = new VisCheckBox("Highlight chunks with cursor", Base.hoverChunkHighlight);
		VisCheckBox drawSectionBordersCheckbox = new VisCheckBox("Draw section borders", Base.drawSectionBorders);
		VisCheckBox drawCreeperLevelCheckbox = new VisCheckBox("Show creeper levels", Base.drawCreeperLevel);
		VisCheckBox drawTerrainEdgesCheckbox = new VisCheckBox("Highlight terrain edges", Base.drawTerrainEdges);
		VisCheckBox drawCorruptionEdgesCheckbox = new VisCheckBox("Highlight corruption edges", Base.drawCorruptionEdges);
		VisCheckBox drawCHeightInequalityCheckbox = new VisCheckBox("Highlight c_height inequal areas", Base.drawCHeightInequality);	
		VisCheckBox drawBorderChunksCheckbox = new VisCheckBox("Highlight border chunks", Base.drawBorderChunks);
		VisCheckBox drawActiveSectionsCheckbox = new VisCheckBox("Highlight active update sections", Base.drawActiveSections);
		VisCheckBox drawBuildingTilesCheckbox = new VisCheckBox("Highlight building tiles", Base.drawBuildingTiles);
		VisCheckBox drawConnectorCollidersCheckbox = new VisCheckBox("Draw connector colliders", Base.drawConnectorColliders);
		
		VisCheckBox drawOresCheckbox = new VisCheckBox("Draw ores", Base.drawOres);
		VisCheckBox errorCheckbox = new VisCheckBox("Test error", false);
		
		engineSettingsTable.add(loggingCheckbox).left().row();
		engineSettingsTable.add(errorCheckbox).left().row();	
		engineSettingsTable.add(drawGeneralStatsCheckbox).left().row();
		engineSettingsTable.add(hoverChunkHighlightCheckbox).left().row();
		engineSettingsTable.add(drawOresCheckbox).left().row();		
		engineSettingsTable.add(drawSectionBordersCheckbox).left().row();
		engineSettingsTable.add(drawCreeperLevelCheckbox).left().row();
		engineSettingsTable.add(drawTerrainEdgesCheckbox).left().row();
		engineSettingsTable.add(drawCorruptionEdgesCheckbox).left().row();
		engineSettingsTable.add(drawCHeightInequalityCheckbox).left().row();
		engineSettingsTable.add(drawBorderChunksCheckbox).left().row();
		engineSettingsTable.add(drawActiveSectionsCheckbox).left().row();
		engineSettingsTable.add(drawBuildingTilesCheckbox).left().row();	
		engineSettingsTable.add(drawConnectorCollidersCheckbox).left().row();	
	
		table.add(engineSettingsButton).fillX().row();
		table.add(engineSettingsCollapsibleWidget).expandX().fillX().row();
		
		VisTextButton gameSettingsButton = new VisTextButton("Game settings");
		VisTable gameSettingsTable = new VisTable(true);
		CollapsibleWidget gameSettingsCollapsibleWidget = new CollapsibleWidget(gameSettingsTable);	
		gameSettingsTable.defaults().left();
		
		gameSettingsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				gameSettingsCollapsibleWidget.setCollapsed(!gameSettingsCollapsibleWidget.isCollapsed());
			}
		});
		
		VisCheckBox infiniteResourcesCheckbox = new VisCheckBox("Infinite resources", Base.infiniteResources);
		
		gameSettingsTable.add(infiniteResourcesCheckbox).left().row();
		
		table.add(gameSettingsButton).fillX().row();
		table.add(gameSettingsCollapsibleWidget).expandX().fillX().row();
		
		VisTextButton graphicsSettingsButton = new VisTextButton("Graphics settings");
		VisTable graphicsSettingsTable = new VisTable(true);
		CollapsibleWidget graphicSettingsCollapsibleWidget = new CollapsibleWidget(graphicsSettingsTable);	
		graphicsSettingsButton.defaults().left();
		
		graphicsSettingsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				graphicSettingsCollapsibleWidget.setCollapsed(!graphicSettingsCollapsibleWidget.isCollapsed());
			}
		});
		
		VisCheckBox vsyncCheckbox = new VisCheckBox("Enable vSync", Base.vSyncEnabled);
		VisCheckBox fullscreenCheckbox = new VisCheckBox("Fullscreen", Base.fullscreen);
		VisCheckBox disableBackgroundCheckbox = new VisCheckBox("Disable background shader", Base.disableBackground);	
		VisCheckBox clipMapCheckbox = new VisCheckBox("Clip map edges", Base.clipMap);
		VisCheckBox disableEdgesCheckbox = new VisCheckBox("Disable edge resolving", Base.disableEdges);
		VisCheckBox enablePostProcessingCheckbox = new VisCheckBox("Enable post-processing", Base.enablePostProcessing);
		VisCheckBox enableBloomCheckbox	 = new VisCheckBox("Bloom", Base.enableBloom);
		

		graphicsSettingsTable.add(vsyncCheckbox).left().row();	
		graphicsSettingsTable.add(fullscreenCheckbox).left().row();	
		graphicsSettingsTable.add(disableBackgroundCheckbox).left().row();	
		graphicsSettingsTable.add(clipMapCheckbox).left().row();	
		graphicsSettingsTable.add(disableEdgesCheckbox).left().row();
		graphicsSettingsTable.add(enablePostProcessingCheckbox).left().row();	
		graphicsSettingsTable.add(enableBloomCheckbox).left().row();	
		
		table.add(graphicsSettingsButton).fillX().row();
		table.add(graphicSettingsCollapsibleWidget).expandX().fillX().row();	
		
		VisTextButton closeButton = new VisTextButton("Close");
		Container c = new Container(closeButton);			
		table.add(c).bottom().row();
		
		add(table);
		
		this.pack();
		
		loggingCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.enableGlProgilerLogging = loggingCheckbox.isChecked();
				if (Base.enableGlProgilerLogging) GameScreen.glProfiler.enable();
				if (!Base.enableGlProgilerLogging) GameScreen.glProfiler.disable();	
		    }
	    });			
		hoverChunkHighlightCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.hoverChunkHighlight = hoverChunkHighlightCheckbox.isChecked();
		    }
	    });		
		drawSectionBordersCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawSectionBorders = drawSectionBordersCheckbox.isChecked();
		    }
	    });
		drawCreeperLevelCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawCreeperLevel = drawCreeperLevelCheckbox.isChecked();
		    }
	    });
		drawTerrainEdgesCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawTerrainEdges = drawTerrainEdgesCheckbox.isChecked();
		    }
	    });		
		drawCorruptionEdgesCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawCorruptionEdges = drawCorruptionEdgesCheckbox.isChecked();
		    }
	    });		
		drawCHeightInequalityCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawCHeightInequality = drawCHeightInequalityCheckbox.isChecked();
		    }
	    });	
		drawBorderChunksCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawBorderChunks = drawBorderChunksCheckbox.isChecked();
		    }
	    });	
		drawActiveSectionsCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawActiveSections = drawActiveSectionsCheckbox.isChecked();
		    }
	    });	
		
		drawBuildingTilesCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawBuildingTiles = drawBuildingTilesCheckbox.isChecked();
		    }
	    });	
		
		drawOresCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawOres = drawOresCheckbox.isChecked();
				GameScreen.chunks.updateAllSectionMeshes(false);
		    }
	    });		
		errorCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.listToString(null);
		    }
	    });		
		drawGeneralStatsCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawGeneralStats = drawGeneralStatsCheckbox.isChecked();
		    }
	    });		
		drawConnectorCollidersCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawConnectorColliders = drawConnectorCollidersCheckbox.isChecked();
		    }
	    });			
		
		infiniteResourcesCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				Base.infiniteResources = infiniteResourcesCheckbox.isChecked();
				if (infiniteResourcesCheckbox.isChecked()) {					
					GameScreen.resourceManager.addBits(99999999);
				} else {
					GameScreen.resourceManager.removeBits(99999999);
				}
		    }
	    });		
		vsyncCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				Base.vSyncEnabled = vsyncCheckbox.isChecked();
				Gdx.graphics.setVSync(vsyncCheckbox.isChecked());
		    }
	    });		
		fullscreenCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				if (fullscreenCheckbox.isChecked()) {
					Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
				} else {
					System.out.println("settings window mode");
					Gdx.graphics.setWindowedMode(Base.defaultWindowSize.width, Base.defaultWindowSize.height);
				}
		    }
	    });					
		disableBackgroundCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				Base.disableBackground = disableBackgroundCheckbox.isChecked();
		    }
	    });		
		clipMapCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				Base.clipMap = clipMapCheckbox.isChecked();
		    }
	    });	
		disableEdgesCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				Base.disableEdges = disableEdgesCheckbox.isChecked();
				GameScreen.chunks.updateAllSectionMeshes(false);
				GameScreen.chunks.updateAllSectionMeshes(true);
		    }
	    });	
		enableBloomCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				Base.enableBloom = enableBloomCheckbox.isChecked();
		    }
	    });	
		enablePostProcessingCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				Base.enablePostProcessing = enablePostProcessingCheckbox.isChecked();
		    }
	    });	
		closeButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 
				Base.settingsOpened = false;
				window.remove();
		    }
	    });	
	}	
}
