/***
 * Class to model the entity Student
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: August 24, 2022
 * Last Date Modified: August 28, 2022
 */
public class Student extends Person {
    private int id;
    private String major;

    /***
	 * Default constructor
	 * No parameters
	 * Initializes name, address, phone, and email, and major to the string "none" and id to the integer 0
	 */
    public Student() {
        super();
        id = 0; 
        major = "none";
    }
    /***
	 * Constructor with six parameters
	 * @param	name for the name of a student
	 * @param	address for the address of a student
	 * @param	phone for the phone number of a student
	 * @param	email for the email address of a student
     * @param   id for the id number of a student
     * @param   major for the major of a student
	 */
    Student(String name, String address, String phone, String email, int id, String major) {
        super(name, address, phone, email);
        this.id = id;
        this.major = major;
    }

    /***
	 * Getter for the id of a student
	 * @param	no parameters
	 * @return	the value of the data member id
	 */
    public int getId() {
        return id;
    }
    /***
	 * Getter for the major of a student
	 * @param	no parameters
	 * @return	the value of the data member major
	 */
    public String getMajor(){
        return major;
    
    }
    /***
	 * Setter for the name of a student
	 * @param	id to set the data member id
	 * no return value
	 */

    public void setID(int id) {
        this.id = id;
    }

    /***
	 * Setter for the name of a student
	 * @param	major to set the data member major
	 * no return value
	 */
    public void setMajor(String major) {
        this.major = major;
    }
    /***
	 * Method to get the Student information
	 * no parameters
	 * @return formatted string containing the value of the data members
	 */
    public String toString() {
        String str = super.toString();
        str+= String.format("ID: %d\nMajor: %s\n", id, major);
        return str;
    }
}
