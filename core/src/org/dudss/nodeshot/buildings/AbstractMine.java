package org.dudss.nodeshot.buildings;

import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.Chunks.OreType;
import org.dudss.nodeshot.terrain.Section;

/**Building that generates items at a rate.*/
public abstract class AbstractMine extends AbstractStorage {

		static float width = Base.CHUNK_SIZE*3, height = Base.CHUNK_SIZE*3;
		
		public int mineRange = 7;
		
		public StorableItem oreType;
		public int productionRate = 400;
		public int nextSimTick = -1;
		
		public boolean canGenerate = false;
		
		public AbstractMine(float cx, float cy) {
			super(cx, cy, width, height);
			activateIONode(true);
		}
		
		@Override
		public void update() {
			if (nextSimTick <= SimulationThread.simTick) {
				nextSimTick = SimulationThread.simTick + productionRate; 
				generate();
			}
		}

		abstract public void generate();
		
		@Override
		public void build() {
			super.build();
			
			calculateProductionRate();
			
			List<Section> sectionsToUpdate = GameScreen.chunks.getSectionsAroundWorldSpacePoint(this.cx, this.cy);
			for (Section s : sectionsToUpdate) {
				GameScreen.chunks.updateSectionMesh(s, false);
			}
			
			updateFogOfWar(true);
		}
		
		private void calculateProductionRate() {
			float totalOreLevel = 0;
			
			int nOfCoalChunks = 0;
			int nOfIronChunks = 0;
			
			List<Chunk> surroundingChunks = GameScreen.chunks.getChunksAroundWorldSpacePoint(cx, cy, mineRange);
			
			for (Chunk c : surroundingChunks) {
				totalOreLevel += c.getOreLevel();
				switch(c.getOreType()) {
					case COAL: nOfCoalChunks++; break;
					case IRON: nOfIronChunks++; break;
					default:
						break;
				}
			}
			
			if (totalOreLevel > 0) {
				if (nOfCoalChunks > nOfIronChunks) {
					this.oreType = new StorableItem(ItemType.COAL);
				} else {
					this.oreType = new StorableItem(ItemType.IRON);
				}
				canGenerate = true;
				productionRate = Math.round((productionRate / totalOreLevel));
				System.out.println("production rate: " + productionRate);
			}
			
			for (Chunk c : surroundingChunks) {
				if (oreType != null) {
					switch(oreType.getType()) {
						case COAL: if (c.getOreLevel() > 0 && c.getOreType() == OreType.COAL) {c.setOreOutlined(true);} break;
						case IRON: if (c.getOreLevel() > 0 && c.getOreType() == OreType.IRON) {c.setOreOutlined(true);} break;
						default: break;
					}
				}
			}
			
			nextSimTick = SimulationThread.simTick + productionRate;			
		}
}
