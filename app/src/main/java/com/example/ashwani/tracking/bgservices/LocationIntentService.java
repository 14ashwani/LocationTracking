package com.example.ashwani.tracking.bgservices;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.example.ashwani.tracking.db.MyDatabase;
import com.google.android.gms.location.LocationResult;

import java.util.List;

public class LocationIntentService extends IntentService {
    public static final String TAG = LocationIntentService.class.getSimpleName();

    private LocationManager mLocationManager;
    SQLiteDatabase db = null;
    public LocationIntentService() {
        super("LocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult result = LocationResult.extractResult(intent);
            List<Location> locations = result.getLocations();
            MyDatabase myDb = new MyDatabase(this);

            for (Location location : locations) {
                Log.i(TAG,"location is"+ location.getLatitude()+ "," +location.getLongitude());
                db = myDb.getWritableDatabase();
                db.insert(MyDatabase.TABLE_NAME, null, getContentValues(location));
                }
                retriveResult();
            }
        }

    public void retriveResult(){
        Cursor cursor = db.query(MyDatabase.TABLE_NAME, new String[]{"Latitude", "Longitude"}, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.i(TAG,"Latitude -> " + cursor.getString(0));
            Log.i(TAG, "Longitude -> " + cursor.getString(1));
            cursor.moveToNext();
        }
    }

       private ContentValues getContentValues(Location location) {
        Log.d(TAG,"content value");
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDatabase.TIME, location.getTime());
        contentValues.put(MyDatabase.LAT, location.getLatitude());
        contentValues.put(MyDatabase.LNG, location.getLongitude());
        return contentValues;
    }
}