package com.jjh.blesample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jjh.blesample.adapter.BLEDeviceListAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity{

    private final int REQUEST_CODE_ENABLE_BT = 0;
    private final int REQUEST_CODE_PERMISSION_LOCATION = 1;

    private final long SCAN_PERIOD = 10000;

    private boolean mScanning = false;

    private BluetoothAdapter mBluetoothAdapter;

    private BLEDeviceListAdapter mDeviceListAdapter;

    @ViewById(R.id.lvDeviceList) protected ListView lvDeviceList;

    @AfterInject
    public void init(){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) { // BLE 불가능 디바이스
            Toast.makeText(this, "Your device is not support BLE feature.", Toast.LENGTH_SHORT).show();
            finish();
        }
        mBluetoothAdapter = ((BluetoothManager)getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
    }

    @AfterViews
    public void initViews(){
        setTitle("Device scan");
        mDeviceListAdapter = new BLEDeviceListAdapter(this);

        lvDeviceList = (ListView)findViewById(R.id.lvDeviceList);
        lvDeviceList.setAdapter(mDeviceListAdapter);
    }

    @ItemClick(R.id.lvDeviceList)
    public void onListItemClick(Object obj){
        BLEServiceActivity_.intent(this).bleDevice((BluetoothDevice)obj).start().withAnimation(R.anim.transition_enter_from_right, android.R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCoarseLocation == PackageManager.PERMISSION_DENIED || permissionFineLocation == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION);
            } else {
                startScanLeDevice();
            }
        } else {
            startScanLeDevice();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScanLeDevice();
        mDeviceListAdapter.clear();
    }

    private void startScanLeDevice(){
        mScanning = true;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
        }else{
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    private void stopScanLeDevice(){
        mScanning = false;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
        }else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.i("LeScanCallback", "onLeScan--" + device.getName());
        }
    };

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            mDeviceListAdapter.addItem(result.getDevice());
            Log.i("ScanCallback", "onScanResult : " + result.getDevice().getName() + "/" + result.getDevice().getAddress());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.i("ScanCallback", "onBatchScanResults--");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("ScanCallback", "onBatchScanResults--");
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_PERMISSION_LOCATION){
            boolean isGranted = true;
            for(int result : grantResults){
                if(result == PackageManager.PERMISSION_DENIED){
                    isGranted = false;
                    break;
                }
            }
            if(isGranted){
                startScanLeDevice();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setVisible(false);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            if(menu.findItem(R.id.menu_refresh).getActionView() == null){
                ProgressBar progress = new ProgressBar(this);
                progress.setLayoutParams(new ViewGroup.LayoutParams(getResources().getDimensionPixelSize(R.dimen.menu_progress_width), getResources().getDimensionPixelSize(R.dimen.menu_progress_height)));
                menu.findItem(R.id.menu_refresh).setActionView(progress);
            }
            menu.findItem(R.id.menu_refresh).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mDeviceListAdapter.clear();
                startScanLeDevice();
                break;
            case R.id.menu_stop:
                stopScanLeDevice();
                break;
        }
        return true;
    }
}
