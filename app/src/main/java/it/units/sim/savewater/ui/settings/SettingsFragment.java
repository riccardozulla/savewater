package it.units.sim.savewater.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.Navigation;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.snackbar.Snackbar;

import it.units.sim.savewater.R;
import it.units.sim.savewater.model.User;
import it.units.sim.savewater.ui.auth.AuthActivity;
import it.units.sim.savewater.utils.FirebaseUtils;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference editAccount = findPreference("edit_account");
        editAccount.setOnPreferenceClickListener(preference -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_nav_settings_to_nav_edit_account);
            return true;
        });

        EditTextPreference target = findPreference("edit_target");
        target.setOnPreferenceChangeListener((preference, newValue) -> {
            if (isValidNumber((String) newValue)) {
                FirebaseUtils.userRef.update("target", Integer.valueOf((String) newValue));
            } else {
                Snackbar.make(requireView(), "Value must be a number", Snackbar.LENGTH_LONG).show();
            }
            return false;
        });

        SwitchPreferenceCompat switchDarkTheme = findPreference("enable_dark_theme");
        switchDarkTheme.setOnPreferenceClickListener((preference) -> {
            if (switchDarkTheme.isChecked()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            return false;
        });


        Preference logOut = findPreference("sign_out");
        logOut.setOnPreferenceClickListener(preference -> {
            FirebaseUtils.signOut();
            requireActivity().finish();
            startActivity(new Intent(requireActivity(), AuthActivity.class));
            return false;
        });

        FirebaseUtils.userRef.addSnapshotListener((value, error) -> {
            User user = value.toObject(User.class);
            target.setSummary(user.getTarget() + " liters per day");
        });
    }

    private boolean isValidNumber(String number) {
        return number.matches("[0-9]+");

    }
}