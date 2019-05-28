package com.fighton;

public class User {
    private boolean isLoggedIn = false;
    private String id = "";
    boolean inGame = false;

    public User (String id) {
        this.id = id;
    }

    public void logIn(String id) {
        isLoggedIn = true;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
