package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.dao.ChannelDao;
import in.nimbo.dao.FilterNews;
import in.nimbo.dao.NewsDao;
import in.nimbo.entity.Channel;
import in.nimbo.entity.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private NewsDao newsDao;
    private ChannelDao channelDao;
    private ScheduledExecutorService scheduledExecutorService;

    public App(NewsDao newsDao, ChannelDao channelDao) {
        this.newsDao = newsDao;
        this.channelDao = channelDao;
        scheduledExecutorService = new ScheduledThreadPoolExecutor(10);
        List<Channel> channels = this.getAllChannels();
        for (Channel c: channels) {
            scheduledExecutorService.scheduleWithFixedDelay(new RSSReadThread(channelDao, newsDao, c.getRSSUrl()),
                    0, 60, TimeUnit.SECONDS);
        }
    }

    public void addLink(String url) {
        scheduledExecutorService.scheduleWithFixedDelay(new RSSReadThread(channelDao, newsDao, url),
                0, 60, TimeUnit.SECONDS);
    }

    public News[] getNews(FilterNews filter) {
        return newsDao.search(filter);
    }

    public Channel getChannel(int id) {
        return channelDao.getChannel(id);
    }

    public Channel getChannel(String url) {
        return channelDao.getChannel(url);
    }

    public List<Channel> getAllChannels() {
        return channelDao.getAllChannels();
    }
}