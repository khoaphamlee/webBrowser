package com.example.sky.begin;


public class BeginItem {
    private String url;
    private String event;



    public BeginItem(String url, String event) {
        this.url = url;
        this.event = event;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }


}
