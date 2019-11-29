package com.techart.crimemapper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techart.crimemapper.constants.Constants;
import com.techart.crimemapper.constants.FireBaseUtils;
import com.techart.crimemapper.utils.EditorUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

/**
 *  Handles actions related to reporting cases
 */

public class ReportActivity extends AppCompatActivity{
    //string resources
    private String subject;
    private String issueType;
    private String province;
    private String particularsOfOffence;
    private String realPath;

    //ui components
    private TextView tvCropError;
    private Spinner spIssue;
    private Button btLocation;
    private Button btDate;
    private Button btTime;
    private EditText etPlace;
    private TextView tvDate;
    private TextView tvTime;
    private EditText etParticulars;
    private TextView tvLat;
    private TextView tvLng;
    String userUrl;
    private double latitude;
    private double longitude;

    float distanceInMeters;
    Location coordStation;
    String stationName;


    //image
    private static final int PLACE_PICKER_REQUEST = 1;
    private Uri uri;

    //Permission

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        etPlace = findViewById(R.id.et_place);
        etParticulars = findViewById(R.id.et_particulars);
        tvDate = findViewById(R.id.et_date);
        tvTime = findViewById(R.id.et_time);
        tvLat = findViewById(R.id.tv_lat);
        tvLng = findViewById(R.id.tv_lng);
        btLocation = findViewById(R.id.bt_location);
        btDate = findViewById(R.id.bt_date);
        btTime = findViewById(R.id.bt_time);

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent picker = new Intent(ReportActivity.this,MapsActivity.class);
                startActivityForResult(picker,PLACE_PICKER_REQUEST);
            }
        });

        //Issue type
        final String[] issues = getResources().getStringArray(R.array.categories);
        spIssue = findViewById(R.id.sp_issue);
        ArrayAdapter<String> issuesAdapter = new ArrayAdapter<String>(ReportActivity.this, R.layout.tv_dropdown, issues);
        issuesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        issuesAdapter.notifyDataSetChanged();

        spIssue.setAdapter(issuesAdapter);
        spIssue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                issueType = issues[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                tvDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ReportActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tvTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, mHour, mMinute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }



    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu menu resource to be inflated
     * @return true if successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_post:
                if (validate()){
                    getStation();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sends information to database
     */
    private void sendPost() {
        final String url = FireBaseUtils.db.collection(Constants.OCCURANCES_KEY).document().getId();
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.SUBJECT, issueType);
        values.put(Constants.ICON,issueType.toLowerCase());
        values.put(Constants.PLACE, etPlace.getText().toString().trim());
        values.put(Constants.DATE, tvDate.getText().toString().trim() + ": " + tvTime.getText().toString().trim());
        values.put(Constants.PARTICULARS_OF_OFFENCE, particularsOfOffence);
        values.put(Constants.LATITUDE, latitude);
        values.put(Constants.LONGITUDE, longitude);
        values.put(Constants.STATUS,"Pending");
        values.put(Constants.MODE_OF_SUBMISSION,"Mobile");
        values.put(Constants.USER_URL, FireBaseUtils.getUiD());
        values.put(Constants.STATION, stationName);
        values.put(Constants.TIME_CREATED, FieldValue.serverTimestamp());

        FireBaseUtils.db.collection(Constants.OCCURANCES_KEY).document(url).set(values)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Item Posted",LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "ERROR" + e.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getStation() {
        final Location crimeScene = new Location("");
        crimeScene.setLatitude(latitude);
        crimeScene.setLongitude(longitude);
        distanceInMeters = 0;
        FireBaseUtils.db.collection(Constants.STATION_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Location closestStation = new Location("");
                        closestStation.setLatitude(Double.valueOf(document.getString("latitude")));
                        closestStation.setLongitude(Double.valueOf(document.getString("longitude")));
                        if ( distanceInMeters == 0){
                            coordStation = closestStation;
                            stationName = document.getString("station");
                            distanceInMeters = crimeScene.distanceTo(closestStation);
                        } else if (distanceInMeters > crimeScene.distanceTo(closestStation)){
                            coordStation = closestStation;
                            distanceInMeters = crimeScene.distanceTo(closestStation);
                            stationName = document.getString("station");
                        }
                        sendPost();
                    }
                } else {
                    Toast.makeText(ReportActivity.this,"Failed",LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * validates the entries before submission
     * @return true if successful
     */
    private boolean validate(){
        String place = etPlace.getText().toString().trim();
        particularsOfOffence = etParticulars.getText().toString().trim();
        tvCropError = (TextView) spIssue.getSelectedView();
        return  EditorUtils.dropDownValidator(issueType,getResources().getString(R.string.default_category),tvCropError) &&
            EditorUtils.editTextValidator(place, etPlace,"Type in the place of crime occuence") &&
            EditorUtils.editTextValidator(particularsOfOffence,etParticulars,"Type in the particulars");
    }

    /**
     * Called upon selecting an image
     * @param requestCode
     * @param resultCode was operation successful or not
     * @param data data returned from the operation
     */
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            latitude = Double.valueOf(data.getStringExtra(Constants.LATITUDE));
            longitude = Double.valueOf(data.getStringExtra(Constants.LONGITUDE));
            tvLat.setVisibility(View.VISIBLE);
            tvLat.setText("Latitude: " + latitude);
            tvLng.setVisibility(View.VISIBLE);
            tvLng.setText("Latitude: " + latitude);
        }
    }
}