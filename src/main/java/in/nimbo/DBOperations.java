package nimbo.in;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.URL;
import java.sql.*;

public class DBOperations {
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "RSSFeed";
    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection conn = null;
    private static Statement stmt = null;
    private static int tableId;

    public static void createDB() {
        try {
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL + "?useSsl=false&useUnicode=yes&characterEncoding=UTF-8", USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating database...");
            stmt = conn.createStatement();

            String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(sql);
            System.out.println("Database created successfully...");
        } catch (SQLException ignored) {
            System.out.println("Database was exist!");
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static void createTables() {
        try {
            conn = DriverManager.getConnection(DB_URL + DB_NAME + "?useSsl=false&useUnicode=yes&characterEncoding=UTF-8", USER, PASS);
            stmt = conn.createStatement();

            System.out.println("Creating table in given database...");

            System.out.println("creating RSSChannel table...");

            try {
                String sql = "CREATE TABLE IF NOT EXISTS RSSChannel " +
                        "(id int(11) NOT NULL AUTO_INCREMENT, " +
                        " RSSLink TEXT not NULL, " +
                        " Title TEXT, " +
                        " Link TEXT, " +
                        " Description TEXT, " +
                        " LastBuildDate DATE, " +
                        " PRIMARY KEY (id)) ENGINE=InnoDB CHARSET=utf8mb4;";
                stmt.executeUpdate(sql);

                stmt.executeUpdate("ALTER TABLE `RSSChannel` ADD INDEX(`RSSLink`)");
                System.out.println("table RSSChannel created.");

            } catch (SQLException e) {
                System.out.println("no change is needed");
            }

            System.out.println("creating News table...");
            try {
                String sql = "CREATE TABLE IF NOT EXISTS News " +
                        "(id int(11) NOT NULL AUTO_INCREMENT, " +
                        "Title TEXT, " +
                        "Link TEXT, " +
                        "Description TEXT null , " +
                        "Author TEXT, " +
                        "PublishedDate DATE, " +
                        "RSSLink int(11) NOT NULL, " +
                        "FOREIGN KEY (RSSLink) REFERENCES RSSChannel(id) ON UPDATE CASCADE ON DELETE RESTRICT, " +
                        "PRIMARY KEY (id)" +
                        ") ENGINE=InnoDB CHARSET=utf8mb4;";
                stmt.executeUpdate(sql);
                System.out.println("table News created.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("no change is needed");
            }
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static void RSSRead(String url) {
        try {
            System.out.println("Add data in RSSChannel table...");
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            try {
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO RSSChannel(RSSLink, Title, Link, Description, LastBuildDate) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                pstm.setString(1, url);
                pstm.setString(2, feed.getTitle());
                pstm.setString(3, feed.getLink());
                pstm.setString(4, feed.getDescription());
                pstm.setDate(5, new Date(feed.getPublishedDate().getTime()));
                pstm.executeUpdate();
                ResultSet rs = pstm.getGeneratedKeys();
                if (rs.next()) {
                    tableId = rs.getInt(1);
                }
                System.out.println("Add data in RSSChannel table finished.");
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }

            System.out.println("Add data in News table...");
            for (SyndEntry syndEntry : feed.getEntries()) {
                try {
                    PreparedStatement pstm = conn.prepareStatement("INSERT INTO News(Title, Link, Description, Author, PublishedDate, RSSLink) VALUES(?, ?, ?, ?, ?, ?)");
                    pstm.setString(1, syndEntry.getTitle());
                    pstm.setString(2, syndEntry.getLink());
                    if (syndEntry.getDescription() != null)
                        pstm.setString(3, syndEntry.getDescription().getValue());
                    else
                        pstm.setString(3, null);
                    pstm.setString(4, syndEntry.getAuthor());
                    pstm.setDate(5, new Date(syndEntry.getPublishedDate().getTime()));
                    pstm.setInt(6, tableId);
                    pstm.executeUpdate();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("ERROR: " + ex.getMessage());
                }
            }
            System.out.println("Add data in RSSChannel table finished.");
        } catch (Exception ex) {
            ex.printStackTrace();
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
        RSSRead("https://90tv.ir/rss/news");
        RSSRead("https://www.tabnak.ir/fa/rss/1");
    }
}