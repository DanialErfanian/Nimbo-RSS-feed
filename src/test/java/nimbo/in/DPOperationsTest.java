package nimbo.in;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DPOperationsTest {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/";

    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "";
    private static final String DB_NAME = "RSSFeed";

    @Test
    public void createDBTest() throws ClassNotFoundException, SQLException {
        DBOperations.createDB();
        Connection conn;
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
        Assert.assertNotNull(conn);
    }

    @Test
    public void createTablesTestExist() throws ClassNotFoundException, SQLException {
        DBOperations.createDB();
        DBOperations.createTables();
        Connection conn;
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
        Assert.assertNotNull(conn);
        ResultSet resultSet = conn.createStatement().executeQuery("show tables");
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        while (resultSet.next())
            set1.add(resultSet.getString(1));
        set2.add("RSSChannel");
        set2.add("News");
        Assert.assertEquals(set1, set2);
    }

    @Test
    public void createTablesTestNewsFields() throws ClassNotFoundException, SQLException {
        DBOperations.createDB();
        DBOperations.createTables();
        Connection conn;
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
        Assert.assertNotNull(conn);
        ResultSet resultSet = conn.createStatement().executeQuery("DESCRIBE News");
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        while (resultSet.next())
            set1.add(resultSet.getString(1));
        set2.add("id");
        set2.add("Title");
        set2.add("Link");
        set2.add("Author");
        set2.add("PublishedDate");
        set2.add("RSSLink");
        Assert.assertEquals(set1, set2);
    }


    @Test
    public void createTablesTestRSSChannelFields() throws ClassNotFoundException, SQLException {
        DBOperations.createDB();
        DBOperations.createTables();
        Connection conn;
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
        Assert.assertNotNull(conn);
        ResultSet resultSet = conn.createStatement().executeQuery("DESCRIBE RSSChannel");
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        while (resultSet.next())
            set1.add(resultSet.getString(1));
        set2.add("RSSLink");
        set2.add("Title");
        set2.add("Link");
        set2.add("Description");
        set2.add("LastBuildDate");
        Assert.assertEquals(set1, set2);
    }
}
