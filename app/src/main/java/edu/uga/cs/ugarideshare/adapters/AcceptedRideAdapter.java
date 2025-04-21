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
import edu.uga.cs.ugarideshare.models.AcceptedRide;

/**
 * Adapter for displaying accepted rides in a RecyclerView.
 */
public class AcceptedRideAdapter extends RecyclerView.Adapter<AcceptedRideAdapter.AcceptedRideViewHolder> {
    private Context context;
    private List<AcceptedRide> acceptedRides;
    private String currentUserId;
    private OnRideConfirmClickListener listener;
    private SimpleDateFormat dateFormatter;

    /**
     * Interface for handling ride confirm button clicks.
     */
    public interface OnRideConfirmClickListener {
        void onConfirmClick(AcceptedRide acceptedRide);
    }

    /**
     * Constructor for AcceptedRideAdapter.
     * @param context Context
     * @param acceptedRides List of accepted rides
     * @param currentUserId ID of the current user
     * @param listener Listener for confirm button clicks
     */
    public AcceptedRideAdapter(Context context, List<AcceptedRide> acceptedRides, String currentUserId, OnRideConfirmClickListener listener) {
        this.context = context;
        this.acceptedRides = acceptedRides;
        this.currentUserId = currentUserId;
        this.listener = listener;
        this.dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.US);
    }

    @NonNull
    @Override
    public AcceptedRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_accepted_ride, parent, false);
        return new AcceptedRideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AcceptedRideViewHolder holder, int position) {
        AcceptedRide acceptedRide = acceptedRides.get(position);
        holder.bind(acceptedRide);
    }

    @Override
    public int getItemCount() {
        return acceptedRides.size();
    }

    /**
     * ViewHolder for accepted ride items.
     */
    class AcceptedRideViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDateTime, tvStartPoint, tvDestination, tvRider, tvDriver, tvPoints, tvStatus;
        private Button btnConfirm;

        public AcceptedRideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStartPoint = itemView.findViewById(R.id.tvStartPoint);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvRider = itemView.findViewById(R.id.tvRider);
            tvDriver = itemView.findViewById(R.id.tvDriver);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
        }

        /**
         * Bind data to views.
         * @param acceptedRide Accepted ride to display
         */
        public void bind(final AcceptedRide acceptedRide) {
            // Set text views
            tvDateTime.setText(dateFormatter.format(acceptedRide.getDateTime()));
            tvStartPoint.setText("From: " + acceptedRide.getStartPoint());
            tvDestination.setText("To: " + acceptedRide.getDestination());
            tvRider.setText("Rider: " + acceptedRide.getRiderEmail());
            tvDriver.setText("Driver: " + acceptedRide.getDriverEmail());
            tvPoints.setText("Points: " + acceptedRide.getPoints());

            // Determine if the current user is the driver or rider
            boolean isDriver = currentUserId.equals(acceptedRide.getDriverId());
            boolean isRider = currentUserId.equals(acceptedRide.getRiderId());

            // Update status text and confirm button visibility based on confirmation status
            if (isDriver) {
                if (acceptedRide.isDriverConfirmed()) {
                    tvStatus.setText("Status: You have confirmed this ride");
                    btnConfirm.setVisibility(View.GONE);
                } else {
                    tvStatus.setText("Status: Awaiting your confirmation");
                    btnConfirm.setVisibility(View.VISIBLE);
                }
            } else if (isRider) {
                if (acceptedRide.isRiderConfirmed()) {
                    tvStatus.setText("Status: You have confirmed this ride");
                    btnConfirm.setVisibility(View.GONE);
                } else {
                    tvStatus.setText("Status: Awaiting your confirmation");
                    btnConfirm.setVisibility(View.VISIBLE);
                }
            }

            // Set confirm button click listener
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onConfirmClick(acceptedRide);
                }
            });
        }
    }
}