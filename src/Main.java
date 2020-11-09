import java.io.File;

public class Main {

    /*
        NOTES
        Checking works parallel! Solving doesn't!
        Bonus task is done!

        my other solution (v1) does not have the bonus task, but parallel works (though slower than normal)
    */

    private static File file = new File("1_sudoku_level1.csv");

    public static void main(String[] args) {
        SudokuSolver ss = new SudokuSolver();

        //reading
        int[][] input = ss.readSudoku(file);

        //printing raw
        printSudoku(input);

        //solving
        int[][] outputSingle = ss.solveSudoku(input);
        //int[][] outputParallel = ss.solveSudokuParallel(input);

        //printing solution
        System.out.println("\nSOLUTION (single)");
        printSudoku(outputSingle);
        //System.out.println("\nSOLUTION (parallel)");
        //printSudoku(outputParallel);

        //checking
        System.out.println("SOLVED (single) = " + ss.checkSudoku(outputSingle)/*+"\n"
                +"SOLVED (parallel) = " + ss.checkSudokuParallel(outputParallel)*/);

        //benchmarking
        System.out.println("\nBENCHMARK" +
                "\nSingle: " + ss.benchmark(input) + "ms" /*+
                "\nParallel: " + ss.benchmarkParallel(input) + "ms"*/);
    }

    private static void printSudoku(int[][] rawSudoku){
        String horizontalSeparator = "++---+---+---++---+---+---++---+---+---++\n";
        String verticalSeparator = "|";

        StringBuilder output = new StringBuilder();

        output.append(horizontalSeparator.replaceAll("-","="));

        for(int row = 0; row<9; row++){

            output.append(verticalSeparator+verticalSeparator);

            for(int column = 0; column<9; column++){
                output.append(" "+rawSudoku[row][column]+" ");

                if((column+1)%3==0){
                    output.append(verticalSeparator+verticalSeparator);
                }else{
                    output.append(verticalSeparator);
                }
            }

            if((row+1)%3==0){
                output.append("\n"+horizontalSeparator.replaceAll("-","="));
            }else{
                output.append("\n"+horizontalSeparator);
            }
        }

        System.out.println(output.toString());
    }
}
