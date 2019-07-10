package in.nimbo.dao;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import in.nimbo.TestDataSource;
import in.nimbo.entity.Channel;
import in.nimbo.entity.News;
import org.junit.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class NewsDaoImplTest {
    private static ChannelDao channelDao;
    private static NewsDao newsDao;
    private static Connection connection;
    private static News news;
    private static Channel channel;

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
        channel = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        channelDao.add(channel);
        newsDao.add(news);
    }

    @Test
    public void getAndAddNewsTest() {
        News n = newsDao.getNews(news.getEntry().getLink());
        Assert.assertNotNull(n);
        Assert.assertEquals(n, news);
        Assert.assertEquals(n.toString(), news.toString());
    }

    @Test
    public void updateNewsTest() {
        News news2 = new News();
        news2.setText("UpdateText");
        SyndEntry syndEntry = new SyndEntryImpl();
        syndEntry.setAuthor("infoUpdate@nimbo.in");
        syndEntry.setLink("nimbo.in");
        syndEntry.setTitle("NimboUpdate");
        syndEntry.setDescription(new SyndContentImpl());
        news2.setEntry(syndEntry);
        newsDao.update(news2);
        News n = newsDao.getNews(news2.getEntry().getLink());
        Assert.assertNotNull(n);
        n.setId(1);
        Assert.assertEquals(n, news2);
        Assert.assertEquals(n.toString(), news2.toString());
    }

    @Test
    public void equalsTest() {
        Assert.assertFalse(news.equals(channel));
        Assert.assertTrue(news.checkDescription(null, null));
        Assert.assertFalse(news.checkDescription(null, news.getEntry().getDescription()));
        Assert.assertFalse(news.checkDescription(news.getEntry().getDescription(), null));
        SyndContent syndContent = new SyndContentImpl();
        syndContent.setValue("test");
        Assert.assertFalse(news.checkDescription(news.getEntry().getDescription(), syndContent));
    }
}
