package com.example.com.example.soundcloud;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Token;

public class LoginActivity extends Activity {
	
	EditText etUsername;
	EditText etPasswd;
	Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                      .permitNetwork()
                                      .build()
                                  );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPasswd = (EditText) findViewById(R.id.etPassword);
        loginButton = (Button) findViewById(R.id.btnLogin);
        
        etUsername.setText("adi2188");
        etPasswd.setText("abcd123");
        
    }
    
    public void onLoginClick(View v) {
    	String username = etUsername.getText().toString();
    	String password = etPasswd.getText().toString();
    	this.doSomethingWithSoundcloud(username, password);
    }

    public void doSomethingWithSoundcloud(String username, String passwd) {
        try {
            ApiWrapper wrapper = new ApiWrapper("3b70c135a3024d709e97af6b0b686ff3",
                                                "51ec6f9c19487160b5942ccd4f642053",
                                                null,
                                                null);


            //Token token = wrapper.login("adi2188", "abcd123");
            Token token = wrapper.login(username, passwd);
            Intent i = new Intent(this, ListActivity.class);
            i.putExtra("token", token);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            



            Log.d("DEBUG", "token is: " + token);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}