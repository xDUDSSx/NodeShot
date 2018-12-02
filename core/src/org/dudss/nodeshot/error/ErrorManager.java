package org.dudss.nodeshot.error;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

/**A manager that handles {@link ErrorReporter} initialisations*/
public class ErrorManager {
	List<ErrorReporter> activeErrorReporters;
	
	/**Maximum of {@link ErrorReporter}s displayed at once*/
	public final int MAX_ACTIVE_REPORTERS = 5;
	
	/**A manager that handles {@link ErrorReporter} initialisations*/
	public ErrorManager() {
		activeErrorReporters = new ArrayList<ErrorReporter>();
	}
	
	/**Displays an {@linkplain ErrorReporter} with details of the exception.
	 * A visual window will not be displayed if there are {@value MAX_ACTIVE_REPORTERS} displayed already to prevent overwhelming the user.
	 * @param t Exception throwable.
	 * @param message Additional exception description.
	 * @see {@link ErrorReporter}*/
	public void report(Throwable t, String message) {
		t.printStackTrace();
		if (activeErrorReporters.size() >= MAX_ACTIVE_REPORTERS) {
			System.err.println("ErrorManager max displayed reporters limit reached(" + MAX_ACTIVE_REPORTERS + ")\nPrinting stacktrace only!.");
			return;
		}
		
		//Invoking the error reporter on the swing EDT thread
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run()
			{						
				ErrorReporter reporter = new ErrorReporter(t, message, ErrorManager.this);
				ErrorManager.this.activeErrorReporters.add(reporter);
				reporter.setVisible(true);
			}
		});
	}
	
	/**Displays an {@linkplain ErrorReporter} with details of the exception.
	 * A visual window will not be displayed if there are {@value MAX_ACTIVE_REPORTERS} displayed already to prevent overwhelming the user.
	 * @param t Exception throwable.
	 * @param message Additional exception description.
	 * @param customDetails Additional details added after the stacktrace.
	 * @see {@link ErrorReporter}*/
	public void reportWithCustomDetails(Throwable t, String message, String customDetails) {
		t.printStackTrace();
		if (activeErrorReporters.size() >= MAX_ACTIVE_REPORTERS) {
			System.err.println("ErrorManager max displayed reporters limit reached(" + MAX_ACTIVE_REPORTERS + ")\nPrinting stacktrace only!.");
			return;
		}
		
		//Invoking the error reporter on the swing EDT thread
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run()
			{						
				ErrorReporter reporter = new ErrorReporter(t, message, customDetails, ErrorManager.this);
				ErrorManager.this.activeErrorReporters.add(reporter);
				reporter.setVisible(true);
			}
		});
	}
	
	/**Removes the {@link ErrorReporter} from this {@linkplain ErrorManager}.
	 * @param reporter {@linkplain ErrorReporter} to remove.
	 * */
	void removeReporter(ErrorReporter reporter) {
		activeErrorReporters.remove(reporter);
	}
	
	/**Returns the number of {@link ErrorReporter}s shown by this manager*/
	public int getSize() {
		return activeErrorReporters.size();
	}
}
