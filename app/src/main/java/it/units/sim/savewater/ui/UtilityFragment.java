package it.units.sim.savewater.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import it.units.sim.savewater.R;
import it.units.sim.savewater.databinding.FragmentUtilityBinding;
import it.units.sim.savewater.model.Utility;

public class UtilityFragment extends Fragment implements UtilityAdapter.OnUtilitySelectedListener {

    private static final String TAG = "UtilityFragment";
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private UtilityAdapter mAdapter;
    private FragmentUtilityBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentUtilityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Query query = firebaseFirestore.collection("utilities");

        // RecyclerView
        mAdapter = new UtilityAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                //TODO: do something when data retrieved
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(binding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        RecyclerView recyclerView = binding.utilityList;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query q = firebaseFirestore.collection("utilities").whereGreaterThanOrEqualTo("name", newText).whereLessThanOrEqualTo("name", newText + '~');
                mAdapter.setQuery(q);
                return true;
            }
        });

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
        Utility utility = snapshot.toObject(Utility.class);

        Bundle bundle = new Bundle();
        bundle.putString(UtilityDetailFragment.ARG_TITLE, utility.getName());
        bundle.putInt(UtilityDetailFragment.ARG_WATER_CONSUMPTION, utility.getWaterConsumption());
        bundle.putString(UtilityDetailFragment.ARG_DESCRIPTION, utility.getDescription());

        Navigation.findNavController(requireView()).navigate(R.id.action_utilityFragment_to_utilityDetailFragment, bundle);
    }
}