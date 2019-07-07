package in.nimbo.dao;

import in.nimbo.entity.Channel;

public interface ChannelDao {
    Channel getChannel(String url);

    Channel getChannel(int id);

    void update(Channel Channel);

    void add(Channel Channel);
}
