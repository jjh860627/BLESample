package com.jjh.blesample;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjh.blesample.adapter.BleServiceCharacterListAdapter;
import com.jjh.blesample.attrs.GattAttributes;
import com.jjh.blesample.service.BluetoothLeService;
import com.jjh.blesample.vo.BleCharacteristic;
import com.jjh.blesample.vo.BleService;
import com.jjh.blesample.vo.measurement.BP;
import com.jjh.blesample.vo.measurement.Glucose;
import com.jjh.blesample.vo.measurement.HeartRate;
import com.jjh.blesample.vo.measurement.Temperature;
import com.jjh.blesample.vo.measurement.Weight;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_bleservice)
public class BLEServiceActivity extends AppCompatActivity {

    @ViewById(R.id.tvDeviceAddress)
    protected TextView tvDeviceAddress;

    @ViewById(R.id.tvDeviceState)
    protected TextView tvDeviceState;

    @ViewById(R.id.llProgressContainer)
    protected LinearLayout llProgressContainer;

    @ViewById(R.id.tvCharValue)
    protected TextView tvCharValue;

    @ViewById(R.id.tvProgressMsg)
    protected TextView tvProgressMsg;

    @ViewById(R.id.elvServiceList)
    protected ExpandableListView elvServiceList;

    @Extra
    protected BluetoothDevice bleDevice;

    private BluetoothGatt mBluetoothGatt;

    private BluetoothLeService mBluetoothLeService;

    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private boolean mConnected = false;

    List<List<BluetoothGattCharacteristic>> mCharacteristicList = null;

