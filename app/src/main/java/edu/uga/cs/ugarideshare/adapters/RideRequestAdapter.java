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
import edu.uga.cs.ugarideshare.models.RideRequest;

/**
 * Adapter for displaying ride requests in a RecyclerView.
 */
public class RideRequestAdapter extends RecyclerView.Adapter<RideRequestAdapter.RideRequestViewHolder> {
    private Context context;
    private List<RideRequest> rideRequests;
    private String currentUserId;
    private OnRideRequestClickListener listener;
    private SimpleDateFormat dateFormatter;

    /**
     * Interface for handling ride request item clicks.
     */
    public interface OnRideRequestClickListener {
        void onAcceptClick(RideRequest rideRequest);
        void onUpdateClick(RideRequest rideRequest);
        void onDeleteClick(RideRequest rideRequest);
    }

    /**
     * Constructor for RideRequestAdapter.
     * @param context Context
     * @param rideRequests List of ride requests
     * @param currentUserId ID of the current user
     * @param listener Listener for item clicks
     */
    public RideRequestAdapter(Context context, List<RideRequest> rideRequests, String currentUserId, OnRideRequestClickListener listener) {
        this.context = context;
        this.rideRequests = rideRequests;
        this.currentUserId = currentUserId;
        this.listener = listener;
        this.dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.US);
    }

    /**
     * Create a new RideRequestViewHolder.
     * @param parent Parent view group
     * @param viewType View type
     * @return New RideRequestViewHolder
     */
    @NonNull
    @Override
    public RideRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ride_request, parent, false);
        return new RideRequestViewHolder(view);
    }

    /**
     * Bind data to a RideRequestViewHolder.
     * @param holder RideRequestViewHolder to bind data to
     * @param position Position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull RideRequestViewHolder holder, int position) {
        RideRequest rideRequest = rideRequests.get(position);
        holder.bind(rideRequest);
    }

    /**
     * Get the number of items in the list.
     * @return Number of items
     */
    @Override
    public int getItemCount() {
        return rideRequests.size();
    } // getItemCount

    /**
     * ViewHolder for ride request items.
     */
    class RideRequestViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDateTime, tvStartPoint, tvDestination, tvRider;
        private Button btnAccept, btnUpdate, btnDelete;

        public RideRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStartPoint = itemView.findViewById(R.id.tvStartPoint);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvRider = itemView.findViewById(R.id.tvRider);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        } // Constructor

        /**
         * Bind data to views.
         * @param rideRequest Ride request to display
         */
        public void bind(final RideRequest rideRequest) {
            // Set text views
            tvDateTime.setText(dateFormatter.format(rideRequest.getDateTime()));
            tvStartPoint.setText("From: " + rideRequest.getStartPoint());
            tvDestination.setText("To: " + rideRequest.getDestination());
            tvRider.setText("Rider: " + rideRequest.getRiderEmail());

            // Show/hide buttons based on whether the current user is the rider
            boolean isRider = currentUserId.equals(rideRequest.getRiderId());
            btnAccept.setVisibility(isRider ? View.GONE : View.VISIBLE);
            btnUpdate.setVisibility(isRider ? View.VISIBLE : View.GONE);
            btnDelete.setVisibility(isRider ? View.VISIBLE : View.GONE);

            // Set button click listeners
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAcceptClick(rideRequest);
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUpdateClick(rideRequest);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(rideRequest);
                }
            });
        } // bind
    } // RideRequestViewHolder
} // RideRequestAdapter