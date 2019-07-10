package in.nimbo.dao;

import in.nimbo.entity.Channel;

import java.sql.Timestamp;
import java.util.Objects;

public class FilterNews {
    private Timestamp start, end;
    private String title, text;
    private Channel channel;

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isEmpty() {
        return start == null && end == null && title == null && text == null && channel == null;
    }

    @Override
    public String toString() {
        return "title: " + this.title +
                ", text: " + this.text +
                ", start: " + this.start +
                ", end: " + this.end +
                ", channel: " + channel;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FilterNews))
            return false;
        FilterNews that = (FilterNews) o;
        return Objects.equals(start, that.start) &&
                Objects.equals(end, that.end) &&
                Objects.equals(title, that.title) &&
                Objects.equals(text, that.text) &&
                Objects.equals(channel, that.channel);
    }
}
    