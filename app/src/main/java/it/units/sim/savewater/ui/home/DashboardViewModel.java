package it.units.sim.savewater.ui.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

import it.units.sim.savewater.model.User;
import it.units.sim.savewater.model.Utility;
import it.units.sim.savewater.utils.FirebaseUtils;

public class DashboardViewModel extends ViewModel {

    private static final String TAG = "DashboardViewModel";
    private final MutableLiveData<Date> date;
    private final MutableLiveData<Integer> targetWaterConsumption;
    private final MutableLiveData<Integer> instantWaterConsumption;

    public DashboardViewModel() {
        date = new MutableLiveData<>(Timestamp.now().toDate());
        targetWaterConsumption = new MutableLiveData<>();
        instantWaterConsumption = new MutableLiveData<>();

    }

    public MutableLiveData<Date> getDate() {
        return date;
    }

    public void setDate(Date date) {
        Log.d(TAG, "setDate");
        this.date.setValue(date);
    }

    public MutableLiveData<Integer> getTargetWaterConsumption() {
        retrieveTargetWaterConsumption();
        Log.d(TAG, "get " + targetWaterConsumption.getValue());
        return targetWaterConsumption;
    }

    public void setTargetWaterConsumption(Integer targetWaterConsumption) {
        this.targetWaterConsumption.setValue(targetWaterConsumption);
    }

    public MutableLiveData<Integer> getInstantWaterConsumption() {
        retrieveInstantWaterConsumption();
        return instantWaterConsumption;
    }

    public void setInstantWaterConsumption(Integer instantWaterConsumption) {
        this.instantWaterConsumption.setValue(instantWaterConsumption);
    }

    private void retrieveTargetWaterConsumption() {
        if (!FirebaseUtils.isAuthenticated()) {
            return;
        }
        FirebaseUtils.userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    targetWaterConsumption.setValue(user.getTarget());
                } else {
                    Log.w(TAG, "retrieveTargetWaterConsumption failed");
                }
            }
        });
    }

    private void retrieveInstantWaterConsumption() {
        if(!FirebaseUtils.isAuthenticated()) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateWithoutTime(date.getValue()));
        Timestamp startTime = new Timestamp(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        Timestamp endTime = new Timestamp(calendar.getTime());

        FirebaseUtils.utilitiesRef.orderBy("timestamp").startAt(startTime).endAt(endTime)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int result = 0;
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                Utility utility = document.toObject(Utility.class);
                                result += utility.getWaterConsumption();
                            }
                            instantWaterConsumption.setValue(result);
                        } else {
                            Log.w(TAG, "retrieveInstantWaterConsumption failed");
                        }
                    }
                });
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