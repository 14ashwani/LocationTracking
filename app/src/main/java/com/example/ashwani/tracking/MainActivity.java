package com.example.ashwani.tracking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ashwani.tracking.bgservices.LocationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DIALOG_ERROR = "dialog_error";
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    public static final int UPDATES_REQUEST = 1002;

    private GoogleApiClient client;
    private boolean mResolvingError = false;
    int which;
    Fragment showLocation;
    private GoogleMap myMap;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  showLocation = (Fragment) findViewById(R.id.showLocation);
        Button startTracking = (Button) findViewById(R.id.startTracking);
        Button showHistory = (Button) findViewById(R.id.showHistory);
        showHistory.setOnClickListener(this);
        startTracking.setOnClickListener(this);
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        fragmentManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startTracking:
                showAlertBox();
                break;
            case R.id.showHistory:
                break;
            default:
        }
    }

  /*  public void deleteRecords() {
        Log.i(TAG, "Deleting previous records");
        MyDatabase mydatabase = new MyDatabase(this);
        SQLiteDatabase sqLiteDatabase = mydatabase.getWritableDatabase();
        sqLiteDatabase.delete(MyDatabase.TABLE_NAME, null, null);
        sqLiteDatabase.close();
    }*/


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed");
        if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                client.connect();
            }
        } else {
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    public void showAlertBox() {
        final CharSequence[] items = {" 1 Hour ", " 2 Hour ", " 3 Hour ",
                "4 Hour ", "5 Hour", "6 Hour"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Time");
        builder.setCancelable(true);
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                Log.d(TAG, "item");
                which = item + 1;
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int ok) {
                Log.d(TAG, "OK");
                client.connect();
            }
        });
        builder.create().show();
    }

    private PendingIntent getPendingIntent() {
        Log.d(TAG, "pending intent");
        Intent locationIntent = new Intent(this, LocationIntentService.class);
        PendingIntent pIntent = PendingIntent.getService(this, UPDATES_REQUEST, locationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

    protected LocationRequest getLocationRequest(int expireTime) {
        Log.d(TAG, "location request");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000 * 2);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setExpirationDuration(1000 * 30 * (expireTime));
        return mLocationRequest;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "connected............");
        LocationServices.FusedLocationApi.requestLocationUpdates(client,
                getLocationRequest(which),
                getPendingIntent());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended");
        //do nothing
    }

    private void onDialogDismissed() {
        mResolvingError = false;
    }

    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    private void fragmentManager() {
        FragmentManager myFragmentManager = getFragmentManager();
        MapFragment myMapFragment1 =
                (MapFragment) myFragmentManager.findFragmentById(R.id.showLocation);
        myMap = myMapFragment1.getMap();
        myMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    Log.i(TAG, "onMap ii ready");
        Polyline route = googleMap.addPolyline(new PolylineOptions()
                .width(5)
                .color(Color.RED)
                .geodesic(true)
                .zIndex(0));
       LatLng newPoint = new LatLng(12.93, 77.69);
        List<LatLng> points = route.getPoints();
        for (LatLng ll : points)
        points.add(newPoint);
        route.setPoints(points);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(newPoint, 25);
        googleMap.animateCamera(update);
        addMarker();


    }
    public void addMarker() {
        myMap.addMarker(new MarkerOptions()
                .position(new LatLng(12.93, 77.69))
                .title("Tracking Started"));
      // myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new, 15));
    }

    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }
}