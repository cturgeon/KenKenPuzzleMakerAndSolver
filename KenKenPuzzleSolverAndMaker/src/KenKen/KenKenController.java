package KenKen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class KenKenController {
	
	private KenKenPuzzleMaker theModel;
	private KenKenView theView;
	
	public KenKenController(KenKenPuzzleMaker theModel, KenKenView theView) {
		
		KenKenPuzzleMaker newGrid = new KenKenPuzzleMaker(4);
		this.theModel = newGrid;
		this.theView = theView;
		
		this.theView.addGridListener(new GridListenerButton());
	}
	
	class GridListenerButton implements ActionListener {
		
		ArrayList<LocationInfo> grid = new ArrayList<LocationInfo>();

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				
				grid = theModel.getGrid();
				
				for (int i = 0; i < grid.size(); i++) {
					
				}
				
			} catch (Exception err) {
				System.out.println(err);
			}
			
		}
		
	}
}
