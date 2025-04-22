package edu.uga.cs.ugarideshare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uga.cs.ugarideshare.models.RideOffer;
import edu.uga.cs.ugarideshare.models.RideRequest;
import edu.uga.cs.ugarideshare.utils.FirebaseCallback;
import edu.uga.cs.ugarideshare.utils.FirebaseUtil;

/**
 * Activity for updating a ride offer or request.
 */
public class UpdateRideActivity extends AppCompatActivity {
    public static final String EXTRA_RIDE_TYPE = "ride_type";
    public static final String EXTRA_RIDE_ID = "ride_id";
    public static final String EXTRA_RIDE_DATETIME = "ride_datetime";
    public static final String EXTRA_RIDE_START = "ride_start";
    public static final String EXTRA_RIDE_DESTINATION = "ride_destination";

    public static final String TYPE_OFFER = "offer";
    public static final String TYPE_REQUEST = "request";

    private Button btnSelectDateTime, btnUpdate;
    private TextView tvSelectedDateTime;
    private EditText etStartPoint, etDestination;
    private ProgressBar progressBar;
    private Calendar selectedDateTime;
    private SimpleDateFormat dateTimeFormatter;

    private String rideType;
    private String rideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ride);

        // Initialize views
        btnSelectDateTime = findViewById(R.id.btnSelectDateTime);
        btnUpdate = findViewById(R.id.btnUpdate);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);
        etStartPoint = findViewById(R.id.etStartPoint);
        etDestination = findViewById(R.id.etDestination);
        progressBar = findViewById(R.id.progressBar);

        // Initialize date formatter
        dateTimeFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.US);

        // Initialize calendar with current date/time
        selectedDateTime = Calendar.getInstance();

        // Get data from intent
        rideType = getIntent().getStringExtra(EXTRA_RIDE_TYPE);
        rideId = getIntent().getStringExtra(EXTRA_RIDE_ID);
        long dateTime = getIntent().getLongExtra(EXTRA_RIDE_DATETIME, -1);
        String startPoint = getIntent().getStringExtra(EXTRA_RIDE_START);
        String destination = getIntent().getStringExtra(EXTRA_RIDE_DESTINATION);

        // Set data to views
        if (dateTime != -1) {
            selectedDateTime.setTimeInMillis(dateTime);
            updateSelectedDateTimeText();
        }
        etStartPoint.setText(startPoint);
        etDestination.setText(destination);

        // Check if we have saved instance state
        if (savedInstanceState != null) {
            long savedDateTime = savedInstanceState.getLong("dateTime", -1);
            if (savedDateTime != -1) {
                selectedDateTime.setTimeInMillis(savedDateTime);
                updateSelectedDateTimeText();
            }
        }

        // Set click listener for date/time button
        btnSelectDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        // Set click listener for update button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRide();
            }
        });
    }

    /**
     * Show date and time picker dialogs.
     */
    private void showDateTimePicker() {
        // Get current date values
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        // Create date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Set date values in calendar
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Now show time picker
                showTimePicker();
            }
        }, year, month, day);

        // Show date picker
        datePickerDialog.show();
    }

    /**
     * Show time picker dialog.
     */
    private void showTimePicker() {
        // Get current time values
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        // Create time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Set time values in calendar
                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDateTime.set(Calendar.MINUTE, minute);

                // Update text with selected date and time
                updateSelectedDateTimeText();
            }
        }, hour, minute, false);

        // Show time picker
        timePickerDialog.show();
    }

    /**
     * Update text view with selected date and time.
     */
    private void updateSelectedDateTimeText() {
        tvSelectedDateTime.setText(dateTimeFormatter.format(selectedDateTime.getTime()));
    }

    /**
     * Validate user input and update ride.
     */
    private void updateRide() {
        String startPoint = etStartPoint.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();

        // Validate inputs
        if (TextUtils.equals(tvSelectedDateTime.getText(), "No date/time selected")) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(startPoint)) {
            etStartPoint.setError("Start point is required");
            return;
        }

        if (TextUtils.isEmpty(destination)) {
            etDestination.setError("Destination is required");
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Update ride based on type
        if (TYPE_OFFER.equals(rideType)) {
            updateRideOffer(startPoint, destination);
        } else if (TYPE_REQUEST.equals(rideType)) {
            updateRideRequest(startPoint, destination);
        } else {
            // Invalid ride type
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Invalid ride type", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Update a ride offer.
     * @param startPoint Starting point of the ride
     * @param destination Destination of the ride
     */
    private void updateRideOffer(String startPoint, String destination) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // First, get the full ride offer to preserve all its properties
        FirebaseUtil.getRideOfferById(rideId, new FirebaseCallback<RideOffer>() {
            @Override
            public void onSuccess(RideOffer originalOffer) {
                // Update only the fields that should be changed
                originalOffer.setDateTime(selectedDateTime.getTimeInMillis());
                originalOffer.setStartPoint(startPoint);
                originalOffer.setDestination(destination);
                // Make sure we don't change the status or driver info

                // Update ride offer in Firebase
                FirebaseUtil.updateRideOffer(originalOffer, new FirebaseCallback<RideOffer>() {
                    @Override
                    public void onSuccess(RideOffer result) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);

                        // Show success message
                        Toast.makeText(UpdateRideActivity.this, "Ride offer updated successfully", Toast.LENGTH_SHORT).show();

                        // Finish activity
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);

                        // Show error message
                        Toast.makeText(UpdateRideActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show error message
                Toast.makeText(UpdateRideActivity.this, "Failed to retrieve original ride offer: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Update a ride request.
     * @param startPoint Starting point of the ride
     * @param destination Destination of the ride
     */
    private void updateRideRequest(String startPoint, String destination) {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // First, get the full ride request to preserve all its properties
        FirebaseUtil.getRideRequestById(rideId, new FirebaseCallback<RideRequest>() {
            @Override
            public void onSuccess(RideRequest originalRequest) {
                // Update only the fields that should be changed
                originalRequest.setDateTime(selectedDateTime.getTimeInMillis());
                originalRequest.setStartPoint(startPoint);
                originalRequest.setDestination(destination);
                // Make sure we don't change the status or rider info

                // Update ride request in Firebase
                FirebaseUtil.updateRideRequest(originalRequest, new FirebaseCallback<RideRequest>() {
                    @Override
                    public void onSuccess(RideRequest result) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);

                        // Show success message
                        Toast.makeText(UpdateRideActivity.this, "Ride request updated successfully", Toast.LENGTH_SHORT).show();

                        // Finish activity
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);

                        // Show error message
                        Toast.makeText(UpdateRideActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show error message
                Toast.makeText(UpdateRideActivity.this, "Failed to retrieve original ride request: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handle state during configuration changes.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("dateTime", selectedDateTime.getTimeInMillis());
        outState.putString("startPoint", etStartPoint.getText().toString());
        outState.putString("destination", etDestination.getText().toString());
    }

    /**
     * Restore state after configuration changes.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        long dateTime = savedInstanceState.getLong("dateTime", -1);
        if (dateTime != -1) {
            selectedDateTime.setTimeInMillis(dateTime);
            updateSelectedDateTimeText();
        }
        etStartPoint.setText(savedInstanceState.getString("startPoint", ""));
        etDestination.setText(savedInstanceState.getString("destination", ""));
    }
}