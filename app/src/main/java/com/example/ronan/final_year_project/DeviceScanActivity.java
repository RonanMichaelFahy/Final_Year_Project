package com.example.ronan.final_year_project;

import android.app.ActionBar;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DeviceScanActivity extends ListActivity {

    private BluetoothDevice mBluetoothDevice;
    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private static final String TAG = "DeviceScanActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private boolean mScanning;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private int listItemPosition;
    private List<ScanFilter> filters = new ArrayList();
    private ScanSettings settings;
    private static final long SCAN_PERIOD = 100000;
    private static final int REQUEST_ENABLE_BT = 1;
    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            Log.i(TAG, "Scanned device: "+result.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(result.getDevice());
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    Log.i("Found device", result.getDevice().toString());
                }
            });
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(TAG, "Batch scan results"+results.toString());
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Scan failed: "+Integer.toString(errorCode));
            super.onScanFailed(errorCode);
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.i(TAG, "Service connected");

            BluetoothLeService.LocalBinder localBinder = (BluetoothLeService.LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            Log.i(TAG, "mBluetoothLeService: "+mBluetoothLeService.toString());
            mBound = true;

            mBluetoothLeService.initialize();
            boolean connected = mBluetoothLeService.connect(mLeDeviceListAdapter.getDevice(listItemPosition).getAddress());
            if (connected) {
                Toast toast = Toast.makeText(DeviceScanActivity.this, "Connected", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(DeviceScanActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        else {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            scanDevice(!mScanning);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){

        if(mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()){
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            scanDevice(!mScanning);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth_set_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id){

        //Stop scanning
        scanDevice(false);
        mBluetoothDevice = mLeDeviceListAdapter.getDevice(position);

        //Save device object for retrieval from other activities
        /*SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mBluetoothDevice);
        Log.i(TAG, "onListItemClick() - json: "+json);
        editor.putString("device", json);
        editor.commit();*/

        if (mBluetoothDevice == null) return;

        listItemPosition = position;
        Intent intent = new Intent(this, BluetoothLeService.class);
        Log.i(TAG, "Starting bluetooth service");
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause(){
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        if (mScanning) {
            scanDevice(false);
        }
        super.onPause();
    }

    private void scanDevice(final boolean enable) {

        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        settings = scanSettingsBuilder.build();

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.i(TAG, "Stopping scan");
                    Toast toast = Toast.makeText(DeviceScanActivity.this, "Stopping scan", Toast.LENGTH_SHORT);
                    toast.show();
                    mBluetoothLeScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            Log.i(TAG, "Starting scan");
            Toast toast = Toast.makeText(this, "Starting scan", Toast.LENGTH_SHORT);
            toast.show();
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        }

        else {
            mScanning = false;
            Log.i(TAG, "Stopping scan");
            Toast toast = Toast.makeText(this, "Stopping scan", Toast.LENGTH_SHORT);
            toast.show();
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    public class LeDeviceListAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mLeDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText("Unknown Device");
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    static class ViewHolder {
        public TextView deviceAddress;
        public TextView deviceName;

        ViewHolder() {
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "deviceAddress=" + deviceAddress +
                    ", deviceName=" + deviceName +
                    '}';
        }
    }

    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Toast toast = Toast.makeText(DeviceScanActivity.this, msg.toString(), Toast.LENGTH_SHORT);
            toast.show();
            super.handleMessage(msg);
        }
    }
}
