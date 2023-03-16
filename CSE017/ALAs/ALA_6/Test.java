import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

/***
 * Class to model the entity Test
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 5, 2022
 * Last Date Modified: October 11, 2022
 */
public class Test {
    public static void main(String[] args) {
        Stack<Integer> postfixStack = new Stack<>();
        Scanner keyboard = new Scanner(System.in);
        String selection = "";
        // Postfix expression
        do {
            
            System.out.println("Enter a postfix expression");
            String expression = keyboard.nextLine();

            String[] tokens = expression.split(" ");
            try {
                for (int i = 0; i < tokens.length; i++) {
                    if (tokens[i].matches("\\d{1,}")) {
                        postfixStack.push(Integer.parseInt(tokens[i]));
                    } else {
                        int op1 = postfixStack.pop();
                        int op2 = postfixStack.pop();
                        switch(tokens[i]) {
                            case "+":
                                postfixStack.push(op1+op2);
                                break;
                            case "-":
                                postfixStack.push(op2-op1);
                                break;
                            case "*":
                                postfixStack.push(op1*op2);
                                break;
                            case "/":
                                postfixStack.push(op2/op1);
                                break;
                            default:
                                System.out.println("Invalid operation");
                                break;
                        }
                    }
                }
                int result = postfixStack.pop();
                if(postfixStack.isEmpty()) {
                    System.out.println("Result = " + result);
                } else {
                    System.out.println("postfix expression malformed");
                }
            }
            catch (NoSuchElementException e) { // malformed expressions
                System.out.println("postfix expression malformed");
            } 
            catch (Exception e) {
                System.out.println("NOPE");
            }
            System.out.println("Do you want to do another expression (yes/no)");
            selection = keyboard.nextLine();
            postfixStack.clear();
        } while (selection.equalsIgnoreCase("yes"));

        //Part 2

        PriorityQueue<PrintRequest> printer = new PriorityQueue<>();
       
        File file = new File("requests.txt");
        try {
            Scanner read = new Scanner(file);
            while(read.hasNext()) {
                int id = read.nextInt();
                String group = read.next();
                double size = read.nextDouble();
                PrintRequest pr = new PrintRequest(id, group, size);
                printer.offer(pr);
            }
            read.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        }
        // process the requests
        
        double speed = 10000;
        double time, totalTime = 0;
        System.out.println("User ID         Group   Size            Completion Time");
        while(!printer.isEmpty()) {
            PrintRequest pr = printer.poll();
            time = pr.getSize() / speed;
            System.out.println(pr + "\t" + timeCalc(time));
            totalTime += time;
        }
        System.out.println("Total prinitng time: " + timeCalc(totalTime));
        keyboard.close();
    }

    /***
     * Method to format the time taken in 00:00:00:00
     * @param time for time taken in seconds
     * @return time formated time
     */
    public static String timeCalc(double time) {
        String s = "00", m = "00", h = "00", d = "00";
        // calculate each unit
        double sec = time % 60;
        double minutes = time % 3600 / 60;
        double hours = time % 86400 / 3600;
        double days = time / 86400;

        // assign to a string
        s = String.valueOf((int) Math.round(sec));
        m = String.valueOf((int) minutes);
        h = String.valueOf((int) hours);
        d = String.valueOf((int) days);
        
        // add a zero in front if number < 10
        if (sec < 10 && !s.equals("10")) {
            s = "0" + ((int) Math.round(sec));
        }
        if (minutes < 10 && minutes >= 1) {
            m = "0" + ((int) minutes);
        } 
        if (hours < 10 && hours >= 1) {
            h = "0" +  ((int) hours);
        }
        if (days < 10 && days >= 1) {
            d = "0" +  ((int) days);
        }
           
        // sets any times = 0 to 00
        if(s.equals("0")) {
            s = "00";
        }
        if(m.equals("0")) {
            m = "00";
        }
        if(h.equals("0")) {
            h = "00";
        }
        if(d.equals("0")) {
            d = "00";
        }

        return d + ":" + h + ":" + m + ":" + s;
    }

    
}
