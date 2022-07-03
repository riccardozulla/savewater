package it.units.sim.savewater.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import it.units.sim.savewater.R;
import it.units.sim.savewater.databinding.FragmentRegistrationBinding;
import it.units.sim.savewater.model.User;

public class RegistrationFragment extends Fragment {
    private static final String TAG = "RegistrationFragment";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
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
                signUp();

            }
        });

        return binding.getRoot();
    }

    private void signUp() {
        if (!areInputsValid()) {
            Snackbar.make(requireView(), "Inputs not valid", Snackbar.LENGTH_LONG).show();
            return;
        }

        showProgressBar();
        firebaseAuth.createUserWithEmailAndPassword(
                        binding.editTextNewEmail.getText().toString(), binding.editTextNewPassword.getText().toString()).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Log.w(TAG, "User creation failed");
                            Snackbar.make(requireView(), "Sign up failed", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser firebaseUser) {
        String name = binding.editTextName.getText().toString();
        String surname = binding.editTextSurname.getText().toString();
        writeUserInfo(firebaseUser.getUid(), name, surname);
        Navigation.findNavController(requireView()).navigate(R.id.action_registrationFragment_to_nav_home);
    }

    private void writeUserInfo(String uid, String name, String surname) {
        User user = new User(name, surname);
        firebaseFirestore.collection("users").document(uid).set(user);

    }

    private boolean areInputsValid() {
        boolean result = true;

        if (TextUtils.isEmpty(binding.editTextName.getText()) || !binding.editTextName.getText().toString().matches("[a-zA-Z]+")) {
            binding.editTextName.setError("Not valid");
            result = false;
        }

        if (TextUtils.isEmpty(binding.editTextSurname.getText()) || !binding.editTextSurname.getText().toString().matches("[a-zA-Z]+")) {
            binding.editTextSurname.setError("Not valid");
            result = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(binding.editTextNewEmail.getText()).matches()) {
            binding.editTextNewEmail.setError("Email format not valid");
            result = false;
        }

        if (binding.editTextNewPassword.getText().length() < 6) {
            binding.editTextNewPassword.setError("Password must have at least 6 characters");
            result = false;
        }

        if (!TextUtils.equals(binding.editTextNewPassword.getText().toString(), binding.editTextConfirmPassword.getText().toString())) {
            binding.editTextConfirmPassword.setError("Passwords must be equal");
            result = false;
        }

        return result;
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