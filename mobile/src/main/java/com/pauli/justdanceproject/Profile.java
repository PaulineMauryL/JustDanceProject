package com.pauli.justdanceproject;

import java.io.Serializable;

public class Profile implements Serializable {

    String username;
    String language;
    String photoPath;
    String level;
    /*String[] level = {
            "1st year student",
            "Raw recruit",
            "Basic soldier",
            "Warrior",
            "Ninja",
            "Samurai",
            "Warrior Queen/King",
            "Dancer",
            "Bachata dancer",
    };*/
    //String[] mStrings = new String[level.length];

    public Profile(String username, String language) {
        // When you create a new Profile, it's good to build it based on username and password
        this.username = username;
        this.language = language;
    }

    public Profile(String username) {

        this.username = username;
    }

    //public Profile(String username, int level_nb) {

        //this.username = username;
        //this.level = level[level_nb];
    //}
}
