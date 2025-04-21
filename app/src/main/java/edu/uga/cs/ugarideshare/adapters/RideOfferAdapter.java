package edu.uga.cs.ugarideshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import edu.uga.cs.ugarideshare.R;
import edu.uga.cs.ugarideshare.models.RideOffer;

/**
 * Adapter for displaying ride offers in a RecyclerView.
 */
public class RideOfferAdapter extends RecyclerView.Adapter<RideOfferAdapter.RideOfferViewHolder> {
    private Context context;
    private List<RideOffer> rideOffers;
    private String currentUserId;
    private OnRideOfferClickListener listener;
    private SimpleDateFormat dateFormatter;

    /**
     * Interface for handling ride offer item clicks.
     */
    public interface OnRideOfferClickListener {
        void onAcceptClick(RideOffer rideOffer);
        void onUpdateClick(RideOffer rideOffer);
        void onDeleteClick(RideOffer rideOffer);
    }

    /**
     * Constructor for RideOfferAdapter.
     * @param context Context
     * @param rideOffers List of ride offers
     * @param currentUserId ID of the current user
     * @param listener Listener for item clicks
     */
    public RideOfferAdapter(Context context, List<RideOffer> rideOffers, String currentUserId, OnRideOfferClickListener listener) {
        this.context = context;
        this.rideOffers = rideOffers;
        this.currentUserId = currentUserId;
        this.listener = listener;
        this.dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.US);
    }

    @NonNull
    @Override
    public RideOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ride_offer, parent, false);
        return new RideOfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideOfferViewHolder holder, int position) {
        RideOffer rideOffer = rideOffers.get(position);
        holder.bind(rideOffer);
    }

    @Override
    public int getItemCount() {
        return rideOffers.size();
    }

    /**
     * ViewHolder for ride offer items.
     */
    class RideOfferViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDateTime, tvStartPoint, tvDestination, tvDriver;
        private Button btnAccept, btnUpdate, btnDelete;

        public RideOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStartPoint = itemView.findViewById(R.id.tvStartPoint);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDriver = itemView.findViewById(R.id.tvDriver);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        /**
         * Bind data to views.
         * @param rideOffer Ride offer to display
         */
        public void bind(final RideOffer rideOffer) {
            // Set text views
            tvDateTime.setText(dateFormatter.format(rideOffer.getDateTime()));
            tvStartPoint.setText("From: " + rideOffer.getStartPoint());
            tvDestination.setText("To: " + rideOffer.getDestination());
            tvDriver.setText("Driver: " + rideOffer.getDriverEmail());

            // Show/hide buttons based on whether the current user is the driver
            boolean isDriver = currentUserId.equals(rideOffer.getDriverId());
            btnAccept.setVisibility(isDriver ? View.GONE : View.VISIBLE);
            btnUpdate.setVisibility(isDriver ? View.VISIBLE : View.GONE);
            btnDelete.setVisibility(isDriver ? View.VISIBLE : View.GONE);

            // Set button click listeners
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAcceptClick(rideOffer);
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUpdateClick(rideOffer);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(rideOffer);
                }
            });
        }
    }
}