package com.Newz.newz;

public class HistoryHelper {
    String id;
    String uniqueid;
    String date;
    String pointsEarned;
    String type;

    public HistoryHelper() {
    }

    public HistoryHelper(String id,String uniqueid,  String date, String pointsEarned, String type) {
        this.id = id;
        this.uniqueid = uniqueid;
        this.date = date;
        this.pointsEarned = pointsEarned;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public String getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(String pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
