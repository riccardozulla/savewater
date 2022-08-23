package it.units.sim.savewater.ui.home;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.units.sim.savewater.model.User;
import it.units.sim.savewater.utils.FirebaseUtils;

public class DashboardViewModel extends ViewModel {

    private static final String TAG = "DashboardViewModel";
    private final MutableLiveData<Integer> targetWaterConsumption;
    private final MutableLiveData<List<DocumentSnapshot>> dailyWaterConsumption;
    private Date date;

    public DashboardViewModel() {
        this.targetWaterConsumption = new MutableLiveData<>();
        this.dailyWaterConsumption = new MutableLiveData<>();
        addTargetListener();

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        Log.d(TAG, "Setting date");
        this.date = date;
        addDailyWaterConsumptionListener(date);
    }


    private void addTargetListener() {
        Log.d(TAG, "addTargetListener");
        if (!FirebaseUtils.isAuthenticated()) {
            Log.d(TAG, "Not authenticated");
            return;
        }
        FirebaseUtils.userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                targetWaterConsumption.setValue(user.getTarget());
            }
        });
    }

    private void addDailyWaterConsumptionListener(Date date) {
        if (!FirebaseUtils.isAuthenticated())
            return;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateWithoutTime(date));
        Timestamp startTime = new Timestamp(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        Timestamp endTime = new Timestamp(calendar.getTime());

        FirebaseUtils.utilitiesRef.orderBy("timestamp").startAt(startTime).endAt(endTime).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                dailyWaterConsumption.setValue(value.getDocuments());
            }
        });

    }

    public MutableLiveData<Integer> getTargetWaterConsumption() {
        return targetWaterConsumption;
    }

    public MutableLiveData<List<DocumentSnapshot>> getDailyWaterConsumption() {
        return dailyWaterConsumption;
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