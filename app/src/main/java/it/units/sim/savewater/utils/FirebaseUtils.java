package it.units.sim.savewater.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String TAG = "Utils";

    public static DocumentReference userRef;
    public static CollectionReference utilitiesRef;

    public static void init() {
        if (isAuthenticated()) {
            userRef = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
            utilitiesRef = userRef.collection("diary");
        }
    }

    public static void signOut() {
        if (isAuthenticated()) {
            firebaseAuth.signOut();
        }
    }

    public static void deleteAccount() {
        if (isAuthenticated()) {
            firebaseAuth.getCurrentUser().delete()
                    .onSuccessTask(t -> userRef.delete())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Delete account failed");
                        }
                    });
        }
    }

    public static void sendResetPassword() {
        firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Reset password failed");
                    }
                });
    }

    public static void changeEmail(String email) {
        firebaseAuth.getCurrentUser().updateEmail(email)
                .onSuccessTask(t -> userRef.update("email", email))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Change email failed");
                    }
                });
    }

    public static boolean isAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;

    }
}
