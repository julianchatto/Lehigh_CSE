/***
 * Class to model the entity PrintRequest
 * @author Julian Chattopadhyay
 * @version 0.1
 * Date of creation: October 5, 2022
 * Last Date Modified: October 11, 2022
 */
public class PrintRequest implements Comparable<PrintRequest> {
    private int userID;
    private String group; // root, admin, user, batch
    private double size;
    private static String[] groups = {"root", "admin", "user", "batch"};

    /*** 
     * Constructor with 3 parameters
     * @param   id for the id of the print request
     * @param   gr fpr the group of the print request
     * @param   s for the file size of the print request
    */
    public PrintRequest(int id, String gr, double s) {
        userID = id;
        group = gr;
        size = s;
    }

    /***
     * Method to return the UserId
     * @return the UserId of the print request 
     */
    public int getUserID() {
        return userID;
    }

    /***
     * Method to return the group 
     * @return the group of the print request
     */
    public String getGroup() {
        return group;
    }

    /***
     * Method to return the size 
     * @return the size of the print request file
     */
    public double getSize() {
        return size;
    }

    /***
     * Method to set the userId of a print request
     * @param   ID for the id to set as userID
     * no return value
     */
    public void setUserID(int ID) {
        userID = ID;
    }

    /***
     * Method to set the group of a print request
     * @param   g for the group to set 
     * no return value
     */
    public void setGroup(String g) {
        group = g;
    }
    /***
     * Method to set the userId of a print request
     * @param   s for the size to set 
     * no return value
     */
    public void setSize(double s) {
        size = s;
    }

    /***
     * Method to output the information of printrequest
     */
    public String toString() {
        String bSize;
        if (size < 1000) {
            bSize = size + "bytes";
        } else if (size/1000000000 > 1) {
            bSize = String.format("%.1f", size/1000000000) + "GB";
        } else if (size/1000000 > 1) {
            bSize = String.format("%.1f", size/1000000) + "MB";
        } else {
            bSize = String.format("%.1f", size/1000) + "KB";
        }
        return String.format("%-10d\t%-5s\t%-10s", userID, group, bSize);
    }

    /***
     * Returns to numerical value of group using the array groups
     * @param   gr for the name of the group
     * @return the numerical value of the group
     */
    private int getGroupValue(String gr) {
        for (int i = 0; i < groups.length; i++) {
            if (gr.equals(groups[i])) {
                return i;
            }
        }
        return -1;
    }
    
    /***
     * Method to comapre PrintRequest objects by group
     * @param   pr for the PrintRequest object being compared
     */
    public int compareTo(PrintRequest pr) {
        int gr1 = getGroupValue(this.group);
        int gr2 = getGroupValue(pr.group);

        return gr1 - gr2;
    }
}
