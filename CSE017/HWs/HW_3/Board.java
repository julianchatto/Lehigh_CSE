import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/***
 * Class to model the entity Board
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 3, 2022
 * Last Date Modified: October 6, 2022
 */
public class Board {
    private ArrayList<ArrayList<Integer>> board;
    private ArrayList<Integer> availableNumbers;
    private final int EMPTY = 0;
    private int solveCount;

    /***
     * Constructor with one parameter
     * @param   filename for the name of the file
     */
    public Board(String filename) {
        solveCount = 0;
        // set each element in board to EMPTY  
        board = new ArrayList<>();
        availableNumbers = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                temp.add(EMPTY);
            }
            board.add(temp);
        }
        

        // set each element in board to 9
        for (int i = 0; i < 9; i++) {
            availableNumbers.add(9);
        }
     

        // read from sudoku.txt 
        readBoard(filename);

    }
    /***
     * Method to return the variable solveCount
     * @return solveCount for the number of recursive calls made to solve board
     */
    public int getSolveCount() {
        return solveCount;
    }

    /***
     * Method to read sudoku board from a file
     * @param   filename for the name of the file
     * @throws IllegalArgumentException
     */
    private void readBoard(String filename) throws IllegalArgumentException {
        File file = new File(filename);
        try {
            Scanner readFile = new Scanner(file);
            ArrayList<Integer> temp;
            for (int i = 0; i < 9; i++) {
                if (readFile.hasNext()) {
                    String[] split = readFile.nextLine().split(" ");
                    temp = new ArrayList<>();
                    for (int j = 0; j < 9; j++) {
                        int x = Integer.parseInt(split[j]);
                        temp.add(x);
                       
                        // modify available numbers
                        if (x != EMPTY) {
                            availableNumbers.set(x-1, availableNumbers.get(x-1) - 1);
                        }
                        

                    }    
                } else {
                    break;
                }
                board.set(i, temp);
                
            }
            readFile.close();

            // check if board is valid
            for(int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if(!checkMove(i, j)) {
                        System.out.println("Row: " + i + " Col: " + j + " failed.");
                        throw new IllegalArgumentException();
                    }
                }
            }
            

        }
        catch (FileNotFoundException e) { // file not found
            System.out.println("Cannont write to file: " + filename);
            System.exit(0);
        }
        catch (ArrayIndexOutOfBoundsException e) { // error while reading file
            System.out.println("Array index out of bounds exception while reading file");
            System.exit(0);
        }
        
    }

    /***
     * Method to detrime wheter a digit is actually available to be placed
     * @param   digit for the digit being checked
     * @return true if the number can be added to the board, false otherwise
     */
    private boolean isAvailable(int digit) {
        if (availableNumbers.get(digit-1) > 0) {
            return true;
        }
        return false;
    }

    /***
     * Method to check if all 9 occurences of each digit (1-9) have been placed
     * @return true if all numbers have been used, false otherwise
     * no parameters
     */
    private boolean noNumbersLeft() {
        for (int i = 0; i < 9; i++) {
            if (availableNumbers.get(i) != 0) {
                return false;
            }
        }
        return true;
    }

    /***
     * Method to check if a move is calid according to the rules of Sudoku
     * @param   row for the row of the board
     * @param   col for the col of the board
     * @return false if there is another occurrence of that digit in the corresponding column, row or block, true otherwise
     */
    private boolean checkMove(int row, int col) {
        ArrayList<Integer> square = new ArrayList<>();
        ArrayList<Integer> current = board.get(row);
        int currentNum = current.get(col);
        
        if (currentNum == EMPTY) {
            return true;
        }
        
        //checks row
        for (int i = 0; i < 9; i++) {
            if (i == col) {
                continue;
            } else if (currentNum == current.get(i)) {
                return false;
            }
        }

        //checks col
        for (int i = 0; i < 9; i++) {
            current = board.get(i);
            if (i == row) {
                continue;
            } else if (currentNum == current.get(col)) {
                return false;
            }
        }
        
        //adds values to check square
        ArrayList<Integer> temp1 = board.get(row);
        ArrayList<Integer> temp2;
        ArrayList<Integer> temp3;
        int positionR = row % 3;
        int positionC = col % 3;
        if (positionR == 0) {
            temp2 = board.get(row+1);
            temp3 = board.get(row+2);
        } else if (positionR == 1) {
            temp2 = board.get(row-1);
            temp3 = board.get(row+1);
        } else {
            temp2 = board.get(row-1);
            temp3 = board.get(row-2); 
        }

        square.add(temp1.get(col));
        if (positionC == 0) {
            square.add(temp1.get(col+1));
            square.add(temp1.get(col+2));
            for (int i = col; i < col + 3; i++) {
                square.add(temp2.get(i));
                square.add(temp3.get(i));
            }
        } else if (positionC == 1) {
            square.add(temp1.get(col-1));
            square.add(temp1.get(col+1));
            for (int i = col - 1; i < col + 2; i++) {
                square.add(temp2.get(i));
                square.add(temp3.get(i));
            }
        } else {
            square.add(temp1.get(col-1));
            square.add(temp1.get(col-2));
            for (int i = col; i > col - 3; i--) {
                square.add(temp2.get(i));
                square.add(temp3.get(i));
            }
        }

        // checks square for duplicates
        for (int i = 1; i < 9; i++) {
             if (currentNum == square.get(i)) {
                return false;
            }
        }


        return true;
    }

    /***
     * Method that utilizes the helper methods above to attempt to solve a given Sudoku puzzle
     * @return true if successful and false if it cannot be solved
     * no parameters
     */
    public boolean solve() {
        return solve(0,0);
    }

    /***
     * Helper Method for solve() method
     * @param   row for the row of the puzzle
     * @param   col for the col of the puzzle
     * @return  true if a valid position, false otherwise
     */
    public boolean solve(int row, int col) {
        solveCount++;

        // base case
        if (noNumbersLeft()) {
            return true;
        }
        
        // base case
        if (row == 9) {
            return true;
        }

    
        if ((board.get(row).get(col)) != EMPTY) {
            if (col == 8) {
                return solve(row + 1, 0);
            }
            return solve(row, col + 1);
        }

        for (int i = 1; i < 10; i++) {
            if (!isAvailable(i)) {
                continue;
            }
            board.get(row).set(col, i);
            if (checkMove(row, col) && solve(row, col)) {
                return true;
            }
        }
        board.get(row).set(col, EMPTY);
        return false;
    }

    /***
     * Method to print out the entire 9 x 9 board with a space between each cell/digit
     * no parameters
     */
    @Override
    public String toString() {
        String output = "";
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                output += board.get(i).get(j) + " ";
            }
            output += "\n";
        }
        return output;
    }
}
