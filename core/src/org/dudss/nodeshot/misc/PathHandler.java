package org.dudss.nodeshot.misc;

/**A handler assigned to each movable {@link Package} that handles actions after its assigned package has finished its current connector line segment.
 * Most importantly this handler represents the path-finding logic of the {@linkplain Package}.*/
public interface PathHandler {
	
	/**Type of the path handler
	 * Note: DefinitePathHandler (eg. handler with path-finding) is currently unused and undocumented.
	 */
	enum PathHandlerType {
		DefinitePathHandler, IndefinitePathHandler
	}
	
	/**Starts the path handler.*/
	void start();
	
	/**Notify the handler that a {@link Package} has finished and it needs further directions
	 * @return Whether the action was successful, eg. if the package has a connector where to go.*/
	boolean nextNode();
	
	/**Deactivates this path handler.*/
	void finish();
	
	/**@return Whether this path handler is inactive.*/
	boolean isDone();
	PathHandlerType getType();
}
