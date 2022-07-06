package it.units.sim.savewater.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

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
            firebaseAuth.getCurrentUser().delete().getResult();
            userRef.delete().getResult();
        }
    }

    public static boolean isAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;

    }

}
