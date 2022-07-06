package it.units.sim.savewater.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Calendar;
import java.util.Date;

import it.units.sim.savewater.model.User;
import it.units.sim.savewater.model.Utility;

public class DashboardViewModel extends ViewModel {

    private static final String TAG = "DashboardViewModel";
    private final MutableLiveData<Date> date;
    private final MutableLiveData<Integer> targetWaterConsumption;
    private final MutableLiveData<Integer> instantWaterConsumption;
    private final DocumentReference target = FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
    @ServerTimestamp
    private final Timestamp timestamp;
    private Query utilities;

    public DashboardViewModel() {
        Log.d(TAG, "Constructor");
        date = new MutableLiveData<>();
        timestamp = Timestamp.now();
        date.setValue(timestamp.toDate());
        utilities = generateUtilitiesQuery();

        targetWaterConsumption = new MutableLiveData<>();
        instantWaterConsumption = new MutableLiveData<>();

    }

    public MutableLiveData<Date> getDate() {
        return date;
    }

    public void setDate(Date date) {
        Log.d(TAG, "setDate");
        this.date.setValue(date);
        utilities = generateUtilitiesQuery();
        loadInstantWater();
    }

    public MutableLiveData<Integer> getTargetWaterConsumption() {
        loadTargetWater();
        return targetWaterConsumption;
    }

    public void setTargetWaterConsumption(Integer targetWaterConsumption) {
        this.targetWaterConsumption.setValue(targetWaterConsumption);
    }

    public MutableLiveData<Integer> getInstantWaterConsumption() {
        loadInstantWater();
        return instantWaterConsumption;
    }

    public void setInstantWaterConsumption(Integer instantWaterConsumption) {
        this.instantWaterConsumption.setValue(instantWaterConsumption);
    }

    private void loadTargetWater() {
        target.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    targetWaterConsumption.setValue(user.getTarget());
                } else {
                    Log.w(TAG, "loadTargetWater failed");
                }
            }
        });
    }

    private void loadInstantWater() {
        Log.d(TAG, "loadInstantWater");
        utilities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int result = 0;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Utility utility = document.toObject(Utility.class);
                        result += utility.getWaterConsumption();
                    }
                    instantWaterConsumption.setValue(result);
                } else {
                    Log.w(TAG, "loadInstantWater failed");
                }
            }
        });
    }

    private Query generateUtilitiesQuery() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateWithoutTime(date.getValue()));
        Timestamp startTime = new Timestamp(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        Timestamp endTime = new Timestamp(calendar.getTime());
        return utilities = FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("diary")
                .orderBy("timestamp").startAt(startTime).endAt(endTime);
    }

    private Date dateWithoutTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}