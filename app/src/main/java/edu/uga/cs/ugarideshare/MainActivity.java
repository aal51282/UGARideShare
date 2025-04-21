package edu.uga.cs.ugarideshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import edu.uga.cs.ugarideshare.fragments.AcceptedRidesFragment;
import edu.uga.cs.ugarideshare.fragments.RideOffersFragment;
import edu.uga.cs.ugarideshare.fragments.RideRequestsFragment;
import edu.uga.cs.ugarideshare.utils.SessionManager;

/**
 * Main activity for the app, contains navigation drawer and hosts fragments.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private SessionManager sessionManager;
    private TextView tvUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            // User is not logged in, redirect to login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up the header view
        View headerView = navigationView.getHeaderView(0);
        tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
        tvUserEmail.setText(sessionManager.getUserEmail());

        // Set up drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // If we're starting the app, show ride offers by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RideOffersFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_ride_offers);
            getSupportActionBar().setTitle("Ride Offers");
        }
    }

    /**
     * Handle navigation item selection.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        Intent intent = null;

        // Determine which item was selected
        int itemId = item.getItemId();
        if (itemId == R.id.nav_ride_offers) {
            selectedFragment = new RideOffersFragment();
            getSupportActionBar().setTitle("Ride Offers");
        } else if (itemId == R.id.nav_ride_requests) {
            selectedFragment = new RideRequestsFragment();
            getSupportActionBar().setTitle("Ride Requests");
        } else if (itemId == R.id.nav_accepted_rides) {
            selectedFragment = new AcceptedRidesFragment();
            getSupportActionBar().setTitle("Accepted Rides");
        } else if (itemId == R.id.nav_post_offer) {
            intent = new Intent(MainActivity.this, PostRideOfferActivity.class);
        } else if (itemId == R.id.nav_post_request) {
            intent = new Intent(MainActivity.this, PostRideRequestActivity.class);
        } else if (itemId == R.id.nav_logout) {
            // Log out user
            sessionManager.logout();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        // Open the selected fragment or activity
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        } else if (intent != null) {
            startActivity(intent);
        }

        // Close the drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Handle back button press.
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handle configuration changes.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // We don't need to save state here as the navigation drawer handles the state
    }
}