package com.yahoo.android.soundcloudapp;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import com.yahoo.android.soundcloudapp.models.Track;

public class ListActivity extends Activity {

	ArrayList<Track> tracksList;
	ArrayAdapter<Track> adapter;
	MediaPlayer mediaPlayer;
	Token token;
	ApiWrapper wrapper;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reloadMethod();
	}

	private void reloadMethod() {
		setContentView(R.layout.activity_list);
		System.out.println("onCreate");
		
		//String tokenAccess = this.getIntent().getStringExtra("token_access");
		token = (Token) this.getIntent().getSerializableExtra("token");
		wrapper = new ApiWrapper("3b70c135a3024d709e97af6b0b686ff3",
                "51ec6f9c19487160b5942ccd4f642053",
                null,
                token);
		try {
			HttpResponse resp = wrapper.get(Request.to("/me/tracks"));
			JSONArray responseArray = new JSONArray(EntityUtils.toString(resp.getEntity()));
			tracksList = Track.fromJsonArray(responseArray);
			adapter = new TracksArrayAdapter(this, tracksList);
			
			ListView lv = (ListView) findViewById(R.id.listView1);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View parent, int position,
						long rowId) {
					Track track = tracksList.get(position);
					String url = track.getUrl();
					String title = track.getTitle();
					url += "?client_id=3b70c135a3024d709e97af6b0b686ff3";
					Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
					intent.putExtra("url", url);
					intent.putExtra("title", title);
					startActivity(intent);
				}
			});
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Toast.makeText(this, "got menu callback!",
		// Toast.LENGTH_SHORT).show();
		Log.d("DDDDD", "onOptionsItemSelected called");
		switch (item.getItemId()) {
		case R.id.miRecord:
			Intent i = new Intent(this, RecordActivity.class);
			i.putExtra("token", token);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_list, menu);

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		reloadMethod();
	}

}
