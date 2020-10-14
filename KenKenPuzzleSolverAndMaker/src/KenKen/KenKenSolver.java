package KenKen;


public class KenKenSolver {

	public static void main(String[] args) {
		KenKenPuzzleMaker newPuzzle = new KenKenPuzzleMaker(9);
		newPuzzle.printKenKenPuzzle();
		
		KenKenPuzzleBoard grid = new KenKenPuzzleBoard();
		grid.solvePuzzle();
		grid.printGridState();
		grid.printInfo();
	}
}
