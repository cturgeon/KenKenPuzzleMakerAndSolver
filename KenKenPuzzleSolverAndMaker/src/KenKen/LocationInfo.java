package KenKen;

import java.util.ArrayList;

/**
 *
 * Class used to keep track of all valuable information for each location in the
 * grid of KenKen Puzzle
 * 
 * @author cturgeon
 */
public class LocationInfo {

	private int row;
	private int col;
	private int myValue;
	private ArrayList<Integer> values;

	private CageInfo cage;

	public LocationInfo(int row, int col) {
		this.row = row;
		this.col = col;
		this.myValue = -1;
		this.values = new ArrayList<Integer>();
	}

	public void setCage(CageInfo cage) {
		this.cage = cage;
	}

	public CageInfo getCage() {
		return cage;
	}

	public int getLocationValue() {
		return myValue;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public char getLetter() {
		return cage.getGroupLetter();
	}

	public void setValue(int value) {
		this.myValue = value;
	}

	public void setPossibleValues(ArrayList<Integer> values) {
		for (Integer i : values) {
			addPossibleValue(i);
		}
	}

	public ArrayList<Integer> getPossibleValues() {
		return values;
	}

	public void removePossibleValue(Integer number) {
		if (values.contains(number)) {
			values.remove(number);
		}
	}
	
	public void addPossibleValue(Integer number) {
		if (!values.contains(number)) {
			values.add(number);
		}
	}
}