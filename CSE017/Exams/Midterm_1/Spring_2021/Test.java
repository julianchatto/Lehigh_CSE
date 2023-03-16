import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Room[] rooms = new Room[100];
        boolean testing = true;
        Scanner scan = new Scanner(System.in);
        
        int count = readFromFile(rooms, "rooms.txt");
        while (testing) {
            System.out.println("Choose an option 1-4");
            System.out.println("1. Find a room");
            System.out.println("2. View list of rooms");
            System.out.println("3. Sort list of rooms");
            System.out.println("4. Exit program"); 
        
            try {
                String option = scan.nextLine();
                switch(option) {
                    case "1": // find room
                        System.out.println("Enter a room number");
                        String roomNumber = scan.nextLine();
                        checkRoomNumber(roomNumber);
                        int result = findRoom(rooms, count, roomNumber);
                        if (result >= 0) {
                            System.out.println("Room found at index: " + result);
                        } else {
                            System.out.println("Room not found");
                        }
                        break;
                    case "2": // view rooms
                        printRooms(rooms, count);
                        break;
                    case "3": // sort rooms
                        java.util.Arrays.sort(rooms,0,count); // doesn't work because throwing a nullpointer exception???
                        break;
                    case "4":
                        System.out.println("Program exited");
                        testing = false;
                        break;
                    default: 
                        System.out.println("Incorrect input try again");
                        break;
       
               }
            }
            catch (InputMismatchException e) {
                System.out.println("Input mismatch");
            } 
            catch (Exception e) {
                System.out.println("Error");
            }
        }
        scan.close();
        
        
    }

    public static void printRooms(Room[] list, int count) {
        for (int i = 0; i < count; i++) {
            System.out.println(list[i].toString());
        }
    }

    public static int findRoom(Room[] list, int count, String roomNumber) {
        for (int i = 0; i < count; i++) {
            if (list[i].getNumber().equals(roomNumber)) {
                return i;
            }
        }
        return -1;
    }

    public static void checkRoomNumber(String roomNumber) throws Exception {
        String[] split = roomNumber.split("\\-");

        try {
            int digits = Integer.parseInt(split[1]);
            if(digits > 999 || digits < 100) {
                throw new Exception();
            }
            String[] splited = split[0].split("");
            
            if (!splited[0].matches("[a-zA-Z]+")) {
                throw new Exception();
            } 
            if (!splited[1].matches("[a-zA-Z]+")) {
                throw new Exception();
            } 
            if (splited.length > 2) {
                throw new Exception();
            }
        } 
        catch (InputMismatchException e) {
            throw new Exception();
        }
        catch (Exception e) {
            throw new Exception();
        }
    }

    public static int readFromFile(Room[] list, String filename) {
        File file = new File(filename);
        int count = 0;
        try {
            Scanner readFile = new Scanner(file);
            
            for (int i = 0; i < list.length; i++) {
                if (readFile.hasNext()) {
                    String[] split = readFile.nextLine().split("\\ ");
                    if (split[0].equals("lab")) {
                        list[i] = new Lab(split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
                    } else if (split[0].equals("office")) {
                        String name = split[4] + split[5];
                        list[i] = new Office(split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]), name);
                    } else {
                        list[i] = new Classroom(split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]));
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
}
