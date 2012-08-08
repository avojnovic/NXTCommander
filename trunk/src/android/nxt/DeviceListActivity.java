
package android.nxt;

import java.util.Set;

import android.nxt.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class DeviceListActivity extends Activity {
    static final String PAIRING = "pairing";

  
    public static String DEVICE_NAME_AND_ADDRESS = "device_infos";
    public static String EXTRA_DEVICE_ADDRESS = "device_address";


    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);
 
     
        setResult(Activity.RESULT_CANCELED);

      
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

     
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

     
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

      
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

     
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

       
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

      
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        
        boolean legoDevicesFound = false;
        
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                
                if (device.getAddress().startsWith(BTCommunicator.OUI_LEGO)) {
                    legoDevicesFound = true;
                    mPairedDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
                }
            }
        }
        
        if (legoDevicesFound == false) {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

 
        this.unregisterReceiver(mReceiver);
    }

   
    private void doDiscovery() {

        
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

      
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

       
        mBtAdapter.startDiscovery();
    }

    
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            String info = ((TextView) v).getText().toString();
            
            if (info.lastIndexOf('-') != info.length()-18) 
                return;

            
            mBtAdapter.cancelDiscovery();
            
            String address = info.substring(info.lastIndexOf('-')+1);
            
            Intent intent = new Intent();
            Bundle data = new Bundle();
            data.putString(DEVICE_NAME_AND_ADDRESS, info);
            data.putString(EXTRA_DEVICE_ADDRESS, address);
            data.putBoolean(PAIRING,av.getId()==R.id.new_devices);
            intent.putExtras(data);
            
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
                }
            
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

}
