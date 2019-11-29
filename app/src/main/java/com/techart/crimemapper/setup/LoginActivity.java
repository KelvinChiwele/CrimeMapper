package com.techart.crimemapper.setup;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.techart.crimemapper.MainActivity;
import com.techart.crimemapper.R;
import com.techart.crimemapper.constants.Constants;
import com.techart.crimemapper.constants.FireBaseUtils;
import com.techart.crimemapper.utils.EditorUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private ProgressDialog mProgress;

    private EditText etUsername;
    private EditText etPassWord;
    private String email;
    private boolean isAttached;

    // Firebase references.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        etUsername = findViewById(R.id.username);
        etPassWord = findViewById(R.id.password);
        // TextView tvReset = findViewById(R.id.tv_reset);
        Button mLogin = findViewById(R.id.login);
        Button mRegister = findViewById(R.id.bt_register);
        /*ImageView logo = findViewById(R.id.logo);
        Glide.with(LoginActivity.this)
                .load(R.drawable.logo)
                .into(logo);*/
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()) {
                    validUserCredentials();
                } else {
                    noIntenet();
                }
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetworkConnection()) {
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(registerIntent);
                } else {
                    noIntenet();
                }
            }
        });

        /*
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etUsername.getText().toString().trim();
                Intent registerIntent = new Intent(LoginActivity.this, PasswordResetDialog.class);
                registerIntent.putExtra("Email",email);
                startActivity(registerIntent);
            }
        });*/
    }

    private void validUserCredentials() {
        mProgress = new ProgressDialog(LoginActivity.this);
        email = etUsername.getText().toString().trim();
        String password = etPassWord.getText().toString().trim();
        if (validate(email, password)) {
            mProgress.setMessage("Logging in ...");
            mProgress.setCancelable(false);
            mProgress.show();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        validUsersExistance();
                    } else {
                        closeDialog();
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            etUsername.setTextColor(Color.RED);
                            etUsername.setError("Unrecognized email...! Use the email you registered with");
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            etPassWord.setTextColor(Color.RED);
                            etPassWord.setError("Wrong password, enter the password you registered with");
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed!, ensure your internet is working and try again", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    private boolean validate(String email, String password) {
        return EditorUtils.editTextValidator(email, etUsername, "enter a valid email") &&
                EditorUtils.editTextValidator(password, etPassWord, "enter a valid password");
    }


    private boolean haveNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
            return netWorkInfo != null && netWorkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    private void validUsersExistance() {
        DocumentReference docIdRef = FireBaseUtils.db.collection(Constants.COMPLAINANT_KEY).document(FireBaseUtils.getUiD());
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    closeDialog();
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    } else {
                        Toast.makeText(LoginActivity.this, "You need to setup an Account", Toast.LENGTH_LONG).show();
                        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(registerIntent);
                    }
                } else {
                    closeDialog();
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
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

    private void closeDialog() {
        if (isAttached) {
            mProgress.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            finishAffinity();
                        }
                        if (button == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.dismiss();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit application? ")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }

    private void noIntenet() {
        Toast.makeText(LoginActivity.this, "No internet...! Turn on Data or Wifi.", Toast.LENGTH_LONG).show();
    }

}