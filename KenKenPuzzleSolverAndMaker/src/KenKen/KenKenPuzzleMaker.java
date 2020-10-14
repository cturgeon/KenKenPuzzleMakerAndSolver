package KenKen;
/**
 * maybe add constraints for size <=9
 * 
 * 
 * @author cturgeon
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

public class KenKenPuzzleMaker extends JPanel implements KeyListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private int size;
	private ArrayList<LocationInfo> grid;
	private ArrayList<CageInfo> cages;
	private char currentChar;

	public KenKenPuzzleMaker(int size) {
		this.size = size;
		grid = new ArrayList<LocationInfo>();
		makePuzzle();
	}

	public void makePuzzle() {
		cages = new ArrayList<CageInfo>();
		for (char i = 'A'; i < 'A' + size; i++) {
			CageInfo cage = new CageInfo(i);
			cages.add(cage);
		}
		for (int row = 0; row < size; row++) {
			CageInfo currentCage = cages.get(row);
			for (int col = 0; col < size; col++) {
				LocationInfo location = new LocationInfo(row, col);
				grid.add(location);
				location.setCage(currentCage);
			}
		}
		assignValues();
		unassignCageLetters();
		assignCages();
	}
	
	/**
	 * returns current state of game grid
	 * @return
	 */
	public ArrayList<LocationInfo> getGrid() {
		return grid;
	}

	/**
	 * finds first in sequential grid order with -1 value
	 * 
	 * @return
	 */
	public LocationInfo getNextLocation() {
		LocationInfo info = null;
		for (LocationInfo entry : grid) {
			if (entry.getLocationValue() == -1) {
				info = entry;
				break;
			}
		}
		return info;
	}

	/**
	 * go through each row and assign all values in each column up to size make set
	 * of available numbers pick random number from set assign value iterate through
	 * each column location else backtrack
	 * 
	 * @param row
	 */
	public boolean assignValues() {
		LocationInfo location = getNextLocation();
		if (location != null) {
			ArrayList<Integer> randomNumbers = randomNumbersForLocation(location);
			for (Integer i : randomNumbers) {
				location.setValue(i);
				if (assignValues()) {
					return true;
				}
			}
			location.setValue(-1);
			return false;
		}
		return true;
	}

	/**
	 * makes set of random numbers to choose from in each location. values bound by
	 * row and column
	 * 
	 * @param location
	 * @return
	 */
	public ArrayList<Integer> randomNumbersForLocation(LocationInfo location) {
		ArrayList<Integer> randomNumberSet = new ArrayList<Integer>();
		for (int i = 1; i <= size; i++) {
			if (check(location, i)) {
				randomNumberSet.add(i);
			}
		}
		Collections.shuffle(randomNumberSet);
		return randomNumberSet;

	}

	/**
	 * Checks if location can have number in row and column
	 * 
	 * @param location
	 * @param number
	 */
	public boolean check(LocationInfo location, int number) {
		int row = location.getRow();
		int col = location.getCol();
		for (int i = 0; i < size; i++) { // checks same row of location
			if (grid.get(row * size + i).getLocationValue() == number) {
				return false;
			}
		}
		for (int i = 0; i < size; i++) { // checks same column of location
			if (grid.get(i * size + col).getLocationValue() == number) {
				return false;
			}
		}
		return true;
	}
	
	public void printKenKenPuzzle() {
		System.out.println(size);
		printGridState();
		printCageInfo();
	}

	/**
	 * Prints the KenKenPuzzle Grid
	 */
	public void printGridState() {
		for (int i = 1; i <= grid.size(); i++) {
			if (grid.get(i - 1).getCage() == null) {
				System.out.print(". "); // cages are empty
				if (i % size == 0) {
					System.out.println();
				}
			} else {
				System.out.print(grid.get(i - 1).getCage().getGroupLetter());
				if (i % size == 0) {
					System.out.println();
				}
			}
		}

		// debugging for values
//		for (int i = 1; i <= grid.size(); i++) {
//			System.out.print(grid.get(i - 1).getLocationValue() + " ");
//			if (i % size == 0) {
//				System.out.println();
//			}
//		}
//		System.out.println();
	}

	/**
	 * used after the initial grid is created
	 */
	public void unassignCageLetters() {
		cages = new ArrayList<CageInfo>();
		for (LocationInfo entry : grid) {
			entry.setCage(null);
		}
	}

	public char nextLetter() {
		if (currentChar == 'Z') {
			currentChar = 96;
		}
		return currentChar++;
	}

	public void assignCages() {
		currentChar = 65;
		assignCageLetters();
		assignCageOperators();
		assignCageValues();
	}

	public LocationInfo getNextNullCage() {
		LocationInfo location = null;
		for (LocationInfo info : grid) {
			if (info.getCage() == null) {
				location = info;
				break;
			}
		}
		return location;
	}

	public boolean assignCageLetters() {
		// for each -1 value
		// assign next letter
		// if (random > some learning value) decrease value as more cages added
		// assign adjacent location with -1 value to cage

		LocationInfo location = getNextNullCage();

		while (location != null) {
			CageInfo cage = new CageInfo(currentChar);
			nextLetter();
			location.setCage(cage);
			location.getCage().addGroupMembers(location);

			double value = Math.random();
			double addValue = 0.05; // chance to add another location to current cage
			while (value > addValue) {
				int row = location.getRow();
				int col = location.getCol();

				if (col == size - 1 && row != size - 1) { // left edge of grid but not at bottom edge
					LocationInfo belowLocation = grid.get((row + 1) * size + col);
					if (belowLocation.getCage() == null) {
						location = belowLocation;
						location.setCage(cage);
						location.getCage().addGroupMembers(location);
					}
				} else if (row == size - 1 && col != size - 1) { // bottom edge of grid but not at left edge
					LocationInfo leftLocation = grid.get(row * size + (col + 1));
					if (leftLocation.getCage() == null) {
						location = leftLocation;
						location.setCage(cage);
						location.getCage().addGroupMembers(location);
					}
				} else { // somewhere in the middle of grid expanding the left of down possible
					double colOrRowDouble = Math.random();
					if (colOrRowDouble > 0.60) { // add left
						if (col != size - 1) { // bottom edge of grid but not at left edge
							LocationInfo leftLocation = grid.get(row * size + (col + 1));
							if (leftLocation.getCage() == null) {
								location = leftLocation;
								location.setCage(cage);
								location.getCage().addGroupMembers(location);
							}
						}
					} else { // add below
						if (row != size - 1) { // left edge of grid but not at bottom edge
							LocationInfo belowLocation = grid.get((row + 1) * size + col);
							if (belowLocation.getCage() == null) {
								location = belowLocation;
								location.setCage(cage);
								location.getCage().addGroupMembers(location);
							}
						}
					}
					
				}
//				value -= .4; // change chance for another location to be added
				addValue += .5;
			}
			cages.add(cage);

			if (assignCageLetters()) {
				return true;
			}
			return false;

		}
		return true;
	}

	public void assignCageOperators() {
		for (CageInfo cage : cages) {
			if (cage.getGroupSize() == 1) {
				cage.setGroupOp(' ');
			} else if (cage.getGroupSize() == 2) {
				double chooseBoundOrUnbound = Math.random(); // sub or divide are bounded to 2, those take precedence
				if (chooseBoundOrUnbound >= 0.35) { // sub or divide more often
					double subOrDiv = Math.random();
					if (subOrDiv > 0.25) { // divide first and make sure divides
						int locationOneValue = cage.getGroupMembers().get(0).getLocationValue();
						int locationTwoValue = cage.getGroupMembers().get(1).getLocationValue();
						if (locationOneValue % locationTwoValue == 0 || locationTwoValue % locationOneValue == 0) {
							cage.setGroupOp('/');
						} else {
							cage.setGroupOp('-');
						}
					} else {
						cage.setGroupOp('-');
					}
				} else { // multiply or add
					double mulOrAdd = Math.random();
					if (mulOrAdd >= 0.5) { // multiply first
						cage.setGroupOp('*');
					} else {
						cage.setGroupOp('+');
					}
				}
			} else { // multiply or add in 3 or more size cages
				double mulOrAdd = Math.random();
				if (mulOrAdd >= 0.5) { // multiply first
					cage.setGroupOp('*');
				} else {
					cage.setGroupOp('+');
				}
			}
		}
	}

	public void assignCageValues() {
		for (CageInfo cage : cages) {
			int sum = 0;
			if (cage.getGroupOp() == Operator.NONE) {
				sum = cage.getGroupMembers().get(0).getLocationValue();
			} else if (cage.getGroupOp() == Operator.ADD) {
				for (LocationInfo entry : cage.getGroupMembers()) {
					sum += entry.getLocationValue();
				}
			} else if (cage.getGroupOp() == Operator.SUB) {
				sum = Math.abs(cage.getGroupMembers().get(0).getLocationValue()
						- cage.getGroupMembers().get(1).getLocationValue());
			} else if (cage.getGroupOp() == Operator.MUL) {
				sum = 1;
				for (LocationInfo entry : cage.getGroupMembers()) {
					sum *= entry.getLocationValue();
				}
			} else if (cage.getGroupOp() == Operator.DIV) {
				int locationOneValue = cage.getGroupMembers().get(0).getLocationValue();
				int locationTwoValue = cage.getGroupMembers().get(1).getLocationValue();
				if (locationOneValue > locationTwoValue) {
					sum = locationOneValue / locationTwoValue;
				} else {
					sum = locationTwoValue / locationOneValue;
				}
			}
			cage.setGroupValue(sum);
		}
	}

	/**
	 * prints in form: A:11+
	 */
	public void printCageInfo() {
		for (CageInfo cage : cages) {
			Operator groupOp = cage.getGroupOp();
			char groupOpChar;
			if (groupOp == Operator.ADD) {
				groupOpChar = '+';
			} else if (groupOp == Operator.SUB) {
				groupOpChar = '-';
			} else if (groupOp == Operator.MUL) {
				groupOpChar = '*';
			} else if (groupOp == Operator.DIV) {
				groupOpChar = '/';
			} else {
				groupOpChar = ' ';
			}
			System.out.println(cage.getGroupLetter() + ":" + cage.getGroupValue() + groupOpChar);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
