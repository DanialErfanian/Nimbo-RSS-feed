package in.nimbo;

import in.nimbo.dao.ChannelDao;
import in.nimbo.dao.ChannelDaoImpl;
import in.nimbo.entity.Channel;
import org.junit.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChannelDaoImplTest {
    static private ChannelDao channelDao;
    static private Connection connection;

    @BeforeClass
    public static void init() throws SQLException, ClassNotFoundException, IOException {
        DataSource dataSource = TestDataSource.init();
        channelDao = new ChannelDaoImpl(dataSource);
        connection = dataSource.getConnection();
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @Before
    public void clearDB() throws SQLException {
        connection.createStatement().executeUpdate("DELETE FROM RSSChannel");
        connection.createStatement().executeUpdate("DELETE FROM News");
        connection.createStatement().execute("ALTER TABLE News ALTER COLUMN id RESTART WITH 1");
        connection.createStatement().execute("ALTER TABLE RSSChannel ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    public void getChannelTest() {
        Channel channel = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        Assert.assertTrue(channelDao.add(channel));
        channel = new Channel(1, "http://google.com", "google.com", channel.getLastUpdate(), "Description", "title");
        Assert.assertEquals(channelDao.getAllChannels().size(), 1);
        Assert.assertEquals(channel, channelDao.getChannel(1));
    }

    @Test
    public void addRepeatedChanelLink() {
        Channel channel = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        Assert.assertTrue(channelDao.add(channel));
        Assert.assertFalse(channelDao.add(channel));
    }

    @Test
    public void getAllChannelTest() {
        Channel channel1 = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        Channel channel2 = new Channel(-1, "http://tabnak.ir", null, new Timestamp(new Date().getTime()), "some_Description", "some_title");
        Assert.assertTrue(channelDao.add(channel1));
        Assert.assertTrue(channelDao.add(channel2));
        channel1.setId(1);
        channel2.setId(2);
        List<Channel> channels = channelDao.getAllChannels();
        Assert.assertArrayEquals(channels.toArray(new Channel[0]), new Channel[]{channel1, channel2});
    }

    @Test
    public void getChannelByUrlTest() {
        Channel channel1 = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        Channel channel2 = new Channel(-1, "http://tabnak.ir", "easy.ir", new Timestamp(new Date().getTime()), "some_Description", "some_title");
        Assert.assertTrue(channelDao.add(channel1));
        Assert.assertTrue(channelDao.add(channel2));
        channel1.setId(1);
        channel2.setId(2);
        Assert.assertEquals(channelDao.getChannel(channel1.getRSSUrl()), channel1);
        Assert.assertNull(channelDao.getChannel("THIS IS NOT IN DB"));
    }

    @Test
    public void testUpdateWithRepeatedUrl() {
        Channel channel1 = new Channel();
        channel1.setId(-1);
        channel1.setRSSUrl("http://google.com");
        channel1.setLink("google.com");
        channel1.setLastUpdate(new Timestamp(new Date().getTime()));
        channel1.setDescription("description danial");
        channel1.setTitle("and another danial");

        Channel channelOrg = new Channel();
        channelOrg.setId(1);
        channelOrg.setRSSUrl("http://google.com");
        channelOrg.setLink("google.com");
        channelOrg.setLastUpdate(new Timestamp(new Date().getTime()));
        channelOrg.setDescription("description danial");
        channelOrg.setTitle("and another danial");

        Channel channel2 = new Channel(-1, "http://tabnak.ir", "easy.ir", new Timestamp(new Date().getTime()), "some_Description", "some_title");
        Assert.assertTrue(channelDao.add(channel1));
        Assert.assertTrue(channelDao.add(channel2));

        channel1.setId(1);
        channel2.setId(2);

        channel1.setDescription("some other description");
        channel1.setLink("some other link");
        channel1.setRSSUrl("http://tabnak.ir");
        channel1.setTitle("some other title");
        channel1.setLastUpdate(new Timestamp(new Random().nextLong()));
        Assert.assertFalse(channelDao.update(channel1));
        List<Channel> channels = channelDao.getAllChannels();
        Assert.assertEquals(channels.size(), 2);
        Assert.assertArrayEquals(channels.toArray(new Channel[0]), new Channel[]{channelOrg, channel2});
    }

    @Test
    public void testUpdate() {
        Channel channel1 = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        Channel channel2 = new Channel(-1, "http://tabnak.ir", "easy.ir", new Timestamp(new Date().getTime()), "some_Description", "some_title");
        Assert.assertTrue(channelDao.add(channel1));
        Assert.assertTrue(channelDao.add(channel2));
        channel1.setId(1);
        channel2.setId(2);
        channel1.setDescription("some other description");
        channel1.setLink("some other link");
        channel1.setRSSUrl("some other rss url");
        channel1.setTitle("some other title");
        channel1.setLastUpdate(new Timestamp(new Random().nextLong()));
        Assert.assertTrue(channelDao.update(channel1));
        List<Channel> channels = channelDao.getAllChannels();
        Assert.assertArrayEquals(channels.toArray(new Channel[0]), new Channel[]{channel1, channel2});
    }

    @Test
    public void testGetAllChannels() {
        Channel channel = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        Assert.assertTrue(channelDao.add(channel));
        Assert.assertFalse(channelDao.add(channel));
        channel.setId(1);
        Assert.assertEquals(channelDao.getAllChannels().size(), 1);
        Assert.assertEquals(channelDao.getAllChannels().get(0).toString(), channel.toString());
        Assert.assertNotEquals(channelDao.getChannel(2), channel);
        Assert.assertNotEquals(channel, channelDao.getChannel(2));
        Assert.assertNull(channelDao.getChannel(2));
    }

}
