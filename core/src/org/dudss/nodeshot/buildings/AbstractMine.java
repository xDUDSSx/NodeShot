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
		
		public int mineRange = 11;
		
		public StorableItem oreType;
		public int productionRate = 800;
		public int nextSimTick = -1;
		
		public boolean canGenerate = false;
		
		public AbstractMine(float cx, float cy) {
			super(cx, cy, width, height);
			//activateIONode(true);
			maxStorage = 1;
		}
		
		@Override
		public void update() {
			super.update();
			if (nextSimTick <= SimulationThread.simTick && storage.size() < this.maxStorage) {
				generate();
				nextSimTick = SimulationThread.simTick + productionRate; 
			}
		}
		
		/**A call to generate the specified {@link StorableItem}.*/
		abstract public void generate();
		
		@Override
		public void build() {
			super.build();
			
			calculateProductionRate();
			
			List<Section> sectionsToUpdate = GameScreen.chunks.getSectionsAroundWorldSpacePoint(this.cx, this.cy);
			for (Section s : sectionsToUpdate) {
				GameScreen.chunks.updateSectionMesh(s, false);
			}
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
		
		@Override
		public boolean canStore(StorableItem p) {
			return false;
		}
}