    BleServiceCharacterListAdapter mListAdapter = null;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder)service).getService();
            if(!mBluetoothLeService.initialize()){
                finish();
            }
            connectGatt();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    private final ExpandableListView.OnChildClickListener mCharacteristicClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if(mCharacteristicList != null){
                final BluetoothGattCharacteristic characteristic = mCharacteristicList.get(groupPosition).get(childPosition);
                final int charProp = characteristic.getProperties();
                if((charProp & BluetoothGattCharacteristic.PROPERTY_READ) != 0){
                    if(mNotifyCharacteristic != null){
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mBluetoothLeService.readCharacteristic(characteristic);
                }
                if((charProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0){
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
                if((charProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0){
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicIndication(characteristic, true);
                }
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBluetoothLeService != null) {
            unbindService(mServiceConnection);
        }
    }

    @AfterViews
    protected void initViews(){
        setTitle(bleDevice.getName());
        tvDeviceAddress.setText(bleDevice.getAddress());
        elvServiceList.setOnChildClickListener(mCharacteristicClickListener);
        tvCharValue.setMovementMethod(new ScrollingMovementMethod());
    }

    private void showProgressView(final boolean isShow, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isShow){
                    llProgressContainer.setVisibility(View.VISIBLE);
                    elvServiceList.setVisibility(View.GONE);
                    tvProgressMsg.setText(message);
                }else{
                    llProgressContainer.setVisibility(View.GONE);
                    elvServiceList.setVisibility(View.VISIBLE);
                    tvProgressMsg.setText("");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, R.anim.transition_exit_to_right);
    }

    private void connectGatt(){
        showProgressView(true, "Connecting to Gatt Server...");
        mBluetoothLeService.connect(bleDevice.getAddress());
    }

    @Receiver(actions = BluetoothLeService.ACTION_GATT_CONNECTED)
    protected void onActionGattConnected(){
        Log.i("BroadcastReceiver", "onActionGattConnected()");
        mConnected = true;
        tvDeviceState.setText("Connected");
        showProgressView(true, "Discovering services...");
        invalidateOptionsMenu();
    }

    @Receiver(actions = BluetoothLeService.ACTION_GATT_DISCONNECTED)
    protected void onActionGattDisconnected(){
        Log.i("BroadcastReceiver", "onActionGattDisconnected()");
        mConnected = false;
        tvDeviceState.setText("Disconnected");
        invalidateOptionsMenu();
        if(mListAdapter != null) mListAdapter.clear();
    }

    @Receiver(actions = BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
    protected void onActionGattServicesDiscovered(){
        Log.i("BroadcastReceiver", "onActionGattServicesDiscovered()");
        showProgressView(false, null);
        List<BleService> serviceList = new ArrayList<BleService>();
        List<List<BleCharacteristic>> charGroupList = new ArrayList<List<BleCharacteristic>>();
        mCharacteristicList = new ArrayList<List<BluetoothGattCharacteristic>>();
        for(BluetoothGattService service : mBluetoothLeService.getSupportedGattServices()) {
            BleService bleService = new BleService();
            bleService.setName(GattAttributes.getServiceName(service.getUuid()));
            bleService.setUuid(service.getUuid());
            serviceList.add(bleService);

            List<BleCharacteristic> charList = new ArrayList<BleCharacteristic>();
            List<BluetoothGattCharacteristic> gattCharList = new ArrayList<BluetoothGattCharacteristic>();
            for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()){
                BleCharacteristic bleChar = new BleCharacteristic();
                bleChar.setName(GattAttributes.getCharacteristicName(characteristic.getUuid()));
                bleChar.setUuid(characteristic.getUuid());
                charList.add(bleChar);
                gattCharList.add(characteristic);
            }
            mCharacteristicList.add(gattCharList);
            charGroupList.add(charList);
        }

        mListAdapter = new BleServiceCharacterListAdapter(this, serviceList, charGroupList);
        elvServiceList.setAdapter(mListAdapter);
    }

    @Receiver(actions = BluetoothLeService.ACTION_DATA_AVAILABLE)
    protected void onActionDataAvailable(@Receiver.Extra(BluetoothLeService.EXTRA_DATA) Serializable data, @Receiver.Extra(BluetoothLeService.EXTRA_IS_APPEND) boolean isExpand){
        Log.i("BroadcastReceiver", "onActionDataAvailable()");

        if(data == null) return;

        Log.i("Data", data.toString());

        StringBuilder sbText = new StringBuilder();
        if(data instanceof BP){
            BP bp = (BP)data;
            sbText.append("SYS : ").append(bp.getSys()).append(" ").append(bp.getUnitStr()).append("\n")
                  .append("DIA : ").append(bp.getDia()).append(" ").append(bp.getUnitStr()).append("\n")
                  .append("MAP : ").append(bp.getMap()).append(" ").append(bp.getUnitStr()).append("\n")
                  .append("Pulse Rate : ").append(bp.getPulseRate()).append(" ").append(bp.getUnitStr()).append("\n");
        }else if(data instanceof HeartRate){
            HeartRate heartRate = (HeartRate)data;
            sbText.append("Heart Rate : ").append(heartRate.getHeartRate()).append("\n")
                  .append("Energy Expended : ").append(heartRate.getEnergyExpended()).append("\n");
        }else if(data instanceof Temperature){
            Temperature temperature = (Temperature)data;
            sbText.append("Temperature : ").append(temperature.getTemperature()).append("\n")
                  .append("Unit : ").append(temperature.getUnit()).append("(").append(temperature.getUnitStr()).append(")").append("\n");
        }else if(data instanceof Glucose){
            Glucose glucose = (Glucose)data;
            sbText.append("Glucose : ").append(glucose.getGlucose()).append(" ").append(glucose.getUnitStr()).append("\n");
        }else if(data instanceof Weight){
            Weight weight = (Weight)data;
            sbText.append("Weihgt : ").append(weight.getWeight()).append(" ").append(weight.getUnitStr()).append("\n")
                  .append("BMI : ").append(weight.getBmi()).append("\n")
                  .append("Height : ").append(weight.getHeight()).append("\n");

        }else{
            sbText.append(data);
        }

        if(isExpand){
            tvCharValue.setText(tvCharValue.getText().toString() + "\n" + sbText.toString());
        }else{
            tvCharValue.setText(sbText.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                connectGatt();
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
