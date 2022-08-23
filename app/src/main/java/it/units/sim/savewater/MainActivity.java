package it.units.sim.savewater;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

import it.units.sim.savewater.databinding.ActivityMainBinding;
import it.units.sim.savewater.model.User;
import it.units.sim.savewater.ui.auth.AuthActivity;
import it.units.sim.savewater.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUtils.init();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_diary, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, mNavController);


        View headerView = binding.navView.getHeaderView(0);
        TextView headerName = headerView.findViewById(R.id.text_view_nav_header_name);
        TextView headerEmail = headerView.findViewById(R.id.text_view_nav_header_email);

        if (FirebaseUtils.userRef != null) {
            FirebaseUtils.userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    User user = value.toObject(User.class);
                    headerName.setText(user.getName());
                    headerEmail.setText(user.getEmail());
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!FirebaseUtils.isAuthenticated()) {
            startActivity(new Intent(this, AuthActivity.class));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}