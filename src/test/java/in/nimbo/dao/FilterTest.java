package in.nimbo.dao;

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

public class FilterTest {
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
    public void emptyFilterTest() {
        FilterNews filter = new FilterNews();
        News[] n = newsDao.search(filter);
        News[] ne = new News[1];
        ne[0] = news;
        Assert.assertArrayEquals(n, ne);
    }

    @Test
    public void titleFilterTest() {
        News news2 = new News();
        news2.setText("UpdateText");
        SyndEntry syndEntry = new SyndEntryImpl();
        syndEntry.setAuthor("infoUpdate@nimbo.in");
        syndEntry.setLink("nimboUpdate.in");
        syndEntry.setTitle("nimboUpdate");
        syndEntry.setDescription(new SyndContentImpl());
        news2.setEntry(syndEntry);
        news2.setId(1);
        newsDao.add(news2);
        FilterNews filter = new FilterNews();
        filter.setTitle("nim");
        News[] n = newsDao.search(filter);
        News[] ne = new News[1];
        ne[0] = news2;
        Assert.assertArrayEquals(n, ne);
    }

    @Test
    public void textFilterTest() {
        News news2 = new News();
        news2.setText("UpdateText");
        SyndEntry syndEntry = new SyndEntryImpl();
        syndEntry.setAuthor("infoUpdate@nimbo.in");
        syndEntry.setLink("nimboUpdate.in");
        syndEntry.setTitle("nimboUpdate");
        syndEntry.setDescription(new SyndContentImpl());
        news2.setEntry(syndEntry);
        news2.setId(1);
        newsDao.add(news2);
        FilterNews filter = new FilterNews();
        filter.setText("Up");
        News[] n = newsDao.search(filter);
        News[] ne = new News[1];
        ne[0] = news2;
        Assert.assertArrayEquals(n, ne);
    }

    @Test
    public void channelFilterTest() {
        FilterNews filter = new FilterNews();
        filter.setChannel(channel);
        News[] n = newsDao.search(filter);
        News[] ne = new News[1];
        ne[0] = news;
        Assert.assertArrayEquals(n, ne);
    }

    @Test
    public void startDateFilterTest() {
        FilterNews filter = new FilterNews();
        filter.setChannel(channel);
        News[] n = newsDao.search(filter);
        News[] ne = new News[1];
        ne[0] = news;
        Assert.assertArrayEquals(n, ne);
    }
}
