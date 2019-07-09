package in.nimbo.entity;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.Objects;

public class News {
    //foreign key to parent
    private int id;
    private SyndEntry entry;
    private String text;

    public News(SyndEntry entry, String text) {
        this.entry = entry;
        this.text = text;
    }

    public News() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SyndEntry getEntry() {
        return entry;
    }

    public void setEntry(SyndEntry entry) {
        this.entry = entry;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return ", title: " + entry.getTitle() +
                ", link: " + entry.getLink() +
                ", lastUpdate: " + entry.getPublishedDate() +
                ", author: " + entry.getAuthor() +
                ", text: " + text;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof News))
            return false;
        News news = (News) obj;

        return Objects.equals(text, news.getText()) &&
                Objects.equals(entry.getTitle(), news.getEntry().getTitle()) &&
                Objects.equals(entry.getLink(), news.getEntry().getLink()) &&
                Objects.equals(entry.getPublishedDate(), news.getEntry().getPublishedDate()) &&
                Objects.equals(entry.getAuthor(), news.getEntry().getAuthor()) &&
                checkDescription(entry.getDescription(), news.getEntry().getDescription());
    }

    public boolean checkDescription(SyndContent d1, SyndContent d2) {
        if (d1 == null && d2 == null)
            return true;
        else if (d1 == null)
            return false;
        else if (d2 == null)
            return false;
        else if (Objects.equals(d1.getValue(), d2.getValue()))
            return true;
        return false;
    }
}
