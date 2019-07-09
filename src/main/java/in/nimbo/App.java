package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.dao.ChannelDao;
import in.nimbo.dao.FilterNews;
import in.nimbo.dao.NewsDao;
import in.nimbo.entity.Channel;
import in.nimbo.entity.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.Timestamp;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private NewsDao newsDao;
    private ChannelDao channelDao;

    public App(NewsDao newsDao, ChannelDao channelDao) {
        this.newsDao = newsDao;
        this.channelDao = channelDao;
    }

    public void addLink(String url) {
        Channel channel = new Channel();
        SyndFeed feed = null;
        try {
            feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            channel.setRSSUrl(url);
            channel.setDescription(feed.getDescription());
            channel.setLink(feed.getLink());
            channel.setTitle(feed.getTitle());
            channel.setLastUpdate(new Timestamp(feed.getPublishedDate().getTime()));
        } catch (Exception e) {
            LOGGER.error("There was a problem on loading URL", e);
        }
        if (channelDao.getChannel(url) == null) {
            channelDao.add(channel);
        } else {
            channelDao.update(channel);
        }
        for (SyndEntry s: feed.getEntries()) {
            News news = new News();
            news.setEntry(s);
            news.setText(Utility.extraxtText(s.getLink()));
            news.setId(channelDao.getChannel(url).getId());
            if(newsDao.getNews(s.getLink()) == null) {
                newsDao.add(news);
            }
            else {
                newsDao.update(news);
            }
        }
    }

    public News[] getNews(FilterNews filter) {
        return newsDao.search(filter);
    }
}
