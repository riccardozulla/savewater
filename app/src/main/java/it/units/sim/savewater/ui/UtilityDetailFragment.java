package it.units.sim.savewater.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.sim.savewater.R;
import it.units.sim.savewater.databinding.FragmentUtilityDetailBinding;
import it.units.sim.savewater.model.Utility;

public class UtilityDetailFragment extends Fragment {

    public static final String ARG_TITLE = "title";
    public static final String ARG_WATER_CONSUMPTION = "water_consumption";
    public static final String ARG_DESCRIPTION = "description";
    public static final int NUMBER_PICKER_MAX_VALUE = 500;
    public static final int NUMBER_PICKER_MIN_VALUE = 0;
    private static final String TAG = "UtilityDetailFragment";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FragmentUtilityDetailBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentUtilityDetailBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;

        binding.editTextUtilityTitle.setText(getArguments().getString(ARG_TITLE));
        binding.numberPickerWaterConsumption.setMaxValue(NUMBER_PICKER_MAX_VALUE);
        binding.numberPickerWaterConsumption.setMinValue(NUMBER_PICKER_MIN_VALUE);
        binding.numberPickerWaterConsumption.setValue(getArguments().getInt(ARG_WATER_CONSUMPTION));
        binding.editTextUtilityDescription.setText(getArguments().getString(ARG_DESCRIPTION));


        binding.buttonUtilityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_utilityDetailFragment_to_utilityFragment);
            }
        });

        binding.buttonUtilityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility utility = new Utility(binding.editTextUtilityTitle.getText().toString(), binding.editTextUtilityDescription.getText().toString(), binding.numberPickerWaterConsumption.getValue());
                firebaseFirestore.collection("users").document(firebaseAuth.getUid()).collection("diary").add(utility).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(requireView(), "Success", Snackbar.LENGTH_LONG).show();
                            Navigation.findNavController(v).navigate(R.id.action_utilityDetailFragment_to_utilityFragment);
                        } else {
                            Snackbar.make(requireView(), "Failed", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}