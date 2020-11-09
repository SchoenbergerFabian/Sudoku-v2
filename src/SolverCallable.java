import java.util.concurrent.Callable;

public class SolverCallable implements Callable<int[][]> {

    private final int[][] rawSudoku;

    public SolverCallable(int[][] rawSudoku) {
        this.rawSudoku = rawSudoku;
    }

    @Override
    public int[][] call() throws Exception {
        SudokuSolver ss = new SudokuSolver();
        ss.solveParallel(rawSudoku,0,0);
        return rawSudoku;
    }
}
