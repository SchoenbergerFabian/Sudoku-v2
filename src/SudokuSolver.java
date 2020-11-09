import CheckerCallables.BlockCheckerCallable;
import CheckerCallables.ColumnCheckerCallable;
import CheckerCallables.RowCheckerCallable;
import sun.swing.BakedArrayList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

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
        int[][] clone = new int[9][9];
        for(int row = 0; row<9; row++){
            for(int column = 0; column<9; column++){
                clone[row][column] = rawSudoku[row][column];
            }
        }
        //clone = rawSudoku;

        solve(clone,0,0);
        return clone;
    }

    private boolean solve(int[][] rawSudoku, int row, int column){

        if(row == 8 && column == 9){
            return true;
        }

        if(column == 9){
            row++;
            column=0;
        }

        if(rawSudoku[row][column]!=0){
            return solve(rawSudoku, row, column+1);
        }

        for(int number = 1; number<10; number++){

            if(isSafe(rawSudoku, row, column, number)){
                rawSudoku[row][column] = number;
                if(solve(rawSudoku,row,column+1)){
                    return true;
                }
            }

            rawSudoku[row][column] = 0;
        }
        return false;
    }

    //<editor-fold desc="DOES NOT WORK">
    @Override
    public int[][] solveSudokuParallel(int[][] rawSudoku) {

        int[][] clone = new int[9][9];
        for(int row = 0; row<9; row++){
            for(int column = 0; column<9; column++){
                clone[row][column] = rawSudoku[row][column];
            }
        }

        solveParallel(clone,0,0);

        return clone;
    }

    public boolean solveParallel(int[][] rawSudoku, int row, int column){

        if(row == 8 && column == 9){
            return true;
        }

        if(column == 9){
            row++;
            column=0;
        }

        if(rawSudoku[row][column]!=0){
            return solveParallel(rawSudoku, row, column+1);
        }else{
            List<Callable<int[][]>> solvers = new ArrayList<>();
            for(int number = 1; number<10; number++){

                if(isSafe(rawSudoku, row, column, number)){

                    int[][] clone = new int[9][9];
                    for(int r = 0; r<9; r++){
                        for(int c = 0; c<9; c++){
                            clone[r][c] = rawSudoku[r][c];
                        }
                    }

                    clone[row][column] = number;

                    solvers.add(new SolverCallable(clone));
                }

                if(solvers.size()!=0){
                    ExecutorService executor = Executors.newCachedThreadPool();
                    try {
                        rawSudoku = executor.invokeAny(solvers);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return true;
    }
    //</editor-fold>

    private boolean isSafe(int[][] rawSudoku, int row, int column, int number){
        //looks through row
        for(int c = 0; c<9; c++){
            if(rawSudoku[row][c]==number){
                return false;
            }
        }

        //looks through column
        for(int r = 0; r<9; r++){
            if(rawSudoku[r][column]==number){
                return false;
            }
        }

        //looks through block
        int startRow = row - row % 3;
        int startCol = column - column % 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (rawSudoku[r + startRow][c + startCol] == number) {
                    return false;
                }
            }
        }

        //safe
        return true;
    }

    public long benchmark(int[][] rawSudoku){
        long start = System.currentTimeMillis();
        for(int counter = 0; counter<10; counter++){
            int[][] solution = solveSudoku(rawSudoku);
            checkSudoku(solution);
        }
        long end = System.currentTimeMillis();
        return (end-start)/10;
    }

    //<editor-fold desc="DOES NOT WORK">
    public long benchmarkParallel(int[][] rawSudoku){
        long start = System.currentTimeMillis();
        for(int counter = 0; counter<10; counter++){
            int[][] solution = solveSudokuParallel(rawSudoku);
            checkSudokuParallel(solution);
        }
        long end = System.currentTimeMillis();
        return (end-start)/10;
    }
    //</editor-fold>
}
