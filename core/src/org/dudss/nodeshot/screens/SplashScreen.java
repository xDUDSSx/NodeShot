package org.dudss.nodeshot.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.SystemColor;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.LineBorder;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.utils.SpriteLoader;
import org.lwjgl.opengl.Display;

import net.miginfocom.swing.MigLayout;

/**An external Swing window running on its own EDT thread. This window displays loading progress of the static {@link SpriteLoader}.
 * Once {@link SpriteLoader} finishes loading the {@link #loaded()} that disposes of this {@link JFrame} after a small delay.
 * Using {@link #isLoaded()} the main OpenGL thread can check if loading is done and proceed with the application startup.*/
public class SplashScreen extends JFrame {
	private static final long serialVersionUID = 1L;
	
    private JProgressBar progressBar = new JProgressBar();
    private JLabel infoLabel;
    private boolean loaded = false;

    public SplashScreen() {
    	super();
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	setUndecorated(true);

    	JPanel content = new JPanel();
    	content.setBackground(SystemColor.windowBorder);
    	getContentPane().add(content);
    	
    	content.setLayout(new MigLayout("", "[]", "[][][][]"));

        JPanel panel = new JPanel();
        panel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
        panel.setBackground(Color.LIGHT_GRAY);
        content.add(panel, "cell 0 0,alignx center,aligny center");
        panel.setLayout(new MigLayout("", "[]", "[]"));
        
        JLabel lblPlaceholder_1 = new JLabel("NodeEngine " + BaseClass.ver);
        lblPlaceholder_1.setBackground(Color.LIGHT_GRAY);
        lblPlaceholder_1.setFont(new Font("Bebas Kai", Font.PLAIN, 58));
        panel.add(lblPlaceholder_1, "cell 0 0");
        progressBar.setStringPainted(true);
        content.add(progressBar, "cell 0 2 1 2,grow");
        
        infoLabel = new JLabel("placeholder");
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        content.add(infoLabel, "cell 0 1,alignx left,growy");

        pack();
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(true);
    }

    /**Updates the splash screen data and ends loading if finished.
     * This method has to be called after {@link SpriteLoader#loaded} is set to true!*/
    public void updateProgress() {  	
    	if (SpriteLoader.loaded == true) {
    		loaded();
    	} else {
    		progressBar.setValue((int)SpriteLoader.getProgress());
        	infoLabel.setText(SpriteLoader.getMessage());
    	}
    }
    
    private void loaded() {
    	infoLabel.setText("Done loading!");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			BaseClass.errorManager.report(e, "SplashScreen interrupted!");
		}
		loaded = true;
    	dispose();
    }
    
    /**Check if the loading process is finished*/
    public boolean isLoaded() {
    	return loaded;
    }
};
