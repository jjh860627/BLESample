
package com.jjh.blesample.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.jjh.blesample.attrs.GattAttributes;
import com.jjh.blesample.attrs.RACPAttributes;
import com.jjh.blesample.util.GattDataParser;
import com.jjh.blesample.vo.measurement.BP;
import com.jjh.blesample.vo.measurement.Glucose;
import com.jjh.blesample.vo.measurement.HeartRate;
import com.jjh.blesample.vo.measurement.Spo2;
import com.jjh.blesample.vo.measurement.Temperature;
import com.jjh.blesample.vo.measurement.Weight;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class BluetoothLeService extends Service {

    public static final String ACTION_GATT_CONNECTED = "com.jjh.blesample.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.jjh.blesample.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.jjh.blesample.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = "com.jjh.blesample.ACTION_DATA_AVAILABLE";

    public static final String EXTRA_DATA = "extra_data";
    public static final String EXTRA_IS_APPEND = "extra_is_expand";

    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private byte mReadCount = 0;

    private GattDataParser mGattDataParser;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d("BluetoothGattCallback", "onConnectionStateChange--" + gatt.getDevice().getName() + "/" + gatt.getDevice().getName() + " -> " + (newState == STATE_CONNECTED? "STATE_CONNECTED" : newState == STATE_DISCONNECTED ? "STATE_DISCONNECTED" : ""));
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "--Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "--Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "--Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.i(TAG, "--onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("BluetoothGattCallback", "--onCharacteristicRead = " + GattAttributes.getCharacteristicName(characteristic.getUuid()));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("BluetoothGattCallback", "--onCharacteristicChanged = " + GattAttributes.getCharacteristicName(characteristic.getUuid()));
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i("BluetoothGattCallback", "--onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i("BluetoothGattCallback", "--onDescriptorWrite = " + GattAttributes.getCharacteristicName(descriptor.getCharacteristic().getUuid()));
            if(GattAttributes.Characteristic.RECORD_ACCESS_CONTROL_POINT.isMatchedWithUUID(descriptor.getCharacteristic().getUuid())){
                writeRACP(descriptor.getCharacteristic(), RACPAttributes.OpCode.REPORT_NUMBER_OF_STORED_RECORDS, RACPAttributes.Operator.ALL_RECORDS);
            }
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic  characteristic) {

        final Intent intent = new Intent(action);
        Log.i("Value", GattAttributes.getCharacteristicName(characteristic.getUuid()) + "(" + characteristic.getUuid().toString() + ") = " + Arrays.toString(characteristic.getValue()));
        byte[] value = characteristic.getValue();
        if (value != null && value.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(value.length);
            for (byte byteChar : value)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.i("Value_HEX", GattAttributes.getCharacteristicName(characteristic.getUuid()) + "(" + characteristic.getUuid().toString() + ") = " + stringBuilder.toString());
        }

        if (GattAttributes.Characteristic.HEART_RATE_MEASUREMENT.isMatchedWithUUID(characteristic.getUuid())) {
            //Heart Rate - Measurement
            HeartRate heartRate = mGattDataParser.parseHeartRateData(characteristic);
            intent.putExtra(EXTRA_DATA, heartRate);

        } else if (GattAttributes.Characteristic.BODY_SENSOR_LOCATION.isMatchedWithUUID(characteristic.getUuid())) {
            //Heart Rate - Body Sensor Location
            String sensorName = "Other";
            switch (value[0]) {
                case 1: sensorName = "Chest"; break;
                case 2: sensorName = "Wrist"; break;
                case 3: sensorName = "Finger"; break;
                case 4: sensorName = "Hand"; break;
                case 5: sensorName = "Ear Lobe"; break;
                case 6: sensorName = "Foot"; break;
            }
            intent.putExtra(EXTRA_DATA, "Body Sensor Location : " + sensorName);

        } else if (GattAttributes.Characteristic.MEASUREMENT_INTERVAL.isMatchedWithUUID(characteristic.getUuid())) {
            //Health Thermometer - Interval
            int intervalSec = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            intent.putExtra(EXTRA_DATA, String.valueOf(intervalSec) + " sec");

        } else if (GattAttributes.Characteristic.TEMPERATURE_MEASUREMENT.isMatchedWithUUID(characteristic.getUuid())
                || GattAttributes.Characteristic.INTERMEDIATE_TEMPERATURE.isMatchedWithUUID(characteristic.getUuid())) {
            //Health Thermometer - Temperature Measurement
            Temperature temperature = mGattDataParser.parseTemperatureData(characteristic);
            intent.putExtra(EXTRA_DATA, temperature);
            //setCharacteristicIndication(characteristic, false);

        } else if(GattAttributes.Characteristic.TEMPERATURE_TYPE.isMatchedWithUUID(characteristic.getUuid())){
            //Health Thermometer - Temperature Type
            byte temperatureType = value[0];
            String temperatureTypeStr = "TemperatureType : ";
            for(Temperature.TemperatureType tempType : Temperature.TemperatureType.values()){
                if(tempType.index == temperatureType){
                    temperatureTypeStr += tempType.text;
                    break;
                }
            }
            intent.putExtra(EXTRA_DATA, temperatureTypeStr);

        } else if (GattAttributes.Characteristic.BLOOD_PRESSURE_MEASUREMENT.isMatchedWithUUID(characteristic.getUuid())
                || GattAttributes.Characteristic.INTERMEDIATE_CUFF_PRESSURE.isMatchedWithUUID(characteristic.getUuid())) {
            //Blood Pressure - Blood Pressure Measurement
            BP bp = mGattDataParser.parseBPData(characteristic);
            intent.putExtra(EXTRA_DATA, bp);

        } else if (GattAttributes.Characteristic.BLOOD_PRESSURE_FEATURE.isMatchedWithUUID(characteristic.getUuid())){
            //Blood Pressure - Blood Pressure Feature
            StringBuilder sb = new StringBuilder();
            sb.append("Blood Pressure Feature : ").append("\n");
            int bpFeature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            int bitFlag = 0x01;
            for(BP.BPFeature feature : BP.BPFeature.values()){
                sb.append(feature.name()).append(" : ").append((bpFeature & bitFlag) == 0? "Not Supported" : "Supported").append("\n");
                bitFlag *= 2;
            }
            intent.putExtra(EXTRA_DATA, sb.toString());

        } else if (GattAttributes.Characteristic.RECORD_ACCESS_CONTROL_POINT.isMatchedWithUUID(characteristic.getUuid())){
            //Glucose, SPo2 - RACP
            readRACP(characteristic);
            return;

        } else if (GattAttributes.Characteristic.GLUCOSE_MEASUREMENT.isMatchedWithUUID(characteristic.getUuid())){
            //Glucose - Glucose Measurement
            Glucose glucose = mGattDataParser.parseGlucoseData(characteristic);
            intent.putExtra(EXTRA_IS_APPEND, mReadCount++ != 0);
            intent.putExtra(EXTRA_DATA, glucose);

        } else if (GattAttributes.Characteristic.GLUCOSE_MEASUREMENT_CONTEXT.isMatchedWithUUID(characteristic.getUuid())){
            //Glucose - Glucose Measurement Context
            Glucose.MeasurementContext measurementContext = mGattDataParser.parseGlucoseContextData(characteristic);
            Log.i("MeasurementContext", measurementContext.toString());
            return;

        } else if (GattAttributes.Characteristic.GLUCOSE_FEATURE.isMatchedWithUUID(characteristic.getUuid())){
            //Glucose - Glucose Feature
            StringBuilder sb = new StringBuilder();
            sb.append("Glucose Feature : ").append("\n");
            int glucoseFeature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
            int bitFlag = 0x01;
            for(Glucose.GlucoseFeature feature : Glucose.GlucoseFeature.values()){
                sb.append(feature.name()).append(" : ").append((glucoseFeature & bitFlag) == 0? "Not Supported" : "Supported").append("\n");
                bitFlag *= 2;
            }
            intent.putExtra(EXTRA_DATA, sb.toString());

        } else if (GattAttributes.Characteristic.WEIGHT_MEASUREMENT.isMatchedWithUUID(characteristic.getUuid())){
            //Weight - Weight Measurement
            Weight weight = mGattDataParser.parseWeightScaleData(characteristic);
            intent.putExtra(EXTRA_DATA, weight);
        } else if (GattAttributes.Characteristic.WEIGHT_SCALE_FEATURE.isMatchedWithUUID(characteristic.getUuid())){
            //Weight - Weight Scale Feature
            StringBuilder sb = new StringBuilder();
            int weightFeature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);

            sb.append(Weight.WeightScaleFeature.TIME_STAMP_SUPPORTED.name()).append(" : ").append(Weight.WeightScaleFeature.TIME_STAMP_SUPPORTED.getFlagValue(weightFeature & 0x01)).append("\n")
              .append(Weight.WeightScaleFeature.MULTIPLE_USERS_SUPPORTED.name()).append(" : ").append(Weight.WeightScaleFeature.MULTIPLE_USERS_SUPPORTED.getFlagValue(weightFeature & 0x02)).append("\n")
              .append(Weight.WeightScaleFeature.BMI_SUPPORTED.name()).append(" : ").append(Weight.WeightScaleFeature.BMI_SUPPORTED.getFlagValue(weightFeature & 0x04)).append("\n")
              .append(Weight.WeightScaleFeature.WEIGHT_MEASUREMENT_RESOLUTION.name()).append(" : ").append(Weight.WeightScaleFeature.WEIGHT_MEASUREMENT_RESOLUTION.getFlagValue((weightFeature >> 3) & 0x0F)).append("\n")
              .append(Weight.WeightScaleFeature.HEIGHT_MEASUREMENT_RESOLUTION.name()).append(" : ").append(Weight.WeightScaleFeature.HEIGHT_MEASUREMENT_RESOLUTION.getFlagValue((weightFeature >> 7) & 0x07)).append("\n");
            intent.putExtra(EXTRA_DATA, sb.toString());

        } else if (GattAttributes.Characteristic.PLX_SPOT_CHECK_MEASUREMENT.isMatchedWithUUID(characteristic.getUuid())){
            //Pulse Oximeter - PLX Spot-Check Measurement
            Spo2 spo2 = mGattDataParser.parseSpo2Data(characteristic);
            intent.putExtra(EXTRA_DATA, spo2);

        } else if (GattAttributes.Characteristic.PLX_CONTINUOUS_MEASUREMENT.isMatchedWithUUID(characteristic.getUuid())){
            //Pulse Oximeter - PLX Continuous Measurement
            Spo2 spo2 = mGattDataParser.parseSpo2ContinuousData(characteristic);
            intent.putExtra(EXTRA_DATA, spo2);

        } else if (GattAttributes.Characteristic.PLX_FEATURES.isMatchedWithUUID(characteristic.getUuid())){
            //Pulse Oximeter - PLX Feature
            StringBuilder sb = new StringBuilder();
            int plxFeature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);

            int bitFlag = 0x01;
            for(Spo2.SupportedFeature sf : Spo2.SupportedFeature.values()){ //Supported Features
                sb.append(sf.name()).append(" : ").append((plxFeature & bitFlag) == 1).append("\n");
                bitFlag *= 2;
            }

            int index = 2;
            if((plxFeature & 0x01) == 1){ //Measurement Status Support
                int measurementStatusSupport = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
                index += 2;
                bitFlag = 0x10;
                for(Spo2.MeasurementStatus ms : Spo2.MeasurementStatus.values()){
                    sb.append(ms.name()).append("SUPPORTED : ").append((measurementStatusSupport & bitFlag) == 1).append("\n");
                    bitFlag *= 2;
                }
            }

            if((plxFeature & 0x02) == 1){ //Device and Sensor Status Support
                int deviceAndSensorStatus = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index) + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index + 1) << 8;
                bitFlag = 0x01;
                for(Spo2.DeviceAndSensorStatus dass : Spo2.DeviceAndSensorStatus.values()){
                    sb.append(dass.name()).append("SUPPORTED : ").append((deviceAndSensorStatus & bitFlag) == 1).append("\n");
                    bitFlag *= 2;
                }
            }
            intent.putExtra(EXTRA_DATA, sb.toString());

        } else {
           if (value != null && value.length > 0) {
               final StringBuilder stringBuilder = new StringBuilder(value.length);
               for (byte byteChar : value)
                   stringBuilder.append(String.format("%02X ", byteChar));
               intent.putExtra(EXTRA_DATA, new String(value) + "\n" + stringBuilder.toString());
           }
       }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        mGattDataParser = new GattDataParser();
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.i(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.i(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

       /* // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }*/

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.i(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.i(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.i(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt = null;
        mBluetoothDeviceAddress = null;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        Log.i("BluetoothLEService", "Close Bluetooth Gatt");
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.i(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        Log.i("BluetoothLeService", "setCharacteristicNotification=" + GattAttributes.getCharacteristicName(characteristic.getUuid()));
       if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.i(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        for(BluetoothGattDescriptor descriptor : characteristic.getDescriptors()){
            descriptor.setValue(enabled? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
        // This is specific to Heart Rate Measurement.
//        if (GattAttributes.Characteristic.HEART_RATE_MEASUREMENT.isMatchedWithUUID(characteristic.getUuid())) {
            /*BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")); //??*/
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }

    public void setCharacteristicIndication(BluetoothGattCharacteristic characteristic,
                                            boolean enabled) {
        Log.i("BluetoothLeService", "setCharacteristicIndication=" + GattAttributes.getCharacteristicName(characteristic.getUuid()));
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.i(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if(enabled) {
            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }else{
            mBluetoothGatt.setCharacteristicNotification(characteristic, false);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    private void writeRACP(BluetoothGattCharacteristic characteristic, RACPAttributes.OpCode opCode, RACPAttributes.Operator operator){

        byte[] data = new byte[2];
        data[0] = opCode.code;
        data[1] = operator.code;
        characteristic.setValue(data);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Field f = mBluetoothGatt.getClass().getDeclaredField("mDeviceBusy");
            f.setAccessible(true);
            boolean deviceBusy = (Boolean)f.get(mBluetoothGatt);
            Log.e("device busy", "" + deviceBusy);
            /*if(deviceBusy) {
                f.set(mBluetoothGatt, false);
            }*/
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        boolean isRACPWriteSuccess =  mBluetoothGatt.writeCharacteristic(characteristic);
        Log.i("writeRACP", opCode.name() + "/" + operator.name() + "/isWriteSuccess=" + isRACPWriteSuccess);


        /*Iterator<BluetoothGattCharacteristic> iterator = mStandardGattService.getCharacteristics().iterator();
        while (iterator.hasNext()){
            BluetoothGattCharacteristic writeRACPchar = iterator.next();
            getLog().debug(String.format("find in writeRACP = true : uuid = %s", writeRACPchar.getUuid().toString()));
            if ( UUID_org_bluetooth_characteristic_record_access_contrl_point != getUUIDtoINT(writeRACPchar))
                continue;

            getLog().debug(String.format("conti... find in writeRACP = true : uuid = %s", writeRACPchar.getUuid().toString()));
            byte[] data = new byte[2];
            data[0] = opCode;
            data[1] = operator;
            writeRACPchar.setValue(data);
            racpOPCode = opCode;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if( getMBluetoothGatt().writeCharacteristic(writeRACPchar) ){
                readyRACPWriteAck = false;
            }

            break;
        }*/
    }

    private void readRACP(BluetoothGattCharacteristic characteristic){
        final byte[] data = characteristic.getValue();
        if (data == null || data.length < 1)
            return;
        byte opCode = data[0];
        byte operator = data[1];
        byte requestOpCode = data[3];
        byte responseCodeValue = data[2];
        if(opCode == RACPAttributes.OpCode.NUMBER_OF_STORED_RECORDS_RESPONSE.code){
            Log.i("readRACP", "Stored Records Count = " + responseCodeValue);
            mReadCount = 0;
            if(responseCodeValue > 0){
                writeRACP(characteristic, RACPAttributes.OpCode.REPORT_STORED_RECORDS, RACPAttributes.Operator.ALL_RECORDS);
            }
        }
        /*if(opCode == OPCODE_NUMBER_OF_STORED_RECORDS_RESPONSE){
            nGluTCnt = data[2];
            nGluTCnt = nGluTCnt + data[3]*256;
            getLog().debug(String.format("Number of stored recodes = %d", nGluTCnt));

            if(nGluTCnt > 0 ){
                if(readyRACPWriteAck)
                    writeRACP(OPCODE_REPORT_STORED_RECORDS, OPERATOR_ALL_RECORDS);
            } else {
                if( !bleSetTime()) {
                    //There is no data...
                }
            }
        } else if (OPCode == OPCODE_RESPONSE_CODE){
            if(RequestOPCode == OPCODE_REPORT_STORED_RECORDS){
                if(ResponseCodeValue == RESPONSE_CODE_SUCCESS) {
//                    writeRACP(OPCODE_DELETE_STORED_RECORDS, OPERATOR_ALL_RECORDS);
                } else if(ResponseCodeValue == RESPONSE_CODE_NO_RECORDS_FOUND){
                    if( !bleSetTime()) {
                        //There is no data...
                    }
                }
            }
            else if (RequestOPCode == OPCODE_DELETE_STORED_RECORDS) {
                if(ResponseCodeValue == RESPONSE_CODE_SUCCESS){
                    if( !bleSetTime()) {
                        //VAL_STATE_COMMUNICATION_END);

                    }
                } else if(ResponseCodeValue == RESPONSE_CODE_OP_CODE_NOT_SUPPORTED){

                }

            }
        }*/
    }
}
