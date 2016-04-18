package com.osmani.location;

import com.osmani.utils.Constants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationTrackerService extends Service {
	
	
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 10; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000 * 60 * 2; // in Milliseconds, 2 mins
	protected LocationManager locationManager=null;
	
	private TrackerLocationListener trackerLocationListener=null;
	private Location lastSavedLocation=null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		startTracking();
		
		
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v("LocationTracker", "onStartCommand: DONE");
		if(locationManager==null || trackerLocationListener==null){
			startTracking();
		}
		return START_STICKY;
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v("LocationTracker", "onDestroy: DONE");
        locationManager.removeUpdates(trackerLocationListener);
	}
	private void startTracking(){
		trackerLocationListener = new TrackerLocationListener();

		// initialization done
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, trackerLocationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATES,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, trackerLocationListener);

		// initial location data
		Location firstLocation = null;
		Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (gpsLocation != null) {
			firstLocation = gpsLocation;
		} else {
			Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (networkLocation != null) {
				firstLocation = networkLocation;
			}
		}
		if (firstLocation != null) {
			saveLocation(firstLocation);
		}
	}
	
	private void saveLocation(Location location){
		
		if(lastSavedLocation!=null && isBetterLocation(location, lastSavedLocation)==false){
			return;
		}
		
		SharedPreferences sharedPreferences= getSharedPreferences(Constants.SHAREDPREFERRENCE_TAG_USER_LOCATION, 0);			
		
		SharedPreferences.Editor spEditor= sharedPreferences.edit();
		
		spEditor.putString("user_last_location_latitude",  location.getLatitude()+"");
		spEditor.putString("user_last_location_longitude",  location.getLongitude()+"");
		spEditor.commit();
		lastSavedLocation = location;
	}

	
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > MINIMUM_TIME_BETWEEN_UPDATES;
        boolean isSignificantlyOlder = timeDelta < -MINIMUM_TIME_BETWEEN_UPDATES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



/** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }

	
	private class TrackerLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			saveLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			/* this is called if/when the GPS is disabled in settings */
			Log.v("<x_x>", provider +" Disabled");

			/* bring up the GPS settings */
			//Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			//startActivity(intent);							
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
