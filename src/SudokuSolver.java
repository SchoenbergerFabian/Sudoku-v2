import CheckerCallables.BlockCheckerCallable;
import CheckerCallables.ColumnCheckerCallable;
import CheckerCallables.RowCheckerCallable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SudokuSolver implements ISudokuSolver {
    @Override
    public int[][] readSudoku(File file) {
        try {
            return Files.lines(file.toPath())
                    .map(line -> line.split(";"))
                    .map(splitLine -> Arrays.stream(splitLine)
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    .toArray(int[][]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new int[9][9];
    }

    @Override
    public boolean checkSudoku(int[][] rawSudoku) {
        boolean result = true;

        Set<Integer> checker = new HashSet<>();

        //check rows
        for(int row = 0; row<9; row++){
            for(int column = 0; column <9; column++){
                if(rawSudoku[row][column]==0){
                    return false;
                }else{
                    checker.add(rawSudoku[row][column]);
                }
            }
            result = checker.size()==9;
            checker.clear();
        }

        //check columns
        for(int column = 0; column<9; column++){
            for(int row = 0; row <9; row++){
                if(rawSudoku[column][row]==0){
                    return false;
                }else{
                    checker.add(rawSudoku[column][row]);
                }
            }
            result = result && checker.size()==9;
            checker.clear();
        }

        //check blocks
        for(int blockRow = 0; blockRow < 3; blockRow++){
            for(int blockColumn = 0; blockColumn < 3; blockColumn++){
                for(int row = 0; row < 3; row++){
                    for(int column = 0; column < 3; column++){
                        int number = rawSudoku[row+(blockRow*3)][column+(blockColumn*3)];
                        if(number==0){
                            return false;
                        }else{
                            checker.add(number);
                        }
                    }
                }

                result = result && checker.size()==9;
                checker.clear();
            }
        }

        return result;
    }

    public boolean checkSudokuParallel(int[][] rawSudoku){
        boolean AbsoluteResult = false;

        List<Callable<Boolean>> checkers = new ArrayList<>();

        //rows & columns
        for(int row_column = 0; row_column<9; row_column++){
            checkers.add(new RowCheckerCallable(rawSudoku,row_column));
            checkers.add(new ColumnCheckerCallable(rawSudoku,row_column));
        }

        //blocks
        for(int blockRow = 0; blockRow<3; blockRow++){
            for(int blockColumn = 0; blockColumn<3; blockColumn++){
                checkers.add(new BlockCheckerCallable(rawSudoku,blockRow,blockColumn));
            }
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            AbsoluteResult = executor.invokeAll(checkers).stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .allMatch(result -> result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return AbsoluteResult;
    }

    @Override
    public int[][] solveSudoku(int[][] rawSudoku) {
        return new int[0][];
    }

    @Override
    public int[][] solveSudokuParallel(int[][] rawSudoku) {
        return new int[0][];
    }

    public void benchmark(int[][] rawSudoku){

    }

    public void benchmarkParallel(int[][] rawSudoku){

    }
}
