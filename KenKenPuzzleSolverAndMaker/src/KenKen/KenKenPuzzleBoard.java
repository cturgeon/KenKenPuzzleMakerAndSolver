package KenKen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class KenKenPuzzleBoard {

	private int SIZE;
	private ArrayList<LocationInfo> grid;

	// counts how many groups
	public HashMap<Character, Integer> charMap;

	public ArrayList<CageInfo> cages;

	// how many times needed to reset a number
	public int backtrackingIterations;

	// how long it takes to run puzzle solver
	public long startTime;
	public long elapsedTime;
	public boolean timer;

	public KenKenPuzzleBoard() {
		KenKenPuzzle();
	}

	public void KenKenPuzzle() {
		Scanner scanner = new Scanner(System.in);
		SIZE = scanner.nextInt();

		charMap = new HashMap<Character, Integer>();
		cages = new ArrayList<CageInfo>();
		grid = new ArrayList<LocationInfo>();

		for (int row = 0; row < SIZE; row++) {
			String s = scanner.next();
			for (int col = 0; col < SIZE; col++) {
				char cageLetter = s.charAt(col);
				if (charMap.keySet().contains(cageLetter)) { // if letter is already found on grid
					charMap.merge(cageLetter, 1, (v, vv) -> ++v);
					for (CageInfo cage : cages) { // should be guaranteed to find such cage
						if (cage.getGroupLetter() == cageLetter) {
							LocationInfo location = new LocationInfo(row, col);
							cage.addGroupMembers(location);
							grid.add(location);
							location.setCage(cage);
						}
					}
				} else {
					charMap.put(cageLetter, 1);
					CageInfo cage = new CageInfo(cageLetter);
					LocationInfo location = new LocationInfo(row, col);
					cage.addGroupMembers(location);
					grid.add(location);
					cages.add(cage);
					location.setCage(cage);
				}

			}
		}

		for (int i = 0; i < cages.size(); i++) {
			String s = scanner.next();
			char groupLetter = ' ';
			String valueString = "";
			char operator = ' ';

			for (int k = 0; k < s.length(); k++) {
				if (Character.isDigit(s.charAt(k))) {
					valueString += s.charAt(k);
				} else if (Character.isAlphabetic(s.charAt(k))) {
					groupLetter = s.charAt(k);
				} else if (s.charAt(k) <= 47 || s.charAt(k) >= 42) {
					operator = s.charAt(k);
				}
			}
			int valueInteger = Integer.parseInt(valueString);

			for (CageInfo cage : cages) {
				if (cage.getGroupLetter() == groupLetter) {
					cage.setGroupValue(valueInteger);
					cage.setGroupOp(operator);
				}
			}
		}

		for (LocationInfo l : grid) {
			setPossibleValues(l);
		}

		scanner.close();
	}

	public void printBacktrackingIterations() {
		System.out.println("Backtracks: " + backtrackingIterations);
	}

	public void printElapsedTime() {
		System.out.println("Total time: " + elapsedTime);
	}

	public void printInfo() {
		System.out.println();
		printBacktrackingIterations();
		printElapsedTime();
	}

	/**
	 * finds the most constrained variable
	 * 
	 * @return
	 */
	public LocationInfo nextLocation() {

		int mostConstrained = SIZE;

		LocationInfo info = null;
		for (LocationInfo location : grid) {
			if (location.getLocationValue() == -1) {
				if (location.getPossibleValues().size() <= mostConstrained) {
					info = location;
					mostConstrained = location.getPossibleValues().size() - 1;
					if (mostConstrained == 0) {
						return info;
					}
				}
			}
		}
		return info;
	}

	public void printLetterSetForLocation(int row, int col) {
		System.out.println("Possible Values for: " + grid.get(row * SIZE + col).getLetter() + ": "
				+ grid.get(row * SIZE + col).getPossibleValues());
	}

	/**
	 * runs a recursive solver for the grid
	 * 
	 * @return
	 */
	public boolean solvePuzzle() {
		if (!timer) {
			startTime = System.currentTimeMillis();
			timer = true;
		}
		LocationInfo info = nextLocation();

		if (info != null) {
			for (int i = 1; i <= SIZE; i++) {
				if (info.getPossibleValues().contains(i)) {
					if (check(info, i) && tryValue(info, i)) {
						info.setValue(i);
						if (solvePuzzle()) {
							elapsedTime = System.currentTimeMillis() - startTime;
							return true;
						}
					}
				}
			}
			backtrackingIterations++;
			info.setValue(-1);
			return false;
		}
		return true;
	}

	/**
	 * removes a number from locations in the same row
	 * 
	 * @param location
	 * @param number
	 */
	public void removeNumberRow(LocationInfo location, int number) {
		int row = location.getRow();
		for (int i = 0; i < SIZE; i++) {
			grid.get(row * SIZE + i).removePossibleValue(number);
		}
	}

	public void addNumberRow(LocationInfo location, int number) {
		int row = location.getRow();
		for (int i = 0; i < SIZE; i++) {
//			if (grid.get(row * SIZE + i) != location) {
			grid.get(row * SIZE + i).addPossibleValue(number);
//			}
		}
	}

	/**
	 * removes a number from locations in the same column
	 * 
	 * @param location
	 * @param number
	 */
	public void removeNumberCol(LocationInfo location, int number) {
		int col = location.getCol();
		for (int i = 0; i < SIZE; i++) {
			grid.get(col + SIZE * i).removePossibleValue(number);
		}
	}

	public void addNumberCol(LocationInfo location, int number) {
		int col = location.getCol();
		for (int i = 0; i < SIZE; i++) {
//			if (grid.get(col + SIZE * i) != location) {
			grid.get(col + SIZE * i).addPossibleValue(number);
//			}
		}
	}

	/**
	 * removes a number from locations in the same cage
	 * 
	 * @param location
	 * @param number
	 */
	public void removeNumberCage(LocationInfo location, int number) {
		for (LocationInfo entry : location.getCage().getGroupMembers()) {
			if (entry == location) {
				continue;
			} else {
				entry.removePossibleValue(number);
			}
		}
	}

	public void addNumberCage(LocationInfo location, int number) {
		for (LocationInfo entry : location.getCage().getGroupMembers()) {
			if (entry == location) {
				continue;
			} else {
				entry.addPossibleValue(number);
			}
		}
	}

	/**
	 * removes a number from locations in the same row, column, and cage
	 * 
	 * @param location
	 * @param number
	 */
	public void removeNumber(LocationInfo location, int number) {
		removeNumberRow(location, number);
		removeNumberCol(location, number);
//		removeNumberCage(location, number);
	}

	public void addNumber(LocationInfo location, int number) {
		addNumberRow(location, number);
		addNumberCol(location, number);
//		addNumberCage(location, number);
	}

	/**
	 * Prints the KenKenPuzzle Grid
	 */
	public void printGridState() {
		System.out.println();
		for (int i = 1; i <= grid.size(); i++) {
			System.out.print(grid.get(i - 1).getLocationValue() + " ");
			if (i % SIZE == 0) {
				System.out.println();
			}
		}
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
		for (int i = 0; i < SIZE; i++) { // checks same row of location
			if (grid.get(row * SIZE + i).getLocationValue() == number) {
				return false;
			}
		}
		for (int i = 0; i < SIZE; i++) { // checks same column of location
			if (grid.get(i * SIZE + col).getLocationValue() == number) {
				return false;
			}
		}
		return true;
	}

	public void setPossibleValues(LocationInfo location) {
		CageInfo group = location.getCage();
		int groupValue = group.getGroupValue();
		int groupSize = group.getGroupSize();

		ArrayList<Integer> values = new ArrayList<Integer>();

		if (group.getGroupSize() == 1) {
			values.add(groupValue);
		} else if (group.getGroupOp() == Operator.ADD) {
			if (groupSize == 2) {
				for (int i = SIZE; i >= 2; i--) {
					for (int j = SIZE - 1; j >= 1; j--) {
						if (i + j == groupValue && i != j) {
							values.add(i);
							values.add(j);
						}
					}
				}
			} else {
				for (int i = 1; i <= SIZE; i++) {
					if (i < groupValue) {
						values.add(i);
					}
				}

			}
		} else if (group.getGroupOp() == Operator.SUB) {
			for (int i = SIZE; i >= groupValue; i--) {
				for (int j = SIZE - 1; j >= 1; j--) {
					if (Math.abs(i - j) == groupValue) {
						values.add(i);
						values.add(j);
					}
				}
			}
		} else if (group.getGroupOp() == Operator.DIV) {
			for (int i = groupValue; i <= SIZE; i += groupValue) {
				if (i % groupValue == 0) {
					values.add(i);
					values.add(i / groupValue);
				}
			}
		} else if (group.getGroupOp() == Operator.MUL) {
			values.add(1);
			for (int i = 2; i <= SIZE; i++) {
				if (groupValue % i == 0) {
					values.add(i);
				}
			}
		}
		location.setPossibleValues(values);
	}

	/**
	 * tries a number in a location
	 * 
	 * @param location
	 * @param number
	 * @return
	 */
	public boolean tryValue(LocationInfo location, int number) {
		CageInfo group = location.getCage();
		ArrayList<LocationInfo> groupMembers = group.getGroupMembers();
		int groupValue = group.getGroupValue();

		int currentSize = 1;
		for (LocationInfo entry : groupMembers) {
			if (entry == location) {
				continue;
			} else if (entry.getLocationValue() != -1) {
				currentSize++;
			}
		}

		if (currentSize == 1 || group.getGroupSize() == 1) { // only member in group
			return true;
		}

		if (group.getGroupOp() == Operator.ADD) {
			int sum = 0;
			for (LocationInfo entry : groupMembers) {
				if (entry == location) {
					continue;
				} else if (entry.getLocationValue() != -1) {
					sum += entry.getLocationValue();
				}
			}
			if (currentSize == group.getGroupSize()) {
				return sum + number == groupValue;
			} else {
				return sum + number <= groupValue;
			}
		} else if (group.getGroupOp() == Operator.SUB) {
			int otherValue = 0;
			for (LocationInfo entry : groupMembers) {
				if (entry == location) {
					continue;
				} else if (entry.getLocationValue() != -1) {
					otherValue = entry.getLocationValue();
				}
			}
			return Math.abs(number - otherValue) == groupValue;
		} else if (group.getGroupOp() == Operator.MUL) {
			int sum = 1;
			for (LocationInfo entry : groupMembers) {
				if (entry == location) {
					continue;
				} else if (entry.getLocationValue() != -1) {
					sum *= entry.getLocationValue();
				}
			}
			if (currentSize < group.getGroupSize()) {
				return sum * number <= groupValue;
			} else {
				return sum * number == groupValue;
			}
		} else if (group.getGroupOp() == Operator.DIV) {
			int otherValue = 0;
			for (LocationInfo entry : groupMembers) {
				if (entry == location) {
					continue;
				} else if (entry.getLocationValue() != -1) {
					otherValue = entry.getLocationValue();
				}
			}
			if (number > otherValue) {
				return number % otherValue == 0;
			} else {
				return otherValue % number == 0;
			}
		}

		return false;
	}
}
