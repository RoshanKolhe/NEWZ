package com.Newz.newz;

public class Tags {
    String id;
    String tagname;

    public Tags(String title) {
    }

    public Tags(String id, String tagname) {
        this.id = id;
        this.tagname = tagname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }
}
