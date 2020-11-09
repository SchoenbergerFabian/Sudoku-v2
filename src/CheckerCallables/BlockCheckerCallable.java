package CheckerCallables;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class BlockCheckerCallable implements Callable<Boolean> {

    private final int[][] rawSudoku;
    private final int blockRow;
    private final int blockColumn;

    public BlockCheckerCallable(int[][] rawSudoku, int blockRow, int blockColumn) {
        this.rawSudoku = rawSudoku;
        this.blockRow = blockRow;
        this.blockColumn = blockColumn;
    }

    @Override
    public Boolean call() throws Exception {
        Set<Integer> checker = new HashSet<>();
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 3; column++){
                checker.add(rawSudoku[row+(blockRow*3)][column+(blockColumn*3)]);
            }
        }
        return checker.size()==9;
    }
}
