import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class MergingSort {
    public static int iterations;
    public static boolean split(String filename, String file1, String file2) {
        File filefname = new File(filename); // Open file name for reading
        File filer1 = new File(file1), filer2 = new File(file2); // open file1 and file2 for writing
        try {
            PrintWriter writerFile1 = new PrintWriter(filer1);
            PrintWriter writerFile2 = new PrintWriter(filer2);
            PrintWriter writeFile = writerFile1;

            // read two items from filename
            int previous = 1, current = 1;

            // Reading previous and current
            Scanner scan = new Scanner(filefname);
            previous = Integer.parseInt(scan.nextLine());



            // Writing previous to writeFile
            writerFile1.println(previous);

            while(scan.hasNextLine()) {
                iterations++;
                current = Integer.parseInt(scan.nextLine());// read current here
                if (current > previous) {
                    writeFile.println(current);
                }
                else {
                    if (writeFile == writerFile1) {
                        writeFile = writerFile2;
                    } else {
                        writeFile = writerFile1;
                    }
                    writeFile.println(current); // write the current item to writeFile
                }

                previous = current;
            }
            scan.close(); writerFile1.close(); writerFile2.close();

        } catch (Exception e) {
            System.out.println("Error in split: " + e.getMessage());
        }
        filer2 = new File(file2);
        return filer2.length() == 0L;
    }

    public static void merge(String file1, String file2, String filename) {
        File filefname = new File(filename); // Open filename for reading
        File filer1 = new File(file1), filer2 = new File(file2); // open file1 and file2 for writing
        try {
            Scanner scan = new Scanner(filer1);
            Scanner scan2 = new Scanner(filer2);

            int item1 = 1, item2 = 1;
            if (scan.hasNextLine()) {
                item1 = Integer.parseInt(scan.nextLine());
            }
            if (scan2.hasNextLine()) {
                item2 = Integer.parseInt(scan2.nextLine());
            }

            PrintWriter myWriter = new PrintWriter(filefname);
            while(scan.hasNextLine() && scan2.hasNextLine()) {
                iterations++;
                if (item1 < item2) {
                    myWriter.println(item1);
                    item1 = Integer.parseInt(scan.nextLine());
                } else {
                    myWriter.println(item2);
                    item2 = Integer.parseInt(scan2.nextLine());
                }
            }

            // processing item1 and item2 from the loop
            if (item1 < item2) {
                myWriter.println(item1);
                myWriter.println(item2);
            } else {
                myWriter.println(item2);
                myWriter.println(item1);
            }

            // process any remaining items from file1 or file2
            while(scan.hasNextLine()) {
                iterations++;
                myWriter.println(Integer.parseInt(scan.nextLine()));
            }

            while(scan2.hasNextLine()) {
                iterations++;
                myWriter.println(Integer.parseInt(scan2.nextLine()));
            }

            myWriter.close(); scan.close(); scan2.close();
        }
        catch (Exception e) {
            System.out.println("Error in merge: " + e.getMessage());
        }
    }
    public static int getIterations() {
        return iterations;
    }
    public static void setIterations0() {
        iterations = 0;
    }
}
