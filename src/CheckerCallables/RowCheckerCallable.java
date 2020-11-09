package CheckerCallables;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class RowCheckerCallable implements Callable<Boolean> {

    private final int[][] rawSudoku;
    private final int row;

    public RowCheckerCallable(int[][] rawSudoku, int row) {
        this.rawSudoku = rawSudoku;
        this.row = row;
    }

    @Override
    public Boolean call() throws Exception {
        Set<Integer> checker = new HashSet<>();
        for(int column = 0; column < 9; column++){
            checker.add(rawSudoku[row][column]);
        }
        return checker.size()==9;
    }
}
