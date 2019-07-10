package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.dao.ChannelDao;
import in.nimbo.dao.NewsDao;
import in.nimbo.entity.Channel;
import in.nimbo.entity.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;

public class RSSReadThread implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private NewsDao newsDao;
    private ChannelDao channelDao;
    private String url;

    public RSSReadThread(ChannelDao channelDao, NewsDao newsDao, String url) {
        this.newsDao = newsDao;
        this.channelDao = channelDao;
        this.url = url;
    }

    @Override
    public void run() {
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
}
