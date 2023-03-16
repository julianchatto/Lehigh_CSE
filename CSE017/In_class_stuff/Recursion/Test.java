import java.io.File;
import java.util.Scanner;

public class Test {
    public static int binarySearch(int[] list,int first,int last,int key){
        if (first > last) return -1;
        else{
            int middle = (last + first) / 2;
            if (key == list[middle]) {
                return middle;
            } 
            else if (key < list[middle]) {
                last = middle - 1;
            }
             
            else {
                first = middle + 1;
            }
        
        return binarySearch(list, first, last, key);
        }
    }
    public static int binarySearch(int[] list, int key) {
        int first = 0;
        int last = list.length-1;
        return binarySearch(list, first, last, key); 
    }
    public static void main(String[] args) {
        // int[]  numbers = {32,102,44,56,12,9,21};
        // java.util.Arrays.sort(numbers);
        // System.out.println(binarySearch(numbers, 9));
        // System.out.println(binarySearch(numbers, 22));

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter a path: ");
        String startPath = keyboard.nextLine();
        System.out.println("Enter a file name: ");
        String filename = keyboard.nextLine();
        
        String returnPath = searchFile(startPath, filename);

        if (returnPath.equals("")) {
            System.out.println("File not found");
        } else {
            System.out.println("File found at: " + returnPath);
        }
        keyboard.close();
    }
    public static String searchFile(String path, String filename){
        System.out.println("Looking in the folder: " + path);
        File file = new File(path);
        String found = "";
        if(file.exists()) { // checking that path is valid
            if(file.isDirectory()) { // checking that path is a folder
                File[] files = file.listFiles(); // returns the contents of path in an array of type file
                for(int i=0; i<files.length; i++) { // iterating throuhg th econtents of the folder
                    if(files[i].isFile()) {
                        if (files[i].getName().equals(filename)) // comparing name of the file to filenmae 
                            return files[i].getAbsolutePath(); // return the path to the file name
                    }
                    else { // it is a folder, go deeper (recursion)
                        found = searchFile(files[i].getAbsolutePath(), filename); // recursive search
                        if(!found.equals(""))
                            return found;
                    }
                }
            }
        }
        return found;
    }
}

