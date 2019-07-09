package in.nimbo;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Utility {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);


    public static String extraxtText(String link){
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
}
