package org.dudss.nodeshot.buildings;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.items.StorableItem;

/**An extended version of an {@link AbstractStorage} that can hold items in 2 separate item pools. And needs both pools free to accept further items.*/
public abstract class AbstractIOStorage extends AbstractStorage {
	
	List<StorableItem> processedStorage = new ArrayList<StorableItem>();
	int maxProcessedStorage = 5;
	
	public AbstractIOStorage(float cx, float cy, float width, float height) {
		super(cx, cy, width, height);
	}

	@Override
	public void update() {
		if (storage.size() < maxStorage && processedStorage.size() < maxProcessedStorage) {
			input.update();
		}
	}
	
	@Override
	public boolean canStore(StorableItem p) {
		if (this.accepted.size() > 0) {
			if (this.accepted.contains(p.getType()) && storage.size() < maxStorage && processedStorage.size() < maxProcessedStorage) {
				return true;
			}
			return false;
		}
		return true;
	}
}
