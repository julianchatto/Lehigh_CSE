package edu.lehigh.cse216.ducks.admin;

public class Comment extends RowData {
    Integer ID;
    Integer PostID;
    String UserID;
    String UserName;
    String Text;
    String webURL;
    String fileURL;

    /**
     * Construct a Comments object by providing values for its fields
     * @param PostID for the postID associated with the comment
     * @param UserID for the userID who made the comment
     * @param UserName for the UserName of the commenter
     * @param Text for the text of the comment
     * @param webURL for the URL link to a website
     * @param fileURL for the URL link to a file
     */
    public Comment(Integer ID, Integer PostID, String UserID, String UserName, String Text, String webURL, String fileURL) {
        this.ID = ID;
        this.PostID = PostID;
        this.UserID = UserID;
        this.UserName = UserName;
        this.Text = Text;
        this.webURL = webURL;
        this.fileURL = fileURL;
    }

    /**
     * Convert a Comment object to a string
     * @return a string representation of the Comment
     */
    public String toString() {
        return String.format( "%-6d %-6d %-30s %-30s %-30s %-30s %-30s", ID, PostID, UserID, UserName, Text, webURL, fileURL);
    }
}
