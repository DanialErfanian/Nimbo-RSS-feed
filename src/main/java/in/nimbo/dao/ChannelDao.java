package in.nimbo.dao;

import in.nimbo.entity.Channel;

import java.util.List;

public interface ChannelDao {
    Channel getChannel(String url);

    Channel getChannel(int id);

    List<Channel> getAllChannels();

    boolean update(Channel channel);

    boolean add(Channel channel);
}
