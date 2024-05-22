package com.example.sky.bookmark;
import java.io.Serializable;
public class BookmarkItem {
    private String url;
    private String event;

    public BookmarkItem( String url, String event) {
        this.url = url;
        this.event = event;
    }

    public void setUrl(String url) { this.url = url;}

    public String getUrl() {
        return url;
    }

    public void setEvent(String event) {this.event = event;}
    public String getEvent() {
        return event;
    }
}
