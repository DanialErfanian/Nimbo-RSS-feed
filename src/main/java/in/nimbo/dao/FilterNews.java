package in.nimbo.dao;

import in.nimbo.entity.Channel;

import java.sql.Timestamp;

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
}
    