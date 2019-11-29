package com.techart.crimemapper.setup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.ServerValue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.techart.crimemapper.MainActivity;
import com.techart.crimemapper.R;
import com.techart.crimemapper.constants.Constants;
import com.techart.crimemapper.constants.FireBaseUtils;
import com.techart.crimemapper.models.Profile;
import com.techart.crimemapper.utils.EditorUtils;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Handles registration process
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etNrc;
    private EditText etPhone;
    private EditText etResidence;
    private EditText etLogin;
    private EditText etPassword;
    private EditText etRepeatedPassword;
    private String firstPassword;
    private String name;
    private String email;
    private ProgressDialog mProgress;
    private SharedPreferences mPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etFirstName = findViewById(R.id.first_name);
        etLastName = findViewById(R.id.last_name);
        etNrc = findViewById(R.id.nrc);
        etPhone = findViewById(R.id.et_phone);
        etResidence = findViewById(R.id.et_residence);
        etLogin = findViewById(R.id.et_login);
        etPassword = findViewById(R.id.et_password);
        etRepeatedPassword = findViewById(R.id.et_repeatPassword);
        Button btRegister = findViewById(R.id.bt_register);
        btRegister.setClickable(true);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()) {
                    if (validateCredentials()) {
                        startRegister();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this,"Ensure that your internet is working", Toast.LENGTH_LONG ).show();
                }
            }
        });
    }

    /**
     * implementation of the registration
     */
    private void startRegister() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Signing Up  ...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,firstPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mPref = getSharedPreferences(String.format("%s",getString(R.string.app_name)),MODE_PRIVATE);
                        String url = FireBaseUtils.db.collection(Constants.COMPLAINANT_KEY).document().getId();
                        Map<String, Object> values = new HashMap<>();
                        values.put(Constants.FIRST_NAME,etFirstName.getText().toString().trim());
                        values.put(Constants.LAST_NAME,etLastName.getText().toString().trim());
                        values.put(Constants.NRC,etNrc.getText().toString().trim());
                        values.put(Constants.PHONE,etPhone.getText().toString().trim());
                        values.put(Constants.USER_URL, FireBaseUtils.getUiD());
                        values.put(Constants.RESIDENCE,etResidence.getText().toString().trim());
                        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
                        FireBaseUtils.db.collection(Constants.COMPLAINANT_KEY).document(url).set(values)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this, "User created",LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Error creating user" + e.toString(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    if (user != null) {
                        mProgress.dismiss();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Profile profile = Profile.getInstance();
                                           // profile.setSignedAs(signingInAs);
                                            SharedPreferences.Editor editor = mPref.edit();
                                            editor.putString("user",name);
                                            editor.apply();
                                            Toast.makeText(RegisterActivity.this, "User profile updated.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error encountered, Please try again later", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mProgress.dismiss();
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterActivity.this,"User already exits, use another email address", Toast.LENGTH_LONG ).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Error encountered, Please try again later", Toast.LENGTH_LONG ).show();
                    }
                }
            }
        });
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            return netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    /**
     * Validates the entries
     * @return true if they all true
     */
    private boolean validateCredentials()  {
        firstPassword =  etPassword.getText().toString().trim();
        String repeatedPassword = etRepeatedPassword.getText().toString().trim();
        name =  etFirstName.getText().toString().trim();
        email = etLogin.getText().toString().trim();
        return  EditorUtils.editTextValidator(name,etFirstName,"enter a valid username") &&
                EditorUtils.editTextValidator(email,etLogin,"enter a valid email") &&
                EditorUtils.isEmailValid(email, etPassword) &&
                EditorUtils.editTextValidator(firstPassword,etPassword,"enter a valid password") &&
                EditorUtils.doPassWordsMatch(firstPassword, repeatedPassword,etRepeatedPassword);
    }

}

