package edu.lehigh.cse216.ducks.backend;

/**
 * Post is a simple object that holds information about a post.
 */
public final class Post extends RowData {
    int ID; 
    String UserID; 
    String UserName;
    String Subject; 
    String Message;
    int VoteSum;
    String webURL;
    String fileURL;
    String base64;

    /**
     * Construct a Post object by providing values for its fields
     * @param PostID for the PostID
     * @param UserID for the UserID
     * @param UserName for the UserName of the poster
     * @param Subject for the Subject
     * @param Message for the Message
     * @param VoteSum for the VoteSum
     * @param webUrl for the webUrl
     * @param fileURR for the fileUrl
     */
    public Post(int ID, String UserID, String UserName, String Subject, String Message, int VoteSum, String webURL, String fileURL, String base64) {
        this.ID = ID;
        this.UserID = UserID;
        this.UserName = UserName;
        this.Subject = Subject;
        this.Message = Message;
        this.VoteSum = VoteSum;
        this.webURL = webURL;
        this.fileURL = fileURL;
        this.base64 = base64;
    }
    
    /**
     * Convert a Post object to a string
     * @return a string representation of the Post
     */
    public String toString() {
        return String.format( "%-6d %-6d %-10s %-30s %-30s %-30d %-30s %-30s", ID, UserID, UserName, Subject, Message, VoteSum, webURL, fileURL);
    }    
}