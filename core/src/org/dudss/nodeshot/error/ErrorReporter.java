package org.dudss.nodeshot.error;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

/**Swing UI frame that runs on a separate thread and can report runtime exceptions, should be invoked by an {@link ErrorManager}.*/
public class ErrorReporter extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	/**The {@link ErrorManager} that controls this reporter.*/
	private ErrorManager errorManager;
	
	private JTextArea textArea;
	private JLabel messageLabel;
	private JButton btnCloseAll;
	private JPanel panel;
	
	/**frame width*/
	private int width = 800;
	/**frame height*/
	private int height = 500;
	
	/**Create a swing {@link JFrame} and make it display details of the exception.
	 * Invoked by an {@link ErrorManager}.
	 * @param t Exception throwable.
	 * @param message Additional exception description.
	 * @param manager The manager invoking this reporter.
	 * */
	public ErrorReporter(Throwable t, String message, ErrorManager manager) {
		this();
		
		errorManager = manager;
		if(errorManager != null) {
			if (errorManager.getSize() >= 1) {
				panel.add(btnCloseAll, "cell 1 5,alignx right");
			}
		}
		
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
        	
		messageLabel.setText(message);		
		addStacktraceMessage(sw.getBuffer().toString());
	}
	
	/**Create a swing {@link JFrame} and make it display custom exception details.
	 * Invoked by an {@link ErrorManager}.
	 * @param t Exception throwable.
	 * @param message Additional exception description.
	 * @param customStacktrace Custom exception details that will be shown where stacktrace is usually printed.
	 * @param manager The manager invoking this reporter.
	 * */
	public ErrorReporter(Throwable t, String message, String customStacktrace, ErrorManager manager) {
		this();
		
		errorManager = manager;
		if(errorManager != null) {
			if (errorManager.getSize() >= 1) {
				panel.add(btnCloseAll, "cell 1 5,alignx right");
			}
		}    	
		
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
        	
		messageLabel.setText(message);		
		addStacktraceMessage(sw.getBuffer().toString());
		addStacktraceMessage("\n\nCustom details:\n" + customStacktrace);
	}
	
	/**Create a swing {@link JFrame} and make it display details of the exception.
	 * Can be invoked without an {@link ErrorManager}.
	 * @param t Exception throwable.
	 * */
	public ErrorReporter(Throwable t, String message) {
		this();
		
		errorManager = null;
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		
		messageLabel.setText(message);		
		addStacktraceMessage(sw.getBuffer().toString());
	}
	
	/**Create an empty swing {@link JFrame} that can display exception details once configured with {@link #addStacktraceMessage(String)}.*/
	ErrorReporter() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBackground(Color.WHITE);
		setTitle("NodeEngine runtime error reporter");
		setFont(new Font("HelveticaNeue LT 55 Roman", Font.PLAIN, 12));
		setSize(width, height);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width/2 - width/2, screenSize.height/2 - height/2);
		setAutoRequestFocus(true);
		
		panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[grow][grow]", "[][][][][grow][]"));
		
		JLabel errorLabel = new JLabel("An exception occurred.");
		errorLabel.setForeground(Color.WHITE);
		errorLabel.setFont(new Font("HelveticaNeue LT 55 Roman", Font.PLAIN, 14));
		panel.add(errorLabel, "cell 0 0 2 1,alignx left");
		
		JSeparator separator = new JSeparator();
		panel.add(separator, "cell 0 1 2 1,grow");
		
		JLabel lblDetails = new JLabel("Details:");
		lblDetails.setForeground(Color.WHITE);
		lblDetails.setFont(new Font("HelveticaNeue LT 55 Roman", Font.PLAIN, 14));
		panel.add(lblDetails, "cell 0 2");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportBorder(null);
		panel.add(scrollPane, "cell 0 4 2 1,grow");
		
		textArea = new JTextArea();
		textArea.setBorder(null);
		textArea.setForeground(Color.WHITE);
		textArea.setFont(new Font("HelveticaNeue LT 55 Roman", Font.PLAIN, 14));
		textArea.setBackground(Color.DARK_GRAY);
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		messageLabel = new JLabel("");
		messageLabel.setForeground(Color.WHITE);
		messageLabel.setFont(new Font("HelveticaNeue LT 55 Roman", Font.PLAIN, 14));
		panel.add(messageLabel, "cell 0 3 2 1,alignx left,growy");
		
		JButton btnQuit = new JButton("Quit");
		panel.add(btnQuit, "flowx,cell 0 5 2 1,alignx center");
		JButton btnCopy = new JButton("Copy");
		panel.add(btnCopy, "cell 1 2,alignx right");
		
		//If there is more than 1 reporter displayed by the assigned manager, show the close all reporters button
		btnCloseAll = new JButton("Close all error reporters");		
		btnCloseAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Iterator<ErrorReporter> reporterIterator = errorManager.activeErrorReporters.iterator();
				while (reporterIterator.hasNext()) {
					reporterIterator.next().dispose();
					reporterIterator.remove();
				}
			}
		});
		
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (errorManager != null) errorManager.removeReporter(ErrorReporter.this);
				dispose();
			}
		});
		
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = textArea.getText();
				StringSelection stringSelection = new StringSelection(s);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		});
		
		addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	if(errorManager != null) {
        			if (errorManager.getSize() >= 1) {
		            	Iterator<ErrorReporter> reporterIterator = errorManager.activeErrorReporters.iterator();
						while (reporterIterator.hasNext()) {
							reporterIterator.next().dispose();
							reporterIterator.remove();
						}
        			}
        		} else {
        			dispose();
        		}
            }
        });
	}
	
	/**Set the additional exception description.
	 * @message Description.
	 * */
	public void setDescriptionMessage(String message) {
		SwingUtilities.invokeLater(new Runnable() 
	    {
	      public void run()
	      {
	        messageLabel.setText(message);
	      }
	    });
	}
	
	/**Add the stacktrace to textarea contents.
	 * @message Exception stacktrace.
	 * */
	public void addStacktraceMessage(String message) {
		SwingUtilities.invokeLater(new Runnable() 
	    {
	      public void run()
	      {
	        textArea.append(message);
	      }
	    });
	}
}
