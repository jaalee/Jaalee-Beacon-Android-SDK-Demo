package com.jaalee.BeaconDemo;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jaalee.BeaconDemo.R;
import com.jaalee.sdk.BLEDevice;
import com.jaalee.sdk.Beacon;
import com.jaalee.sdk.BeaconManager;
import com.jaalee.sdk.DeviceDiscoverListener;
import com.jaalee.sdk.RangingListener;
import com.jaalee.sdk.Region;
import com.jaalee.sdk.ServiceReadyCallback;
import com.jaalee.sdk.utils.L;

import java.util.ArrayList;
import java.util.Collections;
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
public class ListBeaconsActivity extends Activity {

  private static final String TAG = ListBeaconsActivity.class.getSimpleName();

  public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
  public static final String EXTRAS_BEACON = "extrasBeacon";

  private static final int REQUEST_ENABLE_BT = 1234;
  
  private static final String JAALEE_BEACON_PROXIMITY_UUID = "EBEFD083-70A2-47C8-9837-E7B5634DF524";//Jaalee BEACON Default UUID
  private static final Region ALL_BEACONS_REGION = new Region("rid", null, null, null);

  private BeaconManager beaconManager;
  private LeDeviceListAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    // Configure device list.
    adapter = new LeDeviceListAdapter(this);
    ListView list = (ListView) findViewById(R.id.device_list);
    list.setAdapter(adapter);
    list.setOnItemClickListener(createOnItemClickListener());

    // Configure verbose debug logging.
    L.enableDebugLogging(false);

    // Configure BeaconManager.
    beaconManager = new BeaconManager(this);
    beaconManager.setRangingListener(new RangingListener() {
      @Override
      public void onBeaconsDiscovered(Region region, final List beacons) {
        // Note that results are not delivered on UI thread.
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            // Note that beacons reported here are already sorted by estimated
            // distance between device and beacon.
            List<Beacon> JaaleeBeacons = filterBeacons(beacons);
            getActionBar().setSubtitle("Found beacons: " + JaaleeBeacons.size());
            
            adapter.replaceWith(JaaleeBeacons);
          }
        });
      }
    });
    
    //BLE device around the phone 
    beaconManager.setDeviceDiscoverListener(new DeviceDiscoverListener() {
		
		@Override
		public void onBLEDeviceDiscovered(BLEDevice device) {
			// TODO Auto-generated method stub
			Log.i("JAALEE", "On ble device  discovery:" + device.getMacAddress());
		}
	});
  }

  private List<Beacon> filterBeacons(List<Beacon> beacons) {
    List<Beacon> filteredBeacons = new ArrayList<Beacon>(beacons.size());
    for (Beacon beacon : beacons) 
    {
//    	only detect the Beacon of Jaalee
//    	if ( beacon.getProximityUUID().equalsIgnoreCase(JAALEE_BEACON_PROXIMITY_UUID) ) 
//    	if (beacon.getRssi() > -50)
    	{
    		Log.i("JAALEE", "JAALEE:"+beacon.getBattLevel());
    		filteredBeacons.add(beacon);
    	}
    }
    return filteredBeacons;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.scan_menu, menu);
    MenuItem refreshItem = menu.findItem(R.id.refresh);
    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
    return true;
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
  protected void onDestroy() {
    beaconManager.disconnect();

    super.onDestroy();
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Check if device supports Bluetooth Low Energy.
    if (!beaconManager.hasBluetooth()) {
      Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
      return;
    }

    // If Bluetooth is not enabled, let user enable it.
    if (!beaconManager.isBluetoothEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    } else {
      connectToService();
    }
  }

  @Override
  protected void onStop() {
    try {
		beaconManager.stopRanging(ALL_BEACONS_REGION);
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    super.onStop();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ENABLE_BT) {
      if (resultCode == Activity.RESULT_OK) {
        connectToService();
      } else {
        Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
        getActionBar().setSubtitle("Bluetooth not enabled");
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void connectToService() {
    getActionBar().setSubtitle("Scanning...");
    adapter.replaceWith(Collections.<Beacon>emptyList());
    beaconManager.connect(new ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        try {
			beaconManager.startRangingAndDiscoverDevice(ALL_BEACONS_REGION);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
    });
  }

  private AdapterView.OnItemClickListener createOnItemClickListener() {
    return new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
          try {
            Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
            Intent intent = new Intent(ListBeaconsActivity.this, clazz);
            Beacon temp = adapter.getItem(position);
            intent.putExtra(EXTRAS_BEACON, temp);
            
            if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY).contains("com.jaalee.BeaconDemo.CharacteristicsDemoActivity"))
           	{
            	startActivity(intent);
//            	if (temp.getConnectable())
//            	{
//            		startActivity(intent);	
//            	}
//            	else
//            	{
//					ListBeaconsActivity.this.runOnUiThread(new Runnable()  
//			        {  
//			            public void run()  
//			            {  
//			            	Toast.makeText(ListBeaconsActivity.this, "Current Beacon is in Non-Connectable mode", Toast.LENGTH_LONG).show();
//			            }  
//			  
//			        });
//            	}
           	}
            else
            {
            	startActivity(intent);	
            }

          } catch (ClassNotFoundException e) {
            Log.e(TAG, "Finding class by name failed", e);
          }
        }
      }
    };
  }

}
