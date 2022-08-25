package it.units.sim.savewater.utils;

import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.sim.savewater.R;

public class FirebaseUtils {

    private static final String TAG = "Utils";

    public static DocumentReference userRef;
    public static CollectionReference utilitiesRef;

    public static void init() {
        if (isAuthenticated()) {
            userRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            utilitiesRef = userRef.collection("diary");
        }
    }

    public static void signOut() {
        if (isAuthenticated()) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    public static void deleteAccount(View view) {
        if (isAuthenticated()) {
            FirebaseAuth.getInstance().getCurrentUser().delete()
                    .addOnSuccessListener(t -> {
                        userRef.delete();
                        Snackbar.make(view, "Account deleted", Snackbar.LENGTH_LONG).show();
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "Delete account failed");
                        Snackbar.make(view, "Delete account failed", Snackbar.LENGTH_LONG).show();
                    });
        }
    }

    public static void sendResetPassword(View view) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Sent email");
                    Snackbar.make(view, String.format(view.getResources().getString(R.string.success_reset_password), email), Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Reset password failed");
                    Snackbar.make(view, "Reset password failed", Snackbar.LENGTH_LONG).show();
                });
    }

    public static void changeEmail(String email) {
        FirebaseAuth.getInstance().getCurrentUser().updateEmail(email)
                .onSuccessTask(t -> userRef.update("email", email))
                .addOnFailureListener(e -> Log.w(TAG, "Change email failed"));
    }

    public static boolean isAuthenticated() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;

    }
}
