package in.nimbo;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class DBOperations {
    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "RSSFeed";
    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection conn;
    private static Statement stmt;

    private static final Logger logger = LoggerFactory.getLogger(DBOperations.class);

    static {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL + "?useSsl=false&useUnicode=yes&characterEncoding=UTF-8", USER, PASS);
            stmt = conn.createStatement();
            createDB();
            conn = DriverManager.getConnection(DB_URL + DB_NAME + "?useSsl=false&useUnicode=yes&characterEncoding=UTF-8", USER, PASS);
            stmt = conn.createStatement();
            logger.info("connection reset");
            createTables();
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("creating connection failed", e);
        }
    }

    static void createDB() throws SQLException {
        logger.info("Creating DB...");
        String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
        stmt.executeUpdate(sql);
        logger.info("DB created successfully.");
    }

    static void createTables() {
        try {
            logger.info("Creating table in given DB...");
            logger.info("Creating RSSChannel table...");
            createRSSChannelTable();
            logger.info("Creating News table...");
            createNewsTable();
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
    }

    private static void createNewsTable() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS News " +
                    "(id int NOT NULL AUTO_INCREMENT, " +
                    "Title TEXT, " +
                    "Link TEXT, " +
                    "Description TEXT, " +
                    "Author TEXT, " +
                    "PublishedDate DATETIME, " +
                    "RSSLink int(11) NOT NULL, " +
                    "FOREIGN KEY (RSSLink) REFERENCES RSSChannel(id) ON UPDATE CASCADE ON DELETE RESTRICT, " +
                    "PRIMARY KEY (id)" +
                    ") ENGINE=InnoDB CHARSET=utf8mb4;";
            stmt.executeUpdate(sql);// TODO index on title field
            logger.info("News table created.");
        } catch (SQLException s) {
            logger.error("There was a problem on creating News table in DB!", s);
        }
    }

    private static void createRSSChannelTable() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS RSSChannel " +
                    "(id int NOT NULL AUTO_INCREMENT, " +
                    " RSSLink VARCHAR(600) UNIQUE, " +
                    " Title TEXT, " +
                    " Link TEXT, " +
                    " Description TEXT, " +
                    " LastBuildDate DATETIME, " +
                    " PRIMARY KEY (id)) ENGINE=InnoDB CHARSET=utf8mb4;";
            stmt.executeUpdate(sql);
            logger.info("RSSChannel table created.");
        } catch (SQLException s) {
            logger.error("There was a problem on creating RSSChannel table in DB!", s);
        }
    }

    static void RSSRead(String url) {
        try {
            logger.info("Add data in RSSChannel table...");
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT COUNT(*) FROM RSSChannel WHERE RSSLink = '" + url + "'");// TODO use prepareStatement
            int tableID;
            resultSet.next();
            if (resultSet.getInt(1) > 0)
                tableID = updateRSSChannel(feed, url);
            else
                tableID = insertIntoRSSChannel(feed, url);

            logger.info("Add data in News table...");
            Set<String> newsTitle = new HashSet<>();
            resultSet = conn.createStatement().executeQuery("SELECT Title FROM News");// TODO use DB search
            while (resultSet.next())
                newsTitle.add(resultSet.getString(1));
            for (SyndEntry syndEntry : feed.getEntries())
                if (newsTitle.contains(syndEntry.getTitle()))
                    updateNewsTable(syndEntry);
                else
                    insertIntoNewsTable(syndEntry, tableID);
        } catch (Exception e) {
            logger.error("There was a problem on loading URL", e);
        }
    }

    private static int getChannelID(String url) throws SQLException {
        PreparedStatement pstm = conn.prepareStatement("SELECT id FROM RSSChannel WHERE RSSLink = ?");
        pstm.setString(1, url);
        ResultSet rs = pstm.executeQuery();
        if (rs.next())
            return rs.getInt(1);
        logger.error("this line must be unreachable");
        return 0;
    }

    private static int updateRSSChannel(SyndFeed feed, String url) {
        try {
            logger.info("URL exist in DB.");
            logger.info("Updating URL information...");
            PreparedStatement pstm = conn.prepareStatement("UPDATE RSSChannel SET LastBuildDate = ? WHERE RSSLink = ?");
            pstm.setTimestamp(1, new Timestamp(feed.getPublishedDate().getTime()));
            pstm.setString(2, url);
            pstm.executeUpdate();
            logger.info("URL information updated.");
            return getChannelID(url);
        } catch (SQLException s) {
            logger.error("There was a problem on update RSSChannel table!", s);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
        logger.error("this line must be unreachable");
        return 0;
    }

    private static int insertIntoRSSChannel(SyndFeed feed, String url) {
        try {
            logger.info("Insert data into RSSChannel table...");
            PreparedStatement pstm = conn.prepareStatement("INSERT INTO RSSChannel(RSSLink, Title, Link, Description, LastBuildDate) VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstm.setString(1, url);
            pstm.setString(2, feed.getTitle());
            pstm.setString(3, feed.getLink());
            pstm.setString(4, feed.getDescription());
            pstm.setTimestamp(5, new Timestamp(feed.getPublishedDate().getTime()));
            pstm.executeUpdate();
            ResultSet rs = pstm.getGeneratedKeys();
            if (rs.next())
                return rs.getInt(1);
            logger.info("Data inserted into RSSChannel.");
        } catch (SQLException s) {
            logger.error("There was a problem on insert data into RSSChannel table!", s);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
        logger.error("this line must be unreachable");
        return 0;
    }

    private static void updateNewsTable(SyndEntry syndEntry) {
        try {
            logger.info("News exist in News table.");
            logger.info("Updating news information...");
            PreparedStatement ps = conn.prepareStatement("UPDATE News SET Link = ?, Description = ?, Author = ?, PublishedDate = ? WHERE Title = ?");
            ps.setString(1, syndEntry.getLink());
            ps.setString(2, extractNewsText(syndEntry.getLink()));
            ps.setString(3, syndEntry.getAuthor());
            ps.setTimestamp(4, new Timestamp(syndEntry.getPublishedDate().getTime()));
            ps.setString(5, syndEntry.getTitle());
            ps.executeUpdate();
            logger.info("News information updated.");
        } catch (SQLException s) {
            logger.error("There was a problem on update News table!", s);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
    }

    private static void insertIntoNewsTable(SyndEntry syndEntry, int tableID) {
        try {
            logger.info("Insert data into News table...");
            PreparedStatement pstm = conn.prepareStatement("INSERT INTO News(Title, Link, Description, Author, PublishedDate, RSSLink) VALUES(?, ?, ?, ?, ?, ?)");
            pstm.setString(1, syndEntry.getTitle());
            pstm.setString(2, syndEntry.getLink());
            pstm.setString(3, extractNewsText(syndEntry.getLink()));
            pstm.setString(4, syndEntry.getAuthor());
            pstm.setTimestamp(5, new Timestamp(syndEntry.getPublishedDate().getTime()));
            pstm.setInt(6, tableID);
            pstm.executeUpdate();
            logger.info("Data inserted into News table.");
        } catch (SQLException s) {
            logger.error("There was a problem on insert data into News table!", s);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
    }

    private static String extractNewsText(String link) throws UnsupportedEncodingException {
        String article = null;
        try {
            logger.info("Extracting news text...");
            String encode;
            if (!link.contains("%")) {
                int index = link.lastIndexOf('/') + 1;
                encode = link.substring(0, index) + URLEncoder.encode(link.substring(index), "UTF-8");
            } else {
                encode = link;
            }
            URL url = new URL(encode);
            article = ArticleExtractor.INSTANCE.getText(url);
            logger.info("News text extracted successfully.");
        } catch (MalformedURLException e) {
            logger.error("Exception thrown for invalid url!", e);
        } catch (BoilerpipeProcessingException e) {
            logger.error("Exception thrown during scraping process!", e);
        }
        return article;
    }

    static SyndEntry[] searchTitle(String title) {
        try {
            String query = String.format("SELECT * FROM News WHERE Title LIKE '%%%s%%'\n", title);
            ResultSet resultSet = conn.createStatement().executeQuery(query);
            return convert(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SyndEntry[0];
    }

    static SyndEntry[] search(String s) {
        try {
            String sql = String.format("SELECT * FROM News WHERE Description LIKE '%%%s%%'\n", s);
            ResultSet resultSet = conn.createStatement().executeQuery(sql);
            return convert(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new SyndEntry[0];
    }

    private static SyndEntry[] convert(ResultSet resultSet) throws SQLException {
        ArrayList<SyndEntry> list = new ArrayList<>();
        while (resultSet.next()) {
            SyndEntry syndEntry = new SyndEntryImpl();

            //syndEntry.;
            syndEntry.setLink(resultSet.getString("Link"));
            syndEntry.setTitle(resultSet.getString("Title"));
            SyndContent content = new SyndContentImpl();
            content.setValue(resultSet.getString("Description"));
            syndEntry.setDescription(content);
            syndEntry.setAuthor(resultSet.getString("Author"));
            syndEntry.setPublishedDate(resultSet.getDate("PublishedDate"));
            list.add(syndEntry);
        }
        return list.toArray(new SyndEntry[0]);
    }
}
