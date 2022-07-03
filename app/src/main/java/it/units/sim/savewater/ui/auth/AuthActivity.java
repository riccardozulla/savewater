package it.units.sim.savewater.ui.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.units.sim.savewater.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = "AuthActivity";
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}