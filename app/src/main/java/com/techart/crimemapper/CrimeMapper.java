package com.techart.crimemapper;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.firebase.client.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.techart.crimemapper.utils.TypefaceUtil;

/**
 * Initializes Fire base context
 */

public class CrimeMapper extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        TypefaceUtil.overrideFonts(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
