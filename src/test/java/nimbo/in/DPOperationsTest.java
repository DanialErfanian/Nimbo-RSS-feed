package nimbo.in;

import org.junit.Assert;
import org.junit.Test;

import java.sql.*;

public class DPOperationsTest {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/";

    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "";
    private static final String DB_NAME = "RSSFeed";

    @Test
    public void createDBTest() throws ClassNotFoundException, SQLException {
        Connection conn;
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
        Assert.assertNotNull(conn);
    }
}
