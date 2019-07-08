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

public class ChannelDaoImplTest {
    static private ChannelDao channelDao;
    static private Connection connection;

    @BeforeClass
    public static void init() throws SQLException, ClassNotFoundException, IOException {
        DataSource dataSource = TestDataSource.init();
        channelDao = new ChannelDaoImpl(dataSource);
        connection = dataSource.getConnection();
    }

    @After
    public void closeConnection() throws SQLException {
        connection.close();
    }

    @Before
    public void clearDB() throws SQLException {
        connection.createStatement().executeUpdate("DELETE FROM RSSChannel");
        connection.createStatement().executeUpdate("DELETE FROM News");
    }

    @Test
    public void getChannelTest() {
        Channel channel = new Channel(-1, "http://google.com", "google.com", new Timestamp(new Date().getTime()), "Description", "title");
        Assert.assertTrue(channelDao.add(channel));
        channel = new Channel(1, "http://google.com", "google.com", channel.getLastUpdate(), "Description", "title");
        Assert.assertEquals(channelDao.getAllChannels().size(), 1);
        Assert.assertEquals(channel, channelDao.getChannel(1));
    }
}
