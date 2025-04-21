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

import edu.uga.cs.ugarideshare.models.RideRequest;
import edu.uga.cs.ugarideshare.utils.FirebaseCallback;
import edu.uga.cs.ugarideshare.utils.FirebaseUtil;
import edu.uga.cs.ugarideshare.utils.SessionManager;

/**
 * Activity for posting a ride request.
 */
public class PostRideRequestActivity extends AppCompatActivity {
    private Button btnSelectDateTime, btnPostRequest;
    private TextView tvSelectedDateTime;
    private EditText etStartPoint, etDestination;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private Calendar selectedDateTime;
    private SimpleDateFormat dateTimeFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ride_request);

        // Initialize views
        btnSelectDateTime = findViewById(R.id.btnSelectDateTime);
        btnPostRequest = findViewById(R.id.btnPostRequest);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);
        etStartPoint = findViewById(R.id.etStartPoint);
        etDestination = findViewById(R.id.etDestination);
        progressBar = findViewById(R.id.progressBar);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Initialize date formatter
        dateTimeFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.US);

        // Initialize calendar with current date/time
        selectedDateTime = Calendar.getInstance();

        // Check if we have saved instance state
        if (savedInstanceState != null) {
            long dateTime = savedInstanceState.getLong("dateTime", -1);
            if (dateTime != -1) {
                selectedDateTime.setTimeInMillis(dateTime);
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

        // Set click listener for post request button
        btnPostRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRideRequest();
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
     * Validate user input and post ride request.
     */
    private void postRideRequest() {
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

        // Create ride request object
        RideRequest request = new RideRequest(
                sessionManager.getUserId(),
                sessionManager.getUserEmail(),
                selectedDateTime.getTimeInMillis(),
                startPoint,
                destination
        );

        // Post ride request to Firebase
        FirebaseUtil.postRideRequest(request, new FirebaseCallback<RideRequest>() {
            @Override
            public void onSuccess(RideRequest result) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show success message
                Toast.makeText(PostRideRequestActivity.this, "Ride request posted successfully", Toast.LENGTH_SHORT).show();

                // Finish activity
                finish();
            }

            @Override
            public void onError(String error) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Show error message
                Toast.makeText(PostRideRequestActivity.this, error, Toast.LENGTH_SHORT).show();
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