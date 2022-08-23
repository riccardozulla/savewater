package it.units.sim.savewater.ui.diary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import it.units.sim.savewater.databinding.FragmentDiaryBinding;
import it.units.sim.savewater.ui.UtilityAdapter;
import it.units.sim.savewater.ui.home.DashboardViewModel;

public class DiaryFragment extends Fragment implements UtilityAdapter.OnUtilitySelectedListener {

    private static final String TAG = "DiaryFragment";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
    MaterialDatePicker<Long> datePicker;
    private UtilityAdapter mAdapter;
    private FragmentDiaryBinding binding;
    @ServerTimestamp
    private Timestamp timestamp;
    private DashboardViewModel dashboardViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiaryBinding.inflate(inflater, container, false);

        dashboardViewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (datePicker == null)
            datePicker = createDatePicker();

        Date now = new Date();

        binding.editTextDate.setText(formatter.format(now));
        binding.editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getChildFragmentManager(), TAG);
            }
        });
        dashboardViewModel.setDate(now);
        Query query = generateQuery(now);
        // RecyclerView
        mAdapter = new UtilityAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() < 1) {
                    binding.textViewSwipeAdvice.setVisibility(View.GONE);
                } else {
                    binding.textViewSwipeAdvice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(binding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        RecyclerView recyclerView = binding.utilityDiary;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onStart() {
        super.onStart();

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onUtilitySelected(DocumentSnapshot utility) {
        Log.w(TAG, "utility selected");
        //TODO: do something

    }

    private Query generateQuery(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Timestamp startTime = new Timestamp(calendar.getTime());
        calendar.add(Calendar.DATE, 1);
        Timestamp endTime = new Timestamp(calendar.getTime());

        return firebaseFirestore.collection("users").
                document(firebaseAuth.getCurrentUser().getUid()).
                collection("diary").orderBy("timestamp").
                startAt(startTime).endAt(endTime);

    }

    private MaterialDatePicker<Long> createDatePicker() {
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                Date date = new Date();
                date.setTime((Long) selection);
                mAdapter.setQuery(generateQuery(date));
                binding.editTextDate.setText(formatter.format(date));
                dashboardViewModel.setDate(date);
            }
        });
        return datePicker;
    }
}