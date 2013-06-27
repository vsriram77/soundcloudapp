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
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;

import com.yahoo.android.soundcloudapp.models.Track;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;

public class ListActivity extends Activity implements OnPreparedListener, MediaController.MediaPlayerControl {
	
	ArrayList<Track> tracksList;
	ArrayAdapter<Track> adapter;
	MediaPlayer mediaPlayer;
	MediaController mediaController;
	ApiWrapper wrapper;
	Token token;
	
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		
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
			
			mediaPlayer = new MediaPlayer();
		    mediaPlayer.setOnPreparedListener(this);

		    mediaController = new MediaController(this);


			
			ListView lv = (ListView) findViewById(R.id.listView1);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View parent, int position,
						long rowId) {
					//Intent i = new Intent(getApplicationContext(), PlayActivity.class);
					Track track = tracksList.get(position);
					String url = track.getUrl();
					try {
					      mediaPlayer.setDataSource(url);
					      mediaPlayer.prepare();
					      mediaPlayer.start();
					    } catch (IOException e) {
					      Log.e("DEBUG", "Could not open file " + url + " for playback.", e);
					    }
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list, menu);
		return true;
	}

	public void start() {
	    mediaPlayer.start();
	  }

	  public void pause() {
	    mediaPlayer.pause();
	  }

	  public int getDuration() {
	    return mediaPlayer.getDuration();
	  }

	  public int getCurrentPosition() {
	    return mediaPlayer.getCurrentPosition();
	  }

	  public void seekTo(int i) {
	    mediaPlayer.seekTo(i);
	  }

	  public boolean isPlaying() {
	    return mediaPlayer.isPlaying();
	  }

	  public int getBufferPercentage() {
	    return 0;
	  }

	  public boolean canPause() {
	    return true;
	  }

	  public boolean canSeekBackward() {
	    return true;
	  }

	  public boolean canSeekForward() {
	    return true;
	  }

	  @Override
	  protected void onStop() {
	    super.onStop();
	    mediaController.hide();
	    mediaPlayer.stop();
	    mediaPlayer.release();
	  }

	  @Override
	  public boolean onTouchEvent(MotionEvent event) {
	    //the MediaController will hide after 3 seconds - tap the screen to make it appear again
	    mediaController.show();
	    return false;
	  }
	  
	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
	    Log.d("DEBUG", "onPrepared");
	    mediaController.setMediaPlayer(this);
	    mediaController.setAnchorView(findViewById(R.id.relativeLayout));

	    handler.post(new Runnable() {
	      public void run() {
	        mediaController.setEnabled(true);
	        mediaController.show();
	      }
	    });
	  }
	
	public void onRecClick(View v) {
        Intent i = new Intent(this, RecordActivity.class);
        i.putExtra("token", token);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
	}

}
