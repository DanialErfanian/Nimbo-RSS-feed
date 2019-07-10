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

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private NewsDao newsDao;
    private ChannelDao channelDao;

    public App(NewsDao newsDao, ChannelDao channelDao) {
        this.newsDao = newsDao;
        this.channelDao = channelDao;
    }

    public void addLink(String url) {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            Channel channel = Utility.syndFeedToChannel(feed, url);
            if (channelDao.getChannel(url) == null) {
                channelDao.add(channel);
            } else {
                channelDao.update(channel);
            }
            for (SyndEntry s : feed.getEntries()) {
                News news = new News();
                news.setEntry(s);
                news.setText(Utility.extractText(s.getLink()));
                news.setId(channelDao.getChannel(url).getId());
                if (newsDao.getNews(s.getLink()) == null) {
                    newsDao.add(news);
                } else {
                    newsDao.update(news);
                }
            }
        } catch (FeedException | IllegalArgumentException | IOException e) {
            LOGGER.error("There was a problem on loading URL", e);
        }
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