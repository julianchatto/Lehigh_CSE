/***
 * Class to model the entity Pair
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 25, 2022
 * Last Date Modified: September 26, 2022
 */
public class Pair<E1, E2> {
    private E1 first;
    private E2 second;

    /***
     * Default constructor
     * No parameters
     * Initializes first and second to null
     */
    public Pair(){
        first = null;
        second = null;
    }

    /***
     * Constructor with two parameters
     * @param   first for the first object
     * @param   second for the second object
     * 
     */
    public Pair(E1 first, E2 second) {
        this.first = first;
        this.second = second;
    
    }

    /**
     * Method to return first
     * @return first
     */
    public E1 getFirst() {
        return first;
    }
    /**
     * Method to return second
     * @return second
     */
    public E2 getSecond() {
        return second;
    }

    /***
     * Method to set First
     * @param first for what will be set to first
     * no return value
     */
    public void setFirst(E1 first) {
        this.first = first;
    }

    /***
     * Method to set Second
     * @param second for what will be set to second
     * no return value
     */
    public void setSecond(E2 second) {
        this.second = second;
    }

    /***
     * Method to print the class information
     * @return String containg the class information
     */
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

    /***
     * Method to compare a Pair and another object 
     * @param   obj for the object be compared
     * @return boolean if one is greater or less  
     */
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair<E1, E2> p = (Pair<E1, E2>) obj; // downcasting obj to type Pair
            boolean eq1 = this.getFirst().equals(p.getFirst());
            boolean eq2 = this.getSecond().equals(p.getSecond());
            return eq1 & eq2;
        }
        return false;
    }
    
}
