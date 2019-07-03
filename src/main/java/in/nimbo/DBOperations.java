package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static Logger logger;

    public static void init() {
        logger = LoggerFactory.getLogger(DBOperations.class);
    }

    public static void createDB() {
        try {
            Class.forName(JDBC_DRIVER);

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL + "?useSsl=false&useUnicode=yes&characterEncoding=UTF-8", USER, PASS);

            //STEP 4: Execute a query
            stmt = conn.createStatement();
            logger.info("Creating DB...");
            String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(sql);
            logger.info("DB created successfully.");
        } catch (SQLException s) {
            logger.error("There was a problem on creating DB!", s);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
    }

    public static void createTables() {
        try {
            logger.info("Connecting to DB...");
            conn = DriverManager.getConnection(DB_URL + DB_NAME + "?useSsl=false&useUnicode=yes&characterEncoding=UTF-8", USER, PASS);
            stmt = conn.createStatement();
            logger.info("Connected to DB.");


            logger.info("Creating table in given DB...");
            logger.info("Creating RSSChannel table...");
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
                logger.info("RSSChannel table created.");
            } catch (SQLException s) {
                logger.error("There was a problem on creating RSSChannel table in DB!", s);
            }

            logger.info("Creating News table...");
            try {
                String sql = "CREATE TABLE IF NOT EXISTS News " +
                        "(id int(11) NOT NULL AUTO_INCREMENT, " +
                        "Title TEXT, " +
                        "Link TEXT, " +
                        "NewsText TEXT , " +
                        "Description TEXT null , " +
                        "Author TEXT, " +
                        "PublishedDate DATE, " +
                        "RSSLink int(11) NOT NULL, " +
                        "FOREIGN KEY (RSSLink) REFERENCES RSSChannel(id) ON UPDATE CASCADE ON DELETE RESTRICT, " +
                        "PRIMARY KEY (id)" +
                        ") ENGINE=InnoDB CHARSET=utf8mb4;";
                stmt.executeUpdate(sql);
                logger.info("News table created.");
            } catch (SQLException s) {
                logger.error("There was a problem on creating News table in DB!", s);
            }
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
    }

    public static void RSSRead(String url) {
        try {
            logger.info("Add data in RSSChannel table...");
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            try {
                Set<String> RSSLinksInTable = new HashSet<>();
                ResultSet resultSet = conn.createStatement().executeQuery("SELECT RSSLink FROM RSSChannel");
                while (resultSet.next()) {
                    RSSLinksInTable.add(resultSet.getString(1));
                }
                if (RSSLinksInTable.contains(url)) {
                    logger.info("URL exist in DB.");
                    logger.info("Updating URL information...");
                    PreparedStatement pstm = conn.prepareStatement("UPDATE RSSChannel SET LastBuildDate = ? WHERE RSSLink = ?");
                    pstm.setDate(1, new Date(feed.getPublishedDate().getTime()));
                    pstm.setString(2, url);
                    pstm.executeUpdate();
                    logger.info("URL information updated.");

                    pstm = conn.prepareStatement("SELECT id FROM RSSChannel WHERE RSSLink = ?");
                    pstm.setString(1, url);
                    ResultSet rs = pstm.executeQuery();
                    if (rs.next()) {
                        tableId = rs.getInt(1);
                    }
                } else {
                    logger.info("Insert data into RSSChannel table...");
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
                    logger.info("Data inserted into RSSChannel.");
                }
            } catch (SQLException s) {
                logger.error("There was a problem on insert data into RSSChannel table!", s);
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }

            logger.info("Add data in News table...");

            Set<String> newsTitle = new HashSet<>();
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT Title FROM News");
            while (resultSet.next()) {
                newsTitle.add(resultSet.getString(1));
            }
            try {
                for (SyndEntry syndEntry : feed.getEntries()) {

                    boolean repeatedNews = false;
                    if (newsTitle.contains(syndEntry.getTitle())) {
                        logger.info("News exist in News table.");
                        logger.info("Updating news information...");
                        /*int id = 0;
                        PreparedStatement pstm = conn.prepareStatement("SELECT id FROM News WHERE Title = ?");
                        pstm.setString(1, syndEntry.getTitle());
                        ResultSet rs = pstm.executeQuery();
                        if (rs.next()) {
                            id = rs.getInt(1);
                        }
                        if (id == tableId) {*/
                        repeatedNews = true;
                        PreparedStatement pstm = conn.prepareStatement("UPDATE News SET Link = ?, NewsText = ?, Description = ?, Author = ?, PublishedDate = ? WHERE Title = ?");
                        pstm.setString(1, syndEntry.getLink());
                        pstm.setString(2, extractNewsText(syndEntry.getLink()));
                        pstm.setString(3, syndEntry.getDescription().getValue());
                        pstm.setString(4, syndEntry.getAuthor());
                        pstm.setDate(5, new Date(syndEntry.getPublishedDate().getTime()));
                        pstm.setString(6, syndEntry.getTitle());
                        //pstm.setInt(7, tableId);
                        pstm.executeUpdate();
                        //}
                        logger.info("News information updated.");
                    }
                    if (!repeatedNews) {
                        logger.info("Insert data into News table...");
                        PreparedStatement pstm = conn.prepareStatement("INSERT INTO News(Title, Link, NewsText, Description, Author, PublishedDate, RSSLink) VALUES(?, ?, ?, ?, ?, ?, ?)");
                        pstm.setString(1, syndEntry.getTitle());
                        pstm.setString(2, syndEntry.getLink());
                        pstm.setString(3, extractNewsText(syndEntry.getLink()));
                        if (syndEntry.getDescription() != null)
                            pstm.setString(4, syndEntry.getDescription().getValue());
                        else
                            pstm.setString(4, null);
                        pstm.setString(5, syndEntry.getAuthor());
                        pstm.setDate(6, new Date(syndEntry.getPublishedDate().getTime()));
                        pstm.setInt(7, tableId);
                        pstm.executeUpdate();
                        logger.info("Data inserted into News table.");
                    }

                }
            } catch (SQLException s) {
                logger.error("There was a problem on insert data into News table!", s);

            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("There was a problem on loading URL", e);
        }
    }

    public static String extractNewsText(String link) {
        String article = null;
        try {
            logger.info("Extracting news text...");
            URL url = new URL(link);
            article = ArticleExtractor.INSTANCE.getText(url);
            logger.info("News text extracted successfully.");

        } catch (MalformedURLException e) {
            logger.error("Exception thrown for invalid url!" + e);
        } catch (BoilerpipeProcessingException e) {
            logger.error("Exception thrown during scraping process!" + e);
        }
        return article;
    }

    public static void main(String[] args) {
        init();
        createDB();
        createTables();
        RSSRead("https://90tv.ir/rss/news");
        //RSSRead("https://www.tabnak.ir/fa/rss/1");
    }
}