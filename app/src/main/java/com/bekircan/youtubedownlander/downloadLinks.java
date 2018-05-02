package com.bekircan.youtubedownlander;

public class downloadLinks {

    private String type;
    private String url;
    private String quality;
    private String size;

    public downloadLinks(String type, String url, String quality, String size) {
        this.type = type;
        this.url = url;
        this.quality = quality;
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
