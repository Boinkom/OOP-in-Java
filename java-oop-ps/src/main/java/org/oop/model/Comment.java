package org.oop.model;

import java.util.Date;

public class Comment {
    private String username;
    private String content;
    private Date date;

    public Comment(String username, String content) {
        this.username = username;
        this.content = content;
        this.date = new Date();
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {
        return date;
    }
}