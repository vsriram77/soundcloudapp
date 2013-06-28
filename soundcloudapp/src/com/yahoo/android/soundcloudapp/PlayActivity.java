package com.yahoo.android.soundcloudapp;

import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayActivity extends Activity  implements OnSeekBarChangeListener {
	private MediaPlayer mediaPlayer; 
	private ImageButton btnPlayPause;
	private ImageButton btnStop;
	private ImageButton btnForward;
	private ImageButton btnRewind;
	private TextView tvTrackTitle;
	private TextView tvTrackProgress;
	private SeekBar sbTrackProgress;
	private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        
        setPlayPauseListener();
        setStopListener();
        setForwardListener();
        setRewindListener();
        
        initMediaPlayer();
    }
    
    private void initMediaPlayer() {
    	//String url = "http://licensing.glowingpigs.com/audio/159.mp3"; // your URL here
    	//String trackTitle = "Flying Monkeys by EchoSmith";
        String url = getIntent().getStringExtra("url");
    	String trackTitle = getIntent().getStringExtra("title");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        tvTrackTitle = (TextView)findViewById(R.id.tvTrackTitle);
        tvTrackTitle.setText(trackTitle);
        sbTrackProgress = (SeekBar)findViewById(R.id.sbTrackProgress);
        sbTrackProgress.setOnSeekBarChangeListener(this);
        tvTrackProgress = (TextView)findViewById(R.id.tvTrackProgress);
        
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				sbTrackProgress.setProgress(0);
				sbTrackProgress.setMax(100);
				
				// Updating progress bar
	            updateProgressBar();
			}
        	
        });
        
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				btnPlayPause.setImageResource(R.drawable.media_playback_start_orange);	
			}
		});
        
        try {
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
	        e.printStackTrace();
	    } catch (IllegalStateException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
    }
	
	private void setPlayPauseListener() {
		btnPlayPause = (ImageButton)findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mediaPlayer.isPlaying()) {
		    		mediaPlayer.pause();
		    		btnPlayPause.setImageResource(R.drawable.media_playback_start_orange);

		    	} else {
		    		mediaPlayer.start();
		    		btnPlayPause.setImageResource(R.drawable.media_playback_pause_orange);
		    	}
			}
        });
    }
	
	private void setStopListener() {
		btnStop = (ImageButton)findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//mediaPlayer.stop();
				mediaPlayer.seekTo(mediaPlayer.getDuration());
				btnPlayPause.setImageResource(R.drawable.media_playback_start_orange);
			}
		});
	}
	
	private void setForwardListener() {
		btnForward = (ImageButton)findViewById(R.id.btnForward);
		btnForward.setOnClickListener(new View.OnClickListener() {
			 
            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mediaPlayer.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mediaPlayer.getDuration()){
                    // forward song
                	mediaPlayer.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                	mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
            }
        });
	}

	private void setRewindListener() {
		btnRewind = (ImageButton)findViewById(R.id.btnRewind);
		btnRewind.setOnClickListener(new View.OnClickListener() {
			 
            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mediaPlayer.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                	mediaPlayer.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                	mediaPlayer.seekTo(0);
                }
            }
        });
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onDestroy(){
    	mediaPlayer.release();
    	mHandler.removeCallbacks(mUpdateTimeTask);
    	super.onDestroy();
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }  
    
    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
           public void run() {
               long totalDuration = mediaPlayer.getDuration();
               long currentDuration = mediaPlayer.getCurrentPosition();
               
               tvTrackProgress.setText(Utilities.millisecToTime(currentDuration, totalDuration));
               // Updating progress bar
               int progress = (int)(Utilities.getProgressPercentage(currentDuration, totalDuration));
 
               sbTrackProgress.setProgress(progress);
 
               // Running this thread after 100 milliseconds
               mHandler.postDelayed(this, 100);
           }
        };
	@Override
	public void onProgressChanged(SeekBar sb, int pg, boolean arg2) {
		//sb.setProgress(pg);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar sb) {
		mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = Utilities.progressToTime(sb.getProgress(), totalDuration);
 
        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);
 
        // update timer progress again
        updateProgressBar();
	}
	
}
