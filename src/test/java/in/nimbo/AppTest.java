package in.nimbo;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import in.nimbo.dao.*;
import in.nimbo.entity.Channel;
import in.nimbo.entity.News;
import org.junit.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class AppTest {
    private static ChannelDao channelDao;
    private static NewsDao newsDao;
    private static Connection connection;
    private static News news;
    private static Channel channel;
    private static App app;

    @BeforeClass
    public static void init() throws SQLException, ClassNotFoundException, IOException {
        DataSource dataSource = TestDataSource.init();
        channelDao = new ChannelDaoImpl(dataSource);
        newsDao = new NewsDaoImpl(dataSource);
        connection = dataSource.getConnection();
        news = new News();
        news.setId(1);
        news.setText("NewsAddTest");
        SyndEntry syndEntry = new SyndEntryImpl();
        syndEntry.setAuthor("info@nimbo.in");
        syndEntry.setLink("nimbo.in");
        syndEntry.setTitle("Nimbo");
        syndEntry.setDescription(new SyndContentImpl());
        news.setEntry(syndEntry);
        app = new App(newsDao, channelDao);
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @Before
    public void clearDB() throws SQLException {
        connection.createStatement().executeUpdate("DELETE FROM News");
        connection.createStatement().executeUpdate("DELETE FROM RSSChannel");
        connection.createStatement().execute("ALTER TABLE News ALTER COLUMN id RESTART WITH 1");
        connection.createStatement().execute("ALTER TABLE RSSChannel ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    public void getNewsTest() {
        channel = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        channelDao.add(channel);
        newsDao.add(news);
        News[] n = app.getNews(null);
        News[] ne = new News[1];
        ne[0] = news;
        Assert.assertArrayEquals(n, ne);
    }
}
