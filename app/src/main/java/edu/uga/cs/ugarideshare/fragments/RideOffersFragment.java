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
import edu.uga.cs.ugarideshare.adapters.RideOfferAdapter;
import edu.uga.cs.ugarideshare.models.AcceptedRide;
import edu.uga.cs.ugarideshare.models.RideOffer;
import edu.uga.cs.ugarideshare.utils.FirebaseCallback;
import edu.uga.cs.ugarideshare.utils.FirebaseUtil;
import edu.uga.cs.ugarideshare.utils.SessionManager;

/**
 * Fragment for displaying available ride offers.
 */
public class RideOffersFragment extends Fragment implements RideOfferAdapter.OnRideOfferClickListener {
    private RecyclerView recyclerView;
    private RideOfferAdapter adapter;
    private List<RideOffer> rideOffers;
    private ProgressBar progressBar;
    private TextView tvNoOffers;
    private SessionManager sessionManager;

    /**
     * Create a new instance of the fragment.
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
        View view = inflater.inflate(R.layout.fragment_ride_offers, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoOffers = view.findViewById(R.id.tvNoOffers);

        // Initialize session manager
        sessionManager = new SessionManager(getContext());

        // Initialize ride offers list
        rideOffers = new ArrayList<>();

        // Set up recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RideOfferAdapter(getContext(), rideOffers, sessionManager.getUserId(), this);
        recyclerView.setAdapter(adapter);

        // Load ride offers
        loadRideOffers();

        return view;
    } // onCreateView

    /**
     * Load ride offers from Firebase.
     */
    private void loadRideOffers() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Load ride offers from Firebase
        FirebaseUtil.getAvailableRideOffers(new FirebaseCallback<List<RideOffer>>() {
            @Override
            public void onSuccess(List<RideOffer> result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Sort by date/time (soonest first)
                Collections.sort(result, new Comparator<RideOffer>() {
                    @Override
                    public int compare(RideOffer o1, RideOffer o2) {
                        return Long.compare(o1.getDateTime(), o2.getDateTime());
                    }
                });

                // Update adapter
                rideOffers.clear();
                rideOffers.addAll(result);
                adapter.notifyDataSetChanged();

                // Show/hide no offers text
                if (rideOffers.isEmpty()) {
                    tvNoOffers.setVisibility(View.VISIBLE);
                } else {
                    tvNoOffers.setVisibility(View.GONE);
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
    } // loadRideOffers

    /**
     * Handle ride offer accept button click.
     */
    @Override
    public void onAcceptClick(RideOffer rideOffer) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Accept ride offer
        FirebaseUtil.acceptRideOffer(rideOffer, sessionManager.getUserId(), sessionManager.getUserEmail(), new FirebaseCallback<AcceptedRide>() {
            @Override
            public void onSuccess(AcceptedRide result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show success message
                Toast.makeText(getContext(), "Ride offer accepted successfully", Toast.LENGTH_SHORT).show();

                // Remove accepted ride offer from list
                rideOffers.remove(rideOffer);
                adapter.notifyDataSetChanged();

                // Show/hide no offers text
                if (rideOffers.isEmpty()) {
                    tvNoOffers.setVisibility(View.VISIBLE);
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
    } // onAcceptClick

    /**
     * Handle ride offer update button click.
     */
    @Override
    public void onUpdateClick(RideOffer rideOffer) {
        // Create intent to open update activity
        Intent intent = new Intent(getActivity(), UpdateRideActivity.class);
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_TYPE, UpdateRideActivity.TYPE_OFFER);
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_ID, rideOffer.getId());
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_DATETIME, rideOffer.getDateTime());
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_START, rideOffer.getStartPoint());
        intent.putExtra(UpdateRideActivity.EXTRA_RIDE_DESTINATION, rideOffer.getDestination());
        startActivity(intent);
    } // onUpdateClick

    /**
     * Handle ride offer delete button click.
     */
    @Override
    public void onDeleteClick(RideOffer rideOffer) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Delete ride offer
        FirebaseUtil.deleteRideOffer(rideOffer.getId(), new FirebaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show success message
                Toast.makeText(getContext(), "Ride offer deleted successfully", Toast.LENGTH_SHORT).show();

                // Remove deleted ride offer from list
                rideOffers.remove(rideOffer);
                adapter.notifyDataSetChanged();

                // Show/hide no offers text
                if (rideOffers.isEmpty()) {
                    tvNoOffers.setVisibility(View.VISIBLE);
                }
            } // onSuccess

            @Override
            public void onError(String error) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show error message
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            } // onError
        });
    } // onDeleteClick

    /**
     * Reload ride offers when fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadRideOffers();
    } // onResume
} // RideOffersFragment