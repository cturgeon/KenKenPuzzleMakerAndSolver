package KenKen;

import java.util.ArrayList;

/**
 * Contains all info for each cage on the KenKen puzzle grid
 * 
 * @author cturgeon
 *
 */
public class CageInfo {

	private char groupLetter;
	private int groupValue;
	private Operator groupOp;
	private ArrayList<LocationInfo> groupMembers;

	public CageInfo(char groupLetter) {
		this.groupLetter = groupLetter;
		this.groupMembers = new ArrayList<LocationInfo>();
	}

	public void addGroupMembers(LocationInfo location) {
		this.groupMembers.add(location);
	}

	public ArrayList<LocationInfo> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupValue(int groupValue) {
		this.groupValue = groupValue;
	}

	public void setGroupOp(Character c) {
		if (c == '/') {
			this.groupOp = Operator.DIV;
		} else if (c == '*') {
			this.groupOp = Operator.MUL;
		} else if (c == '+') {
			this.groupOp = Operator.ADD;
		} else if (c == '-') {
			this.groupOp = Operator.SUB;
		} else {
			this.groupOp = Operator.NONE;
		}
	}

	public char getGroupLetter() {
		return groupLetter;
	}

	public int getGroupSize() {
		return groupMembers.size();
	}

	public int getGroupValue() {
		return groupValue;
	}

	public Operator getGroupOp() {
		return groupOp;
	}

	public boolean sameRow() {
		int same = 0;
		int row = getGroupMembers().get(0).getRow();
		for (LocationInfo entry : groupMembers) {
			if (row == entry.getRow()) {
				same++;
			}
		}
		return same == getGroupSize();
	}

	public boolean sameCol() {
		int same = 0;
		int col = getGroupMembers().get(0).getCol();
		for (LocationInfo entry : groupMembers) {
			if (col == entry.getCol()) {
				same++;
			}
		}
		return same == getGroupSize();
	}

	/**
	 * if both return false, all locations in cage are NOT in same row or col
	 * @return
	 */
	public boolean sameLine() {
		return sameRow() || sameCol();
	}
}
