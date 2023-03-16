import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Animal[] animals = new Animal[9];
        readFile("animals.txt", animals);
        boolean testing = true;
        
        while(testing) {
            System.out.println("Choose an option (1-4):");
            System.out.println("1. View all animals");
            System.out.println("2. View flying animals");
            System.out.println("3. Sort animals by weight");
            System.out.println("4. Exit");
            try {
                String option = scan.nextLine();
                switch(option) {
                    case "1":
                        print(animals);
                        break;
                    case "2":
                        printFlyers(animals);
                        break;
                    case "3":
                        java.util.Arrays.sort(animals);
                        break;
                    case "4":
                        System.out.println("Exited");
                        testing = false;
                        break;
                    default:
                        System.out.println("Not a valid input");
                }
            }
            catch(InputMismatchException e) {
                System.out.println("Input mismatch");
            }
            catch(Exception e) {
                System.out.println("Error");
            }
        }

        scan.close();
        


    }
    public static void print(Animal[] list) {
        for (Animal a: list) {
            System.out.println(a.toString());
        }
    }
    public static void printFlyers(Animal[] list) {
        for (int i = 0; i < list.length; i++) {
            if(list[i] instanceof CanFly) {
                System.out.println(list[i].toString());
            }
            
        }
    }
    public static void readFile(String fileName, Animal[] list) {
        File file = new File(fileName);
       
        try {
            Scanner readFile = new Scanner(file);
            
            for (int i = 0; i < list.length; i++) {
                if (readFile.hasNext()) {
                    String[] split = readFile.nextLine().split("\\ ");
                    if (split[0].equals("Bat")) {
                        list[i] = new Bat(split[1], Double.parseDouble(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
                    } else if (split[0].equals("Bird")) {
                        list[i] = new Bird(split[1], Double.parseDouble(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
                    } else {
                        list[i] = (new Cat(split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3])));
                    }
                                        
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
       
    }
}