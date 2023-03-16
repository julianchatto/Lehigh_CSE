import java.util.Comparator;
/***
 * Class to model the entity ComparatorBySecond
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: September 25, 2022
 * Last Date Modified: September 26, 2022
 */
public class ComparatorBySecond<E1, E2  extends Comparable<E2>> implements Comparator<Pair<E1, E2>>{
    
    /***
     * Method to compare the first elements of Pair
     * @param p1 for the first Pair
     * @param p2 for the second Pair
     * @return the values 1, -1, 0 depending on compairson
     */
    public int compare(Pair<E1, E2> p1, Pair<E1, E2> p2) {
        return p1.getSecond().compareTo(p2.getSecond());
    }
}
