package com.marvik.apps.gdgmmustsmsdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by victor_mwenda on 11/15/2014. 5:20pm - 5:23pm
  * Phone: 0718034449
 * Email: vmwenda.vm@gmail.com
 * 	other: victor@merusongs.com
 * Website: http://www.merusongs.com
 */

public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutInflater().inflate(R.layout.splash, null, false));

        openNewActivity(3000);
    }

    private void openNewActivity(final long millis) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(millis);
                    startActivity(new Intent(getApplicationContext(), SendMessage.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}