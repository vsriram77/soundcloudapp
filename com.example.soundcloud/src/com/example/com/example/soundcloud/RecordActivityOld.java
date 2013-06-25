package com.example.com.example.soundcloud;

/*
 * The application needs to have the permission to write to external storage
 * if the output file is written to the external storage, and also the
 * permission to record audio. These permissions must be set in the
 * application's AndroidManifest.xml file, with something like:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Params;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;


public class RecordActivityOld extends FragmentActivity
{
    private static final String LOG_TAG = "RecordActivity";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
    	   QuantityDialogFragment dialog = new QuantityDialogFragment();
           dialog.show(getSupportFragmentManager(), "Dialog");

           /*
        mPlayer = new MediaPlayer();
        try {
			FileInputStream fis = openFileInput("foos.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String aLine = br.readLine();
			br.close();
	    	Toast.makeText(this, "foos.txt read! line=" + aLine, Toast.LENGTH_LONG).show();


	    	
        	// String url = "http://ragasurabhi.com/carnatic-music-mp3/raga-abhogi-signature.mp3"; // your URL here
        	// MediaPlayer mediaPlayer = new MediaPlayer();
        	// mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        	// mediaPlayer.setDataSource(url);
        	// mediaPlayer.prepare(); // might take long! (for buffering, etc)
        	// mediaPlayer.start();
        	
        	
            mPlayer.setDataSource(mFileName);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepare();
            mPlayer.start();
            
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed: fr "+ mFileName + ":"+e.toString());
            e.printStackTrace();
        }
        */
    }

    private void stopPlaying() {
        // mPlayer.release();
        // mPlayer = null;
    }

	private void startRecording() {
		String aLine = "ooo";

		try {
			FileOutputStream fos = openFileOutput("foos.txt",
					MODE_WORLD_WRITEABLE);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write("hello record\n");
			bw.write("hello record2\n");
			bw.close();
		} catch (IOException exp) {
            Log.e(LOG_TAG, "fileio failed:" + exp.toString());
			exp.printStackTrace();
		}
    	Toast.makeText(this, "foos.txt write", Toast.LENGTH_LONG).show();


    	
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
        try {
        File audioFile = new File(mFileName);
        audioFile.setReadable(true, false);
    	Toast.makeText(this, "world readable: " + mFileName, Toast.LENGTH_LONG).show();
    	
    	
    	
        HttpResponse resp = wrapper.post(Request.to(Endpoints.TRACKS)
                .add(Params.Track.TITLE,     mFileName)
                .add(Params.Track.TAG_LIST, "demo upload")
                .withFile(Params.Track.ASSET_DATA, audioFile));
        
        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
        	Toast.makeText(this, "\n201 Created "+resp.getFirstHeader("Location").getValue() + ": " + mFileName, Toast.LENGTH_LONG).show();

            // dump the representation of the new track
            // System.out.println("\n" + Http.getJSON(resp).toString(4));
        } else {
        	Toast.makeText(this, "Invalid status received: " + resp.getStatusLine() + ": " + mFileName, Toast.LENGTH_SHORT).show();
        }
        

        } catch(Exception exp) {
        	Log.e("ERR", "ERR: " + exp.toString());
        }
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop REC");
                } else {
                    setText("Start REC");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start REC");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop PLAY");
                } else {
                    setText("Start PLAY");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start PLAY");
            setOnClickListener(clicker);
        }
    }

    
    public RecordActivityOld() {
    }
    
	ApiWrapper wrapper;
	Token token;


    public void onUserSelectValue(String selectedValue) {

    	Toast.makeText(this, "From Dialog=" + selectedValue, Toast.LENGTH_LONG).show();

    }
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
		//String tokenAccess = this.getIntent().getStringExtra("token_access");
		token = (Token) this.getIntent().getSerializableExtra("token");
		wrapper = new ApiWrapper("3b70c135a3024d709e97af6b0b686ff3",
                "51ec6f9c19487160b5942ccd4f642053",
                null,
                token);


        LinearLayout ll = new LinearLayout(this);

        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        setContentView(ll);
        //mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordingSound.3gp";
        File fff = this.getFilesDir();
        mFileName = fff.getAbsolutePath() + "/RecordActivity1.3gp";

    }
    
    

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}