package in.nimbo;

import com.rometools.rome.feed.synd.SyndEntry;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import in.nimbo.entity.News;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Utility {
    public static String extraxtText(String link){
        String article = null;
        try {
            //logger.info("Extracting news text...");
            String encode;
            if (!link.contains("%")) {
                int index = link.lastIndexOf('/') + 1;
                encode = link.substring(0, index) + URLEncoder.encode(link.substring(index), "UTF-8");
            } else {
                encode = link;
            }
            URL url = new URL(encode);
            article = ArticleExtractor.INSTANCE.getText(url);
            //logger.info("News text extracted successfully.");
        } catch (MalformedURLException e) {
            //logger.error("Exception thrown for invalid url!", e);
        } catch (BoilerpipeProcessingException e) {
            //logger.error("Exception thrown during scraping process!", e);
        } catch (UnsupportedEncodingException e) {
            //logger.error("Exception thrown for invalid encode!", e);
        }
        return article;
    }
}
