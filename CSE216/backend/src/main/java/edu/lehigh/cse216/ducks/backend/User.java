package edu.lehigh.cse216.ducks.backend;

/**
 * User is a simple object that holds information about a user. 
 */
public final class User extends RowData {
    String UserID; 
    String Name; 
    String Email;
    String Role;
    String GI;
    String SO;
    String PICURL;
    
    /**
     * Construct a User object by providing values for its fields
     * @param UserID for the UserID
     * @param Name for the Name
     * @param Email for the Email
     * @param Role for the Role
     * @param GI for the GI
     * @param SO for the SO
     * @param PICURL for the PICURL
     */
    public User(String UserID, String Name, String Email, String Role, String GI, String SO, String PICURL) {
        this.UserID = UserID;
        this.Name = Name;
        this.Email = Email;
        this.Role = Role;
        this.GI = GI;
        this.SO = SO;
        this.PICURL = PICURL;
    }

    /**
     * Convert a User object to a string
     * @return a string representation of the User
     */
    public String toString() {
        return String.format( "%-30s %-15s %-25s %-15s %-17s %-15s %-50s", UserID, Name, Email, Role, GI, SO, PICURL);
    }    
}