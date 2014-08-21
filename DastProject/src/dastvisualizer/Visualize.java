package dastvisualizer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class Visualize extends JFrame{
	
	/**
	 * 
	 */
	
	Container contentPane;
	
	private static final long serialVersionUID = -944159544965143986L;
	
	final int CellWidth = 100;
	final int CellLength = 20;
	
	private Draw panel;
	private JScrollPane mainPanel;

	Visualize(List<ObjectInfo> cell){
		this.setSize(2000, 700);
		this.setTitle("ver14");
		this.setBackground(Color.WHITE);
		this.setLocationRelativeTo(null);
		
		//this.setLayout(null);
		
		panel = new Draw(cell);
		JScrollPane scrollpane = new JScrollPane(panel);
		
		contentPane = getContentPane();
		contentPane.add(scrollpane);
		
		scrollpane.setVisible(true);
		mainPanel = scrollpane;
		this.setVisible(true);
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				contentPane.invalidate();
				contentPane.validate();

			}
		});
		
	}
	
	public void reDraw(List<ObjectInfo> cell){
		panel = new Draw(cell);
		JScrollPane scrollpane = new JScrollPane(panel);
		
		contentPane = getContentPane();
		contentPane.add(scrollpane);
		
		scrollpane.setVisible(true);
		mainPanel.setVisible(false);
		
		mainPanel = scrollpane;
		this.setVisible(true);
		
	}
	

}
