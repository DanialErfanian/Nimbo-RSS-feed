package in.nimbo.dao;

import in.nimbo.entity.Channel;

public interface ChannelDao {
    Channel getChannel(String url);

    Channel getChannel(int id);

    boolean update(Channel channel);

    boolean add(Channel channel);
}
