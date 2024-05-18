package edu.lehigh.cse216.ducks.admin;

public class Vote extends RowData {
    Integer PostID;
    String UserID;
    Integer VoteType;

    /**
     * Construct a Vote object by providing values for its fields
     * @param PostID for the postID associated with the vote
     * @param UserID for the userID who made the vote
     * @param VoteType for the type of vote
     */
    public Vote(Integer PostID, String UserID, Integer VoteType) {
        this.PostID = PostID;
        this.UserID = UserID;
        this.VoteType = VoteType;
    }

    /**
     * Convert a Vote object to a string
     * @return a string representation of the Vote
     */
    public String toString() {
        return String.format( "%-6d %-30s %-6d", PostID, UserID, VoteType);
    }
}
