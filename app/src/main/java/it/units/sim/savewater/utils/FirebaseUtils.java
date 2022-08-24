package it.units.sim.savewater.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public static void deleteAccount() {
        if (isAuthenticated()) {
            FirebaseAuth.getInstance().getCurrentUser().delete()
                    .onSuccessTask(t -> userRef.delete())
                    .addOnFailureListener(e -> Log.w(TAG, "Delete account failed"));
        }
    }

    public static void sendResetPassword() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addOnFailureListener(e -> Log.w(TAG, "Reset password failed"));
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
