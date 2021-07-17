package com.Newz.newz;

public class Bookmark {
    String id;
    String useremail;
    String newsid;
    String isBookmarked;

    public Bookmark() {
    }

    public Bookmark(String id, String useremail, String newsid, String isBookmarked) {
        this.id = id;
        this.useremail = useremail;
        this.newsid = newsid;
        this.isBookmarked = isBookmarked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getNewsid() {
        return newsid;
    }

    public void setNewsid(String newsid) {
        this.newsid = newsid;
    }

    public String getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(String isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
}
