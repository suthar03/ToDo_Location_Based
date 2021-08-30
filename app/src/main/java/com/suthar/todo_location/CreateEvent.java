package com.suthar.todo_location;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class CreateEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int LOCATION_SELECTION_REQUEST = 101;
    private static final String TAG = "CreateEvent";
    EditText etTitle, etDescription, etDate, etTime, etAddress;
    Button btnCancel, btnSave;
    ImageView ivCalender, ivTime, ivLocationSelector;
    Spinner spnrMode, spnrAccuracy;
    LinearLayout pnlDate, pnlTime, pnlLocation, pnlAccuracy;
    boolean isLocationValid = false;
    String Address;
    double Latitude = 0.0, Longitude = 0.0;

    public static boolean hasPermissionInManifest(Activity activity, int requestCode, String permissionName) {
        if (ContextCompat.checkSelfPermission(activity,
                permissionName)
                != PackageManager.PERMISSION_GRANTED) {
            //Requesting the permission.
            ActivityCompat.requestPermissions(activity, new String[]{permissionName}, requestCode);
        } else {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etAddress = findViewById(R.id.etAddress);
        ivLocationSelector = findViewById(R.id.iv_LocationSelector);
        spnrAccuracy = findViewById(R.id.spnrAccuracy);
        pnlAccuracy = findViewById(R.id.pnlAccuracy);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        ivCalender = findViewById(R.id.ivCalender);
        ivTime = findViewById(R.id.ivTime);
        spnrMode = findViewById(R.id.spnrMode);
        pnlDate = findViewById(R.id.pnlDate);
        pnlTime = findViewById(R.id.pnlTime);
        pnlLocation = findViewById(R.id.pnlLocation);
        etTime.setEnabled(false);
        etDate.setEnabled(false);
        isLocationValid = false;

        //Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrMode.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this, R.array.accuracies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrAccuracy.setAdapter(adapter);
        spnrAccuracy.setSelection(2);

        spnrMode.setOnItemSelectedListener(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etTitle.getText().toString().isEmpty() && !((etAddress.getText().toString().isEmpty() && !isLocationValid) && (etDate.getText().toString().isEmpty() || etTime.getText().toString().isEmpty()))) {
                    Intent intent = new Intent();
                    Log.d(TAG, "onClick: lat: " + Latitude + ", lon:" + Longitude);
                    intent.putExtra(Statics.TYPE, spnrMode.getSelectedItem().toString().trim());
                    intent.putExtra(Statics.TITLE, etTitle.getText().toString().trim());
                    intent.putExtra(Statics.DESCRIPTION, etDescription.getText().toString().trim());
                    intent.putExtra(Statics.DATE, etDate.getText().toString().trim());
                    intent.putExtra(Statics.TIME, etTime.getText().toString().trim());
                    intent.putExtra(Statics.ADDRESS, etAddress.getText().toString().trim());
                    intent.putExtra(Statics.LATITUDE, Latitude);
                    intent.putExtra(Statics.LONGITUDE, Longitude);
                    intent.putExtra(Statics.ACCURACY, Integer.parseInt(spnrAccuracy.getSelectedItem().toString()));
                    setResult(RESULT_OK, intent);
                    CreateEvent.this.finish();
                } else {
                    Toast.makeText(CreateEvent.this, "Please fill all mandatory fields or select a valid location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                CreateEvent.this.finish();
            }
        });

        ivCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDateButton();
            }
        });

        ivTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTimeButton();
            }
        });

        ivLocationSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationSelectionHandler();
            }
        });
    }

    private void LocationSelectionHandler() {
        if (hasPermissionInManifest(CreateEvent.this, 1, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Intent intent = new Intent(CreateEvent.this, LocationPicker.class);
            startActivityForResult(intent, LOCATION_SELECTION_REQUEST);

        }
    }

    private void handleDateButton() {
        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                //String text = String.format("%02d",date)+"/"+String.format("%02d",month)+"/"+year;
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DATE, date);

                String text = (String) DateFormat.format("EEEE , yyyy/MM/dd", calendar1);
                etDate.setText(text);
            }
        }, YEAR, MONTH, DATE);
        datePickerDialog.show();
    }

    private void handleTimeButton() {
        Calendar calendar = Calendar.getInstance();
        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        final boolean is24HourFormat = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR, hour);
                calendar1.set(Calendar.MINUTE, minute);
                if (is24HourFormat) {
                    String text = String.format("%02d:%02d", hour, minute);
                    etTime.setText(text);
                } else {
                    boolean isPM = (hour >= 12);
                    String text = String.format("%02d:%02d %s", (hour == 12 || hour == 0) ? 12 : hour % 12, minute, isPM ? "PM" : "AM");
                    etTime.setText(text);
                }
            }
        }, HOUR, MINUTE, is24HourFormat);

        timePickerDialog.show();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0) {
            pnlTime.setVisibility(View.VISIBLE);
            pnlDate.setVisibility(View.VISIBLE);
            pnlLocation.setVisibility(View.GONE);
            pnlAccuracy.setVisibility(View.GONE);
        } else {
            pnlTime.setVisibility(View.GONE);
            pnlDate.setVisibility(View.GONE);
            pnlLocation.setVisibility(View.VISIBLE);
            pnlAccuracy.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SELECTION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d(TAG, "Non null data found");
                Address = data.getStringExtra(Statics.SELECTED_ADDRESS);
                Latitude = data.getDoubleExtra(Statics.LOCATION_LAT_EXTRA, 0);
                Longitude = data.getDoubleExtra(Statics.LOCATION_LNG_EXTRA, 0);
                isLocationValid = data.getBooleanExtra(Statics.LOCATION_VALID, false);

                ivLocationSelector.setImageResource(R.drawable.ic_edit_location);
                etAddress.setText(Address.substring(0, 25));
            }
        }
    }
}
