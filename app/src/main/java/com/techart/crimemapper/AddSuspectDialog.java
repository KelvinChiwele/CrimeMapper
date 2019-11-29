package com.techart.crimemapper;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.techart.crimemapper.constants.Constants;
import com.techart.crimemapper.constants.FireBaseUtils;
import com.techart.crimemapper.utils.EditorUtils;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Activity for editing title of posted story
 * Uses Story URL to map each story during an update
 * Created by Kelvin on 30/07/2017.
 */

public class AddSuspectDialog extends AppCompatActivity {

    private boolean isAttached;

    //ui components
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etNrc;
    private EditText etPhone;
    private EditText etResidence;

    //ui components
    private String firstName;
    private String lastName;
    private String nrc;
    private String phone;
    private String residence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_suspect);
        etFirstName = findViewById(R.id.first_name);
        etLastName = findViewById(R.id.last_name);
        etNrc = findViewById(R.id.nrc);
        etPhone = findViewById(R.id.et_phone);
        etResidence = findViewById(R.id.et_residence);
        TextView tvUpdate = findViewById(R.id.tv_update);
        TextView tvCancel = findViewById(R.id.tv_cancel);

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    sendPost();
                    finish();
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * Sends information to database
     */
    private void sendPost() {
        final String url = FireBaseUtils.db.collection(Constants.SUSPECT_KEY).document().getId();
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.FIRST_NAME, firstName);
        values.put(Constants.LAST_NAME, lastName);
        values.put(Constants.NRC, nrc);
        values.put(Constants.PHONE, phone);
        values.put(Constants.TIME_CREATED, FieldValue.serverTimestamp());
        values.put(Constants.RESIDENCE, residence);
        FireBaseUtils.db.collection(Constants.SUSPECT_KEY).document(url).set(values)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Item Posted",LENGTH_LONG).show();
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

    /**
     * validates the entries before submission
     * @return true if successful
     */
    private boolean validate(){
        firstName =  etFirstName.getText().toString().trim();
        lastName =  etLastName.getText().toString().trim();
        nrc =  etNrc.getText().toString().trim();
        phone =  etPhone.getText().toString().trim();
        residence =  etResidence.getText().toString().trim();
        return  EditorUtils.editTextValidator(firstName,etFirstName,"enter a valid username") &&
                EditorUtils.editTextValidator(lastName,etFirstName,"enter a valid username") &&
                EditorUtils.editTextValidator(nrc,etNrc,"enter a valid username") &&
                EditorUtils.editTextValidator(phone,etPhone,"enter a valid username");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }
}
