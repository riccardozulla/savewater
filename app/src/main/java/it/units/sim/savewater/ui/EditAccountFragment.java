package it.units.sim.savewater.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import it.units.sim.savewater.R;
import it.units.sim.savewater.model.User;
import it.units.sim.savewater.ui.auth.AuthActivity;
import it.units.sim.savewater.utils.FirebaseUtils;

public class EditAccountFragment extends PreferenceFragmentCompat {

    private static final String TAG = "EditAccountFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.edit_account_preferences, rootKey);

        EditTextPreference editName = findPreference("edit_name");
        editName.setOnPreferenceChangeListener((preference, newValue) -> {
            if (isValidName(newValue.toString()))
                FirebaseUtils.userRef.update("name", newValue);
            return false;
        });
        EditTextPreference editSurname = findPreference("edit_surname");
        editSurname.setOnPreferenceChangeListener((preference, newValue) -> {
            if (isValidName(newValue.toString()))
                FirebaseUtils.userRef.update("surname", newValue);
            return false;
        });
        EditTextPreference editEmail = findPreference("edit_email");
        editEmail.setOnPreferenceChangeListener((preference, newValue) -> {
            if (isValidEmail(newValue.toString())) {
                FirebaseUtils.changeEmail(newValue.toString());
            }
            return false;
        });
        Preference changePassword = findPreference("edit_password");
        changePassword.setOnPreferenceClickListener(preference -> {
            FirebaseUtils.sendResetPassword();
            return false;
        });
        Preference delete = findPreference("delete_account");
        delete.setOnPreferenceClickListener(preference -> {
            showDeleteAlert();
            return true;
        });
        FirebaseUtils.userRef.addSnapshotListener((value, error) -> {
            User user = value.toObject(User.class);
            editName.setSummary(user.getName());
            editSurname.setSummary(user.getSurname());
            editEmail.setSummary(user.getEmail());

        });
    }

    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showDeleteAlert() {
        Log.d(TAG, "delete");
        new AlertDialog.Builder(requireContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getResources().getString(R.string.delete))
                .setMessage(getResources().getString(R.string.confirm_delete))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    FirebaseUtils.deleteAccount();
                    requireActivity().finish();
                    startActivity(new Intent(requireActivity(), AuthActivity.class));
                }).setNegativeButton(getResources().getString(R.string.no), null)
                .show();
    }
}