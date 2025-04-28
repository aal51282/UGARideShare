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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import edu.uga.cs.ugarideshare.fragments.AcceptedRidesFragment;
import edu.uga.cs.ugarideshare.fragments.RideOffersFragment;
import edu.uga.cs.ugarideshare.fragments.RideRequestsFragment;
import edu.uga.cs.ugarideshare.models.User;
import edu.uga.cs.ugarideshare.utils.FirebaseCallback;
import edu.uga.cs.ugarideshare.utils.FirebaseUtil;
import edu.uga.cs.ugarideshare.utils.SessionManager;

/**
 * Main activity for the app, contains navigation drawer and hosts fragments.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private SessionManager sessionManager;
    private TextView tvUserEmail;
    private TextView tvUserPoints;
    private ActionBarDrawerToggle toggle;
    private ProgressBar progressBar;
    private PointsUpdateListener pointsUpdateListener;

    /**
     * Interface for listening to points updates
     */
    private interface PointsUpdateListener {
        void onPointsUpdated(int newPoints);
    }

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

        // Ensure home button is enabled
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Set up drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        progressBar = findViewById(R.id.progressBar);

        // Set up the header view
        View headerView = navigationView.getHeaderView(0);
        tvUserEmail = headerView.findViewById(R.id.tvUserEmail);
        tvUserPoints = headerView.findViewById(R.id.tvUserPoints);

        // Set user email
        tvUserEmail.setText(sessionManager.getUserEmail());

        // Update points display
        updatePointsDisplay();

        // Register for points updates
        registerPointsUpdateListener();

        // Set up drawer toggle
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // If we're starting the app, show ride offers by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RideOffersFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_ride_offers);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Ride Offers");
            }
        }

        // Add a click listener to the toolbar to open the drawer
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * Register a listener for points updates
     */
    private void registerPointsUpdateListener() {
        String userId = sessionManager.getUserId();
        if (userId != null) {
            pointsUpdateListener = new PointsUpdateListener() {
                @Override
                public void onPointsUpdated(int newPoints) {
                    // Update the points display in the UI
                    if (tvUserPoints != null) {
                        runOnUiThread(() -> {
                            tvUserPoints.setText("Points: " + newPoints);
                        });
                    }
                }
            };
            FirebaseUtil.addPointsUpdateListener(userId, pointsUpdateListener);
        }
    }

    /**
     * Update the points display in the navigation header
     */
    private void updatePointsDisplay() {
        FirebaseUtil.getUserById(sessionManager.getUserId(), new FirebaseCallback<User>() {
            @Override
            public void onSuccess(User user) {
                // Update the points display
                tvUserPoints.setText("Points: " + user.getRidePoints());
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error getting user points: " + error);
                // Default points display in case of error
                tvUserPoints.setText("Points: --");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh points when returning to the activity
        updatePointsDisplay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up points listener
        if (pointsUpdateListener != null) {
            FirebaseUtil.removePointsUpdateListener(sessionManager.getUserId(), pointsUpdateListener);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred
        toggle.syncState();
    }

    /**
     * Handle navigation item selection.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "Navigation item selected: " + item.getTitle());
        Fragment selectedFragment = null;
        Intent intent = null;

        // Determine which item was selected
        int itemId = item.getItemId();
        if (itemId == R.id.nav_ride_offers) {
            selectedFragment = new RideOffersFragment();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Ride Offers");
            }
        } else if (itemId == R.id.nav_ride_requests) {
            selectedFragment = new RideRequestsFragment();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Ride Requests");
            }
        } else if (itemId == R.id.nav_accepted_rides) {
            selectedFragment = new AcceptedRidesFragment();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Accepted Rides");
            }
        } else if (itemId == R.id.nav_post_offer) {
            intent = new Intent(MainActivity.this, PostRideOfferActivity.class);
        } else if (itemId == R.id.nav_post_request) {
            // Check if user has enough points before allowing them to post a ride request
            checkPointsAndNavigateToPostRequest();
            return true; // Return early as we're handling the navigation ourselves
        } else if (itemId == R.id.nav_logout) {
            // Log out user from Firebase Auth
            FirebaseAuth.getInstance().signOut();

            // Log out user from session
            sessionManager.logout();

            // Redirect to login screen
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
     * Check if user has enough points to post a ride request
     * If yes, navigate to PostRideRequestActivity
     * If no, show a toast message
     */
    private void checkPointsAndNavigateToPostRequest() {
        // Show progress dialog or indicator
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Get user's current points
        FirebaseUtil.getUserById(sessionManager.getUserId(), new FirebaseCallback<User>() {
            @Override
            public void onSuccess(User user) {
                // Hide progress
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                // Define minimum points required to post a ride request
                int MINIMUM_POINTS_REQUIRED = 50;

                if (user.getRidePoints() >= MINIMUM_POINTS_REQUIRED) {
                    // User has enough points, navigate to post request activity
                    Intent intent = new Intent(MainActivity.this, PostRideRequestActivity.class);
                    startActivity(intent);
                } else {
                    // User doesn't have enough points
                    Toast.makeText(MainActivity.this,
                            "You need at least " + MINIMUM_POINTS_REQUIRED +
                                    " points to post a ride request. You currently have " +
                                    user.getRidePoints() + " points.", Toast.LENGTH_LONG).show();

                    // Suggest posting a ride offer instead
                    Toast.makeText(MainActivity.this,
                            "Try posting a ride offer to earn points!",
                            Toast.LENGTH_SHORT).show();
                }

                // Close the drawer
                drawer.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onError(String error) {
                // Hide progress
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                // Show error message
                Toast.makeText(MainActivity.this,
                        "Error checking points: " + error, Toast.LENGTH_SHORT).show();

                // Close the drawer
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * Handle options menu item selection
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle navigation toggle when clicked
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle other menu items
        return super.onOptionsItemSelected(item);
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

    /**
     * Helper method to manually open the drawer - useful for debugging
     */
    private void openDrawer() {
        drawer.openDrawer(GravityCompat.START);
        Toast.makeText(this, "Opening drawer", Toast.LENGTH_SHORT).show();
    }
}