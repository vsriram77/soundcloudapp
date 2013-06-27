package com.yahoo.android.soundcloudapp;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Params;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;



public class RecordActivity extends FragmentActivity {
    private static String TMP_RECORDFILE = "clip.3gp";
    private static final String LOG_TAG = "RecordActivity";

    private ImageButton ibtnMic;
    private Switch swPrivate;
	private enum RecordState { NONE, RECORDING, UPLOADING };
	
	private RecordState recordState = RecordState.NONE;
    private MediaRecorder mRecorder = null;
    private String mFileName;
	private ApiWrapper wrapper;
	private Token token;
    private int tmpfileNumber = 0; 
    
	private Handler mHandler = new Handler();
	private long mStartTime = 0L;
	private TextView tvTimeDisplay;

    
    private static class AudioClip {
    	public String clipName;
    	public String filePath;
    	
    	public AudioClip(String clipName, String filePath) {
    		this.clipName = clipName;
    		this.filePath = filePath;    		
    	}
    	
    }
    
    private class UploadToSoundCloudTask extends AsyncTask<AudioClip, Integer, Integer> {
    	private RecordActivity recordActivity;
    	private ApiWrapper wrapper;
    	private String clipName;
    	
    	public UploadToSoundCloudTask(RecordActivity recordActivity, ApiWrapper wrapper) {
    		this.recordActivity =  recordActivity;
    		this.wrapper = wrapper;
    	}
    	
        protected Integer doInBackground(AudioClip... clips) {
			try {
				Log.d("DDDDD", "uploading in background...");
				this.clipName = clips[0].clipName;
				File audioFile = new File(clips[0].filePath);
				audioFile.setReadable(true, false);
				HttpResponse resp = wrapper.post(Request.to(Endpoints.TRACKS)
						.add(Params.Track.TITLE, this.clipName)
						.add(Params.Track.TAG_LIST, "demo upload")
						.add(Params.Track.SHARING, 
								swPrivate.isChecked()? Params.Track.PRIVATE : Params.Track.PUBLIC)
						.withFile(Params.Track.ASSET_DATA, audioFile));
				Log.d("DDDDD", "background thread done...");
				return Integer.valueOf(resp.getStatusLine().getStatusCode());
			} catch (IOException exp) {
				Log.d("DDDDD",
						"Error uploading audioclip: IOException: "
								+ exp.toString());
				return Integer.valueOf(500);
			}
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Integer result) {
			Log.d("DDDDD", "UI thread resume: got result...");
			if (result.intValue() == HttpStatus.SC_CREATED) {
				Toast.makeText(
						this.recordActivity,
						"upload successful: "
								+ ": " + clipName, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(
						this.recordActivity,
						"Invalid status received: " + result.toString()
								+ ": " + mFileName, Toast.LENGTH_SHORT).show();
			}
			recordState = RecordState.NONE;
	        ibtnMic.setBackgroundResource(R.drawable.purple_mic2_start);    		
        }
    }
    

	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
		       final long start = mStartTime;
		       long millis = SystemClock.uptimeMillis() - start;
		       int seconds = (int) (millis / 1000);
		       int minutes = seconds / 60;
		       seconds     = seconds % 60;

		       if (seconds < 10) {
		           tvTimeDisplay.setText("" + minutes + ":0" + seconds);
		       } else {
		           tvTimeDisplay.setText("" + minutes + ":" + seconds);            
		       }
		     
		       mHandler.postAtTime(this,
		               start + (((minutes * 60) + seconds + 1) * 1000));
		   }
		};
		


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        File appDataDir = this.getFilesDir();
        mFileName = appDataDir.getAbsolutePath() + "/" + String.valueOf(this.tmpfileNumber++) + TMP_RECORDFILE;
		token = (Token) this.getIntent().getSerializableExtra("token");
		wrapper = new ApiWrapper("3b70c135a3024d709e97af6b0b686ff3",
                "51ec6f9c19487160b5942ccd4f642053",
                null,
                token);

		this.ibtnMic = (ImageButton) findViewById(R.id.ibtnMic); 
		this.swPrivate = (Switch) findViewById(R.id.swPrivate);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        tvTimeDisplay = (TextView) findViewById(R.id.tvTimeDisplay);

        return true;
    }
    
    public void onMicClick(View view) {
    	switch (recordState) {
    	case NONE:
    		recordState = RecordState.RECORDING;
            ibtnMic.setBackgroundResource(R.drawable.purple_mic2_stop);
			tvTimeDisplay.setText("0:00");
			mStartTime = SystemClock.uptimeMillis();
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 100);
    		startRecording();
    		break;
    	case RECORDING:
    		recordState = RecordState.NONE;
            ibtnMic.setBackgroundResource(R.drawable.purple_mic2_start);
			mHandler.removeCallbacks(mUpdateTimeTask);
			mStartTime = 0L;
			tvTimeDisplay.setText("0:00");
    		stopRecording();
    		break;
    	case UPLOADING:
    		Log.d("DDDDD", "do nothing while uploading...");
    		break;
    	}
    }
    
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed############################:" + e.toString());
            e.printStackTrace();
        }

        mRecorder.start();        
    }
    
	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		QuantityDialogFragment dialog = new QuantityDialogFragment();
		dialog.show(getSupportFragmentManager(), "Give it a name");

    }
    
	public void onNamePicked(String clipName) {
		recordState = RecordState.UPLOADING;
        ibtnMic.setBackgroundResource(R.drawable.purple_mic2_wait);    		
		try {
			Toast.makeText(this, "uploading to soundcloud...", Toast.LENGTH_SHORT).show();
			UploadToSoundCloudTask uploadTask = new UploadToSoundCloudTask(this, wrapper);
			uploadTask.execute(new AudioClip(clipName, mFileName));			
		} catch (Exception exp) {
			Log.e("ERR", "ERR: " + exp.toString());
		}
	}

}
