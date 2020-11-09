import java.io.File;

public class Main {

    private static File file = new File("1_sudoku_level1_solution.csv");

    public static void main(String[] args) {
        SudokuSolver ss = new SudokuSolver();
        int[][] input = ss.readSudoku(file);

        printSudoku(input);

        System.out.println(ss.checkSudoku(input));
        System.out.println(ss.checkSudokuParallel(input));
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
