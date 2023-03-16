/***
 * Class to model the entity Employee
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: August 24, 2022
 * Last Date Modified: August 28, 2022
 */
public class Employee extends Person {
    private int id;
    private String position;
    private double salary;

    /***
	 * Default constructor
	 * No parameters
	 * Initializes name, address, phone, email, and position to the string "none" and salary to the double 0.0
	 */
    public Employee(){
        super();
        position = "none";
        salary = 0.0;
    }

    /***
	 * Constructor with seven parameters
	 * @param	name for the name of an employee
	 * @param	address for the address of an employee
	 * @param	phone for the phone number of an employee
	 * @param	email for the email address of an employee
     * @param   id for the id of an employee
     * @param   position for the position of an employee
     * @param   salary for the salary of an employee
	 */
    Employee(String name, String address, String phone, String email, int id, String position, double salary)  {
        super(name, address, phone, email);
        this.id = id;
        this.position = position;
        this.salary = salary;
    }

    /***
	 * Getter for the id of an employee
	 * @param	no parameters
	 * @return	the value of the data member name
	 */
    public int getId(){
        return id;
    }

    /***
	 * Getter for the id of an employee
	 * @param	no parameters
	 * @return	the value of the data member name
	 */
    public String getPosition () {
        return position;
    }

    /***
	 * Getter for the salary of an employee
	 * @param	no parameters
	 * @return	the value of the data member name
	 */
    public double getSalary() {
        return salary;
    
    }

    /***
	 * Setter for the id of a employee
	 * @param	id to set the data member id
	 * no return value
	 */
    public void setId(int id) {
        this.id = id;
    }
    /***
	 * Setter for the position of an employee
	 * @param	position to set the data member position
	 * no return value
	 */
    public void setPosition(String position) {
        this.position = position;
    }
    /***
	 * Setter for the salary of an employee
	 * @param	salary to set the data member salary
	 * no return value
	 */
    public void setSalary(double salary) {
        this.salary = salary;
    }


    public String toString() {
        String str = super.toString();
        str+= String.format("ID: %d\nPosition: %s\nSalary: %f\n", id, position, salary);
        return str;
    }

}
