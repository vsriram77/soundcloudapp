package com.example.souncloud.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;





import java.util.ArrayList;

public class Track extends BaseModel {
	private User user;
	
	 public User getUser() {
	        return user;
	 }
    
    public String getTitle() {
        return getString("title");
    }

    public long getId() {
        return getLong("id");
    }
    
    public String getSharing() {
    	return getString("sharing");
    }
    
    public String getCreatedAt() {
    	return getString("created_at");
    }


    public static Track fromJson(JSONObject jsonObject) {
        Track track = new Track();
        
        try {
        	track.jsonObject = jsonObject;
			track.user = User.fromJson(jsonObject.getJSONObject("user"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
        return track;
    }

    public static ArrayList<Track> fromJsonArray(JSONArray jsonArray) {
        ArrayList<Track> tracks = new ArrayList<Track>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject trackJson = null;
            try {
                trackJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Track tweet = Track.fromJson(trackJson);
            if (tweet != null) {
                tracks.add(tweet);
            }
        }

        return tracks;
    }

	public String getUrl() {
		// TODO Auto-generated method stub
		return getString("stream_url");
	}
}
