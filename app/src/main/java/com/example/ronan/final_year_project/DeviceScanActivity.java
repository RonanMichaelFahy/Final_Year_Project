package com.example.ronan.final_year_project;

import android.app.ActionBar;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
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

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DeviceScanActivity extends ListActivity {

    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private static final String TAG = "DeviceScanActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean mScanning;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private int listItemPosition;
    private ArrayList<android.bluetooth.le.ScanFilter> filters = new ArrayList<>();
    private static final long SCAN_PERIOD = 50000L;
    private static final int REQUEST_ENABLE_BT = 1;
    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, final ScanResult result) {
            Log.i(TAG, "Scanned device: " + result.toString());
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
            Log.i(TAG, "Batch scan results" + results.toString());
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Scan failed: " + Integer.toString(errorCode));
            super.onScanFailed(errorCode);
        }
    };
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.i(TAG, "Service connected");

            BluetoothLeService.LocalBinder localBinder = (BluetoothLeService.LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            Log.i(TAG, "mBluetoothLeService: " + mBluetoothLeService.toString());
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
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Toast toast = Toast.makeText(DeviceScanActivity.this, (CharSequence) message.obj, Toast.LENGTH_SHORT);
            toast.show();
        }
    };
    // Handles various events fired by the Service.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i(TAG, "action: "+action);
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action) && mBluetoothLeService != null){
                Log.i(TAG, "Available services: "+mBluetoothLeService.getSupportedGattServices());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);

        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    scanDevice(!mScanning);
                }
            });
            thread.start();
            //scanDevice(!mScanning);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            scanDevice(!mScanning);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_login:
                Intent i = new Intent(this, LoginActivity.class);
                i.putExtra("Calling_Activity", TAG);
                startActivity(i);
                break;
            case R.id.action_sign_up:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                ParseUser.logOut();
                break;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {

        //Stop scanning
        scanDevice(false);
        BluetoothDevice mBluetoothDevice = mLeDeviceListAdapter.getDevice(position);
        if (mBluetoothDevice == null) return;

        listItemPosition = position;
        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, mBluetoothDevice.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, mBluetoothDevice.getAddress());
        if (mScanning) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }

    @Override
    public void onPause() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        if (mScanning) {
            scanDevice(false);
        }

        unregisterReceiver(mGattUpdateReceiver);

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        String callingActivity = intent.getStringExtra("Calling_Activity");
        Log.i(TAG, intent.getStringExtra("Calling_Activity"));
        if (callingActivity.equals(MainActivity.TAG)) {
            Intent intent1 = new Intent(this, MainActivity.class);
            startActivity(intent1);
        } else if (callingActivity.equals(PrescriptionSetupActivity.TAG)) {
            Intent intent1 = new Intent(this, PrescriptionSetupActivity.class);
            startActivity(intent1);
        }
    }

    private void scanDevice(final boolean enable) {

        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        ScanSettings settings = scanSettingsBuilder.build();

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.i(TAG, "Stopping scan");
                    Message message = mHandler.obtainMessage(1, "Stopping Scan");
                    message.sendToTarget();
                    mBluetoothLeScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            Log.i(TAG, "Starting scan");
            Message message = mHandler.obtainMessage(1, "Starting Scan");
            message.sendToTarget();
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        } else {
            mScanning = false;
            Log.i(TAG, "Stopping scan");
            Message message = mHandler.obtainMessage(1, "Stopping Scan");
            message.sendToTarget();
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public class LeDeviceListAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflater;

        public LeDeviceListAdapter() {
            mLeDevices = new ArrayList<>();
            mInflater = getLayoutInflater();
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
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflater.inflate(R.layout.listitem_device, null);
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
}
