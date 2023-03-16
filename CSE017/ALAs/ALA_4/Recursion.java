import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Recursion {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        try {
            System.out.println("Enter a path/file");
            String path = keyboard.nextLine();
            System.out.println("Enter a word");
            String word = keyboard.nextLine();
            // find word in file
            findWord(path,word);

            // get size of path/file
            System.out.println("Enter a path/file"); 
            String path2 = keyboard.nextLine();
            long size = getSize(path2);
            String unit = "bytes";
            // converting size
            if (size > 1000000000) { // GB
                size /= 1000000000;
                unit = "GB";

            } else if(size > 1000000) {  // MB
                size /= 1000000;
                unit = "MB";
            } else if(size > 1000) { // KB
                size /= 1000;
                unit = "KB";
            } 
            System.out.println(path + ": " + size + " " + unit);
        }
        catch (NullPointerException e) { // null pointer exception
            
        }
        catch (Exception e) { // other errors

        }
         
        keyboard.close(); 
    }

    /***
     * Method go find a word in file
     * @param folder
     * @param word
     */
    public static void findWord(String folder, String word) {
        File file = new File(folder);

        if (file.isFile()) { //open rhe file and search for word
            int count = countWord(file, word);
            if (count != 0) {
                System.out.println(word + " was found " + count + " times in " + file.getAbsolutePath());
            }
        } else if (file.isDirectory()) {
            File[] files = file.listFiles(); // returns the contents of the folder
            for (File f: files) {
                findWord(f.getAbsolutePath(), word);
            }
        }
    }

    /***
     * Method to count the number of occurences of word in a file
     * @param file for the name of the file
     * @param word for the word being looked for
     * @return the number of times word was found in file
     */
    public static int countWord(File file, String word) {
        int count = 0;
        try {
            Scanner read = new Scanner(file);
            while (read.hasNextLine()) {
                String line = read.nextLine();
                int index = line.indexOf(word);
                while (index!= -1) {
                    count++;
                    index = line.indexOf(word, index+1);
                }
            }
            read.close();
        }
        catch(FileNotFoundException e) {

        }
       
        return count;
    }

    /***
     * Method to get the size of a directory/file
     * @param path for the name of the folder
     * @return the size of path
     */
    public static long getSize(String path) {
        long size = 0;
        File file = new File(path);

        if (file.isFile()) { // a file
            size = file.length(); // size of the files in bytes
        } else if (file.isDirectory()) { // a folder
            File[] files = file.listFiles();

            for (File f: files) {
                size += getSize(f.getAbsolutePath());
            }
        }    
        return size;
    }
}