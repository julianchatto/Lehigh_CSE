package edu.lehigh.cse216.ducks.backend;

/**
 * SimpleRequest provides a format for clients to present title and message 
 * strings to the server.
 * 
 * NB: since this will be created from JSON, all fields must be public, and we
 *     do not need a constructor.
 */
public class SimpleRequest {
        Integer PostID;
        Integer id;
        
        String UserID;
        String Name;
        String Email;
        String Role;
        String SO;
        String GI;
        String PICURL;

        Integer VoteType;

        String Subject;
        String Message;

        String webURL;
        String file;
        String fileName;
        
        String Text;

        String DataField;
}