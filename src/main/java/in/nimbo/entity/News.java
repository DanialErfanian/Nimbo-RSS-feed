package in.nimbo.entity;

import com.rometools.rome.feed.synd.SyndEntry;

public class News {
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
}
