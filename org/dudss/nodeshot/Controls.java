/*
package org.dudss.nodeshot;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.dudss.nodeshot.algorithms.GraphComponentsAlgorithm;
import org.dudss.nodeshot.entities.*;
import org.dudss.nodeshot.entities.Package;

import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import net.miginfocom.swing.MigLayout;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Color;


public class Controls {

	//Components
	private final JPanel contentPanel = new JPanel();
	JDialog dialog;
	private JButton btnShowNodeInfo;
	private JButton btnNewButton;
	private JButton btnToggleSelectiveInfo;
	private JButton btnToggleConnectionDistance;
	private JButton btnToggleBrush;
	private JButton btnGetNumberOf;
	private JButton btnDistanceBetween;
	private JButton btnGeneratePackages;
	private JButton btnClearPackages;
	private JButton btnGetStepsFrom;
	private JSeparator separator;
	private JTextField txtFrom;
	private JTextField txtTo;
	private JLabel lblF;
	private JLabel lblT;
	private JSeparator separator_1;
	private JTextField txtGofrom;
	private JTextField txtGoto;
	private JLabel lblF_1;
	private JLabel lblT_1;
	private JButton btnSendPackage;
	private JTextField txtDistancefrom;
	private JTextField txtDistanceto;
	private JLabel lblA;
	private JLabel lblB;
	private JSeparator separator_2;
	private JTextField txtPaintspacing;
	private JLabel lblSpacing;
	private JButton btnSet;
	private JButton btnNewButton_1;
	private JButton btnToggleConnectMode;
	
	//Vars
	public static int WIDTH;
	public static int HEIGHT;
	private JButton btnPingPackagehandler;
	private JButton btnBenchmark;
	private JButton btnKeepSendingPackages;
	private JButton btnSendOpposingPkgs;
	private JTextField txtClose;
	private JButton btnCloseNode;
	
	public Controls(JFrame frame) {
		dialog = new JDialog(frame, "NodeShot controls");
		dialog.setBounds(100, 100, 204, 250);
		dialog.setModal(false);
		dialog.getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		dialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JButton btnClearNodes = new JButton("Clear nodes");
		btnClearNodes.setBackground(Color.RED);
		btnClearNodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (Node n : BaseClass.nodelist) {
					n.remove();
				}
			}
		});
		contentPanel.setLayout(new MigLayout("", "[165px,grow]", "[23px][][23px][23px][23px][23px][][][23px][][23px][][][][][][][][][][][][][][]"));
		contentPanel.add(btnClearNodes, "cell 0 0,growx,aligny top");
		
		btnClearPackages = new JButton("Clear packages");
		btnClearPackages.setBackground(Color.RED);
		btnClearPackages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BaseClass.packagelist.clear();
				BaseClass.packageHandler.clear();
			}
		});
		contentPanel.add(btnClearPackages, "cell 0 1,growx");
		
		btnToggleSelectiveInfo = new JButton("Toggle selective info");
		btnToggleSelectiveInfo.setBackground(Color.YELLOW);
		btnToggleSelectiveInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BaseClass.NodeSelectiveInfo = !BaseClass.NodeSelectiveInfo;
			}
		});
		
		btnNewButton = new JButton("Toggle gameloop");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Base.noupdate = !Base.noupdate;
			}
		});
		contentPanel.add(btnNewButton, "cell 0 2,growx,aligny top");
		contentPanel.add(btnToggleSelectiveInfo, "cell 0 4,growx,aligny top");
		
		btnToggleConnectionDistance = new JButton("Toggle connection distance");
		btnToggleConnectionDistance.setBackground(Color.YELLOW);
		btnToggleConnectionDistance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BaseClass.NodeConnectRadiusHidden = !BaseClass.NodeConnectRadiusHidden;
			}
		});
		contentPanel.add(btnToggleConnectionDistance, "cell 0 5,growx,aligny top");
		
		btnToggleBrush = new JButton("Toggle brush");
		btnToggleBrush.setBackground(Color.ORANGE);
		btnToggleBrush.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Base.toggleBrush = !Base.toggleBrush;
			}
		});
		
		separator_2 = new JSeparator();
		contentPanel.add(separator_2, "cell 0 6");
		
		lblSpacing = new JLabel("spacing:");
		contentPanel.add(lblSpacing, "flowx,cell 0 7");
		
		txtPaintspacing = new JTextField();
		contentPanel.add(txtPaintspacing, "cell 0 7,growx");
		txtPaintspacing.setColumns(10);
		contentPanel.add(btnToggleBrush, "cell 0 8,growx,aligny top");
		
		btnGetNumberOf = new JButton("Get number of graphs (disabled)");
	/*	btnGetNumberOf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GraphNumbering gN = new GraphNumbering();
				gN.getNumberOfGraphs();
			}
		});

		btnToggleConnectMode = new JButton("Toggle connect mode");
		btnToggleConnectMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BaseClass.toggleConnectMode = !BaseClass.toggleConnectMode;
			}
		});
		btnToggleConnectMode.setBackground(Color.ORANGE);
		contentPanel.add(btnToggleConnectMode, "cell 0 9,growx");
		contentPanel.add(btnGetNumberOf, "cell 0 10,growx,aligny top");
		
		btnGetStepsFrom = new JButton("Get steps from to");
		btnGetStepsFrom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BaseClass.nodelist.get(Integer.valueOf(txtFrom.getText())).getStepsTo(BaseClass.nodelist.get(Integer.valueOf(txtTo.getText())));
			}
		});
		
		btnNewButton_1 = new JButton("gCA");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GraphComponentsAlgorithm gCA = new GraphComponentsAlgorithm();
				gCA.getWebNodesList();
				System.out.println("Number of webs: " + gCA.getNumberOfWebs());
			}
		});
		
		btnPingPackagehandler = new JButton("Ping PackageHandler");
		btnPingPackagehandler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BaseClass.packageHandler.addPath(BaseClass.nodelist.get(0),BaseClass.nodelist.get(3));
			}
		});
		contentPanel.add(btnPingPackagehandler, "cell 0 12,growx");
		
		btnBenchmark = new JButton("Benchmark");
		btnBenchmark.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				org.dudss.nodeshot.utils.Benchmark benchmark = new org.dudss.nodeshot.utils.Benchmark();
				benchmark.start(1000, 20, 500);
			}
		});
		btnBenchmark.setBackground(new Color(204, 102, 0));
		contentPanel.add(btnBenchmark, "cell 0 13,growx");
		contentPanel.add(btnNewButton_1, "cell 0 15,growx");
		
		lblA = new JLabel("a:");
		contentPanel.add(lblA, "flowx,cell 0 16");
		
		txtDistancefrom = new JTextField();
		contentPanel.add(txtDistancefrom, "cell 0 16,growx");
		txtDistancefrom.setColumns(10);
		
		separator = new JSeparator();
		contentPanel.add(separator, "cell 0 18");
		
		lblF = new JLabel("f:");
		contentPanel.add(lblF, "flowx,cell 0 19");
		
		txtFrom = new JTextField();
		contentPanel.add(txtFrom, "cell 0 19,growx");
		txtFrom.setColumns(10);
		contentPanel.add(btnGetStepsFrom, "cell 0 20,growx");
		
		lblT = new JLabel("t:");
		contentPanel.add(lblT, "cell 0 19");
		
		txtTo = new JTextField();
		contentPanel.add(txtTo, "cell 0 19");
		txtTo.setColumns(10);
		
		separator_1 = new JSeparator();
		contentPanel.add(separator_1, "cell 0 21");
		
		lblF_1 = new JLabel("f:");
		contentPanel.add(lblF_1, "flowx,cell 0 22");
		
		txtGofrom = new JTextField();
		contentPanel.add(txtGofrom, "cell 0 22,growx");
		txtGofrom.setColumns(10);
		
		lblT_1 = new JLabel("t:");
		contentPanel.add(lblT_1, "cell 0 22");
		
		txtGoto = new JTextField();
		contentPanel.add(txtGoto, "cell 0 22,growx");
		txtGoto.setColumns(10);
		
		btnSendPackage = new JButton("Send package from to");
		btnSendPackage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				com.badlogic.gdx.graphics.Color color = new com.badlogic.gdx.graphics.Color((Base.getRandomIntNumberInRange(0, 255) / 255f),(Base.getRandomFloatNumberInRange(0, 255) / 255f),(Base.getRandomFloatNumberInRange(0, 255) / 255f), 1.0f);
				BaseClass.nodelist.get(Integer.valueOf(txtGofrom.getText())).sendPackage(BaseClass.nodelist.get(Integer.valueOf(txtGoto.getText())), color);
			}
		});
		contentPanel.add(btnSendPackage, "flowy,cell 0 23,growx");
		
		btnDistanceBetween = new JButton("Distance between a, b");
		btnDistanceBetween.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(BaseClass.nodelist.get(Integer.valueOf(txtDistancefrom.getText())).getDistance(BaseClass.nodelist.get(Integer.valueOf(txtDistanceto.getText()))));
			}
		});
		contentPanel.add(btnDistanceBetween, "cell 0 17,growx,aligny top");
		
		btnGeneratePackages = new JButton("Generate packages randomely");
		btnGeneratePackages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Timer timer;
				timer = new Timer();
		        timer.schedule(new GeneratePackagesTask(), 0, 700);
			}
			
			class GeneratePackagesTask extends TimerTask {
				
				public void run() {
					for(int i = 0; i < BaseClass.nodelist.size(); i++) {						
						Package packAge = new Package(BaseClass.nodelist.get(i), getTargetNodeOtherThanIndex(i));
						packAge.go();
					}
				}
				Node getTargetNodeOtherThanIndex(int i) {
					int result = Base.getRandomIntNumberInRange(1, BaseClass.nodelist.size()) - 1;
					
					if (result == i) {
						result = Base.getRandomIntNumberInRange(1, BaseClass.nodelist.size()) - 1;
					}
					
					return BaseClass.nodelist.get(result);
				}
			}
		});
		contentPanel.add(btnGeneratePackages, "cell 0 11,growx,aligny top");
		
		lblB = new JLabel("b:");
		contentPanel.add(lblB, "cell 0 16");
		
		txtDistanceto = new JTextField();
		contentPanel.add(txtDistanceto, "cell 0 16,growx");
		txtDistanceto.setColumns(10);
		
		btnSet = new JButton("Set");
		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Base.paint_spacing = Integer.valueOf(txtPaintspacing.getText());
			}
		});
		contentPanel.add(btnSet, "cell 0 7");
		
		btnShowNodeInfo = new JButton("Hide node info");
		btnShowNodeInfo.setBackground(Color.YELLOW);
		btnShowNodeInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(BaseClass.NodeInfoHidden == false) {
					BaseClass.NodeInfoHidden = true;
				} else {
					BaseClass.NodeInfoHidden = false;
				}
			}
		});
		contentPanel.add(btnShowNodeInfo, "cell 0 3,growx,aligny top");
		
		btnKeepSendingPackages = new JButton("Keep sending packages from to");
		btnKeepSendingPackages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				com.badlogic.gdx.graphics.Color color = new com.badlogic.gdx.graphics.Color((Base.getRandomIntNumberInRange(0, 255) / 255f),(Base.getRandomFloatNumberInRange(0, 255) / 255f),(Base.getRandomFloatNumberInRange(0, 255) / 255f), 1.0f);
				System.out.println("Color created r: " + color.r) ;
				
				class generatePackages implements Runnable {
					int loops = 0;
					
					int from = Integer.valueOf(txtGofrom.getText());
					int to = Integer.valueOf(txtGoto.getText());
								
					@Override
					public void run() {
						if (loops >= 100) {
							return;
						}
						
						Boolean isClear = true;
						for (NodeConnector nC : BaseClass.nodeConnectorHandler.getAllConnectorsToNode(BaseClass.nodelist.get(from))) {
							Boolean clear = nC.checkEntrance(BaseClass.nodelist.get(from), Base.PACKAGE_BLOCK_RANGE, Base.PACKAGE_SPEED); 
							if (clear == false) {
								isClear = false;
							}
						}
						
						if (isClear) {
							BaseClass.nodelist.get(from).sendPackage(BaseClass.nodelist.get(to), color);
						}
						loops++;
					}
					
					int getLoops() {
						return loops;
					}
				}
				
				generatePackages gP = new generatePackages();
				
				ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			   	service.scheduleAtFixedRate(gP, 0, 500, TimeUnit.MILLISECONDS);
			   	
			   	if (gP.getLoops() >= 100) {
			   		service.shutdown();
			   	}
			}
		});
		
		contentPanel.add(btnKeepSendingPackages, "cell 0 23,growx");
		
		btnSendOpposingPkgs = new JButton("Send opposing pkgs 0 1");
		btnSendOpposingPkgs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(System.currentTimeMillis());
				BaseClass.nodelist.get(0).sendPackage(BaseClass.nodelist.get(1));
				BaseClass.nodelist.get(1).sendPackage(BaseClass.nodelist.get(0));
				System.out.println(System.currentTimeMillis());
			}
		});
		contentPanel.add(btnSendOpposingPkgs, "cell 0 23,growx");
		
		txtClose = new JTextField();
		contentPanel.add(txtClose, "flowy,cell 0 24,growx");
		txtClose.setColumns(10);
		
		btnCloseNode = new JButton("Close node");
		btnCloseNode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Boolean isClosed = BaseClass.nodelist.get(Integer.valueOf(txtClose.getText())).isClosed();
				BaseClass.nodelist.get(Integer.valueOf(txtClose.getText())).setClosed(!isClosed);
			}
		});
		contentPanel.add(btnCloseNode, "cell 0 24,growx");
		
		WIDTH = dialog.getWidth();
		HEIGHT = dialog.getHeight();
		
		ResizeListener resizeadapter = new ResizeListener();
		
		dialog.addComponentListener(resizeadapter);
		
		dialog.pack();
		dialog.setVisible(true);			
	}
	
	class ResizeListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			WIDTH = dialog.getWidth();
			HEIGHT = dialog.getHeight();
	    }
	}
}
*/