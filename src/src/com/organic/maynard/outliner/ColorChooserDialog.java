import java.awt.*;
import java.awt.event.*;

import java.util.*;
import javax.swing.*;

public class ColorChooserDialog extends JDialog {
	
	public static final JColorChooser colorChooser = new JColorChooser();
	public Color color = null;
	
	// The Constructors
	public ColorChooserDialog(JFrame frame, String title) {
		super(frame,title,true);

		getContentPane().add(colorChooser, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(new JButton("OK"));
		bottomPanel.add(new JButton("Cancel"));
		
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		pack();
	}
	
	public void display(Color color) {
		this.color = color;
		colorChooser.setColor(color);
		show();
	}
}