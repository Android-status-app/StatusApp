package com.akumbhar20.status.models;

import com.google.firebase.Timestamp;

public class Video {
    String UserId, downloaduri, status, thumbnail, title;
    Timestamp timestamp;

    public Video() {

    }

    public Video(String userId, String downloaduri, String status, String thumbnail, String title, Timestamp timestamp) {
        UserId = userId;
        this.downloaduri = downloaduri;
        this.status = status;
        this.thumbnail = thumbnail;
        this.title = title;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getDownloaduri() {
        return downloaduri;
    }

    public void setDownloaduri(String downloaduri) {
        this.downloaduri = downloaduri;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
