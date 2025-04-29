package edu.uga.cs.ugarideshare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.uga.cs.ugarideshare.R;
import edu.uga.cs.ugarideshare.adapters.AcceptedRideAdapter;
import edu.uga.cs.ugarideshare.models.AcceptedRide;
import edu.uga.cs.ugarideshare.utils.FirebaseCallback;
import edu.uga.cs.ugarideshare.utils.FirebaseUtil;
import edu.uga.cs.ugarideshare.utils.SessionManager;

/**
 * Fragment for displaying accepted rides.
 */
public class AcceptedRidesFragment extends Fragment implements AcceptedRideAdapter.OnRideConfirmClickListener {
    private RecyclerView recyclerView;
    private AcceptedRideAdapter adapter;
    private List<AcceptedRide> acceptedRides;
    private ProgressBar progressBar;
    private TextView tvNoRides;
    private SessionManager sessionManager;

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted_rides, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoRides = view.findViewById(R.id.tvNoRides);

        // Initialize session manager
        sessionManager = new SessionManager(getContext());

        // Initialize accepted rides list
        acceptedRides = new ArrayList<>();

        // Set up recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AcceptedRideAdapter(getContext(), acceptedRides, sessionManager.getUserId(), this);
        recyclerView.setAdapter(adapter);

        // Load accepted rides
        loadAcceptedRides();

        return view;
    }

    /**
     * Load accepted rides from Firebase.
     */
    private void loadAcceptedRides() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Load accepted rides from Firebase
        FirebaseUtil.getAcceptedRidesForUser(sessionManager.getUserId(), new FirebaseCallback<List<AcceptedRide>>() {
            @Override
            public void onSuccess(List<AcceptedRide> result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Sort by date/time (soonest first)
                Collections.sort(result, new Comparator<AcceptedRide>() {
                    @Override
                    public int compare(AcceptedRide o1, AcceptedRide o2) {
                        return Long.compare(o1.getDateTime(), o2.getDateTime());
                    }
                });

                // Update adapter
                acceptedRides.clear();
                acceptedRides.addAll(result);
                adapter.notifyDataSetChanged();

                // Show/hide no rides text
                if (acceptedRides.isEmpty()) {
                    tvNoRides.setVisibility(View.VISIBLE);
                } else {
                    tvNoRides.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show error message
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    } // loadAcceptedRides

    /**
     * Handle confirm button click.
     */
    @Override
    public void onConfirmClick(AcceptedRide acceptedRide) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Determine if the current user is the driver or rider
        boolean isDriver = sessionManager.getUserId().equals(acceptedRide.getDriverId());

        // Confirm ride
        FirebaseUtil.confirmRide(acceptedRide, isDriver, new FirebaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // If both parties have confirmed, the ride will be removed from the list
                // So we need to reload the list
                loadAcceptedRides();

                // Show success message
                if (acceptedRide.isFullyConfirmed()) {
                    Toast.makeText(getContext(), "Ride completed and points transferred", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Ride confirmed, waiting for other party to confirm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show error message
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    } // onConfirmClick

    /**
     * Reload accepted rides when fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadAcceptedRides();
    } // onResume
} // AcceptedRidesFragment