import java.io.File;

public interface ISudokuSolver {
    
    int[][] readSudoku(File file);
    
    boolean checkSudoku(int[][] rawSudoku);
    
    int[][] solveSudoku(int[][] rawSudoku);   
    
    int[][] solveSudokuParallel(int[][] rawSudoku);  
}
