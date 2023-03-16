import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class Arena{
    public static void main(String[] args){
        Robot[] robots = new Robot[20];
        int trackLength = 1000;
        int count = readRobots(robots, "robots.txt");
        if(count > 0){
            // Creating 5 clone robots and changing their names
            for(int i=0; i<5; i++){
                String name = "Robot" + (10+i+1);
                int rIndex = (int)(Math.random() * count);
                robots[count] = (Robot) (robots[rIndex].clone());
                robots[count].setName(name);
                count++;
            }
            System.out.println("Robot race started ...");
            // Loop to implement the robot race
            for(int i=0; i<3; i++){
                for(int j=0; j<count; j++){
                    try {
                        robots[j].move(trackLength);
                    }
                    catch (OutOfRangeException e) {
                        System.out.println(robots[j].toString() + " read out of range.");
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    
                }
            }
            // Displaying the final position of the robots
            System.out.println("Robot race ended.\n");
            System.out.println("Robot positions at the end of the race");
            print(robots, count);
            // Sorting the robots by position
            java.util.Arrays.sort(robots, 0, count);
            System.out.println("\nRobot ranking at the end of the race");
            print(robots, count);
            // Finding the robot with the max position, but not out of range 
            int winner = 0;
            for(int i=0; i<count; i++){
                if(robots[i].getPosition() > robots[winner].getPosition() &&
                   robots[i].getPosition() <= trackLength){
                        winner = i;
                }
            }
            System.out.println("The race winner: " + robots[winner]);
      }
      else{
        System.out.println("There are no robots in the race.");
      }
    }
    public static int readRobots(Robot[] list, String filename){
        File file = new File(filename);
        int count = 0;
       
        try {
            Scanner readFile = new Scanner(file);
            
            for (int i = 0; i < list.length; i++) {
                if (readFile.hasNext()) {
                    String[] split = readFile.nextLine().split("\\ ");
                    if (split[0].equals("Turbo")) {
                        list[i] = new TurboRobot(split[1], Integer.parseInt(split[2]));
                    } else if (split[0].equals("Fast")) {
                        list[i] = new FastRobot(split[1], Integer.parseInt(split[2]));
                    } else {
                        list[i] = new SonicRobot(split[1], Integer.parseInt(split[2]));
                    }
                    count++;                   
                } else {
                    break;
                }
                
                
            }
            readFile.close();
        }
        catch (FileNotFoundException e) { // file not found
            System.out.println("Cannont write to file: accounts.txt");
            System.exit(0);
        }
        catch (ArrayIndexOutOfBoundsException e) { // error while reading file
            System.out.println("Array index out of bounds exception while reading file");
            System.exit(0);
        }
        return count;
       
    }
    public static void print(Robot[] list, int count){
        for(int i=0; i<count; i++){
            System.out.println(list[i]);
        }
    }
}