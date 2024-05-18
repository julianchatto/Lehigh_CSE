package edu.lehigh.cse216.ducks.backend;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;

import com.google.api.services.drive.Drive;

import net.rubyeye.xmemcached.MemcachedClient;


public class Database {
    /**
     * The connection to the database.  When there is no connection, it should
     * be null.  Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    // Users
    private PreparedStatement mCreateUserTable;
    private PreparedStatement mDropUserTable;
    private PreparedStatement mSelectAllUsers;
    private PreparedStatement mSelectOneUser;
    private PreparedStatement mDeleteOneUser;
    private PreparedStatement mInsertOneUser;
    private PreparedStatement mUpdateOneUser;

    // Posts
    private PreparedStatement mCreatePostsTable;
    private PreparedStatement mDropPostsTable;
    private PreparedStatement mSelectAllPosts;
    private PreparedStatement mSelectOnePost;
    private PreparedStatement mDeleteOnePost;
    private PreparedStatement mInsertOnePost;
    private PreparedStatement mUpdateOnePost;

    // Votes
    private PreparedStatement mCreateVotesTable;
    private PreparedStatement mDropVotesTable;
    private PreparedStatement mSelectAllVotes;
    private PreparedStatement mSelectVoteForUserForPost;
    private PreparedStatement mDeleteOneVote;
    private PreparedStatement mInsertOneVote;
    private PreparedStatement mUpdateVoteType;

    // Comments
    private PreparedStatement mCreateCommentsTable;
    private PreparedStatement mDropCommentsTable;
    private PreparedStatement mSelectAllComments;
    private PreparedStatement mSelectAllCommentsForPost;
    private PreparedStatement mSelectOneComment;
    private PreparedStatement mDeleteOneComment;
    private PreparedStatement mInsertOneComment;
    private PreparedStatement mUpdateOneComment;



    /**
     * The Database constructor is private: we only create Database objects 
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
    * Get a fully-configured connection to the database
    * 
    * @param db_url The url to the database
    * @param port_default port to use if absent in db_url
    * 
    * @return A Database object, or null if we cannot connect properly
    */ 
    static Database getDatabase(String db_url, String port_default) {
        try {
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String host = dbUri.getHost();
            String port = dbUri.getPort() == -1 ? port_default : Integer.toString(dbUri.getPort());

            return getDatabase(host, port, username, password);
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }
    } 

