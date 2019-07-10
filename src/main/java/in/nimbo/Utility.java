package in.nimbo;

import com.rometools.rome.feed.synd.SyndFeed;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import in.nimbo.dao.FilterNews;
import in.nimbo.entity.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class Utility {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);


    static String extractText(String link) {
        String article = null;
        try {
            LOGGER.info("Extracting news text...");
            String encode;
            if (!link.contains("%")) {
                int index = link.lastIndexOf('/') + 1;
                encode = link.substring(0, index) + URLEncoder.encode(link.substring(index), "UTF-8");
            } else {
                encode = link;
            }
            URL url = new URL(encode);
            article = ArticleExtractor.INSTANCE.getText(url);
            LOGGER.info("News text extracted successfully.");
        } catch (MalformedURLException e) {
            LOGGER.error("Exception thrown for invalid url!", e);
        } catch (BoilerpipeProcessingException e) {
            LOGGER.error("Exception thrown during scraping process!", e);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Exception thrown for invalid encode!", e);
        }
        return article;
    }

    static FilterNews parseNewsFilter(String args, App app) {
        String[] split = args.split("-");
        FilterNews filter = new FilterNews();
        for (String s : split) {
            s = s.trim();
            if (s.length() == 0)
                continue;
            String[] split1 = s.split("\\s+");
            if (split1.length < 2)
                return null;
            String key = split1[0];
            String value = s.substring(key.length() + 1);
            switch (key) {
                case "start":
                    Timestamp start = parseDate(value);
                    if (start == null)
                        return null;
                    else
                        filter.setStart(start);
                    break;
                case "end":
                    Timestamp end = parseDate(value);
                    if (end == null)
                        return null;
                    else
                        filter.setEnd(end);
                    break;
                case "title":
                    filter.setTitle(value);
                    break;
                case "text":
                    filter.setText(value);
                    break;
                case "channel":
                    Channel channel = app.getChannel(value);
                    if (channel == null)
                        return null;
                    else
                        filter.setChannel(channel);
            }
        }
        return filter;
    }

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

    public static Timestamp parseDate(String value) {
        try {
            return new Timestamp(formatter.parse(value).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    static Channel syndFeedToChannel(SyndFeed feed, String url) {
        Channel channel = new Channel();
        channel.setDescription(feed.getDescription());
        channel.setLink(feed.getLink());
        channel.setTitle(feed.getTitle());
        channel.setLastUpdate(new Timestamp(feed.getPublishedDate().getTime()));
        channel.setRSSUrl(url);
        return channel;
    }
}
