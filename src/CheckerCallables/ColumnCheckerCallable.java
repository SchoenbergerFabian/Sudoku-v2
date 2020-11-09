package CheckerCallables;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class ColumnCheckerCallable implements Callable<Boolean> {

    private final int[][] rawSudoku;
    private final int column;

    public ColumnCheckerCallable(int[][] rawSudoku, int column) {
        this.rawSudoku = rawSudoku;
        this.column = column;
    }

    @Override
    public Boolean call() throws Exception {
        Set<Integer> checker = new HashSet<>();
        for(int row = 0; row < 9; row++){
            checker.add(rawSudoku[row][column]);
        }
        return checker.size()==9;
    }
}