    /**
     * Get a fully-configured connection to the database
     * 
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * 
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String ip, String port, String user, String pass) {
        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + port + "/", user, pass);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        }

        // Attempt to create all of our prepared statements.  If any of these 
        // fail, the whole getDatabase() call should fail
        try {
            db.mCreateUserTable = db.mConnection.prepareStatement("CREATE TABLE users (id SERIAL PRIMARY KEY, UserID varchar(30) NOT NULL, Name varchar(255) NOT NULL, Email varchar(255) NOT NULL, Role varchar(255) NOT NULL, GI varchar(255) NOT NULL, SO varchar(255) NOT NULL, PICURL varchar(500) NOT NULL)");
            db.mDropUserTable = db.mConnection.prepareStatement("DROP TABLE users");
            db.mSelectAllUsers = db.mConnection.prepareStatement("SELECT * FROM users");
            db.mSelectOneUser = db.mConnection.prepareStatement("SELECT * FROM users WHERE UserID = ?");
            db.mDeleteOneUser = db.mConnection.prepareStatement("DELETE FROM users WHERE UserID = ?");
            db.mInsertOneUser = db.mConnection.prepareStatement("INSERT INTO users VALUES (default, ?, ?, ?, ?, ?, ?, ?)");
            db.mUpdateOneUser = db.mConnection.prepareStatement("Update users SET Name = ?, Email = ?, Role = ?, GI= ?, SO = ? WHERE UserID = ?");

            db.mCreatePostsTable = db.mConnection.prepareStatement("CREATE TABLE posts (id SERIAL PRIMARY KEY, UserID varchar(30) NOT NULL, Subject varchar(255) NOT NULL, Message varchar(2048) NOT NULL, webURL varchar(2048) NOT NULL DEFAULT '', fileURL varchar(2048) NOT NULL DEFAULT '', FOREIGN KEY (UserID) REFERENCES users(UserID))");
            db.mDropPostsTable = db.mConnection.prepareStatement("DROP TABLE posts");
            db.mSelectAllPosts = db.mConnection.prepareStatement("SELECT * FROM posts");
            db.mSelectOnePost = db.mConnection.prepareStatement("SELECT * FROM posts WHERE id = ?");
            db.mDeleteOnePost = db.mConnection.prepareStatement("DELETE FROM posts WHERE id = ?");
            db.mInsertOnePost = db.mConnection.prepareStatement("INSERT INTO posts VALUES (default, ?, ?, ?, default, ?, ?)", Statement.RETURN_GENERATED_KEYS); // this second paprameter allows us to get the generated PostID
            db.mUpdateOnePost = db.mConnection.prepareStatement("UPDATE posts SET Subject = ?, Message = ?, webURL = ?, fileURL = ? WHERE id = ?");

            db.mCreateVotesTable = db.mConnection.prepareStatement("CREATE TABLE votes (PostID int NOT NULL, UserID varchar(30) NOT NULL, VoteType int NOT NULL, PRIMARY KEY (PostID, UserID), FOREIGN KEY (PostID) REFERENCES posts(id) ON DELETE CASCADE, FOREIGN KEY (UserID) REFERENCES users(UserID))");
            db.mDropVotesTable = db.mConnection.prepareStatement("DROP TABLE votes");
            db.mSelectAllVotes = db.mConnection.prepareStatement("SELECT * FROM votes");
            db.mSelectVoteForUserForPost = db.mConnection.prepareStatement("SELECT VoteType FROM votes WHERE PostID = ? AND UserID = ?");
            db.mDeleteOneVote = db.mConnection.prepareStatement("DELETE FROM votes WHERE PostID = ? AND UserID = ?");
            db.mInsertOneVote = db.mConnection.prepareStatement("INSERT INTO votes VALUES (?, ?, ?)");
            db.mUpdateVoteType = db.mConnection.prepareStatement("UPDATE votes SET VoteType = ? WHERE PostID = ? AND UserID = ?");

            db.mCreateCommentsTable = db.mConnection.prepareStatement("CREATE TABLE comments (id SERIAL PRIMARY KEY, PostID int NOT NULL, UserID varchar(30) NOT NULL, Text varchar(2048) NOT NULL, webURL varchar(2048) NOT NULL DEFAULT '', fileURL varchar(2048) NOT NULL DEFAULT '', FOREIGN KEY (PostID) REFERENCES posts(id) ON DELETE CASCADE, FOREIGN KEY (UserID) REFERENCES users(UserID)");
            db.mDropCommentsTable = db.mConnection.prepareStatement("DROP TABLE comments");
            db.mSelectAllComments = db.mConnection.prepareStatement("SELECT * FROM comments");
            db.mSelectAllCommentsForPost = db.mConnection.prepareStatement("SELECT * FROM comments WHERE PostID = ?");
            db.mSelectOneComment = db.mConnection.prepareStatement("SELECT * FROM comments WHERE id = ?");
            db.mDeleteOneComment = db.mConnection.prepareStatement("DELETE FROM comments WHERE id = ?");
            db.mInsertOneComment = db.mConnection.prepareStatement("INSERT INTO comments (PostID, UserID, Text, webURL, fileURL) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            db.mUpdateOneComment= db.mConnection.prepareStatement("UPDATE comments SET Text = ?, webURL = ?, fileURL = ? WHERE id = ?");

        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            db.disconnect();
            return null;
        }
        return db;
    }

    /**
     * Close the current connection to the database, if one exists.
     * 
     * NB: The connection will always be null after this call, even if an 
     *     error occurred during the closing operation.
     * 
     * @return True if the connection was cleanly closed, false otherwise
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }
   
    /**
     * Insert a new user into the database
     * 
     * @param UserID The UserID of the user
     * @param Name The name of the user
     * @param Email The email of the user
     * @param Role The role of the user
     * @param GI The gender identity of the user
     * @param SO The sexual orientation of the user
     * @param PICURL The picture of the user
     * 
     * @return The number of rows that were inserted. -1 indicates an error.
     */
    int insertUser(User user) {
        int affectedRows = -1;
        try {
            mInsertOneUser.setString(1, user.UserID);
            mInsertOneUser.setString(2, user.Name);
            mInsertOneUser.setString(3, user.Email);
            mInsertOneUser.setString(4, user.Role); 
            mInsertOneUser.setString(5, user.GI);
            mInsertOneUser.setString(6, user.SO); 
            mInsertOneUser.setString(7, user.PICURL);

            affectedRows = mInsertOneUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectedRows;
        
    }
    
    /**
     * Insert a new post into the database
     * 
     * @param UserID The UserID of the user
     * @param Subject The subject of the post
     * @param Message The message of the post
     * @param webURL  URL link to a website
     * @param fileURL URL link to a file 
     * 
     * @return The number of rows that were inserted. -1 indicates an error.
     */
    int insertPost(String UserID, String Subject, String Message, String webURL, String fileURL) {
        int postID = -1;
        try {
            if (webURL == null) {
                webURL = "";
            }
            if (fileURL == null) {
                fileURL = "";
            }
            mInsertOnePost.setString(1, UserID);
            mInsertOnePost.setString(2, Subject);
            mInsertOnePost.setString(3, Message);
            mInsertOnePost.setString(4, webURL);
            mInsertOnePost.setString(5, fileURL);

            if (mInsertOnePost.executeUpdate() > 0) {
                ResultSet generatedKeys = mInsertOnePost.getGeneratedKeys();
                if (generatedKeys.next()) {
                    postID = generatedKeys.getInt(1); // Get the generated PostID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postID;
    }

    /**
     * Insert a new vote into the database
     * 
     * @param PostID The PostID of the post
     * @param UserID The UserID of the user
     * @param VoteType The type of vote
     * 
     * @return The number of rows that were inserted. -1 indicates an error.
     */
    int insertVote(Integer PostID, String UserID, Integer VoteType) {
        int affectedRows = 0;
        try {
            mInsertOneVote.setInt(1, PostID);
            mInsertOneVote.setString(2, UserID);
            mInsertOneVote.setInt(3, VoteType);

            affectedRows = mInsertOneVote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectedRows;
    }

    /**
     * Insert a new comment into the database
     * 
     * @param PostID The PostID of the post
     * @param UserID The UserID of the user
     * @param text The text of the comment
     * @param webURL URL link to a website 
     * @param fileURL URL link to a file
     * 
     * @return The commentID. -1 indicates an error.
    */
    int insertComment(Integer PostID, String UserID, String text, String webURL, String fileURL) {
        int commentID = -1;
        try {
            if (webURL == null) {
                webURL = "";
            }
            if (fileURL == null) {
                fileURL = "";
            }
            mInsertOneComment.setInt(1, PostID);
            mInsertOneComment.setString(2, UserID);
            mInsertOneComment.setString(3, text);
            mInsertOneComment.setString(4, webURL);
            mInsertOneComment.setString(5, fileURL);

            int affectedRows = mInsertOneComment.executeUpdate();
            
            // If the insert was successful, get the generated CommentID
            if (affectedRows > 0) {
                ResultSet generatedKeys = mInsertOneComment.getGeneratedKeys();
                if (generatedKeys.next()) {
                    commentID = generatedKeys.getInt(1); // Get the generated CommentID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentID;
    }

    /**
     * Query the database for a list of all users or posts
     * 
     * @param table The table to query
     * 
     * @return All rows, as an ArrayList<RowData>
     */
    ArrayList<RowData> selectAll(String table, MemcachedClient mc, Drive service) {
        ArrayList<RowData> res = new ArrayList<RowData>();
        try {
            if (table.equals("users")) {
                ResultSet rs = mSelectAllUsers.executeQuery(); // get all users
                while (rs.next()) { // iterate through the result set
                    res.add(new User(rs.getString("UserID"), rs.getString("Name"), rs.getString("Email"), rs.getString("Role"), rs.getString("GI"), rs.getString("SO"), rs.getString("PICURL")));
                }
                rs.close();
                return res;
            } else if (table.equals("posts")) {
                ResultSet rs = mSelectAllPosts.executeQuery(); // get all posts
                while (rs.next()) { // iterate through the result set
                    String base64 = null;
                    String fileurl = rs.getString("fileurl");
                    if (fileurl != null && !fileurl.equals("")) {
                        base64 = mc.get(fileurl);
                    }
                    
                    if (base64 == null) {
                        base64 = getBase64(fileurl, service);
                        res.add(new Post(rs.getInt("id"), rs.getString("UserID"), ((User)selectOneUser(rs.getString("UserID"))).Name, rs.getString("Subject"), rs.getString("Message"), rs.getInt("VoteSum"), rs.getString("webURL"), fileurl, base64));
                    } else {
                        res.add(new Post(rs.getInt("id"), rs.getString("UserID"), ((User)selectOneUser(rs.getString("UserID"))).Name, rs.getString("Subject"), rs.getString("Message"), rs.getInt("VoteSum"), rs.getString("webURL"), fileurl, base64));
                    }
                }
                rs.close();
                return res;
            } else if (table.equals("votes")) {
                ResultSet rs = mSelectAllVotes.executeQuery(); // get all votes
                while (rs.next()) { // iterate through the result set
                    res.add(new Vote(rs.getInt("PostID"), rs.getString("UserID"), rs.getInt("VoteType")));
                }
                rs.close();
                return res;
            } else if (table.equals("comments")) {
                ResultSet rs = mSelectAllComments.executeQuery(); // get all comments
                while (rs.next()) { // iterate through the result set
                    res.add(new Comment(rs.getInt("id"), rs.getInt("PostID"), rs.getString("UserID"), ((User)selectOneUser("UserID")).UserID, rs.getString("Text"), rs.getString("webURL"), rs.getString("fileURL")));
                }
                rs.close();
                return res;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        System.err.println("Error: invalid table name");
        return null;
            
    }
    private String getBase64(String fileURL, Drive service) {
        if (fileURL == null || fileURL.equals("")) {
            return "";
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            service.files().get(fileURL.split("/")[5]).executeMediaAndDownloadTo(outputStream);
              // Convert the outputStream to byte array
            byte[] data = outputStream.toByteArray();
            
            // Encode the byte array to Base64 string

            return Base64.getEncoder().encodeToString(data);
        } catch (Exception e) {
            System.err.println("Error in getBase64()");
        }
        return "";
    }

    /**
     * Query the database for one users 
     * 
     * @param UserID The UserID of the user
     * 
     * @return The user with the given UserID, or null if the UserID was invalid
     */
    RowData selectOneUser(String UserID) {
        RowData res = null;
        try {
            mSelectOneUser.setString(1, UserID); // get the user with the given ID
            ResultSet rs = mSelectOneUser.executeQuery(); // execute the query
            if (rs.next()) {
                res = new User(rs.getString("UserID"), rs.getString("Name"), rs.getString("Email"), rs.getString("Role"), rs.getString("GI"), rs.getString("SO"), rs.getString("PICURL"));
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
        
    }

    /**
     * Query the database for one post
     * 
     * @param PostID The PostID of the post
     * 
     * @return The post with the given PostID, or null if the PostID was invalid
    */
    RowData selectOnePost(Integer PostID, MemcachedClient mc, Drive service) {
        RowData res = null;
        try {
            mSelectOnePost.setInt(1, PostID); // get the user with the given ID
            ResultSet rs = mSelectOnePost.executeQuery(); // execute the query
            if (rs.next()) {
                String base64 = null;
                String fileurl = rs.getString("fileURL");
                if (fileurl != null && !fileurl.equals("")) {
                    base64 = mc.get(fileurl);
                }
                
                if (base64 == null || base64.equals("")) {
                    base64 = getBase64(fileurl, service);
                } 
                res = new Post(rs.getInt("id"), rs.getString("UserID"), ((User)selectOneUser(rs.getString("UserID"))).Name, rs.getString("Subject"), rs.getString("Message"), rs.getInt("VoteSum"), rs.getString("webURL"), rs.getString("fileURL"), base64);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Query the database for one comment
     * 
     * @param id The id of the comment
     * 
     * @return The comment with the given id, or null if the id was invalid
    */
    RowData selectOneComment(Integer id) {
        RowData res = null;
        try {
            mSelectOneComment.setInt(1, id); // get the user with the given ID
            ResultSet rs = mSelectOneComment.executeQuery(); // execute the query
            if (rs.next()) {
                res = new Comment(rs.getInt("id"), rs.getInt("PostID"), rs.getString("UserID"), ((User)selectOneUser(rs.getString("UserID"))).UserID, rs.getString("Text"), rs.getString("webURL"), rs.getString("fileURL"));
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Query the database for the vote for a specified post of a specified user
     * 
     * @param PostID The PostID of the post
     * @param UserID The UserID of the user
     * 
     * @return the vote for a user for a specified post
    */
    int selectVoteForPost(Integer PostID, String UserID) {
        int res = Integer.MIN_VALUE;
        try {
            mSelectVoteForUserForPost.setInt(1, PostID); // get the user with the given ID
            mSelectVoteForUserForPost.setString(2, UserID);
            ResultSet rs = mSelectVoteForUserForPost.executeQuery(); // get all votes
            if (rs.next()) { // iterate through the result set
                res = rs.getInt("VoteType");
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Query the database for all the comments for a specified post
     * 
     * @param PostID The PostID of the post
     * 
     * @return All comments for the post with the given PostID
    */
    ArrayList<RowData> selectCommentsForPost(Integer PostID) {
        ArrayList<RowData> res = new ArrayList<RowData>();

        try {
            mSelectAllCommentsForPost.setInt(1, PostID); // get the user with the given ID
            ResultSet rs = mSelectAllCommentsForPost.executeQuery(); // get all votes
            while (rs.next()) { // iterate through the result set
                res.add(new Comment(rs.getInt("id"), rs.getInt("PostID"), rs.getString("UserID"), ((User)selectOneUser(rs.getString("UserID"))).Name, rs.getString("Text"), rs.getString("webURL"), rs.getString("fileURL")));
            }
            rs.close();
            return res;
        }  catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Delete a vote 
     * 
     * @param PostID The PostID of the post
     * @param UserID The UserID of the user
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int deleteVote(Integer PostID, String UserID) {
        int res = -1;

        try {
            mDeleteOneVote.setInt(1, PostID);
            mDeleteOneVote.setString(2, UserID);
            res = mDeleteOneVote.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
    
    /**
     * Delete a comment
     * 
     * @param id The id of the comment
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
     */
    int deleteComment(Integer id) {
        int res = -1;

        try {
            mDeleteOneComment.setInt(1, id);
            res = mDeleteOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
    
    /**
     * Delete all votes for a post
     * 
     * @param PostID The PostID of the post
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
    */
    int deletePost(Integer id) {
        int res = -1;

        try {
            mDeleteOnePost.setInt(1, id);
            res = mDeleteOnePost.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Delete a user
     * 
     * @param UserID The UserID of the user
     * 
     * @return The number of rows that were deleted. -1 indicates an error.
    */
    int deleteUser(String UserID) {
        int res = -1;

        try {
            mDeleteOneUser.setString(1, UserID);
            res = mDeleteOneUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Update the data for a user
     * 
     * @param dataField The field to update
     * @param value The new value for the field
     * @param UserID The UserID of the user
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int updateUser(String Name, String Email, String Role, String GI, String SO, String UserID) {
        int res = -1;
        try {
            mUpdateOneUser.setString(1, Name);
            mUpdateOneUser.setString(2, Email);
            mUpdateOneUser.setString(3, Role);
            mUpdateOneUser.setString(4, GI);
            mUpdateOneUser.setString(5, SO);
            mUpdateOneUser.setString(6, UserID);
            
            res = mUpdateOneUser.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Update the Subject and Message for a post
     * 
     * @param id The id of the post
     * @param Subject The new subject for the post
     * @param Message The new message for the post
     * @param webURL The new webURL for the post
     * @param fileURL the new fileURL for the post
     * 
     * @return The number of rows that were updated. -1 indicates an error.
    */
    int updatePost(Integer id, String Subject, String Message, String webURL, String fileURL) {
        int res = -1;

        try {
            if (webURL == null) {
                webURL = "";
            }
            if (fileURL == null) {
                fileURL = "";
            }
            mUpdateOnePost.setString(1, Subject);
            mUpdateOnePost.setString(2, Message);
            mUpdateOnePost.setInt(3, id);
            mUpdateOnePost.setString(4, webURL);
            mUpdateOnePost.setString(5, fileURL);

            res = mUpdateOnePost.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Update the VoteType for a vote
     * 
     * @param VoteType The new VoteType for the vote
     * @param PostID The PostID of the post
     * @param UserID The UserID of the user
     * 
     * @return The number of rows that were updated. -1 indicates an error.
    */
    int updateVote(Integer VoteType, Integer PostID, String UserID) {
        int res = -1;
        
        try {
            mUpdateVoteType.setInt(1, VoteType);
            mUpdateVoteType.setInt(2, PostID);
            mUpdateVoteType.setString(3, UserID);

            res = mUpdateVoteType.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Update the text for a comment
     * 
     * @param Text The new text for the comment
     * @param id The id of the comment
     * @param webURL The webURL of the comment
     * @param fileURL The fileURL of the comment
     * 
     * @return The number of rows that were updated. -1 indicates an error.
     */
    int updateComment(Integer id, String Text, String webURL, String fileURL) {
        int res = -1;
        
        try {
            if (webURL == null) {
                webURL = "";
            }
            if (fileURL == null) {
                fileURL = "";
            }
            mUpdateOneComment.setString(1, Text);
            mUpdateOneComment.setString(2, webURL);
            mUpdateOneComment.setString(3, fileURL);
            mUpdateOneComment.setInt(4, id);

            res = mUpdateOneComment.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    String getUserName(String UserId) {
        String name = null;
        try {
            mSelectOneUser.setString(1, UserId);
            ResultSet res = mSelectOneUser.executeQuery();
            if (res.next()) {
                name = res.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * Create tblData.  If it already exists, this will print an error
     * 
     * @param table The table to create
     */
    void createTable(String table) {
        try {
            if (table.equals("users")) {
                mCreateUserTable.execute();
            } else if (table.equals("posts")) {
                mCreatePostsTable.execute();
            } else if (table.equals("votes")) {
                mCreateVotesTable.execute();
            } else if (table.equals("comments")) {
                mCreateCommentsTable.execute();
            } else {
                System.err.println("Error: invalid table name");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Remove tblData from the database.  If it does not exist, this will print
     * an error.
     * 
     * @param table The table to drop
     */
    void dropTable(String table) {
        try {
            if (table.equals("users")) {
                mDropUserTable.execute();
            } else if (table.equals("posts")) {
                mDropPostsTable.execute();
            } else if (table.equals("votes")) {
                mDropVotesTable.execute();
            } else if (table.equals("comments")) {
                mDropCommentsTable.execute();
            } else {
                System.err.println("Error: invalid table name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
}