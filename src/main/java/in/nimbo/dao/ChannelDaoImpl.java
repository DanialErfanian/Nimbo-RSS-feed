package in.nimbo.dao;

import in.nimbo.entity.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class ChannelDaoImpl implements ChannelDao {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "RSSFeed";
    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection conn;
    private static Statement stmt;

    private static final Logger logger = LoggerFactory.getLogger(ChannelDaoImpl.class);


    static {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL + DB_NAME + "?useSsl=false&useUnicode=yes&characterEncoding=UTF-8", USER, PASS);
            stmt = conn.createStatement();
            logger.info("connection reset");
        } catch (SQLException | ClassNotFoundException e) {
            logger.error("creating connection failed", e);
        }
    }

    @Override
    public Channel getChannel(String url) {
        try {
            PreparedStatement statement;
            statement = conn.prepareStatement("SELECT * FROM RSSChannel WHERE RSSLink = ?");
            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return toChannel(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        try {
            PreparedStatement statement;
            statement = conn.prepareStatement("SELECT * FROM RSSChannel WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return toChannel(resultSet);
        } catch (SQLException e) {
            logger.error("Error in converting resultSet to Channel Object ", e);
        }
        return null;
    }

    @Override
    public boolean update(Channel channel) {
        String sql = "UPDATE RSSChannel" +
                " SET RSSLink = ?, Title = ?, Link = ?, Description = ?, LastBuildDate = ?" +
                " WHERE id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(6, channel.getId());
            statement.setString(1, channel.getRSSUrl());
            statement.setString(2, channel.getTitle());
            statement.setString(3, channel.getLink());
            statement.setString(4, channel.getDescription());
            statement.setDate(5, new Date(channel.getLastUpdate().getTime()));
            return statement.execute();
        } catch (SQLException e) {
            logger.error("Error in update a row of RSSChannel table", e);
        }
        return false;
    }

    @Override
    public boolean add(Channel channel) {
        String sql = "INSERT into RSSChannel(id, RSSLink, Title, Link, Description, LastBuildDate) VALUES(?,?,?,?,?,?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, channel.getId());
            statement.setString(2, channel.getRSSUrl());
            statement.setString(3, channel.getTitle());
            statement.setString(4, channel.getLink());
            statement.setString(5, channel.getDescription());
            statement.setDate(6, new Date(channel.getLastUpdate().getTime()));
            return statement.execute();
        } catch (SQLException e) {
            logger.error("Error in insert into RSSChannel table", e);
        }
        return false;
    }
}
