package nimbo.in;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBOperations {
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/";
    private static final String DB_NAME = "RSSFeed";
    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "";


    public static void createDB() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating database...");
            stmt = conn.createStatement();

            String sql = "CREATE DATABASE " + DB_NAME;
            stmt.executeUpdate(sql);
            System.out.println("Database created successfully...");
        } catch (SQLException ignored) {
            System.out.println("Database was exist!");
        } catch (Exception se) {
            se.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void createTables() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);

            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
            System.out.println("Connected database successfully...");

            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();

            System.out.println("creating RSSChannel table...");

            try {
                String sql = "CREATE TABLE RSSChannel " +
                        "(RSSLink VARCHAR(100) not NULL, " +
                        " Title VARCHAR(100), " +
                        " Link VARCHAR(100), " +
                        " Description LONGTEXT, " +
                        " LastBuildDate DATETIME, " +
                        " PRIMARY KEY (RSSLink))";
                stmt.executeUpdate(sql);

                stmt.executeUpdate("ALTER TABLE `RSSChannel` ADD INDEX(`RSSLink`)");
                System.out.println("table RSSChannel created.");

            } catch (SQLException e) {
                System.out.println("no change is needed");
            }

            System.out.println("creating News table...");
            try {
                String sql = "CREATE TABLE News\n" +
                        "(" +
                        "    id            INTEGER not NULL auto_increment,\n" +
                        "    Title         VARCHAR(100),\n" +
                        "    Link          VARCHAR(100),\n" +
                        "    Author        VARCHAR(100),\n" +
                        "    PublishedDate DATETIME,\n" +
                        "    RSSLink     VARCHAR(100)     not null,\n" +
                        "    FOREIGN KEY (RSSLink) REFERENCES RSSChannel (RSSLink) ON UPDATE CASCADE ON DELETE RESTRICT,\n" +
                        "    PRIMARY KEY (id )\n" +
                        ")";
                stmt.executeUpdate(sql);
                System.out.println("table News created.");
            } catch (SQLException e) {
                System.out.println("no change is needed");
            }
        } catch (Exception se) {
            se.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException ignored) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        createDB();
        createTables();
    }
}
