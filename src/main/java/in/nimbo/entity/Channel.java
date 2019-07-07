package in.nimbo.entity;

import java.util.Date;

public class Channel {
    private int id;
    private String RSSUrl;
    private String link;
    private Date lastUpdate;
    private String description;

    public Channel(int id, String RSSUrl, String link, Date lastUpdate, String description) {
        this.id = id;
        this.RSSUrl = RSSUrl;
        this.link = link;
        this.lastUpdate = lastUpdate;
        this.description = description;
    }

    public Channel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRSSUrl() {
        return RSSUrl;
    }

    public void setRSSUrl(String RSSUrl) {
        this.RSSUrl = RSSUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
