package com.techart.crimemapper.constants;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Has constants for Fire base variable names
 * Created by Kelvin on 11/09/2017.
 */

public final class FireBaseUtils {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static FirebaseAuth mAuth  = FirebaseAuth.getInstance();

    private FireBaseUtils()  {

    }

    @NonNull
    public static String getAuthor(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.isAnonymous()) {
            return "Reporter";
        } else {
            return user.getDisplayName();
        }
    }

    @NonNull
    public static String getUiD() {
        return mAuth.getCurrentUser().getUid();
    }
}
