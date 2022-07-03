package it.units.sim.savewater.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.units.sim.savewater.R;
import it.units.sim.savewater.databinding.FragmentRegistrationBinding;

public class RegistrationFragment extends Fragment {
    private static final String TAG = "RegistrationFragment";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FragmentRegistrationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RegistrationViewModel loginViewModel =
                new ViewModelProvider(this).get(RegistrationViewModel.class);

        binding = FragmentRegistrationBinding.inflate(inflater, container, false);

        binding.linkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_registrationFragment_to_loginFragment);
            }
        });
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                createNewUser();

            }
        });

        return binding.getRoot();
    }

    private void createNewUser() {
        Editable email = binding.editTextNewEmail.getText();
        Editable password = binding.editTextNewPassword.getText();
        firebaseAuth.createUserWithEmailAndPassword(email.toString(), password.toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "User created");
                onAuthSuccess(authResult.getUser());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "User creation failed");
                Snackbar.make(requireView(), "Sign up failed", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onAuthSuccess(FirebaseUser user) {
        hideProgressBar();
        Navigation.findNavController(requireView()).navigate(R.id.action_registrationFragment_to_nav_home);
    }

    private void showProgressBar() {
        binding.progressBarRegistration.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.progressBarRegistration.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}