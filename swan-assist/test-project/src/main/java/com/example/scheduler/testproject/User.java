package com.example.scheduler.testproject;

public class User {

    private final long id;
    private final String name;
    private final String date;

    public User(long id, String name, String date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

}
