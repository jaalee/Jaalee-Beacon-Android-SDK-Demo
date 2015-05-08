package com.jaalee.BeaconDemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.jaalee.BeaconDemo.R;
import com.jaalee.sdk.Beacon;
import com.jaalee.sdk.BeaconManager;
import com.jaalee.sdk.RangingListener;
import com.jaalee.sdk.Region;
import com.jaalee.sdk.ServiceReadyCallback;
import com.jaalee.sdk.Utils;

import java.util.List;

/**
 * http://www.jaalee.com/
 * @author JAALEE, Inc.
 * We have been trying to provide better services and products! Jaalee Beacon makes 
 * life more simple and cheerful! If you are interested in our product, 
 * please contact us in following ways. We will provide the best service wholeheartedly for you!
 * 
 * Buy Jaalee Beacon: sales@jaalee.com
 * 
 * Technical Support: dev@jaalee.com
 * 
 */
public class DistanceBeaconActivity extends Activity {

  private static final String TAG = DistanceBeaconActivity.class.getSimpleName();

  // Y positions are relative to height of bg_distance image.
  private static final double RELATIVE_START_POS = 320.0 / 1110.0;
  private static final double RELATIVE_STOP_POS = 885.0 / 1110.0;

  private BeaconManager beaconManager;
  private Beacon beacon;
  private Region region;

  private View dotView;
  private int startY = -1;
  private int segmentLength = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    setContentView(R.layout.distance_view);
    dotView = findViewById(R.id.dot);

    beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
    region = new Region("regionid", beacon.getProximityUUID(), beacon.getMajor(), beacon.getMinor());
    if (beacon == null) {
      Toast.makeText(this, "Beacon not found in intent extras", Toast.LENGTH_LONG).show();
      finish();
    }

    beaconManager = new BeaconManager(this);
    beaconManager.setRangingListener(new com.jaalee.sdk.RangingListener() {
      @Override
      public void onBeaconsDiscovered(Region region, final List rangedBeacons) {
        // Note that results are not delivered on UI thread.
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            // Just in case if there are multiple beacons with the same uuid, major, minor.
            Beacon foundBeacon = null;
            
            for (Object rangedBeacon : rangedBeacons) {
            	
            	Beacon temp = (Beacon)rangedBeacon;
              if (temp.getMacAddress().equals(beacon.getMacAddress())) {
                foundBeacon = temp;
              }
            }
            if (foundBeacon != null) {
              updateDistanceView(foundBeacon);
            }
          }
        });
      }
    });

    final View view = findViewById(R.id.sonar);
    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        startY = (int) (RELATIVE_START_POS * view.getMeasuredHeight());
        int stopY = (int) (RELATIVE_STOP_POS * view.getMeasuredHeight());
        segmentLength = stopY - startY;

        dotView.setVisibility(View.VISIBLE);
        dotView.setTranslationY(computeDotPosY(beacon));
      }
    });
  }

  private void updateDistanceView(Beacon foundBeacon) {
    if (segmentLength == -1) {
      return;
    }

    dotView.animate().translationY(computeDotPosY(foundBeacon)).start();
  }

  private int computeDotPosY(Beacon beacon) {
    // Let's put dot at the end of the scale when it's further than 6m.
    double distance = Math.min(Utils.computeAccuracy(beacon), 6.0);
    return startY + (int) (segmentLength * (distance / 6.0));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStart() {
    super.onStart();

    beaconManager.connect(new ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        Toast.makeText(DistanceBeaconActivity.this, "start ranging",
		        Toast.LENGTH_LONG).show();
        try {
			beaconManager.startRangingAndDiscoverDevice(region);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
    });
  }

  @Override
  protected void onStop() {
    beaconManager.disconnect();

    super.onStop();
  }
}
