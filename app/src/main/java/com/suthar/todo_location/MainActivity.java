package com.suthar.todo_location;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements EventAdapter.ItemClicked, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    static MainActivity instance;
    final int CREATE_EVENT = 1;
    ArrayList<Event> data = new ArrayList<Event>();
    ArrayList<Event> dataToDo = new ArrayList<Event>();

    Button btnAdd, btn_start, btn_stop;
    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter myAdapter;

    private MyReceiver myReceiver;
    private LocationUpdatesService mService = null;
    private boolean mBound = false;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myReceiver = new MyReceiver();
        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
        instance = this;

        btnAdd = findViewById(R.id.btnAdd);
        btn_start = findViewById(R.id.btn_start_service);
        btn_stop = findViewById(R.id.btn_stop_service);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Todo: " + dataToDo.size(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, com.suthar.todo_location.CreateEvent.class);
                startActivityForResult(intent, CREATE_EVENT);
            }
        });

        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,true);
        recyclerView.setLayoutManager(layoutManager);

        loadData();
        loadDataToDo();
        MyApplication myApplication = (MyApplication) getApplicationContext();
        myApplication.setEventToDo(dataToDo);
        myAdapter = new EventAdapter(this, data);
        recyclerView.setAdapter(myAdapter);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(myApplication, "Do any thing", Toast.LENGTH_SHORT).show();
            //startService();
        } else {
            Toast.makeText(this, "There is permission issue", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "onCreate: ");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == CREATE_EVENT) {
            if (resultCode == RESULT_OK) {


                String Generated_ID = Generate_Unique();
                Event eve = new Event(Generated_ID, intent.getStringExtra(Statics.TYPE), intent.getStringExtra(Statics.TITLE)
                        , intent.getStringExtra(Statics.DESCRIPTION), intent.getStringExtra(Statics.DATE), intent.getStringExtra(Statics.TIME)
                        , intent.getStringExtra(Statics.ADDRESS), intent.getDoubleExtra(Statics.LATITUDE, 0), intent.getDoubleExtra(Statics.LONGITUDE, 0), intent.getIntExtra(Statics.ACCURACY, 100));

                eve.setId(Generated_ID);
                eve.setisDone(0);//Yet Not Done

                try {
                    EventDataBase db = new EventDataBase(MainActivity.this);
                    db.open();
                    db.createEntry(eve);
                    db.close();
                    data.add(eve);

                    loadDataToDo();
                    myAdapter.notifyDataSetChanged();
                    MyApplication myApplication = (MyApplication) getApplicationContext();
                    myApplication.setEventToDo(dataToDo);
                    if (dataToDo.size() == 1)
                        startService();
                    Toast.makeText(MainActivity.this, "Event Successfully Saved!", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Toast.makeText(this, "Something wrong is here", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnItemClicked(int index) {
        myAdapter.notifyDataSetChanged();
        Toast.makeText(this, "You clicked on the event titled as " + data.get(index).getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnCheckBoxClicked(int index, boolean newState) {

        try {
            if (newState)//Done so we have to update
                data.get(index).setisDone(1);
            else // Not Done Yet
                data.get(index).setisDone(0);

            EventDataBase db = new EventDataBase(MainActivity.this);
            db.open();
            long tr = db.updateEntry(data.get(index));
            db.close();
            myAdapter.notifyDataSetChanged();

            //Remove from ToDo if
            loadDataToDo();
            MyApplication myApplication = (MyApplication) getApplicationContext();
            myApplication.setEventToDo(dataToDo);
            if (dataToDo.size() == 1)
                startService();
            Toast.makeText(MainActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void OnItemLongClicked(final int index) {
        //Toast.makeText(MainActivity.this,"Long Pressed ",Toast.LENGTH_SHORT).show();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        //.setMessage(" Message ")
        alertDialog.setTitle("Do you want to delete or edit?");

        alertDialog.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "EDITING", Toast.LENGTH_SHORT).show();

            }
        });
        alertDialog.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    EventDataBase db = new EventDataBase(MainActivity.this);
                    db.open();
                    db.deleteEntry(data.get(index).getId());
                    data.remove(index);
                    db.close();
                    myAdapter.notifyItemRemoved(index);

                    // remove from todo if
                    loadDataToDo();
                    MyApplication myApplication = (MyApplication) getApplicationContext();
                    myApplication.setEventToDo(dataToDo);
                    Toast.makeText(MainActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.show();
    }

    private void loadData() {

        try {
            EventDataBase db = new EventDataBase(MainActivity.this);
            db.open();
            data = db.getData();
            db.close();
        } catch (SQLException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void loadDataToDo() {
        dataToDo.clear();
        for (int i = 0; i < data.size(); ++i) {
            if (data.get(i).getisDone() == 0) {
                dataToDo.add(data.get(i));
            }
        }

    }

    protected String Generate_Unique() {
        String res = "";
        Calendar calendar = Calendar.getInstance();
        res = (String) DateFormat.format("yyyyMMdd", calendar);
        res = res + String.format("%02d%02d%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        return res;
    }

    // Not in Current use
    protected void ShowData() {
        ArrayList<Event> ds = new ArrayList<>();
        try {
            EventDataBase db = new EventDataBase(MainActivity.this);
            db.open();
            ds = db.getData();
            db.close();
        } catch (SQLException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < ds.size(); ++i) {
            Log.d("Title: ", ds.get(i).getTitle() + " " + ds.get(i).getisDone());
        }
    }


    protected void setEventDone(String idd) {
        Log.d(TAG, "setEventDone: " + idd);
        try {
            Event toUpdate = null;
            for (Event event : dataToDo) {
                if (event.getId().equalsIgnoreCase(idd)) {
                    toUpdate = event;
                    break;
                }
            }
            if (toUpdate == null) return;

            toUpdate.setisDone(1);
            EventDataBase db = new EventDataBase(MainActivity.this);
            db.open();
            long tr = db.updateEntry(toUpdate);
            db.close();
            myAdapter.notifyDataSetChanged();

            //Remove from ToDo if
            loadDataToDo();
            MyApplication myApplication = (MyApplication) getApplicationContext();
            myApplication.setEventToDo(dataToDo);
            if (dataToDo.size() > 0)
                startService();
            Toast.makeText(MainActivity.this, "Marked as done", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "onStart: ");
        //startService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        Log.d(TAG, "onResume: ");

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        "com.suthar.todo_location", null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
        }
    }


    private void stopService() {
        mService.removeLocationUpdates();
    }

    private void startService() {
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            mService.requestLocationUpdates();
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(MainActivity.this, Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
