package com.example.administrator.multinotes;

import java.io.Serializable;

public class Note implements Serializable {

    private String title;
    private String date;
    private String description;

    public Note() {
        this.title = null;
        this.date = null;
        this.description = null;
    }

    public Note(String title, String date, String description) {
        this.title = title;
        this.date = date;
        this.description = description;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return '{' + "title: " + title + ", date: " + date + ", description: " + description + '}';
    }
}
