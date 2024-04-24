package com.serkan.kelimeapp;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private boolean isLoggedIn;
    private List<String> activeChannels;
    private List<String> pendingRequests;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = false;
        this.activeChannels = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public List<String> getActiveChannels() {
        return activeChannels;
    }

    public List<String> getPendingRequests() {
        return pendingRequests;
    }

    public void sendRequest(String username) {
        pendingRequests.add(username);
    }

    public void acceptRequest(String username) {
        activeChannels.add(username);
        pendingRequests.remove(username);
    }

    public void rejectRequest(String username) {
        pendingRequests.remove(username);
    }

}
