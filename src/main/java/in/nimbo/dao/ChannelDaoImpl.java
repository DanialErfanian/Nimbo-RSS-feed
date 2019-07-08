package in.nimbo.dao;

import com.mysql.cj.jdbc.MysqlDataSource;
import in.nimbo.entity.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class ChannelDaoImpl implements ChannelDao {

    private static final Logger logger = LoggerFactory.getLogger(ChannelDaoImpl.class);
    private final DataSource source;

    public ChannelDaoImpl(DataSource source) {
        this.source = source;
    }

    public ChannelDaoImpl() {
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
    public Channel getChannel(String url) {
        Channel channel = null;
        try {
            PreparedStatement statement;
            Connection connection = source.getConnection();
            statement = connection.prepareStatement("SELECT * FROM RSSChannel WHERE RSSLink = ?");
            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                channel = toChannel(resultSet);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return channel;
    }

    private Channel toChannel(ResultSet resultSet) throws SQLException {
        int id;
        String RSSUrl, description, link, title;
        Date lastUpdate;
        id = resultSet.getInt("id");
        RSSUrl = resultSet.getString("RSSLink");
        description = resultSet.getString("Description");
        link = resultSet.getString("Link");
        lastUpdate = resultSet.getDate("LastBuildDate");
        title = resultSet.getString("Title");
        return new Channel(id, RSSUrl, link, lastUpdate, description, title);
    }

    @Override
    public Channel getChannel(int id) {
        Channel channel = null;
        try {
            PreparedStatement statement;
            Connection connection = source.getConnection();
            statement = connection.prepareStatement("SELECT * FROM RSSChannel WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                channel = toChannel(resultSet);
            connection.close();
        } catch (SQLException e) {
            logger.error("Error in converting resultSet to Channel Object ", e);
        }
        return channel;
    }

    @Override
    public boolean update(Channel channel) {
        String sql = "UPDATE RSSChannel" +
                " SET RSSLink = ?, Title = ?, Link = ?, Description = ?, LastBuildDate = ?" +
                " WHERE id = ?";
        boolean res = false;
        try {
            Connection connection = source.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(6, channel.getId());
            statement.setString(1, channel.getRSSUrl());
            statement.setString(2, channel.getTitle());
            statement.setString(3, channel.getLink());
            statement.setString(4, channel.getDescription());
            statement.setDate(5, new Date(channel.getLastUpdate().getTime()));
            res = statement.execute();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error in update a row of RSSChannel table", e);
        }
        return res;
    }

    @Override
    public boolean add(Channel channel) {
        String sql = "INSERT into RSSChannel(id, RSSLink, Title, Link, Description, LastBuildDate) VALUES(?,?,?,?,?,?)";
        boolean res = false;
        try {
            Connection connection = source.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, channel.getId());
            statement.setString(2, channel.getRSSUrl());
            statement.setString(3, channel.getTitle());
            statement.setString(4, channel.getLink());
            statement.setString(5, channel.getDescription());
            statement.setDate(6, new Date(channel.getLastUpdate().getTime()));
            res = statement.execute();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error in insert into RSSChannel table", e);
        }
        return res;
    }
}
