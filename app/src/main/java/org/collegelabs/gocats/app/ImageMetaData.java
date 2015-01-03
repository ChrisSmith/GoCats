package org.collegelabs.gocats.app;

/**
*/
public class ImageMetaData {
    public String author;
    public String url;
    public String title;
    public String permalink;
    public String id;

    public ImageMetaData(String url, String title, String author, String permalink, String id){
        this.author = author;
        this.url = url;
        this.title = title;
        this.permalink = permalink;
        this.id = id;
    }
}
