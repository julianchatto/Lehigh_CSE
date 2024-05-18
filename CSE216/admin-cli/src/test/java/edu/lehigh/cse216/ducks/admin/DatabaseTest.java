package edu.lehigh.cse216.ducks.admin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DatabaseTest extends TestCase {
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DatabaseTest( String testName )
    {
        super( testName );
    }
    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DatabaseTest.class );
    }

    /**
     * Ensure that the database can be connected to
     */
    public void testDatabase() {
        Database db = Database.getDatabase("ruby.db.elephantsql.com", "5432", "ptqcpmqs", "T-yihKqKhCIg9NdrKfNT3uQA_zdhQRco");
        assertNotNull(db);
        db.disconnect();
    }
}
