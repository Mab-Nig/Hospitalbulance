package project.cs426.hospitalbulance;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeScreenPersonalActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_personal);

        drawerLayout = findViewById(R.id.drawer_layout);
        RelativeLayout navigationView = findViewById(R.id.sidebar_layout);
        // Initialize Firebase
        // Initialize Firebase if it hasn't been initialized yet
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(getApplicationContext());
        }

// Get Firestore instance
        db = FirebaseFirestore.getInstance();

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        findViewById(R.id.sidebar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });
        findViewById(R.id.back_sidebar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(navigationView);
            }
        });
        findViewById(R.id.sidebar_image2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreenPersonalActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.sidebar_image3).setOnClickListener(v -> showLogoutConfirmationDialog());
        findViewById(R.id.sidebar_image1).setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenPersonalActivity.this, EditInfo.class);
            startActivity(intent);
        });

        addSignOutListener();
    }

    public void logoutClicked(View view) {
        FirebaseAuth.getInstance().signOut();
    }

    private void addSignOutListener() {
        FirebaseAuth.IdTokenListener signOutListener = auth -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                Intent intent = new Intent(HomeScreenPersonalActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };
        FirebaseAuth.getInstance().addIdTokenListener(signOutListener);
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenPersonalActivity.this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        builder.setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(true)
                .setPositiveButton("Logout", (dialog, id) -> {
                    Intent intent = new Intent(HomeScreenPersonalActivity.this,SignupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store email and password in SharedPreferences
        editor.putString("email", email);
        editor.putString("password", password);

        // Apply changes
        editor.apply();
    }
}
