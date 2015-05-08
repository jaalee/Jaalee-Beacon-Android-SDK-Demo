package com.jaalee.BeaconDemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaalee.BeaconDemo.R;
import com.jaalee.sdk.Beacon;
import com.jaalee.sdk.connection.BeaconCharacteristics;
import com.jaalee.sdk.connection.BeaconConnection;
import com.jaalee.sdk.connection.ConnectionCallback;
import com.jaalee.sdk.connection.JaaleeDefine;
import com.jaalee.sdk.connection.WriteCallback;

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
public class CharacteristicsDemoActivity extends Activity {

  private Beacon beacon;
  private BeaconConnection connection;

  private TextView statusView;
  private TextView beaconDetailsView;
  private View afterConnectedView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.characteristics_demo);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    statusView = (TextView) findViewById(R.id.status);
    beaconDetailsView = (TextView) findViewById(R.id.beacon_details);
    afterConnectedView = findViewById(R.id.after_connected);

    beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
    connection = new BeaconConnection(this, beacon, createConnectionCallback());
    findViewById(R.id.Config_UUID).setOnClickListener(createUpdateButtonListener());     
    
    findViewById(R.id.Call).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
	    	  connection.CallBeacon();			
			
		}
	});
    
    findViewById(R.id.Config_Major).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			connection.writeMajor(153, new WriteCallback() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub	
					
					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Major Value Config Success");
			            }  
			  
			        });  

				}
				
				@Override
				public void onError() {
					// TODO Auto-generated method stub					

					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Major Value Config Fail");
			            }  
			        });  					
				}
			});
		}
	});
    
    
    
    findViewById(R.id.Config_BeaconState).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			connection.writeBeaconState(JaaleeDefine.JAALEE_BEACON_STATE_ENABLE, new WriteCallback() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Beacon State Config Success");
			            }  
			        });
				}
				
				@Override
				public void onError() {
					// TODO Auto-generated method stub
					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Beacon State Config Fail");
			            }  
			        });
				}
			});
		}
	});

    
    findViewById(R.id.Config_Minor).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			connection.writeMinor(153, new WriteCallback() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Minor Value Config Success");
			            }  
			        });
				}
				
				@Override
				public void onError() {
					// TODO Auto-generated method stub
					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Minor Value Config Fail");
			            }  
			        });
				}
			});
		}
	});
    
    findViewById(R.id.Config_MeasuredPower).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			connection.writeBroadcastingPowerValue(197, new WriteCallback() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Power Value Config Success");
			            }  
			        });
				}
				
				@Override
				public void onError() {
					// TODO Auto-generated method stub
					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Power Value Config Fail");
			            }  
			        });
				}
			});
		}
	});
    
    findViewById(R.id.Config_BroadcastRete).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			connection.writeAdvertisingInterval(100, new WriteCallback() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Broadcast interval Config Success");
			            }  
			        });
				}
				
				@Override
				public void onError() {
					// TODO Auto-generated method stub
					CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
			        {  
			            public void run()  
			            {  
							showToast("Broadcast interval Config Fail");
			            }  
			        });
				}
			});
		}
	});
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!connection.isConnected()) {
      statusView.setText("Status: Connecting...");
      connection.connectBeaconWithPassword("666666");
    }
  }

  @Override
  protected void onDestroy() {
    connection.disconnect();
    super.onDestroy();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Returns click listener on update minor button.
   * Triggers update minor value on the beacon.
   */
  private View.OnClickListener createUpdateButtonListener() {
    return new View.OnClickListener() {
      @Override 
      public void onClick(View v) {
    	  
    	  connection.writeProximityUuid("EBEFD083-70A2-47C8-9837-E7B5634DF599", new WriteCallback() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
		        {  
		            public void run()  
		            {  
						showToast("UUID Config Success");
		            }  
		        });
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				CharacteristicsDemoActivity.this.runOnUiThread(new Runnable()  
		        {  
		            public void run()  
		            {  
						showToast("UUID Config Fail");
		            }  
		        });
			}
		});

      }
    };
  }

  private ConnectionCallback createConnectionCallback() {
    return new ConnectionCallback() {
      @Override public void onAuthenticated(final BeaconCharacteristics beaconChars) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Connected to beacon");
            StringBuilder sb = new StringBuilder()
            	.append("UUID: ").append(beaconChars.getBeaconUUID()).append("\n")
                .append("Major: ").append(beaconChars.getMajor()).append("\n")
                .append("Minor: ").append(beaconChars.getMinor()).append("\n")
                .append("Advertising interval: ").append(beaconChars.getBroadcastRate()).append("ms\n")
                .append("Broadcasting power: ").append(beaconChars.getBroadcastingPower()).append(" dBm\n")
                .append("Device Name: ").append(beaconChars.getBeaconName()).append("\n")
            	.append("Beacon State: ").append(beaconChars.getBeaconState());
            beaconDetailsView.setText(sb.toString());
            afterConnectedView.setVisibility(View.VISIBLE);
          }
        });
      }

      @Override public void onAuthenticationError() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Cannot connect to beacon. Authentication problems.");
          }
        });
      }

      @Override public void onDisconnected() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Disconnected from beacon");
          }
        });
      }
    };
  }

  private void showToast(String text) {
    Toast.makeText(CharacteristicsDemoActivity.this, text, Toast.LENGTH_LONG).show();
  }
}
