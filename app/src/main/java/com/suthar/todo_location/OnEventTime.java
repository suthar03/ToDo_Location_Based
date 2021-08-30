package com.suthar.todo_location;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OnEventTime extends AppCompatActivity {

    public static final String TAG = "OnEventTime";
    static OnEventTime instance;
    Button btn_done;

    public static OnEventTime getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_event_time);

        instance = this;

        btn_done = findViewById(R.id.btn_done);


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication myApplication = (MyApplication) getApplicationContext();
                MainActivity.getInstance().setEventDone(myApplication.getIdd());
                instance = null;
                LocationUpdatesService.getInstance().stopVibration();
                finish();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        instance = null;

    }
}