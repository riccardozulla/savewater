package it.units.sim.savewater.ui.diary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import it.units.sim.savewater.databinding.FragmentDiaryBinding;
import it.units.sim.savewater.ui.UtilityAdapter;

public class DiaryFragment extends Fragment implements UtilityAdapter.OnUtilitySelectedListener {

    private static final String TAG = "DiaryFragment";
    private UtilityAdapter mAdapter;
    private FragmentDiaryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Query query = FirebaseFirestore.getInstance().collection("utilities");

        // RecyclerView
        mAdapter = new UtilityAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                //TODO: do something when data retrieved
                Log.d(TAG, "Something retrieved: " + getItemCount());
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

        //TODO: check if user is authenticated

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
}