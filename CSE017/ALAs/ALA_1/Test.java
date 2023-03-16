/***
 * Class to model the entity Test
 * @author Julian
 * @version 0.1
 * Date of creation: August 23, 2022
 * Last Date Modified: August 23, 2022
 */
public class Test {
    public static void main(String[] args) {
        /***
         * Initializes the array people and assigns each index
         */
        Person[] people = new Person[4];
        people[0] = new Person("Helen Brown", "222 10th Street Bethlehem",
                                "610-334-2288", "hbrown@gmail.com");
        people[1] = new Student("Paul Leister","972 4th Street Allentown",
                                "610-331-7177","pleister@gmail.com", 12345, "CSE");
        people[2] = new Employee("Beth Down", "234 Main Street Philadelphia",
                                 "484-222-4433", "bdown@gmail.com", 33442,
                                "Systems Administrator", 75000.00);
        people[3] = new Faculty("Mark Jones", "21 Orchid Street Bethlehem",
                            "610-333-2211", "mjones@gmail.com", 22222, 
                            "Associate Professor", 100000.00, "Associate Professor");

        /***
         * Prints array people
         * Sorts array people
         * Prints array people
         */                    
        System.out.println("Original list:\n");
        printArray(people);
        sortArray(people);
        System.out.println("Sorted list:\n");
        printArray(people);
    }

    /***
     * Method to print an array of type person
     * @param array
     * no return value
     */
    public static void printArray(Person[] array){
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]); // automatically invokes toString
        }
    }
    /***
     * Method to sort an array of type person by name
     * @param array
     * no return value
     */
    public static void sortArray(Person[] array) {
        for (int i = 0; i < array.length; i++) {
            int minIndex = i;
            for (int j=i+1; j < array.length; j++) {
                if(array[j].getName().compareTo(array[minIndex].getName()) < 0) { // ==0 means left = right; >0 means left after right; <0 means right after left
                    minIndex = j;
                }
            }
            Person temp = array[i];
            array[i] = array[minIndex];
            array[minIndex] = temp;
        }

    }


}
