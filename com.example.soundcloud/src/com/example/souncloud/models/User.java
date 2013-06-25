package com.example.souncloud.models;

import org.json.JSONObject;

public class User extends BaseModel {
    public String getName() {
        return getString("username");
    }

    public long getId() {
        return getLong("id");
    }

    public String getProfileImageUrl() {
        return getString("avatar_url");
    }

    public static User fromJson(JSONObject json) {
        User u = new User();

        try {
            u.jsonObject = json;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return u;
    }


}