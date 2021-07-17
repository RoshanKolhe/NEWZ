package com.Newz.newz;

public class News {
    String id;
    String title;
    String short_news;
    String detail_news;
    String news_link;
    String original_article;
    String category;
    String photo;
    String date;
    String tags;
    String written;

    public News() {
    }

    public News(String id, String title, String short_news, String detail_news, String news_link, String original_article, String category, String photo,String date,String tags,String written) {
        this.id = id;
        this.title = title;
        this.short_news = short_news;
        this.detail_news = detail_news;
        this.news_link = news_link;
        this.original_article = original_article;
        this.category = category;
        this.photo = photo;
        this.date=date;
        this.tags=tags;
        this.written=written;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShort_news() {
        return short_news;
    }

    public void setShort_news(String short_news) {
        this.short_news = short_news;
    }

    public String getDetail_news() {
        return detail_news;
    }

    public void setDetail_news(String detail_news) {
        this.detail_news = detail_news;
    }

    public String getNews_link() {
        return news_link;
    }

    public void setNews_link(String news_link) {
        this.news_link = news_link;
    }

    public String getOriginal_article() {
        return original_article;
    }

    public void setOriginal_article(String original_article) {
        this.original_article = original_article;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getWritten() {
        return written;
    }

    public void setWritten(String written) {
        this.written = written;
    }
}
