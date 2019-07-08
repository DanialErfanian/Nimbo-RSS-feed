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
        Channel channel2 = new Channel(-1, "http://tabnak.ir", "easy.ir", new Timestamp(new Date().getTime()), "some_Description", "some_title");
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
    public void testUpdate() {
        Channel channel1 = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        Channel channel2 = new Channel(-1, "http://tabnak.ir", "easy.ir", new Timestamp(new Date().getTime()), "some_Description", "some_title");
        Assert.assertTrue(channelDao.add(channel1));
        Assert.assertTrue(channelDao.add(channel2));
        channel1.setId(1);
        channel2.setId(2);
        channel1.setDescription("some other description");
        Assert.assertTrue(channelDao.update(channel1));
        List<Channel> channels = channelDao.getAllChannels();
        Assert.assertArrayEquals(channels.toArray(new Channel[0]), new Channel[]{channel1, channel2});
    }

    @Test

}
