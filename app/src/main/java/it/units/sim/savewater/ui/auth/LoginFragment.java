package it.units.sim.savewater.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import it.units.sim.savewater.MainActivity;
import it.units.sim.savewater.R;
import it.units.sim.savewater.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.buttonLogin.setOnClickListener(v -> signIn());
        binding.linkToRegistration.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registrationFragment));

        return binding.getRoot();
    }

    private void signIn() {
        String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        showProgressBar();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).
                addOnCompleteListener(task -> {
                    hideProgressBar();
                    if (task.isSuccessful()) {
                        onAuthSuccess();
                    } else {
                        Log.w(TAG, "Login failed");
                        binding.editTextEmail.setError("Wrong email");
                        binding.editTextPassword.setError("Wrong password");
                        Snackbar.make(requireView(), "Login failed", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void onAuthSuccess() {
        startActivity(new Intent(requireActivity(), MainActivity.class));
    }

    private void showProgressBar() {
        binding.progressBarLogin.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.progressBarLogin.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}