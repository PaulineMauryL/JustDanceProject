package com.pauli.justdanceproject;

import java.io.Serializable;

public class Profile implements Serializable {

    String username;
    String language;

    String photoPath;

    public Profile(String username, String language) {
        // When you create a new Profile, it's good to build it based on username and password
        this.username = username;
        this.language = language;
    }

    public Profile(String username) {
        this.username = username;
    }
}
