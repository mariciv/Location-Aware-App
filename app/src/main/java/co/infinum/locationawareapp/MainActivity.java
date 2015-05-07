package co.infinum.locationawareapp;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_RESOLVE_ERROR = 1010;

    @InjectView(R.id.last_location_label)
    TextView lastLocationLabel;

    private LocationRequest locationRequest;

    private LocationTrackDialog dialog;

    @OnClick(R.id.last_known_location_btn)
    public void getLastKnownLocation() {
        if (googleApiClient.isConnected()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                lastLocationLabel.setText("Last known location: " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
            }
        }
    }

    @OnClick(R.id.start_tracking_location_btn)
    public void startTrackingLocation() {
        if (googleApiClient.isConnected()) {
            createLocationRequest();
            dialog = new LocationTrackDialog();
            dialog.show(getSupportFragmentManager(), "track_dialog");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, dialog);
        }
    }

    @OnClick(R.id.places_demo_btn)
    public void placesDemo() {

    }

    private GoogleApiClient googleApiClient;

    private Location lastLocation;

    private GoogleApiClient.ConnectionCallbacks playServicesConnectionCallback = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            lastLocationLabel.setText("Connected to play services");
        }

        @Override
        public void onConnectionSuspended(int i) {
            // The connection has been interrupted.
            // Disable any UI components that depend on Google APIs
            // until onConnected() is called.
        }
    };

    private GoogleApiClient.OnConnectionFailedListener playServicesFailListener =
            new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult result) {
                    if (resolvingError) {
                        return;
                    } else if (result.hasResolution()) {
                        try {
                            resolvingError = true;
                            result.startResolutionForResult(MainActivity.this, REQUEST_RESOLVE_ERROR);
                        } catch (IntentSender.SendIntentException e) {
                            googleApiClient.connect();
                        }
                    } else {
                        Dialog errorDialog = GooglePlayServicesUtil
                                .getErrorDialog(result.getErrorCode(), MainActivity.this, REQUEST_RESOLVE_ERROR);
                        errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                resolvingError = false;
                            }
                        });
                        errorDialog.show();
                        resolvingError = true;
                    }
                }
            };

    private Context context;

    private boolean resolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        context = this;

        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!resolvingError) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(playServicesConnectionCallback)
                .addOnConnectionFailedListener(playServicesFailListener)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            resolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, dialog);
    }
}