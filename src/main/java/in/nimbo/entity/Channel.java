package in.nimbo.entity;

import java.sql.Timestamp;
import java.util.Objects;

public class Channel {
    private int id;
    private String title;
    private String RSSUrl;
    private String link;
    private Timestamp lastUpdate;
    private String description;

    public Channel(int id, String RSSUrl, String link, Timestamp lastUpdate, String description, String title) {
        this.id = id;
        this.RSSUrl = RSSUrl;
        this.link = link;
        this.lastUpdate = lastUpdate;
        this.description = description;
        this.title = title;
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

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Channel))
            return false;
        Channel channel = (Channel) obj;
        return this.id == channel.id &&
                Objects.equals(this.title, channel.title) &&
                Objects.equals(this.RSSUrl, channel.RSSUrl) &&
                Objects.equals(this.link, channel.link) &&
                Objects.equals(this.lastUpdate, channel.lastUpdate) &&
                Objects.equals(this.description, channel.description);
    }

    @Override
    public String toString() {
        return "id: " + id +
                ", title: " + title +
                ", RSSUrl: " + RSSUrl +
                ", link: " + link +
                ", lastUpdate: " + lastUpdate +
                ", description: " + description;
    }
}
