package org.dudss.nodeshot.misc;

public interface PathHandler {
	
	enum PathHandlerType {
		DefinitePathHandler, IndefinitePathHandler
	}
	
	void start();
	void update();
	void finish();
	boolean isDone();
	PathHandlerType getType();
}
