package com.seminar.rust;

import java.util.HashMap;
import java.util.List;

public class Map {
    private static final HashMap<String, User> users = new HashMap<>();
    public static synchronized void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static synchronized User getUser(String username) {
        return users.get(username);
    }

    public static synchronized List<User> getAllUsers() {
        return users.values().stream().toList();
    }
}
