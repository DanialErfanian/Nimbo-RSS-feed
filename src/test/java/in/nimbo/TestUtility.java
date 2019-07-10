package in.nimbo;

import com.rometools.rome.feed.synd.SyndFeedImpl;
import in.nimbo.dao.ChannelDao;
import in.nimbo.dao.ChannelDaoImpl;
import in.nimbo.dao.FilterNews;
import in.nimbo.dao.NewsDaoImpl;
import in.nimbo.entity.Channel;
import org.junit.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static in.nimbo.Utility.parseNewsFilter;

public class TestUtility {
    private static App app;
    private static Connection connection;
    private static ChannelDao channelDao;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

    @BeforeClass
    public static void init() throws SQLException, ClassNotFoundException, IOException {
        DataSource dataSource = TestDataSource.init();
        channelDao = new ChannelDaoImpl(dataSource);
        NewsDaoImpl newsDao = new NewsDaoImpl(dataSource);
        app = new App(newsDao, channelDao);
        connection = dataSource.getConnection();
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
    public void parseNewFilterTest() throws ParseException {
        FilterNews filter = new FilterNews();
        Assert.assertEquals(parseNewsFilter("", app), filter);
        Assert.assertNull(parseNewsFilter("-ti", app));
        Assert.assertNull(parseNewsFilter("-title", app));
        Assert.assertNull(parseNewsFilter("-title ", app));

        filter.setTitle("some title");
        Assert.assertEquals(parseNewsFilter("-title some title", app), filter);

        filter.setTitle("title");
        filter.setText("به سوی تو");
        String args = "-title " + filter.getTitle() + " -text " + filter.getText();
        Assert.assertEquals(parseNewsFilter(args, app), filter);

        Date start = new Date();
        filter.setStart(new Timestamp(formatter.parse(formatter.format(start)).getTime()));
        Assert.assertNull(parseNewsFilter(args + " -start some wrong date", app));
        args += " -start " + formatter.format(start);
        Assert.assertEquals(parseNewsFilter(args, app), filter);

        Date end = new Date();
        filter.setEnd(new Timestamp(formatter.parse(formatter.format(end)).getTime()));
        Assert.assertNull(parseNewsFilter(args + " -end some wrong date", app));
        args += " -end " + formatter.format(end);
        Assert.assertEquals(parseNewsFilter(args, app), filter);

        Channel channel = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        Assert.assertTrue(channelDao.add(channel));

        channel.setId(1);
        filter.setChannel(channel);
        Assert.assertNull(parseNewsFilter(args + " -channel " + "some wrong url", app));
        args += " -channel " + channel.getRSSUrl();
        Assert.assertEquals(parseNewsFilter(args, app), filter);
        Assert.assertEquals(Objects.requireNonNull(parseNewsFilter(args, app)).toString(), filter.toString());
        Assert.assertNotEquals(parseNewsFilter(args, app), null);
    }

    @Test
    public void syndFeedToChannelTest() {
        SyndFeedImpl syndFeed = new SyndFeedImpl();
        String RSSUrl = "http://danial.org :{";
        syndFeed.setLink("https://danial.ir");
        syndFeed.setTitle("danial");
        syndFeed.setDescription("Easy Description");
        syndFeed.setPublishedDate(new Date());
        Channel channel = Utility.syndFeedToChannel(syndFeed, RSSUrl);
        Assert.assertEquals(channel.getRSSUrl(), RSSUrl);
        Assert.assertEquals(channel.getLink(), syndFeed.getLink());
        Assert.assertEquals(channel.getTitle(), syndFeed.getTitle());
        Assert.assertEquals(channel.getDescription(), syndFeed.getDescription());
        Assert.assertEquals(channel.getLastUpdate(), new Timestamp(syndFeed.getPublishedDate().getTime()));

    }

    @Test
    public void parseDate() throws ParseException {
        Assert.assertNull(Utility.parseDate("something wrong"));
        Date date = new Date();
        date = formatter.parse(formatter.format(date));
        Assert.assertEquals(Utility.parseDate(formatter.format(date)), new Timestamp(date.getTime()));
    }
}
