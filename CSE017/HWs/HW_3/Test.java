/***
 * Class to model the entity Test
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 3, 2022
 * Last Date Modified: October 4, 2022
 */
public class Test {
    public static void main(String[] args) {
        try { 
            Board b = new Board("sudoku.txt");

            System.out.println("\nStarting Puzzle:");
            System.out.println(b.toString());

            if (b.solve()) {
                System.out.println("Solved!");
                System.out.println(b.getSolveCount() + " recurssive calls made to solve the board!\n");
                System.out.println(b.toString());
            } else {
                System.out.println("Board not solvable");
            }
        } 
        catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } 
        catch (Exception e) {
            System.out.println(e.getMessage());   
        } 
    }
}
