package com.yahoo.android.soundcloudapp;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
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
	private boolean isRecording = false;
    private MediaRecorder mRecorder = null;
    private String mFileName;
	private ApiWrapper wrapper;
	private Token token;
    private int tmpfileNumber = 0; 


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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onYellowMicClick(View view) {
		if (!isRecording) {
			isRecording = true;
            ibtnMic.setBackgroundResource(R.drawable.yellomic_stop);
            startRecording();
		} else {
			isRecording = false;
			ibtnMic.setBackgroundResource(R.drawable.yellowmic_start);
			stopRecording();			
			Toast.makeText(this, "Sound saved!", Toast.LENGTH_SHORT).show();
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
		try {
			File audioFile = new File(mFileName);
			audioFile.setReadable(true, false);
			Toast.makeText(this, "uploading to soundcloud...", Toast.LENGTH_SHORT).show();
			HttpResponse resp = wrapper.post(Request.to(Endpoints.TRACKS)
					.add(Params.Track.TITLE, clipName)
					.add(Params.Track.TAG_LIST, "demo upload")
					.withFile(Params.Track.ASSET_DATA, audioFile));

			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
				Toast.makeText(
						this,
						"upload successful: "
								+ ": " + clipName, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						this,
						"Invalid status received: " + resp.getStatusLine()
								+ ": " + mFileName, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception exp) {
			Log.e("ERR", "ERR: " + exp.toString());
		}
	}

}
