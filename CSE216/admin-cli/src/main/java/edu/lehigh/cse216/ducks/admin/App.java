package edu.lehigh.cse216.ducks.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;


/**
 * App is our basic admin app.  For now, it is a demonstration of the six key 
 * operations on a database: connect, insert, update, query, delete, disconnect
 */
public class App {

    /**
     * Print the menu for our program
     */
    static void menu() {
        System.out.println("Main Menu");
        System.out.println("  [T] Create tblData");
        System.out.println("  [D] Drop tblData");
        System.out.println("  [1] Query for a specific row");
        System.out.println("  [*] Query for all rows");
        System.out.println("  [-] Delete a row");
        System.out.println("  [+] Insert a new row");
        System.out.println("  [~] Update a row");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
        System.out.println("  [#] Invalidate a post");
        System.out.println("  [$] Invalidate a user");
        System.out.println("  [^] Invalidate the LRU cache");
    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * 
     * @return The character corresponding to the chosen menu option
     */
    static char prompt(BufferedReader in) {
        // The valid actions:
        String actions = "TD1*-+~q?#$^";

        // We repeat until a valid single-character option is selected        
        while (true) {
            System.out.print("[" + actions + "] :> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1)
                continue;
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided.  May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided.  On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }
    static long getLong(BufferedReader in, String message) {
        long i = -1;
        try {
            System.out.print(message + " :> ");
            i = Long.parseLong(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options.  Ignored by this program.
     */
    public static void main(String[] argv) {
        Database db;
        if( System.getenv("DATABASE_URL") != null ){
            db =  Database.getDatabase(System.getenv("DATABASE_URL"), "5432");
        } else {
            // get the Postgres configuration from the environment
            Map<String, String> env = System.getenv();
            String ip = env.get("POSTGRES_IP");
            String port = env.get("POSTGRES_PORT");
            String user = env.get("POSTGRES_USER");
            String pass = env.get("POSTGRES_PASS");

            // Get a fully-configured connection to the database, or exit 
            // immediately
            db = Database.getDatabase(ip, port, user, pass);
        }

        if (db == null)
            return;

        List<InetSocketAddress> servers = AddrUtil.getAddresses(System.getenv("MEMCACHIER_SERVERS").replace(",", " "));
        AuthInfo authInfo = AuthInfo.plain(System.getenv("MEMCACHIER_USERNAME"), System.getenv("MEMCACHIER_PASSWORD"));

        MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);

        // Configure SASL auth for each server
        for(InetSocketAddress server : servers) {
            builder.addAuthInfo(server, authInfo);
        }

        // Use binary protocol
        builder.setCommandFactory(new BinaryCommandFactory());
        // Connection timeout in milliseconds (default: )
        builder.setConnectTimeout(1000);
        // Reconnect to servers (default: true)
        builder.setEnableHealSession(true);
        // Delay until reconnect attempt in milliseconds (default: 2000)
        builder.setHealSessionInterval(2000);

        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            char action = prompt(in);
            System.out.println(action);
            if (action == '?') {
                menu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                db.createTable(getString(in, "Enter the table name"));
            } else if (action == 'D') {
                db.dropTable(getString(in, "Enter the table name"));
            } else if (action == '1') {
                String table = getString(in, "Enter the table name");
                RowData res = null;
                if (table.equals("users")) {
                    res = db.selectOneUser(getString(in, "Enter the userID"));
                } else if (table.equals("posts")) {
                    res = db.selectOnePost(getInt(in, "Enter the postID"));
                } else if (table.equals("comments")) {
                    res = db.selectOneComment(getInt(in, "Enter the commentID"));
                } else if (table.equals("votes")) {
                    System.out.println("result : " + db.selectVoteForPost(getInt(in, "Enter the postID"), getString(in, "Enter the userID")));
                    continue;
                }
                printRow(res);
            } else if (action == '*') {
                String table = getString(in, "Enter the table name");
                ArrayList<RowData> res = db.selectAll(table);
                if (res == null)
                    continue;
                printAll(res);
            } else if (action == '-') {
                int id = getInt(in, "Enter the row ID");
                int res = -1;
                String table = getString(in, "Enter the table name");
                if (id == -1)
                    continue;
                if (table.equals("users")) {
                    res = db.deleteUser(getString(in, "Enter the userID"));
                } else if (table.equals("posts")) {
                    res = db.deletePost(getInt(in, "Enter the postID"));
                } else if (table.equals("comments")) {
                    res = db.deleteComment(getInt(in, "Enter the commentID"));
                } else if (table.equals("votes")) {
                    res = db.deleteVote(getInt(in, "Enter the postID"), getString(in, "Enter the userID"));
                }
                System.out.println("  " + res + " rows deleted");
            } else if (action == '+') {
                String table = getString(in, "Enter the table name");
                int res = 0;
                if (table.equals("users")) {
                    User user = new User(getString(in, "Enter the userID"), getString(in, "Enter the Name"), getString(in, "Enter the Email"), getString(in, "Enter the Role"), getString(in, "Enter the GI"), getString(in, "Enter the SO"), getString(in, "Enter the PICURL"));
                    res = db.insertUser(user);
                } else if (table.equals("posts")) {
                    res = db.insertPost(getString(in, "Enter the UserID"), getString(in, "Enter the Subject"), getString(in, "Enter the Message"), getString(in, "Enter the webURL"), getString(in, "Enter the fileURL"));
                } else if (table.equals("comments")) {
                    res = db.insertComment(getInt(in, "Enter the postID"),  getString(in, "Enter the UserID"), getString(in, "Enter the Text"), getString(in, "Enter the webURL"), getString(in, "Enter the fileURL"));
                } else if (table.equals("votes")) {
                    res = db.insertVote(getInt(in, "Enter the postID"), getString(in, "Enter the userID"), getInt(in, "Enter the value of the vote"));
                }
                
                System.out.println(res + " rows added");
            } else if (action == '~') {
                int res = 0;
                String table = getString(in, "Enter the table name");
                if (table.equals("users")) {
                    String userID = getString(in, "Enter the userID");
                    res = db.updateUser(getString(in, "Enter the new Name"), getString(in, "Enter the new Email"), getString(in, "Enter the new Role"), getString(in, "Enter the new GI"), getString(in, "Enter the new SO"), userID);
                } else if (table.equals("posts")) {
                    Integer postID = getInt(in, "Enter the postID");
                    res = db.updatePost(postID, getString(in, "Enter the new Subject"), getString(in, "Enter the new Message"), getString(in, "Enter the new webURL"), getString(in, "Enter the new fileURL"));
                } else if (table.equals("comments")) {
                    Integer commentID = getInt(in, "Enter the commentID");
                    res = db.updateComment(commentID, getString(in, "Enter the new Text"), getString(in, "Enter the new webURL"), getString(in, "Enter the new fileURL"));
                } else if (table.equals("votes")) {
                    Integer postID = getInt(in, "Enter the postID associated with the vote");
                    String userID = getString(in, "Enter the userID associated with the vote");
                    res = db.updateVote(getInt(in, "Enter the new value of the vote"), postID, userID);
                }

                System.out.println("  " + res + " rows updated");
            } else if (action == '#') { // invalidate a post
                int res = db.deletePost(getInt(in, "Enter the postID"));
                if (res <= 0) {
                    System.out.println("  No post found");
                } else {
                    System.out.println(res + " rows deleted");
                }
            } else if (action == '$') { // invalidate a user
                int res = db.deleteUser(getString(in, "Enter the userID"));
                if (res <= 0) {
                    System.out.println("  No user found");
                } else {
                    System.out.println(res + " rows deleted");
                }
            } else if (action == '^') {
                try {
                    MemcachedClient mc = builder.build();
                    mc.flushAll();
                    System.out.println("LRU cache removed");
                } catch (Exception ioe) {
                    System.err.println("Couldn't create a connection to MemCachier: " +
                                        ioe.getMessage());
                }
            }
        }
        // Always remember to disconnect from the database when the program 
        // exits
        db.disconnect();
    }

    /**
     * Print all of the rows in the result set
     * @param res for the result set
     */
    private static void printAll(ArrayList<RowData> res) {
        if (res.size() == 0) {
            System.out.println("  No rows found");
            return;
        }
        System.out.println("  Current Database Contents");
        System.out.println("  -------------------------");
        
        RowData rd = res.get(0);
        if (rd instanceof Post) {
            System.out.println(String.format( "%-6d %-6d %-30s %-30s", "PostID", "UserID", "Subject", "Message"));
        } else if (rd instanceof User) {
            System.out.println(String.format( "%-30s %-15s %-25s %-15s %-17s %-15s %-50s", "UserID", "Name", "Email", "Role", "GI", "SO", "PICURL"));
        } else if (rd instanceof Comment) {
            System.out.println(String.format( "%-6d %-6d %-30s %-30s", "ID", "PostID", "UserID", "Text"));
        } else if (rd instanceof Vote){
            System.out.println(String.format( "%-6d %-30s %-6d", "PostID", "UserID", "VoteType"));
        }
        for (RowData obj : res) {
            System.out.println(obj);
        }
    }

    /**
     * Print the row in the result set
     * @param res for the result set
     */
    private static void printRow(RowData res) {
        if (res == null) {
            System.out.println("  No rows found");
            return;
        }
        System.out.println("  Current Database Contents");
        System.out.println("  -------------------------");
        if (res instanceof Post) {
            System.out.println(String.format( "%-6d %-6d %-30s %-30s", "PostID", "UserID", "Subject", "Message"));
        } else if (res instanceof User) {
            System.out.println(String.format( "%-30s %-15s %-25s %-15s %-17s %-15s %-50s", "UserID", "Name", "Email", "Role", "GI", "SO", "PICURL"));
        } else if (res instanceof Comment) {
            System.out.println(String.format( "%-6d %-6d %-30s %-30s", "ID", "PostID", "UserID", "Text"));
        } else if (res instanceof Vote){
            System.out.println(String.format( "%-6d %-30s %-6d", "PostID", "UserID", "VoteType"));
        }
        System.out.println(res);
    }
}