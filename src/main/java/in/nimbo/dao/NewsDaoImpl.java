package in.nimbo.dao;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import in.nimbo.Utility;
import in.nimbo.entity.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NewsDaoImpl implements NewsDao {
    private static final Logger logger = LoggerFactory.getLogger(NewsDao.class);
    private final DataSource source;

    public NewsDaoImpl(DataSource source) {
        this.source = source;
    }

    public NewsDaoImpl() {
        Properties props = new Properties();
        FileInputStream fis;
        MysqlDataSource mysqlDS = null;
        try {
            fis = new FileInputStream(new File("src/main/resources/db.properties"));
            props.load(fis);
            Class.forName(props.getProperty("MYSQL_DB_DRIVER_CLASS"));
            mysqlDS = new MysqlDataSource();
            mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
            mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
            mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.source = mysqlDS;
    }

    @Override
    public News[] search(FilterNews filter) {
        String sqlCommand = "SELECT  * FROM News WHERE ";
        ResultSet resultSet = null;
        if (filter.getChannel() != null) {
            sqlCommand += "RSSLink = " + filter.getChannel().getId();
        }
        if (filter.getTitle() != null) {
            sqlCommand += "Title LIKE '%" + filter.getTitle() + "%' AND ";
        }
        if (filter.getText() != null) {
            sqlCommand += "NewsText LIKE '%" + filter.getText() + "%' AND ";
        }
        if (filter.getStart() != null) {
            sqlCommand += "PublishedDate >= '" + new Timestamp(filter.getStart().getTime()) + "' AND";
        }
        if (filter.getEnd() != null) {
            sqlCommand += "PublishedDate <= '" + new Timestamp(filter.getEnd().getTime()) + "' AND";
        }
        try (Connection conn = source.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM News WHERE Link = ?");
            resultSet = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return convert(resultSet);
    }

    private News[] convert(ResultSet resultSet) {
        List<News> newsList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                SyndEntry newsSyndEntry = new SyndEntryImpl();
                News news = new News();
                newsSyndEntry.setLink(resultSet.getString("Link"));
                newsSyndEntry.setTitle(resultSet.getString("Title"));
                SyndContent content = new SyndContentImpl();
                content.setValue(resultSet.getString("Description"));
                newsSyndEntry.setDescription(content);
                newsSyndEntry.setAuthor(resultSet.getString("Author"));
                newsSyndEntry.setPublishedDate(resultSet.getDate("PublishedDate"));
                news.setEntry(newsSyndEntry);
                news.setText(resultSet.getString("NewsText"));
                newsList.add(news);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public News getNews(String url) {
        SyndEntry syndEntry = new SyndEntryImpl();
        try (Connection conn = source.getConnection()) {
            String text = null;
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM News WHERE Link = ?");
            ps.setString(1, url);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                syndEntry.setTitle(resultSet.getString("Title"));
                syndEntry.setLink(resultSet.getString("Link"));
                text = resultSet.getString("NewsText");
                SyndContent description = new SyndContentImpl();
                description.setValue(resultSet.getString("Description"));
                syndEntry.setDescription(description);
                syndEntry.setAuthor(resultSet.getString("Author"));
                syndEntry.setPublishedDate(resultSet.getTimestamp("PublishedDate"));
                return new News(syndEntry, text);
            }
        } catch (SQLException s) {
            logger.error("There was a problem on update News table!", s);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
        return null;
    }

    @Override
    public void update(News news) {
        try (Connection conn = source.getConnection()) {
            logger.info("News exist in News table.");
            logger.info("Updating news information...");
            PreparedStatement ps = conn.prepareStatement("UPDATE News SET Link = ?, NewsText = ?, Description = ?, Author = ?, PublishedDate = ? WHERE Title = ?");
            ps.setString(1, news.getEntry().getLink());
            ps.setString(2, Utility.extraxtText(news.getEntry().getLink()));
            if (news.getEntry().getDescription() != null)
                ps.setString(3, news.getEntry().getDescription().getValue());
            else
                ps.setString(3, null);
            ps.setString(4, news.getEntry().getAuthor());
            ps.setTimestamp(5, new Timestamp(news.getEntry().getPublishedDate().getTime()));
            ps.setString(6, news.getEntry().getTitle());
            ps.executeUpdate();
            logger.info("News information updated.");
        } catch (SQLException s) {
            logger.error("There was a problem on update News table!", s);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
    }

    @Override
    public void add(News news) {
        try (Connection conn = source.getConnection()) {
            logger.info("Insert data into News table...");
            PreparedStatement ps = conn.prepareStatement("INSERT INTO News(Title, Link, NewsText, Description, Author, PublishedDate, RSSLink) VALUES(?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, news.getEntry().getTitle());
            ps.setString(2, news.getEntry().getLink());
            ps.setString(3, Utility.extraxtText(news.getEntry().getLink()));
            if (news.getEntry().getDescription() != null)
                ps.setString(4, news.getEntry().getDescription().getValue());
            else
                ps.setString(4, null);
            ps.setString(5, news.getEntry().getAuthor());
            ps.setTimestamp(6, new Timestamp(news.getEntry().getPublishedDate().getTime()));
            ps.setInt(7, news.getId());
            ps.executeUpdate();
            logger.info("Data inserted into News table.");
        } catch (SQLException s) {
            logger.error("There was a problem on insert data into News table!", s);
        } catch (Exception e) {
            logger.error("ERROR: ", e);
        }
    }
}
