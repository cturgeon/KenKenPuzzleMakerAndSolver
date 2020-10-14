package KenKen;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
public class KenKenView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<JTextField> grid;
	
	private JButton gridButton = new JButton("start");
	
	public KenKenView(int size) {
		
		JPanel kenKenGrid = new JPanel();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600,600);
		
		grid = new ArrayList<JTextField>();
		
		kenKenGrid.add(gridButton);
		
		for (int i = 0; i < size; i++) {
			grid.add(new JTextField(10));
			kenKenGrid.add(grid.get(i));
		}
		
		this.add(kenKenGrid);
		
	}
	
	public int getGridLocationValue(int currentLocation) {
		return Integer.parseInt(grid.get(currentLocation).getText());
	}
	
	public void addGridListener(ActionListener listenForGridButton) {
		
	}

}
