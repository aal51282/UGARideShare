package edu.uga.cs.ugarideshare.fragments;

import android.content.Intent;
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
import edu.uga.cs.ugarideshare.UpdateRideActivity;
import edu.uga.cs.ugarideshare.adapters.RideRequestAdapter;
import edu.uga.cs.ugarideshare.models.AcceptedRide;
import edu.uga.cs.ugarideshare.models.RideRequest;
import edu.uga.cs.ugarideshare.utils.FirebaseCallback;
import edu.uga.cs.ugarideshare.utils.FirebaseUtil;
import edu.uga.cs.ugarideshare.utils.SessionManager;

/**
 * Fragment for displaying available ride requests.
 */
public class RideRequestsFragment extends Fragment implements RideRequestAdapter.OnRideRequestClickListener {
    private RecyclerView recyclerView;
    private RideRequestAdapter adapter;
    private List<RideRequest> rideRequests;
    private ProgressBar progressBar;
    private TextView tvNoRequests;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_requests, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoRequests = view.findViewById(R.id.tvNoRequests);

        // Initialize session manager
        sessionManager = new SessionManager(getContext());

        // Initialize ride requests list
        rideRequests = new ArrayList<>();

        // Set up recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RideRequestAdapter(getContext(), rideRequests, sessionManager.getUserId(), this);
        recyclerView.setAdapter(adapter);

        // Load ride requests
        loadRideRequests();

        return view;
    }

    /**
     * Load ride requests from Firebase.
     */
    private void loadRideRequests() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Load ride requests from Firebase
        FirebaseUtil.getAvailableRideRequests(new FirebaseCallback<List<RideRequest>>() {
            @Override
            public void onSuccess(List<RideRequest> result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Sort by date/time (soonest first)
                Collections.sort(result, new Comparator<RideRequest>() {
                    @Override
                    public int compare(RideRequest o1, RideRequest o2) {
                        return Long.compare(o1.getDateTime(), o2.getDateTime());
                    }
                });

                // Update adapter
                rideRequests.clear();
                rideRequests.addAll(result);
                adapter.notifyDataSetChanged();

                // Show/hide no requests text
                if (rideRequests.isEmpty()) {
                    tvNoRequests.setVisibility(View.VISIBLE);
                } else {
                    tvNoRequests.setVisibility(View.GONE);
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
    }

    /**
     * Handle ride request accept button click.
     */
    @Override
    public void onAcceptClick(RideRequest rideRequest) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Accept ride request
        FirebaseUtil.acceptRideRequest(rideRequest, sessionManager.getUserId(), sessionManager.getUserEmail(), new FirebaseCallback<AcceptedRide>() {
            @Override
            public void onSuccess(AcceptedRide result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show success message
                Toast.makeText(getContext(), "Ride request accepted successfully", Toast.LENGTH_SHORT).show();

                // Remove accepted ride request from list
                rideRequests.remove(rideRequest);
                adapter.notifyDataSetChanged();

                // Show/hide no requests text
                if (rideRequests.isEmpty()) {
                    tvNoRequests.setVisibility(View.VISIBLE);
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
    }

    /**
     * Handle ride request update button click.
     */
    @Override
    public void onUpdateClick(RideRequest rideRequest) {
        // Create intent to open update activity
        Intent intent = new Intent(getActivity(), UpdateRideActivity.class);
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_TYPE, UpdateRideActivity.TYPE_REQUEST);
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_ID, rideRequest.getId());
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_DATETIME, rideRequest.getDateTime());
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_START, rideRequest.getStartPoint());
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_DESTINATION, rideRequest.getDestination());
        startActivity(intent);
    }

    /**
     * Handle ride request delete button click.
     */
    @Override
    public void onDeleteClick(RideRequest rideRequest) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Delete ride request
        FirebaseUtil.deleteRideRequest(rideRequest.getId(), new FirebaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show success message
                Toast.makeText(getContext(), "Ride request deleted successfully", Toast.LENGTH_SHORT).show();

                // Remove deleted ride request from list
                rideRequests.remove(rideRequest);
                adapter.notifyDataSetChanged();

                // Show/hide no requests text
                if (rideRequests.isEmpty()) {
                    tvNoRequests.setVisibility(View.VISIBLE);
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
    }

    /**
     * Reload ride requests when fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadRideRequests();
    }
}