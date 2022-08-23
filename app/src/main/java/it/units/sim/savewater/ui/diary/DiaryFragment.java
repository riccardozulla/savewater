package it.units.sim.savewater.ui.diary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import it.units.sim.savewater.R;
import it.units.sim.savewater.databinding.FragmentDiaryBinding;
import it.units.sim.savewater.ui.UtilityAdapter;
import it.units.sim.savewater.ui.home.DashboardViewModel;
import it.units.sim.savewater.utils.FirebaseUtils;

public class DiaryFragment extends Fragment implements UtilityAdapter.OnUtilitySelectedListener {

    public static final float ALPHA_FULL = 1.0f;
    private static final String TAG = "DiaryFragment";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
    MaterialDatePicker<Long> datePicker;
    private UtilityAdapter mAdapter;
    private FragmentDiaryBinding binding;
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        String id = mAdapter.retrieveSnapshot(viewHolder.getAbsoluteAdapterPosition()).getId();
                        Log.d(TAG, id);
                        FirebaseUtils.utilitiesRef.document(id).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(requireView(), "Deleted", Snackbar.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(requireView(), "Error deleting document", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                            // Get RecyclerView item from the ViewHolder
                            View itemView = viewHolder.itemView;

                            Paint p = new Paint();
                            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_trash_50);
                            p.setColor(Color.RED);
                            c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                    (float) itemView.getRight(), (float) itemView.getBottom(), p);

                            c.drawBitmap(icon,
                                    (float) itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                                    (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                                    p);

                            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                            viewHolder.itemView.setAlpha(alpha);
                            viewHolder.itemView.setTranslationX(dX);

                        } else {
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }
                    }

                    private int convertDpToPx(int dp) {
                        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
    public void onUtilitySelected(DocumentSnapshot snapshot) {
        Log.d(TAG, "utility selected");
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