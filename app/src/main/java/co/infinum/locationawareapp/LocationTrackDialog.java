package co.infinum.locationawareapp;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.LocationSource;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Ivan on 07/05/15.
 */
public class LocationTrackDialog extends DialogFragment implements LocationListener {

    @InjectView(R.id.location_updates_text_view)
    TextView locationUpdatesTextView;

    @OnClick(R.id.stop_tracking_btn)
    public void stopTracking() {
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ((MainActivity)getActivity()).stopLocationUpdates();
        super.onDismiss(dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_location, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Date time = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("'['HH:mm:sss']'");
            String line = df.format(time) + " " + location.getLatitude() + ", " + location.getLongitude();
            appendLine(line);
        }
    }

    private void appendLine(String line) {
        if (locationUpdatesTextView != null) {
            locationUpdatesTextView.append(line + "\n");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
